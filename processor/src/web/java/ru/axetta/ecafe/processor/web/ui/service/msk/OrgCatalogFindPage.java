/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.partner.nsi.MskNSIService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope("session")
public class OrgCatalogFindPage extends BasicWorkspacePage {
    @Resource
    MskNSIService nsiService;
    
    String orgName;
    
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgName() {
        return orgName;
    }

    List<MskNSIService.OrgInfo> orgInfos = new LinkedList<MskNSIService.OrgInfo>();

    public void updateList() {
        try {
            orgInfos = nsiService.getOrgByName(orgName);
        } catch (Exception e) {
            super.logAndPrintMessage("Ошибка получения данных", e);
        }
    }

    public String getPageFilename() {
        return "service/msk/org_catalog_find_page";
    }

    public List<MskNSIService.OrgInfo> getOrgInfos() {
        return orgInfos;
    }

}
