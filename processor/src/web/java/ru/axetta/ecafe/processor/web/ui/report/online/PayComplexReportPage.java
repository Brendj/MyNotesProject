/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import org.apache.poi.ss.usermodel.Workbook;
import ru.axetta.ecafe.processor.core.report.PayComplexReport;

import org.hibernate.Session;
import ru.axetta.ecafe.processor.web.ui.report.excel.PayComplexReportService;
import ru.axetta.ecafe.processor.web.ui.report.excel.WriteExcelHelper;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

public class PayComplexReportPage extends OnlineReportPage {
    private PayComplexReport complexReport;
    private final PayComplexReportService payComplexReportService = new PayComplexReportService();

    public String getPageFilename() {
        return "report/online/pay_complex_report";
    }

    public PayComplexReport getComplexReport() {
        return complexReport;
    }

    public void buildReport(Session session) throws Exception {
        this.complexReport = new PayComplexReport();
        PayComplexReport.Builder reportBuilder = new PayComplexReport.Builder();
        this.complexReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
    }

    public void buildReportExcel(FacesContext facesContext) throws Exception {
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                .getResponse();
        try {
            Workbook wb = payComplexReportService.buildReport(complexReport.getComplexItems());
            WriteExcelHelper.saveExcelReport(wb, response);
        } catch (NullPointerException e) {
            printError("Нет данных для выгрузки отчета");
        }
    }
}
