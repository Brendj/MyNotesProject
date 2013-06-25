/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import ru.axetta.ecafe.processor.core.logic.ClientManager;

import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.06.13
 * Time: 12:55
 * To change this template use File | Settings | File Templates.
 */
public class ClientDesc {
    protected int recId;
    protected String contractSurname;
    protected String contractName;
    protected String contractSecondName;
    protected String contractDoc;
    protected String surname;
    protected String name;
    protected String secondName;
    protected String doc;
    protected String address;
    protected String phone;
    protected String mobilePhone;
    protected String email;
    protected String group;
    protected String snils;
    protected boolean notifyBySms;
    protected boolean notifyByEmail;
    protected String comments;
    protected Long cardNo;
    protected Long cardPrintedNo;
    protected int cardType;
    protected Date cardExpiry;
    protected Date cardIssued;

    public static ClientManager.ClientFieldConfig buildClientFieldConfig(ClientDesc cd) throws Exception{
        ClientManager.ClientFieldConfig fc = new ClientManager.ClientFieldConfig();
        if (cd.getContractSurname()!=null) {
            fc.setValue(ClientManager.FieldId.CONTRACT_SURNAME, cd.contractSurname);
        }  else {
            fc.setValue(ClientManager.FieldId.CONTRACT_SURNAME, " ");
        }
        if (cd.contractName!=null) fc.setValue(ClientManager.FieldId.CONTRACT_NAME, cd.contractName);
        if (cd.contractSecondName!=null) fc.setValue(ClientManager.FieldId.CONTRACT_SECONDNAME, cd.contractSecondName);
        if (cd.contractDoc!=null) fc.setValue(ClientManager.FieldId.CONTRACT_DOC, cd.contractDoc);
        if (cd.surname!=null) fc.setValue(ClientManager.FieldId.SURNAME, cd.surname);
        if (cd.name!=null) fc.setValue(ClientManager.FieldId.NAME, cd.name);
        if (cd.secondName!=null) fc.setValue(ClientManager.FieldId.SECONDNAME, cd.secondName);
        if (cd.doc!=null) fc.setValue(ClientManager.FieldId.DOC, cd.doc);
        if (cd.address!=null) fc.setValue(ClientManager.FieldId.ADDRESS, cd.address);
        if (cd.phone!=null) fc.setValue(ClientManager.FieldId.PHONE, cd.phone);
        if (cd.mobilePhone!=null) fc.setValue(ClientManager.FieldId.MOBILE_PHONE, cd.mobilePhone);
        if (cd.email!=null) fc.setValue(ClientManager.FieldId.EMAIL, cd.email);
        if (cd.group!=null) fc.setValue(ClientManager.FieldId.GROUP, cd.group);
        fc.setValue(ClientManager.FieldId.NOTIFY_BY_SMS, cd.notifyBySms?"1":"0");
        fc.setValue(ClientManager.FieldId.NOTIFY_BY_EMAIL, cd.notifyByEmail?"1":"0");
        if (cd.comments!=null) fc.setValue(ClientManager.FieldId.COMMENTS, cd.comments);
        if (cd.cardNo!=null) fc.setValue(ClientManager.FieldId.CARD_ID, cd.cardNo);
        if (cd.cardPrintedNo!=null) fc.setValue(ClientManager.FieldId.CARD_PRINTED_NUM, cd.cardPrintedNo);
        fc.setValue(ClientManager.FieldId.CARD_TYPE, cd.cardType);
        if (cd.cardExpiry!=null) fc.setValue(ClientManager.FieldId.CARD_EXPIRY, cd.cardExpiry);
        if (cd.cardIssued!=null) fc.setValue(ClientManager.FieldId.CARD_ISSUED, cd.cardIssued);
        if (cd.snils!=null) fc.setValue(ClientManager.FieldId.SAN, cd.snils);
                /* Генерируем GUID клиента при регистрации  */
        fc.setValue(ClientManager.FieldId.CLIENT_GUID, UUID.randomUUID().toString());
        return fc;
    }

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
    }

    public String getContractSurname() {
        return contractSurname;
    }

    public void setContractSurname(String contractSurname) {
        this.contractSurname = contractSurname;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractSecondName() {
        return contractSecondName;
    }

    public void setContractSecondName(String contractSecondName) {
        this.contractSecondName = contractSecondName;
    }

    public String getContractDoc() {
        return contractDoc;
    }

    public void setContractDoc(String contractDoc) {
        this.contractDoc = contractDoc;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public boolean isNotifyBySms() {
        return notifyBySms;
    }

    public void setNotifyBySms(boolean notifyBySms) {
        this.notifyBySms = notifyBySms;
    }

    public boolean isNotifyByEmail() {
        return notifyByEmail;
    }

    public void setNotifyByEmail(boolean notifyByEmail) {
        this.notifyByEmail = notifyByEmail;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    public void setCardPrintedNo(Long cardPrintedNo) {
        this.cardPrintedNo = cardPrintedNo;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public Date getCardExpiry() {
        return cardExpiry;
    }

    public void setCardExpiry(Date cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

    public Date getCardIssued() {
        return cardIssued;
    }

    public void setCardIssued(Date cardIssued) {
        this.cardIssued = cardIssued;
    }

    protected ClientDesc() {
    }
}
