/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;


import java.util.ArrayList;
import java.util.List;

public class PreorderReportClientItem implements Comparable<PreorderReportClientItem> {
    private Long idOfOrg;
    private String shortNameInfoService;
    private String address;
    private Long contractId;
    private String clientName;
    private String clientGroup;
    private Integer allAmount;
    private Long allPreorderSum;
    private Long allDiscount;
    private Long allTotalPrice;
    private List<PreorderReportItem> preorderItems;

    public PreorderReportClientItem(Long idOfOrg, String shortNameInfoService, String address, Long contractId,
            String clientName, String clientGroup) {
        this.idOfOrg = idOfOrg;
        this.shortNameInfoService = shortNameInfoService;
        this.address = address;
        this.contractId = contractId;
        this.clientName = clientName;
        this.clientGroup = clientGroup;
        preorderItems = new ArrayList<PreorderReportItem>();
    }

    public void calculateTotalValues() {
        this.allAmount = 0;
        this.allPreorderSum = 0L;
        this.allDiscount = 0L;
        this.allTotalPrice = 0L;
        for (PreorderReportItem item : preorderItems) {
            this.allAmount += getIntSafe(item.getAmount());
            this.allPreorderSum += getLongSafe(item.getPreorderSum());
            this.allDiscount += getLongSafe(item.getDiscount());
            this.allTotalPrice += getLongSafe(item.getTotalPrice());
        }
    }

    private Long getLongSafe(Long value) {
        return (null == value) ? 0L : value;
    }

    private Integer getIntSafe(Integer value) {
        return (null == value) ? 0 : value;
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

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(String clientGroup) {
        this.clientGroup = clientGroup;
    }

    public List<PreorderReportItem> getPreorderItems() {
        return preorderItems;
    }

    public void setPreorderItems(List<PreorderReportItem> preorderItems) {
        this.preorderItems = preorderItems;
    }

    public Integer getAllAmount() {
        return allAmount;
    }

    public void setAllAmount(Integer allAmount) {
        this.allAmount = allAmount;
    }

    public Long getAllPreorderSum() {
        return allPreorderSum;
    }

    public void setAllPreorderSum(Long allPreorderSum) {
        this.allPreorderSum = allPreorderSum;
    }

    public Long getAllDiscount() {
        return allDiscount;
    }

    public void setAllDiscount(Long allDiscount) {
        this.allDiscount = allDiscount;
    }

    public Long getAllTotalPrice() {
        return allTotalPrice;
    }

    public void setAllTotalPrice(Long allTotalPrice) {
        this.allTotalPrice = allTotalPrice;
    }

    @Override
    public int compareTo(PreorderReportClientItem item) {
        int result = getIntFromGroupName(this.clientGroup).compareTo(getIntFromGroupName(item.getClientGroup()));
        if (0 == result) {
            result = this.clientName.compareTo(item.getClientName());
        }
        return result;
    }

    public Integer getIntFromGroupName(String clientGroup) {
        String number = clientGroup.replaceAll("\\D", "");
        return number.isEmpty() ? 0 : Integer.parseInt(number);
    }
}
