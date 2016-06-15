/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.security;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationSecurityLevel;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 10.06.16
 * Time: 17:30
 * To change this template use File | Settings | File Templates.
 */
public class OrgSecurityItem {
    private Long idOfOrg;
    private String shortName;
    private String shortNameInfoService;
    private String officialName;
    private String inn;
    private String address;
    private String guid;
    private String city;
    private String district;
    private OrganizationType type;
    private Integer state;
    private String statusDetailing;
    private OrganizationSecurityLevel securityLevel;

    public OrgSecurityItem() {

    }

    public OrgSecurityItem(Org org) {
        this.setIdOfOrg(org.getIdOfOrg());
        this.setShortName(org.getShortName());
        this.setShortNameInfoService(org.getShortNameInfoService());
        this.setOfficialName(org.getOfficialName());
        this.setInn(org.getINN());
        this.setAddress(org.getAddress());
        this.setGuid(org.getGuid());
        this.setCity(org.getCity());
        this.setDistrict(org.getDistrict());
        this.setType(org.getType());
        this.setState(org.getState());
        this.setStatusDetailing(org.getStatusDetailing());
        this.setSecurityLevel(org.getSecurityLevel());
    }

    public void updateOrg(OrganizationSecurityLevel securityLevel) {
        //DAOService.getInstance().updateOrgSecurityLevel(securityLevel);
    }

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

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public OrganizationSecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public String getSecurityLevelToTurnOn() {
        switch (securityLevel) {
            case STANDARD : return OrganizationSecurityLevel.EXTENDED.toString();
            case EXTENDED : return OrganizationSecurityLevel.STANDARD.toString();
        }
        return OrganizationSecurityLevel.STANDARD.toString();
    }

    public void switchSecurityLevel() {
        switch (this.securityLevel) {
            case STANDARD : {
                this.securityLevel = OrganizationSecurityLevel.EXTENDED;
                break;
            }
            case EXTENDED : {
                this.securityLevel = OrganizationSecurityLevel.STANDARD;
                break;
            }
        }
        DAOService.getInstance().changeOrgSecurityLevel(this.idOfOrg, this.securityLevel);
    }

    public Boolean isStandardSecurityLevel() {
        return securityLevel == OrganizationSecurityLevel.STANDARD;
    }

    public Boolean isExtendedSecurityLevel() {
        return securityLevel == OrganizationSecurityLevel.EXTENDED;
    }

    public void setSecurityLevel(OrganizationSecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getStatusDetailing() {
        if (statusDetailing == null || statusDetailing.equals("''") || statusDetailing.equals("/")) {
            return "";
        }
        int p = statusDetailing.indexOf("/");
        if (p > 1) {
            return " " + statusDetailing.substring(p);
        } else {
            return "";
        }
    }

    public void setStatusDetailing(String statusDetailing) {
        this.statusDetailing = statusDetailing;
    }
}
