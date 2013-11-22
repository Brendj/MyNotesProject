package ru.axetta.ecafe.processor.core.daoservices.employees;

import ru.axetta.ecafe.processor.core.persistence.CardOperationStation;
import ru.axetta.ecafe.processor.core.persistence.CardTemp;
import ru.axetta.ecafe.processor.core.persistence.ClientTypeEnum;
import ru.axetta.ecafe.processor.core.persistence.Visitor;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.13
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public class CardItem {

    private Long id;
    private Long cardNo;
    private String cardPrintedNo;
    private CardOperationStation cardStation;
    private Date createDate;
    private Date validDate;
    private VisitorItem visitorItem;

    public CardItem() {}

    public CardItem(CardTemp cardTemp) {
        this.id = cardTemp.getIdOfCartTemp();
        this.cardNo = cardTemp.getCardNo();
        this.cardPrintedNo = cardTemp.getCardPrintedNo();
        this.cardStation = cardTemp.getCardStation();
        this.createDate = cardTemp.getCreateDate();
        this.validDate = cardTemp.getValidDate();
    }

    public CardItem(CardTemp cardTemp, Visitor visitor) {
        this.id = cardTemp.getIdOfCartTemp();
        this.cardNo = cardTemp.getCardNo();
        this.cardPrintedNo = cardTemp.getCardPrintedNo();
        this.cardStation = cardTemp.getCardStation();
        this.createDate = cardTemp.getCreateDate();
        this.validDate = cardTemp.getValidDate();
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

    public CardOperationStation getCardStation() {
        return cardStation;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getValidDate() {
        return validDate;
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

    public void setValidDate(Date validDate) {
        this.validDate = validDate;
    }

    public void setVisitorItem(VisitorItem visitorItem) {
        this.visitorItem = visitorItem;
    }

    public void setCardStation(CardOperationStation cardStation) {
        this.cardStation = cardStation;
    }
}
