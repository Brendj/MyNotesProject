/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.CreatedAndReissuedCardReport;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.report.security.UserSelectPage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class IssuedCardsReportPage extends OnlineReportPage implements UserSelectPage.CompleteHandler  {

    private final static Logger logger = LoggerFactory.getLogger(MonitoringOfReportPage.class);
    private final String reportName = CreatedAndReissuedCardReport.REPORT_NAME;
    private Calendar localCalendar;
    private String htmlReport = null;
    private final UserSelectPage.UserShortItem userItem = new UserSelectPage.UserShortItem();

    public IssuedCardsReportPage() {
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
        this.endDate = localCalendar.getTime();
        this.periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY);
    }

    public void showUserSelectPage() {
        MainPage.getSessionInstance().showUserSelectPage(User.DefaultRole.CARD_OPERATOR);
    }

    public Object buildReportHTML() {
        htmlReport = null;
        if (validateFormData())
            return null;
        String templateFilename = checkIsTemplateExists();
        if (StringUtils.isEmpty(templateFilename))
            return null;
        CreatedAndReissuedCardReport.Builder builder = new CreatedAndReissuedCardReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        BasicReportJob report = null;
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            builder.setUser(MainPage.getSessionInstance().getCurrentUser());
            persistenceTransaction = session.beginTransaction();
            builder.setUser((User) session.load(User.class, userItem.getIdOfUser()));
            report = builder.build(session, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
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
                printError("Ошибка при построении отчета: "+e.getMessage());
                logger.error("Failed build report ",e);
            }
        }
        return null;
    }

    public void exportToXLS(ActionEvent actionEvent) {
        if (validateFormData())
            return;
        String templateFilename = checkIsTemplateExists();
        if (StringUtils.isEmpty(templateFilename))
            return;
        Date generateTime = new Date();
        CreatedAndReissuedCardReport.Builder builder = new CreatedAndReissuedCardReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        BasicReportJob report = null;
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            builder.setUser(MainPage.getSessionInstance().getCurrentUser());
            persistenceTransaction = session.beginTransaction();
            builder.setUser((User) session.load(User.class, userItem.getIdOfUser()));
            report =  builder.build(session, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
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

    private boolean validateFormData() {
        if (null == userItem.getIdOfUser()) {
            printError("Выберите пользователя");
            return true;
        }
        return false;
    }

    private String checkIsTemplateExists() {
        String templateFileName = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + CreatedAndReissuedCardReport.TEMPLATE_FILE_NAME;

        if (!(new File(templateFileName)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", CreatedAndReissuedCardReport.TEMPLATE_FILE_NAME));
            return null;
        }
        return templateFileName;
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
        return String.format("%s-%s-%s", "IssuedCardsReport", reportDistinctText, format);
    }

    @Override
    public void completeUserSelection(Session session, Long id) throws Exception {
        if (id == null) {
            filter = "Не выбрано";
            userItem.setIdOfUser(null);
            userItem.setUserName(null);
            return;
        }
        User user = DAOReadonlyService.getInstance().findUserById(id);
        if (user != null) {
            filter = user.getUserName();
            userItem.setIdOfUser(user.getIdOfUser());
            userItem.setUserName(user.getUserName());
            Person person = user.getPerson();
            if (null != person) {
                userItem.setFirstName(person.getFirstName());
                userItem.setSecondName(person.getSecondName());
                userItem.setSurname(person.getSurname());
                filter += " (" + userItem.getSurnameAndFirstLetters() + ")";
            }
        } else {
            filter = "Не выбрано";
            userItem.setIdOfUser(null);
            userItem.setUserName(null);
        }
    }

    @Override
    public String getPageFilename() {
        return "report/online/issued_cards_report";
    }

    public String getReportName() {
        return reportName;
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
}