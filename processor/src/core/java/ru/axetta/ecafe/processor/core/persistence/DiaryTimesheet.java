/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.06.2009
 * Time: 14:02:41
 * To change this template use File | Settings | File Templates.
 */
public class DiaryTimesheet {

    private CompositeIdOfDiaryTimesheet compositeIdOfDiaryTimesheet;
    private Org org;
    private ClientGroup clientGroup;
    private Date recDate;
    private Long c0;
    private Long c1;
    private Long c2;
    private Long c3;
    private Long c4;
    private Long c5;
    private Long c6;
    private Long c7;
    private Long c8;
    private Long c9;
    private DiaryClass diaryClass0;
    private DiaryClass diaryClass1;
    private DiaryClass diaryClass2;
    private DiaryClass diaryClass3;
    private DiaryClass diaryClass4;
    private DiaryClass diaryClass5;
    private DiaryClass diaryClass6;
    private DiaryClass diaryClass7;
    private DiaryClass diaryClass8;
    private DiaryClass diaryClass9;

    DiaryTimesheet() {
        // For Hibernate only
    }

    public DiaryTimesheet(CompositeIdOfDiaryTimesheet compositeIdOfDiaryTimesheet) {
        this.compositeIdOfDiaryTimesheet = compositeIdOfDiaryTimesheet;
    }

    public CompositeIdOfDiaryTimesheet getCompositeIdOfDiaryTimesheet() {
        return compositeIdOfDiaryTimesheet;
    }

    private void setCompositeIdOfDiaryTimesheet(CompositeIdOfDiaryTimesheet compositeIdOfDiaryTimesheet) {
        // For Hibernate only
        this.compositeIdOfDiaryTimesheet = compositeIdOfDiaryTimesheet;
    }

    public Org getOrg() {
        return org;
    }

    private void setOrg(Org org) {
        // For Hibernate only
        this.org = org;
    }

    public ClientGroup getClientGroup() {
        return clientGroup;
    }

    private void setClientGroup(ClientGroup clientGroup) {
        // For Hibernate only
        this.clientGroup = clientGroup;
    }

    public Date getRecDate() {
        return recDate;
    }

    private void setRecDate(Date recDate) {
        // For Hibernate only
        this.recDate = recDate;
    }

    public Long getC0() {
        return c0;
    }

    public void setC0(Long c0) {
        this.c0 = c0;
    }

    public Long getC1() {
        return c1;
    }

    public void setC1(Long c1) {
        this.c1 = c1;
    }

    public Long getC2() {
        return c2;
    }

    public void setC2(Long c2) {
        this.c2 = c2;
    }

    public Long getC3() {
        return c3;
    }

    public void setC3(Long c3) {
        this.c3 = c3;
    }

    public Long getC4() {
        return c4;
    }

    public void setC4(Long c4) {
        this.c4 = c4;
    }

    public Long getC5() {
        return c5;
    }

    public void setC5(Long c5) {
        this.c5 = c5;
    }

    public Long getC6() {
        return c6;
    }

    public void setC6(Long c6) {
        this.c6 = c6;
    }

    public Long getC7() {
        return c7;
    }

    public void setC7(Long c7) {
        this.c7 = c7;
    }

    public Long getC8() {
        return c8;
    }

    public void setC8(Long c8) {
        this.c8 = c8;
    }

    public Long getC9() {
        return c9;
    }

    public void setC9(Long c9) {
        this.c9 = c9;
    }

    public DiaryClass getDiaryClass0() {
        return diaryClass0;
    }

    private void setDiaryClass0(DiaryClass diaryClass0) {
        // For Hibernate only
        this.diaryClass0 = diaryClass0;
    }

    public DiaryClass getDiaryClass1() {
        return diaryClass1;
    }

    private void setDiaryClass1(DiaryClass diaryClass1) {
        // For Hibernate only
        this.diaryClass1 = diaryClass1;
    }

    public DiaryClass getDiaryClass2() {
        return diaryClass2;
    }

    private void setDiaryClass2(DiaryClass diaryClass2) {
        // For Hibernate only
        this.diaryClass2 = diaryClass2;
    }

    public DiaryClass getDiaryClass3() {
        return diaryClass3;
    }

    private void setDiaryClass3(DiaryClass diaryClass3) {
        // For Hibernate only
        this.diaryClass3 = diaryClass3;
    }

    public DiaryClass getDiaryClass4() {
        return diaryClass4;
    }

    private void setDiaryClass4(DiaryClass diaryClass4) {
        // For Hibernate only
        this.diaryClass4 = diaryClass4;
    }

    public DiaryClass getDiaryClass5() {
        return diaryClass5;
    }

    private void setDiaryClass5(DiaryClass diaryClass5) {
        // For Hibernate only
        this.diaryClass5 = diaryClass5;
    }

    public DiaryClass getDiaryClass6() {
        return diaryClass6;
    }

    private void setDiaryClass6(DiaryClass diaryClass6) {
        // For Hibernate only
        this.diaryClass6 = diaryClass6;
    }

    public DiaryClass getDiaryClass7() {
        return diaryClass7;
    }

    private void setDiaryClass7(DiaryClass diaryClass7) {
        // For Hibernate only
        this.diaryClass7 = diaryClass7;
    }

    public DiaryClass getDiaryClass8() {
        return diaryClass8;
    }

    private void setDiaryClass8(DiaryClass diaryClass8) {
        // For Hibernate only
        this.diaryClass8 = diaryClass8;
    }

    public DiaryClass getDiaryClass9() {
        return diaryClass9;
    }

    private void setDiaryClass9(DiaryClass diaryClass9) {
        // For Hibernate only
        this.diaryClass9 = diaryClass9;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiaryTimesheet)) {
            return false;
        }
        final DiaryTimesheet that = (DiaryTimesheet) o;
        return compositeIdOfDiaryTimesheet.equals(that.getCompositeIdOfDiaryTimesheet());
    }

    @Override
    public int hashCode() {
        return compositeIdOfDiaryTimesheet.hashCode();
    }

    @Override
    public String toString() {
        return "DiaryTimesheet{" + "compositeIdOfDiaryTimesheet=" + compositeIdOfDiaryTimesheet + ", org=" + org
                + ", clientGroup=" + clientGroup + ", c0=" + c0 + ", c1=" + c1 + ", c2=" + c2 + ", c3=" + c3 + ", c4="
                + c4 + ", c5=" + c5 + ", c6=" + c6 + ", c7=" + c7 + ", c8=" + c8 + ", c9=" + c9 + ", diaryClass0="
                + diaryClass0 + ", diaryClass1=" + diaryClass1 + ", diaryClass2=" + diaryClass2 + ", diaryClass3="
                + diaryClass3 + ", diaryClass4=" + diaryClass4 + ", diaryClass5=" + diaryClass5 + ", diaryClass6="
                + diaryClass6 + ", diaryClass7=" + diaryClass7 + ", diaryClass8=" + diaryClass8 + ", diaryClass9="
                + diaryClass9 + '}';
    }
}