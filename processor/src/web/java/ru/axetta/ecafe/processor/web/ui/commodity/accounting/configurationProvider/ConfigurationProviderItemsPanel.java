/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.12
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ConfigurationProviderItemsPanel extends BasicPage {

    private final static Logger logger = LoggerFactory.getLogger(ConfigurationProviderItemsPanel.class);
    private final Queue<ConfigurationProviderSelect> completeHandlerLists = new LinkedList<ConfigurationProviderSelect>();
    private List<ConfigurationProvider> configurationProviderList;
    private ConfigurationProvider selectConfigurationProvider;
    private String filter;

    @Autowired
    private DAOService daoService;
    @Autowired
    private ContextDAOServices contextDAOServices;

    public void pushCompleteHandler(ConfigurationProviderSelect handler) {
        completeHandlerLists.add(handler);
    }

    public Object addConfigurationProvider(){
        completeSelection(true);
        return null;
    }

    private void completeSelection(boolean flag){
        if (!completeHandlerLists.isEmpty()) {
            completeHandlerLists.peek().select(flag?selectConfigurationProvider:null);
            completeHandlerLists.poll();
        }
    }

    public Object cancel(){
        completeSelection(false);
        return null;
    }

    @PostConstruct
    public void reload() throws Exception {
        try {
            retrieveProduct();
            selectConfigurationProvider = new ConfigurationProvider();
            filter="";
        } catch (Exception e) {
            printError("Ошибка при загрузке страницы: "+e.getMessage());
            logger.error("Error load page", e);
        }
    }

    public Object updateConfigurationProviderSelectPage(){
        try {
            retrieveProduct();
        } catch (Exception e) {
            printError("Ошибка при загрузке страницы: "+e.getMessage());
            logger.error("Error load page", e);
        }
        return null;
    }

    private void retrieveProduct() throws Exception {
        User user = MainPage.getSessionInstance().getCurrentUser();
        if(user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification())){
            if(StringUtils.isEmpty(filter)){
                configurationProviderList = contextDAOServices.findConfigurationProviderByContragentSet(user.getIdOfUser());
            } else {
                configurationProviderList = contextDAOServices.findConfigurationProviderByContragentSet(user.getIdOfUser(), filter);
            }
        } else {
            if(StringUtils.isEmpty(filter)){
                configurationProviderList = daoService.findConfigurationProvidersList();
            } else {
                configurationProviderList = daoService.findConfigurationProvidersList(filter);
            }
        }
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<ConfigurationProvider> getConfigurationProviderList() {
        return configurationProviderList;
    }

    public void setConfigurationProviderList(List<ConfigurationProvider> configurationProviderList) {
        this.configurationProviderList = configurationProviderList;
    }

    public ConfigurationProvider getSelectConfigurationProvider() {
        return selectConfigurationProvider;
    }

    public void setSelectConfigurationProvider(ConfigurationProvider selectConfigurationProvider) {
        this.selectConfigurationProvider = selectConfigurationProvider;
    }

    public void printError(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
}
