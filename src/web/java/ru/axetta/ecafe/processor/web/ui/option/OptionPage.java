/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 11.11.11
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
public class OptionPage extends BasicWorkspacePage {

    private Option option;

    private Boolean withOperator;

    public Boolean getWithOperator() {
        return withOperator;
    }

    public void setWithOperator(Boolean withOperator) {
        this.withOperator = withOperator;
    }

    public String getPageFilename() {
        return "option/option";
    }

    public void fill(Session session) throws Exception {
        Criteria criteria = session.createCriteria(Option.class);
        criteria.add(Restrictions.eq("idOfOption", 2L));
        option = (Option)criteria.uniqueResult();
        withOperator = option.getOptionText().equals("1");
    }

    public void save(Session session) {
        option.setOptionText(withOperator.equals(true) ? "1" : "0");
        session.merge(option);
    }

    public void cancelOption() {
        withOperator = option.getOptionText().equals("1");
    }
}
