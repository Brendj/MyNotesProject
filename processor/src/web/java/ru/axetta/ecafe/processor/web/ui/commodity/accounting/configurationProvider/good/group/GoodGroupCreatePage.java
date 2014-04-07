/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
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

import java.util.Date;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class GoodGroupCreatePage extends BasicWorkspacePage implements ConfigurationProviderSelect, OrgSelectPage.CompleteHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodGroupCreatePage.class);
    private GoodGroup goodGroup;
    private Org org;
    private ConfigurationProvider currentConfigurationProvider;
    @Autowired
    private DAOService daoService;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;

    @Override
    public void onShow() throws Exception {
        goodGroup = new GoodGroup();
    }

    public Object onSave(){
        try {
            if(org==null){
                printError("Поле 'Организация поставщик' обязательное.");
                return null;
            }
            if(goodGroup.getNameOfGoodsGroup() == null || goodGroup.getNameOfGoodsGroup().equals("")){
                printError("Поле 'Наименование группы' обязательное.");
                return null;
            }
            if(currentConfigurationProvider==null){
                printError("Поле 'Производственная конфигурация' обязательное.");
                return null;
            }
            goodGroup.setCreatedDate(new Date());
            goodGroup.setDeletedState(false);
            goodGroup.setOrgOwner(org.getIdOfOrg());
            goodGroup.setIdOfConfigurationProvider(currentConfigurationProvider.getIdOfConfigurationProvider());
            goodGroup.setGlobalVersion(daoService.updateVersionByDistributedObjects(GoodGroup.class.getSimpleName()));
            daoService.persistEntity(goodGroup);
            printMessage("Группа сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при созданиии группы.");
            LOGGER.error("Error create good group", e);
        }
        return null;
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            org = daoService.findOrById(idOfOrg);
        }
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

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/good/group/create";
    }

    public String getShortName() {
        return (org == null?"":this.org.getShortName());
    }

    public GoodGroup getGoodGroup() {
        return goodGroup;
    }

    public void setGoodGroup(GoodGroup goodGroup) {
        this.goodGroup = goodGroup;
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }

    public void setCurrentConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        this.currentConfigurationProvider = currentConfigurationProvider;
    }
}
