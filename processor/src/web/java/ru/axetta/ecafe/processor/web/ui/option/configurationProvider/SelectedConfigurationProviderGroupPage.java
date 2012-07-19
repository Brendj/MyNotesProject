/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 16.05.12
 * Time: 22:11
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class SelectedConfigurationProviderGroupPage extends BasicWorkspacePage {

    private String title;
    private Long idOfConfigurationProvider;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() throws Exception {
        ConfigurationProvider configurationProvider = entityManager.find(ConfigurationProvider.class, idOfConfigurationProvider);
        if (null == configurationProvider) {
            this.title = null;
        } else {
            this.title = String.format("%d: %s", configurationProvider.getIdOfConfigurationProvider(),
                    configurationProvider.getName());
        }
    }

    public String getTitle() {
        return title;
    }

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }
}
