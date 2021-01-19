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

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.10.2009
 * Time: 14:09:39
 * Отчет по балансам клиентов на дату
 */
public class ClientBalanceByOrgReport extends BasicReportForContragentJob {
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
    public static final String REPORT_NAME = "Остаток денежных средств по организациям на дату";
    public static final String[] TEMPLATE_FILE_NAMES = {"ClientBalanceByOrgReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    public static final int NO_CONDITION = 0;
    public static final int LT_ZERO = 1;
    public static final int EQ_ZERO = 2;
    public static final int GT_ZERO = 3;
    public static final int NE_ZERO = 4;
    private String htmlReport;

    final public static String P_CLIENT_BALANCE_CONDITION_TYPE = "clientBalanceCondition";

    public String getHtmlReport() {
        return htmlReport;
    }

    public static class Builder extends BasicReportForContragentJob.Builder {

        public String getTemplateFilename() {
            return templateFilename;
        }

        protected static class OrgItem {

            private final Long idOfOrg;
            private final String shortName;
            private final String officialName;

            public Long getIdOfOrg() {
                return idOfOrg;
            }

            public String getShortName() {
                return shortName;
            }

            public String getOfficialName() {
                return officialName;
            }

            public OrgItem(Org org) {
                this.idOfOrg = org.getIdOfOrg();
                this.shortName = org.getShortName();
                this.officialName = org.getOfficialName();
            }

            public OrgItem() {
                this.idOfOrg = null;
                this.shortName = null;
                this.officialName = null;
            }

            @Override
            public String toString() {
                return "OrgItem{" + "idOfOrg=" + idOfOrg + ", shortName='" + shortName + '\'' + ", officialName='"
                        + officialName + '\'' + '}';
            }
        }

        public static class OrgBalanceInfo {

            private String orgShortName;
            private Long idOfOrg;
            private String orgAddress;
            private long totalBalance;

            OrgBalanceInfo() {

            }

            OrgBalanceInfo(Org org, long totalBalance) {
                this.orgShortName = org.getShortName();
                this.idOfOrg = org.getIdOfOrg();
                this.orgAddress = org.getAddress();
                this.totalBalance = totalBalance;
            }

            public Long getIdOfOrg() {
                return idOfOrg;
            }

            public void setIdOfOrg(Long idOfOrg) {
                this.idOfOrg = idOfOrg;
            }

            public String getOrgShortName() {
                return orgShortName;
            }

            public void setOrgShortName(String orgShortName) {
                this.orgShortName = orgShortName;
            }

            public long getTotalBalance() {
                return totalBalance;
            }

            public void setTotalBalance(long totalBalance) {
                this.totalBalance = totalBalance;
            }

            public String getOrgAddress() {
                return orgAddress;
            }

            public void setOrgAddress(String orgAddress) {
                this.orgAddress = orgAddress;
            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(endTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("endDate", endTime);
            Long idOfContragent1 = -1L;
            if (contragent != null) {
                parameterMap.put("contragentName", contragent.getContragentName());
                idOfContragent1 = contragent.getIdOfContragent();
            }
            String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            String clientGroupIdString = reportProperties.getProperty("clientGroupId");
            Long clientGroupId = null;
            try {
                clientGroupId = Long.valueOf(clientGroupIdString);
            } catch (NumberFormatException e) {
                clientGroupId = ClientGroupMenu.CLIENT_ALL;
            }
            Integer clientBalanceCondition = Integer.parseInt(reportProperties.getProperty("clientBalanceCondition"));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, endTime, idOfOrgList, clientGroupId, clientBalanceCondition));
            Date generateEndTime = new Date();
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
            return new ClientBalanceByOrgReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, idOfContragent1).setHtmlReport(os.toString("UTF-8"));
        }

        private JRDataSource createDataSource(Session session, Date endTime, List<Long> idOfOrgList, Long clientGroupId,
                Integer clientBalanceCondition) throws Exception {
            Long idOfContragent1 = null;
            if (contragent != null) {
                idOfContragent1 = contragent.getIdOfContragent();
            }
            List<OrgBalanceInfo> result = buildReportItems(session, idOfContragent1, idOfOrgList, endTime,
                    clientGroupId, clientBalanceCondition);
            return new JRBeanCollectionDataSource(result);
        }

        public List<OrgBalanceInfo> buildReportItems(Session session, Long idOfContragent, List<Long> idOfOrgList,
                Date endTime, Long clientGroupId, Integer clientBalanceCondition) {

            List<OrgBalanceInfo> result = new ArrayList<OrgBalanceInfo>();

            Criteria orgsCriteria = session.createCriteria(Org.class);
            if (!CollectionUtils.isEmpty(idOfOrgList)) {
                orgsCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
            }
            if (idOfContragent != null) {
                orgsCriteria.add(Restrictions.eq("defaultSupplier.idOfContragent", idOfContragent));
            }
            String orgs_str = "";
            orgsCriteria.addOrder(Order.asc("idOfOrg"));
            List<Org> orgs = orgsCriteria.list();
            for (Org org : orgs) {
                orgs_str += org.getIdOfOrg().toString() + ",";
            }
            orgs_str = orgs_str.substring(0, orgs_str.length()-1);

            String groupWhere = "";
            if (!clientGroupId.equals(ClientGroupMenu.CLIENT_ALL)) {
                if (clientGroupId.equals(ClientGroupMenu.CLIENT_STUDENTS)) {
                    List<Long> www = ClientGroupMenu.getNotStudent();
                    for (Long v : www) {
                        groupWhere += v.toString() + ",";
                    }
                    groupWhere = "and g.idofclientgroup not in (" + groupWhere.substring(0, groupWhere.length()-1) + ")";
                } else {
                    groupWhere = String.format("and g.idofclientgroup = %s", clientGroupId);
                }
            }

            List infos = DAOService.getInstance().getClientBalanceInfosWithoutMigrations(orgs_str, groupWhere, endTime,
                    new Date(System.currentTimeMillis()), "");
            infos.addAll(DAOService.getInstance().getClientBalanceInfosWithMigrations(orgs_str, groupWhere, endTime,
                    new Date(System.currentTimeMillis()), "", idOfOrgList));
            for (Org org : orgs) {
                OrgBalanceInfo orgItem = new OrgBalanceInfo();
                Long idOfOrg = org.getIdOfOrg();
                Long orgBalance = 0L;

                for (Object obj : infos) {
                    Object[] row = (Object[]) obj;
                    Long orgId = ((BigInteger)row[11]).longValue();
                    if (!orgId.equals(idOfOrg)) {
                        continue;
                    }
                    Long totalBalance = ((BigInteger)row[8]).longValue() - ((BigDecimal)row[9]).longValue();
                    switch (clientBalanceCondition) {
                        case 0:
                            orgBalance += totalBalance;
                            break;
                        case 1:
                            if (totalBalance < 0L) {
                                orgBalance += totalBalance;
                            }
                            break;
                        case 2:
                            if (totalBalance == 0L) {
                                orgBalance += totalBalance;
                            }
                            break;
                        case 3:
                            if (totalBalance > 0L) {
                                orgBalance += totalBalance;
                            }
                            break;
                        case 4:
                            if (totalBalance != 0L) {
                                orgBalance += totalBalance;
                            }
                            break;
                    }
                }
                orgItem.setIdOfOrg(idOfOrg);
                orgItem.setOrgShortName(org.getShortName());
                orgItem.setOrgAddress(org.getAddress());
                orgItem.setTotalBalance(orgBalance);
                result.add(orgItem);
            }
            return result;
        }


    }

    public ClientBalanceByOrgReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfContragent);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ClientBalanceByOrgReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public int getDefaultReportPeriod() {
        return BasicReportJob.REPORT_PERIOD_TODAY;
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public ClientBalanceByOrgReport() {
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new ClientBalanceByOrgReport();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }

    private static final Logger logger = LoggerFactory.getLogger(ClientBalanceByOrgReport.class);

    private static Integer getClientBalanceCondition(String s) {
        if (s.equals("Не задано")) {
            return NO_CONDITION;
        } else if (s.equals("Меньше 0")) {
            return LT_ZERO;
        } else if (s.equals("Равен 0")) {
            return EQ_ZERO;
        } else if (s.equals("Больше 0")) {
            return GT_ZERO;
        } else if (s.equals("Кроме 0")) {
            return NE_ZERO;
        }
        return null;
    }

}