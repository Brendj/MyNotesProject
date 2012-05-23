/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
//import ru.axetta.ecafe.processor.web.ui.report.productGuide.Item;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 15.05.12
 * Time: 20:59
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationProviderCreatePage extends BasicWorkspacePage {

    private Item item = new Item();

    public Item getItem() {
        return item;
    }

    public void create(Session persistenceSession) {
        ConfigurationProvider cp = new ConfigurationProvider();
        cp.setName(item.getName());
        persistenceSession.save(cp);
    }

    public String getPageFilename() {
        return "option/configuration_provider/create";
    }

    public String getPageTitle() {
        return super.getPageTitle();
    }
}
