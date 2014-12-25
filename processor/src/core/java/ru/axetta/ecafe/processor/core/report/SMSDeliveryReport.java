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
import ru.axetta.ecafe.processor.core.persistence.ClientSms;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 24.12.14
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class SMSDeliveryReport extends BasicReportForAllOrgJob {


    private final static Logger logger = LoggerFactory.getLogger(SMSDeliveryReport.class);

    private List<SMSDeliveryReportItem> items;
    private Date startDate;
    private Date endDate;
    private String htmlReport;

    private static final String ORG_NUM = "Номер ОУ";
    private static final String ORG_NAME = "Наименование ОУ";
    private static final String GOOD_NAME = "Товар";
    private static final List<String> DEFAULT_COLUMNS = new ArrayList<String>();

    static {
        DEFAULT_COLUMNS.add(ORG_NUM);
        DEFAULT_COLUMNS.add(ORG_NAME);
        DEFAULT_COLUMNS.add(GOOD_NAME);
    }


    public List<SMSDeliveryReportItem> getItems() {
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
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + SMSDeliveryReport.class.getSimpleName() + ".jasper";
            exportToHTML = true;
        }

        @Override
        public SMSDeliveryReport build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Long idOfContragent = null;
            Long idOfContract = null;
            if (reportProperties.getProperty("idOfContract") != null &&
                    reportProperties.getProperty("idOfContract").length() > 0) {
                try {
                    idOfContract = Long.parseLong(reportProperties.getProperty("idOfContract"));
                } catch (Exception e) {
                    idOfContract = null;
                }
            }
            if (contragent != null) {
                idOfContragent = contragent.getIdOfContragent();
            }

            return build(session, startTime, endTime, calendar, idOfContragent, idOfContract);
        }

        public SMSDeliveryReport build(Session session, Date startTime, Date endTime, Calendar calendar,
                Long contragent, Long contract) throws Exception {
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

            //Integer.parseInt (((java.util.Map) $V{values}).get("0_maxTimeout").toString())
            Date generateEndTime = new Date();
            List<SMSDeliveryReportItem> items = findDeliveryEntries(session, startTime, endTime);
            JRDataSource dataSource = createDataSource(session, startTime, endTime, (Calendar) calendar.clone(), parameterMap, items);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,dataSource);
            //  Если имя шаблона присутствует, значит строится для джаспера
            if (!exportToHTML) {
                return new SMSDeliveryReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, startTime, endTime, null);
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
                return new SMSDeliveryReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        startTime, endTime, items).setHtmlReport(os.toString("UTF-8"));
            }
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Calendar calendar,
                Map<String, Object> parameterMap, List<SMSDeliveryReportItem> items) throws Exception {
            return new JRBeanCollectionDataSource(items);
        }


        public List<SMSDeliveryReportItem> findDeliveryEntries(Session session, Date start, Date end) {
            String orgCondition = "";
            if (org != null) {
                orgCondition = "            and (org.idoforg=:idoforg) ";
            }



            String sql =
                    "select d.idoforg, d.shortname, smsdate, eventdate "
                    + "from ("
                    + "      select org.idoforg, org.shortname, sms.servicesenddate as smsdate, "
                    + "             case when (sms.contentstype = " + ClientSms.TYPE_ENTER_EVENT_NOTIFY + " and sms.contentsid is not null) "
                    + "                  then (select e.evtdatetime from cf_enterevents e where e.idofenterevent=sms.contentsid and e.idoforg=org.idoforg) "
                    + "                  when (sms.contentstype = " + ClientSms.TYPE_PAYMENT_NOTIFY + " and sms.contentsid is not null) "
                    + "                  then (select o.orderdate from cf_orders o where o.idoforder=sms.contentsid and o.idoforg=org.idoforg) "
                    + "                  else null "
                    + "             end as eventdate "
                    + "      from cf_clientsms sms "
                    + "      join cf_clients c on sms.idofclient=c.idofclient "
                    + "      join cf_orgs org on org.idoforg=c.idoforg "
                    + "      where (contentstype=" + ClientSms.TYPE_ENTER_EVENT_NOTIFY + " or contentstype=" + ClientSms.TYPE_PAYMENT_NOTIFY + ") and "
                    + "            (sms.servicesenddate>=:start and sms.servicesenddate<:end) "
                    + orgCondition
                    + "      order by 1) as d "
                    + "where d.eventdate is not null";
            Query query = session.createSQLQuery(sql);
            query.setParameter("start", start.getTime());
            query.setParameter("end", end.getTime());
            if (org != null) {
                query.setParameter("idoforg", org.getIdOfOrg());
            }

            long prevIdOfOrg = -1L;
            Map<Long, List<DeliveryEntry>> entries = null;
            List<DeliveryEntry> items = null;
            List res = query.list();
            for (Object entry : res) {
                if(entries == null) {
                    entries = new HashMap<Long, List<DeliveryEntry>>();
                }

                Object e[] = (Object[]) entry;
                long idoforg = ((BigInteger) e[0]).longValue();
                String officialname = (String) e[1];
                long smsDate = ((BigInteger) e[2]).longValue();
                long eventDate = ((BigInteger) e[3]).longValue();

                if(prevIdOfOrg == -1 || prevIdOfOrg != idoforg) {
                    items = new ArrayList<DeliveryEntry>();
                    entries.put(idoforg, items);
                    prevIdOfOrg = idoforg;
                }

                DeliveryEntry dE = new DeliveryEntry(idoforg, officialname, smsDate, eventDate);
                items.add(dE);
            }


            //  calc stats
            List<SMSDeliveryReportItem> result = new ArrayList<SMSDeliveryReportItem>();
            for(long idoforg : entries.keySet()) {
                items = entries.get(idoforg);
                if(items == null || items.size() < 1) {
                    continue;
                }
                SMSDeliveryReportItem it = calcSmsDeliveryitem(items);
                it.setUniqueId(result.size() + 1);
                it.setColumnId(1);
                result.add(it);
            }
            /*
                SMSDeliveryReportItem item = new SMSDeliveryReportItem();
                item.setColumnId(1);
                item.setOrgName(officialname);
                item.setUniqueId(i);
             */

            return result;
        }

        protected static SMSDeliveryReportItem calcSmsDeliveryitem(List<DeliveryEntry> items) {
            long minTime = Long.MAX_VALUE;
            long maxTime = Long.MIN_VALUE;
            long sumTime = 0;
            for(DeliveryEntry e : items) {
                long dTime = e.getDifferenceDate();
                minTime = Math.min(minTime, dTime);
                maxTime = Math.max(maxTime, dTime);
                sumTime += dTime;
            }

            String minTimeout = calcTimeout(minTime);
            String avgTimeout = calcTimeout((long) sumTime / items.size());
            String maxTimeout = calcTimeout(maxTime);

            SMSDeliveryReportItem res = new SMSDeliveryReportItem();
            res.setOrgName(items.get(0).getOrgName());
            res.addValue("1_eventsCount", "" + items.size());
            res.addValue("1_minTimeout", minTimeout);
            res.addValue("1_avgTimeout", avgTimeout);
            res.addValue("1_maxTimeout", maxTimeout);
            return res;
        }

        protected static String calcTimeout(long time) {
            DateFormat df;
            if(time / (60*60*24) >= 1) {
                df = new SimpleDateFormat(String.format("'%s' '%s' H '%s' m '%s'", time / 86400000L, "дн.", "час.", "мин."));
            } else if(time / (60*60) >= 1) {
                df = new SimpleDateFormat(String.format("H '%s' m '%s'", "час.", "мин."));
            } else if(time / (60) >= 1) {
                df = new SimpleDateFormat(String.format("m '%s'", "мин."));
            } else {
                df = new SimpleDateFormat(String.format("s '%s'", "сек."));
            }
            Date d = new Date(time*1000);
            return df.format(d);
        }
    }


    public SMSDeliveryReport() {
    }


    public SMSDeliveryReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime, Date endTime,
            List<SMSDeliveryReportItem> items) {
        super(generateTime, generateDuration, print, startTime, endTime);
        this.items = items;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public SMSDeliveryReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public SMSDeliveryReport(Date generateTime, long generateDuration, Date startTime, Date endTime,
            List<SMSDeliveryReportItem> items) {
        this.items = items;
    }


    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new SMSDeliveryReport();  //To change body of implemented methods use File | Settings | File Templates.
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

    protected static class DeliveryEntry {
        protected final Long idOfOrg;
        protected final String orgName;
        protected final long smsDate;
        protected final long eventDate;

        public DeliveryEntry(Long idOfOrg, String orgName, long smsDate, long eventDate) {
            this.idOfOrg = idOfOrg;
            this.orgName = orgName;
            this.smsDate = smsDate;
            this.eventDate = eventDate;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getOrgName() {
            return orgName;
        }

        public long getSmsDate() {
            return smsDate;
        }

        public long getEventDate() {
            return eventDate;
        }

        public long getDifferenceDate() {
            return smsDate - eventDate;
        }
    }
}
