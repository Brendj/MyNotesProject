/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

public class CardInfoItem{
    private Integer cardType;
    private Long cardPrintedNo;
    private Integer state;
    private Long cardNo;

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    public void setCardPrintedNo(Long cardPrintedNo) {
        this.cardPrintedNo = cardPrintedNo;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }
}
