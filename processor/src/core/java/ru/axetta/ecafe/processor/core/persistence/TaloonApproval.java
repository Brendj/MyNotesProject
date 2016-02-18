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
    private Integer qty;
    private Long price;
    private TaloonCreatedTypeEnum createdType;
    //todo не позволять сохранять запись, если она создана ранее от другой OrgOwner.
    private Org orgOwner; //От какой организации создана запись.
    private Long version;
    private Boolean deletedState;

    public TaloonApproval() {
        //for Hibernate only
    }

    public TaloonApproval(CompositeIdOfTaloonApproval compositeIdOfTaloonApproval, Integer qty, Long price, TaloonCreatedTypeEnum createdType) {
        this.compositeIdOfTaloonApproval = compositeIdOfTaloonApproval;
        this.qty = qty;
        this.price = price;
        this.createdType = createdType;
    }

    public CompositeIdOfTaloonApproval getCompositeIdOfTaloonApproval() {
        return compositeIdOfTaloonApproval;
    }

    public void setCompositeIdOfTaloonApproval(CompositeIdOfTaloonApproval compositeIdOfTaloonApproval) {
        this.compositeIdOfTaloonApproval = compositeIdOfTaloonApproval;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
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
        return "TaloonApproval{" + "compositeIdOfTaloonApproval=" + compositeIdOfTaloonApproval + ", org=" + org + ", qty=" + qty + ", price="
                + price + ", createdType=" + createdType + ", orgOwner=" + orgOwner + '}';
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
}
