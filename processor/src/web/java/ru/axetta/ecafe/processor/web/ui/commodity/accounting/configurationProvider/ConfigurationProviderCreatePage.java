/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.ConfigurationProviderService;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 15.05.12
 * Time: 20:59
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ConfigurationProviderCreatePage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList{

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationProviderCreatePage.class);
    private ConfigurationProvider currentConfigurationProvider;
    private String filter;
    private List<Long> idOfOrgList = new ArrayList<Long>();
    @Autowired
    private ConfigurationProviderService service;

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            idOfOrgList = new ArrayList<Long>();
            if (orgMap.isEmpty()) {
                filter = "Не выбрано";
            } else {
                filter = "";
                for (Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    filter = filter.concat(orgMap.get(idOfOrg) + "; ");
                }
                filter = filter.substring(0, filter.length() - 1);
            }
        }
    }

    @Override
    public void onShow() throws Exception {
        currentConfigurationProvider = new ConfigurationProvider();
    }

    public Object save() {
        try {
            onSave();
        } catch (IllegalArgumentException e) {
            printError("Ошибка при сохранении производственной конфигурации: " + e.getMessage());
            logger.error("Error create configuration provider: " + e.getMessage());
        } catch (Exception e) {
            printError("Ошибка при сохранении производственной конфигурации: " + e.getMessage());
            logger.error("Error create configuration provider",e);
        }
        return null;
    }

    protected void onSave() throws Exception{
        currentConfigurationProvider.setCreatedDate(new Date());
        MainPage mainPage = MainPage.getSessionInstance();
        service.onSave(currentConfigurationProvider, mainPage.getCurrentUser(), idOfOrgList);

        //idOfOrgList.clear();
        //filter = "";
        printMessage("Производственная конфигурация сохранена успешно.");
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/create";
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

    public boolean getEligibleToWorkConfigurationProviderList() {
        Boolean result = false;
        try {
            User user = MainPage.getSessionInstance().getCurrentUser();
            if (user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification())) {
                result = true;
            } else {
                result = !user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification());
            }
        } catch (Exception e) {
            getLogger().error("getEligibleToWorkConfigurationProviderList exception", e);
        }
        return result;
    }
}
