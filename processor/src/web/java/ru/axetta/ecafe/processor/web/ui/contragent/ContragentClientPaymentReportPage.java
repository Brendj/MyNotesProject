/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.ContragentClientPaymentReport;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;

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
public class ContragentClientPaymentReportPage extends BasicWorkspacePage {

    private Long idOfContragent;
    private String shortName;
    private Date startDate;
    private Date endDate;
    private ContragentClientPaymentReport contragentClientPaymentReport;

    public String getPageFilename() {
        return "contragent/client_payment_report";
    }

    public Long getIdOfContragent() {
        return idOfContragent;
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

    public ContragentClientPaymentReport getContragentClientPaymentReport() {
        return contragentClientPaymentReport;
    }

    public ContragentClientPaymentReportPage() throws RuntimeContext.NotInitializedException {
        this.idOfContragent = null;
        this.shortName = null;
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();

            FacesContext facesContext = FacesContext.getCurrentInstance();
            Calendar localCalendar = runtimeContext
                    .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

            localCalendar.setTime(new Date());
            this.startDate = DateUtils.truncate(localCalendar, Calendar.MONTH).getTime();

            localCalendar.setTime(this.startDate);
            localCalendar.add(Calendar.MONTH, 1);
            this.endDate = localCalendar.getTime();
            this.contragentClientPaymentReport = new ContragentClientPaymentReport(this.startDate, this.endDate);
        } finally {
        }
    }

    public void fill(Session session, Long idOfContragent) throws Exception {
        Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
        fill(contragent);
    }

    public void buildReport(Session session, Long idOfContragent) throws Exception {
        Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
        this.contragentClientPaymentReport = new ContragentClientPaymentReport(this.startDate, this.endDate);
        ContragentClientPaymentReport.Builder reportBuilder = new ContragentClientPaymentReport.Builder();
        this.contragentClientPaymentReport = reportBuilder.build(session, this.startDate, this.endDate, contragent);
        fill(contragent);
    }

    public void fill(Contragent contragent) throws Exception {
        this.idOfContragent = contragent.getIdOfContragent();
        this.shortName = contragent.getContragentName();
    }
}