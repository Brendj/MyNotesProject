/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

public class CompositeIdOfEnterEventSendInfo implements Serializable {
    private long idOfEnterEvent;
    private long idOfOrg;

    protected CompositeIdOfEnterEventSendInfo() {
        // For Hibernate only
    }

    public CompositeIdOfEnterEventSendInfo(long idOfEnterEvent, long idOfOrg) {
        this.idOfEnterEvent = idOfEnterEvent;
        this.idOfOrg = idOfOrg;
    }

    public long getIdOfEnterEvent() {
        return idOfEnterEvent;
    }

    public void setIdOfEnterEvent(long idOfEnterEvent) {
        // For Hibernate only
        this.idOfEnterEvent = idOfEnterEvent;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        // For Hibernate only
        this.idOfOrg = idOfOrg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompositeIdOfEnterEventSendInfo that = (CompositeIdOfEnterEventSendInfo) o;

        if (idOfEnterEvent != that.idOfEnterEvent) {
            return false;
        }
        if (idOfOrg != that.idOfOrg) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idOfEnterEvent ^ (idOfEnterEvent >>> 32));
        result = 31 * result + (int) (idOfOrg ^ (idOfOrg >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "CompositeIdOfEnterEvent{" + "idOfEnterEvent=" + idOfEnterEvent + ", idOfOrg=" + idOfOrg + '}';
    }
}
