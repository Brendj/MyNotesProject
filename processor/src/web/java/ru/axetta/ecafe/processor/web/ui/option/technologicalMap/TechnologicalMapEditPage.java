/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.technologicalMap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
public class TechnologicalMapEditPage extends BasicWorkspacePage {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapEditPage.class);
    private TechnologicalMap currTechnologicalMap;
    private TechnologicalMapProduct currTechnologicalMapProduct;
    private List<ProductItem> productItems = new LinkedList<ProductItem>();
    private List<Product> pr = new LinkedList<Product>();
    private List<TechnologicalMapProduct> technologicalMapProducts = new LinkedList<TechnologicalMapProduct>();

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

    @PersistenceContext
    EntityManager em;

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    public void reload() throws Exception {
        currTechnologicalMap = em.merge(currTechnologicalMap);
        TypedQuery<TechnologicalMapProduct> query = em.createQuery("from TechnologicalMapProduct where technologicalMap=:technologicalMap",TechnologicalMapProduct.class);
        query.setParameter("technologicalMap",currTechnologicalMap);
        technologicalMapProducts = query.getResultList();
         //technologicalMapProducts = currTechnologicalMap.getTechnologicalMapProduct();
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

    @Transactional
    public void save() {
        try{
            TechnologicalMap tm = em.find(TechnologicalMap.class,currTechnologicalMap.getGlobalId());

            tm.setNameOfTechnologicalMap(currTechnologicalMap.getNameOfTechnologicalMap());
            tm.setNumberOfTechnologicalMap(currTechnologicalMap.getNumberOfTechnologicalMap());
            tm.setTechnologyOfPreparation(currTechnologicalMap.getTechnologyOfPreparation());
            tm.setTempOfPreparation(currTechnologicalMap.getTempOfPreparation());
            tm.setTermOfRealization(currTechnologicalMap.getTermOfRealization());

            tm.setEnergyValue(currTechnologicalMap.getEnergyValue());
            tm.setProteins(currTechnologicalMap.getProteins());
            tm.setCarbohydrates(currTechnologicalMap.getCarbohydrates());
            tm.setFats(currTechnologicalMap.getFats());

            tm.setMicroElCa(currTechnologicalMap.getMicroElCa());
            tm.setMicroElMg(currTechnologicalMap.getMicroElMg());
            tm.setMicroElP(currTechnologicalMap.getMicroElP());
            tm.setMicroElFe(currTechnologicalMap.getMicroElFe());

            tm.setVitaminA(currTechnologicalMap.getVitaminA());
            tm.setVitaminB1(currTechnologicalMap.getVitaminB1());
            tm.setVitaminB2(currTechnologicalMap.getVitaminB2());
            tm.setVitaminPp(currTechnologicalMap.getVitaminPp());
            tm.setVitaminC(currTechnologicalMap.getVitaminC());
            tm.setVitaminE(currTechnologicalMap.getVitaminE());

            tm.setLastUpdate(new Date());
            tm.setGlobalVersion(currTechnologicalMap.getGlobalVersion() + 1);

            tm.setDeletedState(currTechnologicalMap.getDeletedState());

            for (TechnologicalMapProduct technologicalMapProduct: tm.getTechnologicalMapProduct()) {
                TechnologicalMapProduct tmp = em.getReference(TechnologicalMapProduct.class,
                        technologicalMapProduct.getGlobalId());
                em.remove(tmp);
            }
            for (TechnologicalMapProduct tmp: technologicalMapProducts){
                tm.addTechnologicalMapProduct(tmp);
                tmp.setCreatedDate(new Date());
                tmp.setRefGUID(tm.getGuid());
                tmp.setGuid(UUID.randomUUID().toString());
                tmp = em.merge(tmp);
                //DAOService.getInstance().persistEntity(tmp);
            }
            currTechnologicalMap = em.merge(tm);
            printMessage("Технологическая карта сохранена успешно.");
        } catch (Exception e){
            printError("Ошибка при сохранении новой технологической карты.");
            logger.error("Error by edit Technological Map.", e);
        }
    }

    public Object deleteProduct(){
        currTechnologicalMap.removeTechnologicalMapProduct(currTechnologicalMapProduct);
        technologicalMapProducts = currTechnologicalMap.getTechnologicalMapProduct();
        return null;
    }
    
    public Object showProducts() throws Exception {
        //TODO continue
        List<Product> productList = DAOService.getInstance().getDistributedObjects(Product.class);
        productItems.clear();
        for (Product product: productList){
            productItems.add(new ProductItem(pr.contains(product), product));
        }
        //RuntimeContext.getAppContext().getBean(ProductItemsPanel.class).reload(technologicalMapProducts);
        return null;
    }

    public Object addProducts() {
        //TODO continue
       // productItems = RuntimeContext.getAppContext().getBean(ProductItemsPanel.class).getProductItems();
        for (ProductItem productItem: productItems){
            if(productItem.getChecked()){
                TechnologicalMapProduct technologicalMapProduct = new TechnologicalMapProduct();
                technologicalMapProduct.setIdOfProduct(productItem.getProduct().getGlobalId());
                technologicalMapProduct.setNameOfProduct(productItem.getProduct().getProductName());
                technologicalMapProduct.setDeletedState(false);
                technologicalMapProduct.setTechnologicalMap(currTechnologicalMap);
                //technologicalMapProducts.add(technologicalMapProduct);
                currTechnologicalMap.addTechnologicalMapProduct(technologicalMapProduct);
            }
        }
        technologicalMapProducts = currTechnologicalMap.getTechnologicalMapProduct();
        return null;
    }

    public String getPageFilename() {
        return "option/technologicalMap/edit";
    }

    public TechnologicalMap getCurrTechnologicalMap() {
        return currTechnologicalMap;
    }

    public void setCurrTechnologicalMap(TechnologicalMap currTechnologicalMap) {
        this.currTechnologicalMap = currTechnologicalMap;
    }

}
