/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.settlement;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Settlement;

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
public class SettlementFilter {

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
    private ContragentItem contragentReceiver = new ContragentItem();

    public ContragentItem getContragentPayer() {
        return contragentPayer;
    }

    public ContragentItem getContragentReceiver() {
        return contragentReceiver;
    }

    public boolean isEmpty() {
        return (contragentPayer.isEmpty() && contragentReceiver.isEmpty());
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
            else
                this.contragentReceiver = new ContragentItem(contragent);
        }
    }

    public void clear() {
        contragentPayer = new ContragentItem();
        contragentReceiver = new ContragentItem();
    }

    public List retrieveSettlement(Session session) throws Exception {
        Criteria criteria = session.createCriteria(Settlement.class);
        if (!isEmpty()) {
            if (!this.contragentPayer.isEmpty()) {
                Contragent contragent = (Contragent) session
                        .load(Contragent.class, this.contragentPayer.getIdOfContragent());
                criteria.add(Restrictions.eq("idOfContragentPayer", contragent));
            }
            if (!this.contragentReceiver.isEmpty()) {
                Contragent contragent = (Contragent) session
                        .load(Contragent.class, this.contragentReceiver.getIdOfContragent());
                criteria.add(Restrictions.eq("idOfContragentReceiver", contragent));
            }
        }
        criteria.addOrder(Order.asc("idOfSettlement"));
        return criteria.list();
    }
}