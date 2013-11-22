/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.ActiveClientsReport;
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
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 28.10.13
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class ActiveClientsReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(ActiveClientsReportPage.class);
    private ru.axetta.ecafe.processor.core.report.ActiveClientsReport report;


    public String getPageFilename ()
    {
        return "report/online/active_clients_report";
    }

    public ru.axetta.ecafe.processor.core.report.ActiveClientsReport getReport()
    {
        return report;
    }

    public void buildReport (Session session) throws Exception
    {
        this.report = new ActiveClientsReport ();
        ActiveClientsReport.Builder reportBuilder = new ActiveClientsReport.Builder();
        this.report = reportBuilder.build (session, startDate, endDate, new GregorianCalendar());
    }


    public void executeReport ()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance ();
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
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Подготовка отчета завершена успешно", null));
        }
        catch (Exception e)
        {
            //logger.error("Failed to build sales report", e);
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
        }
    }
}
