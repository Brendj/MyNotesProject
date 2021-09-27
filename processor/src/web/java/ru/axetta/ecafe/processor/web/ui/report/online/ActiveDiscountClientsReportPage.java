/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.ActiveDiscountClientsReport;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 17.01.14
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class ActiveDiscountClientsReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(ActiveDiscountClientsReportPage.class);
    private ru.axetta.ecafe.processor.core.report.ActiveDiscountClientsReport report;
    @PersistenceContext(unitName = "processorPU")
    public EntityManager entityManager;

    public String district;


    public String getPageFilename ()
    {
        return "report/online/active_discount_clients_report";
    }

    public ru.axetta.ecafe.processor.core.report.ActiveDiscountClientsReport getReport()
    {
        return report;
    }

    @Override
    public void onShow() throws Exception {
        report = new ActiveDiscountClientsReport();
    }

    public void executeReport(){
        try {
            if(CalendarUtils.betweenMoreMonth(startDate, endDate)) {
                printError("Диапазон построения отчета должен быть не больше месяца");
                RuntimeContext.getAppContext().getBean(ActiveDiscountClientsReportPage.class).clean();
                return;
            }
            if(idOfOrg == null) {
                printError("Выберите организацию");
                RuntimeContext.getAppContext().getBean(ActiveDiscountClientsReportPage.class).clean();
                return;
            }
            RuntimeContext.getAppContext().getBean(ActiveDiscountClientsReportPage.class).execute();
        } catch (Exception e) {
            super.logAndPrintMessage("Ошибка получения данных", e);
    }

        /*FacesContext facesContext = FacesContext.getCurrentInstance ();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try
        {
            runtimeContext = RuntimeContext.getInstance ();
            persistenceSession = runtimeContext.createPersistenceSession ();
            persistenceTransaction = persistenceSession.beginTransaction ();
            buildReport (persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        }
        catch (Exception e)
        {
            logger.error("Failed to build active discount clients report", e);
            facesContext.addMessage (null, new FacesMessage (FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке отчета", null));
        }
        finally
        {
            try {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            } catch (Exception e) {
                logger.error("Failed to build active clients report", e);
            }
        }*/
    }


    @Transactional
    public void execute() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            buildReport (session);
        } catch (Exception e) {
            logger.error("Failed to process report " + this.getClass().getSimpleName(), e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void clean() {
        ActiveDiscountClientsReport.Builder reportBuilder = new ActiveDiscountClientsReport.Builder().setExportToObjects(true);
        this.report = reportBuilder.clean();
    }

    public void buildReport (Session session) throws Exception {
        ActiveDiscountClientsReport.Builder reportBuilder = new ActiveDiscountClientsReport.Builder().setExportToObjects(true);

        if (idOfOrg != null) {
            Org org = null;
            if (idOfOrg != null && idOfOrg > -1) {
                org = DAOReadonlyService.getInstance().findOrById(idOfOrg);
            }
            reportBuilder.setOrg(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName()));
        }
        this.report = reportBuilder.build (session, startDate, endDate, new GregorianCalendar());
    }

    
    public List<ActiveDiscountClientsReport.ActiveDiscountClientsItem> getItems() {
        return report.getItems();
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public SelectItem[] getDistricts() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        String[] districts = DAOReadonlyService.getInstance().getDistricts();
        for(String d : districts) {
            items.add(new SelectItem(d, d));
        }
        return items.toArray(new SelectItem [items.size()]);
    }
}