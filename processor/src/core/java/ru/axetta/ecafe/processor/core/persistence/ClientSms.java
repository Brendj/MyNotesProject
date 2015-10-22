/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.06.2009
 * Time: 14:02:41
 * To change this template use File | Settings | File Templates.
 */
public class ClientSms {

    public static final int TEXT_CONTENTS_MAX_LENGTH = 70;

    public static final String UNKNOWN_CONTENTS_TYPE_DESCRIPTION = "Неизвестен";
    public static final String CONTENTS_TYPE_DESCRIPTION[] = {UNKNOWN_CONTENTS_TYPE_DESCRIPTION, "Баланс меньше нуля",
                                                              "Уведомление о проходе", "Зачисление средств", "Код активации","Уведомление о покупке",
                                                              "Уведомление о скором списании абон. платы за SMS-сервис",
                                                              "Уведомление о списании абон. платы за SMS-сервис","Уведомление о нехватке средств на субсчете"};
    public static final int TYPE_NEGATIVE_BALANCE = 1;
    public static final int TYPE_ENTER_EVENT_NOTIFY = 2;
    public static final int TYPE_PAYMENT_REGISTERED = 3;
    public static final int TYPE_LINKING_TOKEN = 4;
    public static final int TYPE_PAYMENT_NOTIFY= 5;
    public static final int TYPE_SMS_SUBSCRIPTION_FEE = 6;
    public static final int TYPE_SMS_SUB_FEE_WITHDRAW = 7;
    public static final int TYPE_SUBSCRIPTION_FEEDING = 8;
    public static final int TYPE_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS = 9;

    public static final int SENT_TO_SERVICE = 0;
    public static final int SEND_TO_RECIPENT = 1;
    public static final int DELIVERED_TO_RECIPENT = 2;
    public static final int NOT_DELIVERED_TO_RECIPENT = 3;
    public static final String UNKNOWN_DELIVERY_STATUS_DESCRIPTION = "Неизвестен";
    public static final String DELIVERY_STATUS_DESCRIPTION[] = {
            "Отправлено в SMS-шлюз", "Отправлено получателю", "Доставлено получателю", "Недоставлено получателю"};

    private String idOfSms;
    private long version;
    private Client client;
    private AccountTransaction transaction;
    private String phone;
    private Long contentsId;
    private Integer contentsType;
    private String textContents;
    private Integer deliveryStatus;
    private Date serviceSendTime;
    private Date sendTime;
    private Date deliveryTime;
    private Long price;
    private Date eventTime;
    private Long idOfOrg;

    protected ClientSms() {
        // For Hibernate only
    }

    /*ClientSms(String idOfSms, Client client, String phone, Long contentsId, Integer contentsType, String textContents,
            Date serviceSendTime, Long price) {
        this.idOfSms = idOfSms;
        this.client = client;
        this.phone = phone;
        this.contentsId = contentsId;
        this.contentsType = contentsType;
        this.textContents = textContents;
        this.deliveryStatus = SENT_TO_SERVICE;
        this.serviceSendTime = serviceSendTime;
        this.price = price;
    }*/

    public ClientSms(String idOfSms, Client client, AccountTransaction transaction, String phone, Long contentsId, Integer contentsType,
            String textContents, Date serviceSendTime, Long price, Date eventTime, Long idOfOrg) {
        this.idOfSms = idOfSms;
        this.transaction = transaction;
        this.client = client;
        this.phone = phone;
        this.contentsType = contentsType;
        this.textContents = textContents;
        this.deliveryStatus = SENT_TO_SERVICE;
        this.serviceSendTime = serviceSendTime;
        this.price = price;
        this.contentsId = contentsId;
        this.eventTime = eventTime;
        this.idOfOrg = idOfOrg;
    }

    public String getIdOfSms() {
        return idOfSms;
    }

    private void setIdOfSms(String idOfSms) {
        // For Hibernate only
        this.idOfSms = idOfSms;
    }

    public Long getVersion() {
        return version;
    }

    private void setVersion(Long version) {
        // For Hibernate only
        this.version = version;
    }

    public String getPhone() {
        return phone;
    }

    private void setPhone(String phone) {
        // For Hibernate only
        this.phone = phone;
    }

    public Integer getContentsType() {
        return contentsType;
    }

    private void setContentsType(Integer contentsType) {
        // For Hibernate only
        this.contentsType = contentsType;
    }

    public Long getContentsId() {
        return contentsId;
    }

    public void setContentsId(Long contentsId) {
        this.contentsId = contentsId;
    }

    public String getTextContents() {
        return textContents;
    }

    private void setTextContents(String textContents) {
        // For Hibernate only
        this.textContents = textContents;
    }

    public Integer getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(Integer deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Date getServiceSendTime() {
        return serviceSendTime;
    }

    private void setServiceSendTime(Date serviceSendTime) {
        // For Hibernate only
        this.serviceSendTime = serviceSendTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Client getClient() {
        return client;
    }

    private void setClient(Client client) {
        // For Hibernate only
        this.client = client;
    }

    public AccountTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(AccountTransaction accountTransaction) {
        // For Hibernate only
        this.transaction = accountTransaction;
    }

    public Long getPrice() {
        return price;
    }

    private void setPrice(Long price) {
        // For Hibernate only
        this.price = price;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientSms)) {
            return false;
        }
        final ClientSms clientSms = (ClientSms) o;
        if (!idOfSms.equals(clientSms.getIdOfSms())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return idOfSms.hashCode();
    }

    @Override
    public String toString() {
        return "ClientSms{" + "idOfSms='" + idOfSms + '\'' + ", version=" + version + ", client=" + client + ", phone='"
                + phone + '\'' + ", contentsType=" + contentsType + ", contentsId=" + contentsId + ", textContents='" + textContents + '\''
                + ", deliveryStatus=" + deliveryStatus + ", serviceSendTime=" + serviceSendTime + ", sendTime="
                + sendTime + ", deliveryTime=" + deliveryTime + ", price=" + price + '}';
    }
}