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
import ru.axetta.ecafe.processor.core.service.SmsDeliveryCalculationService;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
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
            try {
                List<SMSDeliveryReportItem> items = new ArrayList<SMSDeliveryReportItem>();
                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal = SmsDeliveryCalculationService.resetCalendar(cal);
                if(end.getTime() >= cal.getTimeInMillis() &&
                   start.getTime() >= cal.getTimeInMillis()) {
                } else {
                    if(end.getTime() >= cal.getTimeInMillis()) {
                        throw new IllegalArgumentException("Конечная дата может быть указана текущим днем только в том случае, если начальная дата так же указана текщим днем");
                    }
                    findConsolidated(items, session, start, end);
                }
                findExternal(items, session, start, end);
                return items;
            } catch (Exception e) {
                logger.error("Failed to build SMSDelivery report", e);
                throw new RuntimeException("Failed to build report: " + e.getMessage());
            }
        }

        public List<SMSDeliveryReportItem> findDaily(List<SMSDeliveryReportItem> items, Session session, Date start, Date end) {
            String orgsList = reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG);
            items = SMSDeliveryReport.Builder.findSmsSyncItem(items, session, start, end, orgsList);
            return items;
        }

        public static List<SMSDeliveryReportItem> findSmsSyncItem(Session session, Date start, Date end, String orgsList) {
            List<SMSDeliveryReportItem> items = new ArrayList<SMSDeliveryReportItem>();
            return findSmsSyncItem(items, session, start, end, orgsList);
        }

        public List<SMSDeliveryReportItem> findConsolidated(List<SMSDeliveryReportItem> items, Session session, Date start, Date end) {
            String orgsList = reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG);
            items = SMSDeliveryReport.Builder.findConsolidated(items, session, start, end, orgsList);
            return items;
        }

        protected static final String getDataTypeNameForReport(int dataType) {
            String k = SmsDeliveryCalculationService.getDataTypeName(dataType);
            if(k.endsWith(SmsDeliveryCalculationService.POSTFIX)) {
                k = k.substring(0, k.length() - SmsDeliveryCalculationService.POSTFIX.length());
            }
            return k;
        }

        public static List<SMSDeliveryReportItem> findConsolidated(List<SMSDeliveryReportItem> items, Session session, Date start, Date end, String orgsList) {
            String orgCondition = "";
            if(orgsList != null) {
                String idOfOrgs = StringUtils.trimToEmpty(orgsList);
                if(idOfOrgs != null && !StringUtils.isBlank(idOfOrgs)) {
                    List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
                    StringBuilder builder = new StringBuilder();
                    for(String id : stringOrgList) {
                        builder.append(builder.length() > 0 ? " or " : "").append("sync.idoforg=").append(id);
                    }
                    orgCondition = "            and (" + builder.toString() + ") ";
                }
            }

            String sql =
                    "select o.idoforg, o.shortname, calctype, round(avg(sync.calcValue)) "
                    + "from cf_synchistory_calc sync "
                    + "join cf_orgs o on o.idoforg=sync.idoforg "
                    + "where sync.calcDateAt>=:start and sync.calcDateAt<:end " + orgCondition
                    + "group by o.idoforg, calctype "
                    + "order by o.idoforg";
            Query query = session.createSQLQuery(sql);
            query.setParameter("start", start.getTime());
            query.setParameter("end", end.getTime());
            List res = query.list();

            SMSDeliveryReportItem it = null;
            long prevOrgId = -1L;
            int i=0;
            for(Object entry : res) {
                Object e[] = (Object[]) entry;
                long idoforg = ((BigInteger) e[0]).longValue();
                String officialname = (String) e[1];
                int dataType = ((Integer) e[2]).intValue();
                long ts = ((BigDecimal) e[3]).longValue();

                if(it == null || idoforg != prevOrgId) {
                    if(it != null) {
                        items.add(it);
                    }
                    it = new SMSDeliveryReportItem();
                    it.setUniqueId(i);
                    it.setOrgName(officialname);
                    it.setOrgId(idoforg);
                    it.setColumnId(1);
                    prevOrgId = idoforg;
                }

                String k = getDataTypeNameForReport(dataType);
                String v = calcTimeout(ts);
                it.addValue(k, v);
                i++;
            }
            if(it != null) {
                items.add(it);
            }


            //  get max sync latest sync date
            sql = "select o.idoforg, o.shortname, calctype, max(sync.calcValue) "
                  + "from cf_synchistory_calc sync "
                  + "join cf_orgs o on o.idoforg=sync.idoforg "
                  + "where sync.calcDateAt>=:start and sync.calcDateAt<:end "
                  + "      and calctype=:calctype "
                  + orgCondition
                  + "group by o.idoforg, calctype "
                  + "order by o.idoforg";
            query = session.createSQLQuery(sql);
            query.setParameter("start", start.getTime());
            query.setParameter("end", end.getTime());
            query.setParameter("calctype", SmsDeliveryCalculationService.getDataTypeId("0_lastSyncTs"));
            res = query.list();
            for(Object entry : res) {
                Object e[] = (Object[]) entry;
                long idoforg = ((BigInteger) e[0]).longValue();
                String officialname = (String) e[1];
                int dataType = ((Integer) e[2]).intValue();
                long ts = ((BigInteger) e[3]).longValue();
                SMSDeliveryReportItem targetItem = null;

                for(SMSDeliveryReportItem it2 : items) {
                    if(it2.getOrgId() == idoforg) {
                        targetItem = it2;
                        break;
                    }
                }
                if(targetItem == null) {
                    continue;
                }

                String k = getDataTypeNameForReport(dataType);
                String v = calcDate(ts);
                targetItem.addValue(k, v);
            }
            return items;
        }

        public static List<SMSDeliveryReportItem> findSmsSyncItem(List<SMSDeliveryReportItem> items, Session session, Date start, Date end, String orgsList) {
            String orgCondition = "";
            if(orgsList != null) {
                String idOfOrgs = StringUtils.trimToEmpty(orgsList);
                if(idOfOrgs != null && !StringUtils.isBlank(idOfOrgs)) {
                    List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
                    StringBuilder builder = new StringBuilder();
                    for(String id : stringOrgList) {
                        builder.append(builder.length() > 0 ? " or " : "").append("sync1.idoforg=").append(id);
                    }
                    orgCondition = "            and (" + builder.toString() + ") ";
                }
            }
            /*String orgCondition = "";
            if (org != null) {
                orgCondition = "            and (sync1.idoforg=:idoforg) ";
            }*/

            Calendar startCal = new GregorianCalendar();
            startCal.setTimeInMillis(start.getTime());
            startCal.set(Calendar.HOUR_OF_DAY, 8);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            Calendar endCal = new GregorianCalendar();
            endCal.setTimeInMillis(end.getTime() + 86400000L);
            endCal.set(Calendar.HOUR_OF_DAY, 8);
            endCal.set(Calendar.MINUTE, 0);
            endCal.set(Calendar.SECOND, 0);
            endCal.set(Calendar.MILLISECOND, 0);

            /*String sql =
                      "select o.idoforg, o.shortname, t1, t2 "
                    + "from (select sync1.idoforg as idoforg, "
                    + "             sync1.syncstarttime as t1, "
                    + "             (select sync2.syncstarttime "
                    + "              from cf_synchistory sync2 "
                    + "              where sync2.syncstarttime<sync1.syncstarttime and sync1.idoforg=sync2.idoforg "
                    + "              order by syncstarttime desc limit 1) t2 "
                    + "      from cf_synchistory sync1 "
                    + "      where sync1.syncstarttime>=:start and sync1.syncstarttime<:end " + orgCondition + " ) as history "
                    + "join cf_orgs o on history.idoforg=o.idoforg "
                    + "order by idoforg, t1 asc";*/
            String sql =
                    "select o.idoforg, o.shortname, t1, t2 "
                    + "from (select sync1.idoforg as idoforg, "
                    + "             sync1.syncdate as t1, "
                    + "             (select sync2.syncdate "
                    + "              from cf_synchistory_daily sync2 "
                    + "              where sync2.syncdate<sync1.syncdate and sync1.idoforg=sync2.idoforg "
                    + "              order by syncdate desc limit 1) t2 "
                    + "      from cf_synchistory_daily sync1 "
                    + "      where sync1.syncdate>=:start and sync1.syncdate<:end " + orgCondition + " ) as history "
                    + "join cf_orgs o on history.idoforg=o.idoforg "
                    + " where t2 is not null "
                    + "order by idoforg, t1 asc";
            Query query = session.createSQLQuery(sql);
            query.setParameter("start", startCal.getTimeInMillis());
            query.setParameter("end", endCal.getTimeInMillis());
            /*if (org != null) {
                query.setParameter("idoforg", org.getIdOfOrg());
            }*/

            Long prevIdOfOrg = null;
            SyncEntry prevEntry = null;
            List<SyncEntry> entries = new ArrayList<SyncEntry>();
            List res = query.list();
            for (Object entry : res) {
                Object e[] = (Object[]) entry;
                long idoforg = ((BigInteger) e[0]).longValue();
                String officialname = (String) e[1];
                long t1 = ((BigInteger) e[2]).longValue();
                long t2 = ((BigInteger) e[3]).longValue();


                if(prevIdOfOrg == null || prevIdOfOrg.longValue() != idoforg) {
                    if(prevEntry != null) {
                        entries.add(prevEntry);
                    }
                    prevEntry = new SyncEntry(idoforg, officialname);
                    prevIdOfOrg = idoforg;
                }
                prevEntry.addTs(new Long[] { t1, t2 });
            }
            if(prevEntry != null) {
                entries.add(prevEntry);
            }

            for(SyncEntry se : entries) {
                SMSDeliveryReportItem it = null;
                for(SMSDeliveryReportItem i : items) {
                    if(i.getOrgName() != null && se.getOrgName() != null &&
                       i.getOrgName().equals(se.getOrgName())) {
                        it = i;
                        break;
                    }
                }
                boolean exists = it != null;
                it = calcSmsSyncItem(se, it, exists ? null : items.size() + 1);
                if(!exists) {
                    items.add(it);
                }
            }
            return items;
        }

        public List<SMSDeliveryReportItem> findExternal(List<SMSDeliveryReportItem> items, Session session, Date start, Date end) {
            String orgCondition = "";
            String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            if(idOfOrgs != null && !StringUtils.isBlank(idOfOrgs)) {
                List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
                StringBuilder builder = new StringBuilder();
                for(String id : stringOrgList) {
                    builder.append(builder.length() > 0 ? " or " : "").append("org.idoforg=").append(id);
                }
                orgCondition = "            (" + builder.toString() + ") and ";
            }

            String sql =
                    "select d.idoforg, d.shortname, smsdate, eventdate "
                    + "from ("
                    + "      select org.idoforg, org.shortname, sms.servicesenddate as smsdate, "
                    + "             case when (sms.contentstype = " + ClientSms.TYPE_ENTER_EVENT_NOTIFY + " and sms.contentsid is not null) "
                    + "                  then (select e.evtdatetime from cf_enterevents e where e.idofenterevent=sms.contentsid and e.idofclient=sms.idofclient AND e.evtdatetime<=sms.servicesenddate order by 1 desc limit 1) "
                    + "                  when (sms.contentstype = " + ClientSms.TYPE_PAYMENT_NOTIFY + " and sms.contentsid is not null) "
                    + "                  then (select o.orderdate from cf_orders o where o.idoforder=sms.contentsid and sms.idofclient=o.idofclient AND o.orderdate<=sms.servicesenddate order by 1 desc limit 1) "
                    + "                  else null "
                    + "             end as eventdate "
                    + "      from cf_clientsms sms "
                    + "      join cf_clients c on sms.idofclient=c.idofclient "
                    + "      join cf_orgs org on org.idoforg=c.idoforg "
                    + "      where " + orgCondition
                    + "            (sms.servicesenddate>=:start and sms.servicesenddate<:end) and "
                    + "            (contentstype=" + ClientSms.TYPE_ENTER_EVENT_NOTIFY + " or contentstype=" + ClientSms.TYPE_PAYMENT_NOTIFY + ") "
                    + "      order by 1) as d "
                    + "where d.eventdate is not null";
            Query query = session.createSQLQuery(sql);
            query.setParameter("start", start.getTime());
            query.setParameter("end", end.getTime());
            /*if (org != null) {
                query.setParameter("idoforg", org.getIdOfOrg());
            }*/


            long prevIdOfOrg = -1L;
            Map<Long, List<DeliveryEntry>> entries = null;
            List<DeliveryEntry> temp = null;
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
                    temp = new ArrayList<DeliveryEntry>();
                    entries.put(idoforg, temp);
                    prevIdOfOrg = idoforg;
                }

                DeliveryEntry dE = new DeliveryEntry(idoforg, officialname, smsDate, eventDate);
                temp.add(dE);
            }


            if(entries != null) {
                //  calc stats
                for(long idoforg : entries.keySet()) {
                    temp = entries.get(idoforg);
                    if(temp == null || temp.size() < 1) {
                        continue;
                    }
                    SMSDeliveryReportItem it = null;
                    for(SMSDeliveryReportItem i : items) {
                        if(i.getOrgName() != null && temp.get(0).getOrgName() != null &&
                           i.getOrgName().equals(temp.get(0).getOrgName())) {
                            it = i;
                            break;
                        }
                    }
                    boolean exists = it != null;
                    it = calcSmsDeliveryitem(temp, it, exists ? null : items.size() + 1);
                    if(!exists) {
                        items.add(it);
                    }
                }
                /*
                    SMSDeliveryReportItem item = new SMSDeliveryReportItem();
                    item.setColumnId(1);
                    item.setOrgName(officialname);
                    item.setUniqueId(i);
                 */
            }

            return items;
        }

        public static final long MAX_DELAY = 120000L;
        protected static SMSDeliveryReportItem calcSmsSyncItem(SyncEntry entry, SMSDeliveryReportItem res, Integer uniqueId) {
            long maxDelayMidday = 0L;
            long sumDelayMidday = 0L;
            long maxDelayNight  = 0L;
            long sumDelayNight  = 0L;
            long lastSync       = 0L;

            List<Long[]> tsList = entry.getTsList();
            Calendar t1 = new GregorianCalendar();
            Calendar t2 = new GregorianCalendar();
            for(Long[] ts : tsList) {
                if(ts.length < 2 || ts[0] == null || ts[1] == null) {
                    continue;
                }
                t1.setTimeInMillis(ts[0]);
                t2.setTimeInMillis(ts[1]);
                long diff = t1.getTimeInMillis() - t2.getTimeInMillis();
                if(diff < MAX_DELAY) {
                    continue;
                }

                if(t1.get(Calendar.HOUR_OF_DAY) >= 8 && t1.get(Calendar.HOUR_OF_DAY) < 16) {
                    maxDelayMidday = Math.max(diff, maxDelayMidday);
                    sumDelayMidday += diff;
                } else if(t1.get(Calendar.HOUR_OF_DAY) >= 16 || t1.get(Calendar.HOUR_OF_DAY) < 8) {
                    maxDelayNight = Math.max(diff, maxDelayNight);
                    sumDelayNight += diff;
                }
                lastSync = Math.max(lastSync, t1.getTimeInMillis());
            }

            if(res == null) {
                res = new SMSDeliveryReportItem();
            }
            if(res.getOrgName() == null || StringUtils.isBlank(res.getOrgName())) {
                res.setOrgName(entry.getOrgName());
                res.setOrgId(entry.getIdOfOrg());
                res.setColumnId(1);
            }
            if(uniqueId != null) {
                res.setUniqueId(uniqueId);
            }
            res.addValue("0_maxDelayMidday", calcTimeout(maxDelayMidday));
            res.addValue("0_sumDelayMidday", calcTimeout(sumDelayMidday));
            res.addValue("0_maxDelayNight", calcTimeout(maxDelayNight));
            res.addValue("0_sumDelayNight", calcTimeout(sumDelayNight));
            res.addValue("0_lastSync", calcDate(lastSync));

            res.addValue(SmsDeliveryCalculationService.getDataTypeName(0), "" + maxDelayMidday);
            res.addValue(SmsDeliveryCalculationService.getDataTypeName(1), "" + sumDelayMidday);
            res.addValue(SmsDeliveryCalculationService.getDataTypeName(2), "" + maxDelayNight);
            res.addValue(SmsDeliveryCalculationService.getDataTypeName(3), "" + sumDelayNight);
            res.addValue(SmsDeliveryCalculationService.getDataTypeName(4), "" + lastSync);
            return res;
        }

        protected static SMSDeliveryReportItem calcSmsDeliveryitem(List<DeliveryEntry> items, SMSDeliveryReportItem res, Integer uniqueId) {
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

            if(res == null) {
                res = new SMSDeliveryReportItem();
            }
            if(res.getOrgName() == null || StringUtils.isBlank(res.getOrgName())) {
                res.setOrgName(items.get(0).getOrgName());
                res.setOrgId(items.get(0).getIdOfOrg());
                res.setColumnId(1);
            }
            if(uniqueId != null) {
                res.setUniqueId(uniqueId);
            }
            res.addValue("1_eventsCount", "" + items.size());
            res.addValue("1_minTimeout", minTimeout);
            res.addValue("1_avgTimeout", avgTimeout);
            res.addValue("1_maxTimeout", maxTimeout);
            return res;
        }

        protected static String calcTimeout(long time) {
            /*DateFormat df;
            if(time / (60*60*24) >= 1) {
                df = new SimpleDateFormat(String.format("'%s' '%s' H '%s' m '%s'", time / 86400000L, "дн.", "час.", "мин."));
            } else if(time / (60*60) >= 1) {
                df = new SimpleDateFormat(String.format("H '%s' m '%s'", "час.", "мин."));
            } else if(time / (60) >= 1) {
                df = new SimpleDateFormat(String.format("m '%s'", "мин."));
            } else {
                df = new SimpleDateFormat(String.format("s '%s'", "сек."));
            }*/
            //DateFormat df = new SimpleDateFormat(String.format("HH:mm:ss"));
            DateFormat df = new SimpleDateFormat(String.format("'%s':mm:ss", time / 3600000L));
            Date d = new Date(time);
            return df.format(d);
        }

        protected static String calcDate(long time){
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date d = new Date(time);
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

    protected static class SyncEntry {
        protected final Long idOfOrg;
        protected final String orgName;
        protected List<Long[]> tsList;

        public SyncEntry(Long idOfOrg, String orgName) {
            this.idOfOrg = idOfOrg;
            this.orgName = orgName;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getOrgName() {
            return orgName;
        }

        public void addTs(Long ts[]) {
            if(tsList == null) {
                tsList = new ArrayList<Long[]>();
            }
            tsList.add(ts);
        }

        public List<Long[]> getTsList() {
            return tsList;
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
