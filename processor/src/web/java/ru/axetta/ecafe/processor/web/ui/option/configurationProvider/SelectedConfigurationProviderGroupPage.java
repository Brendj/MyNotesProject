/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 16.05.12
 * Time: 22:11
 * To change this template use File | Settings | File Templates.
 */
public class SelectedConfigurationProviderGroupPage extends BasicWorkspacePage {

    //selectItems

    private String title;

    public String getTitle() {
        return title;
    }

    public void fill(Session session, Long id) throws Exception {
        ConfigurationProvider cp = (ConfigurationProvider)DAOUtils.findConfigurationProvider(session, id);
        if (null == cp) {
            this.title = null;
        } else {
            this.title = String.format("%d: %s", cp.getIdOfConfigurationProvider(), cp.getName());
        }
    }

}
