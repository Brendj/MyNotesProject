/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
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

            ClientBalanceInfo(Client client, long totalBalance) {
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
            }

            public Long getLimit() {
                return limit;
            }

            public void setLimit(Long limit) {
                this.limit = limit;
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
            return null;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar,
                Long clientGroupId) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);
            Long idOfContragent1 = -1L;
            if (contragent != null) {
                parameterMap.put("contragentName", contragent.getContragentName());
                idOfContragent1 = contragent.getIdOfContragent();
            }
            String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, startTime, idOfOrgList, clientGroupId));
            Date generateEndTime = new Date();
            return new ClientBalanceByDayReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, idOfContragent1);
        }

        private JRDataSource createDataSource(Session session, Date endTime, List<Long> idOfOrgList, Long clientGroupId)
                throws Exception {
            Long idOfContragent1 = null;
            if (contragent != null) {
                idOfContragent1 = contragent.getIdOfContragent();
            }
            List<ClientBalanceInfo> result = buildReportItems(session, idOfContragent1, idOfOrgList, endTime,
                    clientGroupId);
            return new JRBeanCollectionDataSource(result);
        }

        public List<ClientBalanceInfo> buildReportItems(Session session, Long idOfContragent, List<Long> idOfOrgList,
                Date endTime, Long clientGroupId) {
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
            if (!CollectionUtils.isEmpty(idOfOrgList)) {
                idOfClientCriteria.add(Restrictions.in("o.idOfOrg", idOfOrgList));
            }
            if (idOfContragent != null) {
                idOfClientCriteria.add(Restrictions.eq("o.defaultSupplier.idOfContragent", idOfContragent));
            }
            idOfClientCriteria.setProjection(Property.forName("idOfClient"));

            Criteria criteria = session.createCriteria(AccountTransaction.class);
            criteria.add(Restrictions.lt("transactionTime", endTime));    // <
            criteria.add(Property.forName("client.idOfClient").in(idOfClientCriteria));
            criteria.setProjection(Projections.projectionList().add(Projections.sum("transactionSum"))
                    .add(Projections.groupProperty("client")));
            criteria.addOrder(Order.asc("client"));
            //criteria.addOrder(Order.asc("client.person.fullName"));
            List list = criteria.list();
            for (Object obj : list) {
                Object[] row = (Object[]) obj;
                long balance = Long.valueOf(row[0].toString());
                Client client = (Client) row[1];
                ClientBalanceInfo clientItem = new ClientBalanceInfo(client, balance);
                result.add(clientItem);
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
}