/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
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
public class GoodGroupEditPage extends BasicWorkspacePage implements ConfigurationProviderSelect, OrgSelectPage.CompleteHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodGroupEditPage.class);
    private GoodGroup currentGoodGroup;
    private ConfigurationProvider currentConfigurationProvider;
    private Org org;
    @Autowired
    private DAOService daoService;
    @Autowired
    private DAOReadonlyService daoReadonlyService;
    @Autowired
    private SelectedGoodGroupGroupPage selectedGoodGroupGroupPage;
    @Autowired
    private GoodGroupListPage goodGroupListPage;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;

    @Override
    public void onShow() throws Exception {
        selectedGoodGroupGroupPage.onShow();
        currentGoodGroup = selectedGoodGroupGroupPage.getCurrentGoodGroup();
        currentGoodGroup = daoService.saveEntity(currentGoodGroup);
        org = daoReadonlyService.findOrById(currentGoodGroup.getOrgOwner());
        if(currentGoodGroup.getIdOfConfigurationProvider()!=null){
            currentConfigurationProvider = daoReadonlyService.getConfigurationProvider(currentGoodGroup.getIdOfConfigurationProvider());
        }
    }

    public Object onSave(){
        try {
            if(org==null){
                printError("Поле 'Организация поставщик' обязательное.");
                return null;
            }
            if(currentConfigurationProvider==null){
                printWarn("Поле 'Производственная конфигурация' обязательное.");
                return null;
            }
            if(currentGoodGroup.getNameOfGoodsGroup() == null || currentGoodGroup.getNameOfGoodsGroup().equals("")){
                printError("Поле 'Наименование группы' обязательное.");
                return null;
            }
            currentGoodGroup.setOrgOwner(org.getIdOfOrg());
            currentGoodGroup.setGlobalVersion(
                    daoService.updateVersionByDistributedObjects(GoodGroup.class.getSimpleName()));
            currentGoodGroup = (GoodGroup) daoService.mergeDistributedObject(currentGoodGroup,currentGoodGroup.getGlobalVersion()+1);
            selectedGoodGroupGroupPage.setCurrentGoodGroup(currentGoodGroup);
            printMessage("Группа товаров сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении группы для продуктов.");
            LOGGER.error("Error saved Good Group", e);
        }
        return null;
    }

    public Object selectConfigurationProvider() throws Exception{
        configurationProviderItemsPanel.reload();
        if(currentConfigurationProvider!=null){
            configurationProviderItemsPanel.setSelectConfigurationProvider(currentConfigurationProvider);
        }
        configurationProviderItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(ConfigurationProvider configurationProvider) {
        currentConfigurationProvider = configurationProvider;
    }

    public Object remove(){
        removeGroup();
        return null;
    }

    protected void removeGroup(){
        if(!currentGoodGroup.getDeletedState()) {
            printError("Группа не может быть удалена.");
            return;
        }
        List<Good> goodList = daoReadonlyService.findGoodsByGoodGroup(currentGoodGroup, true);
        if(!(goodList==null || goodList.isEmpty())){
            printError("В группе имеются зарегистрированные товары.");
            return;
        }
        try{
            daoService.removeGoodGroup(currentGoodGroup);
            goodGroupListPage.reload();
            printMessage("Группа удалена успешно.");
        }  catch (Exception e){
            printError("Ошибка при удалении группа.");
            LOGGER.error("Error by delete Good Group.", e);
        }
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            org = daoReadonlyService.findOrById(idOfOrg);
        }
    }

    public String getShortName() {
        return (org == null?"":this.org.getShortName());
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/good/group/edit";
    }

    public GoodGroup getCurrentGoodGroup() {
        return currentGoodGroup;
    }

    public void setCurrentGoodGroup(GoodGroup currentGoodGroup) {
        this.currentGoodGroup = currentGoodGroup;
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }

    public void setCurrentConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        this.currentConfigurationProvider = currentConfigurationProvider;
    }
}
