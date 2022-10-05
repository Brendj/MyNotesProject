package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import java.util.Date;

public class OrderItem {
    private Long idOfOrder;//
    private Long sumByCard;//
    private Long socDiscount;//
    private Long trdDiscount;//
    private Long grantSum;//
    private Long rSum;//
    private Long sumByCash;//
    private Long idOfOrg;//
    private Long idOfCard;//
    private Date createTime;//
    private int state;

    public OrderItem(Long idOfOrg, Long idOfOrder, Long idOfCard, Long sumByCard,
                     Long socDiscount, Long trdDiscount, Long grantSum,
                     Long rSum, Long sumByCash, Date createTime, int state) {
        this.idOfOrder = idOfOrder;
        this.sumByCard = sumByCard;
        this.socDiscount = socDiscount;
        this.trdDiscount = trdDiscount;
        this.grantSum = grantSum;
        this.rSum = rSum;
        this.sumByCash = sumByCash;
        this.idOfOrg = idOfOrg;
        this.idOfCard = idOfCard;
        this.createTime = createTime;
        this.state = state;
    }

    public Long getSumByCard() {
        return sumByCard;
    }

    public void setSumByCard(Long sumByCard) {
        this.sumByCard = sumByCard;
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

    public Long getGrantSum() {
        return grantSum;
    }

    public void setGrantSum(Long grantSum) {
        this.grantSum = grantSum;
    }

    public Long getrSum() {
        return rSum;
    }

    public void setrSum(Long rSum) {
        this.rSum = rSum;
    }

    public Long getSumByCash() {
        return sumByCash;
    }

    public void setSumByCash(Long sumByCash) {
        this.sumByCash = sumByCash;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }
}
