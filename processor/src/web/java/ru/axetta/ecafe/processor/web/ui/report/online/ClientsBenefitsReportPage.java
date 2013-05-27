/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.ClientsBenefitsReport;
import ru.axetta.ecafe.processor.core.report.DeliveredServicesReport;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contract.ContractFilter;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 24.05.13
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
public class ClientsBenefitsReportPage extends OnlineReportPage implements OrgSelectPage.CompleteHandler {
    private ClientsBenefitsReport clientsBenefitsReport;
    private boolean hideMissedColumns = true;


    public String getPageFilename() {
        return "report/online/clients_benefits_report";
    }

    public ClientsBenefitsReport getClientsBenefitsReport() {
        return clientsBenefitsReport;
    }

    public boolean isHideMissedColumns() {
        return hideMissedColumns;
    }

    public void setHideMissedColumns(boolean hideMissedColumns) {
        this.hideMissedColumns = hideMissedColumns;
    }

    public void buildReport(Session session) throws Exception {
        ClientsBenefitsReport.Builder reportBuilder = new ClientsBenefitsReport.Builder();
        this.clientsBenefitsReport = reportBuilder.build(session, startDate, endDate, idOfOrg, hideMissedColumns);
    }

}
