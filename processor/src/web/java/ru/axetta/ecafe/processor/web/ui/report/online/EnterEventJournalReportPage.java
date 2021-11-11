/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.EnterEventJournalReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.client.ClientFilter;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectListPage;
import ru.axetta.ecafe.processor.web.ui.client.items.ClientGroupMenu;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by anvarov on 04.04.18.
 */
public class EnterEventJournalReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(EnterEventJournalReportPage.class);

    private String htmlReport = null;

    private Integer[] eventState;
    private List<DocumentState> stateList = new ArrayList<DocumentState>();
    private SelectItem[] eventFilter;
    private Integer selectedEventFilter = 0;
    private Boolean outputMigrants = false;
    private Boolean sortedBySections = false;

    public EnterEventJournalReportPage() throws RuntimeContext.NotInitializedException {
        super();
        this.periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);
        startDate = CalendarUtils.startOfDay(new Date());
        onReportPeriodChanged();
    }

    private Boolean allFriendlyOrgs = false;

    private final ClientFilter clientFilter = new ClientFilter();

    public String getPageFilename() {
        return "report/online/enter_event_journal_report";
    }

    public Object buildReportHTML() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile();
        if (templateFilename == null) {
            return null;
        }
        EnterEventJournalReport.Builder builder = new EnterEventJournalReport.Builder(templateFilename);
        if (idOfOrg == null) {
            printError("Выберите организацию ");
            return null;
        }
        builder.setIdOfOrg(idOfOrg);
        builder.setAllFriendlyOrgs(allFriendlyOrgs);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            builder.setReportProperties(buildProperties(persistenceSession));
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

    public void generateXLS(ActionEvent event) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile();
        if (templateFilename != null) {
            EnterEventJournalReport.Builder builder = new EnterEventJournalReport.Builder(templateFilename);
            if (idOfOrg == null) {
                printError("Выберите организацию ");
            } else {
                builder.setIdOfOrg(idOfOrg);
                builder.setAllFriendlyOrgs(allFriendlyOrgs);
                Session persistenceSession = null;
                Transaction persistenceTransaction = null;
                BasicReportJob report = null;
                try {
                    try {
                        persistenceSession = runtimeContext.createReportPersistenceSession();
                        persistenceTransaction = persistenceSession.beginTransaction();
                        builder.setReportProperties(buildProperties(persistenceSession));
                        report = builder.build(persistenceSession, startDate, endDate, localCalendar);
                        persistenceTransaction.commit();
                        persistenceTransaction = null;
                    } finally {
                        HibernateUtils.rollback(persistenceTransaction, logger);
                        HibernateUtils.close(persistenceSession, logger);
                    }
                } catch (Exception e) {
                    logger.error("Failed export report : ", e);
                    printError("Ошибка при подготовке отчета: " + e.getMessage());
                }

                FacesContext facesContext = FacesContext.getCurrentInstance();
                try {
                    if (report != null) {
                        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                                .getResponse();
                        ServletOutputStream servletOutputStream = response.getOutputStream();
                        facesContext.getResponseComplete();
                        facesContext.responseComplete();
                        response.setContentType("application/xls");
                        response.setHeader("Content-disposition", "inline;filename=enterEventJournalReport.xls");
                        JRXlsExporter xlsExporter = new JRXlsExporter();
                        xlsExporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                        xlsExporter.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                        xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                        xlsExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                        xlsExporter
                                .setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                        xlsExporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                        xlsExporter.exportReport();
                        servletOutputStream.close();
                    }
                } catch (Exception e) {
                    logAndPrintMessage("Ошибка при выгрузке отчета:", e);
                }
            }
        }
    }

    private String checkIsExistFile() {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFilename = EnterEventJournalReport.class.getSimpleName() + ".jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFilename;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateFilename));
            return null;
        }
        return templateFilename;
    }

    private Properties buildProperties(Session persistenceSession) throws Exception {
        Properties properties = new Properties();
        String idOfClients = "";
        if (getClientList() != null && getClientList().size() > 0) {
            for (ClientSelectListPage.Item item : getClientList()) {
                idOfClients += item.getIdOfClient() + ",";
            }
            idOfClients = idOfClients.substring(0, idOfClients.length() - 1);
        }
        properties.setProperty(EnterEventJournalReport.P_ID_CLIENT, idOfClients);
        String groupNamesString = getGroupNamesString(persistenceSession, idOfOrg, allFriendlyOrgs);
        properties.setProperty("groupName", groupNamesString);
        properties.setProperty("eventFilter", selectedEventFilter.toString());
        properties.setProperty("outputMigrants", outputMigrants.toString());
        if (outputMigrants) {
            properties.setProperty("sortedBySections", sortedBySections.toString());
        } else {
            properties.setProperty("sortedBySections", "false");
        }
        return properties;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public Integer[] getEventState() {
        return eventState;
    }

    public void setEventState(Integer[] eventState) {
        this.eventState = eventState;
    }

    public List<DocumentState> getStateList() {
        return stateList;
    }

    public void setStateList(List<DocumentState> stateList) {
        this.stateList = stateList;
    }

    public Boolean getAllFriendlyOrgs() {
        return allFriendlyOrgs;
    }

    public void setAllFriendlyOrgs(Boolean allFriendlyOrgs) {
        this.allFriendlyOrgs = allFriendlyOrgs;
    }

    public ClientFilter getClientFilter() {
        return clientFilter;
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public String getGroupNamesString(Session session, Long idOfOrg, Boolean allFriendlyOrgs) throws Exception {

        String groupNamesString = "";

        if (!clientFilter.getClientGroupId().equals(ClientGroupMenu.CLIENT_ALL)) {


            if (clientFilter.getClientGroupId().equals(ClientGroupMenu.CLIENT_STUDY)) {

                List<Long> groupIds = new ArrayList<Long>();

                for (ClientGroup.Predefined predefined : ClientGroup.Predefined.values()) {
                    if (!predefined.getValue().equals(ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue())) {
                        groupIds.add(predefined.getValue());
                    }
                }

                List<ClientGroup> clientGroupList;

                if (allFriendlyOrgs) {
                    Org org = (Org) session.load(Org.class, idOfOrg);
                    List<Long> idOfOrgList = new ArrayList<Long>();

                    for (Org orgItem : org.getFriendlyOrg()) {
                        idOfOrgList.add(orgItem.getIdOfOrg());
                    }
                    clientGroupList = getClientGroupByID(session, groupIds, idOfOrgList);
                } else {
                    clientGroupList = getClientGroupByID(session, groupIds, idOfOrg);
                }

                int i = 0;
                for (ClientGroup clientGroup : clientGroupList) {
                    groupNamesString = groupNamesString.concat(clientGroup.getGroupName());
                    if (i < clientGroupList.size() - 2) {
                        groupNamesString = groupNamesString.concat(",");
                    }
                    i++;
                }
            } else if (clientFilter.getClientGroupId().equals(ClientGroupMenu.CLIENT_PREDEFINED)) {
                int i = 0;
                for (ClientGroup.Predefined predefined : ClientGroup.Predefined.values()) {
                    if (!predefined.getValue().equals(ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue())) {
                        groupNamesString = groupNamesString.concat(predefined.getNameOfGroup());
                        if (i < ClientGroup.Predefined.values().length - 2) {
                            groupNamesString = groupNamesString.concat(",");
                        }
                        i++;
                    }
                }
            } else {
                ClientGroup.Predefined parse = ClientGroup.Predefined.parse(clientFilter.getClientGroupId());
                groupNamesString = parse.getNameOfGroup();
            }
        }

        return groupNamesString;
    }

    @Override
    public void onShow() {
        eventFilter = new SelectItem[4];
        eventFilter[0] = new SelectItem(0, "Все");
        eventFilter[1] = new SelectItem(1, "Вход-выход");
        eventFilter[2] = new SelectItem(2, "С клиентом");
        eventFilter[3] = new SelectItem(3, "Без клиента");
        stateList.clear();
    }

    private List<ClientGroup> getClientGroupByID(Session session, List<Long> groupIds, List<Long> idOfOrgList) {
        Criteria criteria = session.createCriteria(ClientGroup.class);
        criteria.add(Restrictions.in("compositeIdOfClientGroup.idOfOrg", idOfOrgList));
        criteria.add(Restrictions.not(Restrictions.in("compositeIdOfClientGroup.idOfClientGroup", groupIds)));
        return criteria.list();
    }

    public List<ClientGroup> getClientGroupByID(Session session, List<Long> groupIds, Long idOfOrg) throws Exception {
        Criteria criteria = session.createCriteria(ClientGroup.class);
        criteria.add(Restrictions.eq("compositeIdOfClientGroup.idOfOrg", idOfOrg));
        criteria.add(Restrictions.not(Restrictions.in("compositeIdOfClientGroup.idOfClientGroup", groupIds)));
        return criteria.list();
    }

    public SelectItem[] getEventFilter() {
        return eventFilter;
    }

    public void setEventFilter(SelectItem[] eventFilter) {
        this.eventFilter = eventFilter;
    }

    public Integer getSelectedEventFilter() {
        return selectedEventFilter;
    }

    public void setSelectedEventFilter(Integer selectedEventFilter) {
        this.selectedEventFilter = selectedEventFilter;
    }

    public Boolean getOutputMigrants() {
        return outputMigrants;
    }

    public void setOutputMigrants(Boolean outputMigrants) {
        this.outputMigrants = outputMigrants;
    }

    public Boolean getSortedBySections() {
        return sortedBySections;
    }

    public void setSortedBySections(Boolean sortedBySections) {
        this.sortedBySections = sortedBySections;
    }
}
