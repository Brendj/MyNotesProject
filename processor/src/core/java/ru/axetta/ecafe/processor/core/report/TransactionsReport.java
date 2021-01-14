/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 28.05.14
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
public class TransactionsReport extends BasicReportForOrgJob {
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
    public static final String REPORT_NAME = "Отчет по транзакциям";
    public static final String[] TEMPLATE_FILE_NAMES = {"TransactionsReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{};


    private final static Logger logger = LoggerFactory.getLogger(TransactionsReport.class);
    public static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat dailyItemsFormat = new SimpleDateFormat("dd.MM.yyyy");

    private Date startDate;
    private Date endDate;
    protected List<TransactionsReportItem> items;
    private String htmlReport;

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportForOrgJob.Builder {
        private final String templateFilename;
        private boolean exportToHTML = false;
        private long idOfOrg;
        Boolean allFriendlyOrgs;

        public Builder(String templateFilename, long idOfOrg, Boolean allFriendlyOrgs) {
            this.templateFilename = templateFilename;
            this.idOfOrg = idOfOrg;
            this.allFriendlyOrgs = allFriendlyOrgs;
        }

        public Builder(long idOfOrg, Boolean allFriendlyOrgs) {
            String templateName = TransactionsReport.class.getSimpleName();
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + templateName + ".jasper";
            exportToHTML = true;
            this.idOfOrg = idOfOrg;
            this.allFriendlyOrgs = allFriendlyOrgs;
        }

        @Override
        public TransactionsReport build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            return doBuild(session, startTime, endTime, calendar, idOfOrg, allFriendlyOrgs);
        }

        public TransactionsReport doBuild(Session session, Date startTime, Date endTime, Calendar calendar, long idOfOrg, Boolean allFriendlyOrgs) throws Exception {
            if(startTime == null || endTime == null) {
                throw new IllegalArgumentException("Не задан период");
            }
            if (endTime.before(startTime)) {
                throw new IllegalArgumentException("Конечная дата не может быть меньше начальной");
            }
            if (CalendarUtils.getDifferenceInDays(startTime, endTime) > 31) {
                throw new IllegalArgumentException("Период отчета не может превышать 1 месяца");
            }

            Date generateTime = new Date();

            /* Строим параметры для передачи в jasper */
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("startDate", format.format(startTime));
            parameterMap.put("endDate", format.format(endTime));


            List<TransactionsReportItem> items = findItems(session, startTime, endTime, idOfOrg, allFriendlyOrgs);
            //  Если имя шаблона присутствует, значит строится для джаспера
            JasperPrint jasperPrint = JasperFillManager
                    .fillReport(templateFilename, parameterMap, createDataSource(items));
            Date generateEndTime = new Date();

            if (!exportToHTML) {
                return new TransactionsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, startTime, endTime, items);
            } else {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
                return new TransactionsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        startTime, endTime, items).setHtmlReport(os.toString("UTF-8"));
            }
        }


        protected List<TransactionsReportItem> findItems(Session session, Date start, Date end, long idOfOrg, Boolean allFriendlyOrgs) {
            Map<Long, TransactionsReportItem> items = new HashMap<Long, TransactionsReportItem>();

            getEvents(session, start, end, items, idOfOrg, allFriendlyOrgs);
            getSales(session, start, end, items, idOfOrg, allFriendlyOrgs);

            return new ArrayList<TransactionsReportItem>(items.values());
        }


        protected Map<Long, TransactionsReportItem> getEvents(Session session, Date start, Date end,
                                                              Map<Long, TransactionsReportItem> items, long idOfOrg, Boolean allFriendlyOrgs) {
            String condition = allFriendlyOrgs ? " in (select friendlyorg from cf_friendly_organization where currentorg = :idOfOrg) " : " = :idOfOrg ";
            String sql = "select distinct ee.idofclient, o.idoforg, o.shortname, o.address, o.district, count(ee.idofenterevent) as event "
                    + "from cf_orgs as o "
                    + "left join cf_enterevents as ee on o.idoforg=ee.idoforg and (ee.passdirection=0 or ee.passdirection=1) and "
                    + "          ee.evtdatetime >= :startDate and "
                    + "          ee.evtdatetime < :endDate "
                    + "where o.state<>0 and o.IdOfOrg " + condition
                    + "group by o.idoforg, o.shortname, o.address, o.district, ee.idofclient "
                    + "order by o.shortname ";

            Query query = session.createSQLQuery(sql);
            query.setParameter("startDate", start.getTime());
            query.setParameter("endDate", end.getTime());
            query.setParameter("idOfOrg", idOfOrg);
            List res = query.list();
            if(res == null) {
                return Collections.EMPTY_MAP;
            }
            for (Object entry : res) {
                Object e[]        = (Object[]) entry;
                long idoforg      = ((BigInteger) e[1]).longValue();
                String orgName    = (String) e[2];
                String orgAddress = (String) e[3];
                String district   = (String) e[4];
                long eventsCount  = ((BigInteger) e[5]).longValue();

                TransactionsReportItem item = items.get(idoforg);
                if(item == null) {
                    item = new TransactionsReportItem(idoforg, orgName, orgAddress, district);
                    items.put(idoforg, item);
                }
                item.setEvents(eventsCount);
            }
            return items;
        }


        protected Map<Long, TransactionsReportItem> getSales(Session session, Date start, Date end,
                                                             Map<Long, TransactionsReportItem> items, long idOfOrg, Boolean allFriendlyOrgs) {
            String condition = allFriendlyOrgs ? " in (select friendlyorg from cf_friendly_organization where currentorg = :idOfOrg) " : " = :idOfOrg ";
            String sql =
                        "select idoforg, shortname, address, district, count(*), isPaid, isComplex "
                      + "from ( "
                      + "      select distinct oo.idoforder, o.idoforg, o.shortname, 0, o.address, o.district, "
                      + "             case when oo.rsum<>0 then 1 "
                      + "                  when oo.socdiscount<>0 then 0 "
                      + "                  else null end as isPaid, "
                      + "             case when od.menutype>=50 and od.menutype<=99 then 1 "
                      + "                  when od.menutype<50 or od.menutype>99 then 0 "
                      + "                  else null end as isComplex "
                      + "      from cf_orgs as o "
                      + "      join cf_orders as oo on o.idoforg=oo.idoforg and "
                      + "                oo.createddate >= :startDate and "
                      + "                oo.createddate < :endDate "
                      + "      join cf_orderdetails as od on od.idoforder=oo.idoforder "
                      + "      where o.state<>0 and o.IdOfOrg " + condition
                      + "      order by o.shortname "
                      + "      ) as data "
                      + "group by idoforg, shortname, address, district, isPaid, isComplex "
                      + "order by shortname";

            Query query = session.createSQLQuery(sql);
            query.setParameter("startDate", start.getTime());
            query.setParameter("endDate", end.getTime());
            query.setParameter("idOfOrg", idOfOrg);
            List res = query.list();
            for (Object entry : res) {
                Object e[]         = (Object[]) entry;
                long idoforg      = ((BigInteger) e[0]).longValue();
                String orgName    = (String) e[1];
                String orgAddress = (String) e[2];
                String district   = (String) e[3];
                long count        = ((BigInteger) e[4]).longValue();
                Integer isPaid    = ((Integer) e[5]);
                Integer isComplex = ((Integer) e[6]);

                if(isPaid == null || isComplex == null) {
                    continue;
                }

                TransactionsReportItem item = items.get(idoforg);
                if(item == null) {
                    item = new TransactionsReportItem(idoforg, orgName, orgAddress, district);
                    items.put(idoforg, item);
                }

                if(isPaid == 0) {
                    item.setDiscounts(count);
                } else {
                    if(isComplex == 0) {
                        item.setPaidBuffet(count);
                    } else {
                        item.setPaidComplexes(count);
                    }
                }
            }
            return items;
        }

        private JRDataSource createDataSource(List<TransactionsReportItem> items) throws Exception {
            return new JRBeanCollectionDataSource(items);
        }
    }


    public TransactionsReport() {
        items = Collections.emptyList();
    }

    public TransactionsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, List<TransactionsReportItem> items) {
        super(generateTime, generateDuration, print, startTime, endTime, 0L);
        this.items = items;
    }

    public TransactionsReport(Date startTime,
            Date endTime, List<TransactionsReportItem> items) {
        this.items = items;
    }

    public TransactionsReport(Date generateTime, long generateDuration, Date startTime,
            Date endTime, List<TransactionsReportItem> items) {
        this.items = items;
    }


    @Override
    public BasicReportForOrgJob createInstance() {
        return new TransactionsReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportForOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename, 0L, true);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public TransactionsReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }


    public static final class TransactionsReportItem {
        protected long uniqueId;
        protected long columnId;
        protected String orgName;
        protected String orgAddress;
        protected String district;
        protected long events;
        protected long discounts;
        protected long paidComplexes;
        protected long paidBuffet;

        public TransactionsReportItem(long id, String orgName, String orgAddress, String district) {
            this.uniqueId = id;
            columnId = 1;
            this.orgName = orgName;
            this.orgAddress = orgAddress;
            this.district = district;

            events        = 0;
            discounts     = 0;
            paidComplexes = 0;
            paidBuffet    = 0;
        }

        public long getUniqueId() {
            return uniqueId;
        }

        public long getColumnId() {
            return columnId;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getOrgAddress() {
            return orgAddress;
        }

        public void setOrgAddress(String orgAddress) {
            this.orgAddress = orgAddress;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public long getEvents() {
            return events;
        }

        public void setEvents(long events) {
            this.events = events;
        }

        public long getDiscounts() {
            return discounts;
        }

        public void setDiscounts(long discounts) {
            this.discounts = discounts;
        }

        public long getPaidComplexes() {
            return paidComplexes;
        }

        public void setPaidComplexes(long paidComplexes) {
            this.paidComplexes = paidComplexes;
        }

        public long getPaidBuffet() {
            return paidBuffet;
        }

        public void setPaidBuffet(long paidBuffet) {
            this.paidBuffet = paidBuffet;
        }
    }
}
