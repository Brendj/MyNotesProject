package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.msc.DiscrepanciesDataOnOrdersAndPaymentJasperReport;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.payment.orders.DiscrepanciesDataOnOrdersAndPaymentBuilder;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * User: r.kalimullin
 * Date: 03.02.14
 * Time: 17:19
 */

@Component
@Scope(value = "session")
public class DiscrepanciesDataOnOrdersAndPaymentReportPage extends OnlineReportPage {

    private String sourceMenuOrgFilter = "Не выбрано";
    private String orgFilter = "Не выбрано";
    private String htmlReport;

    @Override
    public String getPageFilename() {
        return "report/online/discrepancies_data";
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public String getSourceMenuOrgFilter() {
        return sourceMenuOrgFilter;
    }

    public String getOrgFilter() {
        return orgFilter;
    }

    public Object showOrgListSelectPage() {
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    public Object showOrgSelectPage() {
        MainPage.getSessionInstance().showOrgSelectPage();
        return null;
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        super.completeOrgSelection(session, idOfOrg);
        sourceMenuOrgFilter = filter;
    }

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws HibernateException {
        super.completeOrgListSelection(orgMap);
        orgFilter = filter;
    }

    private DiscrepanciesDataOnOrdersAndPaymentJasperReport buildReport() {
        BasicReportJob report = null;
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
            String templateFilename = autoReportGenerator.getReportsTemplateFilePath()
                    + "DiscrepanciesDataOnOrdersAndPaymentJasperReport.jasper";
            DiscrepanciesDataOnOrdersAndPaymentBuilder builder = new DiscrepanciesDataOnOrdersAndPaymentBuilder(
                    templateFilename);
            builder.setReportProperties(new Properties());
            builder.getReportProperties().setProperty("idOfMenuSourceOrg", idOfOrg == null ? null : idOfOrg.toString());
            String idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
            builder.getReportProperties().setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
            report = builder.build(session, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
        } catch (Exception e) {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            logAndPrintMessage("Ошибка при построении отчета:", e);
        } finally {
            HibernateUtils.close(session, getLogger());
        }
        return (DiscrepanciesDataOnOrdersAndPaymentJasperReport) report;
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
}
