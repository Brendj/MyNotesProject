/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
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
public class TechnologicalMapGroupEditPage extends BasicWorkspacePage implements ConfigurationProviderSelect, OrgSelectPage.CompleteHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapGroupCreatePage.class);
    private TechnologicalMapGroup currentTechnologicalMapGroup;
    private ConfigurationProvider currentConfigurationProvider;
    private Org org;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    @Autowired
    private DAOService daoService;
    @Autowired
    private SelectedTechnologicalMapGroupGroupPage selectedTechnologicalMapGroupGroupPage;

    @Override
    public void onShow() throws Exception {
        selectedTechnologicalMapGroupGroupPage.onShow();
        currentTechnologicalMapGroup = selectedTechnologicalMapGroupGroupPage.getCurrentTechnologicalMapGroup();
        currentTechnologicalMapGroup = entityManager.merge(currentTechnologicalMapGroup);
        org = DAOReadonlyService.getInstance().findOrById(currentTechnologicalMapGroup.getOrgOwner());
        currentConfigurationProvider = DAOReadonlyService.getInstance().getConfigurationProvider(currentTechnologicalMapGroup.getIdOfConfigurationProvider());
    }

    public Object onSave(){
        try {
            if(currentTechnologicalMapGroup.getNameOfGroup() == null || currentTechnologicalMapGroup.getNameOfGroup().equals("")){
                printError("Поле 'Наименование группы' обязательное.");
                return null;
            }
            if(org==null){
                printError("Поле 'Организация поставщик' обязательное.");
                return null;
            }
            if(currentConfigurationProvider==null){
                printError("Поле 'Производственная конфигурация' обязательное.");
                return null;
            }
            currentTechnologicalMapGroup.setOrgOwner(org.getIdOfOrg());
            currentTechnologicalMapGroup.setIdOfConfigurationProvider(currentConfigurationProvider.getIdOfConfigurationProvider());
            currentTechnologicalMapGroup.setGlobalVersion(daoService.updateVersionByDistributedObjects(TechnologicalMapGroup.class.getSimpleName()));
            currentTechnologicalMapGroup = (TechnologicalMapGroup) daoService.mergeDistributedObject(currentTechnologicalMapGroup,currentTechnologicalMapGroup.getGlobalVersion()+1);
            printMessage("Группа сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении группы.");
            logger.error("Error saved Technological Map Group",e);
        }
        return null;
    }

    @Transactional
    public void remove(){
        if(!currentTechnologicalMapGroup.getDeletedState()) {
            printMessage("Группа не может быть удалена.");
            return;
        }
        TypedQuery<TechnologicalMap> query = entityManager.createQuery("from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup",TechnologicalMap.class);
        query.setParameter("technologicalMapGroup",currentTechnologicalMapGroup);
        List<TechnologicalMap> technologicalMapList = query.getResultList();
        if(!(technologicalMapList==null || technologicalMapList.isEmpty())){
            printError("Группа не может быть удален.");
            return;
        }
        try{
            TechnologicalMapGroup tmg = entityManager.getReference(TechnologicalMapGroup.class, currentTechnologicalMapGroup.getGlobalId());
            entityManager.remove(tmg);
            printMessage("Группа удалена успешно.");
        }  catch (Exception e){
            printError("Ошибка при удалении группа.");
            logger.error("Error by delete Technological Map Group.", e);
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

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            org = DAOReadonlyService.getInstance().findOrById(idOfOrg);
        }
    }

    public String getShortName() {
        return (org == null?"":this.org.getShortName());
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/technologicalMap/group/edit";
    }

    public TechnologicalMapGroup getCurrentTechnologicalMapGroup() {
        return currentTechnologicalMapGroup;
    }

    public void setCurrentTechnologicalMapGroup(TechnologicalMapGroup currentTechnologicalMapGroup) {
        this.currentTechnologicalMapGroup = currentTechnologicalMapGroup;
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }

    public void setCurrentConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        this.currentConfigurationProvider = currentConfigurationProvider;
    }
}
