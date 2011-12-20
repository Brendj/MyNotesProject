/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 11.11.11
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationPage extends BasicWorkspacePage {
    private Option option;

    private String configurationText;

    public String getConfigurationText() {
        return configurationText;
    }

    public void setConfigurationText(String configurationText) {
        this.configurationText = configurationText;
    }

    public String getPageFilename() {
        return "option/configuration";
    }

    public void fill(Session session) throws Exception {
        Criteria criteria = session.createCriteria(Option.class);
        criteria.add(Restrictions.eq("idOfOption", 1L));
        option = (Option)criteria.uniqueResult();
        configurationText = option.getOptionText();
    }

    public void save(Session session) {
        option.setOptionText(configurationText);
        session.merge(option);
    }

    public void cancelConfiguration() {
        configurationText = option.getOptionText();
    }
}
