/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgEditPage;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.LinkedList;
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
public class ConfigurationProviderItemsPanel extends BasicPage {

    private final Stack<ConfigurationProviderSelect> completeHandlerLists = new Stack<ConfigurationProviderSelect>();

    private List<ConfigurationProvider> configurationProviderList = new ArrayList<ConfigurationProvider>();
    private ConfigurationProvider selectConfigurationProvider = new ConfigurationProvider();
    private String filter="";

    @PersistenceContext
    private EntityManager entityManager;

    public void pushCompleteHandler(ConfigurationProviderSelect handler) {
        completeHandlerLists.push(handler);
    }

    public Object addConfigurationProvider(){
        if (!completeHandlerLists.empty()) {
            completeHandlerLists.peek().select(selectConfigurationProvider);
            completeHandlerLists.pop();
        }
        return null;
    }

    public void reload() throws Exception {
        configurationProviderList = retrieveProduct();
    }

    public Object updateConfigurationProviderSelectPage(){
        configurationProviderList = retrieveProduct();
        return null;
    }

    @Transactional
    private List<ConfigurationProvider> retrieveProduct(){
        String where="";
        if (StringUtils.isNotEmpty(filter)){
            where = "where UPPER(name) like '%"+filter.toUpperCase()+"%'";
        }
        String query = "from ConfigurationProvider "+ where + " order by id";
        return entityManager.createQuery(query, ConfigurationProvider.class).getResultList();
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

}
