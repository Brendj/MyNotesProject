/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientOperationListPage extends BasicWorkspacePage {

    private Long idOfClient;
    private Date startTime;
    private Date endTime;
    private final ClientPaymentList clientPaymentList = new ClientPaymentList();
    private final ClientOrderList clientOrderList = new ClientOrderList();
    private final ClientSmsList clientSmsList = new ClientSmsList();
    private List<AccountTransfer> accountTransferList = new LinkedList<AccountTransfer>(); 
    private List<AccountRefund> accountRefundList = new LinkedList<AccountRefund>();
    private List<AccountTransaction> accountTransactionList = new LinkedList<AccountTransaction>();

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
        this.endTime = endTime;
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

    public void fill(Session session, Long idOfClient) throws Exception {
        Client client = (Client) session.load(Client.class, idOfClient);
        this.idOfClient = client.getIdOfClient();
        this.clientPaymentList.fill(session, client, this.startTime, this.endTime);
        this.clientOrderList.fill(session, client, this.startTime, this.endTime);
        this.clientSmsList.fill(session, client, this.startTime, this.endTime);
        /////
        accountTransferList = DAOUtils.getAccountTransfersForClient(session,  client, startTime,  endTime);
        for (AccountTransfer at : accountTransferList) {
            // lazy load
            at.getClientBenefactor().getPerson().getFullName();
            at.getClientBeneficiary().getPerson().getFullName();
            at.getCreatedBy().getUserName();
        }
        /////
        accountRefundList = DAOUtils.getAccountRefundsForClient(session, client, startTime,  endTime);
        for (AccountRefund ar : accountRefundList) {
            // lazy load
            ar.getCreatedBy().getUserName();
        }
        /////
        Criteria criteria = session.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.ge("transactionTime", startTime));
        criteria.add(Restrictions.le("transactionTime", endTime));
        criteria.add(Restrictions.eq("client", client));
        this.accountTransactionList = new LinkedList<AccountTransaction>();
        for (Object o : criteria.list()) {
            AccountTransaction accTrans = (AccountTransaction)o;
            if (accTrans.getCard()!=null) accTrans.getCard().getCardNo(); // lazy load
            accountTransactionList.add(accTrans);
        }

    }

}