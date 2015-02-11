/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.model;

/**
 * Created with IntelliJ IDEA.
 * User: regal
 * Date: 06.02.15
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
public class ClientCount {
    private long idOfOrg;
    private int count;

    public ClientCount(long idOfOrg, int count) {
        this.idOfOrg = idOfOrg;
        this.count = count;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
