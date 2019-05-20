/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.kzn;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportForOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoverageNutritionReport extends BasicReportForAllOrgJob {

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
    public BasicReportForAllOrgJob createInstance() {
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
                    if (obj1.toLowerCase().startsWith("комплексы") && !obj2.toLowerCase().startsWith("комплексы")) {
                        return 1;
                    } else if (obj2.toLowerCase().startsWith("комплексы") && !obj1.toLowerCase()
                            .startsWith("комплексы")) {
                        return -1;
                    } else if (obj1.toLowerCase().startsWith("комплексы") && obj2.toLowerCase()
                            .startsWith("комплексы")) {
                        return 0;
                    } else if (matcher1.find() && matcher2.find()) {
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

            List<Long> idOfOrgList = parseStringAsLongList(ReportPropertiesUtils.P_ID_OF_ORG);
            List<Long> idOfSourceOrgList = parseStringAsLongList(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG);

            JRDataSource dataSource = createDataSource(session, startDate, endDate, idOfOrgList, idOfSourceOrgList,
                    showYoungerClasses, showMiddleClasses, showOlderClasses, showEmployeeClasses, showFreeNutrition,
                    showPaidNutrition, showBuffet, showComplexesByOrgCard);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new CoverageNutritionReport(generateTime, generateDuration, jasperPrint, startDate, endDate);
        }

        private JRDataSource createDataSource(Session session, Date startDate, Date endDate, List<Long> idOfOrgList,
                List<Long> idOfSourceOrgList, Boolean showYoungerClasses, Boolean showMiddleClasses,
                Boolean showOlderClasses, Boolean showEmployee, Boolean showFreeNutrition, Boolean showPaidNutrition,
                Boolean showBuffet, Boolean showComplexesByOrgCard) throws Exception {
            List<Long> orgList = loadOrgList(session, idOfSourceOrgList, idOfOrgList);
            if (orgList.isEmpty()) {
                throw new Exception("Выберите организацию");
            }

            HashMap<Long, EmployeeItem> employeeItemHashMap = loadEmployeesByOrgs(session, orgList, startDate, endDate);
            List<Long> managerList = Collections.EMPTY_LIST;
            if (showComplexesByOrgCard) {
                managerList = loadManagers(session);
            }
            List<CoverageNutritionReportItem> itemList = new ArrayList<CoverageNutritionReportItem>();

            String sqlString = "select distinct og.idoforg, og.shortnameinfoservice, og.shortaddress, "
                    + "    st.studentsCountTotal, st.studentsCountYoung, st.studentsCountMiddle, st.studentsCountOld, st.benefitStudentsCountYoung, "
                    + "    st.benefitStudentsCountMiddle, st.benefitStudentsCountOld, st.benefitStudentsCountTotal, st.employeeCount, "
                    + "    case " + (managerList.isEmpty() || !showComplexesByOrgCard ? ""
                    : "when c.idofclient in (:managerList) then 'Комплексы проданные по карте ОО' ")
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 1 and 4 then 'Обучающиеся 1-4 классов' "
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 5 and 9 then 'Обучающиеся 5-9 классов' "
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 10 and 11 then 'Обучающиеся 10-11 классов' "
                    + "         when cg.idofclientgroup in (:clientEmployees, :clientAdministration, :clientTechEmployees) then 'Сотрудники' end as group, "
                    + "    case when od.menutype = 0 and od.menuorigin in (0, 1, 10, 11) then 'Буфет' "
                    + "     when od.menutype between 50 and 99 and od.rprice > 0 then 'Платное питание' "
                    + "         when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then 'Бесплатное питание' end as type, "
                    + "    case when od.menutype between 50 and 99 then od.menudetailname "
                    + "         when od.menutype = 0 and od.menuorigin in (0,1) then 'Горячее' "
                    + "         when od.menutype = 0 and od.menuorigin in (10,11) then 'Покупная' else '' end as complexname, "
                    + "    od.idoforderdetail, c.idofclient,"
                    + "     case when od.menutype between 50 and 99 and od.rprice > 0 then od.rprice "
                    + "          when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then od.discount else 0 end as price "
                    + "from cf_orders o "
                    + "join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + "join cf_clients c on c.idofclient = o.idofclient "
                    + "join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + "join cf_orgs og on og.idoforg = o.idoforg "
                    + "left join cf_kzn_clients_statistic st on st.idoforg = og.idoforg "
                    + "where o.idoforg in (:orgList) and o.createddate between :startDate and :endDate and od.menutype < 150 and og.organizationtype = 0 ";
            String conditionString = " cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) %s between %d and %d";
            List<String> classesConditionList = new ArrayList<String>();
            List<String> classesNotConditionList = new ArrayList<String>();
            if (showYoungerClasses) {
                classesConditionList.add(String.format(conditionString, "", 1, 4));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 1, 4));
            }
            if (showMiddleClasses) {
                classesConditionList.add(String.format(conditionString, "", 5, 9));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 5, 9));
            }
            if (showOlderClasses) {
                classesConditionList.add(String.format(conditionString, "", 10, 11));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 10, 11));
            }
            if (showComplexesByOrgCard && !managerList.isEmpty()) {
                classesConditionList.add(String.format("c.idofclient in (%s)", StringUtils.join(managerList, ",")));
            }

            if (!classesConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(classesConditionList, " or ") + ") ";
            }
            if (!classesNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(classesNotConditionList, " and ");
            }

            if (!showEmployee) {
                sqlString += " and cg.idofclientgroup not in (:clientEmployees, :clientAdministration, :clientTechEmployees)";
            }

            List<String> nutritionConditionList = new ArrayList<String>();
            List<String> nutritionNotConditionList = new ArrayList<String>();
            if (showFreeNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0) ");
            } else {
                nutritionNotConditionList
                        .add(" (od.menutype not between 50 and 99 or od.rprice != 0 or od.discount <= 0) ");
            }
            if (showPaidNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice > 0) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype not between 50 and 99 or od.rprice <= 0) ");
            }
            if (showBuffet) {
                nutritionConditionList.add(" (od.menutype = 0 and od.menuorigin in (0, 1, 10, 11)) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype != 0 or od.menuorigin not in (0, 1, 10, 11)) ");
            }

            if (!nutritionConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(nutritionConditionList, " or ") + ") ";
            }

            if (!nutritionNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(nutritionNotConditionList, " and ");
            }

            Query query = session.createSQLQuery(sqlString);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("clientAdministration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            query.setParameter("clientTechEmployees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            query.setParameterList("orgList", orgList);
            if (!managerList.isEmpty() && showComplexesByOrgCard) {
                query.setParameterList("managerList", managerList);
            }
            List list = query.list();

            for (Object o : list) {
                Object[] row = (Object[]) o;
                Long idOfOrg = ((BigInteger) row[0]).longValue();
                String schoolName = (String) row[1];
                String schoolAddress = (String) row[2];
                Long studentsCountTotal = (null == row[3]) ? 0 : ((BigInteger) row[3]).longValue();
                Long studentsCountYoung = (null == row[4]) ? 0 : ((BigInteger) row[4]).longValue();
                Long studentsCountMiddle = (null == row[5]) ? 0 : ((BigInteger) row[5]).longValue();
                Long studentsCountOld = (null == row[6]) ? 0 : ((BigInteger) row[6]).longValue();
                Long benefitStudentsCountYoung = (null == row[7]) ? 0 : ((BigInteger) row[7]).longValue();
                Long beneftiStudentsCountMiddle = (null == row[8]) ? 0 : ((BigInteger) row[8]).longValue();
                Long benefitStudentsCountOld = (null == row[9]) ? 0 : ((BigInteger) row[9]).longValue();
                Long benefitStudentsCountTotal = (null == row[10]) ? 0 : ((BigInteger) row[10]).longValue();
                Long employeeCount = (null == row[11]) ? 0 : ((BigInteger) row[11]).longValue();
                String group = (String) row[12];
                String foodType = (String) row[13];
                String complexName = (String) row[14];
                Long idOfOrderDetail = (null == row[15]) ? 0 : ((BigInteger) row[15]).longValue();
                Long idOfClient = (null == row[16]) ? 0 : ((BigInteger) row[16]).longValue();
                Long price = (null == row[17]) ? 0 : ((BigInteger) row[17]).longValue();
                EmployeeItem employeeItem = employeeItemHashMap.get(idOfOrg);
                itemList.add(
                        new CoverageNutritionReportItem(schoolName, schoolAddress, studentsCountTotal, studentsCountYoung,
                                studentsCountMiddle, studentsCountOld, benefitStudentsCountYoung,
                                beneftiStudentsCountMiddle, benefitStudentsCountOld, benefitStudentsCountTotal,
                                employeeCount, group, foodType, complexName + priceFormat(price), idOfOrderDetail,
                                idOfClient, employeeItem.getComplexesCount(), employeeItem.getBuffetCount(),
                                employeeItem.getAllFoodCount(), employeeItem.getPercentageOfActive()));
            }

            return new JRBeanCollectionDataSource(itemList);
        }

        private List<Long> loadOrgList(Session session, List<Long> idOfSourceOrgList, List<Long> idOfOrgList) {
            if (idOfSourceOrgList.isEmpty()) {
                return idOfOrgList;
            }
            Criteria criteria = session.createCriteria(Org.class)
                    .createAlias("sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN)
                    .createAlias("categoriesInternal", "cat", JoinType.LEFT_OUTER_JOIN)
                    .add(Restrictions.in("sm.idOfOrg", idOfSourceOrgList))
                    .setProjection(Projections.projectionList().add(Projections.groupProperty("idOfOrg")))
                    .addOrder(Order.asc("idOfOrg"));
            if (!idOfOrgList.isEmpty()) {
                criteria.add(Restrictions.in("idOfOrg", idOfOrgList));
            }
            return criteria.list();
        }

        private List<Long> parseStringAsLongList(String propertyName) {
            String propertyValueString = reportProperties.getProperty(propertyName);
            String[] propertyValueArray = StringUtils.split(propertyValueString, ',');
            List<Long> propertyValueList = new ArrayList<Long>();
            for (String propertyValue : propertyValueArray) {
                try {
                    propertyValueList.add(Long.parseLong(propertyValue));
                } catch (NumberFormatException e) {
                    logger.error(String.format("Unable to parse propertyValue: property = %s, value = %s", propertyName,
                            propertyValue), e);
                }
            }
            return propertyValueList;
        }

        private HashMap<Long, EmployeeItem> loadEmployeesByOrgs(Session session, List<Long> idOfOrgList, Date startDate,
                Date endDate) {
            HashMap<Long, EmployeeItem> employeeItemHashMap = new HashMap<Long, EmployeeItem>();
            for (Long idOfOrg : idOfOrgList) {
                if (!employeeItemHashMap.containsKey(idOfOrg)) {
                    employeeItemHashMap.put(idOfOrg, new EmployeeItem());
                }

                ClientGroup employeeGroup = DAOUtils.findKznEmployeeGroupByOrgId(session, idOfOrg);

                String orgCondition;
                if (null != employeeGroup) {
                    orgCondition = " and cg.idofclientgroup in (:clientEmployees, :clientAdministration, :clientTechEmployees, :employees) ";
                } else {
                    orgCondition = " and cg.idofclientgroup in (:clientEmployees, :clientAdministration, :clientTechEmployees) ";
                }

                String sqlString =
                        "select a.idoforg, a.employeeCount, a.type, count(distinct a.idofclient) as clientcount "
                                + "from ( " + " select og.idoforg, st.employeeCount, "
                                + "     case when od.menutype = 0 and od.menuorigin in (0, 1, 10, 11) then 'Буфет' "
                                + "      when od.menutype between 50 and 99 then 'Комплекс' else '' end as type, "
                                + "     od.idoforderdetail, c.idofclient " + " from cf_orders o "
                                + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                                + " join cf_clients c on c.idofclient = o.idofclient "
                                + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                                + " left join cf_goods g on g.idofgood = od.idofgood join cf_orgs og on og.idoforg = o.idoforg "
                                + " left join cf_kzn_clients_statistic st on st.idoforg = og.idoforg "
                                + " where o.idoforg = :idOfOrg and o.createddate between :startDate and :endDate and od.menutype < :complexItemMin and og.organizationtype = 0 "
                                + orgCondition + " order by 3) a " + "group by a.idoforg, a.employeecount, a.type";
                Query query = session.createSQLQuery(sqlString);
                query.setParameter("startDate", startDate.getTime());
                query.setParameter("endDate", endDate.getTime());
                query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
                query.setParameter("clientAdministration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
                query.setParameter("clientTechEmployees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
                if (null != employeeGroup) {
                    query.setParameter("employees", employeeGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                }
                query.setParameter("complexItemMin", OrderDetail.TYPE_COMPLEX_ITEM_MIN);
                query.setParameter("idOfOrg", idOfOrg);
                List list = query.list();

                for (Object o : list) {
                    Object[] row = (Object[]) o;
                    Long orgId = (null == row[0]) ? 0L : ((BigInteger) row[0]).longValue();
                    Long employeeCount = (null == row[1]) ? 0L : ((BigInteger) row[1]).longValue();
                    String type = (String) row[2];
                    Long clientCount = (null == row[3]) ? 0L : ((BigInteger) row[3]).longValue();

                    EmployeeItem employeeItem = employeeItemHashMap.get(orgId);
                    if (type.equals("Буфет")) {
                        employeeItem.setBuffetCount(clientCount);
                    } else if (type.equals("Комплекс")) {
                        employeeItem.setComplexesCount(clientCount);
                    }
                    employeeItem.setAllFoodCount(employeeItem.getBuffetCount() + employeeItem.getComplexesCount());
                    employeeItem.setPercentageOfActive(
                            employeeItem.getAllFoodCount().doubleValue() / employeeCount.doubleValue());
                }
            }
            return employeeItemHashMap;
        }

        private List<Long> loadManagers(Session session) {
            Criteria criteria = session.createCriteria(Client.class);
            criteria.createAlias("person", "p");
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.like("p.firstName", "#%"));
            disjunction.add(Restrictions.like("p.secondName", "#%"));
            disjunction.add(Restrictions.like("p.surname", "#%"));
            criteria.add(disjunction);
            criteria.setProjection(Property.forName("idOfClient"));
            return criteria.list();
        }

        private String priceFormat(Long price) {
            if (price.equals(0L)) {
                return "";
            }

            Integer rub = new Double(price.doubleValue() / 100.f).intValue();
            Integer cop = new Long(price - rub * 100).intValue();

            String moneyString = String.format(" - %d руб.", rub);

            if (!cop.equals(0)) {
                moneyString += String.format(" %02d коп.", cop);
            }
            return moneyString;
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

    public CoverageNutritionReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public CoverageNutritionReport() {

    }

    public static class CoverageNutritionReportItem {

        private String schoolName;
        private String schoolAddress;
        private Long studentsCountTotal;
        private Long studentsCountYoung;
        private Long studentsCountMiddle;
        private Long studentsCountOld;
        private Long benefitStudentsCountYoung;
        private Long benefitStudentsCountMiddle;
        private Long benefitStudentsCountOld;
        private Long benefitStudentsCountTotal;
        private Long employeeCount;
        private String group;
        private String foodType;
        private String complexName;
        private Long idOfOrderDetail;
        private Long idOfClient;
        private Long employeeComplexesCount;
        private Long employeeBuffetCount;
        private Long employeeAllFoodCount;
        private Double employeePercentageOfActive;

        public CoverageNutritionReportItem() {

        }

        private CoverageNutritionReportItem(String schoolName, String schoolAddress, Long studentsCountTotal,
                Long studentsCountYoung, Long studentsCountMiddle, Long studentsCountOld,
                Long benefitStudentsCountYoung, Long benefitStudentsCountMiddle, Long benefitStudentsCountOld,
                Long benefitStudentsCountTotal, Long employeeCount, String group, String foodType, String complexName,
                Long idOfOrderDetail, Long idOfClient, Long employeeComplexesCount, Long employeeBuffetCount,
                Long employeeAllFoodCount, Double employeePercentageOfActive) {
            this.schoolName = schoolName;
            this.schoolAddress = schoolAddress;
            this.studentsCountTotal = studentsCountTotal;
            this.studentsCountYoung = studentsCountYoung;
            this.studentsCountMiddle = studentsCountMiddle;
            this.studentsCountOld = studentsCountOld;
            this.benefitStudentsCountYoung = benefitStudentsCountYoung;
            this.benefitStudentsCountMiddle = benefitStudentsCountMiddle;
            this.benefitStudentsCountOld = benefitStudentsCountOld;
            this.benefitStudentsCountTotal = benefitStudentsCountTotal;
            this.employeeCount = employeeCount;
            this.group = group;
            this.foodType = foodType;
            this.complexName = complexName;
            this.idOfOrderDetail = idOfOrderDetail;
            this.idOfClient = idOfClient;
            this.employeeComplexesCount = employeeComplexesCount;
            this.employeeBuffetCount = employeeBuffetCount;
            this.employeeAllFoodCount = employeeAllFoodCount;
            this.employeePercentageOfActive = employeePercentageOfActive;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getSchoolAddress() {
            return schoolAddress;
        }

        public void setSchoolAddress(String schoolAddress) {
            this.schoolAddress = schoolAddress;
        }

        public Long getStudentsCountTotal() {
            return studentsCountTotal;
        }

        public void setStudentsCountTotal(Long studentsCountTotal) {
            this.studentsCountTotal = studentsCountTotal;
        }

        public Long getStudentsCountYoung() {
            return studentsCountYoung;
        }

        public void setStudentsCountYoung(Long studentsCountYoung) {
            this.studentsCountYoung = studentsCountYoung;
        }

        public Long getStudentsCountMiddle() {
            return studentsCountMiddle;
        }

        public void setStudentsCountMiddle(Long studentsCountMiddle) {
            this.studentsCountMiddle = studentsCountMiddle;
        }

        public Long getStudentsCountOld() {
            return studentsCountOld;
        }

        public void setStudentsCountOld(Long studentsCountOld) {
            this.studentsCountOld = studentsCountOld;
        }

        public Long getBenefitStudentsCountYoung() {
            return benefitStudentsCountYoung;
        }

        public void setBenefitStudentsCountYoung(Long benefitStudentsCountYoung) {
            this.benefitStudentsCountYoung = benefitStudentsCountYoung;
        }

        public Long getBenefitStudentsCountMiddle() {
            return benefitStudentsCountMiddle;
        }

        public void setBenefitStudentsCountMiddle(Long benefitStudentsCountMiddle) {
            this.benefitStudentsCountMiddle = benefitStudentsCountMiddle;
        }

        public Long getBenefitStudentsCountOld() {
            return benefitStudentsCountOld;
        }

        public void setBenefitStudentsCountOld(Long benefitStudentsCountOld) {
            this.benefitStudentsCountOld = benefitStudentsCountOld;
        }

        public Long getBenefitStudentsCountTotal() {
            return benefitStudentsCountTotal;
        }

        public void setBenefitStudentsCountTotal(Long benefitStudentsCountTotal) {
            this.benefitStudentsCountTotal = benefitStudentsCountTotal;
        }

        public Long getEmployeeCount() {
            return employeeCount;
        }

        public void setEmployeeCount(Long employeeCount) {
            this.employeeCount = employeeCount;
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

        public Long getEmployeeComplexesCount() {
            return employeeComplexesCount;
        }

        public void setEmployeeComplexesCount(Long employeeComplexesCount) {
            this.employeeComplexesCount = employeeComplexesCount;
        }

        public Long getEmployeeBuffetCount() {
            return employeeBuffetCount;
        }

        public void setEmployeeBuffetCount(Long employeeBuffetCount) {
            this.employeeBuffetCount = employeeBuffetCount;
        }

        public Long getEmployeeAllFoodCount() {
            return employeeAllFoodCount;
        }

        public void setEmployeeAllFoodCount(Long employeeAllFoodCount) {
            this.employeeAllFoodCount = employeeAllFoodCount;
        }

        public Double getEmployeePercentageOfActive() {
            return employeePercentageOfActive;
        }

        public void setEmployeePercentageOfActive(Double employeePercentageOfActive) {
            this.employeePercentageOfActive = employeePercentageOfActive;
        }
    }

    public static class EmployeeItem {

        private Long complexesCount;
        private Long buffetCount;
        private Long allFoodCount;
        private Double percentageOfActive;

        private EmployeeItem(Long complexesCount, Long buffetCount, Long allFoodCount, Double percentageOfActive) {
            this.complexesCount = complexesCount;
            this.buffetCount = buffetCount;
            this.allFoodCount = allFoodCount;
            this.percentageOfActive = percentageOfActive;
        }

        private EmployeeItem() {
            complexesCount = 0L;
            buffetCount = 0L;
            allFoodCount = 0L;
            percentageOfActive = 0.;
        }

        public Long getComplexesCount() {
            return complexesCount;
        }

        public void setComplexesCount(Long complexesCount) {
            this.complexesCount = complexesCount;
        }

        public Long getBuffetCount() {
            return buffetCount;
        }

        public void setBuffetCount(Long buffetCount) {
            this.buffetCount = buffetCount;
        }

        public Long getAllFoodCount() {
            return allFoodCount;
        }

        public void setAllFoodCount(Long allFoodCount) {
            this.allFoodCount = allFoodCount;
        }

        public Double getPercentageOfActive() {
            return percentageOfActive;
        }

        public void setPercentageOfActive(Double percentageOfActive) {
            this.percentageOfActive = percentageOfActive;
        }
    }
}
