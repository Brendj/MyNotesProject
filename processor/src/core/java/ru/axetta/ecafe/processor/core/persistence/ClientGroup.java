/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class ClientGroup {

    private CompositeIdOfClientGroup compositeIdOfClientGroup;
    private Org org;
    private String groupName;
    private Set<Client> clients = new HashSet<Client>();
    private Set<DiaryTimesheet> diaryTimesheets = new HashSet<DiaryTimesheet>();

    ClientGroup() {
        // For Hibernate only
    }

    public ClientGroup(CompositeIdOfClientGroup compositeIdOfClientGroup, String groupName) {
        this.compositeIdOfClientGroup = compositeIdOfClientGroup;
        this.groupName = groupName;
    }

    public CompositeIdOfClientGroup getCompositeIdOfClientGroup() {
        return compositeIdOfClientGroup;
    }

    private void setCompositeIdOfClientGroup(CompositeIdOfClientGroup compositeIdOfClientGroup) {
        // For Hibernate only
        this.compositeIdOfClientGroup = compositeIdOfClientGroup;
    }

    public Org getOrg() {
        return org;
    }

    private void setOrg(Org org) {
        // For Hibernate only
        this.org = org;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    private Set<Client> getClientsInternal() {
        // For Hibernate only
        return clients;
    }

    private void setClientsInternal(Set<Client> clients) {
        // For Hibernate only
        this.clients = clients;
    }

    public Set<Client> getClients() {
        return Collections.unmodifiableSet(getClientsInternal());
    }

    public void addClient(Client client) {
        getClientsInternal().add(client);
    }

    public void removeClient(Client client) {
        getClientsInternal().remove(client);
    }

    private Set<DiaryTimesheet> getDiaryTimesheetsInternal() {
        // For Hibernate only
        return diaryTimesheets;
    }

    private void setDiaryTimesheetsInternal(Set<DiaryTimesheet> diaryTimesheets) {
        // For Hibernate only
        this.diaryTimesheets = diaryTimesheets;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets() {
        return Collections.unmodifiableSet(getDiaryTimesheetsInternal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientGroup)) {
            return false;
        }
        final ClientGroup that = (ClientGroup) o;
        if (!compositeIdOfClientGroup.equals(that.getCompositeIdOfClientGroup())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return compositeIdOfClientGroup.hashCode();
    }

    @Override
    public String toString() {
        return "ClientGroup{" + "compositeIdOfClientGroup=" + compositeIdOfClientGroup + ", org=" + org
                + ", groupName='" + groupName + '\'' + '}';
    }
}