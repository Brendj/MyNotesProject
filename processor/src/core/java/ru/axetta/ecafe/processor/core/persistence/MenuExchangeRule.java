/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 09.11.2010
 * Time: 23:26:36
 * To change this template use File | Settings | File Templates.
 */
public class MenuExchangeRule implements Serializable {

    /* Ид поставщика основного меню */
    private Long idOfSourceOrg;
    /* ид потребителя основного меню */
    private Long idOfDestOrg;

    private MenuExchangeRule() {

    }

    public MenuExchangeRule(Long idOfSourceOrg, Long idOfDestOrg) {
        this.idOfSourceOrg = idOfSourceOrg;
        this.idOfDestOrg = idOfDestOrg;
    }

    public Long getIdOfSourceOrg() {
        return idOfSourceOrg;
    }

    public void setIdOfSourceOrg(Long idOfSourceOrg) {
        this.idOfSourceOrg = idOfSourceOrg;
    }

    public Long getIdOfDestOrg() {
        return idOfDestOrg;
    }

    public void setIdOfDestOrg(Long idOfDestOrg) {
        this.idOfDestOrg = idOfDestOrg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuExchangeRule)) {
            return false;
        }

        MenuExchangeRule that = (MenuExchangeRule) o;

        if (idOfDestOrg != null ? !idOfDestOrg.equals(that.idOfDestOrg) : that.idOfDestOrg != null) {
            return false;
        }
        if (idOfSourceOrg != null ? !idOfSourceOrg.equals(that.idOfSourceOrg) : that.idOfSourceOrg != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = idOfSourceOrg != null ? idOfSourceOrg.hashCode() : 0;
        result = 31 * result + (idOfDestOrg != null ? idOfDestOrg.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MenuExchangeRule{" + "idOfSourceOrg=" + idOfSourceOrg + ", idOfDestOrg=" + idOfDestOrg + '}';
    }
}
