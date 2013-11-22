/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 09.11.2010
 * Time: 23:34:42
 * To change this template use File | Settings | File Templates.
 */
public class CompositeIdOfMenuExchange implements Serializable {

    private Date menuDate;
    private Long idOfOrg;

    protected CompositeIdOfMenuExchange() {
        // For Hibernate only
    }

    public CompositeIdOfMenuExchange(Date menuDate, Long idOfOrg) {
        this.menuDate = menuDate;
        this.idOfOrg = idOfOrg;
    }

    public Date getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(Date menuDate) {
        this.menuDate = menuDate;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfMenuExchange)) {
            return false;
        }

        CompositeIdOfMenuExchange that = (CompositeIdOfMenuExchange) o;

        if (idOfOrg != null ? !idOfOrg.equals(that.idOfOrg) : that.idOfOrg != null) {
            return false;
        }
        if (menuDate != null ? !menuDate.equals(that.menuDate) : that.menuDate != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = menuDate != null ? menuDate.hashCode() : 0;
        result = 31 * result + (idOfOrg != null ? idOfOrg.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompositeIdOfMenuExchange{" + "menuDate=" + menuDate + ", idOfOrg=" + idOfOrg + '}';
    }
}