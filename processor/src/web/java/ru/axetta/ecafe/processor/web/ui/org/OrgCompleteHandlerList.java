/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 */

public class OrgCompleteHandlerList implements OrgListSelectPage.CompleteHandlerList {

    private String orgFilter = "Не выбрано";
    private List<OrgItem> orgItems = new ArrayList<OrgItem>(0);

    public String getOrgFilter() {
        return orgFilter;
    }

    public List<OrgItem> getOrgItems() {
        return orgItems;
    }

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            orgItems = new ArrayList<OrgItem>();
            if (orgMap.isEmpty()) {
                orgFilter = "Не выбрано";
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (Long idOfOrg : orgMap.keySet()) {
                    OrgItem orgItem = new OrgItem();
                    orgItem.setIdOfOrg(idOfOrg);
                    orgItem.setShortName(orgMap.get(idOfOrg));
                    //orgItems.add(new OrgItem(idOfOrg, orgMap.get(idOfOrg)));
                    stringBuilder.append(orgMap.get(idOfOrg)).append("; ");
                }
                orgFilter = stringBuilder.substring(0, stringBuilder.length() - 2);
            }
        }
    }
}
