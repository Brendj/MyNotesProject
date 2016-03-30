/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts;

import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * User: akmukov
 * Date: 29.03.2016
 */
public class CategoriesDiscountsAndRulesBuilder {
    public static final String CATEGORIES_DISCOUNTS_AND_RULES_SECTION="CategoriesDiscountsAndRules";
    private Node mainNode;

    public void createMainNode(Node envelopeNode) {
        mainNode = findFirstChildElement(envelopeNode, CATEGORIES_DISCOUNTS_AND_RULES_SECTION);
    }

    public CategoriesDiscountsAndRulesRequest build() {
        if (mainNode != null) {
            return CategoriesDiscountsAndRulesRequest.build(mainNode);
        } else {
            return null;
        }
    }

    public CategoriesDiscountsAndRulesRequest build(Node sectionNode) {
        return CategoriesDiscountsAndRulesRequest.build(sectionNode);
    }
}
