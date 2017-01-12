/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.06.2010
 * Time: 14:23:22
 * To change this template use File | Settings | File Templates.
 */
public class SelectedOrgGroupPage extends BasicWorkspacePage {

    private String shortName;

    public String getShortName() {
        return shortName;
    }

    public void fill(Session session, Long idOfOrg) throws Exception {

        if (idOfOrg == null) {
            this.shortName = null;
        } else {
        Org org = (Org) session.load(Org.class, idOfOrg);
            this.shortName = org.getShortName();
        }
    }

}