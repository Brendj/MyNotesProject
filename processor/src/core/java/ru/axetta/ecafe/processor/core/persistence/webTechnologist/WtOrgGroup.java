/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_org_groups")
public class WtOrgGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfOrgGroup")
    private Long idOfOrgGroup;

    @Column(name = "nameOfOrgGroup")
    private String nameOfOrgGroup;

    @Column(name = "createDate")
    private Date createDate;

    @Column(name = "lastUpdate")
    private Date lastUpdate;

    @Column(name = "deleteState")
    private Integer deleteState;

    @Column(name = "version")
    private Long version;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "create_by_id")
    private User createdUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "update_by_id")
    private User updatedUser;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idOfContragent")
    private Contragent contragent;

    @OneToMany(mappedBy = "wtOrgGroup")
    private List<WtComplex> wtComplexList;

    @OneToMany(mappedBy = "wtOrgGroup")
    private List<WtMenu> wtMenuList;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cf_wt_org_group_relations",
            joinColumns = @JoinColumn(name = "idOfOrgGroup"),
            inverseJoinColumns = @JoinColumn(name = "idOfOrg"))
    private List<Org> orgs = new ArrayList<>();

    public Long getIdOfOrgGroup() {
        return idOfOrgGroup;
    }

    public void setIdOfOrgGroup(Long idOfOrgGroup) {
        this.idOfOrgGroup = idOfOrgGroup;
    }

    public String getNameOfOrgGroup() {
        return nameOfOrgGroup;
    }

    public void setNameOfOrgGroup(String nameOfOrgGroup) {
        this.nameOfOrgGroup = nameOfOrgGroup;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Integer deleteState) {
        this.deleteState = deleteState;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public User getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
    }

    public User getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(User updatedUser) {
        this.updatedUser = updatedUser;
    }

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    public List<WtComplex> getWtComplexList() {
        return wtComplexList;
    }

    public void setWtComplexList(List<WtComplex> wtComplexList) {
        this.wtComplexList = wtComplexList;
    }

    public List<Org> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<Org> orgs) {
        this.orgs = orgs;
    }

    public List<WtMenu> getWtMenuList() {
        return wtMenuList;
    }

    public void setWtMenuList(List<WtMenu> wtMenuList) {
        this.wtMenuList = wtMenuList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtOrgGroup that = (WtOrgGroup) o;
        return Objects.equals(idOfOrgGroup, that.idOfOrgGroup) && Objects.equals(nameOfOrgGroup, that.nameOfOrgGroup)
                && Objects.equals(createDate, that.createDate) && Objects.equals(lastUpdate, that.lastUpdate) && Objects
                .equals(deleteState, that.deleteState) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfOrgGroup, nameOfOrgGroup, createDate, lastUpdate, deleteState, version);
    }
}
