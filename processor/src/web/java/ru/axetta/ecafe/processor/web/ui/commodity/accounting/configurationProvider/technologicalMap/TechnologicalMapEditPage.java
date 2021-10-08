/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group.TechnologicalMapGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group.TechnologicalMapGroupSelect;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.technologicalMapProduct.ProductListItem;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.technologicalMapProduct.ProductListItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.technologicalMapProduct.ProductListSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.LinkedList;
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
public class TechnologicalMapEditPage extends BasicWorkspacePage implements ProductListSelect, TechnologicalMapGroupSelect {

    private static final Logger logger = LoggerFactory.getLogger(TechnologicalMapEditPage.class);

    private TechnologicalMap currentTechnologicalMap;
    private TechnologicalMapProduct currentTechnologicalMapProduct;
    private List<Product> pr = new LinkedList<Product>();
    private List<TechnologicalMapProduct> technologicalMapProducts = new LinkedList<TechnologicalMapProduct>();
    private TechnologicalMapGroup currentTechnologicalMapGroup;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;
    @Autowired
    private DAOService daoService;
    @Autowired
    private ProductListItemsPanel productItemsPanel;
    @Autowired
    private TechnologicalMapGroupItemsPanel technologicalMapGroupItemsPanel;
    @Autowired
    private SelectedTechnologicalMapGroupPage selectedTechnologicalMapGroupPage;

