/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.ClientReport;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 20.10.11
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public class ClientReportPage extends OnlineReportPage  implements ContragentSelectPage.CompleteHandler{
    private ClientReport clientReport;

    public String getPageFilename() {
        return "report/online/client_report";
    }
    
    private Contragent contragent;

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    public ClientReport getClientReport() {
        return clientReport;
    }

    public void buildReport(Session session) throws Exception {
        this.clientReport = new ClientReport();
        ClientReport.Builder reportBuilder = new ClientReport.Builder();
        this.clientReport = reportBuilder.build(contragent, session);
    }

    @Override
    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes)
            throws Exception {
        if (null != idOfContragent) {
            this.contragent = (Contragent) session.get(Contragent.class, idOfContragent);
        }
    }
}
