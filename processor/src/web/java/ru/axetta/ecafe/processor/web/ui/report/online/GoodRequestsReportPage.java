/*
  * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
 import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
 import net.sf.jasperreports.engine.export.JRXlsExporter;
 import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

 import ru.axetta.ecafe.processor.core.RuntimeContext;
 import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
 import ru.axetta.ecafe.processor.core.report.DeliveredServicesReport;
 import ru.axetta.ecafe.processor.core.report.GoodRequestsReport;
 import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
 import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
 import ru.axetta.ecafe.processor.web.ui.MainPage;

 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.transaction.annotation.Transactional;

 import javax.faces.application.FacesMessage;
 import javax.faces.context.FacesContext;
 import javax.faces.event.ActionEvent;
 import javax.persistence.EntityManager;
 import javax.persistence.PersistenceContext;
 import javax.servlet.ServletOutputStream;
 import javax.servlet.http.HttpServletResponse;
 import java.io.OutputStreamWriter;
 import java.io.Writer;
 import java.text.SimpleDateFormat;
 import java.util.*;

/**
  * Created with IntelliJ IDEA.
  * User: chirikov
  * Date: 25.04.13
  * Time: 16:47
  * To change this template use File | Settings | File Templates.
  */
 public class GoodRequestsReportPage extends OnlineReportWithContragentPage {

     private static final Logger logger = LoggerFactory.getLogger(GoodRequestsReportPage.class);
     private GoodRequestsReport goodRequests;
     private Boolean hideMissedColumns;
     private boolean showAll = true;
     private int orgsFilter = 1;
     private String goodName;
     private int dailySamplesMode = 1;
     private Boolean showDailySamplesCount = true;
     private final PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu();

     public PeriodTypeMenu getPeriodTypeMenu() {
         return periodTypeMenu;
     }

     public void onReportPeriodChanged(ActionEvent event) {
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
                 setEndDate(CalendarUtils.addMonth(startDate, 1));
             }
             break;
         }
     }

     public void onEndDateSpecified(ActionEvent event) {
         periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.FIXED_DAY);
     }

     public String getPageFilename() {
         return "report/online/good_requests_report";
     }

     public void fill() {
     }

     public void loadPredefinedContragents() {
            /*if (idOfContragentOrgList.size() > 0) {
                return;
            }
            Session session = null;
            try {
                session = (Session) entityManager.getDelegate();
                List<OrgShortItem> orgs = OrgListSelectPage.retrieveOrgs(session, "", "", 2);
                Map<Long, String> contragentsMap = new HashMap<Long, String>();
                selectIdOfOrgList = false;
                for (OrgShortItem i : orgs) {
                    contragentsMap.put(i.getIdOfOrg(), i.getOfficialName());
                }
                completeOrgListSelection(contragentsMap);
                selectIdOfOrgList = true;
            } catch (Exception e) {
                logger.error("Failed to predefine allowed contragents list", e);
            }*/
     }

     public String getGoodName() {
         return goodName;
     }

     public void setGoodName(String goodName) {
         this.goodName = goodName;
     }

     public int getOrgsFilter() {
         return orgsFilter;
     }

     public void setOrgsFilter(int orgsFilter) {
         this.orgsFilter = orgsFilter;
     }

     public GoodRequestsReport getGoodRequestsReport() {
         return goodRequests;
     }

     public Boolean getHideMissedColumns() {
         return hideMissedColumns;
     }

     public void setHideMissedColumns(Boolean hideMissedColumns) {
         this.hideMissedColumns = hideMissedColumns;
     }

     public GoodRequestsReport getGoodRequests() {
         return goodRequests;
     }

     public void setGoodRequests(GoodRequestsReport goodRequests) {
         this.goodRequests = goodRequests;
     }

     public int getDailySamplesMode() {
         return dailySamplesMode;
     }

     public void setDailySamplesMode(int dailySamplesMode) {
         this.dailySamplesMode = dailySamplesMode;
     }

     public Boolean getShowDailySamplesCount() {
         showDailySamplesCount = (dailySamplesMode == 1);
         return showDailySamplesCount;
     }

     public void setShowDailySamplesCount(Boolean showDailySamplesCount) {
         dailySamplesMode = (showDailySamplesCount ? 1 : 0);
         this.showDailySamplesCount = showDailySamplesCount;
     }

     public boolean isShowAll() {
         return showAll;
     }

     public void setShowAll(boolean showAll) {
         this.showAll = showAll;
     }

     public void buildReport(Session session) throws Exception {
         //  Запускаем отчет
         GoodRequestsReport.Builder reportBuilder = new GoodRequestsReport.Builder();
         this.goodRequests = reportBuilder
                 .build(session, hideMissedColumns, startDate, endDate, idOfOrgList, idOfContragentOrgList, goodName,
                         orgsFilter);
     }

     @Override
     public String getContragentStringIdOfOrgList() {
         return idOfContragentOrgList.toString().replaceAll("[^0-9,]", "");
     }

     public void export(Session session) throws Exception {
         //  пределяем на какой лимит дней необходимо увеличить дату
         //endDate = new Date(getDaysLimitTS(daysLimit, startDate));

         //  Запускаем отчет
         GoodRequestsReport.Builder reportBuilder = new GoodRequestsReport.Builder();
         this.goodRequests = reportBuilder
                 .build(session, hideMissedColumns, startDate, endDate, idOfOrgList, idOfContragentOrgList, goodName,
                         orgsFilter);

         FacesContext facesContext = FacesContext.getCurrentInstance();
         HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
         facesContext.responseComplete();
         response.setContentType("text/csv; charset=windows-1251");
         response.setHeader("Content-disposition", "inline;filename=" + this.getClass().getSimpleName() + ".csv");

         final ServletOutputStream responseOutputStream = response.getOutputStream();
         try {
             Writer writer = new OutputStreamWriter(responseOutputStream);
             GoodRequestsReport.writeToFile(goodRequests, writer);
             writer.flush();
             responseOutputStream.flush();
         } finally {
             responseOutputStream.close();
         }
         facesContext.responseComplete();
     }

 }
