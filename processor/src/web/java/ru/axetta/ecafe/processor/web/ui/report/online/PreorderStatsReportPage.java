/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.preorder.PreorderStatsReport;
import ru.axetta.ecafe.processor.core.report.preorder.PreorderStatsReportBuilder;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Component
@Scope(value = "session")
public class PreorderStatsReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(PreorderStatsReportPage.class);
    private PreorderStatsReport preorderStatsReport;
    private boolean preorderOrgs = false;

    public PreorderStatsReportPage() {
        super();
        periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);
        startDate = CalendarUtils.startOfDay(new Date());
        onReportPeriodChanged();
    }

    @Override
    public String getPageFilename() {
        return "report/online/preorder_stats";
    }

    public Object showOrgListSelectPage() {
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    public void exportToHtml() {
        if (validateFormData()) {
            return;
        }
        preorderStatsReport =(PreorderStatsReport)makeReport();
        if (preorderStatsReport != null) htmlReport = preorderStatsReport.getHtmlReport();
    }

    private boolean validateFormData() {
        if (startDate == null) {
            printError("Не указана дата");
            return true;
        }
        if (CollectionUtils.isEmpty(idOfOrgList) && !preorderOrgs) {
            printError("Выберите одну или несколько организаций для построения отчета");
            return true;
        }
        return false;
    }

    private BasicReportJob makeReport() {
        Properties properties = new Properties();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName = PreorderStatsReport.class.getSimpleName() + ".jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        PreorderStatsReport.Builder builder = new PreorderStatsReportBuilder(templateFilename);
        if (preorderOrgs) {
            properties.setProperty(PreorderStatsReport.PREORDER_ORGS_PARAM, "1");
        } else if (!CollectionUtils.isEmpty(idOfOrgList)) {
            String idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
            properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            startDate = CalendarUtils.truncateToDayOfMonth(startDate);
            endDate = CalendarUtils.endOfDay(endDate); //localCalendar.getTime();
            builder.setReportProperties(properties);
            report = builder.build(persistenceSession, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return report;
    }

    public void exportToXLS(ActionEvent actionEvent) {
        if (validateFormData()) {
            return;
        }
        BasicReportJob report = makeReport();
        if (report != null) {
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

                ServletOutputStream servletOutputStream = response.getOutputStream();

                facesContext.responseComplete();
                response.setContentType("application/xls");
                String filename = buildFileName(new Date(), report);
                response.setHeader("Content-disposition", String.format("inline;filename=%s.xls", filename));

                JRXlsExporter xlsExport = new JRXlsExporter();
                xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                xlsExport.exportReport();
                servletOutputStream.flush();
                servletOutputStream.close();
                printMessage("Отчет построен");
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            }
        }
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = basicReportJob.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s", "PreorderStatsReport", reportDistinctText, format);
    }

    public void preorderOrgsChange() {
        if (preorderOrgs)
            filter = "Все организации с функционалом \"Предзаказ\"";
        else {
            filter = "Не выбрано";
            idOfOrgList.clear();
        }
    }

    public boolean isPreorderOrgs() {
        return preorderOrgs;
    }

    public void setPreorderOrgs(boolean preorderOrgs) {
        this.preorderOrgs = preorderOrgs;
    }
}
