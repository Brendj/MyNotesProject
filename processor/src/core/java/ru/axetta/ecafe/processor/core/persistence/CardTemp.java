/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class CardTemp {

    private Long idOfCartTemp;
    private Long cardNo;
    private Org org;
    private String cardPrintedNo;
    private CardOperationStation cardStation;
    private Date createDate;
    private Date validDate;
    private Client client;
    private Visitor visitor;
    private Long longCardNo;
    //private ClientTypeEnum clientTypeEnum;
    private int visitorType;

    /* Конструктор регистрации врекменой карты, но не выдавая его ни кому */
    public CardTemp(Org org, Long cardNo,  String cardPrintedNo) {
        this.cardNo = cardNo;
        this.org = org;
        this.cardPrintedNo = cardPrintedNo;
        this.cardStation = CardOperationStation.REGISTRATION;
        this.createDate = new Date();
        //this.clientTypeEnum = ClientTypeEnum.CLIENT;
        this.visitorType = 0;
    }

    public CardTemp(Long cardNo,  String cardPrintedNo, Integer clientType) {
        this.cardNo = cardNo;
        this.cardPrintedNo = cardPrintedNo;
        this.cardStation = CardOperationStation.REGISTRATION;
        this.createDate = new Date();
        //this.clientTypeEnum = clientType;
        this.visitorType = clientType;
    }

    public CardTemp(Long cardNo,  String cardPrintedNo,CardOperationStation cardOperationStation,  Integer clientType) {
        this.cardNo = cardNo;
        this.cardPrintedNo = cardPrintedNo;
        this.cardStation = cardOperationStation;
        this.createDate = new Date();
        //this.clientTypeEnum = clientType;
        this.visitorType = clientType;
    }

    public Long getIdOfCartTemp() {
        return idOfCartTemp;
    }

    public void setIdOfCartTemp(Long idOfCartTemp) {
        this.idOfCartTemp = idOfCartTemp;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getCardPrintedNo() {
        return cardPrintedNo;
    }

    public void setCardPrintedNo(String cardPrintedNo) {
        this.cardPrintedNo = cardPrintedNo;
    }

    public CardOperationStation getCardStation() {
        return cardStation;
    }

    public void setCardStation(CardOperationStation cardStation) {
        this.cardStation = cardStation;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getValidDate() {
        return validDate;
    }

    public void setValidDate(Date closeDate) {
        this.validDate = closeDate;
    }

    //public ClientTypeEnum getClientTypeEnum() {
    //    return clientTypeEnum;
    //}
    //
    //public void setClientTypeEnum(ClientTypeEnum clientTypeEnum) {
    //    this.clientTypeEnum = clientTypeEnum;
    //}


    public Long getLongCardNo() {
        return longCardNo;
    }

    public void setLongCardNo(Long longCardNo) {
        this.longCardNo = longCardNo;
    }

    public int getVisitorType() {
        return visitorType;
    }

    public void setVisitorType(int visitorType) {
        this.visitorType = visitorType;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        //this.clientTypeEnum =ClientTypeEnum.CLIENT;
        this.visitorType = 0;
        this.visitor = null;
        this.client = client;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.client=null;
        this.visitor = visitor;
    }

    protected CardTemp() {}

    @Override
    public String toString() {
        return "CardTemp{" +
                "id=" + idOfCartTemp +
                ", cardNo=" + cardNo +
                (org==null?"":", org=" + org) +
                ", customerType=" + visitorType +
                (client==null?"":", client=" + client) +
                (visitor==null?"":", visitor=" + visitor) +
                ", cardPrintedNo='" + cardPrintedNo + '\'' +
                '}';
    }
}