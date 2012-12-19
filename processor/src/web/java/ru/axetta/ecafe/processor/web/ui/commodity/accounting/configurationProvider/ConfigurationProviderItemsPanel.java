/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    private final Queue<ConfigurationProviderSelect> completeHandlerLists = new LinkedList<ConfigurationProviderSelect>();

    private List<ConfigurationProvider> configurationProviderList;
    private ConfigurationProvider selectConfigurationProvider;
    private String filter;

    @PersistenceContext
    private EntityManager entityManager;

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
        //configurationProviderList = new ArrayList<ConfigurationProvider>();
        configurationProviderList = retrieveProduct();
        selectConfigurationProvider = new ConfigurationProvider();
        filter="";
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
