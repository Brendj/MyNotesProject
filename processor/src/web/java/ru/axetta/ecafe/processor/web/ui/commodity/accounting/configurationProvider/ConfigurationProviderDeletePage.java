/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 16.05.12
 * Time: 0:19
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ConfigurationProviderDeletePage extends BasicPage {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationProviderDeletePage.class);
    @Autowired
    private DAOService daoService;
    private ConfigurationProvider configurationProvider;

    public void remove(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            if(daoService.isEmptyOrgConfigurationProvider(configurationProvider)) {
                daoService.removeConfigurationProvider(configurationProvider);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Конфигурация удалена успешно", null));
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Конфигурация имеет в себе список организаций", null));
            }
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Не возможно удалить Конфигурацию", null));
            logger.error("Error delete Configuration Provider: ",e);
        }
    }

    public ConfigurationProvider getConfigurationProvider() {
        return configurationProvider;
    }

    public void setConfigurationProvider(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }
}
