/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.director;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.statistic.DirectorLoader;
import ru.axetta.ecafe.processor.core.statistic.DirectorStudentAttendanceReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.*;

public class DirectorStudentAttendancePage extends OnlineReportPage {
    private Logger logger = LoggerFactory.getLogger(DirectorStudentAttendancePage.class);
    private DirectorStudentAttendanceReport directorStudentAttendanceReport;
    private DirectorLoader directorLoader = new DirectorLoader();

    private static final String SELECT_ALL_OO = "-1";

    private Boolean showReport = false;
    private List<String> selectedOrgs = new ArrayList<String>();

    private CalendarModel startCalendarModel = new CalendarModel();
    private CalendarModel endCalendarModel = new CalendarModel();

    private List<DirectorLoader.OrgItem> organizations = new ArrayList<DirectorLoader.OrgItem>();


    public DirectorStudentAttendancePage() {
        selectedOrgs.add(SELECT_ALL_OO);

        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = RuntimeContext.getInstance()
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

        localCalendar.setTime(new Date());
        this.endDate = localCalendar.getTime();
        localCalendar.add(Calendar.DAY_OF_YEAR, -31);
        this.startDate = localCalendar.getTime();
    }

    public Object buildStudentAttendanceReport() {
        RuntimeContext runtimeContext;
        Session session = null;
        Transaction transaction = null;

        try {
            runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createReportPersistenceSession();
            transaction = session.beginTransaction();

            List<Long> orgList = new ArrayList<Long>();

            if (selectedOrgs.contains(SELECT_ALL_OO)) {      //all
                for (DirectorLoader.OrgItem item : organizations) {
                    orgList.add(item.getIdOfOrg());
                }
            } else {
                for (String id : selectedOrgs) {
                    if (!id.equals(SELECT_ALL_OO))
                        orgList.add(Long.parseLong(id));
                }
            }
            buildReport(session, this.startDate, this.endDate, orgList, selectedOrgs.contains(SELECT_ALL_OO));
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Failed to build director student attendance report", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return null;
    }

    public String getPageFilename() {
        return "student_attendance";
    }

    public void buildReport(Session session, Date startDate, Date endDate, List<Long> idsOfOrg, Boolean allOO) throws Exception {
        DirectorStudentAttendanceReport.Builder reportBuilder = new DirectorStudentAttendanceReport.Builder();
        this.directorStudentAttendanceReport = reportBuilder.build(session, startDate, endDate, idsOfOrg, allOO);
        showReport = true;
    }

    public List<String> getChartData() {
        try {
            if (null == directorStudentAttendanceReport)
                return Collections.emptyList();
            return directorStudentAttendanceReport.chartData();
        } catch (NullPointerException e) {
            logger.error("Failed to build director student attendance report", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public Boolean getShowReport() {
        return showReport;
    }

    public void setShowReport(Boolean showReport) {
        this.showReport = showReport;
    }

    public void loadOrganizations() {
        RuntimeContext runtimeContext;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession(); //открытие сессии к отчетной БД (запросы только на чтение)
            persistenceTransaction = persistenceSession.beginTransaction();
            organizations = directorLoader.loadOrganizations(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to load organizations in director student attendance report", e);
            printError("Ошибка при загрузке списка организаций: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public List<DirectorLoader.OrgItem> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<DirectorLoader.OrgItem> organizations) {
        this.organizations = organizations;
    }

    public List<String> getSelectedOrgs() {
        return selectedOrgs;
    }

    public void setSelectedOrgs(List<String> selectedOrgs) {
        this.selectedOrgs = selectedOrgs;
    }


    public CalendarModel getStartCalendarModel() {
        return startCalendarModel;
    }

    public void setStartCalendarModel(CalendarModel startCalendarModel) {
        this.startCalendarModel = startCalendarModel;
    }

    public CalendarModel getEndCalendarModel() {
        return endCalendarModel;
    }

    public void setEndCalendarModel(CalendarModel endCalendarModel) {
        this.endCalendarModel = endCalendarModel;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        endCalendarModel.updateStartDate(this.startDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        startCalendarModel.updateEndDate(this.endDate);
    }
}
