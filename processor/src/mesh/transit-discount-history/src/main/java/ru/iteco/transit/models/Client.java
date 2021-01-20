/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.transit.models;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cf_clients")
public class Client {

    @Id
    @Column(name = "idofclient")
    private Long idOfClient;

    @Column(name = "meshguid")
    private String meshGuid;

    @Column(name = "agetypegroup")
    private String ageGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idoforg")
    private Org org;

    @ManyToMany
    @JoinTable(
            name = "cf_clients_categorydiscounts",
            joinColumns = @JoinColumn(name = "idofclient"),
            inverseJoinColumns = @JoinColumn(name = "idofcategorydiscount")
    )
    private List<CategoryDiscount> discounts;

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

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public List<CategoryDiscount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<CategoryDiscount> discounts) {
        this.discounts = discounts;
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
}

