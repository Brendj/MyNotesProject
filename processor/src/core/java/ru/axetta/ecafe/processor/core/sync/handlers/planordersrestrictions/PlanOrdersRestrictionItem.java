/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.planordersrestrictions;

import ru.axetta.ecafe.processor.core.persistence.PlanOrdersRestrictionType;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by i.semenov on 27.01.2020.
 */
public class PlanOrdersRestrictionItem {

    private Long idOfClient;
    private Long idOfOrg;
    private Long idOfContragent;
    private Long idOfConfigarationProvider;
    private String complexName;
    private Integer complexId;
    private PlanOrdersRestrictionType planType;
    private Long version;
    private boolean deletedState;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("PRI");

        element.setAttribute("ClientId", Long.toString(idOfClient));
        element.setAttribute("OrgId", Long.toString(idOfOrg));
        element.setAttribute("ConfId", Long.toString(idOfConfigarationProvider));
        element.setAttribute("ComplexName", complexName);
        element.setAttribute("ComplexId", Integer.toString(complexId));
        element.setAttribute("PlanType", Integer.toString(planType.ordinal()));
        element.setAttribute("V", Long.toString(version));
        element.setAttribute("D", deletedState ? "1" : "0");

        return element;
    }

    public static PlanOrdersRestrictionItem build(Node itemNode, Long orgOwner) {

        StringBuilder errorMessage = new StringBuilder();

        Long idOfOrg = getLongValue(itemNode, "OrgId", errorMessage);
        Long idOfClient = getLongValue(itemNode, "ClientId", errorMessage);
        Long idOfConfigurationProvider = getLongValue(itemNode, "ConfId", errorMessage);

    }

    private static Long getLongValue(Node itemNode, String attrName, StringBuilder sb) {
        Long result = null;
        String str = XMLUtils.getAttributeValue(itemNode, attrName);
        if(StringUtils.isNotEmpty(str)){
            try {
                result =  Long.parseLong(str);
            } catch (NumberFormatException e){
                sb.append("NumberFormatException OrgId not found");
            }
        } else {
            sb.append("Attribute OrgId not found");
        }
        return result;
    }
}
