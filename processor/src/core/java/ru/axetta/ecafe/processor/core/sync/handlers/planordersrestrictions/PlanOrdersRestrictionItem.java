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
    private Long idOfConfigarationProvider;
    private String complexName;
    private Integer complexId;
    private PlanOrdersRestrictionType planType;
    private Long version;
    private Boolean deletedState;

    public PlanOrdersRestrictionItem(Long idOfClient, Long idOfOrg, Long idOfConfigarationProvider, String complexName,
            Integer complexId, PlanOrdersRestrictionType planType, Long version, boolean deletedState) {
        this.idOfClient = idOfClient;
        this.idOfOrg = idOfOrg;
        this.idOfConfigarationProvider = idOfConfigarationProvider;
        this.complexName = complexName;
        this.complexId = complexId;
        this.planType = planType;
        this.version = version;
        this.deletedState = deletedState;
    }

    @Override
    public String toString() {
        return "{PlanOrdersRestriction: idOfClient=" + idOfClient == null ? "NULL" : idOfClient.toString() +
                ", idOfOrg=" + idOfOrg == null ? "NULL" : idOfOrg.toString() +
                ", complexId=" + complexId == null ? "NULL" : complexId.toString() + "}";
    }

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

    public static PlanOrdersRestrictionItem build(Node itemNode) {

        StringBuilder errorMessage = new StringBuilder();

        Long idOfOrg = getLongValue(itemNode, "OrgId", errorMessage);
        Long idOfClient = getLongValue(itemNode, "ClientId", errorMessage);
        Long idOfConfigurationProvider = getLongValue(itemNode, "ConfId", errorMessage);
        String complexName = XMLUtils.getAttributeValue(itemNode, "ComplexName");
        Integer complexId = null;
        Long complexIdLong = getLongValue(itemNode, "complexId", errorMessage);
        if (complexIdLong != null) complexId = complexIdLong.intValue();
        PlanOrdersRestrictionType planType = null;
        Long planTypeLong = getLongValue(itemNode, "planType", errorMessage);
        if (planTypeLong != null) planType = PlanOrdersRestrictionType.fromInteger(planTypeLong.intValue());
        Long version = getLongValue(itemNode, "V", errorMessage);
        String deletedStr = XMLUtils.getAttributeValue(itemNode, "D");
        Boolean deletedState = deletedStr == null ? false : deletedStr.equals("1");
        return new PlanOrdersRestrictionItem(idOfClient, idOfOrg, idOfConfigurationProvider, complexName, complexId, planType, version, deletedState);
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

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfConfigarationProvider() {
        return idOfConfigarationProvider;
    }

    public void setIdOfConfigarationProvider(Long idOfConfigarationProvider) {
        this.idOfConfigarationProvider = idOfConfigarationProvider;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Integer getComplexId() {
        return complexId;
    }

    public void setComplexId(Integer complexId) {
        this.complexId = complexId;
    }

    public PlanOrdersRestrictionType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanOrdersRestrictionType planType) {
        this.planType = planType;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(boolean deletedState) {
        this.deletedState = deletedState;
    }
}
