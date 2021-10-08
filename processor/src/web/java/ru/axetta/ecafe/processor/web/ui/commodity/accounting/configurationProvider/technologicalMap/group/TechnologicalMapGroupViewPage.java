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
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.group.SelectedProductGroupGroupPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.TechnologicalMapListPage;

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
public class TechnologicalMapGroupViewPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(TechnologicalMapGroupViewPage.class);
    private TechnologicalMapGroup currentTechnologicalMapGroup;
    private Integer countTechnologicalMaps;
    private Org currentOrg;
    private ConfigurationProvider currentConfigurationProvider;
    @Autowired
    private SelectedProductGroupGroupPage selectedProductGroupGroupPage;
    @Autowired
    private SelectedTechnologicalMapGroupGroupPage selectedTechnologicalMapGroupGroupPage;
    @Autowired
    private TechnologicalMapListPage technologicalMapListPage;
    @Autowired
    private DAOService daoService;
    @Autowired
    private DAOReadonlyService daoReadonlyService;

    @Override
    public void onShow() throws Exception {
        selectedTechnologicalMapGroupGroupPage.onShow();
        currentTechnologicalMapGroup = selectedTechnologicalMapGroupGroupPage.getCurrentTechnologicalMapGroup();
        List<TechnologicalMap> technologicalMapList = daoReadonlyService.findTechnologicalMapByTechnologicalMapGroup(currentTechnologicalMapGroup);
        if(technologicalMapList==null || technologicalMapList.isEmpty()){
            countTechnologicalMaps = 0;
        }else {
            countTechnologicalMaps = technologicalMapList.size();
        }
        currentOrg = daoReadonlyService.findOrById(currentTechnologicalMapGroup.getOrgOwner());
        currentConfigurationProvider = daoReadonlyService.getConfigurationProvider(currentTechnologicalMapGroup.getIdOfConfigurationProvider());
    }

    public Object showTechnologicalMaps() throws Exception{
        technologicalMapListPage.setSelectedTechnologicalMapGroup(currentTechnologicalMapGroup);
         //Показать и удаленный
        technologicalMapListPage.setDeletedStatusSelected(true);
        technologicalMapListPage.reload();
        technologicalMapListPage.show();
        return null;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/technologicalMap/group/view";
    }

    public TechnologicalMapGroup getCurrentTechnologicalMapGroup() {
        return currentTechnologicalMapGroup;
    }

    public void setCurrentTechnologicalMapGroup(TechnologicalMapGroup currentTechnologicalMapGroup) {
        this.currentTechnologicalMapGroup = currentTechnologicalMapGroup;
    }

    public Integer getCountTechnologicalMaps() {
        return countTechnologicalMaps;
    }

    public Org getCurrentOrg() {
        return currentOrg;
    }

    public void setCurrentOrg(Org currentOrg) {
        this.currentOrg = currentOrg;
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }

    public void setCurrentConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        this.currentConfigurationProvider = currentConfigurationProvider;
    }
}
