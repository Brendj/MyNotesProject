/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

public class CompositeIdOfEnterEventSendInfo implements Serializable {
    private Long idOfEnterEvent;
    private Long idOfOrg;

    protected CompositeIdOfEnterEventSendInfo() {
        // For Hibernate only
    }

    public CompositeIdOfEnterEventSendInfo(Long idOfEnterEvent, Long idOfOrg) {
        this.idOfEnterEvent = idOfEnterEvent;
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfEnterEvent() {
        return idOfEnterEvent;
    }

    public void setIdOfEnterEvent(Long idOfEnterEvent) {
        this.idOfEnterEvent = idOfEnterEvent;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        // For Hibernate only
        this.idOfOrg = idOfOrg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        CompositeIdOfEnterEventSendInfo that = (CompositeIdOfEnterEventSendInfo) o;

        return idOfEnterEvent.equals(that.idOfEnterEvent) && idOfOrg.equals(that.idOfOrg);
    }

    @Override
    public int hashCode() {
        int result = (int) (idOfEnterEvent ^ (idOfEnterEvent >>> 32));
        result = 31 * result + (int) (idOfOrg ^ (idOfOrg >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "CompositeIdOfEnterEventSendInfo{" + "idOfEnterEvent=" + idOfEnterEvent + ", idOfOrg=" + idOfOrg + '}';
    }
}
