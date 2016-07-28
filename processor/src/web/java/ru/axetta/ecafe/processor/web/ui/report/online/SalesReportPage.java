/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.SalesReport;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.hibernate.Session;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class SalesReportPage extends OnlineReportPage {
    private SalesReport salesReport;

    public String getPageFilename() {
        return "report/online/sales_report";
    }

    public SalesReport getSalesReport() {
        return salesReport;
    }

    public void buildReport(Session session, FacesContext facesContext) throws Exception {
        this.salesReport = new SalesReport();
        if(CollectionUtils.isEmpty(idOfOrgList)){
            printError("Выберите список организаций");
            return;
        }
        SalesReport.Builder reportBuilder = new SalesReport.Builder();
        this.salesReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
    }
}