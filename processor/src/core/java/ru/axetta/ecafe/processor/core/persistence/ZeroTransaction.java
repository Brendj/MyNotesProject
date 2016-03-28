/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 28.03.16
 * Time: 12:20
 * To change this template use File | Settings | File Templates.
 */
public class ZeroTransaction {
    private CompositeIdOfZeroTransaction compositeIdOfZeroTransaction;
    private Integer targetLevel;
    private Integer actualLevel;
    private Integer criteriaLevel;
    private Integer idOfReason;
    private String comment;
    private Long version;
    private Org org;

    public ZeroTransaction() {

    }

    public ZeroTransaction(CompositeIdOfZeroTransaction compositeIdOfZeroTransaction, Integer targetLevel, Integer actualLevel,
            Integer criteriaLevel, Integer idOfReason, String comment) {
        this.compositeIdOfZeroTransaction = compositeIdOfZeroTransaction;
        this.targetLevel = targetLevel;
        this.actualLevel = actualLevel;
        this.criteriaLevel = criteriaLevel;
        this.idOfReason = idOfReason;
        this.comment = comment;
    }

    public CompositeIdOfZeroTransaction getCompositeIdOfZeroTransaction() {
        return compositeIdOfZeroTransaction;
    }

    public void setCompositeIdOfZeroTransaction(CompositeIdOfZeroTransaction compositeIdOfZeroTransaction) {
        this.compositeIdOfZeroTransaction = compositeIdOfZeroTransaction;
    }

    public Integer getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(Integer targetLevel) {
        this.targetLevel = targetLevel;
    }

    public Integer getActualLevel() {
        return actualLevel;
    }

    public void setActualLevel(Integer actualLevel) {
        this.actualLevel = actualLevel;
    }

    public Integer getCriteriaLevel() {
        return criteriaLevel;
    }

    public void setCriteriaLevel(Integer criteriaLevel) {
        this.criteriaLevel = criteriaLevel;
    }

    public Integer getIdOfReason() {
        return idOfReason;
    }

    public void setIdOfReason(Integer idOfReason) {
        this.idOfReason = idOfReason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }
}
