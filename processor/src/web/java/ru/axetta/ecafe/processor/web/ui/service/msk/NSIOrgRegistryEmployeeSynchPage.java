/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.persistence.ClientGuardianHistory;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.service.RegistryChangeCallback;
import ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeRevisionItem;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 01.10.13
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class NSIOrgRegistryEmployeeSynchPage extends NSIOrgRegistrySyncPageBase implements OrgSelectPage.CompleteHandler {
    protected Org org;
    protected String orgName;

    public String getPageFilename() {
        return "service/msk/nsi_org_registry_employee_sync_page";
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (idOfOrg == null) {
            this.setOrgName(null);
            this.setOrg(null);
        } else {
            this.setOrg((Org) session.load(Org.class, idOfOrg));
            this.setOrgName(getOrg().getShortName());
        }
    }

    public String getPageTitle() {
        return "Синхронизация сотрудников с Реестрами";
    }

    @Override
    public long getIdOfOrg() {
        if (getOrg() == null) {
            return -1L;
        }
        return getOrg().getIdOfOrg();
    }

    @Override
    public boolean getDisplayOrgSelection() {
        return true;
    }

    @Override
    public boolean getShowErrorEditPanel () {
        return true;
    }

    @Override
    protected List<ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItemV2> loadChangedItems() {
        showOnlyClientGoups = false;
        return frontControllerProcessor.
                loadRegistryChangeItemsEmployeeV2(getIdOfOrg(), revisionCreateDate, actionFilter, nameFilter);
    }

    @Override
    protected List<RegistryChangeRevisionItem> loadRevisionsFromController (Long idOfOrg) {
        return frontControllerProcessor.loadRegistryEmployeeChangeRevisions(idOfOrg);
    }

    @Override
    protected List<RegistryChangeCallback> proceedRegistryChangeItemInternal(List<Long> list, int operation,
            boolean fullNameValidation, ClientGuardianHistory clientGuardianHistory) {
        return frontControllerProcessor.proceedRegistryEmployeeChangeItem(list, ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItem.APPLY_REGISTRY_CHANGE, fullNameValidation, null,
                clientGuardianHistory);
    }

    @Override
    protected List<ru.axetta.ecafe.processor.web.internal.front.items.RegistryChangeItemV2> refreshRegistryChangeItemsV2(long idOfOrg) throws Exception {
        return frontControllerProcessor.refreshRegistryChangeEmployeeItemsV2(idOfOrg);
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}