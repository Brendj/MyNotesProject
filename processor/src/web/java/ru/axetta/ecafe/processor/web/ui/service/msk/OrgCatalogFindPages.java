/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.partner.nsi.OrgMskNSIService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope("session")
public class OrgCatalogFindPages extends BasicWorkspacePage {
    @Resource
    OrgMskNSIService nsiService;
    
    String orgName;
    String guid;
    
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    List<OrgMskNSIService.OrgInfo> orgInfos = new LinkedList<OrgMskNSIService.OrgInfo>();

    public void updateList() {
        try {
            orgInfos = nsiService.getOrgByNameAndGuid(orgName, guid);
        } catch (Exception e) {
            super.logAndPrintMessage("Ошибка получения данных", e);
        }
    }

    public String getPageFilename() {
        return "service/msk/org_catalog_find_page";
    }

    public List<OrgMskNSIService.OrgInfo> getOrgInfos() {
        return orgInfos;
    }

}
