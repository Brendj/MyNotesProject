/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.contragent.ContragentCompletionItem;
import ru.axetta.ecafe.processor.core.daoservices.contragent.ContragentDAOService;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.ContragentCompletionReport;
import ru.axetta.ecafe.processor.core.report.ReportDAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.01.13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ContragentCompletionReportPage extends BasicWorkspacePage implements ContragentSelectPage.CompleteHandler {

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
        List<Org> orgItems = null;
        if(defaultSupplier!=null) {
            orgItems = contragentDAOService.findDistributionOrganizationByDefaultSupplier(defaultSupplier);
        } else {
            orgItems = contragentDAOService.findAllDistributionOrganization();
        }

        if(!orgItems.isEmpty()){
            ContragentCompletionItem total = new ContragentCompletionItem(contragentList);
            for (Org org: orgItems){
                ContragentCompletionItem contragentCompletionItem = contragentDAOService.generateReportItems(org.getIdOfOrg(),contragentList, this.startDate, this.endDate);
                this.contragentCompletionItems.add(contragentCompletionItem);
                total.addContragentPayItems(contragentCompletionItem.getContragentPayItems());
            }
            this.contragentCompletionItems.add(total);
        }
        return null;
    }

    @Autowired
    private RuntimeContext runtimeContext;
    @Autowired
    private ReportDAOService daoService;

    public void showCSVList(ActionEvent actionEvent){
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + ContragentCompletionReport.class.getSimpleName() + ".jasper";
        ContragentCompletionReport.Builder builder = new ContragentCompletionReport.Builder(templateFilename);
        builder.setContragent(defaultSupplier);
        Session session = (Session) entityManager.getDelegate();
        try {
            ContragentCompletionReport contragentCompletionReport = (ContragentCompletionReport) builder.build(session,startDate, endDate, localCalendar);

            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/csv");
            response.setHeader("Content-disposition", "inline;filename=contragent_completion.csv");

            JRCsvExporter csvExporter = new JRCsvExporter();
            csvExporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, contragentCompletionReport.getPrint());
            csvExporter.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            csvExporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, ";");
            csvExporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            csvExporter.exportReport();

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
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
