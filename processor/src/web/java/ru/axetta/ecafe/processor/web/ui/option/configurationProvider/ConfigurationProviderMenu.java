/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.web.ui.report.rule.ReportRuleConstants;

import org.hibernate.Session;

import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationProviderMenu {

    private SelectItem[] items;

    public void readAllItems(Session session) {
        List<ConfigurationProvider> list = session.createQuery("from ConfigurationProvider").list();
        items = new SelectItem[list.size()];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new SelectItem(list.get(i).getName());
        }
    }

    public SelectItem[] getItems() {
        return items;
    }
}