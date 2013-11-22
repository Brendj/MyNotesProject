/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 29.08.13
 * Time: 12:28
 */

public class ClientAllocationRule {

    private Long id;
    private Org sourceOrg;
    private String groupFilter;
    private Org destinationOrg;
    private boolean tempClient;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Org getSourceOrg() {
        return sourceOrg;
    }

    public void setSourceOrg(Org sourceOrg) {
        this.sourceOrg = sourceOrg;
    }

    public String getGroupFilter() {
        return groupFilter;
    }

    public void setGroupFilter(String groupFilter) {
        this.groupFilter = groupFilter;
    }

    public Org getDestinationOrg() {
        return destinationOrg;
    }

    public void setDestinationOrg(Org destinationOrg) {
        this.destinationOrg = destinationOrg;
    }

    public boolean isTempClient() {
        return tempClient;
    }

    public void setTempClient(boolean tempClient) {
        this.tempClient = tempClient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientAllocationRule that = (ClientAllocationRule) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
