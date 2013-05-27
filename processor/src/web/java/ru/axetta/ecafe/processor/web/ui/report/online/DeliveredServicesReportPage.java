/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.DeliveredServicesReport;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contract.ContractFilter;
import ru.axetta.ecafe.processor.web.ui.contract.ContractSelectPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 25.04.13
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
public class DeliveredServicesReportPage extends OnlineReportPage
        implements ContragentSelectPage.CompleteHandler, ContractSelectPage.CompleteHandler {
    private DeliveredServicesReport deliveredServices;
    private String goodName;
    private Boolean hideMissedColumns;
    private String htmlReport;
    private final CCAccountFilter contragentFilter = new CCAccountFilter();
    private final ContractFilter contractFilter= new ContractFilter();

    public String getPageFilename() {
        return "report/online/delivered_services_report";
    }

    public DeliveredServicesReport getDeliveredServicesReport() {
        return deliveredServices;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public CCAccountFilter getContragentFilter() {
        return contragentFilter;
    }

    public ContractFilter getContractFilter() {
        return contractFilter;
    }

    public void showContractSelectPage () {
        MainPage.getSessionInstance().showContractSelectPage  (this.contragentFilter.getContragent().getContragentName());
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        contragentFilter.completeContragentSelection(session, idOfContragent);
    }

    public void completeContractSelection(Session session, Long idOfContract, int multiContrFlag, String classTypes) throws Exception {
        this.contractFilter.completeContractSelection(session, idOfContract, multiContrFlag, classTypes);
    }

    public void buildReport(Session session) throws Exception {
        DeliveredServicesReport.Builder reportBuilder = new DeliveredServicesReport.Builder();
        this.deliveredServices = reportBuilder.build(session, startDate, endDate, localCalendar,
                                                    contragentFilter.getContragent().getIdOfContragent(),
                                                    contractFilter.getContract().getIdOfContract());
        htmlReport = deliveredServices.getHtmlReport();
    }

}
