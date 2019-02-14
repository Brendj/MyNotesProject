/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

import java.util.LinkedList;
import java.util.List;

public class JsonLocationsInfo {
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
}
