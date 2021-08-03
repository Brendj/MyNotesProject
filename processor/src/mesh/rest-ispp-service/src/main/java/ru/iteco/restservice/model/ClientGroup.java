/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.compositid.ClientGroupId;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "cf_clientgroups")
public class ClientGroup {
    @EmbeddedId
    private ClientGroupId clientGroupId;

    @Column(name = "groupname")
    private String groupName;

    public ClientGroupId getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(ClientGroupId clientGroupId) {
        this.clientGroupId = clientGroupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientGroup)) {
            return false;
        }
        ClientGroup that = (ClientGroup) o;
        return Objects.equals(getClientGroupId(), that.getClientGroupId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClientGroupId());
    }
}
