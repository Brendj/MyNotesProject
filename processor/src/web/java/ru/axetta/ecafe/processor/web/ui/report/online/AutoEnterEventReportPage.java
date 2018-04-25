/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.AutoEnterEventByDaysReport;
import ru.axetta.ecafe.processor.core.report.AutoEnterEventReport;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.client.ClientFilter;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;

/**
 * Created by anvarov on 04.04.18.
 */
public class AutoEnterEventReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(AutoEnterEventReportPage.class);

    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);

    private String htmlReport = null;

    public AutoEnterEventReportPage() throws RuntimeContext.NotInitializedException {
        super();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DATE, 7);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public String getPageFilename() {
        return "report/online/auto_enter_event_report";
    }

    private Boolean allFriendlyOrgs;

    private final ClientFilter clientFilter = new ClientFilter();

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
        AutoEnterEventByDaysReport.Builder builder = new AutoEnterEventByDaysReport.Builder(templateFilename);
        if (idOfOrg == null) {
            printError("Выберите организацию ");
            return null;
        }

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;

        try {
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                Org org = (Org) persistenceSession.load(Org.class, idOfOrg);
                BasicReportJob.OrgShortItem orgShortItem = new BasicReportJob.OrgShortItem(org.getIdOfOrg(),
                        org.getShortName(), org.getOfficialName(), org.getAddress());
                builder.setOrg(orgShortItem);

                Properties properties = new Properties();
                if (allFriendlyOrgs) {
                    properties.setProperty("isAllFriendlyOrgs", String.valueOf(allFriendlyOrgs));
                }

                String groupNamesString = getGroupNamesString(persistenceSession, idOfOrg, allFriendlyOrgs);
                properties.setProperty("groupName", groupNamesString);
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
            AutoEnterEventReport.Builder builder = new AutoEnterEventReport.Builder(templateFilename);
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            BasicReportJob report = null;
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                if (idOfOrg == null) {
                    printError(String.format("Выберите организацию "));
                }

                Org org = (Org) persistenceSession.load(Org.class, idOfOrg);
                BasicReportJob.OrgShortItem orgShortItem = new BasicReportJob.OrgShortItem(org.getIdOfOrg(),
                        org.getShortName(), org.getOfficialName(), org.getAddress());
                builder.setOrg(orgShortItem);

                Properties properties = new Properties();
                if (allFriendlyOrgs) {
                    properties.setProperty("isAllFriendlyOrgs", String.valueOf(allFriendlyOrgs));
                }

                String groupNamesString = getGroupNamesString(persistenceSession, idOfOrg, allFriendlyOrgs);
                properties.setProperty("groupName", groupNamesString);
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

            FacesContext facesContext = FacesContext.getCurrentInstance();
            try {
                if (report != null) {
                    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                            .getResponse();
                    ServletOutputStream servletOutputStream = response.getOutputStream();
                    facesContext.getResponseComplete();
                    facesContext.responseComplete();
                    response.setContentType("application/xls");
                    response.setHeader("Content-disposition", "inline;filename=typesOfCardReport.xls");
                    JRXlsExporter xlsExporter = new JRXlsExporter();
                    xlsExporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                    xlsExporter.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                    xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                    xlsExporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                    xlsExporter.exportReport();
                    servletOutputStream.close();
                }
            } catch (Exception e) {
                logAndPrintMessage("Ошибка при выгрузке отчета:", e);
            }
        }
    }

    private String checkIsExistFile() {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFilename = "AutoEnterEventByDaysReport.jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFilename;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateFilename));
            return null;
        }
        return templateFilename;
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public String getHtmlReport() {
        return htmlReport;
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

    public String getGroupNamesString(Session session, Long idOfOrg, Boolean allFriendlyOrgs) throws Exception {

        String groupNamesString = "";

        if (!clientFilter.getClientGroupId()
                .equals(ru.axetta.ecafe.processor.web.ui.client.items.ClientGroupMenu.CLIENT_ALL)) {


            if (clientFilter.getClientGroupId()
                    .equals(ru.axetta.ecafe.processor.web.ui.client.items.ClientGroupMenu.CLIENT_STUDY)) {

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
                    if (i < clientGroupList.size()) {
                        groupNamesString = groupNamesString.concat(",");
                        i++;
                    }
                }
            } else if (clientFilter.getClientGroupId()
                    .equals(ru.axetta.ecafe.processor.web.ui.client.items.ClientGroupMenu.CLIENT_PREDEFINED)) {
                int i = 0;
                for (ClientGroup.Predefined predefined : ClientGroup.Predefined.values()) {
                    if (!predefined.getValue().equals(ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue())) {
                        groupNamesString = groupNamesString.concat(predefined.getNameOfGroup());
                        if (i < ClientGroup.Predefined.values().length) {
                            groupNamesString = groupNamesString.concat(",");
                            i++;
                        }
                    }
                }
            } else {
                ClientGroup.Predefined parse = ClientGroup.Predefined.parse(clientFilter.getClientGroupId());
                groupNamesString = parse.getNameOfGroup();
            }
        }

        return groupNamesString;
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
