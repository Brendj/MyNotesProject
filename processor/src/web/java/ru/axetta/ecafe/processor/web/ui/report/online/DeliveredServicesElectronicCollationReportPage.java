/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.DeliveredServicesElectronicCollationReport;
import ru.axetta.ecafe.processor.core.report.DeliveredServicesReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 19.02.16
 * Time: 13:36
 */

public class DeliveredServicesElectronicCollationReportPage extends OnlineReportPage
        implements ContragentSelectPage.CompleteHandler,
        ContractSelectPage.CompleteHandler,
        OrgSelectPage.CompleteHandler {

    private DeliveredServicesElectronicCollationReport deliveredServices;
    private String htmlReport;
    private final CCAccountFilter contragentFilter = new CCAccountFilter();
    private final ContractFilter contractFilter = new ContractFilter();
    protected static final int MILLIS_IN_DAY = 86400000;
    private String region;
    private Boolean otherRegions;
    private final String FILTER_INIT = "Не выбрано. Отчет будет построен по всем образовательным организациям города";
    protected String filter = FILTER_INIT;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.idOfOrg = idOfOrg;
        if (this.idOfOrg == null) {
            filter = FILTER_INIT;
        } else {
            Org org = (Org) session.load(Org.class, this.idOfOrg);
            filter = org.getShortName();
        }
    }

    public String getPageFilename() {
        return "report/online/delivered_services_electronic_collation_report";
    }

    public DeliveredServicesElectronicCollationReport getDeliveredServicesReport() {
        return deliveredServices;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public CCAccountFilter getContragentFilter() {
        return contragentFilter;
    }

    public ContractFilter getContractFilter() {
        return contractFilter;
    }

    public Object showContractSelectPage() {
        MainPage.getSessionInstance().showContractSelectPage(this.contragentFilter.getContragent().getContragentName(),
                this.contragentFilter.getContragent().getIdOfContragent());
        return null;
    }

    public void showOrgSelectPage() {
        Long idOfContragent = null;
        try {
            idOfContragent = this.contragentFilter.getContragent().getIdOfContragent();
        } catch (Exception e) {
        }
        Long idOfContract = null;
        try {
            idOfContract = this.contractFilter.getContract().getIdOfContract();
        } catch (Exception e) {
        }
        MainPage.getSessionInstance().showOrgSelectPage(idOfContragent, idOfContract);
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes)
            throws Exception {
        contragentFilter.completeContragentSelection(session, idOfContragent);
        resetOrg();
    }

    public void completeContractSelection(Session session, Long idOfContract, int multiContrFlag, String classTypes)
            throws Exception {
        this.contractFilter.completeContractSelection(session, idOfContract, multiContrFlag, classTypes);
        resetOrg();
    }

    public void showCSVList(ActionEvent actionEvent) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
            String templateFilename =
                    autoReportGenerator.getReportsTemplateFilePath() + DeliveredServicesElectronicCollationReport.class
                            .getSimpleName() + ".jasper";
            DeliveredServicesReport.Builder builder = new DeliveredServicesReport.Builder(templateFilename);
            if (idOfOrg != null) {
                Org org = null;
                if (idOfOrg > -1) {
                    org = DAOService.getInstance().findOrById(idOfOrg);
                    builder.setOrg(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(),
                            org.getOfficialName()));
                }
            }
            Session session = RuntimeContext.getInstance().createReportPersistenceSession();
            fixDates();
            DeliveredServicesReport deliveredServicesReport = builder
                    .build(session, startDate, endDate, localCalendar, idOfOrg,
                            contragentFilter.getContragent().getIdOfContragent(),
                            contractFilter.getContract().getIdOfContract(), region, otherRegions);

            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            response.setHeader("Content-disposition", "inline;filename=deliveredElectronicCollation.xls");

            JRXlsExporter xlsExport = new JRXlsExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, deliveredServicesReport.getPrint());
            xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            xlsExport.exportReport();

            servletOutputStream.flush();
            servletOutputStream.close();

        } catch (JRException fnfe) {
            String message = (fnfe.getCause() == null ? fnfe.getMessage() : fnfe.getCause().getMessage());
            logAndPrintMessage(String.format("Ошибка при подготовке отчета не найден файл шаблона: %s", message), fnfe);
        } catch (Exception e) {
            getLogger().error("Failed to build sales report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", e.getMessage()));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(persistenceSession, getLogger());
        }
    }

    public void buildReport(Session session) throws Exception {
        DeliveredServicesElectronicCollationReport.Builder reportBuilder = new DeliveredServicesElectronicCollationReport.Builder();
        if (idOfOrg != null) {
            Org org;
            if (idOfOrg > -1) {
                org = DAOService.getInstance().findOrById(idOfOrg);
                reportBuilder.setOrg(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(),
                        org.getOfficialName()));
            }
        }
        fixDates();
        this.deliveredServices = reportBuilder.build(session, startDate, endDate, localCalendar, idOfOrg,
                contragentFilter.getContragent().getIdOfContragent(), contractFilter.getContract().getIdOfContract(),
                region, otherRegions);
        htmlReport = deliveredServices.getHtmlReport();
    }

    protected void fixDates() {
        if (startDate.after(endDate)) {
            startDate.setTime(endDate.getTime() - MILLIS_IN_DAY);
        }
    }

    public List<SelectItem> getRegions() {
        List<String> regions = DAOService.getInstance().getRegions();
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(""));
        for (String reg : regions) {
            items.add(new SelectItem(reg));
        }
        return items;
    }

    public String getRegion() {
        return region;
    }

    public boolean emptyRegion() {
        return ((region == null) || (region.isEmpty())) ? true : false;
    }

    public boolean emptyContragent() {
        return (contragentFilter.getContragent().getIdOfContragent() == null) ? true : false;
    }

    public boolean emptyContract() {
        return (contractFilter.getContract().getIdOfContract() == null) ? true : false;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Boolean getOtherRegions() {
        return otherRegions;
    }

    public void setOtherRegions(Boolean otherRegions) {
        this.otherRegions = otherRegions;
    }

    public void resetOrg() {
        if (!emptyRegion() || !emptyContract() || !emptyContragent()) {
            idOfOrg = null;
            setFilter(FILTER_INIT);
        }
    }
}