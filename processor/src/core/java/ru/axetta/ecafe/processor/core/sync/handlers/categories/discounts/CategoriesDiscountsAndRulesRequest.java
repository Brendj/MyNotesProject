/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */
package ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * User: akmukov
 * Date: 29.03.2016
 */
public class CategoriesDiscountsAndRulesRequest implements SectionRequest {
    public static final String SECTION_NAME="CategoriesDiscountsAndRules";
    private static final String MANY_ORGS_FLAG="ManyOrgs";
    private final boolean isManyOrgs;
    private final Long versionDSZN;

    private CategoriesDiscountsAndRulesRequest(boolean isManyOrgs, Long versionDSZN) {
        this.isManyOrgs = isManyOrgs;
        this.versionDSZN = versionDSZN;
    }

    public boolean isManyOrgs() {
        return isManyOrgs;
    }

    public Long getVersionDSZN() {
        return versionDSZN;
    }

    public static CategoriesDiscountsAndRulesRequest build(Node sectionNode) {
        Integer value = XMLUtils.getIntegerAttributeValue(sectionNode, MANY_ORGS_FLAG);
        Long versionDSZN = XMLUtils.getLongAttributeValue(sectionNode, "DsznVersion");
        return new CategoriesDiscountsAndRulesRequest((value!=null && value == 1), versionDSZN);
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
