/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.ConfigurationProviderService;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.12
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ConfigurationProviderListItemsPanel extends BasicPage {

    private final static Logger logger = LoggerFactory.getLogger(ConfigurationProviderListItemsPanel.class);
    private List<ConfigurationProviderItem> configurationProviderList = new ArrayList<ConfigurationProviderItem>();
    private String filter;
    private String selectedIds = "";
    private List<ConfigurationProviderItem> selectedList = new ArrayList<ConfigurationProviderItem>();
    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();

    @Autowired
    private ConfigurationProviderService service;

    public interface CompleteHandler {
        void completeConfigurationProviderListSelection(List<ConfigurationProviderItem> idOfConfigurationProvider) throws Exception;
    }

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.add(handler);
    }

    public void completeConfigurationProviderSelection() throws Exception {
        if (!completeHandlers.empty()) {
            selectedList.clear();
            for (ConfigurationProviderItem item : configurationProviderList) {
                if (item.getSelected()) selectedList.add(item);
            }
            completeHandlers.peek()
                    .completeConfigurationProviderListSelection(selectedList);
            completeHandlers.pop();
        }
    }

    public Object cancel(){
        if (!completeHandlers.empty()) {
            completeHandlers.pop();
        }
        return null;
    }

    public void reload(List<ConfigurationProviderItem> idOfConfigurationProviders) throws Exception {
        try {
            retrieveConfigurationProvider();
            for (ConfigurationProviderItem item : configurationProviderList) {
                if (idOfConfigurationProviders.contains(item)) {
                    item.setSelected(true);
                } else {
                    item.setSelected(false);
                }
            }
            selectedList = idOfConfigurationProviders;
            filter="";
        } catch (Exception e) {
            printError("Ошибка при загрузке страницы: "+e.getMessage());
            logger.error("Error load page", e);
        }
    }

    public Object updateConfigurationProviderSelectPage(){
        try {
            retrieveConfigurationProvider();
        } catch (Exception e) {
            printError("Ошибка при загрузке страницы: "+e.getMessage());
            logger.error("Error load page", e);
        }
        return null;
    }

    private void retrieveConfigurationProvider() throws Exception {
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<ConfigurationProvider> configurationProviders;
        if(user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification())){
            configurationProviders = service.findConfigurationProviderByContragentSet(user.getIdOfUser(), filter);
        } else {
            configurationProviders = service.findConfigurationProviderByName(filter);
        }
        configurationProviderList.clear();
        for (ConfigurationProvider cp : configurationProviders) {
            ConfigurationProviderItem it = new ConfigurationProviderItem(cp);
            if (selectedList.contains(it)) it.setSelected(true);
            configurationProviderList.add(it);
        }
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<ConfigurationProviderItem> getConfigurationProviderList() {
        return configurationProviderList;
    }

    public void setConfigurationProviderList(List<ConfigurationProviderItem> configurationProviderList) {
        this.configurationProviderList = configurationProviderList;
    }

    public void printError(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public String getSelectedIds() {
        return selectedIds;
    }

    public void setSelectedIds(String selectedIds) {
        this.selectedIds = selectedIds;
    }
}
