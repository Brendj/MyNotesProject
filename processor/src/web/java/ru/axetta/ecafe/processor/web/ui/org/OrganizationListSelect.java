/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.daoservices.org.OrgShortItem;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.02.14
 * Time: 10:23
 * To change this template use File | Settings | File Templates.
 */
public interface OrganizationListSelect {

    public void select(List<OrgShortItem> orgShortItem);

}
