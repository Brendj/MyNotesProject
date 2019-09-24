/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.EZD;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.HashSet;
import java.util.Set;

public class RequestsEzd {

    private Long id;
    private Set<Org> Orgs = new HashSet<Org>();
    private String groupname;
    private Long dateappointment;
    private Long idofcomplex;
    private String complexname;
    private Integer complexcount;
    private String username;
    private Long createddate;
    private Long lastupdate;
    private Integer versionrecord;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Org> getOrgs() {
        return Orgs;
    }

    public void setOrgs(Set<Org> orgs) {
        Orgs = orgs;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public Long getDateappointment() {
        return dateappointment;
    }

    public void setDateappointment(Long dateappointment) {
        this.dateappointment = dateappointment;
    }

    public Long getIdofcomplex() {
        return idofcomplex;
    }

    public void setIdofcomplex(Long idofcomplex) {
        this.idofcomplex = idofcomplex;
    }

    public String getComplexname() {
        return complexname;
    }

    public void setComplexname(String complexname) {
        this.complexname = complexname;
    }

    public Integer getComplexcount() {
        return complexcount;
    }

    public void setComplexcount(Integer complexcount) {
        this.complexcount = complexcount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Long createddate) {
        this.createddate = createddate;
    }

    public Long getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(Long lastupdate) {
        this.lastupdate = lastupdate;
    }

    public Integer getVersionrecord() {
        return versionrecord;
    }

    public void setVersionrecord(Integer versionrecord) {
        this.versionrecord = versionrecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestsEzd)) {
            return false;
        }
        final RequestsEzd requestsEzd = (RequestsEzd) o;
        return id.equals(requestsEzd.getId());
    }

    @Override
    public int hashCode() {

        return id.hashCode();
    }
}
