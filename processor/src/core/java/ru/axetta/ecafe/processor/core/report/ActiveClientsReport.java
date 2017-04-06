/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 25.10.13
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class ActiveClientsReport extends BasicReportForAllOrgJob {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Отчет по активным клиентам";
    public static final String[] TEMPLATE_FILE_NAMES = {"ActiveClientsReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3,32};

    public static final int TOTAL_COUNT_VALUE = 1;
    public static final int DISCOUNT_COUNT_VALUE = 2;
    public static final int PAYMENT_COUNT_VALUE = 3;
    public static final int EMPLOYEE_COUNT_VALUE = 4;
    public static final int REAL_DISCOUNT_COUNT_VALUE = 5;
    public static final int ENTERS_COUNT_VALUE = 6;

    private final static Logger logger = LoggerFactory.getLogger(ActiveClientsReport.class);

    private List<ActiveClientsItem> items;
    private Date startDate;
    private Date endDate;
    private String htmlReport;


    public List<ActiveClientsItem> getItems() {
        return items;
    }


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }


    public static class Builder extends BasicReportForAllOrgJob.Builder {
        private final String templateFilename;
        private boolean exportToHTML = false;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + ActiveClientsReport.class.getSimpleName() + ".jasper";
            exportToHTML = true;
        }

        @Override
        public ActiveClientsReport build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();


            /* Строим параметры для передачи в jasper */
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);


            Date generateEndTime = new Date();
            List<ActiveClientsItem> items = findActiveClients(session, startTime, endTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport
                                                (templateFilename, parameterMap, createDataSource(items));
            //  Если имя шаблона присутствует, значит строится для джаспера
            if (!exportToHTML) {
                return new ActiveClientsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, startTime, endTime, null);
            } else {
                /*ByteArrayOutputStream os = new ByteArrayOutputStream();
                JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();*/
                return new ActiveClientsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(), jasperPrint,
                        startTime, endTime, items)/*.setHtmlReport(os.toString("UTF-8"))*/;
            }
        }

        private JRDataSource createDataSource(List<ActiveClientsItem> items) throws Exception {
            return new JRBeanCollectionDataSource(items);
        }

        public List<ActiveClientsItem> findActiveClients(Session session, Date start, Date end) {
            String orgRestriction = "";

            if (orgShortItemList != null && !orgShortItemList.isEmpty()) {
                orgRestriction = " and cf_orgs.idoforg in (";
                for (int i = 0; i < orgShortItemList.size() - 1; i++) {
                    orgRestriction = orgRestriction + orgShortItemList.get(i).getIdOfOrg() + ", ";
                }
                orgRestriction = orgRestriction + orgShortItemList.get(orgShortItemList.size() - 1).getIdOfOrg();
                orgRestriction = orgRestriction + ") ";
            } else if (reportProperties.containsKey("idOfOrg")) {
                //старт из ручного запуска
                String orgs_str = (String)reportProperties.get("idOfOrg");
                orgRestriction = String.format(" and cf_orgs.idoforg in (%s) ", orgs_str);
            }

            List<ActiveClientsItem> result = new ArrayList<ActiveClientsItem>();
            String sql =
                /* Все */
                "select cf_orgs.idoforg, cf_orgs.shortname, substring(cf_orgs.shortname FROM '[0-9]+') as num, "
                + "       cf_orgs.district, count(distinct totalClients.idofclient), " + TOTAL_COUNT_VALUE + " as valType "
                + "from cf_orgs "
                + "left join cf_clients as totalClients on cf_orgs.idoforg=totalClients.idoforg "
                + "left join cf_clientgroups on totalClients.idoforg=cf_clientgroups.idoforg and totalClients.idOfClientGroup=cf_clientgroups.idOfClientGroup "
                + "where cf_orgs.district is not null and cf_orgs.district<>'' "
                       + getClientsClause("totalClients")
                       + orgRestriction
                + "group by cf_orgs.idOfOrg, cf_orgs.shortname, cf_orgs.district "
                + "union all "
                /* Бесплатники */
                + "select cf_orgs.idoforg, cf_orgs.shortname, substring(cf_orgs.shortname FROM '[0-9]+') as num, "
                + "       cf_orgs.district, count(distinct discountClients.idofclient), " + DISCOUNT_COUNT_VALUE + " as valType "
                + "from cf_orgs "
                + "left join cf_clients as discountClients on cf_orgs.idoforg=discountClients.idoforg and discountClients.discountmode<>0 "
                + "left join cf_clientgroups on discountClients.idoforg=cf_clientgroups.idoforg and discountClients.idOfClientGroup=cf_clientgroups.idOfClientGroup "
                + "where cf_orgs.district is not null and cf_orgs.district<>'' "
                         + getClientsClause("discountClients")
                         + orgRestriction
                + "group by cf_orgs.idOfOrg, cf_orgs.shortname, cf_orgs.district "
                + "union all "
                /* Осуществившие платежи */
                + "select cf_orgs.idoforg, cf_orgs.shortname, substring(cf_orgs.shortname FROM '[0-9]+') as num, "
                + "       cf_orgs.district, count(distinct orders.idofclient), " + PAYMENT_COUNT_VALUE + " as valType "
                + "from cf_orgs "
                + "join cf_orders as orders on cf_orgs.idoforg=orders.idoforg and "
                + "                       orders.createddate between " + start.getTime() + " AND "
                + "                                                  " + end.getTime() + " "
                + "join cf_clients as ordclients on orders.idofclient=ordclients.idofclient "
                + "left join cf_clientgroups on ordclients.idoforg=cf_clientgroups.idoforg and ordclients.idOfClientGroup=cf_clientgroups.idOfClientGroup "
                + "where orders.state = 0 and cf_orgs.district is not null and cf_orgs.district<>'' "
                        + getClientsClause("ordclients")
                        + orgRestriction
                + "group by cf_orgs.idOfOrg, cf_orgs.shortname, cf_orgs.district "
                + "union all "
                /* Сотрудники */
                + "select cf_orgs.idoforg, cf_orgs.shortname, substring(cf_orgs.shortname FROM '[0-9]+') as num, "
                + "       cf_orgs.district, count(distinct employeeClients.idofclient), " + EMPLOYEE_COUNT_VALUE + " as valType "
                + "from cf_orgs "
                + "left join cf_clients as employeeClients on cf_orgs.idoforg=employeeClients.idoforg "
                + "where cf_orgs.district is not null and cf_orgs.district<>'' "
                + "      AND employeeClients.idOfClientGroup>=" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                + "      AND employeeClients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_LEAVING.getValue() + " "
                        + orgRestriction
                + "group by cf_orgs.idOfOrg, cf_orgs.shortname, cf_orgs.district "
                + "union all "
                + "select cf_orgs.idoforg, cf_orgs.shortname, substring(cf_orgs.shortname FROM '[0-9]+') as num, "
                //+ "       cf_orgs.district, count(distinct orders.idofclient) + count(distinct trans.idofclient), " + REAL_DISCOUNT_COUNT_VALUE + " as valType "
                + "       cf_orgs.district, count(distinct orders.idofclient), " + REAL_DISCOUNT_COUNT_VALUE + " as valType "
                + "from cf_orgs "
                + "join cf_clients as transclients on cf_orgs.idoforg=transclients.idoforg "
                + "left join cf_orders as orders on transclients.idofclient=orders.idofclient and socdiscount<>0 and "
                + "                       orders.createddate between " + start.getTime() + " and "
                + "                                                  " + end.getTime() + " "
                + "left join cf_clientgroups on transclients.idoforg=cf_clientgroups.idoforg and transclients.idOfClientGroup=cf_clientgroups.idOfClientGroup "
                + "where orders.state = 0 and cf_orgs.district is not null and cf_orgs.district<>'' "
                        + getClientsClause("transclients")
                        + orgRestriction
                + "group by cf_orgs.idOfOrg, cf_orgs.shortname, cf_orgs.district "
                + "union all "
                //  Проходы
                + "select cf_orgs.idoforg, cf_orgs.shortname, substring(cf_orgs.shortname FROM '[0-9]+') as num, "
                + "       cf_orgs.district, count(distinct cf_enterevents.idofclient), " + ENTERS_COUNT_VALUE + " as valType "
                + "from cf_orgs "
                + "join cf_enterevents on cf_enterevents.idoforg=cf_orgs.idoforg and "
                + "     cf_enterevents.evtdatetime between " + start.getTime() + " AND "
                + "                                        " + end.getTime() + " "
                + "join cf_clients as entclients on cf_enterevents.idofclient=entclients.idofclient and cf_enterevents.idoforg=entclients.idoforg "
                + "left join cf_clientgroups on entclients.idoforg=cf_clientgroups.idoforg and entclients.idOfClientGroup=cf_clientgroups.idOfClientGroup "
                + "where cf_orgs.district is not null and cf_orgs.district<>'' "
                         + getClientsClause("entclients")
                         + orgRestriction
                + "group by cf_orgs.idOfOrg, cf_orgs.shortname, cf_orgs.district "
                + "order by district, shortname, valType";
            Query query = session.createSQLQuery(sql);
            List res = query.list();
            ActiveClientsItem prevItem = null;
            ActiveClientsItem prevRegionItem = null;
            long prevOrg = -1L;
            long localIdOfOrg = 0;
            String prevRegion = null;
            ActiveClientsItem overallItem = new ActiveClientsItem(localIdOfOrg, "ИТОГО", "", "",
                                                                  ActiveClientsItem.OVERALL_STYLE);
            for (Object entry : res) {
                Object e[]       = (Object[]) entry;
                long idOfOrg     = ((BigInteger) e[0]).longValue();
                String shortName = ("" + (String) e[1]).trim();
                String num       = (String) e[2];
                String district  = (String) e[3];
                long count       = ((BigInteger) e[4]).longValue();
                int valueType    = ((Integer) e[5]).intValue();

                num = num == null ? "" : num.trim();
                district = district == null ? "" : district.trim();

                //  Если регион изменен, то добавляем предыдущий и создаем запись о новом
                if (prevRegionItem == null) {
                    prevRegionItem = new ActiveClientsItem(localIdOfOrg, "Итого по " + district,
                                                           "", "", ActiveClientsItem.REGION_STYLE);
                }
                if (prevRegion != null && !prevRegion.equals(district)) {
                    if (prevRegion != null) {
                        prevRegionItem.setIdOfOrg(localIdOfOrg++);
                        result.add(prevRegionItem);
                    }
                    prevRegionItem = new ActiveClientsItem(localIdOfOrg, "Итого по " + district,
                                                           "", "", ActiveClientsItem.REGION_STYLE);
                }
                //  Создаем запись о новой организации, если его id отличается
                if (prevOrg != idOfOrg) {
                    prevItem = new ActiveClientsItem(localIdOfOrg++, shortName, num,
                                                     district, ActiveClientsItem.DEFAULT_STYLE);
                    prevItem.setValue(0);
                    result.add(prevItem);
                    prevOrg = idOfOrg;
                }
                //  Устанавлиаем значения для орга, а так же увеличиваем значение для региона
                switch (valueType) {
                    case TOTAL_COUNT_VALUE:
                        overallItem.setTotalCount(overallItem.getTotalCount() + count);
                        prevRegionItem.setTotalCount(prevRegionItem.getTotalCount() + count);
                        prevItem.setTotalCount(count);
                        break;
                    case DISCOUNT_COUNT_VALUE:
                        overallItem.setDiscountCount(overallItem.getDiscountCount() + count);
                        prevRegionItem.setDiscountCount(prevRegionItem.getDiscountCount() + count);
                        prevItem.setDiscountCount(count);
                        break;
                    case PAYMENT_COUNT_VALUE:
                        overallItem.setPaymentCount(overallItem.getPaymentCount() + count);
                        prevRegionItem.setPaymentCount(prevRegionItem.getPaymentCount() + count);
                        prevItem.setPaymentCount(count);
                        break;
                    case EMPLOYEE_COUNT_VALUE:
                        overallItem.setEmployeesCount(overallItem.getEmployeesCount() + count);
                        prevRegionItem.setEmployeesCount(prevRegionItem.getEmployeesCount() + count);
                        prevItem.setEmployeesCount(count);
                        break;
                    case REAL_DISCOUNT_COUNT_VALUE:
                        overallItem.setRealDiscountCount(overallItem.getRealDiscountCount() + count);
                        prevRegionItem.setRealDiscountCount(prevRegionItem.getRealDiscountCount() + count);
                        prevItem.setRealDiscountCount(count);
                        break;
                    case ENTERS_COUNT_VALUE:
                        overallItem.setEntersCount(overallItem.getEntersCount() + count);
                        prevRegionItem.setEntersCount(prevRegionItem.getEntersCount() + count);
                        prevItem.setEntersCount(count);
                        break;
                }
                prevRegion = district;
            }
            //  Добавляем последний регион
            if (prevRegionItem != null) {
                prevRegionItem.setIdOfOrg(localIdOfOrg++);
                result.add(prevRegionItem);
            }
            //  Добавляем ИТОГО по всем регионам
            overallItem.setIdOfOrg(localIdOfOrg);
            result.add(overallItem);

            for (ActiveClientsItem i : result) {
                i.setActive(i.getTotalCount() == 0 ? 0 : (double)i.getPaymentCount() / (double)i.getTotalCount() * 100D);
            }

            return result;
        }
    }
    
    private static String getClientsClause(String table) {
        /*String onlyActiveClients = " AND " + table + ".idOfClientGroup>=" + ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue() +
                                   " AND " + table + ".idOfClientGroup<" + ClientGroup.Predefined.CLIENT_LEAVING.getValue() + " ";*/
        //String onlyActiveClients =
        //         " AND ((" + table + ".idOfClientGroup>=" + ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue()
        //       + "           AND " + table + ".idOfClientGroup<" + ClientGroup.Predefined.CLIENT_LEAVING.getValue() + ") or "
        //       + "           " + table + ".idOfClientGroup is null or "
        //       + "           cf_clientgroups.groupname='') ";
        //отр. client groups
        String onlyActiveClients =
                " AND (("  + table + ".idOfClientGroup<" + ClientGroup.Predefined.CLIENT_LEAVING.getValue() + ") or "
                        + "           " + table + ".idOfClientGroup is null or "
                        + "           cf_clientgroups.groupname='') ";
        return onlyActiveClients;
    }


    public ActiveClientsReport() {
    }


    public ActiveClientsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, List<ActiveClientsItem> items) {
        super(generateTime, generateDuration, print, startTime, endTime);
        this.items = items;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public ActiveClientsReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public ActiveClientsReport(Date generateTime, long generateDuration, Date startTime, Date endTime,
            List<ActiveClientsItem> items) {
        this.items = items;
    }


    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new ActiveClientsReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }

    public class JasperStringOutputStream extends OutputStream {

        private StringBuilder string = new StringBuilder();

        @Override
        public void write(int b) throws IOException {
            this.string.append((char) b);
        }

        //Netbeans IDE automatically overrides this toString()
        public String toString() {
            return this.string.toString();
        }
    }
}