/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.rule;

import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.classic.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class SelectedRuleGroupPage extends BasicWorkspacePage {
    private String name;

    public String getName() {
        return name;
    }

    public void fill(Session session, Long idOfRule) throws Exception {
        DiscountRule discountRule = (DiscountRule) session.load(DiscountRule.class, idOfRule);
        this.name = discountRule.getDescription();
    }

}
