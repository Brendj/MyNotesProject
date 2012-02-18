/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.discountrule;

import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class RuleDeletePage extends BasicPage {
    public void removeRule(Session session, Long id) throws Exception {
        DiscountRule discountRule = (DiscountRule) session.get(DiscountRule.class, id);
        session.delete(discountRule);
    }
}
