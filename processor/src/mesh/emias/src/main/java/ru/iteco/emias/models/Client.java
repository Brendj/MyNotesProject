/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.emias.models;

import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_clients")
public class Client {
    @Id
    @Column(name = "idofclient")
    private Long idOfClient;

    @WhereJoinTable(clause = "deletedstate = false")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cf_client_guardian",
            joinColumns = @JoinColumn(name = "idofchildren"),
            inverseJoinColumns = @JoinColumn(name = "idofguardian")
    )
    private Set<Client> guardians;

    @Column(name = "meshguid")
    private String meshGuid;

    @Column(name = "agetypegroup")
    private String ageGroup;

    @Column(name = "idoforg", insertable = false, updatable = false)
    private Long idoforg;

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getMeshGuid() {
        return meshGuid;
    }

    public void setMeshGuid(String meshGuid) {
        this.meshGuid = meshGuid;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Set<Client> getGuardians() {
        return guardians;
    }

    public void setGuardians(Set<Client> guardians) {
        this.guardians = guardians;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        Client client = (Client) o;
        return Objects.equals(idOfClient, client.idOfClient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfClient);
    }

    public Long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(Long idoforg) {
        this.idoforg = idoforg;
    }
}
