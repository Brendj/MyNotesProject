/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
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
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
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
 * User: Anvarov
 * Date: 25.03.16
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
public class InteractiveCardDataReport extends BasicReportForAllOrgJob {
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
    public static final String REPORT_NAME = "Отчет по обороту электронных карт";
    public static final String[] TEMPLATE_FILE_NAMES = {"InteractiveCardDataReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{-3};


    private final static Logger logger = LoggerFactory.getLogger(InteractiveCardDataReport.class);
    public static DateFormat dailyItemsFormat = new SimpleDateFormat("dd.MM.yyyy");

    protected List<InteractiveCardDataReportItem> items;
    private String htmlReport;

    public InteractiveCardDataReport() {
    }

    public InteractiveCardDataReport(Date generateTime, long l, JasperPrint jasperPrint,
            List<InteractiveCardDataReportItem> items) {
        super(generateTime, l, jasperPrint, generateTime, generateTime);
        this.items = items;
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        private boolean exportToHTML = false;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
            exportToHTML = true;
        }

        public Builder() {
            String templateName = InteractiveCardDataReport.class.getSimpleName();
            templateFilename =
                    RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + templateName
                            + ".jasper";
        }

        @Override
        public InteractiveCardDataReport build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            return doBuild(session, calendar);
        }

