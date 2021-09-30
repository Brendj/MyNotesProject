/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.group;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
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
public class ProductGroupEditPage extends BasicWorkspacePage implements ConfigurationProviderSelect, OrgSelectPage.CompleteHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProductGroupEditPage.class);
    private ProductGroup currentProductGroup;
    private ConfigurationProvider currentConfigurationProvider;
    private Org org;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    @Autowired
    private DAOService daoService;
    @Autowired
    private SelectedProductGroupGroupPage selectedProductGroupGroupPage;

    @Override
    public void onShow() throws Exception {
        selectedProductGroupGroupPage.onShow();
        currentProductGroup = selectedProductGroupGroupPage.getCurrentProductGroup();
        currentProductGroup = entityManager.merge(currentProductGroup);
        org = DAOReadonlyService.getInstance().findOrById(currentProductGroup.getOrgOwner());
        currentConfigurationProvider = DAOReadonlyService.getInstance()
                .getConfigurationProvider(currentProductGroup.getIdOfConfigurationProvider());
    }

    public Object onSave(){
        try {
            if(org==null){
                printWarn("Поле 'Организация поставщик' обязательное.");
                return null;
            }
            if(currentConfigurationProvider==null){
                printWarn("Поле 'Производственная конфигурация' обязательное.");
                return null;
            }
            if(currentProductGroup.getNameOfGroup() == null || currentProductGroup.getNameOfGroup().equals("")){
                printWarn("Поле 'Наименование группы' обязательное.");
                return null;
            }
            currentProductGroup.setOrgOwner(org.getIdOfOrg());
            currentProductGroup.setIdOfConfigurationProvider(currentConfigurationProvider.getIdOfConfigurationProvider());
            currentProductGroup.setGlobalVersion(daoService.updateVersionByDistributedObjects(ProductGroup.class.getSimpleName()));
            currentProductGroup = (ProductGroup) daoService.mergeDistributedObject(currentProductGroup,currentProductGroup.getGlobalVersion()+1);
            selectedProductGroupGroupPage.setCurrentProductGroup(currentProductGroup);
            printMessage("Группа для продуктов сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении группы для продуктов.");
            logger.error("Error saved Product Group",e);
        }
        return null;
    }

    @Transactional
    public void remove(){
        if(!currentProductGroup.getDeletedState()) {
            printError("Группа не может быть удалена.");
            return;
        }
        TypedQuery<Product> query = entityManager.createQuery("from Product where productGroup=:productGroup",Product.class);
        query.setParameter("productGroup",currentProductGroup);
        List<Product> productList = query.getResultList();
        if(!(productList==null || productList.isEmpty())){
            printError("Группа не может быть удален.");
            return;
        }
        try{
            ProductGroup pg = entityManager.getReference(ProductGroup.class, currentProductGroup.getGlobalId());
            entityManager.remove(pg);
            printMessage("Группа удалена успешно.");
        }  catch (Exception e){
            printError("Ошибка при удалении группа.");
            logger.error("Error by delete Product Group.", e);
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
        return "commodity_accounting/configuration_provider/product/group/edit";
    }

    public ProductGroup getCurrentProductGroup() {
        return currentProductGroup;
    }

    public void setCurrentProductGroup(ProductGroup currentProductGroup) {
        this.currentProductGroup = currentProductGroup;
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }

    public void setCurrentConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        this.currentConfigurationProvider = currentConfigurationProvider;
    }
}
