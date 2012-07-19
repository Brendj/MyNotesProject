/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderMenu;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.group.TechnologicalMapGroupMenu;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct.ProductItem;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct.ProductItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct.ProductSelect;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class TechnologicalMapEditPage extends BasicWorkspacePage implements ProductSelect {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapEditPage.class);

    private TechnologicalMap currTechnologicalMap;
    private TechnologicalMapProduct currTechnologicalMapProduct;
    private List<ProductItem> productItems = new LinkedList<ProductItem>();
    private List<Product> pr = new LinkedList<Product>();
    private List<TechnologicalMapProduct> technologicalMapProducts = new LinkedList<TechnologicalMapProduct>();
    private TechnologicalMapGroupMenu technologicalMapGroupMenu = new TechnologicalMapGroupMenu();
    private List<TechnologicalMapGroup> technologicalMapGroupList;
    private Long currentIdOfTechnologicalMapGroup;
    private Long currentIdOfConfigurationProvider;
    private ConfigurationProviderMenu configurationProviderMenu = new ConfigurationProviderMenu();
    private List<ConfigurationProvider> configurationProviderList;
    @PersistenceContext
    EntityManager em;

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
        technologicalMapGroupList = DAOService.getInstance()
                .getDistributedObjects(TechnologicalMapGroup.class);
        configurationProviderList = DAOService.getInstance().getDistributedObjects(
                ConfigurationProvider.class);
        if(getRendered()){
            configurationProviderMenu.readAllItems(configurationProviderList);
            technologicalMapGroupMenu.readAllItems(technologicalMapGroupList);
        } else {
            printError("Отсутсвуют группы технологических карт.");
        }
    }

    public boolean getRendered(){
        return !(configurationProviderList==null || configurationProviderList.isEmpty() || technologicalMapGroupList==null || technologicalMapGroupList.isEmpty());
    }

    @Override
    public void select(List<ProductItem> productItemList) {
        if (!(productItemList == null || productItemList.isEmpty())) {
            for (ProductItem productItem: productItemList){
                TechnologicalMapProduct technologicalMapProduct = new TechnologicalMapProduct();
                technologicalMapProduct.setProduct(productItem.getProduct());
                technologicalMapProduct.setDeletedState(false);
                technologicalMapProduct.setTechnologicalMap(currTechnologicalMap);
                technologicalMapProducts.add(technologicalMapProduct);
            }
        }
    }

    public void reload() throws Exception {
        currTechnologicalMap = em.merge(currTechnologicalMap);
        TypedQuery<TechnologicalMapProduct> query = em.createQuery("from TechnologicalMapProduct where technologicalMap=:technologicalMap",TechnologicalMapProduct.class);
        query.setParameter("technologicalMap",currTechnologicalMap);
        technologicalMapProducts = query.getResultList();
         for (TechnologicalMapProduct technologicalMapProduct: technologicalMapProducts){
            pr.add(technologicalMapProduct.getProduct());
         }
    }

    @Transactional
    public void remove(){
        if(!currTechnologicalMap.getDeletedState()) {
            printMessage("Технологическая карта не может быть удалена.");
            return;
        }
        try{
            for (TechnologicalMapProduct technologicalMapProduct: currTechnologicalMap.getTechnologicalMapProduct()) {
                TechnologicalMapProduct tmp = em.getReference(TechnologicalMapProduct.class,
                        technologicalMapProduct.getGlobalId());
                em.remove(tmp);
            }
            TechnologicalMap tm=em.getReference(TechnologicalMap.class, currTechnologicalMap.getGlobalId());
            em.remove(tm);
            printMessage("Технологическая карта удалена успешно.");
        }  catch (Exception e){
            printError("Ошибка при удалении технологической карты.");
            logger.error("Error by delete Technological Map.", e);
        }
    }

    public void save(){
        doSave();
    }

    @Transactional
    public void doSave() {
        try{
            TechnologicalMap tm = em.find(TechnologicalMap.class,currTechnologicalMap.getGlobalId());

            tm.fill(currTechnologicalMap);

            tm.setLastUpdate(new Date());
            tm.setDeletedState(currTechnologicalMap.getDeletedState());

            MainPage mainPage = MainPage.getSessionInstance();
            if(tm.getDeletedState().equals(Boolean.TRUE) && currTechnologicalMap.getDeletedState().equals(Boolean.FALSE)){
                tm.setUserDelete(mainPage.getCurrentUser());
            } else {
                tm.setUserEdit(mainPage.getCurrentUser());
            }

            List<TechnologicalMapProduct> technologicalMapProductList = DAOService.getInstance().getTechnologicalMapProducts(tm);
            for (TechnologicalMapProduct technologicalMapProduct: technologicalMapProductList){
                DAOService.getInstance().deleteEntity(technologicalMapProduct);
            }

            //tm.setIdOfConfigurationProvider(currentIdOfConfigurationProvider);

            tm.setTechnologicalMapGroup(DAOService.getInstance().findRefDistributedObject(TechnologicalMapGroup.class, currentIdOfTechnologicalMapGroup));
            DAOService.getInstance().setConfigurationProviderInDO(TechnologicalMapGroup.class,currentIdOfTechnologicalMapGroup, currentIdOfConfigurationProvider);

            currTechnologicalMap = (TechnologicalMap) DAOService.getInstance().mergeDistributedObject(tm,tm.getGlobalVersion()+1);
            DAOService.getInstance().setConfigurationProviderInDO(TechnologicalMap.class,currTechnologicalMap.getGlobalId(), currentIdOfConfigurationProvider);

            for (TechnologicalMapProduct technologicalMapProduct: technologicalMapProducts){
                if(technologicalMapProduct.getGlobalId()==null){
                    technologicalMapProduct.setCreatedDate(new Date());
                    technologicalMapProduct.setGuid(UUID.randomUUID().toString());
                    technologicalMapProduct.setGlobalVersion(0L);
                    technologicalMapProduct.setTechnologicalMap(currTechnologicalMap);
                    technologicalMapProduct.setIdOfConfigurationProvider(currentIdOfConfigurationProvider);
                } else {
                    technologicalMapProduct.setGlobalId(null);
                    technologicalMapProduct.setLastUpdate(new Date());
                    technologicalMapProduct.setIdOfConfigurationProvider(currentIdOfConfigurationProvider);
                    technologicalMapProduct.setGlobalVersion(technologicalMapProduct.getGlobalVersion()+1);
                }
                DAOService.getInstance().persistEntity(technologicalMapProduct);
            }

            printMessage("Технологическая карта сохранена успешно.");
        } catch (Exception e){
            printError("Ошибка при сохранении новой технологической карты.");
            logger.error("Error by edit Technological Map.", e);
        }
    }

    public Object deleteProduct(){
        technologicalMapProducts.remove(currTechnologicalMapProduct);
        return null;
    }

    public Object showProducts() throws Exception {
        RuntimeContext.getAppContext().getBean(ProductItemsPanel.class).reload(technologicalMapProducts);
        RuntimeContext.getAppContext().getBean(ProductItemsPanel.class).pushCompleteHandlerList(RuntimeContext.getAppContext().getBean(getClass()));
        return null;
    }

    public Object addProducts() {
        for (ProductItem productItem: productItems){
            if(productItem.getChecked()){
                TechnologicalMapProduct technologicalMapProduct = new TechnologicalMapProduct();
                technologicalMapProduct.setProduct(productItem.getProduct());
                technologicalMapProduct.setDeletedState(false);
                technologicalMapProduct.setTechnologicalMap(currTechnologicalMap);
                currTechnologicalMap.addTechnologicalMapProduct(technologicalMapProduct);
            }
        }
        technologicalMapProducts = currTechnologicalMap.getTechnologicalMapProduct();
        return null;
    }

    public String getPageFilename() {
        return "option/configuration_provider/technologicalMap/edit";
    }

    public Long getCurrentIdOfConfigurationProvider() {
        return currentIdOfConfigurationProvider;
    }

    public void setCurrentIdOfConfigurationProvider(Long currentIdOfConfigurationProvider) {
        this.currentIdOfConfigurationProvider = currentIdOfConfigurationProvider;
    }

    public ConfigurationProviderMenu getConfigurationProviderMenu() {
        return configurationProviderMenu;
    }

    public void setConfigurationProviderMenu(ConfigurationProviderMenu configurationProviderMenu) {
        this.configurationProviderMenu = configurationProviderMenu;
    }

    public TechnologicalMapGroupMenu getTechnologicalMapGroupMenu() {
        return technologicalMapGroupMenu;
    }

    public void setTechnologicalMapGroupMenu(TechnologicalMapGroupMenu technologicalMapGroupMenu) {
        this.technologicalMapGroupMenu = technologicalMapGroupMenu;
    }

    public TechnologicalMap getCurrTechnologicalMap() {
        return currTechnologicalMap;
    }

    public void setCurrTechnologicalMap(TechnologicalMap currTechnologicalMap) {
        this.currTechnologicalMap = currTechnologicalMap;
    }

    public Long getCurrentIdOfTechnologicalMapGroup() {
        return currentIdOfTechnologicalMapGroup;
    }

    public void setCurrentIdOfTechnologicalMapGroup(Long currentIdOfTechnologicalMapGroup) {
        this.currentIdOfTechnologicalMapGroup = currentIdOfTechnologicalMapGroup;
    }

    public TechnologicalMapProduct getCurrTechnologicalMapProduct() {
        return currTechnologicalMapProduct;
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

    public List<ProductItem> getProductItems() {
        return productItems;
    }

}
