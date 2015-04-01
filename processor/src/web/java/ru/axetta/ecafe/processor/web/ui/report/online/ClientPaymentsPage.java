/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.OrganizationTypeModify;
import ru.axetta.ecafe.processor.core.report.ClientPaymentsReport;
import ru.axetta.ecafe.processor.web.ui.org.OrganizationTypeModifyMenu;

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

    private OrganizationType organizationType = null;

    // тип организации
    private OrganizationTypeModify organizationTypeModify;

    private final OrganizationTypeModifyMenu organizationTypeModifyMenu = new OrganizationTypeModifyMenu();

    public OrganizationTypeModify getOrganizationTypeModify() {
        return organizationTypeModify;
    }

    public void setOrganizationTypeModify(OrganizationTypeModify organizationTypeModify) {
        this.organizationTypeModify = organizationTypeModify;
    }

    public OrganizationTypeModifyMenu getOrganizationTypeModifyMenu() {
        return organizationTypeModifyMenu;
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
        if (endDate == null) {
            printError("Не указано дата выборки до");
            return true;
        }
        if (startDate != null) {
            if (startDate.after(endDate)) {
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

        if (this.idOfOrgList.isEmpty()) {
            printError("Не выбрана организация");
        }
        OrganizationType[] organizationTypes = OrganizationType.values();

        for (OrganizationType orgType : organizationTypes) {
            if (orgType.name().equals(this.organizationTypeModify.name())) {
                this.organizationType = orgType;
                break;
            } else {
                this.organizationType = null;
            }
        }

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
            this.clientPaymentsReport = reportBuilder.build(session, startDate, endDate, orgList);
        } else {
            this.clientPaymentsReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
        }
    }

    public void buildClientPaymentsReportExcel(Session session) throws Exception {

    }
}
