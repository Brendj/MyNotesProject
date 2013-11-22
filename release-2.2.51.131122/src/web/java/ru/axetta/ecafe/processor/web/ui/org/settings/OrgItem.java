/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.persistence.Org;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.04.13
 * Time: 17:15
 * To change this template use File | Settings | File Templates.
 */
public class OrgItem {
    private final Long idOfOrg;
    private final String shortName;
    private final String officialName;

    public OrgItem(Org org) {
        this.idOfOrg = org.getIdOfOrg();
        this.shortName = org.getShortName();
        this.officialName = org.getOfficialName();
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }

    public String getOfficialName() {
        return officialName;
    }
}
