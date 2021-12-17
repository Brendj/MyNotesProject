/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ConfirmDeletePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group.TechnologicalMapGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group.TechnologicalMapGroupSelect;

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
public class TechnologicalMapListPage extends BasicWorkspacePage implements ConfirmDeletePage.Listener ,
        TechnologicalMapGroupSelect, ConfigurationProviderSelect {

    private static final Logger logger = LoggerFactory.getLogger(TechnologicalMapListPage.class);

    private List<TechnologicalMap> technologicalMapList;
    private ConfigurationProvider selectedConfigurationProvider;

    private TechnologicalMapGroup selectedTechnologicalMapGroup;
    private Boolean deletedStatusSelected = Boolean.FALSE;

    @Autowired
    private TechnologicalMapGroupItemsPanel technologicalMapGroupItemsPanel;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;
    @Autowired
    private DAOService daoService;
    @Autowired
    private ContextDAOServices contextDAOServices;

    @Override
    public void onConfirmDelete(ConfirmDeletePage confirmDeletePage) {
        daoService.deleteEntity(confirmDeletePage.getEntity());
        try {
            reload();
        } catch (Exception e) {
            printError(String.format("Ошибка при загрузке данных: %s", e.getMessage()));
            logger.error("TechnologicalMap onSearch error: ", e);
        }
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)",(getEmptyTechnologicalMap()?0:technologicalMapList.size()));
    }

    public Boolean getEmptyTechnologicalMap(){
        return technologicalMapList == null || technologicalMapList.isEmpty();
    }

    @Override
    public void onShow() throws Exception {}

    public Object onSearch(){
        try {
            reload();
        } catch (Exception e) {
            printError(String.format("Ошибка при загрузке данных: %s", e.getMessage()));
            logger.error("TechnologicalMap onSearch error: ", e);
        }
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

    public void reload() throws Exception {
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<Long> orgOwners = contextDAOServices.findOrgOwnersByContragentSet(user.getIdOfUser());
        /* проверка на отсутсвие выбора грпуппы */
        if(selectedTechnologicalMapGroup==null){
            /* проверка на отсутвие конфигурации провайдера */
            if(selectedConfigurationProvider!=null){
                if(!user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification()) && (orgOwners==null || orgOwners.isEmpty())){
                    technologicalMapList = DAOReadonlyService.getInstance()
                            .findTechnologicalMapByConfigurationProvider(
                            selectedConfigurationProvider.getIdOfConfigurationProvider(), deletedStatusSelected);
                } else {
                    technologicalMapList = DAOReadonlyService.getInstance()
                            .findTechnologicalMapByConfigurationProvider(
                            selectedConfigurationProvider.getIdOfConfigurationProvider(), orgOwners, deletedStatusSelected);
                }
            } else {
                if(!user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification()) && (orgOwners==null || orgOwners.isEmpty())){
                    technologicalMapList = DAOReadonlyService.getInstance()
                            .findTechnologicalMapByConfigurationProvider(deletedStatusSelected);
                } else {
                    technologicalMapList = DAOReadonlyService.getInstance()
                            .findTechnologicalMapByConfigurationProvider(orgOwners, deletedStatusSelected);
                }
            }
        } else {
            if(selectedConfigurationProvider!=null){
                if(orgOwners==null || orgOwners.isEmpty()){
                    technologicalMapList = DAOReadonlyService.getInstance()
                            .findTechnologicalMapByConfigurationProvider(selectedTechnologicalMapGroup,
                            selectedConfigurationProvider.getIdOfConfigurationProvider(), deletedStatusSelected);
                } else {
                    technologicalMapList = DAOReadonlyService.getInstance()
                            .findTechnologicalMapByConfigurationProvider(selectedTechnologicalMapGroup,
                            selectedConfigurationProvider.getIdOfConfigurationProvider(), orgOwners, deletedStatusSelected);
                }
            } else {
                if(!user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification()) && (orgOwners==null || orgOwners.isEmpty())){
                    technologicalMapList = DAOReadonlyService.getInstance()
                            .findTechnologicalMapByConfigurationProvider(selectedTechnologicalMapGroup,deletedStatusSelected);
                } else {
                    technologicalMapList = DAOReadonlyService.getInstance()
                            .findTechnologicalMapByConfigurationProvider(selectedTechnologicalMapGroup, orgOwners, deletedStatusSelected);
                }
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
