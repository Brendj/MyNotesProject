/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary;

import ru.axetta.ecafe.processor.core.persistence.Org;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.12.12
 * Time: 19:50
 * To change this template use File | Settings | File Templates.
 */
public class OrgItem {
    private final Long idOfOrg;
    private final String shortName;

    public OrgItem(Org org) {
        this.idOfOrg = org.getIdOfOrg();
        this.shortName = org.getShortName();
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }
}
