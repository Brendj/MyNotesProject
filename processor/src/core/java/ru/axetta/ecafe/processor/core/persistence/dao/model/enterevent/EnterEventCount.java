/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent;

/**
 * Created with IntelliJ IDEA.
 * User: regal
 * Date: 06.02.15
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
public class EnterEventCount {
    private long idOfOrg;
    private int count;
    private long idOfClient;

    public EnterEventCount(long idOfOrg, int count) {
        this.idOfOrg = idOfOrg;
        this.count = count;
    }

    public EnterEventCount( long idOfOrg,long idOfClient) {
        this.idOfOrg = idOfOrg;
        this.idOfClient = idOfClient;
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

    public long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(long idOfClient) {
        this.idOfClient = idOfClient;
    }
}
