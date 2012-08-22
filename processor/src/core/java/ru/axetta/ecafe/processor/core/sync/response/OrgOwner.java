/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 16.08.12
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
 */
public class OrgOwner {

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("OrgOwner");
        element.setAttribute("IdOfOrg", Long.toString(this.idOfOrg));
        element.setAttribute("ShortName", shortName);
        element.setAttribute("OfficialName", officialName);
        element.setAttribute("IsSupplier", supplier?"1":"0");
        return element;
    }

    private final Long idOfOrg;
    private final String shortName;
    private final String officialName;
    /* логическое значение определяющее является лми данная организация поставщиком: false -не является , true - является  */
    private final Boolean supplier;

    public OrgOwner(Long idOfOrg, String shortName, String officialName, Boolean supplier) {
        this.idOfOrg = idOfOrg;
        this.shortName = shortName;
        this.officialName = officialName;
        this.supplier = supplier;
    }

    public Boolean getSupplier() {
        return supplier;
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

    @Override
    public String toString() {
        return String.format("OrgOwner{ idOfOrg= %d, shortName='%s', officialName='%s' }  ", idOfOrg, shortName, officialName);
    }
}
