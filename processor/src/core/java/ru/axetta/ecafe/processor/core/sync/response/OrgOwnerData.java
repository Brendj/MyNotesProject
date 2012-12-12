/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 16.08.12
 * Time: 10:56
 * To change this template use File | Settings | File Templates.
 */
public class OrgOwnerData {

    private List<OrgOwner> orgOwnerList;

    public OrgOwnerData() {}

    public List<OrgOwner> getOrgOwnerList() {
        return orgOwnerList;
    }

    public void process(Session session, Long idOfOrg) throws Exception{
        List<OrgOwner> orgOwners = new LinkedList<OrgOwner>();
        orgOwners.addAll(DAOUtils.getOrgSourceByMenuExchangeRule(session, idOfOrg, false));
        //if(!orgOwners.isEmpty()){
        //}
        Org org = DAOUtils.findOrg(session, idOfOrg);
        orgOwners.add(new OrgOwner(org.getIdOfOrg(),org.getShortName(),org.getOfficialName(), true));
        orgOwners.addAll(DAOUtils.getOrgSourceByMenuExchangeRule(session, idOfOrg, true));
        orgOwnerList = orgOwners;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("OrgOwnerData");
        for (OrgOwner orgOwner : this.orgOwnerList) {
            element.appendChild(orgOwner.toElement(document));
        }
        return element;
    }
}
