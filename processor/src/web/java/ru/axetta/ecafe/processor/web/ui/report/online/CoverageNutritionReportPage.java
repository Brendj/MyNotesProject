/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.kzn.CoverageNutritionReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Component
@Scope("session")
public class CoverageNutritionReportPage extends OnlineReportWithContragentPage {

    Logger logger = LoggerFactory.getLogger(CoverageNutritionReportPage.class);

    private Boolean showYoungerClasses = false;  // 1-4
    private Boolean showMiddleClasses = false;   // 5-9
    private Boolean showOlderClasses = false;    // 10-11
    private Boolean showEmployee = false;

    private Boolean showFreeNutrition = false;
    private Boolean showPaidNutrition = false;
    private Boolean showBuffet = false;

    private Boolean showComplexesByOrgCard = false;

    private Boolean showTotal = true;

    public CoverageNutritionReportPage() {
        onReportPeriodChanged();
    }

    public Object buildReportHTML() {
        htmlReport = null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile(".jasper");
        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        if (idOfContragentOrgList.isEmpty() && idOfOrgList.isEmpty()) {
            printError("Выберите организацию");
            return null;
        }
        if (!checkFoodTypeAndGroups()) {
            printError("Выберите хотя бы одну группу и один тип питания");
            return null;
        }
        CoverageNutritionReport.Builder builder = new CoverageNutritionReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
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
        if (report != null) {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, report.getPrint());
                exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
                htmlReport = os.toString("UTF-8");
                os.close();
            } catch (Exception e) {
                printError("Ошибка при построении отчета: " + e.getMessage());
                logger.error("Failed build report ", e);
            }
        }
        return null;
    }

    public void exportToXLS(ActionEvent actionEvent) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile(".jasper");
        if (StringUtils.isEmpty(templateFilename)) {
            return;
        }
        if (idOfContragentOrgList.isEmpty() && idOfOrgList.isEmpty()) {
            printError("Выберите организацию");
            return;
        }
        if (!checkFoodTypeAndGroups()) {
            printError("Выберите хотя бы одну группу и один тип питания");
            return;
        }
        Date generateTime = new Date();
        CoverageNutritionReport.Builder builder = new CoverageNutritionReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
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

        if (report != null) {
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

                ServletOutputStream servletOutputStream = response.getOutputStream();

                facesContext.responseComplete();
                response.setContentType("application/xls");
                String filename = buildFileName(generateTime, report);
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
        return String.format("%s-%s-%s", CoverageNutritionReport.class.getSimpleName(), reportDistinctText, format);
    }

    private String checkIsExistFile(String suffix) {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName =
                CoverageNutritionReport.class.getSimpleName() + ((showTotal) ? "Total" : "") + ((showEmployee)
                        ? "Employees" : "") + suffix;
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return null;
        }
        return templateFilename;
    }

    public Boolean enableFoodTypeCheckBoxes() {
        return showYoungerClasses || showMiddleClasses || showOlderClasses || showEmployee;
    }

    public void onClassesChecked() {
        if (!enableFoodTypeCheckBoxes()) {
            showFreeNutrition = false;
            showPaidNutrition = false;
            showBuffet = false;
        }
    }

    private Boolean checkFoodTypeAndGroups() {
        return (showYoungerClasses || showMiddleClasses || showOlderClasses || showEmployee) && (showFreeNutrition
                || showPaidNutrition || showBuffet);
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        properties.setProperty(CoverageNutritionReport.P_SHOW_YOUNGER_CLASSES, showYoungerClasses.toString());
        properties.setProperty(CoverageNutritionReport.P_SHOW_MIDDLE_CLASSES, showMiddleClasses.toString());
        properties.setProperty(CoverageNutritionReport.P_SHOW_OLDER_CLASSES, showOlderClasses.toString());
        properties.setProperty(CoverageNutritionReport.P_SHOW_EMPLOYEE_CLASSES, showEmployee.toString());

        properties.setProperty(CoverageNutritionReport.P_SHOW_FREE_NUTRITION, showFreeNutrition.toString());
        properties.setProperty(CoverageNutritionReport.P_SHOW_PAID_NUTRITION, showPaidNutrition.toString());
        properties.setProperty(CoverageNutritionReport.P_SHOW_BUFFET, showBuffet.toString());

        properties.setProperty(CoverageNutritionReport.P_SHOW_COMPLEXES_BY_ORG_CARD, showComplexesByOrgCard.toString());

        properties.setProperty(CoverageNutritionReport.P_SHOW_TOTAL, showTotal.toString());

        properties.setProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG,
                StringUtils.join(idOfContragentOrgList, ','));
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, StringUtils.join(idOfOrgList, ','));
        return properties;
    }

    public Boolean getShowYoungerClasses() {
        return showYoungerClasses;
    }

    public void setShowYoungerClasses(Boolean showYoungerClasses) {
        this.showYoungerClasses = showYoungerClasses;
    }

    public Boolean getShowMiddleClasses() {
        return showMiddleClasses;
    }

    public void setShowMiddleClasses(Boolean showMiddleClasses) {
        this.showMiddleClasses = showMiddleClasses;
    }

    public Boolean getShowOlderClasses() {
        return showOlderClasses;
    }

    public void setShowOlderClasses(Boolean showOlderClasses) {
        this.showOlderClasses = showOlderClasses;
    }

    public Boolean getShowEmployee() {
        return showEmployee;
    }

    public void setShowEmployee(Boolean showEmployee) {
        this.showEmployee = showEmployee;
    }

    public Boolean getShowFreeNutrition() {
        return showFreeNutrition;
    }

    public void setShowFreeNutrition(Boolean showFreeNutrition) {
        this.showFreeNutrition = showFreeNutrition;
    }

    public Boolean getShowPaidNutrition() {
        return showPaidNutrition;
    }

    public void setShowPaidNutrition(Boolean showPaidNutrition) {
        this.showPaidNutrition = showPaidNutrition;
    }

    public Boolean getShowBuffet() {
        return showBuffet;
    }

    public void setShowBuffet(Boolean showBuffet) {
        this.showBuffet = showBuffet;
    }

    public Boolean getShowComplexesByOrgCard() {
        return showComplexesByOrgCard;
    }

    public void setShowComplexesByOrgCard(Boolean showComplexesByOrgCard) {
        this.showComplexesByOrgCard = showComplexesByOrgCard;
    }

    public Boolean getShowTotal() {
        return showTotal;
    }

    public void setShowTotal(Boolean showTotal) {
        this.showTotal = showTotal;
    }

    @Override
    public void onShow() throws Exception {

    }

    @Override
    public String getPageFilename() {
        return "report/online/coverage_nutrition_report";
    }
}
