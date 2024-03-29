/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.DetailedEnterEventReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.web.ui.client.ClientFilter;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectListPage;

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
 * Created by anvarov on 06.04.18.
 */
public class DetailedEnterEventReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(DetailedEnterEventReportPage.class);
    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);

    private String htmlReport = null;

    public DetailedEnterEventReportPage() throws RuntimeContext.NotInitializedException {
        super();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DATE, 7);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public String getPageFilename() {
        return "report/online/detailed_enter_event_report";
    }

    private Boolean allFriendlyOrgs;

    private final ClientFilter clientFilter = new ClientFilter();

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
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String templateFilename = checkIsExistFile();
        if (templateFilename == null) {
            return null;
        }
        DetailedEnterEventReport.Builder builder = new DetailedEnterEventReport.Builder(templateFilename);

        if (idOfOrg == null) {
            printError(String.format("Выберите организацию "));
            return null;
        }

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                builder.setClientGroupNames(getClientGroupsNames(persistenceSession, idOfOrg, allFriendlyOrgs));
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
            DetailedEnterEventReport.Builder builder = new DetailedEnterEventReport.Builder(templateFilename);
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            BasicReportJob report = null;
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                if (idOfOrg == null) {
                    printError(String.format("Выберите организацию "));
                }
                builder.setClientGroupNames(getClientGroupsNames(persistenceSession, idOfOrg, allFriendlyOrgs));
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

            FacesContext facesContext = FacesContext.getCurrentInstance();
            try {
                if (report != null) {
                    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                            .getResponse();
                    ServletOutputStream servletOutputStream = response.getOutputStream();
                    facesContext.getResponseComplete();
                    facesContext.responseComplete();
                    response.setContentType("application/xls");
                    response.setHeader("Content-disposition", "inline;filename=DetailedEnterEventReport.xls");
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
        String templateShortFilename = "DetailedEnterEventReport.jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFilename;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateFilename));
            return null;
        }
        return templateFilename;
    }

    private Properties buildProperties(Session persistenceSession) throws Exception {
        Properties properties = new Properties();
        properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, (null != idOfOrg) ? idOfOrg.toString() : "0");
        properties.setProperty(DetailedEnterEventReport.P_ALL_FRIENDLY_ORGS,
                (null != allFriendlyOrgs) ? allFriendlyOrgs.toString() : "0");
        String idOfClients = "";
        if (getClientList() != null && getClientList().size() > 0) {
            for (ClientSelectListPage.Item item : getClientList()) {
                idOfClients += item.getIdOfClient() + ",";
            }
            idOfClients = idOfClients.substring(0, idOfClients.length() - 1);
        }
        properties.setProperty(DetailedEnterEventReport.P_ID_OF_CLIENTS, idOfClients);

        return properties;
    }

    public Boolean getAllFriendlyOrgs() {
        return allFriendlyOrgs;
    }

    public void setAllFriendlyOrgs(Boolean allFriendlyOrgs) {
        this.allFriendlyOrgs = allFriendlyOrgs;
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public ClientFilter getClientFilter() {
        return clientFilter;
    }

    public List<String> getClientGroupsNames(Session session, Long idOfOrg, Boolean allFriendlyOrgs) throws Exception {

        List<String> clientGroupNames = new ArrayList<>();

        if(clientFilter.getClientGroupId()
                .equals(ru.axetta.ecafe.processor.web.ui.client.items.ClientGroupMenu.CLIENT_ALL)) {
            return clientGroupNames;
        }
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
            for (ClientGroup clientGroup : clientGroupList) {
                clientGroupNames.add(clientGroup.getGroupName());
            }
        } else if (clientFilter.getClientGroupId()
                .equals(ru.axetta.ecafe.processor.web.ui.client.items.ClientGroupMenu.CLIENT_PREDEFINED)) {
            for (ClientGroup.Predefined predefined : ClientGroup.Predefined.values()) {
                if (!predefined.getValue().equals(ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue())) {
                    clientGroupNames.add(predefined.getNameOfGroup());
                }
            }
        } else {
            ClientGroup.Predefined parse = ClientGroup.Predefined.parse(clientFilter.getClientGroupId());
            clientGroupNames.add(parse.getNameOfGroup());
        }

        return clientGroupNames;
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
