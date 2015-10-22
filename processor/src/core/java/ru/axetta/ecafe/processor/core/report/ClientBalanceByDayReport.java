/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.criterion.Order;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static final int NO_CONDITION = 0;
    public static final int LT_ZERO = 1;
    public static final int EQ_ZERO = 2;
    public static final int GT_ZERO = 3;

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
            private String firstName;
            private String surname;
            private String secondName;
            private String groupName;
            private long totalBalance;
            private Long idOfClient;
            private Long limit;
            private String date;

            ClientBalanceInfo(Client client, long totalBalance, String date) {
                this.orgShortName = client.getOrg().getShortName();
                this.contractId = client.getContractId();
                final Person person = client.getPerson();
                this.firstName = person.getFirstName();
                this.surname = person.getSurname();
                this.secondName = person.getSecondName();
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

            public String getFirstName() {
                return firstName;
            }

            public String getSurname() {
                return surname;
            }

            public String getSecondName() {
                return secondName;
            }

            public long getTotalBalance() {
                return totalBalance;
            }

            public void setContractId(Long contractId) {
                this.contractId = contractId;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public void setSurname(String surname) {
                this.surname = surname;
            }

            public void setSecondName(String secondName) {
                this.secondName = secondName;
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
            DetachedCriteria idOfClientCriteria = DetachedCriteria.forClass(Client.class);
            idOfClientCriteria.createAlias("clientGroup", "cg", JoinType.LEFT_OUTER_JOIN);
            String cgFieldName = "cg.compositeIdOfClientGroup.idOfClientGroup";
            if (!clientGroupId.equals(ClientGroupMenu.CLIENT_ALL)) {
                if (clientGroupId.equals(ClientGroupMenu.CLIENT_STUDENTS)) {
                    idOfClientCriteria
                            .add(Restrictions.not(Restrictions.in(cgFieldName, ClientGroupMenu.getNotStudent())));
                } else {
                    idOfClientCriteria.add(Restrictions.eq(cgFieldName, clientGroupId));
                }
            }
            idOfClientCriteria.createCriteria("org", "o");
         /*   if (!CollectionUtils.isEmpty(idOfOrgList)) {
                idOfClientCriteria.add(Restrictions.in("o.idOfOrg", idOfOrgList));
            }*/
            if (idOfContragent != null) {
                idOfClientCriteria.add(Restrictions.eq("o.defaultSupplier.idOfContragent", idOfContragent));
            }
            idOfClientCriteria.setProjection(Property.forName("idOfClient"));

            Criteria criteria = session.createCriteria(AccountTransaction.class);
            criteria.add(Restrictions.lt("transactionTime", endTime));    // <
            criteria.createCriteria("org", "o");
            if (!CollectionUtils.isEmpty(idOfOrgList)) {
                criteria.add(Restrictions.in("o.idOfOrg", idOfOrgList));
            }
            criteria.add(Property.forName("client.idOfClient").in(idOfClientCriteria));
            criteria.setProjection(Projections.projectionList().add(Projections.sum("transactionSum"))
                    .add(Projections.groupProperty("client")).add(Projections.max("transactionTime")));
            criteria.addOrder(Order.asc("client"));
            //criteria.addOrder(Order.asc("client.person.fullName"));
            List list = criteria.list();
            for (Object obj : list) {
                Object[] row = (Object[]) obj;
                long balance = Long.valueOf(row[0].toString());
                Client client = (Client) row[1];
                String date = CalendarUtils.dateTimeToString((Date) row[2]);
                ClientBalanceInfo clientItem = new ClientBalanceInfo(client, balance, date);

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

    private static Integer getClientBalanceCondiotion(String s) {
        if (s.equals("Не задано")) {
            return NO_CONDITION;
        } else if (s.equals("Меньше 0")) {
            return LT_ZERO;
        } else if (s.equals("Равен 0")) {
            return EQ_ZERO;
        } else if (s.equals("Больше 0")) {
            return GT_ZERO;
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
                    List<RuleProcessor.Rule> thisReportRulesList = getThisReportRulesList(session);

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
                    transaction.commit();
                    transaction = null;
                    autoReportBuildTask.executorService.execute(
                            new AutoReportProcessor.ProcessTask(autoReportBuildTask.autoReportProcessor, autoReports,
                                    autoReportBuildTask.documentBuilders));
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