/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientSms;

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
        private final Long idOfTransaction;
        private final String eventType;
        private final Long eventId;
        private final Date eventTime;

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
            this.idOfTransaction = accountTransaction==null?null:accountTransaction.getIdOfTransaction();
            Card card = null == accountTransaction ? null : accountTransaction.getCard();
            if (null == card) {
                this.idOfCard = null;
                this.cardNo = null;
            } else {
                this.idOfCard = card.getIdOfCard();
                this.cardNo = card.getCardNo();
            }
            this.eventType = ClientSms.CONTENTS_TYPE_DESCRIPTION[clientSms.getContentsType()];
            this.eventId = clientSms.getContentsId();
            this.eventTime = clientSms.getEventTime();
        }
        public static final int TYPE_NEGATIVE_BALANCE = 1;
        public static final int TYPE_ENTER_EVENT_NOTIFY = 2;
        public static final int TYPE_PAYMENT_REGISTERED = 3;
        public static final int TYPE_LINKING_TOKEN = 4;
        public static final int TYPE_PAYMENT_NOTIFY= 5;
        public static final int TYPE_SMS_SUBSCRIPTION_FEE = 6;
        public static final int TYPE_SMS_SUB_FEE_WITHDRAW = 7;
        public static final int TYPE_SUBSCRIPTION_FEEDING = 8;
        public static final int TYPE_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS = 9;

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

        public Long getIdOfTransaction() {
            return idOfTransaction;
        }

        public String getEventType() {
            return eventType;
        }

        public Long getEventId() {
            return eventId;
        }

        public Date getEventTime() {
            return eventTime;
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