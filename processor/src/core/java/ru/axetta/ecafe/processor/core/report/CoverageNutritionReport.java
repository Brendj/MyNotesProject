/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoverageNutritionReport extends BasicReportForOrgJob {

    public static final String REPORT_NAME = "Отчет по охвату питания";
    public static final String[] TEMPLATE_FILE_NAMES = {"CoverageNutritionReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};
    private final static Logger logger = LoggerFactory.getLogger(CoverageNutritionReport.class);

    final public static String P_SHOW_YOUNGER_CLASSES = "showYoungerClasses";
    final public static String P_SHOW_MIDDLE_CLASSES = "showMiddleClasses";
    final public static String P_SHOW_OLDER_CLASSES = "showOlderClasses";
    final public static String P_SHOW_EMPLOYEE_CLASSES = "showEmployeeClasses";

    final public static String P_SHOW_FREE_NUTRITION = "showFreeNutrition";
    final public static String P_SHOW_PAID_NUTRITION = "showPaidNutrition";
    final public static String P_SHOW_BUFFET = "showBuffet";

    final public static String P_SHOW_COMPLEXES_BY_ORG_CARD = "showComplexesByOrgCard";

    final public static String P_SHOW_TOTAL = "showTotals";

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new CoverageNutritionReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    public static class Builder extends BasicReportForOrgJob.Builder {

        private final String templateFilename;
        private Long idOfOrg;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public BasicReportJob build(Session session, Date startDate, Date endDate, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startDate));
            parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(endDate));
            parameterMap.put("reportName", REPORT_NAME);
            parameterMap.put("SUBREPORT_DIR",
                    RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath());
            parameterMap.put("OVERRIDE_GroupComparator", new Comparator<String>() {
                public int compare(String obj1, String obj2) {
                    Pattern pattern = Pattern.compile("\\D*(\\d{1,2})-\\d{1,2}");
                    Matcher matcher1 = pattern.matcher(obj1);
                    Matcher matcher2 = pattern.matcher(obj2);
                    if (matcher1.find() && matcher2.find()) {
                        Integer val1 = Integer.parseInt(matcher1.group(1));
                        Integer val2 = Integer.parseInt(matcher2.group(1));
                        return val1.compareTo(val2);
                    } else if (!matcher1.find() && matcher2.find()) {
                        return 1;
                    } else if (matcher1.find() && !matcher2.find()) {
                        return -1;
                    } else {
                        return obj1.compareTo(obj2);
                    }
                }
            });
            parameterMap.put("OVERRIDE_FoodTypeComparator", new Comparator<String>() {
                public int compare(String obj1, String obj2) {
                    if (obj1.toLowerCase().contains("буфет") && !obj2.toLowerCase().contains("буфет")) {
                        return -1;
                    } else if (!obj1.toLowerCase().contains("буфет") && obj2.toLowerCase().contains("буфет")) {
                        return 1;
                    } else {
                        return obj1.compareTo(obj2);
                    }
                }
            });

            Boolean showYoungerClasses = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_YOUNGER_CLASSES));
            Boolean showMiddleClasses = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_MIDDLE_CLASSES));
            Boolean showOlderClasses = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_OLDER_CLASSES));
            Boolean showEmployeeClasses = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_EMPLOYEE_CLASSES));

            Boolean showFreeNutrition = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_FREE_NUTRITION));
            Boolean showPaidNutrition = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_PAID_NUTRITION));
            Boolean showBuffet = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_BUFFET));

            Boolean showComplexesByOrgCard = Boolean
                    .parseBoolean(reportProperties.getProperty(P_SHOW_COMPLEXES_BY_ORG_CARD));

            JRDataSource dataSource = createDataSource(session, startDate, endDate, idOfOrg, showYoungerClasses,
                    showMiddleClasses, showOlderClasses, showEmployeeClasses, showFreeNutrition, showPaidNutrition,
                    showBuffet, showComplexesByOrgCard);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new PreordersReport(generateTime, generateDuration, jasperPrint, startDate, endDate, idOfOrg);
        }

        private JRDataSource createDataSource(Session session, Date startDate, Date endDate, Long idOfOrg,
                Boolean showYoungerClasses, Boolean showMiddleClasses, Boolean showOlderClasses, Boolean showEmployee,
                Boolean showFreeNutrition, Boolean showPaidNutrition, Boolean showBuffet,
                Boolean showComplexesByOrgCard) throws Exception {
            List<CoverageNutritionReportItem> itemList = new ArrayList<>();

            String sqlString = "select distinct "
                    + "    cast(substring(og.shortnameinfoservice, '№\\s{0,1}(\\d{1,5})') as integer) as number, "
                    + "    case when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 1 and 4 then 'Обучающиеся 1-4 классов' "
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 5 and 9 then 'Обучающиеся 5-9 классов' "
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 10 and 11 then 'Обучающиеся 10-11 классов' "
                    + "         when cg.idofclientgroup in (:clientEmployees, :clientAdministration, :clientTechEmployees) then 'Сотрудники' end as group, "
                    + "    case when od.menutype = 0 and od.menuorigin in (0, 1) then 'Буфет горячее' "
                    + "     when od.menutype between 50 and 99 and od.rprice > 0 then 'Платное питание' "
                    + "         when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then 'Бесплатное питание' "
                    + "         when od.menutype = 0 and od.menuorigin in (10, 11) then 'Буфет покупная' end as type, "
                    + "    case when od.menutype between 50 and 99 and od.rprice > 0 then g.nameofgood || ' ' || od.rprice / 100 || ' ' || 'руб.' "
                    + "         when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then g.nameofgood || ' ' || od.discount/100 || ' ' || 'руб.' end as complexname, "
                    + "    od.idoforderdetail, c.idofclient "
                    + "from cf_orders o "
                    + "join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + "join cf_clients c on c.idofclient = o.idofclient "
                    + "join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + "join cf_goods g on g.idofgood = od.idofgood "
                    + "join cf_orgs og on og.idoforg = o.idoforg "
                    + "where o.createddate between :startDate and :endDate and od.menutype < 150 and og.organizationtype = 0";
            String conditionString = " cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) %s between %d and %d";
            if (showYoungerClasses) {
                sqlString += " and (";
                sqlString += String.format(conditionString, "", 1, 4);
            }
            if (showMiddleClasses) {
                if (showYoungerClasses) {
                    sqlString += " or ";
                } else {
                    sqlString += " and (";
                }
                sqlString += String.format(conditionString, "", 5, 9);
            }
            if (showOlderClasses) {
                if (showYoungerClasses || showMiddleClasses) {
                    sqlString += " or ";
                } else {
                    sqlString += " and (";
                }
                sqlString += String.format(conditionString, "", 10, 11);
            }
            if (showYoungerClasses || showMiddleClasses || showOlderClasses) {
                sqlString += ")";
            }

            if (!showYoungerClasses) {
                sqlString += " and " + String.format(conditionString, "not", 1, 4);
            }
            if (!showMiddleClasses) {
                sqlString += " and " + String.format(conditionString, "not", 5, 9);
            }
            if (!showOlderClasses) {
                sqlString += " and " + String.format(conditionString, "not", 10, 11);
            }

            if (!showEmployee) {
                //TODO: Добавить группу сотрудники
                sqlString += " and cg.idofclientgroup not in (:clientEmployees, :clientAdministration, :clientTechEmployees)";
            }

            Query query = session.createSQLQuery(sqlString);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("clientAdministration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            query.setParameter("clientTechEmployees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            List list = query.list();

            for (Object o : list) {
                Object[] row = (Object[]) o;
                Integer schoolNumber = (Integer) row[0];
                String group = (String) row[1];
                String foodType = (String) row[2];
                String complexName = (String) row[3];
                Long idOfOrderDetail = ((BigInteger) row[4]).longValue();
                Long idOfClient = ((BigInteger) row[5]).longValue();
                itemList.add(
                        new CoverageNutritionReportItem(schoolNumber, group, foodType, complexName, idOfOrderDetail,
                                idOfClient));
            }

            return new JRBeanCollectionDataSource(itemList);
        }

        public String getTemplateFilename() {
            return templateFilename;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }
    }

    public static class CoverageNutritionReportItem {

        private Integer schoolNumber;
        private String group;
        private String foodType;
        private String complexName;
        private Long idOfOrderDetail;
        private Long idOfClient;

        public CoverageNutritionReportItem() {

        }

        public CoverageNutritionReportItem(Integer schoolNumber, String group, String foodType, String complexName,
                Long idOfOrderDetail, Long idOfClient) {
            this.schoolNumber = schoolNumber;
            this.group = group;
            this.foodType = foodType;
            this.complexName = complexName;
            this.idOfOrderDetail = idOfOrderDetail;
            this.idOfClient = idOfClient;
        }

        public Integer getSchoolNumber() {
            return schoolNumber;
        }

        public void setSchoolNumber(Integer schoolNumber) {
            this.schoolNumber = schoolNumber;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getFoodType() {
            return foodType;
        }

        public void setFoodType(String foodType) {
            this.foodType = foodType;
        }

        public String getComplexName() {
            return complexName;
        }

        public void setComplexName(String complexName) {
            this.complexName = complexName;
        }

        public Long getIdOfOrderDetail() {
            return idOfOrderDetail;
        }

        public void setIdOfOrderDetail(Long idOfOrderDetail) {
            this.idOfOrderDetail = idOfOrderDetail;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public void setIdOfClient(Long idOfClient) {
            this.idOfClient = idOfClient;
        }
    }
}
