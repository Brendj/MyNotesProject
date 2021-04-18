/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.enums.Gender;

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

    @Column(name = "contractid")
    private Long contractId;

    @Column(name = "meshguid")
    private String meshGuid;

    @Column(name = "agetypegroup")
    private String ageGroup;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "balance")
    private Long balance;

    @Column(name = "gender")
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "idoforg", insertable = false, updatable = false)
    private Org org;

    @ManyToMany
    @JoinTable(
            name = "cf_clients_categorydiscounts",
            joinColumns = @JoinColumn(name = "idofclient"),
            inverseJoinColumns = @JoinColumn(name = "idofcategorydiscount")
    )
    private List<CategoryDiscount> discounts;

    @OneToMany
    @JoinColumn(name = "idofchildren")
    private Set<ClientGuardian> guardians;

    @OneToMany
    @JoinColumn(name = "idofguardian")
    private Set<ClientGuardian> childrens;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "idofclientgroup"),
            @JoinColumn(name = "idoforg")
    })
    private ClientGroup clientGroup;

    @OneToOne
    @JoinColumn(name = "idofperson")
    private Person person;

    public Client() {
    }

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

    public Set<ClientGuardian> getGuardians() {
        return guardians;
    }

    public void setGuardians(Set<ClientGuardian> guardians) {
        this.guardians = guardians;
    }

    public Set<ClientGuardian> getChildrens() {
        return childrens;
    }

    public void setChildrens(Set<ClientGuardian> childrens) {
        this.childrens = childrens;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public ClientGroup getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(ClientGroup clientGroup) {
        this.clientGroup = clientGroup;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
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
