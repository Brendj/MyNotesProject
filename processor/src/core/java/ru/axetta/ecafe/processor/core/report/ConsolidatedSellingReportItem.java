/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created by i.semenov on 12.02.2018.
 */
public class ConsolidatedSellingReportItem {
    private Integer number;
    private Long idOfOrg;
    private String orgName;
    private String district;
    private String address;
    private Long complexFood;
    private Long payFood;
    private Long buffetFood;
    private Long totalFood;
    private Long preorderFood;

    public ConsolidatedSellingReportItem(Long idOfOrg, String orgName, String district, String address, int number) {
        this.idOfOrg = idOfOrg;
        this.orgName = orgName;
        this.district = district;
        this.address = address;
        this.complexFood = 0L;
        this.payFood = 0L;
        this.buffetFood = 0L;
        this.totalFood = 0L;
        this.preorderFood = 0L;
        this.number = number;
    }

    public void addComplexFood(Long value) {
        this.complexFood += value;
    }

    public void addPayFood(Long value) {
        this.payFood += value;
    }

    public void addBufferFood(Long value) {
        this.buffetFood += value;
    }

    public void addPreorderFood(Long value) {
        this.preorderFood += value;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
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

    public Long getComplexFood() {
        return complexFood;
    }

    public void setComplexFood(Long complexFood) {
        this.complexFood = complexFood;
    }

    public Long getPayFood() {
        return payFood;
    }

    public void setPayFood(Long payFood) {
        this.payFood = payFood;
    }

    public Long getBuffetFood() {
        return buffetFood;
    }

    public void setBuffetFood(Long buffetFood) {
        this.buffetFood = buffetFood;
    }

    public Long getTotalFood() {
        return complexFood + payFood + buffetFood + preorderFood;
    }

    public void setTotalFood(Long totalFood) {
        this.totalFood = totalFood;
    }

    public Long getPreorderFood() {
        return preorderFood;
    }

    public void setPreorderFood(Long preorderFood) {
        this.preorderFood = preorderFood;
    }
}
