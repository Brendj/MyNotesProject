/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group.TechnologicalMapGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group.TechnologicalMapGroupSelect;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.technologicalMapProduct.ProductListItem;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.technologicalMapProduct.ProductListItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.technologicalMapProduct.ProductListSelect;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
public class TechnologicalMapCreatePage extends BasicWorkspacePage implements ProductListSelect,
        TechnologicalMapGroupSelect {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapCreatePage.class);

    private TechnologicalMap technologicalMap;
    private TechnologicalMapProduct currTechnologicalMapProduct;
    private List<TechnologicalMapProduct> technologicalMapProducts;
    private TechnologicalMapGroup currentTechnologicalMapGroup;
    @Autowired
    private TechnologicalMapGroupItemsPanel technologicalMapGroupItemsPanel;
    @Autowired
    private DAOService daoService;
    @Autowired
    private ProductListItemsPanel productItemsPanel;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    @Override
    public void onShow() throws Exception {
        reload();
    }

    private void reload() {
        technologicalMap=new TechnologicalMap();
        technologicalMapProducts = new LinkedList<TechnologicalMapProduct>();
    }


    @Override
    public void select(List<ProductListItem> productItemList) {
        if (!(productItemList == null || productItemList.isEmpty())) {
            for (ProductListItem productItem: productItemList){
                TechnologicalMapProduct technologicalMapProduct = new TechnologicalMapProduct();
                technologicalMapProduct.setProduct(productItem.getProduct());
                technologicalMapProduct.setDeletedState(false);
                technologicalMapProduct.setTechnologicalMap(technologicalMap);
                technologicalMapProducts.add(technologicalMapProduct);
            }
        }
    }

    public Object selectTechnologicalMapGroup() throws Exception{
        technologicalMapGroupItemsPanel.reload();
        if(currentTechnologicalMapGroup!=null){
            technologicalMapGroupItemsPanel.setSelectTechnologicalMapGroup(currentTechnologicalMapGroup);
        }
        technologicalMapGroupItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(TechnologicalMapGroup technologicalMapGroup) {
           currentTechnologicalMapGroup = technologicalMapGroup;
    }

    public Object onSave() {
        try{
            if(technologicalMap.getNameOfTechnologicalMap()==null || technologicalMap.getNameOfTechnologicalMap().equals("")){
                printError("Введите имя технологической карты.");
                return null;
            }
            if(technologicalMap.getNumberOfTechnologicalMap()==null){
                printError("Введите номер технологической карты.");
                return null;
            }
            if(currentTechnologicalMapGroup==null){
                printError("Выберите группу для технологической карты.");
                return null;
            }
            technologicalMap.setCreatedDate(new Date());
            technologicalMap.setDeletedState(true);
            technologicalMap.setGlobalVersion(daoService.updateVersionByDistributedObjects(TechnologicalMap.class.getSimpleName()));
            //technologicalMap.setGuid(UUID.randomUUID().toString());

            MainPage mainPage = MainPage.getSessionInstance();
            technologicalMap.setUserCreate(mainPage.getCurrentUser());

            technologicalMap.setTechnologicalMapGroup(currentTechnologicalMapGroup);
            Long currentIdOfConfigurationProvider = currentTechnologicalMapGroup.getIdOfConfigurationProvider();
            Long orgOwner = currentTechnologicalMapGroup.getOrgOwner();
            technologicalMap.setIdOfConfigurationProvider(currentIdOfConfigurationProvider);
            technologicalMap.setOrgOwner(orgOwner);
            daoService.persistEntity(technologicalMap);
            technologicalMap = entityManager.merge(technologicalMap);
            if(!(technologicalMapProducts==null || technologicalMapProducts.isEmpty())) {
                Long version = daoService.updateVersionByDistributedObjects(TechnologicalMapProduct.class.getSimpleName());
                for (TechnologicalMapProduct technologicalMapProduct: technologicalMapProducts){
                    technologicalMapProduct.setCreatedDate(new Date());
                    technologicalMapProduct.setTechnologicalMap(technologicalMap);
                    technologicalMapProduct.setIdOfConfigurationProvider(currentIdOfConfigurationProvider);
                    technologicalMapProduct.setOrgOwner(orgOwner);
                    technologicalMapProduct.setGlobalVersion(version);
                    daoService.persistEntity(technologicalMapProduct);
                }
            }
            printMessage("Новая технологическая карта создана успешно.");
            reload();
        } catch (Exception e){
            printError("Ошибка при создании новой технологической карты.");
            logger.error("Error by create Technological Map.", e);
        }
        return null;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/technologicalMap/create";
    }

    public Object deleteProduct(){
        technologicalMapProducts.remove(currTechnologicalMapProduct);
        return null;
    }

    public Object showProducts() throws Exception {
        productItemsPanel.reload(new LinkedList<TechnologicalMapProduct>());
        productItemsPanel.pushCompleteHandlerList(this);
        return null;
    }

    public TechnologicalMapGroup getCurrentTechnologicalMapGroup() {
        return currentTechnologicalMapGroup;
    }

    public void setCurrentTechnologicalMapGroup(TechnologicalMapGroup currentTechnologicalMapGroup) {
        this.currentTechnologicalMapGroup = currentTechnologicalMapGroup;
    }

    public TechnologicalMap getTechnologicalMap() {
        return technologicalMap;
    }

    public void setCurrTechnologicalMapProduct(TechnologicalMapProduct currTechnologicalMapProduct) {
        this.currTechnologicalMapProduct = currTechnologicalMapProduct;
    }

    public List<TechnologicalMapProduct> getTechnologicalMapProducts() {
        return technologicalMapProducts;
    }

    public void setTechnologicalMapProducts(List<TechnologicalMapProduct> technologicalMapProducts) {
        this.technologicalMapProducts = technologicalMapProducts;
    }

    public TechnologicalMapProduct getCurrTechnologicalMapProduct() {
        return currTechnologicalMapProduct;
    }

}