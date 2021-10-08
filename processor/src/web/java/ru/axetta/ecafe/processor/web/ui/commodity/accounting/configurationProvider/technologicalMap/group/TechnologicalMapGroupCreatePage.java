/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class TechnologicalMapGroupCreatePage extends BasicWorkspacePage implements ConfigurationProviderSelect, OrgSelectPage.CompleteHandler  {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapGroupCreatePage.class);
    private TechnologicalMapGroup technologicalMapGroup;
    private ConfigurationProvider currentConfigurationProvider;
    private Org org;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;
    @Autowired
    private DAOService daoService;

    @Override
    public void onShow() throws Exception {
        technologicalMapGroup = new TechnologicalMapGroup();
    }

    public Object onSave(){
        try {
            if(org==null){
                printError("Поле 'Организация поставщик' обязательное.");
                return null;
            }
            if(technologicalMapGroup.getNameOfGroup() == null || technologicalMapGroup.getNameOfGroup().equals("")){
                printError("Поле 'Наименование группы' обязательное.");
                return null;
            }
            if(currentConfigurationProvider==null){
                printError("Поле 'Производственная конфигурация' обязательное.");
                return null;
            }
            technologicalMapGroup.setCreatedDate(new Date());
            technologicalMapGroup.setDeletedState(true);
            technologicalMapGroup.setGlobalVersion(daoService.updateVersionByDistributedObjects(TechnologicalMapGroup.class.getSimpleName()));
            technologicalMapGroup.setOrgOwner(org.getIdOfOrg());
            technologicalMapGroup.setIdOfConfigurationProvider(currentConfigurationProvider.getIdOfConfigurationProvider());
            daoService.persistEntity(technologicalMapGroup);
            printMessage("Группа сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при созданиии группы.");
            logger.error("Error create Technological Map Group",e);
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

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            org = DAOReadonlyService.getInstance().findOrById(idOfOrg);
        }
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/technologicalMap/group/create";
    }

    public String getShortName() {
        return (org == null?"":this.org.getShortName());
    }

    public TechnologicalMapGroup getTechnologicalMapGroup() {
        return technologicalMapGroup;
    }

    public void setTechnologicalMapGroup(TechnologicalMapGroup technologicalMapGroup) {
        this.technologicalMapGroup = technologicalMapGroup;
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }

    public void setCurrentConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        this.currentConfigurationProvider = currentConfigurationProvider;
    }
}
