/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */
package ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * User: akmukov
 * Date: 29.03.2016
 */
public class CategoriesDiscountsAndRulesRequest {
    private static final String MANY_ORGS_FLAG="ManyOrgs";
    private final boolean isManyOrgs;

    private CategoriesDiscountsAndRulesRequest(boolean isManyOrgs) {

        this.isManyOrgs = isManyOrgs;
    }

    public boolean isManyOrgs() {
        return isManyOrgs;
    }

    public static CategoriesDiscountsAndRulesRequest build(Node sectionNode) {
        Integer value = XMLUtils.getIntegerAttributeValue(sectionNode, MANY_ORGS_FLAG);
        return new CategoriesDiscountsAndRulesRequest(value!=null && value == 1);
    }

}
