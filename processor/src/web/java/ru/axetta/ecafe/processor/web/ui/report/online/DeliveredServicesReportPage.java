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
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.DeliveredServicesReport;
import ru.axetta.ecafe.processor.core.report.DeliveredServicesReportBuilder;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractSelectPage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 25.04.13
 * Time: 16:47
 * Отчет по предоставленным услугам
 */
public class DeliveredServicesReportPage extends OnlineReportPage
        implements ContragentSelectPage.CompleteHandler, ContractSelectPage.CompleteHandler {
    private DeliveredServicesReport deliveredServices;
    private String goodName;
    private Boolean hideMissedColumns;
    private String htmlReport;
    private final CCAccountFilter contragentFilter = new CCAccountFilter();
    private final ContractFilter contractFilter= new ContractFilter();
    protected static final int MILLIS_IN_DAY = 86400000;
    private String region;
    private Boolean otherRegions;
    private Boolean withoutFriendly;
    private final String FILTER_INIT = "Не выбрано. Отчет будет построен по всем образовательным организациям города";
    private final String FILTER_SUPER = "Не выбрано";

    public String getPageFilename() {
        return "report/online/delivered_services_report";
    }

    public DeliveredServicesReport getDeliveredServicesReport() {
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

    public Object showContractSelectPage () {
        MainPage.getSessionInstance().showContractSelectPage(this.contragentFilter.getContragent().getContragentName(),
                                                             this.contragentFilter.getContragent().getIdOfContragent());
        return null;
    }

    public void showOrgListSelectPage () {
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
        List<Long> idOfContragentList = new ArrayList<>();
        idOfContragentList.add(idOfContragent);
        MainPage.getSessionInstance().showOrgListSelectPage(idOfContragentList);
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        contragentFilter.completeContragentSelection(session, idOfContragent);
        resetOrg();
    }

    public void completeContractSelection(Session session, Long idOfContract, int multiContrFlag, String classTypes) throws Exception {
        this.contractFilter.completeContractSelection(session, idOfContract, multiContrFlag, classTypes);
        resetOrg();
    }

    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        super.completeOrgListSelection(orgMap);
        if (emptyOrgs()) {
            withoutFriendly = false;
        }
    }

    public Object clear(){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));
        localCalendar.setTime(new Date());
        this.startDate = DateUtils.truncate(localCalendar, Calendar.MONTH).getTime();
        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();

        contractFilter.clear();
        contragentFilter.clear();
        region = null;
        otherRegions = false;
        filter = "Не выбрано";
        htmlReport = null;
        if(idOfOrgList != null) {
            idOfOrgList.clear();
        }
        return null;
    }

    public void showCSVList(ActionEvent actionEvent){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
            String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + DeliveredServicesReport.class.getSimpleName() + ".jasper";
            DeliveredServicesReportBuilder builder = new DeliveredServicesReportBuilder(templateFilename);
            if (idOfOrgList != null) {
                List<BasicReportJob.OrgShortItem> list = new ArrayList<BasicReportJob.OrgShortItem>();
                for(Long idOfOrg : idOfOrgList) {
                    Org org = null;
                    if (idOfOrg > -1) {
                        org = DAOService.getInstance().findOrById(idOfOrg);
                        list.add(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName()));
                    }
                }
                builder.setOrgShortItemList(list);
            }
            Session session = RuntimeContext.getInstance().createReportPersistenceSession();
            fixDates();
            DeliveredServicesReport deliveredServicesReport = builder.build(session,startDate, endDate, localCalendar, idOfOrg, contragentFilter.getContragent().getIdOfContragent(),
                    contractFilter.getContract().getIdOfContract(), region, otherRegions, withoutFriendly);

            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            response.setHeader("Content-disposition", "inline;filename=delivered.xls");

            JRXlsExporter xlsExport = new JRXlsExporter();
            //JRCsvExporter csvExporter = new JRCsvExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, deliveredServicesReport.getPrint());
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

    public void buildReport(Session session) throws Exception {
        DeliveredServicesReportBuilder reportBuilder = new DeliveredServicesReportBuilder();
        if (idOfOrgList != null) {
            List<BasicReportJob.OrgShortItem> list = new ArrayList<BasicReportJob.OrgShortItem>();
            for(Long idOfOrg : idOfOrgList) {
                Org org = null;
                if (idOfOrg > -1) {
                    org = DAOService.getInstance().findOrById(idOfOrg);
                    list.add(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName()));
                }
            }
            reportBuilder.setOrgShortItemList(list);
        }
        fixDates();
        this.deliveredServices = reportBuilder.build(session, startDate, endDate, localCalendar, idOfOrg,
                                                    contragentFilter.getContragent().getIdOfContragent(),
                                                    contractFilter.getContract().getIdOfContract(), region, otherRegions, withoutFriendly);
        htmlReport = deliveredServices.getHtmlReport();
    }

    protected void fixDates() {
        if(startDate.after(endDate)) {
            startDate.setTime(endDate.getTime() - MILLIS_IN_DAY);
        }
    }

    public String getGetStringIdOfOrgList() {
        return idOfOrgList.toString().replaceAll("[^0-9,]","");
    }

    public List<SelectItem> getRegions() {
        List<String> regions = DAOService.getInstance().getRegions();
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(""));
        for(String reg : regions) {
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

    public boolean emptyOrgs() {
        return ((idOfOrgList == null) || (idOfOrgList.isEmpty())) ? true : false;
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
            idOfOrgList = null;
            filter = FILTER_INIT;
        }
    }

    public String getFILTER_INIT() {
        return FILTER_INIT;
    }

    public String getFILTER_SUPER() {
        return FILTER_SUPER;
    }

    public Boolean getWithoutFriendly() {
        return withoutFriendly;
    }

    public void setWithoutFriendly(Boolean withoutFriendly) {
        this.withoutFriendly = withoutFriendly;
    }
}
