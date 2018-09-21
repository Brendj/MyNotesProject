/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;


import java.util.ArrayList;
import java.util.Date;
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
    private List<PreorderReportComplexItem> preorderComplexItems;

    public PreorderReportClientItem(Long idOfOrg, String shortNameInfoService, String address, Long contractId,
            String clientName, String clientGroup) {
        this.idOfOrg = idOfOrg;
        this.shortNameInfoService = shortNameInfoService;
        this.address = address;
        this.contractId = contractId;
        this.clientName = clientName;
        this.clientGroup = clientGroup;
        preorderComplexItems = new ArrayList<PreorderReportComplexItem>();
    }

    public void calculateTotalValues() {
        this.allAmount = 0;
        this.allPreorderSum = 0L;
        for (PreorderReportComplexItem item : preorderComplexItems) {
            item.calculateTotalPrice();
            this.allAmount += getIntSafe(item.getAmount());
            this.allPreorderSum += getLongSafe(item.getPreorderSum());
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

    public List<PreorderReportComplexItem> getPreorderComplexItems() {
        return preorderComplexItems;
    }

    public void setPreorderComplexItems(List<PreorderReportComplexItem> preorderComplexItems) {
        this.preorderComplexItems = preorderComplexItems;
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

    @Override
    public int compareTo(PreorderReportClientItem item) {
        int result = getIntFromGroupName(this.clientGroup).compareTo(getIntFromGroupName(item.getClientGroup()));
        if (0 == result) {
            result = getLetterFromGroupName(this.clientGroup).compareTo(getLetterFromGroupName(item.getClientGroup()));
            if (0 == result) {
                result = this.clientName.compareTo(item.getClientName());
            }
        }
        return result;
    }

    public Integer getIntFromGroupName(String clientGroup) {
        String number = clientGroup.replaceAll("\\D", "");
        return number.isEmpty() ? 0 : Integer.parseInt(number);
    }

    public String getLetterFromGroupName(String clientGroup) {
        return clientGroup.replaceAll("[^А-Я]+", "");
    }

    public Boolean isComplexExists(Date preorderDate, String complexName) {
        for (PreorderReportComplexItem item : this.preorderComplexItems) {
            if (item.getPreorderDate().equals(preorderDate) && item.getPreorderName().equals(complexName)) {
                return true;
            }
        }
        return false;
    }
}
