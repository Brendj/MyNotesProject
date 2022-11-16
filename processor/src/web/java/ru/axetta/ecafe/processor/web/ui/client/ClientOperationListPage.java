/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.apache.commons.lang.StringUtils;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.BankSubscription;
import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.RegularPayment;
import ru.axetta.ecafe.processor.core.persistence.service.clients.ClientDiscountChangeHistoryService;
import ru.axetta.ecafe.processor.core.persistence.service.clients.ClientGroupMigrationHistoryService;
import ru.axetta.ecafe.processor.core.persistence.service.clients.ClientMigrationHistoryService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.items.ClientPassItem;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientOperationListPage extends BasicWorkspacePage {

    Logger logger = LoggerFactory.getLogger(ClientOperationListPage.class);
    private Long idOfClient;
    private Date startTime;
    private Date endTime;
    private final ClientPaymentList clientPaymentList = new ClientPaymentList();
    private final ClientOrderList clientOrderList = new ClientOrderList();
    private final ClientSmsList clientSmsList = new ClientSmsList();
    private List<AccountTransfer> accountTransferList = new ArrayList<AccountTransfer>();
    private List<AccountRefund> accountRefundList = new ArrayList<AccountRefund>();
    private List<AccountTransaction> accountTransactionList = new ArrayList<AccountTransaction>();
    private List<ClientPassItem> clientPasses = new ArrayList<ClientPassItem>();
    private List<RegularPayment> regularPayments = new ArrayList<RegularPayment>();
    private List<ClientGroupMigrationHistory> clientGroupMigrationHistories = new ArrayList<ClientGroupMigrationHistory>();
    private List<ClientMigration> clientMigrations = new ArrayList<ClientMigration>();
    private List<DiscountChangeHistory> discountChangeHistories = new ArrayList<DiscountChangeHistory>();
    private List<ApplicationForFoodReportItem> applicationsForFood = new ArrayList<ApplicationForFoodReportItem>();
    private List<GeoplanerNotificationJournal> geoplanerNotificationJournalList = new LinkedList<>();
    private ApplicationForFoodReportItem currentApplicationForFood;
    private List<BankSubscription> bankSubscriptions;
    private List<ClientsMobileHistory> clientsMobileHistories = new ArrayList<ClientsMobileHistory>();
    private List<PreorderJournalReportItem> clientPreorders = new ArrayList<>();

    public String getPageFilename() {
        return "client/operation_list";
    }

    public ClientOperationListPage() {
        Date currDate = new Date();
        startTime = currDate;
        endTime = DateUtils.addDays(currDate, 1);
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = CalendarUtils.endOfDay(endTime);
    }

    public ClientPaymentList getClientPaymentList() {
        return clientPaymentList;
    }

    public ClientOrderList getClientOrderList() {
        return clientOrderList;
    }

    public ClientSmsList getClientSmsList() {
        return clientSmsList;
    }

    public List<AccountTransaction> getAccountTransactionList() {
        return accountTransactionList;
    }

    public List<AccountTransfer> getAccountTransferList() {
        return accountTransferList;
    }

    public List<AccountRefund> getAccountRefundList() {
        return accountRefundList;
    }

    public List<ClientPassItem> getClientPasses() {
        return clientPasses;
    }

    public List<RegularPayment> getRegularPayments() {
        return regularPayments;
    }

    public void setRegularPayments(List<RegularPayment> regularPayments) {
        this.regularPayments = regularPayments;
    }

    public List<ClientGroupMigrationHistory> getClientGroupMigrationHistories() {
        return clientGroupMigrationHistories;
    }

    public List<ClientMigration> getClientMigrations() {
        return clientMigrations;
    }

    public List<DiscountChangeHistory> getDiscountChangeHistories() {
        return discountChangeHistories;
    }

    @SuppressWarnings("unchecked")

    public void fill(Session session, Long idOfClient, boolean full) throws Exception {
        Client client = (Client) session.load(Client.class, idOfClient);
        this.idOfClient = client.getIdOfClient();
        this.clientPaymentList.fill(session, client, this.startTime, this.endTime);
        this.clientOrderList.fill(session, client, this.startTime, this.endTime);
        this.clientSmsList.fill(session, client, this.startTime, this.endTime);
        /////
        accountTransferList = DAOUtils.getAccountTransfersForClient(session, client, startTime, endTime);
        for (AccountTransfer at : accountTransferList) {
            // lazy load
            at.getClientBenefactor().getPerson().getFullName();
            at.getClientBeneficiary().getPerson().getFullName();
            at.getCreatedBy().getUserName();
        }
        /////
        accountRefundList = DAOUtils.getAccountRefundsForClient(session, client, startTime, endTime);
        for (AccountRefund ar : accountRefundList) {
            // lazy load
            ar.getCreatedBy().getUserName();
        }
        /////
        Criteria criteria = session.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.ge("transactionTime", startTime));
        criteria.add(Restrictions.le("transactionTime", endTime));
        criteria.add(Restrictions.eq("client", client));
        criteria.addOrder(Order.asc("transactionTime"));
        this.accountTransactionList = new ArrayList<AccountTransaction>();
        for (Object o : criteria.list()) {
            AccountTransaction accTrans = (AccountTransaction) o;
            if (accTrans.getCard() != null) {
                accTrans.getCard()
                        .getCardNo(); // lazy load    TODO: для этого необходимо изменить запрос используя join и projections
            }
            if (accTrans.getClient() != null) {
                accTrans.getClient()
                        .getContractId(); // lazy load    TODO: для этого необходимо изменить запрос используя join и projections
            }
            accountTransactionList.add(accTrans);
        }

        Criteria bankSubscriptionCriteria = session.createCriteria(BankSubscription.class);
        bankSubscriptionCriteria.add(Restrictions.eq("client", client))
                .add(Restrictions.isNotNull("activationDate"))
                .addOrder(Order.asc("activationDate"));
        this.bankSubscriptions = (List<BankSubscription>) bankSubscriptionCriteria.list();

        criteria = session.createCriteria(EnterEvent.class);
        criteria.add(Restrictions.ge("evtDateTime", startTime));
        criteria.add(Restrictions.le("evtDateTime", endTime));
        criteria.add(Restrictions.eq("client", client));
        criteria.addOrder(Order.asc("evtDateTime"));
        List<EnterEvent> res = (List<EnterEvent>) criteria.list();
        clientPasses = new LinkedList<>();
        for (EnterEvent event : res) {
            clientPasses.add(new ClientPassItem(session, event));
        }

        criteria = session.createCriteria(ExternalEvent.class);
        criteria.add(Restrictions.ge("evtDateTime", startTime));
        criteria.add(Restrictions.le("evtDateTime", endTime));
        criteria.add(Restrictions.eq("client", client));
        criteria.addOrder(Order.asc("evtDateTime"));
        List<ExternalEvent> res2 = (List<ExternalEvent>) criteria.list();
        for (ExternalEvent event : res2) {
            clientPasses.add(new ClientPassItem(event));
        }
        Collections.sort(clientPasses);

        criteria = session.createCriteria(GeoplanerNotificationJournal.class);
        criteria.add(Restrictions.ge("createDate", startTime));
        criteria.add(Restrictions.le("createDate", endTime));
        criteria.add(Restrictions.eq("client", client));
        criteria.addOrder(Order.asc("createDate"));
        geoplanerNotificationJournalList = criteria.list();

        criteria = session.createCriteria(RegularPayment.class);
        criteria.add(Restrictions.eq("client", client))
                .add(Restrictions.ge("paymentDate", startTime))
                .add(Restrictions.le("paymentDate", endTime))
                .addOrder(Order.asc("paymentDate"));
        regularPayments = (List<RegularPayment>) criteria.list();

        clientsMobileHistories = new ArrayList<>();
        if (full) {
            criteria = session.createCriteria(ClientsMobileHistory.class);
            criteria.add(Restrictions.eq("client", client))
                    .add(Restrictions.ge("createdate", startTime))
                    .add(Restrictions.le("createdate", endTime))
                    .addOrder(Order.desc("createdate"));
            clientsMobileHistories = (List<ClientsMobileHistory>) criteria.list();
        }

        //// client group migrations
        ClientGroupMigrationHistoryService clientGroupMigrationHistoryService = RuntimeContext.getAppContext()
                .getBean(ClientGroupMigrationHistoryService.class);

        clientGroupMigrationHistories = clientGroupMigrationHistoryService.findAll(client.getOrg(), client);

        //// client  migrations
        ClientMigrationHistoryService clientMigrationHistoryService = RuntimeContext.getAppContext()
                .getBean(ClientMigrationHistoryService.class);

        clientMigrations = clientMigrationHistoryService.findAll(client.getOrg(), client);

        ClientDiscountChangeHistoryService clientDiscountChangeService = RuntimeContext.getAppContext()
                .getBean(ClientDiscountChangeHistoryService.class);

        discountChangeHistories = clientDiscountChangeService.findAll(client);

        applicationsForFood.clear();
        List<ApplicationForFood> applicationForFoodList = DAOUtils.getApplicationForFoodListByClient(session, this.idOfClient);
        for (ApplicationForFood applicationForFood : applicationForFoodList) {
            applicationsForFood.add(new ApplicationForFoodReportItem(session, applicationForFood));
        }

        criteria = session.createCriteria(PreorderComplex.class);
        criteria.add(Restrictions.ge("preorderDate", startTime));
        criteria.add(Restrictions.le("preorderDate", endTime));
        criteria.add(Restrictions.eq("client", client));
        criteria.addOrder(Order.asc("preorderDate"));
        List<PreorderComplex> res3 = (List<PreorderComplex>) criteria.list();
        Integer i = 1;
        clientPreorders.clear();
        for (PreorderComplex preorderComplex : res3) {
            clientPreorders.add(new PreorderJournalReportItem(i++, preorderComplex, "<br/>"));
        }
    }

    public List<ApplicationForFoodReportItem> getApplicationsForFood() {
        return applicationsForFood;
    }

    public void setApplicationsForFood(List<ApplicationForFoodReportItem> applicationsForFood) {
        this.applicationsForFood = applicationsForFood;
    }

    public ApplicationForFoodReportItem getCurrentApplicationForFood() {
        return currentApplicationForFood;
    }

    public void setCurrentApplicationForFood(ApplicationForFoodReportItem currentApplicationForFood) {
        this.currentApplicationForFood = currentApplicationForFood;
    }

    public List<ApplicationForFoodHistoryReportItem> getHistoryItems() {
        List<ApplicationForFoodHistoryReportItem> result = new ArrayList<ApplicationForFoodHistoryReportItem>();
        if (currentApplicationForFood == null) return result;
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            List<ApplicationForFoodHistory> list = DAOUtils.getHistoryByApplicationForFood(session, currentApplicationForFood.getApplicationForFood());

            for (ApplicationForFoodHistory history : list) {
                result.add(new ApplicationForFoodHistoryReportItem(history));
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {

        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    public void getHistoryMobileChange(ActionEvent event) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(ClientsMobileHistory.class);
            Client client = (Client) session.load(Client.class, idOfClient);
            criteria.add(Restrictions.eq("client", client))
                    //.add(Restrictions.ge("createdate", startTime))
                    //.add(Restrictions.le("createdate", endTime))
                    .addOrder(Order.desc("createdate"));
            clientsMobileHistories = (List<ClientsMobileHistory>) criteria.list();
        } catch (Exception e) {

        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public List<GeoplanerNotificationJournal> getGeoplanerNotificationJournalList() {
        return geoplanerNotificationJournalList;
    }

    public void setGeoplanerNotificationJournalList(
            List<GeoplanerNotificationJournal> geoplanerNotificationJournalList) {
        this.geoplanerNotificationJournalList = geoplanerNotificationJournalList;
    }

    public List<BankSubscription> getBankSubscriptions() {
        return bankSubscriptions;
    }

    public void setBankSubscriptions(List<BankSubscription> bankSubscriptions) {
        this.bankSubscriptions = bankSubscriptions;
    }

    public List<ClientsMobileHistory> getClientsMobileHistories() {
        return clientsMobileHistories;
    }

    public void setClientsMobileHistories(List<ClientsMobileHistory> clientsMobileHistories) {
        this.clientsMobileHistories = clientsMobileHistories;
    }

    public List<PreorderJournalReportItem> getClientPreorders() {
        return clientPreorders;
    }

    public void setClientPreorders(List<PreorderJournalReportItem> clientPreorders) {
        this.clientPreorders = clientPreorders;
    }

    public void exportClientPreordersToXLS(ActionEvent actionEvent){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile(".jasper");
        if (StringUtils.isEmpty(templateFilename)) return ;
        Date generateTime = new Date();
        PreorderJournalReport.Builder builder = new PreorderJournalReport.Builder(templateFilename);
        Properties properties = new Properties();
        String idOfClients = idOfClient.toString();
        properties.setProperty(PreorderJournalReport.P_ID_OF_CLIENTS, idOfClients);
        properties.setProperty(PreorderJournalReport.P_LINE_SEPARATOR, "\n");
        builder.setReportProperties(properties);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            report =  builder.build(persistenceSession, startTime, endTime, null);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        if(report!=null){
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

                ServletOutputStream servletOutputStream = response.getOutputStream();

                facesContext.responseComplete();
                response.setContentType("application/xls");
                String filename = buildFileName(generateTime, report);
                response.setHeader("Content-disposition", String.format("inline;filename=%s.xls", filename));

                JRXlsExporter xlsExport = new JRXlsExporter();
                xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                xlsExport.exportReport();
                servletOutputStream.flush();
                servletOutputStream.close();
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            }
        }
    }
    private String checkIsExistFile(String suffix) {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName = PreorderJournalReport.class.getSimpleName() + suffix;
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if(!(new File(templateFilename)).exists()){
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return null;
        }
        return templateFilename;
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = basicReportJob.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s", "PreorderJournalReport", reportDistinctText, format);
    }
}