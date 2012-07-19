/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

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
 * Date: 16.05.12
 * Time: 0:19
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ConfigurationProviderEditPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {

    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationProviderCreatePage.class);
    private ConfigurationProvider currentConfigurationProvider;
    private String filter;
    private List<Long> idOfOrgList = new ArrayList<Long>();
    private Long selectedIdOfConfigurationProvider;

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
        /* необходи нормально сделать без обращения к контексту */
        RuntimeContext.getAppContext().getBean(SelectedConfigurationProviderGroupPage.class).show();
        currentConfigurationProvider = entityManager.find(ConfigurationProvider.class,selectedIdOfConfigurationProvider);
    }

    public Object save() {
        try {
            onSave();
            printMessage("Производственная конфигурация сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении производственной конфигурации.");
            logger.error("Error create configuration provider",e);
        } finally {
            return null;
        }
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
                DAOService.getInstance().setConfigurationProviderInOrg(idOfOrg,currentConfigurationProvider);
            }
        }

        currentConfigurationProvider = new ConfigurationProvider();
    }

    public Long getSelectedIdOfConfigurationProvider() {
        return selectedIdOfConfigurationProvider;
    }

    public void setSelectedIdOfConfigurationProvider(Long selectedIdOfConfigurationProvider) {
        this.selectedIdOfConfigurationProvider = selectedIdOfConfigurationProvider;
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
        return "option/configuration_provider/edit";
    }


}

/*private Item item;

    public Item getItem() {
        return item;
    }

    public void fill(Session persistenceSession, Long id) {
        ConfigurationProvider cp = (ConfigurationProvider)DAOUtils.findConfigurationProvider(persistenceSession, id);
        item = new Item(cp.getIdOfConfigurationProvider(), cp.getName(), cp.getProducts());
    }

    public void update(Session persistenceSession) {
        ConfigurationProvider cp = new ConfigurationProvider();
        cp.setIdOfConfigurationProvider(item.getIdOfConfigurationProvider());
        cp.setName(item.getName());
        persistenceSession.update(cp);
    }

    public String getPageTitle() {
        return super.getPageTitle();
    }*/