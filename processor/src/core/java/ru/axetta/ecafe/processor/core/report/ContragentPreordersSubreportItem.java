/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

public class ContragentPreordersSubreportItem{
    private Long idOfContragent;
    private String contragentName;
    private Long idOfOrg;
    private String orgShortName;
    private String orgShortAddress;
    private String complexName;
    private Integer amount;
    private Long complexPrice;
    private Long usedSum;
    private String complexPriceVision;

    public ContragentPreordersSubreportItem(Long idOfContragent, String contragentName, Long idOfOrg,
            String orgShortName, String orgShortAddress, String complexName, Integer amount, Long complexPrice,
            Long usedSum, String complexPriceVision) {
        this.idOfContragent = idOfContragent;
        this.contragentName = contragentName;
        this.idOfOrg = idOfOrg;
        this.orgShortName = orgShortName;
        this.orgShortAddress = orgShortAddress;
        this.complexName = complexName;
        this.amount = amount;
        this.complexPrice = complexPrice;
        this.usedSum = usedSum;
        this.complexPriceVision = complexPriceVision;
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

    public Long getComplexPrice() {
        return complexPrice;
    }

    public void setComplexPrice(Long complexPrice) {
        this.complexPrice = complexPrice;
    }

    public Long getUsedSum() {
        return usedSum;
    }

    public void setUsedSum(Long usedSum) {
        this.usedSum = usedSum;
    }

    public String getComplexPriceVision() {
        return complexPriceVision;
    }

    public void setComplexPriceVision(String complexPriceVision) {
        this.complexPriceVision = complexPriceVision;
    }
}
