/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
//import ru.axetta.ecafe.processor.web.ui.report.productGuide.Item;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationProviderCreatePage.class);
    private ConfigurationProvider currentConfigurationProvider;
    private String filter;
    private List<Long> idOfOrgList = new ArrayList<Long>();

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
        } catch (Exception e) {
            printError("Ошибка при созданиии производственной конфигурации.");
            logger.error("Error create configuration provider",e);
        } finally {
            return null;
        }
    }

    @Transactional
    private void onSave() throws Exception{
        currentConfigurationProvider.setCreatedDate(new Date());

        //Есть ли необходимость?
        //currentConfigurationProvider.setDeletedState(false);
        //currentConfigurationProvider.setGuid(UUID.randomUUID().toString());
        //product.setGlobalVersion(0L);
        //product.setIdOfConfigurationProvider(currentIdOfConfigurationProvider);

        MainPage mainPage = MainPage.getSessionInstance();
        currentConfigurationProvider.setUserCreate(mainPage.getCurrentUser());

        DAOService.getInstance().persistEntity(currentConfigurationProvider);

        if(!this.idOfOrgList.isEmpty()){
            for (Long idOfOrg: idOfOrgList){
                DAOService.getInstance().setConfigurationProviderInOrg(idOfOrg,currentConfigurationProvider);
            }
        }

        idOfOrgList.clear();
        filter = "";
        currentConfigurationProvider = new ConfigurationProvider();
        printMessage("Производственная конфигурация сохранена успешно.");
    }

    public String getPageFilename() {
        return "option/configuration_provider/create";
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

}

/*private Item item = new Item();

    public Item getItem() {
        return item;
    }

    public String getPageTitle() {
        return super.getPageTitle();
    }

    public void create(Session persistenceSession) {
        ConfigurationProvider cp = new ConfigurationProvider();
        cp.setName(item.getName());
        persistenceSession.save(cp);
    }
    */