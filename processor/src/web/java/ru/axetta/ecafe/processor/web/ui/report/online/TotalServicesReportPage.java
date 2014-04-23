/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.TotalServicesReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 29.10.12
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class TotalServicesReportPage extends OnlineReportPage{
    private TotalServicesReport totalReport;
    private static final Logger logger = LoggerFactory.getLogger(TotalServicesReportPage.class);

    public void buildReport(Session session) throws Exception{
        this.totalReport = new TotalServicesReport ();
        TotalServicesReport.Builder reportBuilder = new TotalServicesReport.Builder();
        this.totalReport = reportBuilder.build(session, startDate, endDate, idOfOrgList);
    }


    public void executeReport(){
        FacesContext facesContext = FacesContext.getCurrentInstance ();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try{
            try{
                runtimeContext = RuntimeContext.getInstance ();
                persistenceSession = runtimeContext.createReportPersistenceSession ();
                persistenceTransaction = persistenceSession.beginTransaction ();
                buildReport(persistenceSession);
                persistenceTransaction.commit();
                persistenceTransaction = null;
                facesContext.addMessage(null, new FacesMessage (FacesMessage.SEVERITY_INFO,
                        "Подготовка отчета завершена успешно", null));
            }
            finally{
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        } catch (Exception e){
            logger.error("Failed to build Total Services report", e);
            facesContext.addMessage (null, new FacesMessage (FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке отчета", null));
        }
    }

    public String getPageFilename (){
        return "report/online/total_srvc_report";
    }

    public TotalServicesReport getTotalReport(){
        return totalReport;
    }
}