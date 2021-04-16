/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.compositid;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ClientGroupId implements Serializable {
    @Column(name = "idofclientgroup")
    private Long idOfClientGroup;

    @Column(name = "idoforg")
    private Long idOfOrg;

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
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
        if (!(o instanceof ClientGroupId)) {
            return false;
        }
        ClientGroupId that = (ClientGroupId) o;
        return Objects.equals(getIdOfClientGroup(), that.getIdOfClientGroup()) && Objects
                .equals(getIdOfOrg(), that.getIdOfOrg());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdOfClientGroup(), getIdOfOrg());
    }
}
