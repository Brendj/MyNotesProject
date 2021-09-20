/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.PaymentTotalsReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
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
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 27.01.15
 * Онлайн-отчет "Онлайн отчет / Отчеты по пополнениям / Итоговые показатели"
 */

@Component
@Scope(value = "session")
public class PaymentTotalsReportPage extends OnlineReportPage implements ContragentSelectPage.CompleteHandler {

    private final static Logger logger = LoggerFactory.getLogger(PaymentTotalsReportPage.class);
    private String htmlReport = null;
    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);

    private User currentUser;
    private Boolean hideNullRows = false;

    private final CCAccountFilter contragentFilter = new CCAccountFilter();

    BasicReportJob storedReport = null;

    public PaymentTotalsReportPage() {
        super();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DATE, 7);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
        //idOfOrgList.add(57L);
        //idOfOrgList.add(101L);
    }

    @Override
    public void onShow() throws Exception {
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        htmlReport = null;
        super.completeOrgSelection(session, idOfOrg);
    }

    public void onReportPeriodChanged(ActionEvent event) {
        htmlReport = null;
        switch (periodTypeMenu.getPeriodType()) {
            case ONE_DAY: {
                setEndDate(startDate);
            }
            break;
            case ONE_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 6));
            }
            break;
            case TWO_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 13));
            }
            break;
            case ONE_MONTH: {
                setEndDate(CalendarUtils.addDays(CalendarUtils.addMonth(startDate, 1), -1));
            }
            break;
        }
    }

    public void onEndDateSpecified(ActionEvent event) {
        htmlReport = null;
        Date end = CalendarUtils.truncateToDayOfMonth(endDate);
        if (CalendarUtils.addMonth(CalendarUtils.addOneDay(end), -1).equals(startDate)) {
            periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        } else {
            long diff = end.getTime() - startDate.getTime();
            int noOfDays = (int) (diff / (24 * 60 * 60 * 1000));
            switch (noOfDays) {
                case 0:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY);
                    break;
                case 6:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);
                    break;
                case 13:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.TWO_WEEK);
                    break;
                default:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.FIXED_DAY);
                    break;
            }
        }
        if (startDate.after(endDate)) {
            printError("Дата выборки от меньше дата выборки до");
        }
    }

    public void onHideNullRowsChange(ActionEvent event) {
        htmlReport = null;
    }

    public Object exportToHTML() {

        if (invalidFormData()) {
            return null;
        }

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile(".jasper");
        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        PaymentTotalsReport.Builder builder = new PaymentTotalsReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            report = builder.build(persistenceSession, startDate, endDate, localCalendar);
            storedReport = report;
            if (report.getPrint().getPages().size() <= 0) {
                printWarn("Данные по выбранным организациям за указанный период отсутствуют");
            }
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
                exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
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
        if (invalidFormData()) {
            return;
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile(".jasper");
        if (StringUtils.isEmpty(templateFilename)) {
            return;
        }
        Date generateTime = new Date();
        PaymentTotalsReport.Builder builder = new PaymentTotalsReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (storedReport == null) {
                report = builder.build(persistenceSession, startDate, endDate, localCalendar);
            } else {
                report = storedReport;
            }
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
                printMessage("Сводный отчет по заявкам построен");
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            }
        }
        return;
    }

    private String checkIsExistFile(String suffix) {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName = PaymentTotalsReport.class.getSimpleName() + suffix;
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return null;
        }
        return templateFilename;
    }

    private boolean invalidFormData() {

        if ((contragentFilter.getContragent().getIdOfContragent() == null) && (idOfOrgList.size() <= 0) && (idOfOrg
                == null)) {
            printError("Выберите список ОО");
            return true;
        }

        if (startDate == null) {
            printError("Не указано дата выборки от");
            return true;
        }
        if (endDate == null) {
            printError("Не указано дата выборки до");
            return true;
        }
        if (startDate.after(endDate)) {
            printError("Дата выборки от меньше дата выборки до");
            return true;
        }
        new MainPage();
        return false;
    }

    public Object clear() {
        contragentFilter.clear();
        idOfOrgList.clear();
        idOfOrg = null;
        filter = null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));
        localCalendar.setTime(new Date());
        this.startDate = DateUtils.truncate(localCalendar, Calendar.MONTH).getTime();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DATE, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
        hideNullRows = true;
        htmlReport = null;
        return null;
    }

    private Properties buildProperties() {
        Properties properties = new Properties();

        Long idOfContragent = contragentFilter.getContragent().getIdOfContragent();
        String idOfContragentString = idOfContragent != null ? Long.toString(idOfContragent) : "";
        properties.setProperty(PaymentTotalsReport.P_CONTRAGENT, idOfContragentString);
        List<Long> orgList = new ArrayList<Long>();
        if (idOfOrg != null) {
            orgList.add(idOfOrg);
        }
        idOfOrgList = orgList;
        properties.setProperty(PaymentTotalsReport.P_ORG_LIST,
                idOfOrgList.size() <= 0 ? "" : StringUtils.join(idOfOrgList.iterator(), ","));
        properties.setProperty(PaymentTotalsReport.P_HIDE_NULL_ROWS, Boolean.toString(hideNullRows));
        return properties;
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = basicReportJob.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s", "PaymentTotalsReport", reportDistinctText, format);
    }

    @Override
    public String getPageFilename() {
        return "report/online/payment_totals_report";
    }

    // Транзакционный метод
    @Override
    public void fill(Session persistenceSession, User currentUser) throws Exception {
        this.currentUser = currentUser;
        //Properties properties = DAOUtils.extractPropertiesByUserReportSetting(persistenceSession, currentUser,
        //        UserReportSetting.GOOD_REQUEST_REPORT);
        htmlReport = null;
    }

    @Override
    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes)
            throws Exception {
        contragentFilter.completeContragentSelection(session, idOfContragent);
    }

    public void showOrgListSelectPage() {
        Long idOfContragent = null;
        try {
            idOfContragent = this.contragentFilter.getContragent().getIdOfContragent();
        } catch (Exception e) {
        }
        List<Long> idOfContragentList = new ArrayList<>();
        idOfContragentList.add(idOfContragent);
        MainPage.getSessionInstance().showOrgListSelectPage(idOfContragentList);
    }


    /* Getter and Setters */

    public String getHtmlReport() {
        return htmlReport;
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Boolean getHideNullRows() {
        return hideNullRows;
    }

    public void setHideNullRows(Boolean hideNullRows) {
        this.hideNullRows = hideNullRows;
    }

    public CCAccountFilter getContragentFilter() {
        return contragentFilter;
    }
}

