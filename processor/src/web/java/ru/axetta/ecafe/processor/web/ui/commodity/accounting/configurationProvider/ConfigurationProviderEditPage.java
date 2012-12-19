/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 16.05.12
 * Time: 0:19
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ConfigurationProviderEditPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationProviderCreatePage.class);
    private ConfigurationProvider currentConfigurationProvider;
    private String filter;
    private List<Long> idOfOrgList = new ArrayList<Long>(0);
    @Autowired
    private SelectedConfigurationProviderGroupPage selectedConfigurationProviderGroupPage;
    @Autowired
    private DAOService daoService;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            idOfOrgList = new ArrayList<Long>();
            if (orgMap.isEmpty()) {
                filter = "Не выбрано";
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    stringBuilder = stringBuilder.append(orgMap.get(idOfOrg) + "; ");
                }
                filter = stringBuilder.substring(0, stringBuilder.length() - 2);
            }
        }
    }

    @Override
    public void onShow() throws Exception {
        selectedConfigurationProviderGroupPage.show();
        currentConfigurationProvider = selectedConfigurationProviderGroupPage.getSelectConfigurationProvider();
        StringBuilder stringBuilder = new StringBuilder();
        TypedQuery<Org> query = entityManager.createQuery("from Org where configurationProvider=:configurationProvider",Org.class);
        query.setParameter("configurationProvider",currentConfigurationProvider);
        for(Org org: query.getResultList()){
            idOfOrgList.add(org.getIdOfOrg());
            stringBuilder = stringBuilder.append(org.getShortName() + "; ");
        }
        if(stringBuilder.length()>2){
            filter = stringBuilder.substring(0, stringBuilder.length() - 2);
        }
    }

    public Object save() {
        try {
            onSave();
            selectedConfigurationProviderGroupPage.setSelectConfigurationProvider(currentConfigurationProvider);
            printMessage("Производственная конфигурация сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении производственной конфигурации.");
            logger.error("Error create configuration provider",e);
        }
        return null;
    }

    @Transactional
    private void onSave() throws Exception{
        ConfigurationProvider cp = entityManager.find(ConfigurationProvider.class, currentConfigurationProvider.getIdOfConfigurationProvider());
        /* fill object fields */
        cp.setName(currentConfigurationProvider.getName());
        cp.setLastUpdate(new Date());
        MainPage mainPage = MainPage.getSessionInstance();
        cp.setUserEdit(mainPage.getCurrentUser());

        currentConfigurationProvider = entityManager.merge(cp);

        if(!this.idOfOrgList.isEmpty()){
            for (Long idOfOrg: idOfOrgList){
                daoService.setConfigurationProviderInOrg(idOfOrg, currentConfigurationProvider);
            }
        }
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }

    public void setCurrentConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        this.currentConfigurationProvider = currentConfigurationProvider;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/edit";
    }


    public String getGetStringIdOfOrgList() {
        return idOfOrgList.toString().replaceAll("[^0-9,]","");
    }
}
