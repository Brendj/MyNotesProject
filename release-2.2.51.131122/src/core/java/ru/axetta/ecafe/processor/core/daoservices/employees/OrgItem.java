/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.employees;

import ru.axetta.ecafe.processor.core.persistence.Org;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 16.08.13
 * Time: 18:44
 * To change this template use File | Settings | File Templates.
 */
public class OrgItem {

    private final Long idOfOrg;
    private String shortName;
    private String refectoryType;


    public OrgItem(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public OrgItem(Long idOfOrg, String shortName, Integer refectoryType) {
        this.idOfOrg = idOfOrg;
        this.shortName = shortName;
        if(refectoryType!=null && refectoryType>-1 && refectoryType<Org.REFECTORY_TYPE_NAMES.length){
            this.refectoryType = Org.REFECTORY_TYPE_NAMES[refectoryType];
        }
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getRefectoryType() {
        return refectoryType;
    }

}
