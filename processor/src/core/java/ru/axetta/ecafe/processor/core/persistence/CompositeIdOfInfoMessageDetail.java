/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created by i.semenov on 31.03.2017.
 */
public class CompositeIdOfInfoMessageDetail implements Serializable {
    private Long idOfInfoMessage;
    private Long idOfOrg;

    public CompositeIdOfInfoMessageDetail() {

    }

    public CompositeIdOfInfoMessageDetail(Long idOfInfoMessage, Long idOfOrg) {
        this.idOfInfoMessage = idOfInfoMessage;
        this.idOfOrg = idOfOrg;
    }

    @Override
    public String toString() {
        return "CompositeIdOfInfoMessageDetail{" +
                "idOfInfoMessage=" + idOfInfoMessage +
                ", idOfOrg=" + idOfOrg +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompositeIdOfInfoMessageDetail that = (CompositeIdOfInfoMessageDetail) o;

        if (!idOfInfoMessage.equals(that.idOfInfoMessage)) {
            return false;
        }
        return idOfOrg.equals(that.idOfOrg);

    }

    @Override
    public int hashCode() {
        int result = idOfInfoMessage.hashCode();
        result = 31 * result + idOfOrg.hashCode();
        return result;
    }

    public Long getIdOfInfoMessage() {
        return idOfInfoMessage;
    }

    public void setIdOfInfoMessage(Long idOfInfoMessage) {
        this.idOfInfoMessage = idOfInfoMessage;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
