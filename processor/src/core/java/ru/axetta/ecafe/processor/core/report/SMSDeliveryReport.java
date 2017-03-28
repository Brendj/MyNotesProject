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
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.service.SmsDeliveryCalculationService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
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
    public static final String REPORT_NAME = "Отчет по быстрой синхронизации и времени доставки сообщений информирования";
    public static final String[] TEMPLATE_FILE_NAMES = {"SMSDeliveryReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3};


    private final static Logger logger = LoggerFactory.getLogger(SMSDeliveryReport.class);

    private List<SMSDeliveryReportItem> items;
    private Date startDate;
    private Date endDate;
    private String htmlReport;
    public static final String IS_ACTIVE_STATE = "isActiveState";

    private static final String ORG_NUM = "Номер ОУ";
    private static final String ORG_NAME = "Наименование ОУ";
    private static final String GOOD_NAME = "Товар";
    private static final List<String> DEFAULT_COLUMNS = new ArrayList<String>();

    public static final long MILLIS_IN_HOUR = 3600000L;
    public static final long MILLIS_IN_MINUTE = 60000L;
    public static final long MILLIS_IN_SECOND = 1000L;

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
        private IPrintWarn printer;
        private static boolean isActiveState = false;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder(IPrintWarn printer) {
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + SMSDeliveryReport.class.getSimpleName() + ".jasper";
            exportToHTML = true;
            this.printer = printer;
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
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
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
                CalendarUtils.truncateToDayOfMonth(cal);

                isActiveState = Boolean.valueOf(reportProperties.getProperty(IS_ACTIVE_STATE, "false"));

                findConsolidated(items, session, start, end);
                findExternal(items, session, start, end);
                findOrgData(items, session);
                findEmptyRows(items);

                return items;
            } catch (Exception e) {
                logger.error("Failed to build SMSDelivery report", e);
                throw new RuntimeException("Failed to build report: " + e.getMessage());
            }
        }

        private List<SMSDeliveryReportItem> findEmptyRows(List<SMSDeliveryReportItem> items) {
            for(SMSDeliveryReportItem item : items){
                item.setIsEmptyValues();
            }
            return items;
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
                        builder.append(builder.length() > 0 ? " or " : "").append("o.idoforg=").append(id);
                    }
                    orgCondition = " and (" + builder.toString() + ") ";
                }
            }

            String orgConditionForOrgData = orgCondition.length() > 1 ? "where " + orgCondition.substring(4) : orgCondition;
            String stateCondition = "";

            if(isActiveState){
                stateCondition = " o.state = " + Org.ACTIVE_STATE;
                if(orgCondition.length() > 1){
                    stateCondition = " and " + stateCondition;
                } else {
                    stateCondition = " where " + stateCondition;
                }
            }

            String sql =
                    "select o.idoforg, o.shortname "
                    + "from cf_orgs o "
                    + orgConditionForOrgData + stateCondition
                    + " order by o.idoforg";
            Query query = session.createSQLQuery(sql);
            List res = query.list();

            SMSDeliveryReportItem it = null;
            int i = 0;

            for(Object entry : res) {
                Object e[] = (Object[]) entry;
                long idoforg = ((BigInteger) e[0]).longValue();
                String officialname = (String) e[1];

                it = new SMSDeliveryReportItem();
                it.setUniqueId(++i);
                it.setOrgName(officialname);
                it.setOrgId(idoforg);
                it.setColumnId(1);
                items.add(it);
            }

            if(isActiveState){
                stateCondition =  " and " + " o.state = " + Org.ACTIVE_STATE;
            }

            sql =
                    "select o.idoforg, calctype, round(avg(sync.calcValue)) "
                    + "from cf_synchistory_calc sync "
                    + "join cf_orgs o on o.idoforg=sync.idoforg "
                    + "where sync.calcDateAt>=:start and sync.calcDateAt<:end " + orgCondition + stateCondition
                    + " group by o.idoforg, calctype "
                    + "order by o.idoforg";
            query = session.createSQLQuery(sql);
            query.setParameter("start", start.getTime());
            query.setParameter("end", end.getTime());
            res = query.list();

            it = null;
            long commonSum = 0;

            for(Object entry : res) {
                Object e[] = (Object[]) entry;
                long idoforg = ((BigInteger) e[0]).longValue();
                int dataType = (Integer) e[1];
                long ts = ((BigDecimal) e[2]).longValue();

                for(SMSDeliveryReportItem it2 : items) {
                    if(it2.getOrgId() == idoforg) {
                        it = it2;
                        break;
                    }
                }

                if(it == null) {
                    continue;
                }

                it.addValue("commonSum", calcTimeout(commonSum));
                items.add(it);
                commonSum = 0;

                if (SmsDeliveryCalculationService.isSumType(dataType)) {
                    commonSum += ts;
                }

                String k = getDataTypeNameForReport(dataType);
                String v = calcTimeout(ts);
                it.addValue(k, v);

                if(it != null) {
                    it.addValue("commonSum", calcTimeout(commonSum));
                }
            }

            //  get max sync latest sync date
            sql = "select o.idoforg, o.shortname, calctype, max(sync.calcValue) "
                  + "from cf_synchistory_calc sync "
                  + "join cf_orgs o on o.idoforg=sync.idoforg "
                  + "where sync.calcDateAt>=:start and sync.calcDateAt<:end "
                  + "      and calctype=:calctype "
                  + orgCondition + stateCondition
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
                String v = (ts == 0L) ? "" : calcDate(ts);
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

            String stateCondition = "";
            String stateConditionJoin = "";

            if(isActiveState){
                stateCondition = " and " + " o.state = " + Org.ACTIVE_STATE;
                stateConditionJoin = " left join cf_orgs o on sync1.idoforg = o.idoforg";
            }

            String sql =
                    "select o.idoforg, o.shortname, t1, t2 "
                    + "from (select sync1.idoforg as idoforg, "
                    + "             sync1.syncdate as t1, "
                    + "             (select sync2.syncdate "
                    + "              from cf_synchistory_daily sync2 "
                    + "              where sync2.syncdate<sync1.syncdate and sync1.idoforg=sync2.idoforg "
                    + "              order by syncdate desc limit 1) t2 "
                    + "      from cf_synchistory_daily sync1 " + stateConditionJoin
                    + "      where sync1.syncdate>=:start and sync1.syncdate<:end " + orgCondition + stateCondition + " ) as history "
                    + "join cf_orgs o on history.idoforg=o.idoforg "
                    + " where t2 is not null "
                    + "order by idoforg, t1 asc";
            Query query = session.createSQLQuery(sql);
            query.setParameter("start", start.getTime());
            query.setParameter("end", end.getTime());

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
            DateComparisonConstants dateConstants = new DateComparisonConstants(start);
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
                it = calcSmsSyncItem(se, it, exists ? null : items.size() + 1, dateConstants);
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
                    builder.append(builder.length() > 0 ? " or " : "").append("o.idoforg=").append(id);
                }
                orgCondition = "            (" + builder.toString() + ") and ";
            }

            String stateCondition = "";

            if(isActiveState){
                stateCondition = " o.state = " + Org.ACTIVE_STATE + " and ";
            }

            String sql =
                    "select o.idoforg, o.shortname, sms.servicesenddate, sms.evtdate "
                            + "from cf_clientsms sms inner join cf_orgs o on sms.idoforg=o.idoforg "
                            + "where " + orgCondition + stateCondition
                            + "            (sms.servicesenddate>=:start and sms.servicesenddate<:end) and "
                            + "            (sms.contentstype=" + ClientSms.TYPE_ENTER_EVENT_NOTIFY + " or sms.contentstype=" + ClientSms.TYPE_PAYMENT_NOTIFY + ") and "
                            + "            sms.evtdate is not null "
                            + "order by 1 ";
            Query query = session.createSQLQuery(sql);
            query.setParameter("start", start.getTime());
            query.setParameter("end", end.getTime());

            long prevIdOfOrg = -1L;
            Map<Long, List<DeliveryEntry>> smsDeliveryOrgsEntries = null;
            List<DeliveryEntry> entryListByOrg = null;
            List queryResult = query.list();
            if(!queryResult.isEmpty()) {
                smsDeliveryOrgsEntries = new HashMap<Long, List<DeliveryEntry>>();
            }
            for (Object rowResult : queryResult) {

                Object fieldResult[] = (Object[]) rowResult;
                long idoforg = ((BigInteger) fieldResult[0]).longValue();
                String officialname = (String) fieldResult[1];
                long smsDate = ((BigInteger) fieldResult[2]).longValue();
                long eventDate = ((BigInteger) fieldResult[3]).longValue();

                if(prevIdOfOrg == -1 || prevIdOfOrg != idoforg) {
                    entryListByOrg = new ArrayList<DeliveryEntry>();
                    smsDeliveryOrgsEntries.put(idoforg, entryListByOrg);
                    prevIdOfOrg = idoforg;
                }

                DeliveryEntry deliveryEntry = new DeliveryEntry(idoforg, officialname, smsDate, eventDate);
                entryListByOrg.add(deliveryEntry);
            }


            if(smsDeliveryOrgsEntries != null) {
                //  calc stats
                for(long idoforg : smsDeliveryOrgsEntries.keySet()) {
                    entryListByOrg = smsDeliveryOrgsEntries.get(idoforg);
                    if(entryListByOrg == null || entryListByOrg.size() < 1) {
                        continue;
                    }
                    SMSDeliveryReportItem mathcedItem = null;
                    for(SMSDeliveryReportItem item : items) {
                        if(item.getOrgName() != null && entryListByOrg.get(0).getOrgName() != null &&
                           item.getOrgName().equals(entryListByOrg.get(0).getOrgName())) {
                            mathcedItem = item;
                            break;
                        }
                    }
                    boolean exists = mathcedItem != null;
                    mathcedItem = calcSmsDeliveryitem(entryListByOrg, mathcedItem, exists ? null : items.size() + 1);
                    if(!exists) {
                        items.add(mathcedItem);
                    }
                }
            }

            return items;
        }

        public static final long MAX_DELAY = 120000L;
        protected static SMSDeliveryReportItem calcSmsSyncItem(SyncEntry entry, SMSDeliveryReportItem res, Integer uniqueId, DateComparisonConstants dateConstants) {
            long maxDelayMidnight = 0L;
            long sumDelayMidnight = 0L;
            long maxDelayMorning = 0L;
            long sumDelayMorning = 0L;
            long maxDelayMidday = 0L;
            long sumDelayMidday = 0L;
            long maxDelayNight  = 0L;
            long sumDelayNight  = 0L;
            long lastSync       = 0L;

            List<Long[]> tsList = entry.getTsList();

            for(Long[] ts : tsList) {
                if(ts.length < 2 || ts[0] == null || ts[1] == null) {
                    continue;
                }
                long diff = ts[0] - ts[1];
                if(diff < MAX_DELAY) {
                    continue;
                }

                if(isTimeBetween7h15mAnd8h45m(ts[0], dateConstants)) {
                    maxDelayMorning = Math.max(diff, maxDelayMorning);
                    sumDelayMorning += diff;
                }else if(isTimeBetween8h45mAnd16h00m(ts[0], dateConstants)) {
                    maxDelayMidday = Math.max(diff, maxDelayMidday);
                    sumDelayMidday += diff;
                }else if(isTimeBetween16h00mAnd0h00m(ts[0], dateConstants)) {
                    maxDelayNight = Math.max(diff, maxDelayNight);
                    sumDelayNight += diff;
                }else if(isTimeBetween0h00mAnd7h15m(ts[0], dateConstants)) {
                    maxDelayMidnight = Math.max(diff, maxDelayMidnight);
                    sumDelayMidnight +=diff;
                }

                lastSync = Math.max(lastSync, ts[0]);
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
            res.addValue("0_maxDelayMorning", calcTimeout(maxDelayMorning));
            res.addValue("0_sumDelayMorning", calcTimeout(sumDelayMorning));
            res.addValue("0_maxDelayMidnight", calcTimeout(maxDelayMidnight));
            res.addValue("0_sumDelayMidnight", calcTimeout(sumDelayMidnight));

            res.addValue(SmsDeliveryCalculationService.getDataTypeName(0), "" + maxDelayMidday);
            res.addValue(SmsDeliveryCalculationService.getDataTypeName(1), "" + sumDelayMidday);
            res.addValue(SmsDeliveryCalculationService.getDataTypeName(2), "" + maxDelayNight);
            res.addValue(SmsDeliveryCalculationService.getDataTypeName(3), "" + sumDelayNight);
            res.addValue(SmsDeliveryCalculationService.getDataTypeName(4), "" + lastSync);
            res.addValue(SmsDeliveryCalculationService.getDataTypeName(5), "" + maxDelayMorning);
            res.addValue(SmsDeliveryCalculationService.getDataTypeName(6), "" + sumDelayMorning);
            res.addValue(SmsDeliveryCalculationService.getDataTypeName(7), "" + maxDelayMidnight);
            res.addValue(SmsDeliveryCalculationService.getDataTypeName(8), "" + sumDelayMidnight);

            return res;
        }

        private void findOrgData(List<SMSDeliveryReportItem> items, Session session) {
            for(SMSDeliveryReportItem reportItem : items) {
                Long orgId = reportItem.getOrgId();
                Org org = (Org) session.get(Org.class, orgId);
                reportItem.setDistrict(org.getDistrict());
                reportItem.setShortNameInfoService(org.getShortNameInfoService());
                reportItem.setShortAddress(org.getShortAddress());
                reportItem.setIntroductionQueue(org.getIntroductionQueue());
                //reportItem.setOrgStatus(org.getStatus().toString());
                reportItem.setOrgStatus(Org.STATE_NAMES[org.getState()]);
            }
        }

        private static boolean isTimeBetween7h15mAnd8h45m(long syncTime , DateComparisonConstants constants) {
            return ((syncTime >= constants.toDay7H15MinInMillis) && (syncTime < constants.toDay8H45MinInMillis));
        }

        private static boolean isTimeBetween8h45mAnd16h00m(long syncTime , DateComparisonConstants constants) {
            return ((syncTime >= constants.toDay8H45MinInMillis) && (syncTime < constants.toDay16H00MinInMillis));

        }

        /*private static boolean isTimeBetween16h00mAnd7h15m(long syncTime , DateComparisonConstants constants) {
            return (((syncTime >= constants.toDay16H00MinInMillis) && (syncTime < constants.secondDayStartInMillis)) ||
                    ((syncTime >= constants.todayStartInMillis) && (syncTime < constants.toDay7H15MinInMillis)));
        }*/

        private static boolean isTimeBetween16h00mAnd0h00m(long syncTime, DateComparisonConstants constants) {
            return (((syncTime >= constants.toDay16H00MinInMillis) && (syncTime < constants.secondDayStartInMillis)) ||
                    ((syncTime >= constants.todayStartInMillis) && (syncTime < constants.toDay0H00MinInMillis)));
        }

        private static boolean isTimeBetween0h00mAnd7h15m(long syncTime, DateComparisonConstants constants) {
            return (((syncTime >= constants.toDay0H00MinInMillis) && (syncTime < constants.secondDayStartInMillis)) || (
                    (syncTime >= constants.todayStartInMillis) && (syncTime < constants.toDay7H15MinInMillis)));
        }

        protected static SMSDeliveryReportItem calcSmsDeliveryitem(List<DeliveryEntry> items, SMSDeliveryReportItem res, Integer uniqueId) {
            long minTime = Long.MAX_VALUE;
            long maxTime = Long.MIN_VALUE;
            long sumTime = 0L;
            for(DeliveryEntry deliveryEntry : items) {
                long timeDifference = deliveryEntry.getDifferenceDate();
                minTime = Math.min(minTime, timeDifference);
                maxTime = Math.max(maxTime, timeDifference);
                sumTime += timeDifference;
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

            final boolean lessThenZero = time < 0L;
            if(lessThenZero) time = Math.abs(time);
            long hourAmount = time / MILLIS_IN_HOUR;
            long minuteAmount = (time - (hourAmount * MILLIS_IN_HOUR)) / MILLIS_IN_MINUTE;
            long secondAmount = (time - (hourAmount * MILLIS_IN_HOUR) - (minuteAmount * MILLIS_IN_MINUTE)) / MILLIS_IN_SECOND;
            return String.format("%s%d:%02d:%02d", lessThenZero ? "-" : "", hourAmount, minuteAmount, secondAmount);
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
        return REPORT_PERIOD_PREV_DAY;
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

    public static class DateComparisonConstants {
        private static final long DAY_MILLISECONDS = 86400000L;

        private final Date toDay0Hours00Minutes;
        private final Date toDay7Hours15Minutes;
        private final Date toDay8Hours45Minutes;
        private final Date toDay16Hours00Minutes;

        public final long todayStartInMillis;
        public final long toDay7H15MinInMillis;
        public final long toDay0H00MinInMillis;
        public final long toDay8H45MinInMillis;
        public final long toDay16H00MinInMillis;
        public final long secondDayStartInMillis;

        public DateComparisonConstants(Date startDate) {
            todayStartInMillis = startDate.getTime();
            secondDayStartInMillis = startDate.getTime() + DAY_MILLISECONDS;

            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(startDate.getTime());
            CalendarUtils.setHoursAndMinutes(calendar, 0, 00);
            toDay0Hours00Minutes = calendar.getTime();
            CalendarUtils.setHoursAndMinutes(calendar, 7, 15);
            toDay7Hours15Minutes = calendar.getTime();
            CalendarUtils.setHoursAndMinutes(calendar, 8, 45);
            toDay8Hours45Minutes = calendar.getTime();
            CalendarUtils.setHoursAndMinutes(calendar, 16, 00);
            toDay16Hours00Minutes = calendar.getTime();

            toDay0H00MinInMillis = toDay0Hours00Minutes.getTime();
            toDay7H15MinInMillis = toDay7Hours15Minutes.getTime();
            toDay8H45MinInMillis = toDay8Hours45Minutes.getTime();
            toDay16H00MinInMillis = toDay16Hours00Minutes.getTime();
        }
    }
}
