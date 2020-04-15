/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.spb;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */
@Component
@Scope("session")
public class SpbRegistrySyncPage extends SpbRegistrySyncPageBase implements OrgSelectPage.CompleteHandler {
    Org org;
    String orgName;


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