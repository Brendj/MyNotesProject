
/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PosPayment", propOrder = {
        "purchases"
})
public class PosPayment {

    @XmlAttribute(name = "CardNo")
    protected Long cardNo;
    @XmlAttribute(name = "Time")
    @XmlSchemaType(name = "dateTime")
    protected Date time;
    @XmlAttribute(name = "OrderDate")
    @XmlSchemaType(name = "dateTime")
    protected Date orderDate;
    @XmlAttribute(name = "SocDiscount")
    protected Long socDiscount;
    @XmlAttribute(name = "TrdDiscount")
    protected Long trdDiscount;
    @XmlAttribute(name = "ConfirmerId")
    protected Long confirmerId;
    @XmlAttribute(name = "Grant")
    protected Long grant;
    @XmlAttribute(name = "IdOfClient")
    protected Long idOfClient;
    @XmlAttribute(name = "IdOfPayForClient")
    protected Long idOfPayForClient;
    @XmlAttribute(name = "IdOfOrder")
    protected Long idOfOrder;
    @XmlAttribute(name = "IdOfCashier")
    protected Long idOfCashier;
    @XmlAttribute(name = "SumByCard")
    protected Long sumByCard;
    @XmlAttribute(name = "SumByCash")
    protected Long sumByCash;
    @XmlAttribute(name = "RSum")
    protected Long rSum;
    @XmlAttribute(name = "IdOfPOS")
    protected Long idOfPOS;
    @XmlAttribute(name = "Comments")
    protected String comments;
    @XmlAttribute(name = "OrderType")
    protected Integer orderType;
    @XmlElement(name = "Purchases")
    protected List<PosPurchase> purchases;


    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Long getSocDiscount() {
        return socDiscount;
    }

    public void setSocDiscount(Long socDiscount) {
        this.socDiscount = socDiscount;
    }

    public Long getTrdDiscount() {
        return trdDiscount;
    }

    public void setTrdDiscount(Long trdDiscount) {
        this.trdDiscount = trdDiscount;
    }

    public Long getConfirmerId() {
        return confirmerId;
    }

    public void setConfirmerId(Long confirmerId) {
        this.confirmerId = confirmerId;
    }

    public Long getGrant() {
        return grant;
    }

    public void setGrant(Long grant) {
        this.grant = grant;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfPayForClient() {
        return idOfPayForClient;
    }

    public void setIdOfPayForClient(Long idOfPayForClient) {
        this.idOfPayForClient = idOfPayForClient;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public Long getIdOfCashier() {
        return idOfCashier;
    }

    public void setIdOfCashier(Long idOfCashier) {
        this.idOfCashier = idOfCashier;
    }

    public Long getSumByCard() {
        return sumByCard;
    }

    public void setSumByCard(Long sumByCard) {
        this.sumByCard = sumByCard;
    }

    public Long getSumByCash() {
        return sumByCash;
    }

    public void setSumByCash(Long sumByCash) {
        this.sumByCash = sumByCash;
    }

    public Long getrSum() {
        return rSum;
    }

    public void setrSum(Long rSum) {
        this.rSum = rSum;
    }

    public Long getIdOfPOS() {
        return idOfPOS;
    }

    public void setIdOfPOS(Long idOfPOS) {
        this.idOfPOS = idOfPOS;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public List<PosPurchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<PosPurchase> purchases) {
        this.purchases = purchases;
    }
}
