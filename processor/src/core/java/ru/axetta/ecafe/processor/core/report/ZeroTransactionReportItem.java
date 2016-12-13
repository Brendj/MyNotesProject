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

    private Integer normDiscountLowGrade;
    private Integer factDiscountLowGrade;
    private String commentDiscountLowGrade;

    private Integer normDiscountMiddleEightGrade;
    private Integer factDiscountMiddleEightGrade;
    private String commentDiscountMiddleEightGrade;

    private Integer normPaydableChildren;
    private Integer factPaydableChildren;
    private String commentPaydableChildren;

    private Integer normPaydableNotChildren;
    private Integer factPaydableNotChildren;
    private String commentPaydableNotChildren;

    private Integer normBuffet;
    private Integer factBuffet;
    private String commentBuffet;

    private Integer goalSumBuffet;

    public ZeroTransactionReportItem() {
    }

    public ZeroTransactionReportItem(Integer num, Long idOfOrg, String orgShortName, String district, String address,
            Date transactionDate, Integer normInOut, Integer factInOut, String commentInOut,
            Integer normDiscountLowGrade, Integer factDiscountLowGrade, String commentDiscountLowGrade,
            Integer normDiscountMiddleEightGrade, Integer factDiscountMiddleEightGrade,
            String commentDiscountMiddleEightGrade, Integer normPaydableChildren, Integer factPaydableChildren,
            String commentPaydableChildren, Integer normPaydableNotChildren, Integer factPaydableNotChildren,
            String commentPaydableNotChildren, Integer normBuffet, Integer factBuffet, String commentBuffet, Integer goalSumBuffet) {
        this.num = num;
        this.idOfOrg = idOfOrg;
        this.orgShortName = orgShortName;
        this.district = district;
        this.address = address;
        this.transactionDate = transactionDate;
        this.normInOut = normInOut;
        this.factInOut = factInOut;
        this.commentInOut = commentInOut;
        this.normDiscountLowGrade = normDiscountLowGrade;
        this.factDiscountLowGrade = factDiscountLowGrade;
        this.commentDiscountLowGrade = commentDiscountLowGrade;
        this.normDiscountMiddleEightGrade = normDiscountMiddleEightGrade;
        this.factDiscountMiddleEightGrade = factDiscountMiddleEightGrade;
        this.commentDiscountMiddleEightGrade = commentDiscountMiddleEightGrade;
        this.normPaydableChildren = normPaydableChildren;
        this.factPaydableChildren = factPaydableChildren;
        this.commentPaydableChildren = commentPaydableChildren;
        this.normPaydableNotChildren = normPaydableNotChildren;
        this.factPaydableNotChildren = factPaydableNotChildren;
        this.commentPaydableNotChildren = commentPaydableNotChildren;
        this.normBuffet = normBuffet;
        this.factBuffet = factBuffet;
        this.commentBuffet = commentBuffet;
        this.goalSumBuffet = goalSumBuffet;
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

    public Integer getNormDiscountLowGrade() {
        return normDiscountLowGrade;
    }

    public void setNormDiscountLowGrade(Integer normDiscountLowGrade) {
        this.normDiscountLowGrade = normDiscountLowGrade;
    }

    public Integer getFactDiscountLowGrade() {
        return factDiscountLowGrade;
    }

    public void setFactDiscountLowGrade(Integer factDiscountLowGrade) {
        this.factDiscountLowGrade = factDiscountLowGrade;
    }

    public String getCommentDiscountLowGrade() {
        return commentDiscountLowGrade;
    }

    public void setCommentDiscountLowGrade(String commentDiscountLowGrade) {
        this.commentDiscountLowGrade = commentDiscountLowGrade;
    }

    public Integer getNormDiscountMiddleEightGrade() {
        return normDiscountMiddleEightGrade;
    }

    public void setNormDiscountMiddleEightGrade(Integer normDiscountMiddleEightGrade) {
        this.normDiscountMiddleEightGrade = normDiscountMiddleEightGrade;
    }

    public Integer getFactDiscountMiddleEightGrade() {
        return factDiscountMiddleEightGrade;
    }

    public void setFactDiscountMiddleEightGrade(Integer factDiscountMiddleEightGrade) {
        this.factDiscountMiddleEightGrade = factDiscountMiddleEightGrade;
    }

    public String getCommentDiscountMiddleEightGrade() {
        return commentDiscountMiddleEightGrade;
    }

    public void setCommentDiscountMiddleEightGrade(String commentDiscountMiddleEightGrade) {
        this.commentDiscountMiddleEightGrade = commentDiscountMiddleEightGrade;
    }

    public Integer getNormPaydableChildren() {
        return normPaydableChildren;
    }

    public void setNormPaydableChildren(Integer normPaydableChildren) {
        this.normPaydableChildren = normPaydableChildren;
    }

    public Integer getFactPaydableChildren() {
        return factPaydableChildren;
    }

    public void setFactPaydableChildren(Integer factPaydableChildren) {
        this.factPaydableChildren = factPaydableChildren;
    }

    public String getCommentPaydableChildren() {
        return commentPaydableChildren;
    }

    public void setCommentPaydableChildren(String commentPaydableChildren) {
        this.commentPaydableChildren = commentPaydableChildren;
    }

    public Integer getNormPaydableNotChildren() {
        return normPaydableNotChildren;
    }

    public void setNormPaydableNotChildren(Integer normPaydableNotChildren) {
        this.normPaydableNotChildren = normPaydableNotChildren;
    }

    public Integer getFactPaydableNotChildren() {
        return factPaydableNotChildren;
    }

    public void setFactPaydableNotChildren(Integer factPaydableNotChildren) {
        this.factPaydableNotChildren = factPaydableNotChildren;
    }

    public String getCommentPaydableNotChildren() {
        return commentPaydableNotChildren;
    }

    public void setCommentPaydableNotChildren(String commentPaydableNotChildren) {
        this.commentPaydableNotChildren = commentPaydableNotChildren;
    }

    public Integer getNormBuffet() {
        return normBuffet;
    }

    public void setNormBuffet(Integer normBuffet) {
        this.normBuffet = normBuffet;
    }

    public Integer getFactBuffet() {
        return factBuffet;
    }

    public void setFactBuffet(Integer factBuffet) {
        this.factBuffet = factBuffet;
    }

    public String getCommentBuffet() {
        return commentBuffet;
    }

    public void setCommentBuffet(String commentBuffet) {
        this.commentBuffet = commentBuffet;
    }

    public Integer getGoalSumBuffet() {
        return goalSumBuffet;
    }

    public void setGoalSumBuffet(Integer goalSumBuffet) {
        this.goalSumBuffet = goalSumBuffet;
    }
}
