/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created by i.semenov on 15.11.2017.
 */
public class PaymentsBetweenContragentsReportItem {
    private String contragentName;
    private String shortNameInfoService;
    private String shortAddress;
    private String menuDetailName;
    private Long rprice;
    private Long qty;
    private Long sum;
    private Long sumDiscount;
    private String shortNameInfoServiceOrgClient;
    private String shortAddressOrgClient;
    private String contragentNameOrgClient;

    public PaymentsBetweenContragentsReportItem(String contragentName, String shortNameInfoService,
            String shortAddress, String menuDetailName, Long rprice, Long qty, Long sum, Long sumDiscount,
            String shortNameInfoServiceOrgClient, String shortAddressOrgClient, String contragentNameOrgClient) {
        this.contragentName = contragentName;
        this.shortNameInfoService = shortNameInfoService;
        this.shortAddress = shortAddress;
        this.menuDetailName = menuDetailName;
        this.rprice = rprice;
        this.qty = qty;
        this.sum = sum;
        this.sumDiscount = sumDiscount;
        this.shortNameInfoServiceOrgClient = shortNameInfoServiceOrgClient;
        this.shortAddressOrgClient = shortAddressOrgClient;
        this.contragentNameOrgClient = contragentNameOrgClient;
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getMenuDetailName() {
        return menuDetailName;
    }

    public void setMenuDetailName(String menuDetailName) {
        this.menuDetailName = menuDetailName;
    }

    public Long getRprice() {
        return rprice;
    }

    public void setRprice(Long rprice) {
        this.rprice = rprice;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public String getShortNameInfoServiceOrgClient() {
        return shortNameInfoServiceOrgClient;
    }

    public void setShortNameInfoServiceOrgClient(String shortNameInfoServiceOrgClient) {
        this.shortNameInfoServiceOrgClient = shortNameInfoServiceOrgClient;
    }

    public String getShortAddressOrgClient() {
        return shortAddressOrgClient;
    }

    public void setShortAddressOrgClient(String shortAddressOrgClient) {
        this.shortAddressOrgClient = shortAddressOrgClient;
    }

    public String getContragentNameOrgClient() {
        return contragentNameOrgClient;
    }

    public void setContragentNameOrgClient(String contragentNameOrgClient) {
        this.contragentNameOrgClient = contragentNameOrgClient;
    }

    public Long getSumDiscount() {
        return sumDiscount;
    }

    public void setSumDiscount(Long sumDiscount) {
        this.sumDiscount = sumDiscount;
    }
}
