/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 07.10.13
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class NSIOrgRegistrySynchPage extends NSIOrgRegistrySyncPageBase implements OrgSelectPage.CompleteHandler {
    private Org org;
    private String orgName;


    public String getOrgName() {
        return orgName;
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (idOfOrg == null) {
            this.orgName = null;
            this.org = null;
        } else {
            this.org = (Org) session.load(Org.class, idOfOrg);
            this.orgName = org.getShortName();
        }
    }

    public String getPageTitle() {
        return "Синхронизация организации с Реестрами";
    }

    @Override
    public long getIdOfOrg() {
        if (org == null) {
            return -1L;
        }
        return org.getIdOfOrg();
    }

    @Override
    public boolean getDisplayOrgSelection() {
        return true;
    }

    @Override
    public boolean getShowErrorEditPanel () {
        return true;
    }
}