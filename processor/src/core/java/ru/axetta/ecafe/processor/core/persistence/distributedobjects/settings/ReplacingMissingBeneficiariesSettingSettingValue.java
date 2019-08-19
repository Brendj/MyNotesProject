/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.ReplacingMissingBeneficiariesType;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.13
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
public class ReplacingMissingBeneficiariesSettingSettingValue extends AbstractParserBySettingValue{

    private String value; // (Резерв\Все)
    private int orgParam;//   int (1 \ 2)  1 только свой корпус 2 все корпуса

    private static final int DEFAULT_CAPACITY = 2;

    public ReplacingMissingBeneficiariesSettingSettingValue(String[] values) throws ParseException {
        super(values);
    }

    @Override
    protected void parse(String[] values) throws ParseException {
        this.value = values[0];
        this.orgParam = Integer.parseInt(values[1]);
    }

    @Override
    public String build() {
        return value+";"+orgParam+";";
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    protected int getECafeSettingArrayCapacity() {
        return DEFAULT_CAPACITY;
    }

    @Override
    protected Integer getOrgSettingTypeByIndex(Integer index) {
        return ReplacingMissingBeneficiariesType.getGlobalIdByECafeSettingValueIndex(index);
    }

    @Override
    protected Integer getIndexByOrgSettingType(Integer type) {
        return ReplacingMissingBeneficiariesType.getECafeSettingValueIndexByGlobalId(type);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getOrgParam() {
        return orgParam;
    }

    public void setOrgParam(int orgParam) {
        this.orgParam = orgParam;
    }
}

