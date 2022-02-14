/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.DailySalesByGroupsReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 11.11.13
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class DailySalesByGroupsReportPage extends OnlineReportPage {
    private DailySalesByGroupsReport dailySalesReport;
    private String htmlReport;
    private Org org;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager em;
    private String includeComplex = "";
    private String groupByMenuGroup = "";
    private String menuGroups = "";
    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY);
    private boolean includeFriendlyOrgs = false;
    private boolean preordersOnly = false;

    public DailySalesByGroupsReportPage() throws RuntimeContext.NotInitializedException {
        super();
        localCalendar.setTime(new Date());
        CalendarUtils.truncateToDayOfMonth(localCalendar);
        localCalendar.add(Calendar.DAY_OF_MONTH, -1);
        this.startDate = localCalendar.getTime();
        localCalendar.add(Calendar.DAY_OF_MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public String getPageFilename() {
        return "report/online/daily_sales_by_groups_report";
    }

    public DailySalesByGroupsReport getDailySalesByGroupsReport() {
        return dailySalesReport;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public String getIncludeComplex() {
        return includeComplex;
    }

    public void setIncludeComplex(String includeComplex) {
        this.includeComplex = includeComplex;
    }

    public String getGroupByMenuGroup() {
        return groupByMenuGroup;
    }

    public void setGroupByMenuGroup(String groupByMenuGroup) {
        this.groupByMenuGroup = groupByMenuGroup;
    }

    public String getMenuGroups() {
        return menuGroups;
    }

    public void setMenuGroups(String menuGroups) {
        this.menuGroups = menuGroups;
    }

    public boolean isIncludeFriendlyOrgs() {
        return includeFriendlyOrgs;
    }

    public void setIncludeFriendlyOrgs(boolean includeFriendlyOrgs) {
        this.includeFriendlyOrgs = includeFriendlyOrgs;
    }

    public boolean isPreordersOnly() {
        return preordersOnly;
    }

    public void setPreordersOnly(boolean preordersOnly) {
        this.preordersOnly = preordersOnly;
    }

    public void showCSVList(ActionEvent actionEvent){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            // Открываем не readonly сессию, так как далее будет создаваться временная таблица для оптимизации запросов
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();

            List<BasicReportJob.OrgShortItem> orgShortItemList;
            if (idOfOrgList == null || idOfOrgList.isEmpty()) {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Не выбрана ни одна организация!", null));
                return;
            }
            List<Long> orgIdsList;
            if(includeFriendlyOrgs) {
                orgIdsList = getFriendlyOrgsIds(persistenceSession);
            }else {
                orgIdsList = checkOrgIdListForPreorder(persistenceSession, idOfOrgList);
            }
            if (orgIdsList == null || orgIdsList.isEmpty()) {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Не выбрана ни одна организация, удовлетворяющая фильтру", null));
                return;
            }
            orgShortItemList = getOrgShortItemList(orgIdsList);

            AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
            String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + DailySalesByGroupsReport.class.getSimpleName() + ".jasper";
            DailySalesByGroupsReport.Builder builder = new DailySalesByGroupsReport.Builder(templateFilename);
            builder.setOrgShortItemList(orgShortItemList);
            persistenceTransaction = persistenceSession.beginTransaction();
            dailySalesReport = (DailySalesByGroupsReport) builder.build(persistenceSession,startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            response.setHeader("Content-disposition", "inline;filename=daily_sales.xls");

            JRXlsExporter xlsExport = new JRXlsExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, dailySalesReport.getPrint());
            xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            xlsExport.exportReport();

            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (JRException fnfe) {
            String message = (fnfe.getCause()==null?fnfe.getMessage():fnfe.getCause().getMessage());
            logAndPrintMessage(String.format("Ошибка при подготовке отчета не найден файл шаблона: %s", message),fnfe);
        } catch (Exception e) {
            getLogger().error("Failed to build sales report", e);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(persistenceSession, getLogger());
        }
    }

    @Transactional
    public void buildReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Transaction persistenceTransaction = null;
        try {
            // Открываем не readonly сессию, так как далее будет создаваться временная таблица для оптимизации запросов
            Session persistenceSession = (Session) RuntimeContext.getInstance().createPersistenceSession();
            List<BasicReportJob.OrgShortItem> orgShortItemList;

            if (idOfOrgList == null || idOfOrgList.isEmpty()) {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Не выбрана ни одна организация!", null));
                return;
            }
            List<Long> orgIdsList;
            if (includeFriendlyOrgs) {
                orgIdsList = getFriendlyOrgsIds(persistenceSession);
            } else {
                orgIdsList = checkOrgIdListForPreorder(persistenceSession, idOfOrgList);
            }
            if (orgIdsList == null || orgIdsList.isEmpty()) {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Не выбрана ни одна организация, удовлетворяющая фильтру", null));
                return;
            }
            orgShortItemList = getOrgShortItemList(orgIdsList);

            persistenceTransaction = persistenceSession.beginTransaction();
            buildReport(persistenceSession, orgShortItemList);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        }
        catch (Exception e) {
            getLogger().error("Failed to build sales report", e);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        }
    }

    public void buildReport(Session session, List<BasicReportJob.OrgShortItem> orgShortItems) throws Exception {
        DailySalesByGroupsReport.Builder reportBuilder = new DailySalesByGroupsReport.Builder();
        reportBuilder.setOrgShortItemList(orgShortItems);
        Properties reportProperties = new Properties();
        reportProperties.setProperty(DailySalesByGroupsReport.PARAM_INCLUDE_COMPLEX, includeComplex);
        reportProperties.setProperty(DailySalesByGroupsReport.PARAM_GROUP_BY_MENU_GROUP, groupByMenuGroup);
        reportProperties.setProperty(DailySalesByGroupsReport.PARAM_MENU_GROUPS, menuGroups);
        reportBuilder.setReportProperties(reportProperties);
        dailySalesReport = (DailySalesByGroupsReport) reportBuilder.build(session, startDate, endDate, localCalendar);
        htmlReport = dailySalesReport.getHtmlReport();
    }

    public void onReportPeriodChanged() {
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
                setEndDate(CalendarUtils.addDays(CalendarUtils.addMonth(startDate, 1), -1));
            } break;
        }
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public void onEndDateSpecified() {
        Date end = CalendarUtils.truncateToDayOfMonth(endDate);
        if(CalendarUtils.addMonth(CalendarUtils.addOneDay(end), -1).equals(startDate)){
            periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        } else {
            long diff=end.getTime()-startDate.getTime();
            int noofdays=(int)(diff/(24*60*60*1000));
            switch (noofdays){
                case 0: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY); break;
                case 6: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK); break;
                case 13: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.TWO_WEEK); break;
                default: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.FIXED_DAY); break;
            }
        }
        if(startDate.after(endDate)){
            printError("Дата выборки от меньше дата выборки до");
        }
    }

    private List<Long> getFriendlyOrgsIds(Session session) {
        List<Long> tempIds = new ArrayList<Long>();
        for(Long orgId : idOfOrgList) {
            List<Long> friendlyOrgIdList = DAOUtils.findFriendlyOrgIds(session, orgId);
            tempIds.addAll(checkOrgIdListForPreorder(session, friendlyOrgIdList));
        }
        Set<Long> distinctIds = new HashSet<Long>(tempIds); //remove doubles
        return new ArrayList<Long>(distinctIds);
    }

    private List<BasicReportJob.OrgShortItem> getOrgShortItemList(List<Long> orgIds) {
        Org org;
        List<BasicReportJob.OrgShortItem> list = new ArrayList<BasicReportJob.OrgShortItem>();
        for (Long idOfOrg : orgIds) {
            org = DAOReadonlyService.getInstance().findOrById(idOfOrg);
            list.add(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(),
                    org.getOfficialName()));
        }
        return list;
    }

    private List<Long> checkOrgIdListForPreorder(Session session, List<Long> idOfOrgList) {
        if (preordersOnly) {
            List<Long> resulList = new ArrayList<Long>();
            for (Long idOfOrg : idOfOrgList) {
                Org org = (Org) session.load(Org.class, idOfOrg);
                if (org.getPreordersEnabled())
                    resulList.add(idOfOrg);
            }
            return resulList;
        } else {
            return idOfOrgList;
        }
    }
}
