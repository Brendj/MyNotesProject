/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
/*import ru.axetta.ecafe.processor.core.persistence.ProductGuide;
import ru.axetta.ecafe.processor.core.persistence.User;*/
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 15.05.12
 * Time: 22:21
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationProviderListPage extends BasicWorkspacePage {

    private List<Item> items = Collections.emptyList();

    public String getPageFilename() {
        return "option/configuration_provider/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    public void fill(Session persistenceSession) {
        List list = DAOUtils.findConfigurationProvider(persistenceSession);
        items = new ArrayList<Item>();
        for (Object o : list) {
            ConfigurationProvider cp = (ConfigurationProvider)o;
            items.add(new Item(cp.getIdOfConfigurationProvider(), cp.getName(), cp.getProducts()));
        }
    }

    public void remove(Session session, long id) {
        ConfigurationProvider configurationProvider = (ConfigurationProvider)session.load(ConfigurationProvider.class, id);
        session.delete(configurationProvider);
        fill(session);
    }
}
