/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class CardPaymentList {

    public static class Item {

        private final Long paySum;
        private final Date createTime;
        private final Date transactionTime;
        private final String idOfPayment;
        private final Long idOfContragent;
        private final String contragentName;
        private final String paymentMethod;
        private final String addPaymentMethod;
        private final String addIdOfPayment;

        public Item(ClientPayment clientPayment) {
            this.paySum = clientPayment.getPaySum();
            this.createTime = clientPayment.getCreateTime();
            this.transactionTime = clientPayment.getTransaction().getTransactionTime();
            this.idOfPayment = clientPayment.getIdOfPayment();
            Contragent contragent = clientPayment.getContragent();
            if (null == contragent) {
                this.idOfContragent = null;
                this.contragentName = "";
            } else {
                this.idOfContragent = contragent.getIdOfContragent();
                this.contragentName = contragent.getContragentName();
            }
            this.paymentMethod = ClientPayment.PAYMENT_METHOD_NAMES[clientPayment.getPaymentMethod()];
            this.addPaymentMethod = StringUtils.defaultString(clientPayment.getAddPaymentMethod());
            this.addIdOfPayment = StringUtils.defaultString(clientPayment.getAddIdOfPayment());
        }

        public Long getPaySum() {
            return paySum;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public Date getTransactionTime() {
            return transactionTime;
        }

        public String getIdOfPayment() {
            return idOfPayment;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public String getAddPaymentMethod() {
            return addPaymentMethod;
        }

        public String getAddIdOfPayment() {
            return addIdOfPayment;
        }
    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }

    public void fill(Session session, Card card, Date startTime, Date endTime) throws Exception {
        Criteria criteria = session.createCriteria(ClientPayment.class);
        criteria.add(Restrictions.eq("payType", ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT));
        criteria.add(Restrictions.ge("createTime", startTime));
        criteria.add(Restrictions.le("createTime", endTime));
        criteria = criteria.createCriteria("transaction");
        criteria.add(Restrictions.eq("card", card));

        List<Item> items = new LinkedList<Item>();
        List clientPayments = criteria.list();
        for (Object object : clientPayments) {
            ClientPayment clientPayment = (ClientPayment) object;
            items.add(new Item(clientPayment));
        }
        this.items = items;
    }

}