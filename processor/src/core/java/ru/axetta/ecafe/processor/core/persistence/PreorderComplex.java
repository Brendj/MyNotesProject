/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;

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

    public static Integer getDaysOfRegularPreorders() {
        return Integer.parseInt(RuntimeContext
                .getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.daysToGenerate", "14"));
    }

    public PreorderComplex() {

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

    public void deleteBySupplier(Long nextVersion, boolean doDelete) {
        this.version = nextVersion;
        this.deletedState = doDelete;
        if (doDelete) this.amount = 0;
        this.state = PreorderState.DELETED;
        this.lastUpdate = new Date();
    }

    public void changeBySupplier(Long nextVersion, boolean doDelete) {
        this.version = nextVersion;
        this.deletedState = doDelete;
        if (doDelete) this.amount = 0;
        this.state = PreorderState.CHANGED_PRICE;
        this.lastUpdate = new Date();
    }

    public boolean equalPrice(ComplexInfo ci) {
        return getPriceNullSafe(this.getComplexPrice()) == getPriceNullSafe(ci.getCurrentPrice());
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
}
