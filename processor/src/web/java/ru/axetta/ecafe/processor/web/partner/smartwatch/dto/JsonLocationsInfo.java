/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

import ru.axetta.ecafe.processor.web.partner.smartwatch.MigrantInfo;
import ru.axetta.ecafe.processor.web.partner.smartwatch.OrgInformation;

import java.util.LinkedList;
import java.util.List;

public class JsonLocationsInfo {
    private String fio;
    private Long contractId;
    private Boolean hasActiveSmartWatch;
    private OrgInformation mainOrgInfo;
    private List<OrgInformation> friendlyOrgInfo;
    private List<MigrantInfo> migrants;

    public JsonLocationsInfo(String mainOrgName, String mainOrgAddress){
        this.mainOrgInfo = new OrgInformation(mainOrgName, mainOrgAddress);
    }

    public OrgInformation getMainOrgInfo() {
        return mainOrgInfo;
    }

    public void setMainOrgInfo(OrgInformation mainOrgInfo) {
        this.mainOrgInfo = mainOrgInfo;
    }

    public List<OrgInformation> getFriendlyOrgInfo() {
        if(friendlyOrgInfo == null){
            friendlyOrgInfo = new LinkedList<OrgInformation>();
        }
        return friendlyOrgInfo;
    }

    public void setFriendlyOrgInfo(List<OrgInformation> friendlyOrgInfo) {
        this.friendlyOrgInfo = friendlyOrgInfo;
    }

    public List<MigrantInfo> getMigrants() {
        if(migrants == null){
            migrants = new LinkedList<MigrantInfo>();
        }
        return migrants;
    }

    public void setMigrants(List<MigrantInfo> migrants) {
        this.migrants = migrants;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Boolean getHasActiveSmartWatch() {
        return hasActiveSmartWatch;
    }

    public void setHasActiveSmartWatch(Boolean hasActiveSmartWatch) {
        this.hasActiveSmartWatch = hasActiveSmartWatch;
    }
}
