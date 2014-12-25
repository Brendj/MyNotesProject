/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.SMSDeliveryReport;
import ru.axetta.ecafe.processor.core.report.SentSmsReport;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 24.12.14
 * Time: 18:19
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class SmsDeliveryReportPage extends OnlineReportPage {
    @PersistenceContext(unitName = "reportsPU")
    public EntityManager entityManager;
    private final static Logger logger = LoggerFactory.getLogger(SmsDeliveryReportPage.class);
    private SMSDeliveryReport report;

    public String getPageFilename() {
        return "report/online/sms_delivery_report";
    }

    public SMSDeliveryReport getReport() {
        return report;
    }

    public void buildReport () throws Exception {
        RuntimeContext.getAppContext().getBean(SmsDeliveryReportPage.class).execute();
    }

    @Transactional
    public void execute() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            buildReport(session);
        } catch (Exception e) {
            logger.error("Failed to load sent sms data", e);
        }
    }

    public void buildReport(Session session) throws Exception {
        this.report = new SMSDeliveryReport ();
        SMSDeliveryReport.Builder reportBuilder = new SMSDeliveryReport.Builder();
        if (idOfOrg != null) {
            Org org = null;
            if (idOfOrg != null && idOfOrg > -1) {
                org = DAOService.getInstance().findOrById(idOfOrg);
            }
            reportBuilder.setOrg(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName()));
        }
        this.report = reportBuilder.build (session, startDate, endDate, new GregorianCalendar());
    }
}
