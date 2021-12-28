/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import org.apache.poi.ss.usermodel.Workbook;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.TotalServicesReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.web.ui.report.excel.TotalServicesReportService;
import ru.axetta.ecafe.processor.web.ui.report.excel.WriteExcelHelper;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 29.10.12
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class TotalServicesReportPage extends OnlineReportPage {

    private TotalServicesReport totalReport;
    private static final Logger logger = LoggerFactory.getLogger(TotalServicesReportPage.class);
    private Boolean showBuildingDetails = true;
    private final TotalServicesReportService totalServicesReportService = new TotalServicesReportService();

    public void buildReportHTML() throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        //AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            this.totalReport = new TotalServicesReport();
            if (idOfOrg == null) throw new Exception("Необходимо выбрать организацию");

            TotalServicesReport.Builder reportBuilder = new TotalServicesReport.Builder(session, startDate, endDate,
                    idOfOrg, showBuildingDetails);

            this.totalReport = reportBuilder.build();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            printMessage("Свод по услугам построен");
        } catch (Exception e) {
            printError("Ошибка при построении отчета: " + e.getMessage());
            //logger.error("Failed building report ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public String buildReportCSV() {
        return "showTotalServicesCSVList";
    }

    public String getPageFilename() {
        return "report/online/total_srvc_report";
    }

    public TotalServicesReport getTotalReport() {
        return totalReport;
    }

    public Boolean getShowBuildingDetails() {
        return showBuildingDetails;
    }

    public void setShowBuildingDetails(Boolean showBuildingDetails) {
        this.showBuildingDetails = showBuildingDetails;
    }

    public void buildReportExcel() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                    .getResponse();
            try {
                Workbook wb = totalServicesReportService.buildReport(totalReport.getItems());
                WriteExcelHelper.saveExcelReport(wb, response);
            } catch (NullPointerException e) {
                printError("Нет данных для выгрузки отчета");
            }
        } catch (Exception e) {
            printError("Ошибка при выгрузке отчета: " + e.getMessage());
        }
    }
}