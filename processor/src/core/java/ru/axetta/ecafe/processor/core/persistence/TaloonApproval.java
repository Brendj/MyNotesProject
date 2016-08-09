/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.02.16
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public class TaloonApproval {
    private CompositeIdOfTaloonApproval compositeIdOfTaloonApproval;

    private Org org;
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
    private String goodsGuid;

    public TaloonApproval() {
        //for Hibernate only
    }

    public TaloonApproval(CompositeIdOfTaloonApproval compositeIdOfTaloonApproval, Integer soldedQty, Long price,
            TaloonCreatedTypeEnum createdType, Integer requestedQty, Integer shippedQty,
            TaloonISPPStatesEnum isppState, TaloonPPStatesEnum ppState,String goodsName,String goodsGuid) {
        this.compositeIdOfTaloonApproval = compositeIdOfTaloonApproval;
        this.soldedQty = soldedQty;
        this.price = price;
        this.createdType = createdType;
        this.requestedQty = requestedQty;
        this.shippedQty = shippedQty;
        this.isppState = isppState;
        this.ppState = ppState;
        this.goodsName = goodsName;
        this.goodsGuid = goodsGuid;
    }

    public CompositeIdOfTaloonApproval getCompositeIdOfTaloonApproval() {
        return compositeIdOfTaloonApproval;
    }

    public void setCompositeIdOfTaloonApproval(CompositeIdOfTaloonApproval compositeIdOfTaloonApproval) {
        this.compositeIdOfTaloonApproval = compositeIdOfTaloonApproval;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsGuid() {
        return goodsGuid;
    }

    public void setGoodsGuid(String goodsGuid) {
        this.goodsGuid = goodsGuid;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
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
        return compositeIdOfTaloonApproval.equals(taloonApproval.getCompositeIdOfTaloonApproval());
    }

    @Override
    public String toString() {
        return "TaloonApproval{" + "compositeIdOfTaloonApproval=" + compositeIdOfTaloonApproval + ", org=" + org + ", soldedQty=" + soldedQty
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
}
