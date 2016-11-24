/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.04.13
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public class OrgItem {

    private Long idOfOrg;
    private String shortName;
    private Long idOfOrgMain;
    private String shortNameMain;
    private String contractId;
    private Integer state;
    private String phone;
    private String tag;
    private String city;
    private String district;
    private String shortAddress;
    private String location;

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Long getIdOfOrgMain() {
        return idOfOrgMain;
    }

    public void setIdOfOrgMain(Long idOfOrgMain) {
        this.idOfOrgMain = idOfOrgMain;
    }

    public String getShortNameMain() {
        return shortNameMain;
    }

    public void setShortNameMain(String shortNameMain) {
        this.shortNameMain = shortNameMain;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
