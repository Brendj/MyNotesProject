/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.07.2009
 * Time: 13:50:22
 * To change this template use File | Settings | File Templates.
 */
public class CompositeIdOfDiaryClass implements Serializable {

    private Long idOfOrg;
    private Long idOfClass;

    CompositeIdOfDiaryClass() {
        // For Hibernate only
    }

    public CompositeIdOfDiaryClass(Long idOfOrg, Long idOfClass) {
        this.idOfOrg = idOfOrg;
        this.idOfClass = idOfClass;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    private void setIdOfOrg(Long idOfOrg) {
        // For Hibernate only
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfClass() {
        return idOfClass;
    }

    private void setIdOfClass(Long idOfClass) {
        // For Hibernate only
        this.idOfClass = idOfClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfDiaryClass)) {
            return false;
        }
        final CompositeIdOfDiaryClass that = (CompositeIdOfDiaryClass) o;
        return idOfClass.equals(that.getIdOfClass()) && idOfOrg.equals(that.getIdOfOrg());
    }

    @Override
    public int hashCode() {
        int result = idOfOrg.hashCode();
        result = 31 * result + idOfClass.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CompositeIdOfDiaryClass{" + "idOfOrg=" + idOfOrg + ", idOfClass=" + idOfClass + '}';
    }
}