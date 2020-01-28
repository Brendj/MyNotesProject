/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.planordersrestrictions;

import ru.axetta.ecafe.processor.core.persistence.PlanOrdersRestrictionType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by i.semenov on 27.01.2020.
 */
public class PlanOrdersRestrictionItem {

    private Long idOfClient;
    private Long idOfOrg;
    private Long idOfContragent;
    private Integer complexId;
    private PlanOrdersRestrictionType planType;
    private Long version;
    private boolean deletedState;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("PRI");

        element.setAttribute("ClientId", Long.toString(idOfClient));
        element.setAttribute("OrgId", Long.toString(idOfOrg));
        element.setAttribute("ContragentId", Long.toString(idOfContragent));
        element.setAttribute("ComplexId", Integer.toString(complexId));
        element.setAttribute("PlanType", Integer.toString(planType.ordinal()));
        element.setAttribute("V", Long.toString(version));
        element.setAttribute("D", deletedState ? "1" : "0");

        return element;
    }
}
