/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.pos;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.POS;

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
public class PosFilter {

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

    private ContragentItem contragent = new ContragentItem();

    public ContragentItem getContragent() {
        return contragent;
    }

    public boolean isEmpty() {
        return contragent.isEmpty();
    }

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    public void completeContragentSelection(Session session, Long idOfContragent) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            this.contragent = new ContragentItem(contragent);
        }
    }

    public void clear() {
        contragent = new ContragentItem();
    }

    public List retrievePos(Session session) throws Exception {
        Criteria criteria = session.createCriteria(POS.class);
        if (!isEmpty()) {
            if (!this.contragent.isEmpty()) {
                Contragent contragent = (Contragent) session
                        .load(Contragent.class, this.contragent.getIdOfContragent());
                criteria.add(Restrictions.eq("contragent", contragent));
            }
        }
        criteria.addOrder(Order.asc("idOfPos"));
        return criteria.list();
    }
}