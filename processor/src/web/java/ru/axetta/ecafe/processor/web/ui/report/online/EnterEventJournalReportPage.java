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
import ru.axetta.ecafe.processor.web.ui.client.ClientFilter;
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
import java.util.*;

/**
 * Created by anvarov on 04.04.18.
 */
public class EnterEventJournalReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(EnterEventJournalReportPage.class);
    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);

    private String htmlReport = null;

    private Integer[] eventState;
    private SelectItem[] eventStateSelectItemList;
    private String[] eventStates = EventState.values();
    private List<DocumentState> stateList = new ArrayList<DocumentState>();

    public EnterEventJournalReportPage() throws RuntimeContext.NotInitializedException {
        super();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DATE, 7);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    private Boolean allFriendlyOrgs = false;

    private final ClientFilter clientFilter = new ClientFilter();

    public String getPageFilename() {
        return "report/online/enter_event_journal_report";
    }

    public void onReportPeriodChanged(javax.faces.event.ActionEvent event) {
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

    public void onEndDateSpecified(javax.faces.event.ActionEvent event) {
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
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                Properties properties = new Properties();
                String groupNamesString = getGroupNamesString(persistenceSession, idOfOrg, allFriendlyOrgs);
                properties.setProperty("groupName", groupNamesString);

                String eventNums = "";

                for (Integer event: eventState) {
                    eventNums = eventNums + event + ",";
                }

                properties.setProperty("eventNums", eventNums);

                builder.setReportProperties(properties);
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
        if (templateFilename == null) {

        } else {
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

                        Properties properties = new Properties();
                        String groupNamesString = getGroupNamesString(persistenceSession, idOfOrg, allFriendlyOrgs);
                        properties.setProperty("groupName", groupNamesString);

                        String eventNums = "";


                        for (Integer event1: eventState) {
                                eventNums = eventNums + event1 + ",";
                        }

                        properties.setProperty("eventNums", eventNums);

                        builder.setReportProperties(properties);
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

    public String getHtmlReport() {
        return htmlReport;
    }

    public SelectItem[] getEventStateSelectItemList() {
        return eventStateSelectItemList;
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

    public void setEventStateSelectItemList(SelectItem[] eventStateSelectItemList) {
        this.eventStateSelectItemList = eventStateSelectItemList;
    }

    public String[] getEventStates() {
        return eventStates;
    }

    public void setEventStates(String[] eventStates) {
        this.eventStates = eventStates;
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
        eventStateSelectItemList = new SelectItem[eventStates.length];

        for (int i = 0; i < eventStates.length; i++) {
            if (eventStates[i].toString().equals("вход")) {
                eventStateSelectItemList[i] = new SelectItem(0, eventStates[i].toString());
            } else if (eventStates[i].toString().equals("выход")) {
                eventStateSelectItemList[i] = new SelectItem(1, eventStates[i].toString());
            } else if (eventStates[i].toString().equals("проход запрещен")) {
                eventStateSelectItemList[i] = new SelectItem(2, eventStates[i].toString());
            } else if (eventStates[i].toString().equals("взлом турникета")) {
                eventStateSelectItemList[i] = new SelectItem(3, eventStates[i].toString());
            } else if (eventStates[i].toString().equals("событие без прохода")) {
                eventStateSelectItemList[i] = new SelectItem(4, eventStates[i].toString());
            } else if (eventStates[i].toString().equals("отказ от прохода")) {
                eventStateSelectItemList[i] = new SelectItem(5, eventStates[i].toString());
            } else if (eventStates[i].toString().equals("повторный вход")) {
                eventStateSelectItemList[i] = new SelectItem(6, eventStates[i].toString());
            } else if (eventStates[i].toString().equals("повторный выход")) {
                eventStateSelectItemList[i] = new SelectItem(7, eventStates[i].toString());
            } else if (eventStates[i].toString().equals("обнаружен на подносе карты внутри здания")) {
                eventStateSelectItemList[i] = new SelectItem(100, eventStates[i].toString());
            } else if (eventStates[i].toString().equals("отмечен в классном журнале через внешнюю систему")) {
                eventStateSelectItemList[i] = new SelectItem(101, eventStates[i].toString());
            } else if (eventStates[i].toString().equals("отмечен учителем внутри здания")) {
                eventStateSelectItemList[i] = new SelectItem(102, eventStates[i].toString());
            } else if (eventStates[i].toString().equals("запрос на вход")) {
                eventStateSelectItemList[i] = new SelectItem(8, eventStates[i].toString());
            } else if (eventStates[i].toString().equals("запрос на выход")) {
                eventStateSelectItemList[i] = new SelectItem(9, eventStates[i].toString());
            }
        }
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
}