        public InteractiveCardDataReport doBuild(Session session, Calendar calendar) throws Exception {
            String idOfOrg = StringUtils
                    .trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));

            Org org = (Org) session.load(Org.class, Long.valueOf(idOfOrg));

            Date generateTime = new Date();

            /* Строим параметры для передачи в jasper */
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(generateTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("reportDate", dailyItemsFormat.format(generateTime));
            parameterMap.put("orgName", org.getShortName());
            parameterMap.put("address", org.getAddress());

            Long idOfOrgL = Long.valueOf(idOfOrg);


            List<Long> friendlyOrgsIds = new ArrayList<Long>();

            List<Org> friendlyOrgs = DAOUtils.findAllFriendlyOrgs(session, idOfOrgL);

            for (Org org1 : friendlyOrgs) {
                friendlyOrgsIds.add(org1.getIdOfOrg());
            }

            List<InteractiveCardDataReportItem> items = findItems(session, idOfOrg, friendlyOrgsIds);

            JRDataSource dataSource = createDataSource(items);

            parameterMap.put("percent", getPercent(session, idOfOrg, items, friendlyOrgsIds));

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();


            if (!exportToHTML) {
                return new InteractiveCardDataReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, items);
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
                return new InteractiveCardDataReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, items).setHtmlReport(os.toString("UTF-8"));

            }
        }

        private JRDataSource createDataSource(List<InteractiveCardDataReportItem> items) {
            return new JRBeanCollectionDataSource(items);
        }

        private List<InteractiveCardDataReportItem> findItems(Session session, String idOfOrg, List<Long> friendlyOrgsIds) throws Exception {

            List<InteractiveCardDataReportItem> items = new ArrayList<InteractiveCardDataReportItem>();

            getInteractiveReportDataEntity(session, idOfOrg, items);

            getFromCardData(session, idOfOrg, items, friendlyOrgsIds);

            Collections.sort(items);

            return items;
        }

        private void getFromCardData(Session session, String idOfOrg, List<InteractiveCardDataReportItem> items, List<Long> friendlyOrgsIds)
                throws Exception {

            Long idOfOrgL = Long.valueOf(idOfOrg);

            //1.1
            String sql = "SELECT count(cfc.cardno) FROM cf_cards cfc "
                    + " LEFT JOIN cf_clients cl ON cfc.idofclient = cl.idofclient "
                    + " LEFT OUTER JOIN cf_clientgroups cfcl ON cl.idoforg = cfcl.idoforg AND cl.IdOfClientGroup = cfcl.IdOfClientGroup "
                    + " WHERE cfc.cardtype IN (3,8) AND cfc.state IN (0, 4) "
                    + " AND cfc.idoforg = :idoforg AND cl.idoforg IN (:friendlyOrgs) "
                    + " AND cfcl.idofclientgroup NOT IN (1100000060, 1100000070)";

            Query query = session.createSQLQuery(sql);
            query.setParameter("idoforg", idOfOrgL);
            query.setParameterList("friendlyOrgs", friendlyOrgsIds);
            Long count = ((BigInteger) query.uniqueResult()).longValue();

            InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(1L, "1.1", count,
                    "Социальные карты учащихся", "Количество СКУ, зарегистрированных в ОО и активных");
            items.add(item);

            //1.2
            String sqlEno = "SELECT count(cfc.cardno) FROM cf_cards cfc "
                    + " LEFT JOIN cf_clients cl ON cfc.idofclient = cl.idofclient "
                    + " LEFT OUTER JOIN cf_clientgroups cfcl ON cl.idoforg = cfcl.idoforg AND cl.IdOfClientGroup = cfcl.IdOfClientGroup "
                    + " WHERE cfc.cardtype IN (0,4,5,6,7) AND cfc.state IN (0,4) "
                    + " AND cfc.idoforg = :idoforg AND cl.idoforg IN (:friendlyOrgs)"
                    + " AND cfcl.idofclientgroup NOT IN (1100000060, 1100000070)";

            Query queryEno = session.createSQLQuery(sqlEno);
            queryEno.setParameter("idoforg", idOfOrgL);
            queryEno.setParameterList("friendlyOrgs", friendlyOrgsIds);
            Long countEno = ((BigInteger) queryEno.uniqueResult()).longValue();

            InteractiveCardDataReportItem itemEno = new InteractiveCardDataReportItem(2L, "1.2", countEno,
                    "Другие виды электронных карт",
                    "Количество прочих видов  карт, зарегистрированных в ОО и активных (УЭК, транспортная, СКМ и т.д.)");
            items.add(itemEno);

            //1.3 Будут считаться отдельно только после введения данного типа карты, пока 0
            Long countOth = 0L;
            InteractiveCardDataReportItem itemOth = new InteractiveCardDataReportItem(3L, "1.3", countOth,
                    "Прочие электронные карты", "Количество карт сотрудников поставщика питания");
            items.add(itemOth);

            //1.
            Long fondEl = count + countEno + countOth;

            InteractiveCardDataReportItem itemFondEl = new InteractiveCardDataReportItem(0L, "1", fondEl,
                    "Фонд поступивших электронных карт (не сервисных)",
                    "Количество не сервисных электронных карт различных видов, используемых в ОО в качестве постоянных (зарегистрированы в ОО и находятся на руках у пользователей на текущую дату)");
            items.add(itemFondEl);

            //3.
            String sqlActive = "SELECT count(cfc.cardno) FROM cf_cards cfc "
                    + " LEFT JOIN cf_clients cl ON cfc.idofclient = cl.idofclient "
                    + " LEFT OUTER JOIN cf_clientgroups cfcl ON cl.idoforg = cfcl.idoforg AND cl.IdOfClientGroup = cfcl.IdOfClientGroup"
                    + " WHERE cfc.cardtype IN (1,2,9,10,11) AND cfc.state IN (0,4) "
                    + " AND cfc.idoforg = :idoforg AND cl.idoforg IN (:friendlyOrgs) "
                    + " AND cfcl.idofclientgroup NOT IN (1100000060, 1100000070)";

            Query queryActive = session.createSQLQuery(sqlActive);
            queryActive.setParameter("idoforg", idOfOrgL);
            queryActive.setParameterList("friendlyOrgs", friendlyOrgsIds);
            Long countActive = ((BigInteger) queryActive.uniqueResult()).longValue();

            InteractiveCardDataReportItem itemActive = new InteractiveCardDataReportItem(8L, "3", countActive,
                    "Фонд активных сервисных карт (в обороте)",
                    "Количество сервисных карт, находящихся на текущую дату на руках у пользователей в виде постоянных карт");
            items.add(itemActive);

            //3.1
            String sqlFin = "SELECT count(cfc.cardno) FROM cf_cards cfc"
                    + " LEFT JOIN cf_clients cl ON cfc.idofclient = cl.idofclient "
                    + " LEFT OUTER JOIN cf_clientgroups cfcl ON cl.idoforg = cfcl.idoforg AND cl.IdOfClientGroup = cfcl.IdOfClientGroup"
                    + " WHERE cfc.cardtype IN (1,2,9,10,11) AND cfc.state IN (0,4) "
                    + " AND cfc.idoforg = :idoforg AND cl.idoforg IN (:friendlyOrgs)"
                    + " AND cfc.validdate < :validdate AND cfcl.idofclientgroup NOT IN (1100000060, 1100000070)";

            Query queryFin = session.createSQLQuery(sqlFin);
            queryFin.setParameter("idoforg", idOfOrgL);
            queryFin.setParameterList("friendlyOrgs", friendlyOrgsIds);
            queryFin.setParameter("validdate", CalendarUtils.truncateToDayOfMonth(new Date()).getTime());
            Long countFin = ((BigInteger) queryFin.uniqueResult()).longValue();

            InteractiveCardDataReportItem itemFin = new InteractiveCardDataReportItem(9L, "3.1", countFin,
                    "В том числе карты с истекшим сроком действия",
                    "Количество сервисных карт, находящихся на текущую дату на руках у пользователей в виде постоянных карт с истекшим сроком действия");
            items.add(itemFin);

            //4.
            //Long vibivZablock = 0L;

            //4.1
            /*String sqlVibiv = "SELECT count(cfc.cardno) FROM cf_cards cfc "
                    + " LEFT JOIN cf_clients cl ON cfc.idofclient = cl.idofclient "
                    + " LEFT OUTER JOIN cf_clientgroups cfcl ON cl.idoforg = cfcl.idoforg AND cl.IdOfClientGroup = cfcl.IdOfClientGroup"
                    + " WHERE cfc.cardtype IN (1,2) AND cfc.state IN (0,6,4,1) "
                    + " AND cfc.idoforg = :idoforg AND cl.idoforg IN (:friendlyOrgs)"
                    + " AND cfcl.idofclientgroup IN (1100000060, 1100000070)";

            Query queryVibiv = session.createSQLQuery(sqlVibiv);
            queryVibiv.setParameter("idoforg", idOfOrgL);
            queryVibiv.setParameterList("friendlyOrgs", friendlyOrgsIds);
            Long countVibiv = ((BigInteger) queryVibiv.uniqueResult()).longValue();

            //4.2
            String sqlNeisp = "SELECT count(cfc.idofclient) FROM cf_cards cfc "
                    + " LEFT JOIN cf_clients cl ON cfc.idofclient = cl.idofclient "
                    + " LEFT OUTER JOIN cf_clientgroups cfcl ON cl.idoforg = cfcl.idoforg AND cl.IdOfClientGroup = cfcl.IdOfClientGroup"
                    + " WHERE cfc.cardtype IN (1,2) AND cfc.state IN (1,6) "
                    + " AND cfc.idoforg = :idoforg AND cl.idoforg IN (:friendlyOrgs) "
                    + " AND cfcl.idofclientgroup NOT IN (1100000060, 1100000070)";

            Query queryNeisp = session.createSQLQuery(sqlNeisp);
            queryNeisp.setParameter("idoforg", idOfOrgL);
            queryNeisp.setParameterList("friendlyOrgs", friendlyOrgsIds);
            Long countNeisp = ((BigInteger) queryNeisp.uniqueResult()).longValue();

            //4.3
            String sqlNeispProch = "SELECT count(cfc.cardno) FROM cf_cards cfc WHERE cfc.state IN (5)AND cfc.idoforg = :idoforg";

            Query queryNeispProch = session.createSQLQuery(sqlNeispProch);
            queryNeispProch.setParameter("idoforg", idOfOrgL);
            Long countNeispProch = ((BigInteger) queryNeispProch.uniqueResult()).longValue();

            for (InteractiveCardDataReportItem itemThree : items) {
                if (itemThree.getId().equals(12L)) {
                    countNeispProch = countNeispProch - itemThree.getValue();
                }
            }

            vibivZablock = countVibiv + countNeisp + countNeispProch;*/

        }

        private List<InteractiveCardDataReportItem> getInteractiveReportDataEntity(Session session, String idOfOrg,
                List<InteractiveCardDataReportItem> items) {

            Long idOfOrgL = Long.valueOf(idOfOrg);

            String sql = "SELECT idofrecord, value FROM cf_interactive_report_data WHERE idoforg = :idoforg ";
            Query query = session.createSQLQuery(sql);
            query.setParameter("idoforg", idOfOrgL);
            List res = query.list();

            if (res == null) {
                return Collections.EMPTY_LIST;
            }

            long fondGk = 0;

            boolean existsSix = false;
            boolean existsTO = false;
            boolean existsTT = false;
            boolean existsTTH = false;
            boolean existsFive = false;

            boolean existingFour = false;

            for (Object entry : res) {
                Object e[] = (Object[]) entry;
                long idOfRecord = ((BigInteger) e[0]).longValue();
                String value = (String) e[1];

                if (idOfRecord == 0L) {
                    fondGk += Long.valueOf(value);
                    InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(5L, "2.1",
                            Long.valueOf(value), "В рамках ГК на внедрение",
                            "Количество сервисных карт, поступивших на этапе внедрения ИС ПП");
                    items.add(item);
                    existsTO = true;
                }

                if (idOfRecord == 1L) {
                    fondGk += Long.valueOf(value);
                    InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(6L, "2.2",
                            Long.valueOf(value), "В рамках ГК на сервис",
                            "Количество сервисных карт, поступивших на этапе эксплуатации ИС ПП");
                    items.add(item);
                    existsTT = true;
                }

                if (idOfRecord == 2L) {
                    fondGk += Long.valueOf(value);
                    InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(7L, "2.3",
                            Long.valueOf(value), "Закуплено ОО",
                            "Количество сервисных карт, поступивших в результате их закупки");
                    items.add(item);
                    existsTTH = true;
                }

                if (idOfRecord == 3L) {
                    InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(12L, "6",
                            Long.valueOf(value), "Фонд резервных карт, доступных к использованию",
                            "Количество карт, физически находящихся в резервном фонде");
                    items.add(item);
                    existsSix = true;
                }

                if (idOfRecord == 4L) {
                    InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(11L, "5",
                            Long.valueOf(value), "Потери сервисных  карт",
                            "Количество подтвержденных физических потерь сервисных карт");
                    items.add(item);
                    existsFive = true;
                }

                if (idOfRecord == 5L) {
                    //4.
                    InteractiveCardDataReportItem itemZablock = new InteractiveCardDataReportItem(10L, "4", Long.valueOf(value),
                            "Фонд заблокированных и иных неиспользуемых сервисных карт",
                            "Количество неиспользуемых сервисных карт, которые находятся на руках у пользователей (заблокированные карты клиентов из групп \"Выбывшие\", \"Удаленные\"). Источник пополнения резервного фонда");
                    items.add(itemZablock);
                    existingFour = true;
                }
            }

            if (existsTO == false) {
                InteractiveCardDataReportItem itemTO = new InteractiveCardDataReportItem(5L, "2.1", 0L,
                        "В рамках ГК на внедрение",
                        "Количество сервисных карт, поступивших на этапе внедрения ИС ПП");
                items.add(itemTO);
            }

            if (existsTT == false) {
                InteractiveCardDataReportItem itemTT = new InteractiveCardDataReportItem(6L, "2.2", 0L,
                        "В рамках ГК на сервис",
                        "Количество сервисных карт, поступивших на этапе эксплуатации ИС ПП");
                items.add(itemTT);
            }

            if (existsTTH == false) {
                InteractiveCardDataReportItem itemTTH = new InteractiveCardDataReportItem(7L, "2.3", 0L, "Закуплено ОО",
                        "Количество сервисных карт, поступивших в результате их закупки");
                items.add(itemTTH);
            }

            if (existsFive == false) {
                InteractiveCardDataReportItem itemFive = new InteractiveCardDataReportItem(11L, "5", 0L,
                        "Потери сервисных  карт",
                        "Количество подтвержденных физических потерь сервисных карт");
                items.add(itemFive);
            }

            if (existsSix == false) {
                InteractiveCardDataReportItem itemSix = new InteractiveCardDataReportItem(12L, "6", 0L,
                        "Фонд резервных карт, доступных к использованию",
                        "Количество карт, физически находящихся в резервном фонде");
                items.add(itemSix);
            }

            if (existingFour == false) {
                //4.
                InteractiveCardDataReportItem itemZablock = new InteractiveCardDataReportItem(10L, "4", 0L,
                        "Фонд заблокированных и иных неиспользуемых сервисных карт",
                        "Количество неиспользуемых сервисных карт, которые находятся на руках у пользователей (заблокированные карты клиентов из групп \"Выбывшие\", \"Удаленные\"). Источник пополнения резервного фонда");
                items.add(itemZablock);
            }

            InteractiveCardDataReportItem item = new InteractiveCardDataReportItem(4L, "2", fondGk,
                    "Фонд поступивших сервисных карт", "Количество сервисных карт, зарегистрированных в ИС ПП");
            items.add(item);

            return items;
        }


    }

    public static String getPercent(Session session, String idOfOrg, List<InteractiveCardDataReportItem> items, List<Long> friendlyOrgsIds) {

        Long idOfOrgL = Long.valueOf(idOfOrg);

        String sqlPercent =
                "SELECT count(cfc.cardno) FROM cf_cards cfc LEFT JOIN cf_clients cl ON cfc.idofclient = cl.idofclient"
                        + " WHERE cl.idoforg IN (:friendlyOrgs) and cl.idofclientgroup NOT IN (1100000030, 1100000040, 1100000050, 1100000060, 1100000070, 1100000080) AND cfc.idoforg = :idoforg";

        Query queryPercent = session.createSQLQuery(sqlPercent);
        queryPercent.setParameter("idoforg", idOfOrgL);
        queryPercent.setParameterList("friendlyOrgs", friendlyOrgsIds);
        int countPercent = ((BigInteger) queryPercent.uniqueResult()).intValue();

        int count = 0;

        for (InteractiveCardDataReportItem itemThree : items) {
            if (itemThree.getId().equals(12L)) {
                count = (int) itemThree.getValue();
            }
        }

        String c = "0";

        if (countPercent != 0L) {
            float co = (count * 100) / (float) countPercent;
            c = String.valueOf(Math.round(co * 100.0) / 100.0);
        }

        return c;
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new InteractiveCardDataReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public InteractiveCardDataReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public static final class InteractiveCardDataReportItem implements Comparable<InteractiveCardDataReportItem> {

        protected Long id;
        protected String rowNum;
        protected long value;
        protected String ruleName;
        protected String description;

        public InteractiveCardDataReportItem(Long id, String rowNum, long value, String ruleName, String description) {
            this.id = id;
            this.rowNum = rowNum;
            this.value = value;
            this.ruleName = ruleName;
            this.description = description;
        }

        public InteractiveCardDataReportItem(String rowNum, long value, String ruleName, String description) {
            this.rowNum = rowNum;
            this.value = value;
            this.ruleName = ruleName;
            this.description = description;
        }

        public InteractiveCardDataReportItem() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getRowNum() {
            return rowNum;
        }

        public void setRowNum(String rowNum) {
            this.rowNum = rowNum;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public int compareTo(InteractiveCardDataReportItem o) {
            int retCode = this.id.compareTo(o.getId());
            return retCode;
        }
    }
}

