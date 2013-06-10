/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.05.13
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
public class CardTemp {

    private Long cardNo;
    private Long idOfOrg;
    private String cardPrintedNo;
    private long idOfCartTemp;

    protected CardTemp() {}

    public CardTemp(Long idOfOrg, Long cardNo,  String cardPrintedNo) {
        this.cardNo = cardNo;
        this.idOfOrg = idOfOrg;
        this.cardPrintedNo = cardPrintedNo;
    }

    public long getIdOfCartTemp() {
        return idOfCartTemp;
    }

    void setIdOfCartTemp(long idOfCartTemp) {
        this.idOfCartTemp = idOfCartTemp;
    }

    public String getCardPrintedNo() {
        return cardPrintedNo;
    }

    void setCardPrintedNo(String cardPrintedNo) {
        this.cardPrintedNo = cardPrintedNo;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getCardNo() {
        return cardNo;
    }

    void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }
}
