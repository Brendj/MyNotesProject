/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.ClientTransactionsReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.client.ClientFilter;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectListPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by anvarov on 09.06.2017.
 */
public class ClientTransactionsReportPage extends OnlineReportPage implements ClientSelectListPage.CompleteHandler {

    private final static Logger logger = LoggerFactory.getLogger(ClientTransactionsReportPage.class);
    private final String reportName = ClientTransactionsReport.REPORT_NAME;
    private final String reportNameForMenu = ClientTransactionsReport.REPORT_NAME_FOR_MENU;

    private String htmlReport = null;
    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY);

    private Boolean showAllBuildings = false;

    private final List<ClientSelectListPage.Item> clientList = new ArrayList<ClientSelectListPage.Item>();

    private final ClientFilter clientFilter = new ClientFilter();

    protected Long selectedOperationType = 0L;

    private ClientTransactionsReport.FilterType selectedTab = ClientTransactionsReport.FilterType.Organization;

    public Long getSelectedOperationType() {
        return selectedOperationType;
    }

    public void setSelectedOperationType(Long selectedOperationType) {
        this.selectedOperationType = selectedOperationType;
    }

    public ClientFilter getClientFilter() {
        return clientFilter;
    }

    protected String filterClient = "Не выбрано";

    public String getFilterClient() {
        return filterClient;
    }

    public ClientTransactionsReportPage() {
        super();
        localCalendar.setTime(new Date());
        CalendarUtils.truncateToDayOfMonth(localCalendar);
        localCalendar.add(Calendar.DAY_OF_MONTH, -1);
        this.startDate = localCalendar.getTime();
        localCalendar.add(Calendar.DAY_OF_MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = CalendarUtils.endOfDay(localCalendar.getTime());
    }

    public void showOrgListSelectPage() {
        MainPage.getSessionInstance().showOrgListSelectPage();
    }

    public void onReportPeriodChanged() {
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

    public void onEndDateSpecified() {
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

    public Object buildReportHTML() {
        htmlReport = null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile(".jasper");
        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }

        ClientTransactionsReport.Builder builder = new ClientTransactionsReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        builder.setFilterType(selectedTab);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;

        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            endDate = CalendarUtils.endOfDay(endDate);
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
        Date generateTime = new Date();
        ClientTransactionsReport.Builder builder = new ClientTransactionsReport.Builder(templateFilename);
        builder.setReportProperties(buildProperties());
        builder.setFilterType(selectedTab);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            endDate = CalendarUtils.endOfDay(endDate);
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

    private String checkIsExistFile(String suffix) {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName = ClientTransactionsReport.class.getSimpleName() + suffix;
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return null;
        }
        return templateFilename;
    }

    //private String orgsEmpty() {
    //    if (idOfOrgList.isEmpty()) {
    //        printError(String.format("Выберите организацию, или список организаций", ""));
    //        return null;
    //    }
    //    return "";
    //}

    private Properties buildProperties() {
        Properties properties = new Properties();
        if (idOfOrg != null) {
            properties.setProperty("idOfOrg", idOfOrg.toString());
        }
        String clientListString = "";
        if (clientList != null) {
            for (ClientSelectListPage.Item client : clientList) {
                clientListString = clientListString + (String.valueOf(client.getIdOfClient())) + ",";
            }
        }
        properties.setProperty("clientList", clientListString.trim());
        properties.setProperty("showAllBuildings", showAllBuildings.toString());
        properties.setProperty("operationType", getSelectedOperationType().toString());
        return properties;
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = basicReportJob.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s", "ClientTransactionsReport", reportDistinctText, format);
    }

    @Override
    public String getPageFilename() {
        return "report/online/client_transactions_report";
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

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getShowAllBuildings() {
        return showAllBuildings;
    }

    public void setShowAllBuildings(Boolean showAllBuildings) {
        this.showAllBuildings = showAllBuildings;
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public String getStringClientList() {
        List<String> val = new ArrayList<String>();
        for (ClientSelectListPage.Item item : getClientList()) {
            val.add(item.getCaption());
        }
        if (val.isEmpty()) {
            return "";
        } else {
            return val.toString();
        }
    }

    @Override
    public void completeClientSelection(Session session, List<ClientSelectListPage.Item> items) throws Exception {
        Client cl = null;
        if (items != null) {
            getClientList().clear();
            for (ClientSelectListPage.Item item : items) {
                getClientList().add(item);
            }
        }
        filterClient = getStringClientList();
    }

    public List<ClientSelectListPage.Item> getClientList() {
        return clientList;
    }

    private List<Long> getClients() {
        List<Long> clients = new ArrayList<Long>();
        for (ClientSelectListPage.Item item : clientList) {
            clients.add(item.getIdOfClient());
        }
        return clients;
    }

    public List<SelectItem> getOperationTypes() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(0, new SelectItem(0, "Все"));
        items.add(1, new SelectItem(1, "Пополнение"));
        items.add(2, new SelectItem(2, "Списание"));
        return items;
    }

    public String getSelectedTab() {
        return selectedTab.toString();
    }

    public void setSelectedTab(String selectedTab) {
        if (selectedTab.equals(ClientTransactionsReport.FilterType.Organization.toString())) {
            this.selectedTab = ClientTransactionsReport.FilterType.Organization;
        } else if (selectedTab.equals(ClientTransactionsReport.FilterType.Client.toString())) {
            this.selectedTab = ClientTransactionsReport.FilterType.Client;
        }
    }
}
