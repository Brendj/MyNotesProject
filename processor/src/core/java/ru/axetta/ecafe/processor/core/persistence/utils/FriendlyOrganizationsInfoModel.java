/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 12.11.14
 * Time: 17:31
 */

public class FriendlyOrganizationsInfoModel {
    private Long idOfOrg;
    private String officialName;
    private String address;
    private Set<Org> friendlyOrganizationsSet;

    public FriendlyOrganizationsInfoModel() {
    }

    public FriendlyOrganizationsInfoModel(Long idOfOrg, String officialName, String address,
            Set<Org> friendlyOrganizationsSet) {
        this.idOfOrg = idOfOrg;
        this.officialName = officialName;
        this.address = address;
        this.friendlyOrganizationsSet = friendlyOrganizationsSet;
    }

    public FriendlyOrganizationsInfoModel(Long idOfOrg, Set<Org> friendlyOrganizationsSet) {
        this.idOfOrg = idOfOrg;
        this.friendlyOrganizationsSet = friendlyOrganizationsSet;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Org> getFriendlyOrganizationsSet() {
        return friendlyOrganizationsSet;
    }

    public void setFriendlyOrganizationsSet(Set<Org> friendlyOrganizationsSet) {
        this.friendlyOrganizationsSet = friendlyOrganizationsSet;
    }
}
