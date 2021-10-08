/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ClientBalanceByDayReport extends BasicReportForContragentJob {
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
    public static final String REPORT_NAME = "Отчет по балансам клиентов на дату";
    public static final String[] TEMPLATE_FILE_NAMES = {"ClientBalanceByDayReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{20, 3, 45};


    public static final int NO_CONDITION = 0;
    public static final int LT_ZERO = 1;
    public static final int EQ_ZERO = 2;
    public static final int GT_ZERO = 3;
    public static final int NE_ZERO = 4;

    final public static String P_CLIENT_BALANCE_CONDITION_TYPE = "clientBalanceCondition";

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

        public static class ClientBalanceInfo {

            private String orgShortName;
            private Long contractId;
            private String groupName;
            private long totalBalance;
            private Long idOfClient;
            private Long limit;
            private String date;

            ClientBalanceInfo() {

            }

            ClientBalanceInfo(Client client, long totalBalance, String date) {
                this.orgShortName = client.getOrg().getShortName();
                this.contractId = client.getContractId();
                final Person person = client.getPerson();
                if (client.getClientGroup() != null) {
                    this.groupName = client.getClientGroup().getGroupName();
                } else {
                    this.groupName = "";
                }
                this.totalBalance = totalBalance;
                this.idOfClient = client.getIdOfClient();
                this.limit = client.getLimit();
                this.date = date;
            }

            public Long getLimit() {
                return limit;
            }

            public void setLimit(Long limit) {
                this.limit = limit;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public Long getIdOfClient() {
                return idOfClient;
            }

            public void setIdOfClient(Long idOfClient) {
                this.idOfClient = idOfClient;
            }

            public String getOrgShortName() {
                return orgShortName;
            }

            public String getGroupName() {
                return groupName;
            }

            public Long getContractId() {
                return contractId;
            }

            public long getTotalBalance() {
                return totalBalance;
            }

            public void setContractId(Long contractId) {
                this.contractId = contractId;
            }

            public void setTotalBalance(long totalBalance) {
                this.totalBalance = totalBalance;
            }

            public void setOrgShortName(String orgShortName) {
                this.orgShortName = orgShortName;
            }

            public void setGroupName(String groupName) {
                this.groupName = groupName;
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
                    //getClientBalanceCondiotion(reportProperties.getProperty("clientBalanceCondition"));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, endTime, idOfOrgList, clientGroupId, clientBalanceCondition));
            Date generateEndTime = new Date();
            return new ClientBalanceByDayReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, idOfContragent1);
        }

        private JRDataSource createDataSource(Session session, Date endTime, List<Long> idOfOrgList, Long clientGroupId,
                Integer clientBalanceCondition) throws Exception {
            Long idOfContragent1 = null;
            if (contragent != null) {
                idOfContragent1 = contragent.getIdOfContragent();
            }
            List<ClientBalanceInfo> result = buildReportItems(session, idOfContragent1, idOfOrgList, endTime,
                    clientGroupId, clientBalanceCondition);
            return new JRBeanCollectionDataSource(result);
        }

        public List<ClientBalanceInfo> buildReportItems(Session session, Long idOfContragent, List<Long> idOfOrgList,
                Date endTime, Long clientGroupId, Integer clientBalanceCondition) {

            List<ClientBalanceInfo> result = new ArrayList<ClientBalanceInfo>();

            Criteria orgsCriteria = session.createCriteria(Org.class);
            if (!CollectionUtils.isEmpty(idOfOrgList)) {
                orgsCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
            }
            if (idOfContragent != null) {
                orgsCriteria.add(Restrictions.eq("defaultSupplier.idOfContragent", idOfContragent));
            }
            String orgs_str = "";
            orgsCriteria.addOrder(Order.asc("shortName"));
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

            String clientWhere = "";

            List infos = DAOReadonlyService.getInstance().getClientBalanceInfos(orgs_str, groupWhere, endTime, new Date(System.currentTimeMillis()), clientWhere);
            for (Object obj : infos) {
                Object[] row = (Object[]) obj;
                ClientBalanceInfo clientItem = new ClientBalanceInfo();
                Long idOfClient = ((BigInteger)row[0]).longValue();
                String orgShortName = (String)row[1];
                String groupName = (String)row[2];
                Long contractId = ((BigInteger)row[3]).longValue();
                //String surname = (String)row[4];
                //String firstName = (String)row[5];
                //String secondName = (String)row[6];
                Long limit = ((BigInteger)row[7]).longValue();
                Long totalBalance = ((BigInteger)row[8]).longValue() - ((BigDecimal)row[9]).longValue();
                String date = row[10] == null ? "" : CalendarUtils.dateTimeToString(new Date(((BigInteger)row[10]).longValue()));

                //String date;
                clientItem.setIdOfClient(idOfClient);
                clientItem.setOrgShortName(orgShortName);
                clientItem.setGroupName(groupName);
                clientItem.setContractId(contractId);
                clientItem.setLimit(limit);
                clientItem.setTotalBalance(totalBalance);
                clientItem.setDate(date);
                switch (clientBalanceCondition) {
                    case 0:
                        result.add(clientItem);
                        break;
                    case 1:
                        if (clientItem.getTotalBalance() < 0L) {
                            result.add(clientItem);
                        }
                        break;
                    case 2:
                        if (clientItem.getTotalBalance() == 0L) {
                            result.add(clientItem);
                        }
                        break;
                    case 3:
                        if (clientItem.getTotalBalance() > 0L) {
                            result.add(clientItem);
                        }
                        break;
                    case 4:
                        if (clientItem.getTotalBalance() != 0L) {
                            result.add(clientItem);
                        }
                        break;
                }
            }
            return result;
        }
    }

    public ClientBalanceByDayReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfContragent);    //To change body of overridden methods use File | Settings | File Templates.
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

    public ClientBalanceByDayReport() {
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new ClientBalanceByDayReport();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }

    private static final Logger logger = LoggerFactory.getLogger(ClientBalanceByDayReport.class);

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

    @Override
    public AutoReportRunner getAutoReportRunner() {
        return new AutoReportRunner(){
            @Override
            public void run(AutoReportBuildTask autoReportBuildTask) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(String.format("Building auto reports \"%s\"",
                            getMyClass().getCanonicalName()));
                }
                String classPropertyValue = getMyClass().getCanonicalName();
                List<AutoReport> autoReports = new ArrayList<AutoReport>();
                Session session = null;
                org.hibernate.Transaction transaction = null;
                try {
                    session = autoReportBuildTask.sessionFactory.openSession();
                    transaction = BasicReport.createTransaction(session);
                    transaction.begin();

                    String jobId = autoReportBuildTask.jobId;
                    Long idOfSchedulerJob = Long.valueOf(jobId);

                    List<RuleProcessor.Rule> thisReportRulesList = getThisReportRulesList(session, idOfSchedulerJob);

                    for (Object object: thisReportRulesList){
                        RuleProcessor.Rule rule = (RuleProcessor.Rule) object;
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug(String.format("Building report \"%s\" for contragent: %s", classPropertyValue));
                        }
                        Properties properties = new Properties();
                        ReportPropertiesUtils.addProperties(properties, getMyClass(), autoReportBuildTask);
                        String clientBalanceCondition = rule.getExpressionValue("clientBalanceCondition");
                        if (clientBalanceCondition != null) {
                            properties.setProperty("clientBalanceCondition", clientBalanceCondition);
                        }
                        Long idOfContragent = null;
                        try {
                            idOfContragent = Long.parseLong(rule.getExpressionValue("idOfContragent"));
                        } catch (NumberFormatException ignored) {}
                        if (idOfContragent != null) {
                            properties.setProperty("idOfContragent", idOfContragent.toString());
                        }
                        Long idOfOrg = null;
                        try {
                            idOfOrg = Long.parseLong(rule.getExpressionValue("idOfOrg"));
                        } catch (NumberFormatException ignored) {}
                        if (idOfOrg != null) {
                            properties.setProperty("idOfOrg", idOfOrg.toString());
                        }

                        BasicReportForContragentJob report = createInstance();
                        report.initialize(autoReportBuildTask.startTime, autoReportBuildTask.endTime,
                                idOfContragent, autoReportBuildTask.templateFileName,
                                autoReportBuildTask.sessionFactory, autoReportBuildTask.startCalendar);
                        autoReports.add(new AutoReport(report, properties));
                    }

                    List<Long> reportHandleRuleIdsList = getRulesIdsByJobRules(session, idOfSchedulerJob);

                    transaction.commit();
                    transaction = null;
                    autoReportBuildTask.executorService.execute(
                            new AutoReportProcessor.ProcessTask(autoReportBuildTask.autoReportProcessor, autoReports,
                                    autoReportBuildTask.documentBuilders, reportHandleRuleIdsList));
                } catch (Exception e) {
                    getLogger().error(String.format("Failed at building auto reports \"%s\"", classPropertyValue), e);
                } finally {
                    HibernateUtils.rollback(transaction, getLogger());
                    HibernateUtils.close(session, getLogger());
                }
            }
        };
    }
}