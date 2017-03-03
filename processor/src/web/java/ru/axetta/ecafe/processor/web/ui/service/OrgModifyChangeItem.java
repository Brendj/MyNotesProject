/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 03.03.16
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class OrgModifyChangeItem {
    private String oldValue;
    private String newValue;
    private String valueName;
    private boolean selected;

    public OrgModifyChangeItem(String valueName, String oldValue, String newValue) {
        this.valueName = valueName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.selected = true;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue == null ? "" : oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue == null ? "" : newValue;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Boolean isEqual() {
        return oldValue.equals(newValue);
    }
}
