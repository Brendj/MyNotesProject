/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.daoservices.org.OrgShortItem;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 02.11.11
 */
public class OrgListSelectPage extends OrgSelectionBasicPage {

    private final Stack<CompleteHandlerList> completeHandlerLists = new Stack<CompleteHandlerList>();
    private Map<Long, String> selectedOrgs = new HashMap<Long, String>();

    public void pushCompleteHandlerList(CompleteHandlerList handlerList) {
        completeHandlerLists.push(handlerList);
    }

    public void completeOrgListSelection(boolean ok) throws Exception {
        resetAvailableOrganizationTypes();
        setFilterMode(0);

        Map<Long, String> orgMap = null;
        if (ok) {
            updateSelectedOrgs();
            orgMap = new HashMap<Long, String>(selectedOrgs);
        }
        if (!completeHandlerLists.empty()) {
            completeHandlerLists.peek().completeOrgListSelection(orgMap);
            completeHandlerLists.pop();
        }
        MainPage.getSessionInstance().resetOrgFilterPageName();
    }

    public void fill(Session session, String orgFilter, Boolean isUpdate, List<Long> idOfContragentOrgList,
        List<Long> idOfContragentList, MainPage mainPage, Boolean webARM) throws Exception {
        if (isUpdate) {
            updateSelectedOrgs();
            mainPage.setOrgFilterOfSelectOrgListSelectPage(StringUtils.join(selectedOrgs.values(), ","));
        } else {
            selectedOrgs.clear();
        }
        String[] idOfOrgs = orgFilter.split(",");
        Set<String> longSet = new HashSet<String>(Arrays.asList(idOfOrgs));
        ///
        for (String sId : longSet) {
            try {
                Long id = Long.parseLong(sId.trim());
                if (selectedOrgs.containsKey(id)) {
                    continue;
                }
                Org org = (Org) session.get(Org.class, id);
                selectedOrgs.put(id, org.getShortName());
            } catch (Exception ignored) {
            }
        }
        ///
        List<OrgShortItem> items = retrieveOrgs(session, idOfContragentOrgList, idOfContragentList, webARM);
        for (OrgShortItem orgShortItem : items) {
            orgShortItem.setSelected(selectedOrgs.containsKey(orgShortItem.getIdOfOrg()));
        }
        this.items = items;
    }

    public void fill(Session session, Boolean isUpdate, List<Long> idOfContragentOrgList, List<Long> idOfContragentList,
            Boolean webARM)
            throws Exception {
        if (isUpdate) {
            updateSelectedOrgs();
        } else {
            selectedOrgs.clear();
        }
        List<OrgShortItem> items = retrieveOrgs(session, idOfContragentOrgList, idOfContragentList, webARM);
        for (OrgShortItem orgShortItem : items) {
            orgShortItem.setSelected(selectedOrgs.containsKey(orgShortItem.getIdOfOrg()));
        }
        this.items = items;
    }

    private void updateSelectedOrgs() {
        for (OrgShortItem i : this.getItems()) {
            if (i.getSelected()) {
                selectedOrgs.put(i.getIdOfOrg(), i.getShortName());
            } else {
                selectedOrgs.remove(i.getIdOfOrg());
            }
        }
    }

    public void clearSelectedOrgMap() {
        selectedOrgs.clear();
    }

    public interface CompleteHandlerList {

        void completeOrgListSelection(Map<Long, String> orgMap) throws Exception;
    }

    public Map<Long, String> getSelectedOrgs() {
        return selectedOrgs;
    }

    public void setSelectedOrgs(Map<Long, String> selectedOrgs) {
        this.selectedOrgs = selectedOrgs;
    }

    public String getSelectedOrgsString() {
        String s = "";
        for (String org : getSelectedOrgs().values()) {
            s += org + ", ";
        }
        if (s.length() > 2) {
            return s.substring(0, s.length() - 2);
        }
        return s;
    }
}
