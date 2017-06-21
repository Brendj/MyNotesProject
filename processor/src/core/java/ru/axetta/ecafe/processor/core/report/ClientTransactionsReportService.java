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

    public List<ClientTransactionsReportItem> buildReportItems(Session session, Date startTime, Date endTime, Long idOfOrg) {

        List<ClientTransactionsReportItem> clientTransactionsReportItemList = new ArrayList<ClientTransactionsReportItem>();

        return clientTransactionsReportItemList;
    }
}
