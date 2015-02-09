/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.report.ClientPaymentsReport;
import ru.axetta.ecafe.processor.web.ui.org.OrganizationTypeMenu;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 10.04.13
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
public class ClientPaymentsPage extends OnlineReportPage {
    private ClientPaymentsReport clientPaymentsReport;

    // тип организации "ПОТРЕБИТЕЛЬ / ПОСТАВЩИК"
    private OrganizationType organizationType;
    private final OrganizationTypeMenu organizationTypeMenu = new OrganizationTypeMenu();

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public OrganizationTypeMenu getOrganizationTypeMenu() {
        return organizationTypeMenu;
    }

    public String getPageFilename() {
        return "report/online/client_payments_report";
    }

    public ClientPaymentsReport getClientPaymentsReport() {
        return clientPaymentsReport;
    }

    public boolean validateFormData() {
/*        if(startDate==null){
            printError("Не указано дата выборки от");
            return true;
        }*/
        if(endDate==null){
            printError("Не указано дата выборки до");
            return true;
        }
        if(startDate!=null){
        if(startDate.after(endDate)){
            printError("Дата выборки от меньше дата выборки до");
            return true;
        }
        }
        return false;
    }

    public void buildReport(Session session) throws Exception {
        this.clientPaymentsReport = new ClientPaymentsReport();
        ClientPaymentsReport.Builder reportBuilder = new ClientPaymentsReport.Builder();

        List<Long> orgList = new ArrayList<Long>();

        if (this.organizationType != null) {

            List<Org> orgListBy = new ArrayList<Org>();

            for (Long id : idOfOrgList) {
                Org org = (Org) session.load(Org.class, id);
                orgListBy.add(org);
            }

            for (Org org : orgListBy) {
                if (org.getType().equals(this.organizationType)) {
                    orgList.add(org.getIdOfOrg());
                }
            }
        }
        if (!orgList.isEmpty()) {
            this.clientPaymentsReport = reportBuilder.build(session, startDate, endDate, orgList);
        } else {
            this.clientPaymentsReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
        }
    }
}
