/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.addpayment;

import ru.axetta.ecafe.processor.core.persistence.AddPayment;
import ru.axetta.ecafe.processor.core.persistence.utils.CurrentPositionsManager;
import ru.axetta.ecafe.processor.core.report.BasicReport;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.classic.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public class AddPaymentEditPage extends BasicWorkspacePage {
    public String getPageFilename() {
        return "contragent/addpayment/edit";
    }

    private String contragentPayerName;
    private String contragentReceiverName;
    private String summa;
    private String comment;
    private Date fromDate;
    private Date toDate;

    public String getContragentPayerName() {
        return contragentPayerName;
    }

    public String getContragentReceiverName() {
        return contragentReceiverName;
    }

    public String getSumma() {
        return summa;
    }

    public void setSumma(String summa) {
        this.summa = summa;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public void fill(Session session, Long idOfaddPayment) throws Exception {
        AddPayment addPayment = (AddPayment) session.load(AddPayment.class, idOfaddPayment);
        fill(addPayment);
    }
    
    public void updateAddPayment(Session persistenceSession, Long idOfAddPayment) throws Exception {
        Long summaLong = (long) (new Double(summa) * 100);
        AddPayment addPayment = (AddPayment) persistenceSession.load(AddPayment.class, idOfAddPayment);
        Long preSumma = addPayment.getSumma();
        addPayment.setSumma(summaLong);
        addPayment.setComment(comment);
        addPayment.setFromDate(fromDate);
        addPayment.setToDate(toDate);
        CurrentPositionsManager.updateAddPayment(persistenceSession, addPayment, summaLong - preSumma);
        fill(addPayment);
    }

    private void fill(AddPayment addPayment) throws Exception {
        this.contragentPayerName = addPayment.getContragentPayer().getContragentName();
        this.contragentReceiverName = addPayment.getContragentReceiver().getContragentName();
        this.summa = BasicReport.longToMoney(addPayment.getSumma());
        this.comment = addPayment.getComment();
        this.fromDate = addPayment.getFromDate();
        this.toDate = addPayment.getToDate();
    }
}