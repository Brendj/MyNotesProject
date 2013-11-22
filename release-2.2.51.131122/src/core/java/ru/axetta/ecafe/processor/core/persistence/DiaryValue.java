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
public class DiaryValue {

    public static final int DAY_VALUE1_TYPE = 0;
    public static final int DAY_VALUE2_TYPE = 1;
    public static final int DAY_EXAM_VALUE_TYPE = 100;

    public static final int DAY_PRESENCE_VALUE_TYPE = 200;
    public static final int DAY_BEHAVIOUR_VALUE_TYPE = 201;

    public static final int YEAR_VALUE_TYPE = 300;

    public static final int QUARTER1_VALUE_TYPE = 301;
    public static final int QUARTER2_VALUE_TYPE = 302;
    public static final int QUARTER3_VALUE_TYPE = 303;
    public static final int QUARTER4_VALUE_TYPE = 304;
    public static final int QUARTER_VALUE_TYPES[] = {301, 302, 303, 304};

    private CompositeIdOfDiaryValue compositeIdOfDiaryValue;
    private Org org;
    private Client client;
    private String value;
    private DiaryClass diaryClass;
    private Date recDate;
    private int vType;

    protected DiaryValue() {
        // For Hibernate only
    }

    public DiaryValue(CompositeIdOfDiaryValue compositeIdOfDiaryValue, String value) {
        this.compositeIdOfDiaryValue = compositeIdOfDiaryValue;
        this.value = value;
    }

    public CompositeIdOfDiaryValue getCompositeIdOfDiaryValue() {
        return compositeIdOfDiaryValue;
    }

    private void setCompositeIdOfDiaryValue(CompositeIdOfDiaryValue compositeIdOfDiaryValue) {
        // For Hibernate only
        this.compositeIdOfDiaryValue = compositeIdOfDiaryValue;
    }

    public Org getOrg() {
        return org;
    }

    private void setOrg(Org org) {
        // For Hibernate only
        this.org = org;
    }

    public Client getClient() {
        return client;
    }

    private void setClient(Client client) {
        // For Hibernate only
        this.client = client;
    }

    public DiaryClass getDiaryClass() {
        return diaryClass;
    }

    private void setDiaryClass(DiaryClass diaryClass) {
        // For Hibernate only
        this.diaryClass = diaryClass;
    }

    public String getValue() {
        return value;
    }

    private void setValue(String value) {
        // For Hibernate only
        this.value = value;
    }

    public Date getRecDate() {
        return recDate;
    }

    private void setRecDate(Date recDate) {
        // For Hibernate only
        this.recDate = recDate;
    }

    public int getVType() {
        return vType;
    }

    private void setVType(int vType) {
        // For Hibernate only
        this.vType = vType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiaryValue)) {
            return false;
        }
        final DiaryValue that = (DiaryValue) o;
        return compositeIdOfDiaryValue.equals(that.getCompositeIdOfDiaryValue());
    }

    @Override
    public int hashCode() {
        return compositeIdOfDiaryValue.hashCode();
    }

    @Override
    public String toString() {
        return "DiaryValue{" + "compositeIdOfDiaryValue=" + compositeIdOfDiaryValue + ", org=" + org + ", client="
                + client + ", value='" + value + '\'' + ", diaryClass=" + diaryClass + '}';
    }
}