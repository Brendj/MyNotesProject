/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 22.07.16
 * Time: 10:37
 */
public class CardPrintedNoItem {
    private Long cardPrintedNo;
    private String cardType;

    public CardPrintedNoItem() {
    }

    public CardPrintedNoItem(Long cardPrintedNo, String cardType) {
        this.cardPrintedNo = cardPrintedNo;
        this.cardType = cardType;
    }

    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    public void setCardPrintedNo(Long cardPrintedNo) {
        this.cardPrintedNo = cardPrintedNo;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
