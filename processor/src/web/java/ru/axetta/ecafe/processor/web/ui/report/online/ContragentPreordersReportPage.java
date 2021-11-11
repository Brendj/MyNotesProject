/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.ContragentPreordersReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Component
@Scope("session")
public class ContragentPreordersReportPage extends OnlineReportPage implements OrgListSelectPage.CompleteHandlerList,
        ContragentSelectPage.CompleteHandler{

    private final Logger logger = LoggerFactory.getLogger(ContragentPreordersReportPage.class);
    private Contragent contragent;
    private String htmlReport;
    private Boolean showOnlyUnpaidItems = false;
    private final String CLASS_TYPE_TSP = Integer.toString(Contragent.TSP);
    private String orgFilter = "Не выбрано";

    public ContragentPreordersReportPage(){
        super();
        startDate = CalendarUtils.startOfDay(new Date());
        onReportPeriodChanged();
    }

    public String getClassTypeTSP(){
        return CLASS_TYPE_TSP;
    }

    public Contragent getContragent(){
        return contragent;
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContragentFlag, String classTypes)
            throws Exception {
        if (idOfContragent != null) {
            contragent = (Contragent) session.get(Contragent.class, idOfContragent);
            filter = this.contragent.getContragentName();
        } else {
            contragent = null;
            filter = "Не выбрано";
        }
        idOfOrgList.clear();
        orgFilter = "Не выбрано";
    }

    public void completeOrgListSelection(Map<Long, String> orgMap){
        orgFilter = "";
        if (orgMap == null || orgMap.isEmpty()) {
            orgFilter = "Не выбрано";
            idOfOrgList.clear();
        } else {
            idOfOrgList = new ArrayList<Long>(orgMap.keySet());
            orgFilter = StringUtils.join(orgMap.values(), "; ");
        }
    }

    public String getPageFilename() {
        return "report/online/contragent_preorders_report";
    }

    public void showOrgListSelectPage(){
        if(contragent != null){
            MainPage.getSessionInstance().setIdOfContragentList(Arrays.asList(contragent.getIdOfContragent()));
        }
        MainPage.getSessionInstance().showOrgListSelectPage();
    }

    public Boolean getShowOnlyUnpaidItems() {
        return showOnlyUnpaidItems;
    }

    public void setShowOnlyUnpaidItems(Boolean showOnlyUnpaidItems) {
        this.showOnlyUnpaidItems = showOnlyUnpaidItems;
    }

    public Object buildHTMLReport() {
        htmlReport="";
        if (!validateFormData()) {
            return null;
        }
        BasicReportJob report = buildReport();
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

    public void exportToXLS() {
        if (!validateFormData()) {
            return;
        }
        BasicReportJob report = buildReport();
        if (report != null) {
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                        .getResponse();
                ServletOutputStream servletOutputStream = response.getOutputStream();

                facesContext.responseComplete();
                response.setContentType("application/xls");
                response.setHeader("Content-disposition", "inline;filename=contragent_preorders_report.xls");
                JRXlsExporter xlsExport = new JRXlsExporter();
                xlsExport.setParameter(JRExporterParameter.JASPER_PRINT, report.getPrint());
                xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                xlsExport.exportReport();
                servletOutputStream.flush();
                servletOutputStream.close();
            } catch (Exception e) {
                printError("Ошибка при построении отчета: " + e.getMessage());
                logger.error("Failed build report ", e);
            }
        }
    }

    private BasicReportJob buildReport(){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            ContragentPreordersReport.Builder builder = new ContragentPreordersReport.Builder();
            builder.setContragent(this.contragent);
            builder.getReportProperties().setProperty("idOfOrgList", getGetStringIdOfOrgList());
            builder.getReportProperties().setProperty("showOnlyUnpaidItems", showOnlyUnpaidItems.toString());

            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

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
        return report;
    }

    private boolean validateFormData() {
        if(startDate == null){
            printError("Не указана дата начала выборки");
            return false;
        }
        if(endDate == null){
            printError("Не указана дата конца выборки");
            return false;
        }
        if(startDate.after(endDate)){
            printError("Дата конца выборки меньше даты начала выборки");
            return false;
        } else {
            int diffInDays = (int)( (endDate.getTime() - startDate.getTime())
                    / (1000 * 60 * 60 * 24));
            if (diffInDays >= 365) {
                printError("Выбран слишком большой период. Измените период и повторите построение отчета");
                return false;
            }
        }
        return true;
    }

    public String getOrgFilter() {
        return orgFilter;
    }

    public void setOrgFilter(String orgFilter) {
        this.orgFilter = orgFilter;
    }

    @Override
    public String getHtmlReport() {
        return htmlReport;
    }

    @Override
    public void setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
    }
}
