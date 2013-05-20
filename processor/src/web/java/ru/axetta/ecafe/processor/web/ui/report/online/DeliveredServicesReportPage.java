/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.DeliveredServicesReport;
import ru.axetta.ecafe.processor.core.report.GoodRequestsReport;
import ru.axetta.ecafe.processor.web.ui.MainPage;

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
public class DeliveredServicesReportPage extends OnlineReportPage {
    private DeliveredServicesReport deliveredServices;
    private String goodName;
    private Boolean hideMissedColumns;
    private String htmlReport;

    public String getPageFilename() {
        return "report/online/delivered_services_report";
    }

    public DeliveredServicesReport getDeliveredServicesReport() {
        return deliveredServices;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public void buildReport(Session session) throws Exception {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        DeliveredServicesReport.Builder reportBuilder = new DeliveredServicesReport.Builder();
        this.deliveredServices = reportBuilder.build(session, startDate, endDate, cal);
        htmlReport = deliveredServices.getHtmlReport();
    }

}
