/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.group;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupItemsPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
public class TechnologicalMapGroupListPage extends BasicWorkspacePage implements ConfigurationProviderSelect {

    private static final Logger logger = LoggerFactory.getLogger(TechnologicalMapGroupCreatePage.class);
    private List<TechnologicalMapGroup> technologicalMapGroupList;
    private Boolean deletedStatusSelected = Boolean.FALSE;
    private ConfigurationProvider selectedConfigurationProvider;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private TechnologicalMapGroupItemsPanel technologicalMapGroupItemsPanel;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;

    @Override
    public void onShow() { }

    public Object onSearch() throws Exception{
        reload();
        return null;
    }

    public Object onClear() throws Exception{
        selectedConfigurationProvider = null;
        return null;
    }

    @Transactional
    private void reload() throws Exception{
        String where = "";
        if(selectedConfigurationProvider!=null){
            where = " where idOfConfigurationProvider=" + selectedConfigurationProvider.getIdOfConfigurationProvider();
        }
        TypedQuery<TechnologicalMapGroup> query = entityManager.createQuery("from TechnologicalMapGroup "+where+" ORDER BY globalId", TechnologicalMapGroup.class);
        technologicalMapGroupList = query.getResultList();
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

    public String getPageFilename() {
        return "option/configuration_provider/technologicalMap/group/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", (getEmptyTechnologicalMapGroup()?0:technologicalMapGroupList.size()));
    }

    public List<TechnologicalMapGroup> getTechnologicalMapGroupList() {
        return technologicalMapGroupList;
    }

    public Boolean getEmptyTechnologicalMapGroup(){
        return technologicalMapGroupList == null || technologicalMapGroupList.isEmpty();
    }

    public void setTechnologicalMapGroupList(List<TechnologicalMapGroup> technologicalMapGroupList) {
        this.technologicalMapGroupList = technologicalMapGroupList;
    }

    public ConfigurationProvider getSelectedConfigurationProvider() {
        return selectedConfigurationProvider;
    }

    public void setSelectedConfigurationProvider(ConfigurationProvider selectedConfigurationProvider) {
        this.selectedConfigurationProvider = selectedConfigurationProvider;
    }

    public Boolean getDeletedStatusSelected() {
        return deletedStatusSelected;
    }

    public void setDeletedStatusSelected(Boolean deletedStatusSelected) {
        this.deletedStatusSelected = deletedStatusSelected;
    }

}
