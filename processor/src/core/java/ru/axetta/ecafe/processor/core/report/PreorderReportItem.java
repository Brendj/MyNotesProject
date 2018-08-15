/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.Date;

/**
 * Created by i.semenov on 11.04.2018.
 */
public class PreorderReportItem {
    private Long idOfContragent;
    private String contragentName;
    private Long idOfOrg;
    private String shortNameInfoService;
    private String address;
    private Long contractId;
    private String surname;
    private String firstname;
    private String secondname;
    private Date preorderDate;
    private Integer amountComplex;
    private String complexName;
    private Long idOfPreorderComplex;
    private String menuDetailName;
    private Integer amountMenuDetail;
    private Boolean isRegularPreorder;

    public PreorderReportItem(Long idOfContragent, String contragentName, Long idOfOrg, String shortNameInfoService,
            String address, Long contractId, String surname, String firstname, String secondname, Date preorderDate,
            Integer amountComplex, String complexName, Long idOfPreorderComplex, String menuDetailName,
            Integer amountMenuDetail, Boolean isRegularPreorder) {
        this.idOfContragent = idOfContragent;
        this.contragentName = contragentName;
        this.idOfOrg = idOfOrg;
        this.shortNameInfoService = shortNameInfoService;
        this.address = address;
        this.contractId = contractId;
        this.surname = surname;
        this.firstname = firstname;
        this.secondname = secondname;
        this.preorderDate = preorderDate;
        this.amountComplex = amountComplex;
        this.complexName = complexName;
        this.idOfPreorderComplex = idOfPreorderComplex;
        this.menuDetailName = menuDetailName;
        this.amountMenuDetail = amountMenuDetail;
        this.isRegularPreorder = isRegularPreorder;
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

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public Date getPreorderDate() {
        return preorderDate;
    }

    public void setPreorderDate(Date preorderDate) {
        this.preorderDate = preorderDate;
    }

    public Integer getAmountComplex() {
        return amountComplex;
    }

    public void setAmountComplex(Integer amountComplex) {
        this.amountComplex = amountComplex;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Long getIdOfPreorderComplex() {
        return idOfPreorderComplex;
    }

    public void setIdOfPreorderComplex(Long idOfPreorderComplex) {
        this.idOfPreorderComplex = idOfPreorderComplex;
    }

    public String getMenuDetailName() {
        return menuDetailName;
    }

    public void setMenuDetailName(String menuDetailName) {
        this.menuDetailName = menuDetailName;
    }

    public Integer getAmountMenuDetail() {
        return amountMenuDetail;
    }

    public void setAmountMenuDetail(Integer amountMenuDetail) {
        this.amountMenuDetail = amountMenuDetail;
    }

    public Boolean getIsRegularPreorder() {
        return isRegularPreorder;
    }

    public void setIsRegularPreorder(Boolean regularPreorder) {
        isRegularPreorder = regularPreorder;
    }
}
