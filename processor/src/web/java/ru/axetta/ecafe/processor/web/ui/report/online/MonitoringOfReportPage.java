/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.MonitoringOfReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by almaz anvarov on 03.05.2017.
 */
public class MonitoringOfReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(MonitoringOfReportPage.class);
    private final String reportName = MonitoringOfReport.REPORT_NAME;
    private final String reportNameForMenu = MonitoringOfReport.REPORT_NAME_FOR_MENU;

    private Date startDate;
    private Calendar localCalendar;
    private String htmlReport = null;
    private Integer selectedPeriod = MonitoringOfReport.FOR_ONE_DAY;

    public MonitoringOfReportPage() {
        super();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));
        localCalendar.setTime(new Date());
        this.startDate = DateUtils.truncate(localCalendar, Calendar.DAY_OF_MONTH).getTime();
        localCalendar.setTime(this.startDate);

        localCalendar.add(Calendar.DAY_OF_MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
    }

    public void showOrgListSelectPage() {
        MainPage.getSessionInstance().showOrgListSelectPage();
    }

    public Object buildReportHTML() {
        htmlReport = null;
        if (validateFormData()){
            return null;
        }

        BasicReportJob report = buildBasicReportJob();
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
                printError("Ошибка при построении отчета: "+e.getMessage());
                logger.error("Failed build report ",e);
            }
        }
        return null;
    }

    public void exportToXLS(ActionEvent actionEvent) {
        if (validateFormData()){
            return;
        }

        BasicReportJob report = buildBasicReportJob();
        Date generateTime = new Date();
        if(report != null){
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

    private BasicReportJob buildBasicReportJob(){
        String templateFilename = checkIsExistFile(selectedPeriod);
        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        if(selectedPeriod.equals(MonitoringOfReport.FOR_ONE_DAY)){
            startDate = CalendarUtils.startOfDay(startDate);
            endDate = CalendarUtils.endOfDay(startDate);
        } else if(selectedPeriod.equals(MonitoringOfReport.FOR_MONTH)){
            startDate = CalendarUtils.getFirstDayOfMonth(startDate);
            endDate = CalendarUtils.getLastDayOfMonth(startDate);
        }
        MonitoringOfReport.Builder builder = new MonitoringOfReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        builder.setSelectedPeriod(selectedPeriod);
        BasicReportJob report = null;
        try {
            report = builder.buildInternal(startDate, endDate, localCalendar);
        }catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
            return null;
        }
        return report;
    }

    private boolean validateFormData() {
        if (CollectionUtils.isEmpty(idOfOrgList)) {
            printError("Выберите список организаций");
            return true;
        }
        Date currentDate = new Date();
        if(startDate.after(currentDate)){
            printError("Выбрана неверная дата для формирования отчета");
            return true;
        }
        return false;
    }

    public List<SelectItem> getPeriods() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(0, new SelectItem(MonitoringOfReport.FOR_ONE_DAY, "За день"));
        items.add(1, new SelectItem(MonitoringOfReport.FOR_MONTH, "За месяц"));
        return items;
    }

    private String checkIsExistFile(Integer period) {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();

        String templateShortFileName = null;

        if (period.equals(MonitoringOfReport.FOR_ONE_DAY)) {
             templateShortFileName = "MonitoringOfReportForOneDay.jasper";
        } else if (period.equals(MonitoringOfReport.FOR_MONTH)) {
             templateShortFileName = "MonitoringOfReportForMonth.jasper";
        } else  {
            return null;
        }

        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if (!(new File(templateFilename)).exists()) {
            if (templateShortFileName != null) {
                printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            } else {
                printError(String.format("Отчет строится на все дни недели кроме 'Воскресенья'"));
            }
            return null;
        }
        return templateFilename;
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        String idOfOrgString = "";
        if (idOfOrgList != null) {
            idOfOrgString = StringUtils.join(idOfOrgList.iterator(), ",");
        }
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOfOrgString);
        return properties;
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = basicReportJob.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s", "MonitoringOfReport", reportDistinctText, format);
    }

    @Override
    public String getPageFilename() {
        return "report/online/monitoring_of_report";
    }

    public String getReportName() {
        return reportName;
    }

    public String getReportNameForMenu() {
        return reportNameForMenu;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getSelectedPeriod() {
        return selectedPeriod;
    }

    public void setSelectedPeriod(Integer selectedPeriod) {
        this.selectedPeriod = selectedPeriod;
    }
}
