/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;


import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by anvarov on 16.06.2017.
 */
public class ClientTransactionsReportService {

    public List<ClientTransactionsReportItem> buildReportItems(Session session, Date startTime, Date endTime,
            List<Long> idOfOrgList, List<Client> clientList) {

        List<ClientTransactionsReportItem> clientTransactionsReportItemList = new ArrayList<ClientTransactionsReportItem>();

        if (clientList.size() == 0) {
            List<AccountTransaction> accountTransactionList = getAccountTransactions(session, startTime, endTime, null,
                    idOfOrgList);

            for (AccountTransaction accountTransaction : accountTransactionList) {
                clientTransactionsReportItemList.addAll(getItemsByAccountTransactions(accountTransaction));
            }
        } else {
            for (Client client : clientList) {
                List<AccountTransaction> accountTransactionList = getAccountTransactions(session, startTime, endTime,
                        client, idOfOrgList);

                for (AccountTransaction accountTransaction : accountTransactionList) {
                    clientTransactionsReportItemList.addAll(getItemsByAccountTransactions(accountTransaction));
                }
            }
        }

        return clientTransactionsReportItemList;
    }

    public List<ClientTransactionsReportItem> getItemsByAccountTransactions(AccountTransaction accountTransaction) {
        List<ClientTransactionsReportItem> clientTransactionsReportItemList = new ArrayList<ClientTransactionsReportItem>();

        if (accountTransaction.getSourceType() == 3) {
            ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

            clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());
            clientTransactionsReportItem.setContragent("");
            clientTransactionsReportItem.setOperationType("Пополнение");
            clientTransactionsReportItem.setTransactionDescription("Пополнение");
            clientTransactionsReportItem.setOrderNumber(String.valueOf(accountTransaction.getIdOfTransaction()));
            clientTransactionsReportItem.setSumm(String.format("%d.%02d", accountTransaction.getTransactionSum() / 100,
                    Math.abs(accountTransaction.getTransactionSum() % 100)));
            clientTransactionsReportItem
                    .setTransactionTime(CalendarUtils.dateTimeToString(accountTransaction.getTransactionTime()));
            clientTransactionsReportItem.setPersonalAccount(accountTransaction.getClient().getContractId());

            clientTransactionsReportItemList.add(clientTransactionsReportItem);
        }

        if (accountTransaction.getSourceType() == 4) {
            ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

            clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());
            clientTransactionsReportItem.setContragent("");
            if (accountTransaction.getTransactionSum() > 0) {
                clientTransactionsReportItem.setOperationType("Пополнение");
                clientTransactionsReportItem.setTransactionDescription("Пополнение");
                clientTransactionsReportItem.setOrderNumber(String.valueOf(accountTransaction.getIdOfTransaction()));
            } else {
                clientTransactionsReportItem.setOperationType("Списание");
                clientTransactionsReportItem.setTransactionDescription("Списание");
                clientTransactionsReportItem.setOrderNumber(String.valueOf(accountTransaction.getIdOfTransaction()));
            }
            clientTransactionsReportItem.setSumm(String.format("%d.%02d", accountTransaction.getTransactionSum() / 100,
                    Math.abs(accountTransaction.getTransactionSum() % 100)));
            clientTransactionsReportItem
                    .setTransactionTime(CalendarUtils.dateTimeToString(accountTransaction.getTransactionTime()));
            clientTransactionsReportItem.setPersonalAccount(accountTransaction.getClient().getContractId());

            clientTransactionsReportItemList.add(clientTransactionsReportItem);
        }

        if (accountTransaction.getSourceType() == 8) {

            Set<Order> oSet = accountTransaction.getOrders();

            if (oSet.size() > 0) {
                for (Order order : oSet) {
                    Set<OrderDetail> orderDetails = order.getOrderDetails();
                    for (OrderDetail orderDetail : orderDetails) {

                        ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

                        clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());
                        clientTransactionsReportItem.setContragent("");
                        clientTransactionsReportItem.setOperationType(orderDetail.getMenuDetailName());
                        clientTransactionsReportItem.setTransactionDescription("Списание");
                        clientTransactionsReportItem
                                .setOrderNumber(String.valueOf(order.getCompositeIdOfOrder().getIdOfOrder()));
                        clientTransactionsReportItem.setSumm(String.format("%d.%02d",
                                ((orderDetail.getRPrice() - orderDetail.getDiscount()) * orderDetail.getQty()) / 100,
                                Math.abs(((orderDetail.getRPrice() - orderDetail.getDiscount()) * orderDetail.getQty())
                                        % 100)));
                        clientTransactionsReportItem.setTransactionTime(
                                CalendarUtils.dateTimeToString(accountTransaction.getTransactionTime()));
                        clientTransactionsReportItem.setPersonalAccount(accountTransaction.getClient().getContractId());

                        clientTransactionsReportItemList.add(clientTransactionsReportItem);
                    }
                }
            }
        }

        if (accountTransaction.getSourceType() == 10) {

        }

        if (accountTransaction.getSourceType() == 20) {

        }

        if (accountTransaction.getSourceType() == 30) {
            ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

            clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());
            clientTransactionsReportItem.setContragent("");
            clientTransactionsReportItem.setOperationType("Отмена");
            clientTransactionsReportItem.setTransactionDescription("Пополнение");
            clientTransactionsReportItem.setOrderNumber(String.valueOf(accountTransaction.getIdOfTransaction()));
            clientTransactionsReportItem.setSumm(String.format("%d.%02d", accountTransaction.getTransactionSum() / 100,
                    Math.abs(accountTransaction.getTransactionSum() % 100)));
            clientTransactionsReportItem
                    .setTransactionTime(CalendarUtils.dateTimeToString(accountTransaction.getTransactionTime()));
            clientTransactionsReportItem.setPersonalAccount(accountTransaction.getClient().getContractId());

            clientTransactionsReportItemList.add(clientTransactionsReportItem);
        }

        if (accountTransaction.getSourceType() == 40) {

        }

        if (accountTransaction.getSourceType() == 50) {

        }

        return clientTransactionsReportItemList;
    }

    public List<AccountTransaction> getAccountTransactions(Session session, Date startTime, Date endTime, Client client,
            List<Long> idOfOrgList) {
        Criteria criteria = session.createCriteria(AccountTransaction.class);
        if (client != null) {
            Criteria clientCriteria = criteria.createCriteria("client", "cl");
            clientCriteria.add(Restrictions.eq("contractId", client.getContractId()));
        }
        Criteria orgCriteria = criteria.createCriteria("org", "o");
        criteria.add(Restrictions.ge("transactionTime", startTime));
        criteria.add(Restrictions.le("transactionTime", endTime));
        orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
        // orgCriteria.addOrder(Order.asc("idOfOrg"));

        List<AccountTransaction> accountTransactionList = criteria.list();

        return accountTransactionList;
    }


}
