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
                 setEndDate(CalendarUtils.addDays(CalendarUtils.addMonth(startDate, 1), -1));
             }
             break;
         }
     }

    public void onEndDateSpecified(ActionEvent event) {
        Date end = CalendarUtils.truncateToDayOfMonth(endDate);
        if(CalendarUtils.addMonth(end, -1).equals(CalendarUtils.addDays(startDate, -1))){
            periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        } else {
            long diff=end.getTime()-startDate.getTime();
            int noofdays=(int)(diff/(1000*24*60*60));
            switch (noofdays){
                case 0: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY); break;
                case 6: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK); break;
                case 13: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.TWO_WEEK); break;
                default: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.FIXED_DAY); break;
            }
        }
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
                         orgsFilter, showDailySamplesCount);
     }

     @Override
     public String getContragentStringIdOfOrgList() {
         return idOfContragentOrgList.toString().replaceAll("[^0-9,]", "");
     }

     public void export(Session session) throws Exception {
         FacesContext facesContext = FacesContext.getCurrentInstance();
         Session persistenceSession = null;
         Transaction persistenceTransaction = null;
         try {
             AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
             String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + GoodRequestsReport.class.getSimpleName() + ".jasper";
             GoodRequestsReport.Builder builder = new GoodRequestsReport.Builder(templateFilename);

             GoodRequestsReport report = builder.build(session, hideMissedColumns, startDate, endDate, idOfOrgList, idOfContragentOrgList, goodName,
                     orgsFilter, showDailySamplesCount);

             HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

             ServletOutputStream servletOutputStream = response.getOutputStream();

             facesContext.responseComplete();
             response.setContentType("application/xls");
             response.setHeader("Content-disposition", "inline;filename=good_requests.xls");

             JRXlsExporter xlsExport = new JRXlsExporter();
             //JRCsvExporter csvExporter = new JRCsvExporter();
             xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
             xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
             xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
             xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
             xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
             //xlsExport.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, ";");
             xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
             xlsExport.exportReport();

             servletOutputStream.flush();
             servletOutputStream.close();

         } catch (JRException fnfe) {
             String message = (fnfe.getCause()==null?fnfe.getMessage():fnfe.getCause().getMessage());
             logAndPrintMessage(String.format("Ошибка при подготовке отчета не найден файл шаблона: %s", message),fnfe);
         } catch (Exception e) {
             getLogger().error("Failed to build sales report", e);
             facesContext.addMessage(null,
                     new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", e.getMessage()));
         } finally {
             HibernateUtils.rollback(persistenceTransaction, getLogger());
             HibernateUtils.close(persistenceSession, getLogger());
         }
     }

 }
