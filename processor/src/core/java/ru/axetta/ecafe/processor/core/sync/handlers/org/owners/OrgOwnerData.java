/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.org.owners;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 16.08.12
 * Time: 10:56
 * To change this template use File | Settings | File Templates.
 */
public class OrgOwnerData extends AbstractToElement {

    private final List<OrgOwner> orgOwnerList;

    public OrgOwnerData(List<OrgOwner> orgOwnerList) {
        this.orgOwnerList = orgOwnerList;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("OrgOwnerData");
        for (OrgOwner orgOwner : this.orgOwnerList) {
            element.appendChild(orgOwner.toElement(document));
        }
        return element;
    }
}
