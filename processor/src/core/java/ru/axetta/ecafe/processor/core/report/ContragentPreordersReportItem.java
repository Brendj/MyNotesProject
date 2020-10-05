/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.Date;

public class ContragentPreordersReportItem {
    private Long idOfContragent;
    private String contragentName;
    private Long idOfOrg;
    private String orgShortName;
    private String orgShortAddress;
    private Long clientContractId;
    private Date preorderDate;
    private String complexName;
    private Integer amount;
    private String dish;
    private Long complexPrice;
    private Date cancelDate;
    private String reversed;
    private Date createdDate;
    private Long orderSum;
    private String idOfOrder;
    private String isPaid;
    private Long usedSum;

    public ContragentPreordersReportItem(Long idOfContragent, String contragentName, Long idOfOrg, String orgShortName,
            String orgShortAddress, Long clientContractId, Date preorderDate, String complexName, Integer amount,
            String dish, Long complexPrice, Date cancelDate, String reversed, Date createdDate,
            Long orderSum, Long idOfOrder, String isPaid, Long usedSum) {
        this.idOfContragent = idOfContragent;
        this.contragentName = contragentName;
        this.idOfOrg = idOfOrg;
        this.orgShortName = orgShortName;
        this.orgShortAddress = orgShortAddress;
        this.clientContractId = clientContractId;
        this.preorderDate = preorderDate;
        this.complexName = complexName;
        this.amount = amount;
        this.dish = dish;
        this.complexPrice = complexPrice;
        this.cancelDate = cancelDate;
        this.reversed = reversed;
        this.createdDate = createdDate;
        this.orderSum = orderSum;
        this.idOfOrder = idOfOrder == null ? "" : idOfOrder.toString();
        this.isPaid = isPaid;
        this.usedSum = usedSum;
    }

    public Long getIdOfContragent() {
        return idOfContragent;
    }

    public void setIdOfContragent(Long idOfContragent) {
        this.idOfContragent = idOfContragent;
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
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

    public String getOrgShortAddress() {
        return orgShortAddress;
    }

    public void setOrgShortAddress(String orgShortAddress) {
        this.orgShortAddress = orgShortAddress;
    }

    public Long getClientContractId() {
        return clientContractId;
    }

    public void setClientContractId(Long clientContractId) {
        this.clientContractId = clientContractId;
    }

    public Date getPreorderDate() {
        return preorderDate;
    }

    public void setPreorderDate(Date preorderDate) {
        this.preorderDate = preorderDate;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public Long getComplexPrice() {
        return complexPrice;
    }

    public void setComplexPrice(Long complexPrice) {
        this.complexPrice = complexPrice;
    }

    public Date getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(Date cancelDate) {
        this.cancelDate = cancelDate;
    }

    public String getReversed() {
        return reversed;
    }

    public void setReversed(String reversed) {
        this.reversed = reversed;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getOrderSum() {
        return orderSum;
    }

    public void setOrderSum(Long orderSum) {
        this.orderSum = orderSum;
    }

    public String getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(String idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }

    public Long getUsedSum() {
        return usedSum;
    }

    public void setUsedSum(Long usedSum) {
        this.usedSum = usedSum;
    }
}
