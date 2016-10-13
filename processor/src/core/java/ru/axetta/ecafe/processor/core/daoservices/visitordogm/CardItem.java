/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.visitordogm;

import ru.axetta.ecafe.processor.core.persistence.CardTemp;
import ru.axetta.ecafe.processor.core.persistence.Visitor;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 12.10.16
 * Time: 10:37
 */
public class CardItem {

    private Long id;
    private Long cardNo;
    private String cardPrintedNo;
    private Date createDate;
    private VisitorItem visitorItem;

    public CardItem() {}

    public CardItem(CardTemp cardTemp) {
        this.id = cardTemp.getIdOfCartTemp();
        this.cardNo = cardTemp.getCardNo();
        this.cardPrintedNo = cardTemp.getCardPrintedNo();
        this.createDate = cardTemp.getCreateDate();
    }

    public CardItem(CardTemp cardTemp, Visitor visitor) {
        this.id = cardTemp.getIdOfCartTemp();
        this.cardNo = cardTemp.getCardNo();
        this.cardPrintedNo = cardTemp.getCardPrintedNo();
        this.createDate = cardTemp.getCreateDate();
        if(visitor!=null){
            this.visitorItem = new VisitorItem(visitor);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public String getCardPrintedNo() {
        return cardPrintedNo;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public VisitorItem getVisitorItem() {
        return visitorItem;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public void setCardPrintedNo(String cardPrintedNo) {
        this.cardPrintedNo = cardPrintedNo;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setVisitorItem(VisitorItem visitorItem) {
        this.visitorItem = visitorItem;
    }

}
