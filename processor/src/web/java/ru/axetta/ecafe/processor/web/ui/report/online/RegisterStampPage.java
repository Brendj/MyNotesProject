package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.RegisterStampReport;
import ru.axetta.ecafe.processor.core.report.ReportDAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;
import ru.axetta.ecafe.processor.core.daoservices.org.OrgShortItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.04.13
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class RegisterStampPage extends OnlineReportPage{

    private final static Logger logger = LoggerFactory.getLogger(RegisterStampPage.class);

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;
    @Autowired
    private RuntimeContext runtimeContext;
    @Autowired
    private ReportDAOService daoService;
    private String htmlReport = null;
    private Boolean includeActDiscrepancies = true;
    private final PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu();

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public void onReportPeriodChanged(ActionEvent event) {
        switch (periodTypeMenu.getPeriodType()){
            case ONE_DAY: {
                setEndDate(startDate);
            } break;
            case ONE_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 6));
            } break;
            case TWO_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 13));
            } break;
            case ONE_MONTH: {
                setEndDate(CalendarUtils.addMonth(startDate, 1));
            } break;
        }
    }

    public void onEndDateSpecified(ActionEvent event) {
        periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.FIXED_DAY);
    }

    @Override
    public void onShow() throws Exception {}

    public String getHtmlReport() {
        return htmlReport;
    }

    public Object buildReportHTML() {
        if(this.idOfOrg==null){
            printError("Не выбрана организация");
            return null;
        }
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateShortFileName = RegisterStampReport.class.getSimpleName() + "_summary.jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if(!(new File(templateFilename)).exists()){
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return null;
        }
        RegisterStampReport.Builder builder = new RegisterStampReport.Builder(templateFilename);
        Properties properties = new Properties();
        properties.setProperty(RegisterStampReport.PARAM_WITH_OUT_ACT_DISCREPANCIES, includeActDiscrepancies.toString());
        builder.setReportProperties(properties);
        Org org = daoService.getOrg(idOfOrg);
        builder.setOrg(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName()));
        try {
            Session session = runtimeContext.createReportPersistenceSession();
            BasicReportJob report =  builder.build(session,startDate, endDate, localCalendar);
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
            printMessage("Реестр талонов построен");
        } catch (Exception e) {
            printError("Ошибка при построении отчета: "+e.getMessage());
            logger.error("Failed build report ",e);
        }
        return null;
    }

    public Object clear(){
        idOfOrg=null;
        filter=null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

        localCalendar.setTime(new Date());
        this.startDate = DateUtils.truncate(localCalendar, Calendar.MONTH).getTime();

        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
        includeActDiscrepancies = true;
        htmlReport = null;
        return null;
    }

    public void showCSVList(ActionEvent actionEvent){
        if(this.idOfOrg==null){
            printError("Не выбрана организация");
            return;
        }
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateShortFileName = RegisterStampReport.class.getSimpleName() + ".jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if(!(new File(templateFilename)).exists()){
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return;
        }
        Date generateTime = new Date();
        RegisterStampReport.Builder builder = new RegisterStampReport.Builder(templateFilename);
        Properties properties = new Properties();
        properties.setProperty(RegisterStampReport.PARAM_WITH_OUT_ACT_DISCREPANCIES, includeActDiscrepancies.toString());
        builder.setReportProperties(properties);
        Org org = daoService.getOrg(idOfOrg);
        builder.setOrg(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName()));
        Session session = (Session) entityManager.getDelegate();
        try {
            RegisterStampReport registerStampReport = (RegisterStampReport) builder.build(session,startDate, endDate, localCalendar);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            String filename = buildFileName(generateTime, registerStampReport);
            response.setHeader("Content-disposition", String.format("inline;filename=%s", filename));

            JRXlsExporter xlsExport = new JRXlsExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, registerStampReport.getPrint());
            xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            xlsExport.exportReport();

            servletOutputStream.flush();
            servletOutputStream.close();

        } catch (JRException fnfe) {
            logger.error("Failed export report: ", fnfe);
            printError("Не найден шаблон отчета: " + fnfe.getMessage());
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        }
    }

    private String buildFileName(Date generateTime, RegisterStampReport registerStampReport) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = registerStampReport.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s.xls", "RegisterStampReport", reportDistinctText, format);
    }

    public Boolean getIncludeActDiscrepancies() {
        return includeActDiscrepancies;
    }

    public void setIncludeActDiscrepancies(Boolean includeActDiscrepancies) {
        this.includeActDiscrepancies = includeActDiscrepancies;
    }

    @Override
    public String getPageFilename() {
        return "report/online/registerstamp_report";
    }

}
