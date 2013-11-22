/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.06.2009
 * Time: 14:02:41
 * To change this template use File | Settings | File Templates.
 */
public class DiaryClass {

    private CompositeIdOfDiaryClass compositeIdOfDiaryClass;
    private Org org;
    private String className;
    private Set<DiaryTimesheet> diaryTimesheets0 = new HashSet<DiaryTimesheet>();
    private Set<DiaryTimesheet> diaryTimesheets1 = new HashSet<DiaryTimesheet>();
    private Set<DiaryTimesheet> diaryTimesheets2 = new HashSet<DiaryTimesheet>();
    private Set<DiaryTimesheet> diaryTimesheets3 = new HashSet<DiaryTimesheet>();
    private Set<DiaryTimesheet> diaryTimesheets4 = new HashSet<DiaryTimesheet>();
    private Set<DiaryTimesheet> diaryTimesheets5 = new HashSet<DiaryTimesheet>();
    private Set<DiaryTimesheet> diaryTimesheets6 = new HashSet<DiaryTimesheet>();
    private Set<DiaryTimesheet> diaryTimesheets7 = new HashSet<DiaryTimesheet>();
    private Set<DiaryTimesheet> diaryTimesheets8 = new HashSet<DiaryTimesheet>();
    private Set<DiaryTimesheet> diaryTimesheets9 = new HashSet<DiaryTimesheet>();
    private Set<DiaryValue> diaryValues = new HashSet<DiaryValue>();

    protected DiaryClass() {
        // For Hibernate only
    }

    public DiaryClass(CompositeIdOfDiaryClass compositeIdOfDiaryClass, String className) {
        this.compositeIdOfDiaryClass = compositeIdOfDiaryClass;
        this.className = className;
    }

    public CompositeIdOfDiaryClass getCompositeIdOfDiaryClass() {
        return compositeIdOfDiaryClass;
    }

    private void setCompositeIdOfDiaryClass(CompositeIdOfDiaryClass compositeIdOfDiaryClass) {
        // For Hibernate only
        this.compositeIdOfDiaryClass = compositeIdOfDiaryClass;
    }

    public Org getOrg() {
        return org;
    }

    private void setOrg(Org org) {
        // For Hibernate only
        this.org = org;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private Set<DiaryTimesheet> getDiaryTimesheets0Internal() {
        // For Hibernate only
        return diaryTimesheets0;
    }

    private void setDiaryTimesheets0Internal(Set<DiaryTimesheet> diaryTimesheets0) {
        // For Hibernate only
        this.diaryTimesheets0 = diaryTimesheets0;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets0() {
        return Collections.unmodifiableSet(getDiaryTimesheets0Internal());
    }

    private Set<DiaryTimesheet> getDiaryTimesheets1Internal() {
        // For Hibernate only
        return diaryTimesheets1;
    }

    private void setDiaryTimesheets1Internal(Set<DiaryTimesheet> diaryTimesheets1) {
        // For Hibernate only
        this.diaryTimesheets1 = diaryTimesheets1;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets1() {
        return Collections.unmodifiableSet(getDiaryTimesheets1Internal());
    }

    private Set<DiaryTimesheet> getDiaryTimesheets2Internal() {
        // For Hibernate only
        return diaryTimesheets2;
    }

    private void setDiaryTimesheets2Internal(Set<DiaryTimesheet> diaryTimesheets2) {
        // For Hibernate only
        this.diaryTimesheets2 = diaryTimesheets2;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets2() {
        return Collections.unmodifiableSet(getDiaryTimesheets2Internal());
    }

    private Set<DiaryTimesheet> getDiaryTimesheets3Internal() {
        // For Hibernate only
        return diaryTimesheets3;
    }

    private void setDiaryTimesheets3Internal(Set<DiaryTimesheet> diaryTimesheets3) {
        // For Hibernate only
        this.diaryTimesheets3 = diaryTimesheets3;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets3() {
        return Collections.unmodifiableSet(getDiaryTimesheets3Internal());
    }

    private Set<DiaryTimesheet> getDiaryTimesheets4Internal() {
        // For Hibernate only
        return diaryTimesheets4;
    }

    private void setDiaryTimesheets4Internal(Set<DiaryTimesheet> diaryTimesheets4) {
        // For Hibernate only
        this.diaryTimesheets4 = diaryTimesheets4;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets4() {
        return Collections.unmodifiableSet(getDiaryTimesheets4Internal());
    }

    private Set<DiaryTimesheet> getDiaryTimesheets5Internal() {
        // For Hibernate only
        return diaryTimesheets5;
    }

    private void setDiaryTimesheets5Internal(Set<DiaryTimesheet> diaryTimesheets5) {
        // For Hibernate only
        this.diaryTimesheets5 = diaryTimesheets5;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets5() {
        return Collections.unmodifiableSet(getDiaryTimesheets5Internal());
    }

    private Set<DiaryTimesheet> getDiaryTimesheets6Internal() {
        // For Hibernate only
        return diaryTimesheets6;
    }

    private void setDiaryTimesheets6Internal(Set<DiaryTimesheet> diaryTimesheets6) {
        // For Hibernate only
        this.diaryTimesheets6 = diaryTimesheets6;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets6() {
        return Collections.unmodifiableSet(getDiaryTimesheets6Internal());
    }

    private Set<DiaryTimesheet> getDiaryTimesheets7Internal() {
        // For Hibernate only
        return diaryTimesheets7;
    }

    private void setDiaryTimesheets7Internal(Set<DiaryTimesheet> diaryTimesheets7) {
        // For Hibernate only
        this.diaryTimesheets7 = diaryTimesheets7;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets7() {
        return Collections.unmodifiableSet(getDiaryTimesheets7Internal());
    }

    private Set<DiaryTimesheet> getDiaryTimesheets8Internal() {
        // For Hibernate only
        return diaryTimesheets8;
    }

    private void setDiaryTimesheets8Internal(Set<DiaryTimesheet> diaryTimesheets8) {
        // For Hibernate only
        this.diaryTimesheets8 = diaryTimesheets8;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets8() {
        return Collections.unmodifiableSet(getDiaryTimesheets8Internal());
    }

    private Set<DiaryTimesheet> getDiaryTimesheets9Internal() {
        // For Hibernate only
        return diaryTimesheets9;
    }

    private void setDiaryTimesheets9Internal(Set<DiaryTimesheet> diaryTimesheets9) {
        // For Hibernate only
        this.diaryTimesheets9 = diaryTimesheets9;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets9() {
        return Collections.unmodifiableSet(getDiaryTimesheets9Internal());
    }

    private Set<DiaryValue> getDiaryValuesInternal() {
        // For Hibernate only
        return diaryValues;
    }

    private void setDiaryValuesInternal(Set<DiaryValue> diaryValues) {
        // For Hibernate only
        this.diaryValues = diaryValues;
    }

    public Set<DiaryValue> getDiaryValues() {
        return Collections.unmodifiableSet(getDiaryValuesInternal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiaryClass)) {
            return false;
        }
        final DiaryClass that = (DiaryClass) o;
        return compositeIdOfDiaryClass.equals(that.getCompositeIdOfDiaryClass());
    }

    @Override
    public int hashCode() {
        return compositeIdOfDiaryClass.hashCode();
    }

    @Override
    public String toString() {
        return "DiaryClass{" + "compositeIdOfDiaryClass=" + compositeIdOfDiaryClass + ", org=" + org + ", name='"
                + className + '\'' + '}';
    }
}