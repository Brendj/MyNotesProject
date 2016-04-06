/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by Liya on 01.04.2016.
 */
public class NewCard {

    private Long idOfNewCard;
    private Date createTime;
    private Long cardNo;
    private Long cardPrintedNo;

    public NewCard() {
    }

    public NewCard(Long cardNo, Long cardPrintedNo) {
        this.cardNo = cardNo;
        this.cardPrintedNo = cardPrintedNo;
        this.createTime = new Date();
    }

    public Long getIdOfNewCard() {
        return idOfNewCard;
    }

    public void setIdOfNewCard(Long idOfNewCard) {
        this.idOfNewCard = idOfNewCard;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NewCard that = (NewCard) o;

        if (!idOfNewCard.equals(that.idOfNewCard)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return idOfNewCard.hashCode();
    }

    @Override
    public String toString() {
        return "NewCard{" +
                "idOfNewCard=" + idOfNewCard +
                ", createTime=" + createTime +
                ", cardNo=" + cardNo +
                ", cardPrintedNo=" + cardPrintedNo +
                '}';
    }
}
