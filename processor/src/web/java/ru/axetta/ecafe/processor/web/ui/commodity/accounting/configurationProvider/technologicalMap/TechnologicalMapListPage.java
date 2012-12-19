/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ConfirmDeletePage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group.TechnologicalMapGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group.TechnologicalMapGroupSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class TechnologicalMapListPage extends BasicWorkspacePage implements ConfirmDeletePage.Listener ,
        TechnologicalMapGroupSelect, ConfigurationProviderSelect {

    private static final Logger logger = LoggerFactory.getLogger(TechnologicalMapListPage.class);


    private TechnologicalMap technologicalMap = new TechnologicalMap();
    private List<TechnologicalMap> technologicalMapList;
    private ConfigurationProvider selectedConfigurationProvider;

    private TechnologicalMapGroup selectedTechnologicalMapGroup;
    private Boolean deletedStatusSelected = Boolean.FALSE;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private TechnologicalMapGroupItemsPanel technologicalMapGroupItemsPanel;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;
    @Autowired
    private DAOService daoService;

    @Override
    public void onConfirmDelete(ConfirmDeletePage confirmDeletePage) {
        daoService.deleteEntity(confirmDeletePage.getEntity());
        reload();
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)",(getEmptyTechnologicalMap()?0:technologicalMapList.size()));
    }

    public Boolean getEmptyTechnologicalMap(){
        return technologicalMapList == null || technologicalMapList.isEmpty();
    }

    @Override
    public void onShow() throws Exception {}

    public Object onSearch() throws Exception{
        reload();
        return null;
    }

    public Object onClear() throws Exception{
        selectedConfigurationProvider = null;
        selectedTechnologicalMapGroup = null;
        return null;
    }

    public List<TechnologicalMap> getTechnologicalMapList() {
        return technologicalMapList;
    }

    public void reload() {
        String where = "";
        if(selectedConfigurationProvider!=null){
            where = " idOfConfigurationProvider=" + selectedConfigurationProvider.getIdOfConfigurationProvider();
        }
        if(selectedTechnologicalMapGroup!=null){
            where = (where.equals("")?"":where + " and ") + " technologicalMapGroup=:technologicalMapGroup";
        }
        where = (where.equals("")?"":" where ") + where;
        TypedQuery<TechnologicalMap> query = entityManager.createQuery("from TechnologicalMap " + where, TechnologicalMap.class);
        if(selectedTechnologicalMapGroup!=null){
            query.setParameter("technologicalMapGroup", selectedTechnologicalMapGroup);
        }
        technologicalMapList = query.getResultList();
    }

    public Object selectConfigurationProvider() throws Exception{
        configurationProviderItemsPanel.reload();
        if(selectedConfigurationProvider !=null){
            configurationProviderItemsPanel.setSelectConfigurationProvider(selectedConfigurationProvider);
        }
        configurationProviderItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(ConfigurationProvider configurationProvider) {
        selectedConfigurationProvider = configurationProvider;
    }

    public Object selectTechnologicalMapGroup() throws Exception{
        technologicalMapGroupItemsPanel.reload();
        technologicalMapGroupItemsPanel.setSelectTechnologicalMapGroup(selectedTechnologicalMapGroup);
        technologicalMapGroupItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(TechnologicalMapGroup technologicalMapGroup) {
        selectedTechnologicalMapGroup = technologicalMapGroup;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/technologicalMap/list";
    }

    public Boolean getDeletedStatusSelected() {
        return deletedStatusSelected;
    }

    public void setDeletedStatusSelected(Boolean deletedStatusSelected) {
        this.deletedStatusSelected = deletedStatusSelected;
    }

    public ConfigurationProvider getSelectedConfigurationProvider() {
        return selectedConfigurationProvider;
    }

    public void setSelectedConfigurationProvider(ConfigurationProvider selectedConfigurationProvider) {
        this.selectedConfigurationProvider = selectedConfigurationProvider;
    }

    public TechnologicalMapGroup getSelectedTechnologicalMapGroup() {
        return selectedTechnologicalMapGroup;
    }

    public void setSelectedTechnologicalMapGroup(TechnologicalMapGroup selectedTechnologicalMapGroup) {
        this.selectedTechnologicalMapGroup = selectedTechnologicalMapGroup;
    }
}
