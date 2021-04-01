/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtAgeGroupItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplexGroupItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDietType;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.ComplexMenuReport;
import ru.axetta.ecafe.processor.core.report.ComplexMenuReportItem;
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
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Scope("session")
public class ComplexMenuReportPage  extends OnlineReportPage implements OrgListSelectPage.CompleteHandlerList,
        ContragentSelectPage.CompleteHandler {

    private final static Logger logger = LoggerFactory.getLogger(ComplexMenuReportPage.class);
    private Contragent contragent;
    private Long selectIdTypeFoodId;
    private Long selectDiet;
    private Long selectIdAgeGroup;
    private Long selectArchived;
    private String orgFilter = "Не выбрано";
    private final String CLASS_TYPE_TSP = Integer.toString(Contragent.TSP);
    List<ComplexMenuReportItem> result;

    public String getPageFilename() {
        return "report/online/complex_menu_report";
    }

    public String getClassTypeTSP(){
        return CLASS_TYPE_TSP;
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
                response.setHeader("Content-disposition", "inline;filename=complex_menu_report.xls");
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

    public void buildForJsf(){
        ComplexMenuReport.Builder builder = new ComplexMenuReport.Builder();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Transaction persistenceTransaction = null;
        Session persistenceSession = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List<String> stringOrgList = Arrays.asList(StringUtils.split(getGetStringIdOfOrgList(), ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());

            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            result = builder.createDataSource(persistenceSession, getContragent(),  idOfOrgList, selectIdTypeFoodId, selectDiet, selectIdAgeGroup, selectArchived );
            persistenceTransaction.commit();
            persistenceTransaction = null;

        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private BasicReportJob buildReport(){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            ComplexMenuReport.Builder builder = new ComplexMenuReport.Builder();
            builder.setContragent(this.contragent);
            builder.getReportProperties().setProperty("idOfOrgList", getGetStringIdOfOrgList());
            builder.getReportProperties().setProperty("selectIdTypeFoodId", selectIdTypeFoodId.toString());
            builder.getReportProperties().setProperty("selectDiet", selectDiet.toString());
            builder.getReportProperties().setProperty("selectIdAgeGroup", selectIdAgeGroup.toString());
            builder.getReportProperties().setProperty("selectArchived", selectArchived.toString());


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

    @Override
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

    public void showOrgListSelectPage(){
        if(contragent != null){
            MainPage.getSessionInstance().setIdOfContragentList(Arrays.asList(contragent.getIdOfContragent()));
        }
        MainPage.getSessionInstance().showOrgListSelectPage();
    }

    public Object showContragentListSelectPage() {
        if (idOfOrgList != null && !idOfOrgList.isEmpty()) {
            MainPage.getSessionInstance().showContragentListSelectPage(idOfOrgList);
        } else {
            MainPage.getSessionInstance().showContragentListSelectPage();
        }
        return null;
    }

    public SelectItem[] getTypesOfComplexFood() {
        List<WtComplexGroupItem> wtGroupComplex = DAOService.getInstance().getTypeComplexFood();
        SelectItem[] items = new SelectItem[wtGroupComplex.size() + 1];
        items[0] = new SelectItem(-1, "Не выбрано");
        int n = 1;
        for (WtComplexGroupItem wtComplexItem : wtGroupComplex) {
            items[n] = new SelectItem(wtComplexItem.getIdOfComplexGroupItem(), wtComplexItem.getDescription());
            ++n;
        }
        return items;
    }

    public SelectItem[] getTypesOfDiet() {
        List<WtDietType> dietGroupItems = DAOService.getInstance().getMapDiet();
        SelectItem[] items = new SelectItem[dietGroupItems.size() + 1];
        items[0] = new SelectItem(-1, "Не выбрано");
        int n = 1;
        for (WtDietType dietGroupItem : dietGroupItems) {
            items[n] = new SelectItem(dietGroupItem.getIdOfDietType(), dietGroupItem.getDescription());
            ++n;
        }
        return items;
    }

    public SelectItem[] getAgeGroup() {
        List<WtAgeGroupItem> ageGroups = DAOService.getInstance().getAgeGroups();
        SelectItem[] items = new SelectItem[ageGroups.size() + 1];
        items[0] = new SelectItem(-1, "Не выбрано");
        int n = 1;
        for (WtAgeGroupItem wtAgeGroupItem : ageGroups) {
            items[n] = new SelectItem(wtAgeGroupItem.getIdOfAgeGroupItem(), wtAgeGroupItem.getDescription());
            ++n;
        }
        return items;
    }

    public SelectItem[] getArchived() {
        SelectItem[] items = new SelectItem[3];
        items[0] = new SelectItem(0, "Без архивных");
        items[1] = new SelectItem(-1, "Включая архивные");
        items[2] = new SelectItem(1, "Только архивные");
        return items;
    }

    public List<ComplexMenuReportItem> getResult() {
        return result;
    }

    public void setResult(List<ComplexMenuReportItem> result) {
        this.result = result;
    }

    @Override
    public String getHtmlReport() {
        return htmlReport;
    }

    @Override
    public void setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
    }

    public Long getSelectArchived() {
        return selectArchived;
    }

    public void setSelectArchived(Long selectArchived) {
        this.selectArchived = selectArchived;
    }

    public Long getSelectIdAgeGroup() {
        return selectIdAgeGroup;
    }

    public void setSelectIdAgeGroup(Long selectIdAgeGroup) {
        this.selectIdAgeGroup = selectIdAgeGroup;
    }

    public Long getSelectIdTypeFoodId() {
        return selectIdTypeFoodId;
    }

    public void setSelectIdTypeFoodId(Long selectidTypeFoodId) {
        this.selectIdTypeFoodId = selectidTypeFoodId;
    }

    public Long getSelectDiet() {
        return selectDiet;
    }

    public void setSelectDiet(Long selectDiet) {
        this.selectDiet = selectDiet;
    }

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    public String getOrgFilter() {
        return orgFilter;
    }

    public void setOrgFilter(String orgFilter) {
        this.orgFilter = orgFilter;
    }

}
