/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

    @Autowired
    private DAOReadonlyService daoService;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;
    @Autowired
    private ContextDAOServices contextDAOServices;

    @Override
    public void onShow() { }

    public Object onSearch(){
        try {
            reload();
        } catch (Exception e) {
            printError(String.format("Ошибка при загрузке данных: %s", e.getMessage()));
            logger.error("TechnologicalMapGroup onSearch error: ", e);
        }
        return null;
    }

    public Object onClear() throws Exception{
        selectedConfigurationProvider = null;
        return null;
    }

    private void reload() throws Exception{
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<Long> orgOwners = contextDAOServices.findOrgOwnersByContragentSet(user.getIdOfUser());
        if(selectedConfigurationProvider!=null){
            if(!user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification()) && (orgOwners==null || orgOwners.isEmpty())){
                technologicalMapGroupList = daoService.findTechnologicalMapGroupByConfigurationProvider(
                        selectedConfigurationProvider.getIdOfConfigurationProvider(), deletedStatusSelected);
            } else {
                technologicalMapGroupList = daoService.findTechnologicalMapGroupByConfigurationProvider(
                        selectedConfigurationProvider.getIdOfConfigurationProvider(), orgOwners, deletedStatusSelected);
            }
        } else {
            if(!user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification()) && (orgOwners==null || orgOwners.isEmpty())){
                technologicalMapGroupList = daoService.findTechnologicalMapGroupByConfigurationProvider(deletedStatusSelected);
            } else {
                technologicalMapGroupList = daoService.findTechnologicalMapGroupByConfigurationProvider(orgOwners, false);
            }
        }
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
        return "commodity_accounting/configuration_provider/technologicalMap/group/list";
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
