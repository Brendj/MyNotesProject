/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

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

    private final List<OrgOwner> orgOwnerList = new LinkedList<OrgOwner>();

    public void addOrgOwner(OrgOwner orgOwner){
        orgOwnerList.add(orgOwner);
    }

    public Iterator<OrgOwner> getOrgOwners(){
        return orgOwnerList.iterator();
    }

}
