/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.security;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.SecurityJournalBalance;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.10.15
 * Time: 18:05
 * To change this template use File | Settings | File Templates.
 */
public class JournalBalancesReport extends BasicReportForAllOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(JournalBalancesReport.class);
    private String htmlReport;

    public JournalBalancesReport() {}

    public JournalBalancesReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        List<Long> clientsIds;

        public Builder(String templateFilename, List<Long> clientsIds) {
            this.templateFilename = templateFilename;
            this.clientsIds = clientsIds;
        }

        public Builder() {
            String reportsTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();
            templateFilename = reportsTemplateFilePath + JournalBalancesReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {

            Date generateTime = new Date();

            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            HashMap<Long, List<String>> mapGuardians = new HashMap<Long, List<String>>();
            List<Client> clients = new ArrayList<Client>();
            for (Long id : clientsIds) {
                Client client =  (Client)session.load(Client.class, id);
                clients.add(client);
            }

            JRDataSource dataSource = createDataSource(session, startTime, endTime, clients, mapGuardians);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();

            long generateDuration = generateEndTime.getTime() - generateTime.getTime();

            return new JournalBalancesReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, List<Client> clients, HashMap<Long, List<String>> mapGuardians) throws Exception {
            List<SJBalances> list = new ArrayList<SJBalances>();
            Criteria criteria = session.createCriteria(SecurityJournalBalance.class);
            criteria.add(Restrictions.ge("eventDate", startTime));
            criteria.add(Restrictions.le("eventDate", endTime));
            if (clients != null && clients.size() > 0) {
                criteria.add(Restrictions.in("client", clients));
            }
            criteria.addOrder(Order.asc("eventDate"));
            //criteria.addOrder(Order.asc("client.person.fullName"));
            List<SecurityJournalBalance> query = criteria.list();
            for (SecurityJournalBalance balance : query) {
                SJBalances item = new SJBalances();
                item.setIdOfJournalBalance(balance.getIdOfJournalBalance());
                item.setEventType(balance.getEventType().toString());
                item.setEventDate(balance.getEventDate());
                item.setEventSource(balance.getEventSource().toString());
                item.setIsSuccess(balance.getIsSuccess() ? "Да" : "Нет");
                item.setTerminal(balance.getTerminal());
                item.setProtocol(balance.getProtocol());
                item.setEventInterface(balance.getEventInterface());
                item.setContractId(balance.getClient().getContractId());
                item.setFioOfClient(balance.getClient().getPerson().getFullName());
                if (balance.getClientPayment() != null) {
                    item.setIdOfClientPayment(balance.getClientPayment().getIdOfClientPayment());
                }
                item.setOrder(balance.getIdOfOrder() == null ? "" : balance.getIdOfOrg().toString() + " / " + balance.getIdOfOrder().toString());
                item.setRequest(balance.getRequest());
                item.setClientAddress(balance.getClientAddress());
                item.setServerAddress(balance.getServerAddress());
                item.setMessage(balance.getMessage());
                if (balance.getAccountTransaction() != null) {
                    item.setIdOfAccountTransaction(balance.getAccountTransaction().getIdOfTransaction());
                }
                list.add(item);
            }

            return new JRBeanCollectionDataSource(list);

        }

    }

    public static class SJBalances {
        private Long idOfJournalBalance;
        private String eventType; //пополнение или списание
        private Date eventDate;
        private String eventSource; //источник записи
        private String isSuccess;
        private String terminal;
        private String protocol;
        private String eventInterface;
        private Long contractId;
        private String fioOfClient;
        private Long idOfClientPayment;
        private String order;
        private String request;
        private String clientAddress;
        private String serverAddress;
        private String message;
        private Long idOfAccountTransaction;

        public Long getIdOfJournalBalance() {
            return idOfJournalBalance;
        }

        public void setIdOfJournalBalance(Long idOfJournalBalance) {
            this.idOfJournalBalance = idOfJournalBalance;
        }

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public Date getEventDate() {
            return eventDate;
        }

        public void setEventDate(Date eventDate) {
            this.eventDate = eventDate;
        }

        public String getEventSource() {
            return eventSource;
        }

        public void setEventSource(String eventSource) {
            this.eventSource = eventSource;
        }

        public String getIsSuccess() {
            return isSuccess;
        }

        public void setIsSuccess(String success) {
            isSuccess = success;
        }

        public String getTerminal() {
            return terminal;
        }

        public void setTerminal(String terminal) {
            this.terminal = terminal;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getEventInterface() {
            return eventInterface;
        }

        public void setEventInterface(String eventInterface) {
            this.eventInterface = eventInterface;
        }

        public Long getContractId() {
            return contractId;
        }

        public void setContractId(Long contractId) {
            this.contractId = contractId;
        }

        public String getFioOfClient() {
            return fioOfClient;
        }

        public void setFioOfClient(String fioOfClient) {
            this.fioOfClient = fioOfClient;
        }

        public Long getIdOfClientPayment() {
            return idOfClientPayment;
        }

        public void setIdOfClientPayment(Long idOfClientPayment) {
            this.idOfClientPayment = idOfClientPayment;
        }

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }

        public String getRequest() {
            return request;
        }

        public void setRequest(String request) {
            this.request = request;
        }

        public String getClientAddress() {
            return clientAddress;
        }

        public void setClientAddress(String clientAddress) {
            this.clientAddress = clientAddress;
        }

        public String getServerAddress() {
            return serverAddress;
        }

        public void setServerAddress(String serverAddress) {
            this.serverAddress = serverAddress;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Long getIdOfAccountTransaction() {
            return idOfAccountTransaction;
        }

        public void setIdOfAccountTransaction(Long idOfAccountTransaction) {
            this.idOfAccountTransaction = idOfAccountTransaction;
        }
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new JournalBalancesReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(); // Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }
}
