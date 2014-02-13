/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.org.OrgShortItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.02.14
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("prototype")
public class OrgSelectListPage extends BasicPage {

    private final static Logger logger = LoggerFactory.getLogger(OrgSelectListPage.class);
    private String orgNameFilter;
    private String tagFilter;
    private final OrganizationTypeSwitchMenu organizationTypeSwitchMenu = new OrganizationTypeSwitchMenu();
    private OrganizationTypeSwitchMenu.OrganizationTypeSwitch organizationTypeSwitch;
    private List<OrgShortItem> items = Collections.emptyList();
    private final Queue<OrganizationListSelect> completeHandlerLists = new LinkedList<OrganizationListSelect>();

    public Object updateOrgsListSelectPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            //if (orgFilterOfSelectOrgListSelectPage.length() == 0) {
            //    orgListSelectPage.fill(persistenceSession, true);
            //} else {
            //    orgListSelectPage.fill(persistenceSession, orgFilterOfSelectOrgListSelectPage, true);
            //}
            //orgListSelectPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill org selection page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы выбора организации",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public Object completeOrgsListSelectionOk() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            completeOrgListSelection(true);
            //if (!modalPages.empty()) {
            //    if (modalPages.peek() == orgListSelectPage) {
            //        modalPages.pop();
            //    }
            //}
        } catch (Exception e) {
            logger.error("Failed to complete orgs selection", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при обработке выбора организаций", null));
        }
        return null;
    }

    public Object completeOrgsListSelectionCancel() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            completeOrgListSelection(false);
            //if (!modalPages.empty()) {
            //    if (modalPages.peek() == orgListSelectPage) {
            //        modalPages.pop();
            //    }
            //}
        } catch (Exception e) {
            logger.error("Failed to complete orgs selection", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при обработке выбора организаций", null));
        }
        return null;
    }

    public void completeOrgListSelection(boolean ok) throws Exception {
        //setFilterMode(0);

        //Map<Long, String> orgMap = null;
        //if (ok) {
        //    updateSelectedOrgs();
        //    orgMap = new HashMap<Long, String>();
        //    orgMap.putAll(selectedOrgs);
        //    /*for (Item item : items) {
        //        if (item.getSelected()) {
        //            orgMap.put(item.getIdOfOrg(), item.getShortName());
        //        }
        //    }*/
        //}
        if (!completeHandlerLists.isEmpty()) {
            completeHandlerLists.peek().select(new ArrayList<OrgShortItem>()); //completeOrgListSelection(orgMap);
            completeHandlerLists.poll();
        }
    }
    public Object clearOrgsListSelectedItemsList() {
        deselectAllItems();
        return null;
    }

    public Object selectAllOrgsListSelectedItemsList() {
        selectAllItems();
        return null;
    }

    public void deselectAllItems() {
        for (OrgShortItem item : items) {
            item.setSelected(false);
        }
    }

    public void selectAllItems() {
        for (OrgShortItem item : items) {
            item.setSelected(true);
        }
    }


    public OrganizationTypeSwitchMenu.OrganizationTypeSwitch getOrganizationTypeSwitch() {
        return organizationTypeSwitch;
    }

    public void setOrganizationTypeSwitch(OrganizationTypeSwitchMenu.OrganizationTypeSwitch organizationTypeSwitch) {
        this.organizationTypeSwitch = organizationTypeSwitch;
    }

    public List<OrgShortItem> getItems() {
        return items;
    }

    public OrganizationTypeSwitchMenu getOrganizationTypeSwitchMenu() {
        return organizationTypeSwitchMenu;
    }

    public String getOrgNameFilter() {
        return orgNameFilter;
    }

    public void setOrgNameFilter(String orgNameFilter) {
        this.orgNameFilter = orgNameFilter;
    }

    public String getTagFilter() {
        return tagFilter;
    }

    public void setTagFilter(String tagFilter) {
        this.tagFilter = tagFilter;
    }
}
