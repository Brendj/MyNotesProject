/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import ru.axetta.ecafe.processor.core.persistence.Org;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonPropertyOrder({"OrgId", "FullName", "ShortName", "NamePP", "ShortAddress", "GUID", "UNOM", "UNAD", "INN", "OGRN", "OrgType", "MainBuilding" })
public class FriendlyOrgDTO {
    @JsonProperty("OrgId")
    private Long orgId;

    @JsonProperty("FullName")
    private String fullName;

    @JsonProperty("ShortName")
    private String shortName;

    @JsonProperty("NamePP")
    private String namePP;

    @JsonProperty("ShortAddress")
    private String shortAddress;

    @JsonProperty("GUID")
    private String guid;

    @JsonProperty("UNOM")
    private Long unom;

    @JsonProperty("UNAD")
    private Long unad;

    @JsonProperty("INN")
    private String inn;

    @JsonProperty("OGRN")
    private String ogrn;

    @JsonProperty("OrgType")
    private String orgType;

    @JsonProperty("MainBuilding")
    private Boolean mainBuilding;

    public FriendlyOrgDTO(){

    }

    public FriendlyOrgDTO(Org org){
        this.orgId = org.getIdOfOrg();
        this.fullName = org.getOfficialName();
        this.shortName = org.getShortNameInfoService();
        this.namePP = org.getShortName();
        this.shortAddress = org.getShortAddress();
        this.guid = org.getGuid();
        this.unom = org.getBtiUnom();
        this.unad = org.getBtiUnad();
        this.inn = org.getINN();
        this.ogrn = org.getOGRN();
        this.orgType = String.format("%o - %s", org.getType().getCode(), org.getType().getShortType());
        this.mainBuilding = org.isMainBuilding();
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getNamePP() {
        return namePP;
    }

    public void setNamePP(String namePP) {
        this.namePP = namePP;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getUnom() {
        return unom;
    }

    public void setUnom(Long unom) {
        this.unom = unom;
    }

    public Long getUnad() {
        return unad;
    }

    public void setUnad(Long unad) {
        this.unad = unad;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public Boolean isMainBuilding() {
        return mainBuilding;
    }

    public void setMainBuilding(boolean mainBuilding) {
        this.mainBuilding = mainBuilding;
    }
}
