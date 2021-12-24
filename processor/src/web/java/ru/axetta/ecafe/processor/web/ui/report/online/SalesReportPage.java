/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import org.apache.poi.ss.usermodel.Workbook;
import ru.axetta.ecafe.processor.core.report.SalesReport;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.hibernate.Session;
import ru.axetta.ecafe.processor.web.ui.report.excel.SalesReportService;
import ru.axetta.ecafe.processor.web.ui.report.excel.WriteExcelHelper;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

public class SalesReportPage extends OnlineReportPage {

    private SalesReport salesReport;
    private final SalesReportService salesReportService = new SalesReportService();

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

    public void buildExcelReport(FacesContext facesContext) throws Exception {
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                .getResponse();
        try {
            Workbook wb = salesReportService.buildReport(salesReport.getSalesItems());
            WriteExcelHelper.saveExcelReport(wb, response);
        }catch (NullPointerException e) {
            printError("Нет данных для выгрузки отчета");
        }
    }
}