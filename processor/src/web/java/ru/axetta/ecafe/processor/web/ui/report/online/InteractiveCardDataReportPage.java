/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.awt.event.ActionEvent;


/**
 * Created with IntelliJ IDEA.
 * User: Anvarov
 * Date: 25.03.16
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class InteractiveCardDataReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(InteractiveCardDataReportPage.class);
    private InteractiveCardDataReport report;

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    public String getPageFilename() {
        return "report/online/interactive_card_data_report";
    }

    public InteractiveCardDataReport getReport() {
        return report;
    }

    public void doGenerate() {
        RuntimeContext.getAppContext().getBean(InteractiveCardDataReportPage.class).generate();
    }

    public void doGenerateXLS(ActionEvent actionEvent) {
        RuntimeContext.getAppContext().getBean(InteractiveCardDataReportPage.class).generateXLS();
    }

    @Transactional
    public void generate() {
        Session session;
        try {
            session = (Session) entityManager.getDelegate();
            generateReport(session, null);
        } catch (Exception e) {
               logger.error("Failed to load clients data", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    @Transactional
    public void generateXLS() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            generateXLS(session);
        } catch (Exception e) {
            logger.error("Failed to load clients data", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void generateReport(Session session, String templateFile) throws Exception {
        InteractiveCardDataReport.Builder reportBuilder = null;
        if (templateFile != null) {
            reportBuilder = new InteractiveCardDataReport.Builder(templateFile);
        }
    }

    public void generateXLS(Session session) {
        FacesContext facesContext = FacesContext.getCurrentInstance();}


}
