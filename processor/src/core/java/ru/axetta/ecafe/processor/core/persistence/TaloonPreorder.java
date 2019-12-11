/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 10.12.19
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public class TaloonPreorder {

    private Long idOfTaloonPreorder;
    private Long idOfOrg;
    private Date taloonDate;
    private Long complexId;
    private String complexName;
    private String goodsName;
    private String goodsGuid;
    private Org org;
    private Long idOfOrgCreated;
    private Integer soldQty;
    private Integer requestedQty;
    private Integer shippedQty;
    private Integer reservedQty;
    private Integer blockedQty;
    private Long price;
    private TaloonCreatedTypeEnum createdType;
    private TaloonISPPStatesEnum isppState;
    private TaloonPPStatesEnum ppState;
    private Long taloonNumber;
    private Org orgOwner; //От какой организации создана запись.
    private Long version;
    private Boolean deletedState;
    private String remarks;

    public TaloonPreorder() {
        //for Hibernate only
    }

    public TaloonPreorder(Long idOfOrg, Date taloonDate, Long complexId, String complexName,
            String goodsName, String goodsGuid, Long idOfOrgCreated, Integer soldQty, Integer requestedQty,
            Integer shippedQty, Integer reservedQty, Integer blockedQty, Long price, TaloonCreatedTypeEnum createdType,
            TaloonISPPStatesEnum isppState, TaloonPPStatesEnum ppState) {
        this.idOfOrg = idOfOrg;
        this.taloonDate = taloonDate;
        this.complexId = complexId;
        this.complexName = complexName;
        this.goodsName = goodsName;
        this.goodsGuid = goodsGuid;
        this.idOfOrgCreated = idOfOrgCreated;
        this.soldQty = soldQty;
        this.requestedQty = requestedQty;
        this.shippedQty = shippedQty;
        this.reservedQty = reservedQty;
        this.blockedQty = blockedQty;
        this.price = price;
        this.createdType = createdType;
        this.isppState = isppState;
        this.ppState = ppState;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Long getIdOfOrgCreated() {
        return idOfOrgCreated;
    }

    public void setIdOfOrgCreated(Long idOfOrgCreated) {
        this.idOfOrgCreated = idOfOrgCreated;
    }

    public Integer getSoldQty() {
        return soldQty;
    }

    public void setSoldQty(Integer soldedQty) {
        this.soldQty = soldedQty;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public TaloonCreatedTypeEnum getCreatedType() {
        return createdType;
    }

    public void setCreatedType(TaloonCreatedTypeEnum createdType) {
        this.createdType = createdType;
    }

    public Long getIdOfTaloonPreorder() {
        return idOfTaloonPreorder;
    }

    public void setIdOfTaloonPreorder(Long idOfTaloonPreorder) {
        this.idOfTaloonPreorder = idOfTaloonPreorder;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getTaloonDate() {
        return taloonDate;
    }

    public void setTaloonDate(Date taloonDate) {
        this.taloonDate = taloonDate;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public String getGoodsGuid() {
        return goodsGuid;
    }

    public void setGoodsGuid(String goodsGuid) {
        this.goodsGuid = goodsGuid;
    }

    public Integer getRequestedQty() {
        return requestedQty;
    }

    public void setRequestedQty(Integer requestedQty) {
        this.requestedQty = requestedQty;
    }

    public Integer getShippedQty() {
        return shippedQty;
    }

    public void setShippedQty(Integer shippedQty) {
        this.shippedQty = shippedQty;
    }

    public Integer getReservedQty() {
        return reservedQty;
    }

    public void setReservedQty(Integer reservedQty) {
        this.reservedQty = reservedQty;
    }

    public Integer getBlockedQty() {
        return blockedQty;
    }

    public void setBlockedQty(Integer blockedQty) {
        this.blockedQty = blockedQty;
    }

    public TaloonISPPStatesEnum getIsppState() {
        return isppState;
    }

    public void setIsppState(TaloonISPPStatesEnum isppState) {
        this.isppState = isppState;
    }

    public TaloonPPStatesEnum getPpState() {
        return ppState;
    }

    public void setPpState(TaloonPPStatesEnum ppState) {
        this.ppState = ppState;
    }

    public Long getTaloonNumber() {
        return taloonNumber;
    }

    public void setTaloonNumber(Long taloonNumber) {
        this.taloonNumber = taloonNumber;
    }

    public Org getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Org orgOwner) {
        this.orgOwner = orgOwner;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getComplexId() {
        return complexId;
    }

    public void setComplexId(Long complexId) {
        this.complexId = complexId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaloonPreorder)) {
            return false;
        }
        final TaloonPreorder taloonPreorder = (TaloonPreorder) o;
        return idOfTaloonPreorder.equals(taloonPreorder.getIdOfTaloonPreorder());
    }

    @Override
    public String toString() {
        return "TaloonPreorder{" + "idOfTaloon=" + idOfTaloonPreorder + ", idOfOrg=" + idOfOrg + ", complexId=" + complexId + ", complexName=" + complexName
                + ", taloonDate=" + taloonDate + ", goodsGuid=" + goodsGuid + ", org=" + org + ", soldQty=" + soldQty
                + ", requestedQty=" + requestedQty + ", shippedQty=" + shippedQty + ", reservedQty=" + reservedQty
                + ", blockedQty=" + blockedQty + ", ispp_state=" + isppState + ", pp_state=" + ppState
                + ", price=" + price + ", createdType=" + createdType + ", orgOwner=" + orgOwner + ", taloonNumber=" + taloonNumber + '}';
    }

}
