/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToOne
    @JoinColumn(name = "create_by_id")
    private User createdUser;

    @ManyToOne
    @JoinColumn(name = "update_by_id")
    private User updatedUser;

    @ManyToOne
    @JoinColumn(name = "idOfContragent")
    private Contragent contragent;

    @OneToMany(mappedBy = "wtOrgGroup")
    private Set<WtComplex> wtComplexes = new HashSet<>();

    @OneToMany(mappedBy = "wtOrgGroup")
    private Set<WtMenu> wtMenus = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cf_wt_org_group_relations",
            joinColumns = @JoinColumn(name = "idOfOrgGroup"),
            inverseJoinColumns = @JoinColumn(name = "idOfOrg"))
    private Set<Org> orgs = new HashSet<>();

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

    public Set<WtComplex> getWtComplexes() {
        return wtComplexes;
    }

    public void setWtComplexes(Set<WtComplex> wtComplexes) {
        this.wtComplexes = wtComplexes;
    }

    public Set<WtMenu> getWtMenus() {
        return wtMenus;
    }

    public void setWtMenus(Set<WtMenu> wtMenus) {
        this.wtMenus = wtMenus;
    }

    public Set<Org> getOrgs() {
        return orgs;
    }

    public void setOrgs(Set<Org> orgs) {
        this.orgs = orgs;
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
        return idOfOrgGroup.equals(that.getIdOfOrgGroup());
    }

    @Override
    public int hashCode() {
        return idOfOrgGroup.hashCode();
    }
}
