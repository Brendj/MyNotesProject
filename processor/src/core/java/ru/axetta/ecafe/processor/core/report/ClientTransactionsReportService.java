/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;


import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by anvarov on 16.06.2017.
 */
public class ClientTransactionsReportService {

    public List<ClientTransactionsReportItem> buildReportItems(Session session, Date startTime, Date endTime, List<Long> idOfOrgList) {

        List<ClientTransactionsReportItem> clientTransactionsReportItemList = new ArrayList<ClientTransactionsReportItem>();

        ClientTransactionsReportItem clientTransactionsReportItem = new ClientTransactionsReportItem();

        clientTransactionsReportItem.setContragent("Контрагент");
        clientTransactionsReportItem.setIdOfOrg(100L);
        clientTransactionsReportItem.setOperationType("Все");

        clientTransactionsReportItemList.add(clientTransactionsReportItem);

        return clientTransactionsReportItemList;
    }


}
