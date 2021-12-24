/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import org.apache.poi.ss.usermodel.Workbook;
import ru.axetta.ecafe.processor.core.report.AllComplexReport;
import ru.axetta.ecafe.processor.core.report.FreeComplexReport;

import org.hibernate.Session;
import ru.axetta.ecafe.processor.web.ui.report.excel.AllComplexReportService;
import ru.axetta.ecafe.processor.web.ui.report.excel.WriteExcelHelper;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

public class FreeComplexReportPage extends OnlineReportPage {
    private AllComplexReport complexReport;
    private final AllComplexReportService allComplexReportService =
            new AllComplexReportService("free_complexes.xlsx", "Бесплатные комплексы");

    public String getPageFilename() {
        return "report/online/free_complex_report";
    }

    public AllComplexReport getComplexReport() {
        return complexReport;
    }

    public void buildReport(Session session) throws Exception {
        this.complexReport = new FreeComplexReport();
        FreeComplexReportBuilder reportBuilder = new FreeComplexReportBuilder();
        this.complexReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
    }

    public void buildReportExcel(FacesContext facesContext) throws Exception {
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                .getResponse();
        try {
            Workbook wb = allComplexReportService.buildReport(complexReport.getComplexItems());
            WriteExcelHelper.saveExcelReport(wb, response);
        } catch (NullPointerException e) {
            printError("Нет данных для выгрузки отчета");
        }
    }
}
