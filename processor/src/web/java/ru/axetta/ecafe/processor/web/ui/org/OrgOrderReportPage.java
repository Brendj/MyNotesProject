/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.OrgOrderReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
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
public class OrgOrderReportPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrgOrderReportPage.class);

    private Long idOfOrg;
    private String filter = "Не выбрано";
    private String shortName;
    private Date startDate;
    private Date endDate;
    private OrgOrderReport orgOrderReport;

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getFilter() {
        return filter;
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.idOfOrg = idOfOrg;
        if (this.idOfOrg == null) {
            filter = "Не выбрано";
        } else {
            Org org = (Org)session.load(Org.class, this.idOfOrg);
            filter = org.getShortName();
        }
    }

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
        this.shortName = "Не выбрано";
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
            this.shortName = "Не выбрано";
        } else {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.shortName = org.getShortName();
        }
    }

    public Object buildOrgOrderReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (idOfOrg != null) {
                buildReport(persistenceSession, idOfOrg);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } else {
                facesContext
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Выберите организацию", null));
            }
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build org order report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public void buildReport(Session session, Long idOfOrg) throws Exception {
        Org org = (Org) session.load(Org.class, idOfOrg);
        this.orgOrderReport = new OrgOrderReport(this.startDate, this.endDate);

        OrgOrderReport.Builder orgOrderReportBuilder = new OrgOrderReport.Builder();
        this.orgOrderReport = orgOrderReportBuilder.build(session, this.startDate, this.endDate, org);
    }
}