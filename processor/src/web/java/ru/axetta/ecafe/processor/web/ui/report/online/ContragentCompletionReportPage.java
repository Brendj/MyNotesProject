/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.contragent.ContragentCompletionItem;
import ru.axetta.ecafe.processor.core.daoservices.contragent.ContragentDAOService;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.ContragentCompletionReport;
import ru.axetta.ecafe.processor.core.report.ReportDAOService;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.01.13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ContragentCompletionReportPage extends OnlineReportPage implements ContragentSelectPage.CompleteHandler {

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;
    protected Date startDate;
    protected Date endDate;
    private ContragentDAOService contragentDAOService = new ContragentDAOService();
    private List<ContragentCompletionItem> contragentCompletionItems;
    private List<Contragent> contragentList;
    private Contragent defaultSupplier;
    private Integer contragentListCount = 0;
    private Calendar localCalendar;

    @Override
    public void onShow() throws Exception {
        contragentDAOService.setSession((Session) entityManager.getDelegate());
        contragentList = contragentDAOService.getPayAgentContragent();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        contragentListCount = contragentList.size();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

        localCalendar.setTime(new Date());

        this.startDate = DateUtils.truncate(localCalendar, Calendar.DAY_OF_MONTH).getTime();
        localCalendar.setTime(this.startDate);

        localCalendar.add(Calendar.DAY_OF_MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public Object generate(){
        contragentCompletionItems = new ArrayList<ContragentCompletionItem>();
        if (validateFormData()) return null;
        List<Org> orgItems = null;
        if(defaultSupplier!=null) {
            orgItems = contragentDAOService.findDistributionOrganizationByDefaultSupplier(defaultSupplier);
        } else {
            orgItems = contragentDAOService.findAllDistributionOrganization();
        }

        List<String> stringOrgList = Arrays.asList(StringUtils.split(getGetStringIdOfOrgList(), ','));
        List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
        for (String idOfOrg : stringOrgList) {
            idOfOrgList.add(Long.parseLong(idOfOrg));
        }

        if (!orgItems.isEmpty()) {
            ContragentCompletionItem total = new ContragentCompletionItem(contragentList);
            List<Long> idOfOrgs = new ArrayList<Long>();
            if (CollectionUtils.isEmpty(idOfOrgList)) {
                for (Org org : orgItems) {
                    idOfOrgs.add(org.getIdOfOrg());
                }
            }
            for (Long idOrg : idOfOrgList) {
                ContragentCompletionItem contragentCompletionItem = contragentDAOService.generateReportItem(idOrg,
                        contragentList, this.startDate, this.endDate);
                this.contragentCompletionItems.add(contragentCompletionItem);
                total.addContragentPayItems(contragentCompletionItem.getContragentPayItems());
                total.appendToPaymentsCount(contragentCompletionItem.getPaymentsCount());
            }
            this.contragentCompletionItems.add(total);
        }
        return null;
    }

    @Autowired
    private RuntimeContext runtimeContext;
    @Autowired
    private ReportDAOService daoService;

    private boolean validateFormData() {
        if(startDate==null){
            printError("Не указано дата выборки от");
            return true;
        }
        if(endDate==null){
            printError("Не указано дата выборки до");
            return true;
        }
        if(startDate.after(endDate)){
            printError("Дата выборки от меньше дата выборки до");
            return true;
        }
        if(defaultSupplier==null){
            printError("Не выбран 'Поставщик'");
            return true;
        }
        return false;
    }

    public void showCSVList(ActionEvent actionEvent){
        if (validateFormData()) return;
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + ContragentCompletionReport.class.getSimpleName() + ".jasper";
        ContragentCompletionReport.Builder builder = new ContragentCompletionReport.Builder(templateFilename);
        builder.setContragent(defaultSupplier);
        Session session = (Session) entityManager.getDelegate();
        builder.getReportProperties().setProperty("idOfOrgList", getGetStringIdOfOrgList());
        try {
            //ContragentCompletionReport contragentCompletionReport = (ContragentCompletionReport) builder.build(session,startDate, endDate, localCalendar);
            BasicReportJob report = builder.build(session,startDate, endDate, localCalendar);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            //facesContext.responseComplete();
            //response.setContentType("application/csv");
            //response.setHeader("Content-disposition", "inline;filename=contragent_completion.csv");
            //
            //JRCsvExporter csvExporter = new JRCsvExporter();
            //csvExporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, contragentCompletionReport.getPrint());
            //csvExporter.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            //csvExporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, ";");
            //csvExporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            //csvExporter.exportReport();
            //
            //servletOutputStream.flush();
            //servletOutputStream.close();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            response.setHeader("Content-disposition", "inline;filename=contragent_completion.xls");

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
            logAndPrintMessage("Error generate csv file ",e);
        }
    }

    public Integer getContragentListCount() {
        return contragentListCount;
    }

    public List<Contragent> getContragentList() {
        return contragentList;
    }


    public List<ContragentCompletionItem> getContragentCompletionItems() {
        return contragentCompletionItems;
    }

    @Override
    public String getPageFilename() {
        return "report/online/contragent_completion_report";
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        if (null != idOfContragent) {
            this.defaultSupplier = (Contragent) session.get(Contragent.class, idOfContragent);
        }
    }

    public Contragent getDefaultSupplier() {
        return defaultSupplier;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        localCalendar.setTime(endDate);
        localCalendar.add(Calendar.DAY_OF_MONTH,1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public boolean isAllHide() {
        return false;
    }

    public Object showOrgListSelectPage() {
        if (defaultSupplier != null) {
            MainPage.getSessionInstance().setIdOfContragentList(Arrays.asList(defaultSupplier.getIdOfContragent()));
        }
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    public Object showContragentSelectPage() {
        idOfOrgList.clear();
        filter = "Не выбрано";
        MainPage.getSessionInstance().showContragentSelectPage();
        return null;
    }
}
