/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.07.2009
 * Time: 13:50:22
 * To change this template use File | Settings | File Templates.
 */
public class CompositeIdOfDiaryValue implements Serializable {

    private Long idOfOrg;
    private Long idOfClient;
    private Long idOfClass;
    private Date recDate;
    private int vType;

    CompositeIdOfDiaryValue() {
        // For Hibernate only
    }

    public CompositeIdOfDiaryValue(Long idOfOrg, Long idOfClient, Long idOfClass, Date recDate, int vType) {
        this.idOfOrg = idOfOrg;
        this.idOfClient = idOfClient;
        this.idOfClass = idOfClass;
        this.recDate = recDate;
        this.vType = vType;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    private void setIdOfOrg(Long idOfOrg) {
        // For Hibernate only
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    private void setIdOfClient(Long idOfClient) {
        // For Hibernate only
        this.idOfClient = idOfClient;
    }

    public Long getIdOfClass() {
        return idOfClass;
    }

    private void setIdOfClass(Long idOfClass) {
        // For Hibernate only
        this.idOfClass = idOfClass;
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
        if (!(o instanceof CompositeIdOfDiaryValue)) {
            return false;
        }
        final CompositeIdOfDiaryValue that = (CompositeIdOfDiaryValue) o;
        if (vType != that.getVType()) {
            return false;
        }
        if (!idOfClass.equals(that.getIdOfClass())) {
            return false;
        }
        if (!idOfClient.equals(that.getIdOfClient())) {
            return false;
        }
        if (!idOfOrg.equals(that.getIdOfOrg())) {
            return false;
        }
        if (!recDate.equals(that.getRecDate())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = idOfOrg.hashCode();
        result = 31 * result + idOfClient.hashCode();
        result = 31 * result + idOfClass.hashCode();
        result = 31 * result + recDate.hashCode();
        result = 31 * result + vType;
        return result;
    }

    @Override
    public String toString() {
        return "CompositeIdOfDiaryValue{" + "idOfOrg=" + idOfOrg + ", idOfClient=" + idOfClient + ", idOfClass="
                + idOfClass + ", recDate=" + recDate + ", vType=" + vType + '}';
    }
}