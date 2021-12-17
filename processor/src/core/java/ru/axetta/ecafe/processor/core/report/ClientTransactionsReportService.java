/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;


import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
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
            List<Long> idOfOrgList, List<Client> clientList, String operationTypeString) {

        List<ClientTransactionsReportItem> clientTransactionsReportItemList = new ArrayList<ClientTransactionsReportItem>();

        if (clientList.size() == 0) {
            List<AccountTransaction> accountTransactionList = getAccountTransactions(session, startTime, endTime, null,
                    idOfOrgList);

            for (AccountTransaction accountTransaction : accountTransactionList) {
                clientTransactionsReportItemList.addAll(getItemsByAccountTransactions(accountTransaction, session));
            }
        } else {
            for (Client client : clientList) {
                List<AccountTransaction> accountTransactionList = getAccountTransactions(session, startTime, endTime,
                        client, idOfOrgList);

                for (AccountTransaction accountTransaction : accountTransactionList) {
                    clientTransactionsReportItemList.addAll(getItemsByAccountTransactions(accountTransaction, session));
                }
            }
        }

        List<ClientTransactionsReportItem> clientTransactionsReportItems = new ArrayList<ClientTransactionsReportItem>();

        List<OrgNameAndAddress> orgNames = new ArrayList<OrgNameAndAddress>();
        List<OrgNameAndAddress> addresses = new ArrayList<OrgNameAndAddress>();

        if (idOfOrgList.size() == 1) {
            Org org = (Org) session.load(Org.class, idOfOrgList.get(0));
            OrgNameAndAddress orgNameAndAddress = new OrgNameAndAddress("Организация", org.getShortNameInfoService());
            OrgNameAndAddress orgNameAndAddress1 = new OrgNameAndAddress("Адрес", org.getAddress());
            orgNames.add(orgNameAndAddress);
            addresses.add(orgNameAndAddress1);
        } else {
            for (Long idOfOrg : idOfOrgList) {
                Org org = (Org) session.load(Org.class, idOfOrg);
                OrgNameAndAddress orgNameAndAddress = new OrgNameAndAddress("Организация", org.getShortNameInfoService());
                OrgNameAndAddress orgNameAndAddress1 = new OrgNameAndAddress("Адрес", org.getAddress());
                orgNames.add(orgNameAndAddress);
                addresses.add(orgNameAndAddress1);
            }
        }

        for (ClientTransactionsReportItem clientTransactionsReportItem : clientTransactionsReportItemList) {
            clientTransactionsReportItem.setOrgNames(orgNames);
            clientTransactionsReportItem.setAddresses(addresses);
        }

        if (!operationTypeString.equals("Все")) {
            for (ClientTransactionsReportItem clientTransactionsReportItem : clientTransactionsReportItemList) {
                if (clientTransactionsReportItem.getTransactionDescription().equals(operationTypeString)) {
                    clientTransactionsReportItems.add(clientTransactionsReportItem);
                } else if (clientTransactionsReportItem.getTransactionDescription().equals(operationTypeString)) {
                    clientTransactionsReportItems.add(clientTransactionsReportItem);
                }
            }
            clientTransactionsReportItemList = clientTransactionsReportItems;
        }

        return clientTransactionsReportItemList;
    }

    public List<ClientTransactionsReportItem> getItemsByAccountTransactions(AccountTransaction accountTransaction, Session session) {
        List<ClientTransactionsReportItem> clientTransactionsReportItemList = new ArrayList<ClientTransactionsReportItem>();

        if (accountTransaction.getSourceType() == 3) {
            ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

            clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());

            Set<ClientPayment> clientPayments = accountTransaction.getClientPayments();

            if (clientPayments.size() > 0) {
                for (ClientPayment clientPayment : clientPayments) {
                    clientTransactionsReportItem.setContragent(clientPayment.getContragent().getContragentName());
                }
            }

            clientTransactionsReportItem.setOperationType("Пополнение");
            clientTransactionsReportItem.setTransactionDescription("Пополнение");
            clientTransactionsReportItem.setIdOfTransaction(String.valueOf(accountTransaction.getIdOfTransaction()));
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

            Set<ClientPayment> clientPayments = accountTransaction.getClientPayments();

            if (clientPayments.size() > 0) {
                for (ClientPayment clientPayment : clientPayments) {
                    clientTransactionsReportItem.setContragent(clientPayment.getContragent().getContragentName());
                }
            }

            if (accountTransaction.getTransactionSum() > 0) {
                clientTransactionsReportItem.setOperationType("Пополнение");
                clientTransactionsReportItem.setTransactionDescription("Пополнение");
                clientTransactionsReportItem.setIdOfTransaction(String.valueOf(accountTransaction.getIdOfTransaction()));
            } else {
                clientTransactionsReportItem.setOperationType("Списание");
                clientTransactionsReportItem.setTransactionDescription("Списание");
                clientTransactionsReportItem.setIdOfTransaction(String.valueOf(accountTransaction.getIdOfTransaction()));
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
                        // пропускаем родителя составного комплекса с ценой 0р
                        if (orderDetail.getMenuType() >= OrderDetail.TYPE_COMPLEX_ITEM_MIN && orderDetail.getMenuType() <= OrderDetail.TYPE_COMPLEX_ITEM_MAX)
                            continue;

                        ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

                        clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());

                        clientTransactionsReportItem.setContragent(order.getContragent().getContragentName());

                        clientTransactionsReportItem.setOperationType(orderDetail.getMenuDetailName());
                        clientTransactionsReportItem.setTransactionDescription("Списание");
                        clientTransactionsReportItem
                                .setOrderNumber(String.valueOf(order.getCompositeIdOfOrder().getIdOfOrder()));
                        clientTransactionsReportItem
                                .setIdOfTransaction(String.valueOf(accountTransaction.getIdOfTransaction()));
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
            ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

            clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());

            Set<ClientPayment> clientPayments = accountTransaction.getClientPayments();

            if (clientPayments.size() > 0) {
                for (ClientPayment clientPayment : clientPayments) {
                    clientTransactionsReportItem.setContragent(clientPayment.getContragent().getContragentName());
                }
            } else {
                clientTransactionsReportItem.setContragent("");
            }

            clientTransactionsReportItem.setOperationType("Списание");
            clientTransactionsReportItem.setTransactionDescription("Списание");
            clientTransactionsReportItem.setIdOfTransaction(String.valueOf(accountTransaction.getIdOfTransaction()));
            clientTransactionsReportItem.setSumm(String.format("%d.%02d", accountTransaction.getTransactionSum() / 100,
                    Math.abs(accountTransaction.getTransactionSum() % 100)));
            clientTransactionsReportItem
                    .setTransactionTime(CalendarUtils.dateTimeToString(accountTransaction.getTransactionTime()));
            clientTransactionsReportItem.setPersonalAccount(accountTransaction.getClient().getContractId());

            clientTransactionsReportItemList.add(clientTransactionsReportItem);

        }

        if (accountTransaction.getSourceType() == 20) {
            ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

            clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());

            Set<ClientPayment> clientPayments = accountTransaction.getClientPayments();

            if (clientPayments.size() > 0) {
                for (ClientPayment clientPayment : clientPayments) {
                    clientTransactionsReportItem.setContragent(clientPayment.getContragent().getContragentName());
                }
            } else {
                clientTransactionsReportItem.setContragent("");
            }
            clientTransactionsReportItem.setOperationType("Списание");
            clientTransactionsReportItem.setTransactionDescription("Списание");
            clientTransactionsReportItem.setIdOfTransaction(String.valueOf(accountTransaction.getIdOfTransaction()));
            clientTransactionsReportItem.setSumm(String.format("%d.%02d", accountTransaction.getTransactionSum() / 100,
                    Math.abs(accountTransaction.getTransactionSum() % 100)));
            clientTransactionsReportItem
                    .setTransactionTime(CalendarUtils.dateTimeToString(accountTransaction.getTransactionTime()));
            clientTransactionsReportItem.setPersonalAccount(accountTransaction.getClient().getContractId());

            clientTransactionsReportItemList.add(clientTransactionsReportItem);
        }

        if (accountTransaction.getSourceType() == 30) {
            ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

            clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());

            Set<ClientPayment> clientPayments = accountTransaction.getClientPayments();

            if (clientPayments.size() > 0) {
                for (ClientPayment clientPayment : clientPayments) {
                    clientTransactionsReportItem.setContragent(clientPayment.getContragent().getContragentName());
                }
            } else {
                clientTransactionsReportItem.setContragent("");
            }

            clientTransactionsReportItem.setOperationType("Отмена");
            clientTransactionsReportItem.setTransactionDescription("Пополнение");
            clientTransactionsReportItem.setIdOfTransaction(String.valueOf(accountTransaction.getIdOfTransaction()));
            CanceledOrder canceledOrder = DAOUtils.getCancelOrderIdBySource(session, accountTransaction.getSource());
            clientTransactionsReportItem.setOrderNumber(canceledOrder == null ? "" : Long.toString(canceledOrder.getIdOfOrder()));
            clientTransactionsReportItem.setSumm(String.format("%d.%02d", accountTransaction.getTransactionSum() / 100,
                    Math.abs(accountTransaction.getTransactionSum() % 100)));
            clientTransactionsReportItem
                    .setTransactionTime(CalendarUtils.dateTimeToString(accountTransaction.getTransactionTime()));
            clientTransactionsReportItem.setPersonalAccount(accountTransaction.getClient().getContractId());

            clientTransactionsReportItemList.add(clientTransactionsReportItem);
        }

        if (accountTransaction.getSourceType() == 40) {
            ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

            clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());

            Set<ClientPayment> clientPayments = accountTransaction.getClientPayments();

            if (clientPayments.size() > 0) {
                for (ClientPayment clientPayment : clientPayments) {
                    clientTransactionsReportItem.setContragent(clientPayment.getContragent().getContragentName());
                }
            } else {
                clientTransactionsReportItem.setContragent("");
            }
            if (accountTransaction.getTransactionSum() > 0) {
                clientTransactionsReportItem.setOperationType("Пополнение");
                clientTransactionsReportItem.setTransactionDescription("Пополнение");
                clientTransactionsReportItem.setIdOfTransaction(String.valueOf(accountTransaction.getIdOfTransaction()));
            } else {
                clientTransactionsReportItem.setOperationType("Списание");
                clientTransactionsReportItem.setTransactionDescription("Списание");
                clientTransactionsReportItem.setIdOfTransaction(String.valueOf(accountTransaction.getIdOfTransaction()));
            }
            clientTransactionsReportItem.setIdOfTransaction(String.valueOf(accountTransaction.getIdOfTransaction()));
            clientTransactionsReportItem.setSumm(String.format("%d.%02d", accountTransaction.getTransactionSum() / 100,
                    Math.abs(accountTransaction.getTransactionSum() % 100)));
            clientTransactionsReportItem
                    .setTransactionTime(CalendarUtils.dateTimeToString(accountTransaction.getTransactionTime()));
            clientTransactionsReportItem.setPersonalAccount(accountTransaction.getClient().getContractId());

            clientTransactionsReportItemList.add(clientTransactionsReportItem);
        }

        if (accountTransaction.getSourceType() == 50) {
            ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

            clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());
            Set<ClientPayment> clientPayments = accountTransaction.getClientPayments();

            if (clientPayments.size() > 0) {
                for (ClientPayment clientPayment : clientPayments) {
                    clientTransactionsReportItem.setContragent(clientPayment.getContragent().getContragentName());
                }
            } else {
                clientTransactionsReportItem.setContragent("");
            }
            clientTransactionsReportItem.setOperationType("Списание");
            clientTransactionsReportItem.setTransactionDescription("Списание");
            clientTransactionsReportItem.setIdOfTransaction(String.valueOf(accountTransaction.getIdOfTransaction()));
            clientTransactionsReportItem.setSumm(String.format("%d.%02d", accountTransaction.getTransactionSum() / 100,
                    Math.abs(accountTransaction.getTransactionSum() % 100)));
            clientTransactionsReportItem
                    .setTransactionTime(CalendarUtils.dateTimeToString(accountTransaction.getTransactionTime()));
            clientTransactionsReportItem.setPersonalAccount(accountTransaction.getClient().getContractId());

            clientTransactionsReportItemList.add(clientTransactionsReportItem);
        }

        if (accountTransaction.getSourceType() == 60) {
            ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

            clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());

            Set<ClientPayment> clientPayments = accountTransaction.getClientPayments();

            if (clientPayments.size() > 0) {
                for (ClientPayment clientPayment : clientPayments) {
                    clientTransactionsReportItem.setContragent(clientPayment.getContragent().getContragentName());
                }
            } else {
                clientTransactionsReportItem.setContragent("");
            }
            clientTransactionsReportItem.setOperationType("Покупка карты " +
                    (accountTransaction.getTransactionSum().equals(-15000L)? "Mifare":"Mifare (Браслет)"));
            clientTransactionsReportItem.setTransactionDescription("Списание");
            clientTransactionsReportItem.setIdOfTransaction(String.valueOf(accountTransaction.getIdOfTransaction()));
            clientTransactionsReportItem.setSumm(String.format("%d.%02d", accountTransaction.getTransactionSum() / 100,
                    Math.abs(accountTransaction.getTransactionSum() % 100)));
            clientTransactionsReportItem
                    .setTransactionTime(CalendarUtils.dateTimeToString(accountTransaction.getTransactionTime()));
            clientTransactionsReportItem.setPersonalAccount(accountTransaction.getClient().getContractId());

            clientTransactionsReportItemList.add(clientTransactionsReportItem);
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
        if (!idOfOrgList.isEmpty()) {
            Criteria orgCriteria = criteria.createCriteria("org", "o");
            orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
        }
        criteria.add(Restrictions.ge("transactionTime", startTime));
        criteria.add(Restrictions.le("transactionTime", endTime));

        List<AccountTransaction> accountTransactionList = criteria.list();

        return accountTransactionList;
    }
}
