/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;


import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.persistence.Client;
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

        for (Client client : clientList) {
            Criteria criteria = session.createCriteria(AccountTransaction.class);
            Criteria clientCriteria = criteria.createCriteria("client", "cl");
            Criteria orgCriteria = criteria.createCriteria("org", "o");

            criteria.add(Restrictions.ge("transactionTime", startTime));
            criteria.add(Restrictions.le("transactionTime", endTime));
            clientCriteria.add(Restrictions.eq("contractId", client.getContractId()));
            orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
            // orgCriteria.addOrder(Order.asc("idOfOrg"));

            List<AccountTransaction> accountTransactionList = criteria.list();

            for (AccountTransaction accountTransaction : accountTransactionList) {

                ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();
                clientTransactionsReportItem.setIdOfOrg(accountTransaction.getOrg().getIdOfOrg());
                clientTransactionsReportItem.setContragent("");

                clientTransactionsReportItem.setTransactionDescription("");

                Set<ru.axetta.ecafe.processor.core.persistence.Order> orderSet = accountTransaction.getOrders();
                String orders = "";
                int count = 0;
                for (ru.axetta.ecafe.processor.core.persistence.Order order : orderSet) {
                    orders = orders + order.getCompositeIdOfOrder().getIdOfOrder();
                    if (count < orderSet.size() - 1) {
                        orders = orders + "; ";
                    }
                    count++;
                }

                clientTransactionsReportItem.setOrderNumber(orders);

                if (accountTransaction.getSourceType() == 3) {

                }

                if (accountTransaction.getSourceType() == 4) {
                    if (accountTransaction.getTransactionSum() > 0) {
                        clientTransactionsReportItem.setOperationType("Пополнение");
                        clientTransactionsReportItem.setTransactionDescription("Пополнение");
                        clientTransactionsReportItem.setOrderNumber(
                                String.valueOf(accountTransaction.getIdOfTransaction()));
                    } else {
                        clientTransactionsReportItem.setOperationType("Списание");
                        clientTransactionsReportItem.setTransactionDescription("Списание");
                        clientTransactionsReportItem.setOrderNumber(
                                String.valueOf(accountTransaction.getIdOfTransaction()));
                    }
                }

                clientTransactionsReportItem.setSumm(
                        String.format("%d.%02d", accountTransaction.getTransactionSum() / 100,
                                Math.abs(accountTransaction.getTransactionSum() % 100)));

                clientTransactionsReportItem
                        .setTransactionTime(CalendarUtils.dateTimeToString(accountTransaction.getTransactionTime()));




                clientTransactionsReportItem.setPersonalAccount(accountTransaction.getClient().getContractId());

                clientTransactionsReportItemList.add(clientTransactionsReportItem);
            }
        }

        return clientTransactionsReportItemList;
    }


}
