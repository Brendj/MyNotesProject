/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 29.03.16
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */
public class ZeroTransactionReportItem {
    private Integer num;
    private Long idOfOrg;
    private String orgShortName;
    private String district;
    private String address;
    private Date transactionDate;
    private Integer normInOut;
    private Integer factInOut;
    private String commentInOut;
    private Integer normDiscount;
    private Integer factDiscount;
    private String commentDiscount;
    private Integer normPaydable;
    private Integer factPaydable;
    private String commentPaydable;

    public ZeroTransactionReportItem(Integer num, Long idOfOrg, String orgShortName, String district, String address,
            Date transactionDate, Integer normInOut, Integer factInOut, String commentOnOut, Integer normDiscount,
            Integer factDiscount, String commentDiscount, Integer normPaydable, Integer factPaydable, String commentPaydable) {
        this.setNum(num);
        this.setIdOfOrg(idOfOrg);
        this.setOrgShortName(orgShortName);
        this.setDistrict(district);
        this.setAddress(address);
        this.setTransactionDate(transactionDate);
        this.setNormInOut(normInOut);
        this.setFactInOut(factInOut);
        this.setCommentInOut(commentOnOut);
        this.setNormDiscount(normDiscount);
        this.setFactDiscount(factDiscount);
        this.setCommentDiscount(commentDiscount);
        this.setNormPaydable(normPaydable);
        this.setFactPaydable(factPaydable);
        this.setCommentPaydable(commentPaydable);
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Integer getNormInOut() {
        return normInOut;
    }

    public void setNormInOut(Integer normInOut) {
        this.normInOut = normInOut;
    }

    public Integer getFactInOut() {
        return factInOut;
    }

    public void setFactInOut(Integer factInOut) {
        this.factInOut = factInOut;
    }

    public String getCommentInOut() {
        return commentInOut;
    }

    public void setCommentInOut(String commentInOut) {
        this.commentInOut = commentInOut;
    }

    public Integer getNormDiscount() {
        return normDiscount;
    }

    public void setNormDiscount(Integer normDiscount) {
        this.normDiscount = normDiscount;
    }

    public Integer getFactDiscount() {
        return factDiscount;
    }

    public void setFactDiscount(Integer factDiscount) {
        this.factDiscount = factDiscount;
    }

    public String getCommentDiscount() {
        return commentDiscount;
    }

    public void setCommentDiscount(String commentDiscount) {
        this.commentDiscount = commentDiscount;
    }

    public Integer getNormPaydable() {
        return normPaydable;
    }

    public void setNormPaydable(Integer normPaydable) {
        this.normPaydable = normPaydable;
    }

    public Integer getFactPaydable() {
        return factPaydable;
    }

    public void setFactPaydable(Integer factPaydable) {
        this.factPaydable = factPaydable;
    }

    public String getCommentPaydable() {
        return commentPaydable;
    }

    public void setCommentPaydable(String commentPaydable) {
        this.commentPaydable = commentPaydable;
    }
}
