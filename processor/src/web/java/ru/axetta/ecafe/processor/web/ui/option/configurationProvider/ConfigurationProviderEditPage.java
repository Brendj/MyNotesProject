/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 16.05.12
 * Time: 0:19
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationProviderEditPage extends BasicWorkspacePage {

    private Item item;

    public Item getItem() {
        return item;
    }

    public void fill(Session persistenceSession, Long id) {
        ConfigurationProvider cp = (ConfigurationProvider)DAOUtils.findConfigurationProvider(persistenceSession, id);
        item = new Item(cp.getIdOfConfigurationProvider(), cp.getName(), cp.getProducts());
    }

    public void update(Session persistenceSession) {
        ConfigurationProvider cp = new ConfigurationProvider();
        cp.setIdOfConfigurationProvider(item.getIdOfConfigurationProvider());
        cp.setName(item.getName());
        persistenceSession.update(cp);
    }

    public String getPageFilename() {
        return "option/configuration_provider/edit";
    }

    public String getPageTitle() {
        return super.getPageTitle();
    }
}
