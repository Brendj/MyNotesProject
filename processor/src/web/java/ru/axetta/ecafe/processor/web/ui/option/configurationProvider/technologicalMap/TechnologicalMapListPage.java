/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ConfirmDeletePage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderMenu;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.group.TechnologicalMapGroupMenu;

import org.slf4j.LoggerFactory;
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
public class TechnologicalMapListPage extends BasicWorkspacePage implements ConfirmDeletePage.Listener  {

    @Override
    public void onConfirmDelete(ConfirmDeletePage confirmDeletePage) {
        DAOService.getInstance().deleteEntity(confirmDeletePage.getEntity());
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    private static final Long ALL = -2L;
    private static final Long NONE = -1L;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapListPage.class);
    private TechnologicalMap technologicalMap = new TechnologicalMap();
    private List<TechnologicalMap> technologicalMapList;

    private Long currentIdOfConfigurationProvider=NONE;
    private Long currentIdOftechnologicalMapGroup=NONE;
    private ConfigurationProviderMenu configurationProviderMenu = new ConfigurationProviderMenu();
    private TechnologicalMapGroupMenu technologicalMapGroupMenu = new TechnologicalMapGroupMenu();
    private List<ConfigurationProvider> configurationProviderList;
    private List<TechnologicalMapGroup> technologicalMapGroupList;
    private Boolean deletedStatusSelected = Boolean.FALSE;



    public List<TechnologicalMap> getTechnologicalMapList() {
        return technologicalMapList;
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", technologicalMapList.size());
    }

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    public Object onChange() throws Exception{
        reload();
        return null;
    }

    public void reload() {
        //technologicalMapList = DAOService.getInstance().getDistributedObjects(TechnologicalMap.class);
        configurationProviderList = DAOService.getInstance().getDistributedObjects(
                ConfigurationProvider.class);
        technologicalMapGroupList = DAOService.getInstance().getDistributedObjects(TechnologicalMapGroup.class);
        if(getRendered()){
            configurationProviderMenu.readAllItems(configurationProviderList);
            technologicalMapGroupMenu.readAllItems(technologicalMapGroupList);
        }
        String where="";
        if(!currentIdOfConfigurationProvider.equals(ALL)){
            where = where+ " idOfConfigurationProvider="+currentIdOfConfigurationProvider;
        }
        TechnologicalMapGroup tmg = null;
        if(!currentIdOftechnologicalMapGroup.equals(ALL)){
            tmg = DAOService.getInstance().findRefDistributedObject(TechnologicalMapGroup.class,currentIdOftechnologicalMapGroup);
            if(tmg!=null){
                if(!where.equals("")) where = where + " and ";
                where = where+ " technologicalMapGroup=:technologicalMapGroup";
            }
        }
        if(!deletedStatusSelected){
            if(!where.equals("")) where = where + " and ";
            where = where+" deletedState=FALSE";
        }
        if(!where.equals("")) where  =" where "+ where;
        TypedQuery<TechnologicalMap> query = entityManager.createQuery("FROM TechnologicalMap "+where+" ORDER BY globalId",TechnologicalMap.class);
        if(tmg!=null) query.setParameter("technologicalMapGroup", tmg);
        technologicalMapList = query.getResultList();
    }

    public boolean getRendered(){
        return !(configurationProviderList==null || configurationProviderList.isEmpty() || technologicalMapGroupList==null || technologicalMapGroupList.isEmpty());
    }

    public String getPageFilename() {
        return "option/technologicalMap/list";
    }

    public TechnologicalMap getTechnologicalMap() {
        return technologicalMap;
    }

    public ConfigurationProviderMenu getConfigurationProviderMenu() {
        return configurationProviderMenu;
    }

    public void setConfigurationProviderMenu(ConfigurationProviderMenu configurationProviderMenu) {
        this.configurationProviderMenu = configurationProviderMenu;
    }

    public Long getCurrentIdOfConfigurationProvider() {
        return currentIdOfConfigurationProvider;
    }

    public void setCurrentIdOfConfigurationProvider(Long currentIdOfConfigurationProvider) {
        this.currentIdOfConfigurationProvider = currentIdOfConfigurationProvider;
    }

    public Long getCurrentIdOftechnologicalMapGroup() {
        return currentIdOftechnologicalMapGroup;
    }

    public void setCurrentIdOftechnologicalMapGroup(Long currentIdOftechnologicalMapGroup) {
        this.currentIdOftechnologicalMapGroup = currentIdOftechnologicalMapGroup;
    }

    public TechnologicalMapGroupMenu getTechnologicalMapGroupMenu() {
        return technologicalMapGroupMenu;
    }

    public void setTechnologicalMapGroupMenu(TechnologicalMapGroupMenu technologicalMapGroupMenu) {
        this.technologicalMapGroupMenu = technologicalMapGroupMenu;
    }

    public Boolean getDeletedStatusSelected() {
        return deletedStatusSelected;
    }

    public void setDeletedStatusSelected(Boolean deletedStatusSelected) {
        this.deletedStatusSelected = deletedStatusSelected;
    }
}
