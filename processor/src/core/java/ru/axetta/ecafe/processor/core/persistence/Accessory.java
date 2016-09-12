/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 15.10.14
 * Time: 19:00
 * To change this template use File | Settings | File Templates.
 */
public class Accessory implements Serializable {
    protected Long idOfAccessory;
    protected Long idOfSourceOrg;
    protected Long idOfTargetOrg;
    protected Integer accessoryType;
    protected String accessoryNumber;
    private Boolean usedSinceSeptember;

    public static final int BANK_ACCESSORY_TYPE = 1;
    public static final int GATE_ACCESSORY_TYPE = 2;
    public static final Object [][] TYPES = { { BANK_ACCESSORY_TYPE, "Касса"},
                                              { GATE_ACCESSORY_TYPE, "Турникет"} };

    public Accessory() {
    }

    public Accessory(Long idOfAccessory, Long idOfSourceOrg, Long idOfTargetOrg, Integer accessoryType,
            String accessoryNumber) {
        this.idOfAccessory = idOfAccessory;
        this.idOfSourceOrg = idOfSourceOrg;
        this.idOfTargetOrg = idOfTargetOrg;
        this.accessoryType = accessoryType;
        this.accessoryNumber = accessoryNumber;
        this.usedSinceSeptember = false;
    }

    public Long getIdOfAccessory() {
        return idOfAccessory;
    }

    public void setIdOfAccessory(Long idOfAccessory) {
        this.idOfAccessory = idOfAccessory;
    }

    public Long getIdOfSourceOrg() {
        return idOfSourceOrg;
    }

    public void setIdOfSourceOrg(Long idOfSourceOrg) {
        this.idOfSourceOrg = idOfSourceOrg;
    }

    public Long getIdOfTargetOrg() {
        return idOfTargetOrg;
    }

    public void setIdOfTargetOrg(Long idOfTargetOrg) {
        this.idOfTargetOrg = idOfTargetOrg;
    }

    public Integer getAccessoryType() {
        return accessoryType;
    }

    public void setAccessoryType(Integer accessoryType) {
        this.accessoryType = accessoryType;
    }

    public String getAccessoryNumber() {
        return accessoryNumber;
    }

    public void setAccessoryNumber(String accessoryNumber) {
        this.accessoryNumber = accessoryNumber;
    }

    public Boolean getUsedSinceSeptember() {
        return usedSinceSeptember;
    }

    public void setUsedSinceSeptember(Boolean usedSinceSeptember) {
        this.usedSinceSeptember = usedSinceSeptember;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Accessory accessory = (Accessory) o;

        if (accessoryNumber != null ? !accessoryNumber.equals(accessory.accessoryNumber)
                : accessory.accessoryNumber != null) {
            return false;
        }
        if (accessoryType != null ? !accessoryType.equals(accessory.accessoryType) : accessory.accessoryType != null) {
            return false;
        }
        if (idOfAccessory != null ? !idOfAccessory.equals(accessory.idOfAccessory) : accessory.idOfAccessory != null) {
            return false;
        }
        if (idOfSourceOrg != null ? !idOfSourceOrg.equals(accessory.idOfSourceOrg) : accessory.idOfSourceOrg != null) {
            return false;
        }
        if (idOfTargetOrg != null ? !idOfTargetOrg.equals(accessory.idOfTargetOrg) : accessory.idOfTargetOrg != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = idOfAccessory != null ? idOfAccessory.hashCode() : 0;
        result = 31 * result + (idOfSourceOrg != null ? idOfSourceOrg.hashCode() : 0);
        result = 31 * result + (idOfTargetOrg != null ? idOfTargetOrg.hashCode() : 0);
        result = 31 * result + (accessoryType != null ? accessoryType.hashCode() : 0);
        result = 31 * result + (accessoryNumber != null ? accessoryNumber.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Accessory{" +
                "idOfAccessory=" + idOfAccessory +
                ", idOfSourceOrg=" + idOfSourceOrg +
                ", idOfTargetOrg=" + idOfTargetOrg +
                ", accessoryType=" + accessoryType +
                ", accessoryNumber=" + accessoryNumber +
                '}';
    }
}