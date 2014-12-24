/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.RegisterStampReport;
import ru.axetta.ecafe.processor.core.report.TotalServicesReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
    private String htmlReport = null;
    private static final Logger logger = LoggerFactory.getLogger(TotalServicesReportPage.class);

    public void buildReportHTML() throws Exception{
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        //AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            this.totalReport = new TotalServicesReport ();
            TotalServicesReport.Builder reportBuilder = new TotalServicesReport.Builder();
            List<Long> orgs = new ArrayList<Long>();
            if(idOfOrg != null && idOfOrg>=0) {
                orgs.add(idOfOrg);
            }
            this.totalReport = reportBuilder.build(session, startDate, endDate, orgs);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            printMessage("Свод по услугам построен");
        } catch (Exception e) {
            printError("Ошибка при построении отчета: " + e.getMessage());
            //logger.error("Failed building report ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public String buildReportCSV() {
        return "showTotalServicesCSVList";
    }

    public String getPageFilename (){
        return "report/online/total_srvc_report";
    }

    public TotalServicesReport getTotalReport(){
        return totalReport;
    }
}