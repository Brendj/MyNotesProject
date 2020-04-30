/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.02.16
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public class TaloonApproval {

    private Long idOfTaloonApproval;
    private Long idOfOrg;
    private Date taloonDate;
    private String taloonName;
    private String goodsGuid;

    private Org org;
    private Long idOfOrgCreated;
    private Integer soldedQty;
    private Integer requestedQty;
    private Integer shippedQty;
    private Long price;
    private TaloonCreatedTypeEnum createdType;
    private TaloonISPPStatesEnum isppState;
    private TaloonPPStatesEnum ppState;
    private Long taloonNumber;
    private Org orgOwner; //От какой организации создана запись.
    private Long version;
    private Boolean deletedState;
    private String goodsName;
    private String remarks;
    private Long complexId;
    private Boolean byWebSupplier;

    public TaloonApproval() {
        //for Hibernate only
    }

    public TaloonApproval(Long idOfOrg, Long idOfOrgCreated, Date taloonDate, String taloonName, String goodsGuid, Integer soldedQty, Long price,
            TaloonCreatedTypeEnum createdType, Integer requestedQty, Integer shippedQty,
            TaloonISPPStatesEnum isppState, TaloonPPStatesEnum ppState,String goodsName) {
        this.idOfOrg = idOfOrg;
        this.idOfOrgCreated = idOfOrgCreated;
        this.taloonDate = taloonDate;
        this.taloonName = taloonName;
        this.goodsGuid = goodsGuid;
        this.soldedQty = soldedQty;
        this.price = price;
        this.createdType = createdType;
        this.requestedQty = requestedQty;
        this.shippedQty = shippedQty;
        this.isppState = isppState;
        this.ppState = ppState;
        this.goodsName = goodsName;
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

    public Integer getSoldedQty() {
        return soldedQty;
    }

    public void setSoldedQty(Integer soldedQty) {
        this.soldedQty = soldedQty;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaloonApproval)) {
            return false;
        }
        final TaloonApproval taloonApproval = (TaloonApproval) o;
        return idOfTaloonApproval.equals(taloonApproval.getIdOfTaloonApproval());
        //return idOfOrg.equals(taloonApproval.getIdOfOrg()) && taloonDate.equals(taloonApproval.getTaloonDate())
        //        && taloonName.equals(taloonApproval.getTaloonName()) && goodsGuid.equals(taloonApproval.getGoodsGuid());
    }

    @Override
    public String toString() {
        return "TaloonApproval{" + "idOfTaloonApproval=" + idOfTaloonApproval + ", idOfOrg=" + idOfOrg + ", taloonName=" + taloonName
                + ", taloonDate=" + taloonDate + ", goodsGuid=" + goodsGuid + ", org=" + org + ", soldedQty=" + soldedQty
                + ", requestedQty=" + requestedQty + ", shippedQty=" + shippedQty + ", ispp_state=" + isppState + ", pp_state=" + ppState
                + ", price=" + price + ", createdType=" + createdType + ", orgOwner=" + orgOwner + ", taloonNumber=" + taloonNumber + '}';
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

    public Long getTaloonNumber() {
        return taloonNumber;
    }

    public void setTaloonNumber(Long taloonNumber) {
        this.taloonNumber = taloonNumber;
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

    public Long getIdOfTaloonApproval() {
        return idOfTaloonApproval;
    }

    public void setIdOfTaloonApproval(Long idOfTaloonApproval) {
        this.idOfTaloonApproval = idOfTaloonApproval;
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

    public String getTaloonName() {
        return taloonName;
    }

    public void setTaloonName(String taloonName) {
        this.taloonName = taloonName;
    }

    public String getGoodsGuid() {
        return goodsGuid;
    }

    public void setGoodsGuid(String goodsGuid) {
        this.goodsGuid = goodsGuid;
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

    public Boolean getByWebSupplier() {
        return byWebSupplier;
    }

    public void setByWebSupplier(Boolean byWebSupplier) {
        this.byWebSupplier = byWebSupplier;
    }
}
