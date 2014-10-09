package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.DetailedDeviationsPaymentOrReducedPriceMealsBuilder;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.DetailedDeviationsPaymentOrReducedPriceMealsJasperReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 02.10.14
 * Time: 12:56
 */

public class DetailedDeviationsPaymentOrReducedPriceMealsReportPage extends OnlineReportPage {

    private String htmlReport;

    @Override
    public String getPageFilename() {
        return "report/online/detailed_deviation_report";
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    private DetailedDeviationsPaymentOrReducedPriceMealsJasperReport buildReport() {
        if (idOfOrgList.isEmpty()) {
            printError("Не указана организация");
            return null;
        }
        BasicReportJob report = null;
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath()
                + "DetailedDeviationsPaymentOrReducedPriceMealsJasperReport.jasper";
        DetailedDeviationsPaymentOrReducedPriceMealsBuilder builder = new DetailedDeviationsPaymentOrReducedPriceMealsBuilder(
                templateFilename);
        String idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
        builder.getReportProperties().setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            report = builder.build(session, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            getLogger().error("Filed build DetailedDeviationsPaymentOrReducedPriceMealsJasperReport", e);
            printError("Ошибка при построении отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }
        return (DetailedDeviationsPaymentOrReducedPriceMealsJasperReport) report;
    }

    public Object buildReportHTML() {
        try {
            BasicReportJob report = buildReport();
            if (report != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, report.getPrint());
                exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
                htmlReport = os.toString("UTF-8");
                os.close();
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при построении отчета:", e);
        }
        return null;
    }

    public void generateXLS(ActionEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            BasicReportJob report = buildReport();
            if (report != null) {
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                ServletOutputStream servletOutputStream = response.getOutputStream();
                facesContext.responseComplete();
                response.setContentType("application/xls");
                response.setHeader("Content-disposition", "inline;filename=discrepancies.xls");
                JRXlsExporter xlsExport = new JRXlsExporter();
                xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                xlsExport.exportReport();
                servletOutputStream.close();
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при выгрузке отчета:", e);
        }
    }

    public void fill() throws Exception {
    }
}
