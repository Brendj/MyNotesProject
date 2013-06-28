/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class CardTemp {

    private Long idOfCartTemp;
    private Long cardNo;
    private Org org;
    private String cardPrintedNo;
    private CardOperationStation cardStation;
    private Date createDate;
    private Date closeDate;
    private Integer customerType;
    private Client client;
    private Visitor visitor;

    /* Конструктор регистрации врекменой карты, но не выдавая его ни кому */
    public CardTemp(Org org, Long cardNo,  String cardPrintedNo) {
        this.cardNo = cardNo;
        this.org = org;
        this.cardPrintedNo = cardPrintedNo;
        this.cardStation = CardOperationStation.REGISTRATION;
        this.createDate = new Date();
        this.customerType = 0;
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

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Integer getCustomerType() {
        return customerType;
    }

    public void setCustomerType(Integer customerType) {
        this.customerType = customerType;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    protected CardTemp() {}
}