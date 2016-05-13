/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 23.12.15
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
public class OrganizationStructureRequest {
    private final Long maxVersion;
    private final boolean isAllOrgs;

    private OrganizationStructureRequest(Long maxVersion, boolean isAllOrgs) {
        this.maxVersion = maxVersion;
        this.isAllOrgs = isAllOrgs;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public boolean isAllOrgs() {
        return isAllOrgs;
    }

    static OrganizationStructureRequest build(Node organizationStructureRequestNode) throws Exception {
        final Long maxVersion = XMLUtils.getLongAttributeValue(organizationStructureRequestNode, "V");
        Boolean isAllOrgsValue = XMLUtils.getBooleanAttributeValue(organizationStructureRequestNode, "IsAllOrgs");
        if (isAllOrgsValue == null){
            isAllOrgsValue = false;
        }
        final boolean isAllOrgs = isAllOrgsValue;
        return new OrganizationStructureRequest(maxVersion, isAllOrgs);
    }

}
