package ru.axetta.ecafe.processor.web.internal;

public class ModifyContactsItem {
    private Integer typeId;
    private String oldValue;
    private String newValue;

    public ModifyContactsItem(Integer typeId, String oldValue, String newValue) {
        this.typeId = typeId;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
