/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.OrgOrderReport;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class OrgOrderReportPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(OrgOrderReportPage.class);

    private String shortName;
    private Date startDate;
    private Date endDate;
    private OrgOrderReport orgOrderReport;

    public String getPageFilename() {
        return "org/order_report";
    }

    public String getShortName() {
        return shortName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public OrgOrderReport getOrgOrderReport() {
        return orgOrderReport;
    }

    public OrgOrderReportPage() {
        this.shortName = null;
        this.startDate = new Date();
        this.endDate = new Date();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            Calendar localCalendar = runtimeContext
                    .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));
            localCalendar.setTime(new Date());
            this.startDate = DateUtils.truncate(localCalendar, Calendar.MONTH).getTime();
            localCalendar.setTime(this.startDate);
            localCalendar.add(Calendar.MONTH, 1);
            this.endDate = localCalendar.getTime();
        } catch (RuntimeContext.NotInitializedException e) {
            logger.error("Failed to retrieve runtime context", e);
        }
        this.orgOrderReport = new OrgOrderReport(this.startDate, this.endDate);
    }

    public void fill(Session session, Long idOfOrg) throws HibernateException {
        if (idOfOrg == null) {
            this.shortName = null;
        } else {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.shortName = org.getShortName();
        }
    }

    public void buildReport(Session session, Long idOfOrg) throws Exception {
        Org org = (Org) session.load(Org.class, idOfOrg);
        this.orgOrderReport = new OrgOrderReport(this.startDate, this.endDate);

        OrgOrderReport.Builder orgOrderReportBuilder = new OrgOrderReport.Builder();
        this.orgOrderReport = orgOrderReportBuilder.build(session, this.startDate, this.endDate, org);
    }
}