/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

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
    private Set<CategoryDiscountDTSZN> discounts;

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

    public Set<CategoryDiscountDTSZN> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Set<CategoryDiscountDTSZN> discounts) {
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

