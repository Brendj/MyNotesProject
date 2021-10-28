/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;

import java.util.Date;
import java.util.Set;

/**
 * Created by i.semenov on 12.03.2018.
 */
public class PreorderComplex {
    public static final Integer DEFAULT_MENU_SYNC_COUNT_DAYS = 14;
    public static final Integer DEFAULT_FORBIDDEN_DAYS = 2;
    public static final Integer COMPLEX_MODE_4 = 4;

    private Long idOfPreorderComplex;
    private Integer armComplexId;
    private Client client;
    private Date preorderDate;
    private Integer amount;
    private Long version;
    private Boolean deletedState;
    private String guid;
    private Long usedSum;
    private Long usedAmount;
    private Set<PreorderMenuDetail> preorderMenuDetails;
    private String complexName;
    private Long complexPrice;
    private Date createdDate;
    private Date lastUpdate;
    private PreorderState state;
    private Long idOfGoodsRequestPosition;
    private RegularPreorder regularPreorder;
    private Integer modeOfAdd;
    private Integer modeFree;
    private Long idOfOrgOnCreate;
    private String mobile;
    private PreorderMobileGroupOnCreateType mobileGroupOnCreate;
    private Boolean cancelnotification;
    private PreorderExternalSystemCode externalSystem;

    public static Integer getDaysOfRegularPreorders() {
        return Integer.parseInt(RuntimeContext
                .getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.daysToGenerate", "14"));
    }

    public PreorderComplex() {

    }

    public boolean isPayedComplex() {
        return usedSum != null && usedSum > 0;
    }

    public boolean isType4Complex() {
        return modeOfAdd.equals(COMPLEX_MODE_4);
    }

    public static void delete(Session session, Long idOfPreorderComplex, Long nextVersion, PreorderState state) {
        Query query = session.createQuery("update PreorderComplex pc set pc.deletedState = true, state = :state, amount = 0, "
                + "version = :version, lastUpdate =:lastUpdate where pc.idOfPreorderComplex = :idOfPreorderComplex");
        query.setParameter("state", state);
        query.setParameter("version", nextVersion);
        query.setParameter("lastUpdate", new Date());
        query.setParameter("idOfPreorderComplex", idOfPreorderComplex);
        query.executeUpdate();

        query = session.createQuery("update PreorderMenuDetail pmd set pmd.deletedState = true, state = :state, amount = 0 "
                + "where pmd.preorderComplex.idOfPreorderComplex = :idOfPreorderComplex");
        query.setParameter("state", state);
        query.setParameter("idOfPreorderComplex", idOfPreorderComplex);
        query.executeUpdate();
    }

    public void deleteByReason(Long nextVersion, boolean doDelete, PreorderState reason) {
        doDelete(nextVersion, doDelete, reason);
    }

    public void deleteByChangeOrg(Long nextVersion, boolean doDelete) {
        doDelete(nextVersion, doDelete, PreorderState.CHANGE_ORG);
    }

    private void doDelete(Long nextVersion, boolean doDelete, PreorderState state) {
        this.version = nextVersion;
        this.deletedState = doDelete;
        if (doDelete) this.amount = 0;
        this.state = state;
        this.lastUpdate = new Date();
    }

    public boolean equalPrice(ComplexInfo ci) {
        return getPriceNullSafe(this.getComplexPrice()) == getPriceNullSafe(ci.getCurrentPrice());
    }

    @Override
    public String toString() {
        return "PreorderComplex{" +
                "idOfClient=" + client.getIdOfClient() +
                ", preorderDate=" + CalendarUtils.dateToString(preorderDate) +
                ", armComplexId=" + armComplexId +
                ", idOfRegularPreorder='" + (regularPreorder == null ? "null" : regularPreorder.getIdOfRegularPreorder()) +
                '}';
    }

    private long getPriceNullSafe(Long value) {
        return value == null ? 0 : value;
    }

    public Long getIdOfPreorderComplex() {
        return idOfPreorderComplex;
    }

    public void setIdOfPreorderComplex(Long idOfPreorderComplex) {
        this.idOfPreorderComplex = idOfPreorderComplex;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getPreorderDate() {
        return preorderDate;
    }

    public void setPreorderDate(Date preorderDate) {
        this.preorderDate = preorderDate;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
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

    public Integer getArmComplexId() {
        return armComplexId;
    }

    public void setArmComplexId(Integer armComplexId) {
        this.armComplexId = armComplexId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getUsedSum() {
        return usedSum;
    }

    public void setUsedSum(Long usedSum) {
        this.usedSum = usedSum;
    }

    public Set<PreorderMenuDetail> getPreorderMenuDetails() {
        return preorderMenuDetails;
    }

    public void setPreorderMenuDetails(Set<PreorderMenuDetail> preorderMenuDetails) {
        this.preorderMenuDetails = preorderMenuDetails;
    }

    public Long getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(Long usedAmount) {
        this.usedAmount = usedAmount;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Long getComplexPrice() {
        return complexPrice;
    }

    public void setComplexPrice(Long complexPrice) {
        this.complexPrice = complexPrice;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public PreorderState getState() {
        return state;
    }

    public void setState(PreorderState state) {
        this.state = state;
    }

    public Long getIdOfGoodsRequestPosition() {
        return idOfGoodsRequestPosition;
    }

    public void setIdOfGoodsRequestPosition(Long idOfGoodsRequestPosition) {
        this.idOfGoodsRequestPosition = idOfGoodsRequestPosition;
    }

    public RegularPreorder getRegularPreorder() {
        return regularPreorder;
    }

    public void setRegularPreorder(RegularPreorder regularPreorder) {
        this.regularPreorder = regularPreorder;
    }

    public Integer getModeOfAdd() {
        return modeOfAdd;
    }

    public void setModeOfAdd(Integer modeOfAdd) {
        this.modeOfAdd = modeOfAdd;
    }

    public Integer getModeFree() {
        return modeFree;
    }

    public void setModeFree(Integer modeFree) {
        this.modeFree = modeFree;
    }

    public Long getIdOfOrgOnCreate() {
        return idOfOrgOnCreate;
    }

    public void setIdOfOrgOnCreate(Long idOfOrgOnCreate) {
        this.idOfOrgOnCreate = idOfOrgOnCreate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public PreorderMobileGroupOnCreateType getMobileGroupOnCreate() {
        return mobileGroupOnCreate;
    }

    public void setMobileGroupOnCreate(PreorderMobileGroupOnCreateType mobileGroupOnCreate) {
        this.mobileGroupOnCreate = mobileGroupOnCreate;
    }

    public Boolean getCancelnotification() {
        return cancelnotification;
    }

    public void setCancelnotification(Boolean cancelnotification) {
        this.cancelnotification = cancelnotification;
    }

    public PreorderExternalSystemCode getExternalSystem() {
        return externalSystem;
    }

    public void setExternalSystem(PreorderExternalSystemCode externalSystem) {
        this.externalSystem = externalSystem;
    }
}
