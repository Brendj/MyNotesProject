/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientSms;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
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
public class ClientSmsList {

    public static class Item {

        private final String idOfSms;
        private final Long idOfCard;
        private final Long cardNo;
        private final Long version;
        private final String phone;
        private final Integer contentsType;
        private final String textContents;
        private final Integer deliveryStatus;
        private final Date serviceSendTime;
        private final Date sendTime;
        private final Date deliveryTime;
        private final Long price;

        public Item(ClientSms clientSms) {
            this.idOfSms = clientSms.getIdOfSms();
            this.version = clientSms.getVersion();
            this.phone = clientSms.getPhone();
            this.contentsType = clientSms.getContentsType();
            this.deliveryStatus = clientSms.getDeliveryStatus();
            this.textContents = clientSms.getTextContents();
            this.serviceSendTime = clientSms.getServiceSendTime();
            this.sendTime = clientSms.getSendTime();
            this.deliveryTime = clientSms.getDeliveryTime();
            this.price = clientSms.getPrice();
            AccountTransaction accountTransaction = clientSms.getTransaction();
            Card card = null == accountTransaction ? null : accountTransaction.getCard();
            if (null == card) {
                this.idOfCard = null;
                this.cardNo = null;
            } else {
                this.idOfCard = card.getIdOfCard();
                this.cardNo = card.getCardNo();
            }
        }

        public String getIdOfSms() {
            return idOfSms;
        }

        public Long getIdOfCard() {
            return idOfCard;
        }

        public Long getCardNo() {
            return cardNo;
        }

        public Long getVersion() {
            return version;
        }

        public String getPhone() {
            return phone;
        }

        public Integer getContentsType() {
            return contentsType;
        }

        public String getTextContents() {
            return textContents;
        }

        public Integer getDeliveryStatus() {
            return deliveryStatus;
        }

        public Date getServiceSendTime() {
            return serviceSendTime;
        }

        public Date getSendTime() {
            return sendTime;
        }

        public Date getDeliveryTime() {
            return deliveryTime;
        }

        public Long getPrice() {
            return price;
        }
    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }

    public void fill(Session session, Client client, Date startTime, Date endTime) throws Exception {
        Criteria criteria = session.createCriteria(ClientSms.class);
        criteria.add(Restrictions.ge("serviceSendTime", startTime));
        criteria.add(Restrictions.le("serviceSendTime", endTime));
        criteria.add(Restrictions.eq("client", client));

        List<Item> items = new LinkedList<Item>();
        List clientPayments = criteria.list();
        for (Object object : clientPayments) {
            ClientSms clientSms = (ClientSms) object;
            items.add(new Item(clientSms));
        }
        this.items = items;
    }

}