    public Object selectTechnologicalMapGroup() throws Exception{
        technologicalMapGroupItemsPanel.reload();
        technologicalMapGroupItemsPanel.setSelectTechnologicalMapGroup(currentTechnologicalMapGroup);
        technologicalMapGroupItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(TechnologicalMapGroup technologicalMapGroup) {
        currentTechnologicalMapGroup = technologicalMapGroup;
    }

    @Override
    public void onShow() throws Exception {
        selectedTechnologicalMapGroupPage.onShow();
        currentTechnologicalMap = selectedTechnologicalMapGroupPage.getCurrentTechnologicalMap();
        if(currentTechnologicalMap.getTechnologicalMapGroup()!=null){
            currentTechnologicalMapGroup = currentTechnologicalMap.getTechnologicalMapGroup();
        }
        reload();
    }

    @Override
    public void select(List<ProductListItem> productItemList) {
        if (!(productItemList == null || productItemList.isEmpty())) {
            for (ProductListItem productItem: productItemList){
                TechnologicalMapProduct technologicalMapProduct = new TechnologicalMapProduct();
                technologicalMapProduct.setProduct(productItem.getProduct());
                technologicalMapProduct.setDeletedState(false);
                technologicalMapProduct.setTechnologicalMap(currentTechnologicalMap);
                technologicalMapProducts.add(technologicalMapProduct);
            }
        }
    }

    public void reload() throws Exception {
        currentTechnologicalMap = em.merge(currentTechnologicalMap);
        TypedQuery<TechnologicalMapProduct> query = em.createQuery("from TechnologicalMapProduct where technologicalMap=:technologicalMap",TechnologicalMapProduct.class);
        query.setParameter("technologicalMap", currentTechnologicalMap);
        technologicalMapProducts = query.getResultList();
         for (TechnologicalMapProduct technologicalMapProduct: technologicalMapProducts){
            pr.add(technologicalMapProduct.getProduct());
         }
    }

    //@Transactional
    public void remove(){
        if(!currentTechnologicalMap.getDeletedState()) {
            printError("Технологическая карта не может быть удалена.");
            return;
        }
        try{
            //for (TechnologicalMapProduct technologicalMapProduct: currentTechnologicalMap.getTechnologicalMapProduct()) {
            //    //TechnologicalMapProduct tmp = em.getReference(TechnologicalMapProduct.class,
            //    //        technologicalMapProduct.getGlobalId());
            //    //em.remove(tmp);
            //}
            //TechnologicalMap tm=em.getReference(TechnologicalMap.class, currentTechnologicalMap.getGlobalId());
            //em.remove(tm);

            daoService.removeTechnologicalMap(currentTechnologicalMap.getGlobalId());
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
            if(currentTechnologicalMap.getNameOfTechnologicalMap()==null || currentTechnologicalMap.getNameOfTechnologicalMap().equals("")){
                printError("Введите имя технологической карты.");
                return;
            }
            if(currentTechnologicalMap.getNumberOfTechnologicalMap()==null || currentTechnologicalMap.getNumberOfTechnologicalMap().equals("")){
                printError("Введите номер технологической карты.");
                return;
            }
            if(currentTechnologicalMapGroup==null){
                printError("Введите группу для технологической карты.");
                return;
            }
            TechnologicalMap tm = em.find(TechnologicalMap.class, currentTechnologicalMap.getGlobalId());

            tm.fill(currentTechnologicalMap);

            tm.setLastUpdate(new Date());
            tm.setDeletedState(currentTechnologicalMap.getDeletedState());

            MainPage mainPage = MainPage.getSessionInstance();
            if(tm.getDeletedState().equals(Boolean.TRUE) && currentTechnologicalMap.getDeletedState().equals(Boolean.FALSE)){
                tm.setUserDelete(mainPage.getCurrentUser());
            } else {
                tm.setUserEdit(mainPage.getCurrentUser());
            }
            tm.setGlobalVersion(daoService.updateVersionByDistributedObjects(TechnologicalMap.class.getSimpleName()));
            Long currentIdOfConfigurationProvider = currentTechnologicalMapGroup.getIdOfConfigurationProvider();
            Long orgOwner = currentTechnologicalMapGroup.getOrgOwner();
            tm.setIdOfConfigurationProvider(currentIdOfConfigurationProvider);
            tm.setOrgOwner(orgOwner);
            tm.setTechnologicalMapGroup(currentTechnologicalMapGroup);

            List<TechnologicalMapProduct> technologicalMapProductList = DAOReadonlyService.getInstance().getTechnologicalMapProducts(tm);
            for (TechnologicalMapProduct technologicalMapProduct: technologicalMapProductList){
                daoService.deleteEntity(technologicalMapProduct);
            }
            if(!(technologicalMapProducts == null || technologicalMapProducts.isEmpty())){
                Long version = daoService.updateVersionByDistributedObjects(TechnologicalMapProduct.class.getSimpleName());
                for (TechnologicalMapProduct technologicalMapProduct: technologicalMapProducts){
                    if(technologicalMapProduct.getGlobalId()==null){
                        technologicalMapProduct.setCreatedDate(new Date());
                        //technologicalMapProduct.setGuid(UUID.randomUUID().toString());
                        technologicalMapProduct.setGlobalVersion(version);
                        technologicalMapProduct.setTechnologicalMap(currentTechnologicalMap);
                        technologicalMapProduct.setOrgOwner(orgOwner);
                        technologicalMapProduct.setIdOfConfigurationProvider(currentIdOfConfigurationProvider);
                    } else {
                        technologicalMapProduct.setGlobalId(null);
                        technologicalMapProduct.setLastUpdate(new Date());
                        technologicalMapProduct.setGlobalVersion(version);
                        technologicalMapProduct.setOrgOwner(orgOwner);
                        technologicalMapProduct.setIdOfConfigurationProvider(currentIdOfConfigurationProvider);
                    }
                    daoService.persistEntity(technologicalMapProduct);
                }
            }
            selectedTechnologicalMapGroupPage.setCurrentTechnologicalMap(currentTechnologicalMap);
            daoService.mergeDistributedObject(tm, tm.getGlobalVersion()+1);
            printMessage("Технологическая карта сохранена успешно.");
        } catch (Exception e){
            printError("Ошибка при сохранении новой технологической карты.");
            logger.error("Error by edit Technological Map.", e);
        }
    }

    public Object deleteProduct(){
        technologicalMapProducts.remove(currentTechnologicalMapProduct);
        return null;
    }

    public Object showProducts() throws Exception {
        productItemsPanel.reload(technologicalMapProducts);
        productItemsPanel.pushCompleteHandlerList(this);
        return null;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/technologicalMap/edit";
    }

    public TechnologicalMapGroup getCurrentTechnologicalMapGroup() {
        return currentTechnologicalMapGroup;
    }

    public void setCurrentTechnologicalMapGroup(TechnologicalMapGroup currentTechnologicalMapGroup) {
        this.currentTechnologicalMapGroup = currentTechnologicalMapGroup;
    }

    public TechnologicalMap getCurrentTechnologicalMap() {
        return currentTechnologicalMap;
    }

    public void setCurrentTechnologicalMap(TechnologicalMap currentTechnologicalMap) {
        this.currentTechnologicalMap = currentTechnologicalMap;
    }


    public TechnologicalMapProduct getCurrentTechnologicalMapProduct() {
        return currentTechnologicalMapProduct;
    }

    public void setCurrentTechnologicalMapProduct(TechnologicalMapProduct currentTechnologicalMapProduct) {
        this.currentTechnologicalMapProduct = currentTechnologicalMapProduct;
    }

    public List<TechnologicalMapProduct> getTechnologicalMapProducts() {
        return technologicalMapProducts;
    }

    public void setTechnologicalMapProducts(List<TechnologicalMapProduct> technologicalMapProducts) {
        this.technologicalMapProducts = technologicalMapProducts;
    }
}
