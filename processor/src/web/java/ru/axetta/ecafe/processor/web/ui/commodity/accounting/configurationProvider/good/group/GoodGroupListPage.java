/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
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
public class GoodGroupListPage extends BasicWorkspacePage implements ConfigurationProviderSelect {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodGroupListPage.class);
    private List<GoodGroup> goodGroupList;
    private Boolean deletedStatusSelected = false;
    private ConfigurationProvider selectedConfigurationProvider;
    @Autowired
    private DAOService daoService;
    @Autowired
    private ContextDAOServices contextDAOServices;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;

    @Override
    public void onShow() {}

    @Override
    public void select(ConfigurationProvider configurationProvider) {
        selectedConfigurationProvider = configurationProvider;
    }

    public Object selectConfigurationProvider() throws Exception{
        configurationProviderItemsPanel.reload();
        if(selectedConfigurationProvider !=null){
            configurationProviderItemsPanel.setSelectConfigurationProvider(selectedConfigurationProvider);
        }
        configurationProviderItemsPanel.pushCompleteHandler(this);
        return null;
    }

    public Object onSearch(){
        try {
            reload();
        } catch (Exception e) {
            printError(String.format("Ошибка при загрузке данных: %s", e.getMessage()));
            LOGGER.error("GoodGroup onSearch error: ", e);
        }
        return null;
    }

    public Object onClear() throws Exception{
        return null;
    }

    public void reload() throws Exception{
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<Long> orgOwners = contextDAOServices.findOrgOwnersByContragentSet(user.getIdOfUser());
        goodGroupList = DAOReadonlyService.getInstance()
                .findGoodGroup(selectedConfigurationProvider, null, orgOwners, deletedStatusSelected);
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/good/group/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", (goodGroupList==null?0:goodGroupList.size()));
    }

    public List<GoodGroup> getGoodGroupList() {
        return goodGroupList;
    }

    public void setGoodGroupList(List<GoodGroup> goodGroupList) {
        this.goodGroupList = goodGroupList;
    }

    public Boolean getEmptyGoodGroupList(){
        return  this.goodGroupList == null || this.goodGroupList.isEmpty();
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
}
