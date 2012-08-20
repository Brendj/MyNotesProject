/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.addpayment;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.CurrentPositionsManager;
import ru.axetta.ecafe.processor.core.persistence.AddPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
public class AddPaymentCreatePage extends BasicWorkspacePage
        implements ContragentSelectPage.CompleteHandler {
    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }

    public String getPageFilename() {
        return "contragent/addpayment/create";
    }

    private ContragentItem contragentPayer = new ContragentItem();
    private Contragent operatorContragent;
    private String summa;
    private String comment;
    private Date fromDate;
    private Date toDate;

    public ContragentItem getContragentPayer() {
        return contragentPayer;
    }

    public Contragent getOperatorContragent() {
        return operatorContragent;
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

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            if (multiContrFlag == 0)
                this.contragentPayer = new ContragentItem(contragent);
        }
    }

    public void fill(Session session) throws Exception {
        Criteria criteria = session.createCriteria(Contragent.class);
        criteria.add(Restrictions.eq("classId", Contragent.OPERATOR));
        operatorContragent = (Contragent) criteria.uniqueResult();
    }

    public void createAddPayment(Session session) throws Exception {
        Contragent contragentPayer = (Contragent) session.load(Contragent.class,
                this.contragentPayer.getIdOfContragent());

        Long summaLong = (long) (new Double(summa) * 100);
        AddPayment addPayment = new AddPayment();
        addPayment.setContragentPayer(contragentPayer);
        addPayment.setContragentReceiver(operatorContragent);
        addPayment.setSumma(summaLong);
        addPayment.setComment(comment);
        addPayment.setToDate(toDate);
        addPayment.setFromDate(fromDate);
        RuntimeContext.getFinancialOpsManager().createAddPayment(session, addPayment);
    }

    public class WrongContragentsException extends Exception {}
}
