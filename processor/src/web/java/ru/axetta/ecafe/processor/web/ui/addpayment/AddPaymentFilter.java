/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.addpayment;

import ru.axetta.ecafe.processor.core.persistence.AddPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.07.2009
 * Time: 12:30:24
 * To change this template use File | Settings | File Templates.
 */
public class AddPaymentFilter {

    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public boolean isEmpty() {
            return null == idOfContragent;
        }

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }

    private ContragentItem contragentPayer = new ContragentItem();

    public ContragentItem getContragentPayer() {
        return contragentPayer;
    }

    public boolean isEmpty() {
        return (contragentPayer.isEmpty());
    }

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            if (multiContrFlag == 0)
                this.contragentPayer = new ContragentItem(contragent);
        }
    }

    public void clear() {
        contragentPayer = new ContragentItem();
    }

    public List retrieveAddPayment(Session session) throws Exception {
        Criteria criteria = session.createCriteria(AddPayment.class);
        if (!isEmpty()) {
            if (!this.contragentPayer.isEmpty()) {
                Contragent contragent = (Contragent) session
                        .load(Contragent.class, this.contragentPayer.getIdOfContragent());
                criteria.add(Restrictions.eq("contragentPayer", contragent));
            }
        }
        criteria.addOrder(Order.asc("idOfAddPayment"));
        return criteria.list();
    }
}