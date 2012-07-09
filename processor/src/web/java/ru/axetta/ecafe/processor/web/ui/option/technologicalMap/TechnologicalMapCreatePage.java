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
import ru.axetta.ecafe.processor.web.ui.option.technologicalMap.technologicalMapProduct.ProductItem;
import ru.axetta.ecafe.processor.web.ui.option.technologicalMap.technologicalMapProduct.ProductItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.technologicalMap.technologicalMapProduct.ProductSelect;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
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
public class TechnologicalMapCreatePage extends BasicWorkspacePage implements ProductSelect {

    @Override
    public void select(List<ru.axetta.ecafe.processor.web.ui.option.technologicalMap.technologicalMapProduct.ProductItem> productItemList) {
        if (!(productItemList == null || productItemList.isEmpty())) {
            for (ru.axetta.ecafe.processor.web.ui.option.technologicalMap.technologicalMapProduct.ProductItem productItem: productItemList){
                TechnologicalMapProduct technologicalMapProduct = new TechnologicalMapProduct();
                //   technologicalMapProduct.setIdOfProduct(productItem.getProduct().getGlobalId());
                technologicalMapProduct.setProduct(productItem.getProduct());
               // technologicalMapProduct.setNameOfProduct(productItem.getProduct().getProductName());
                technologicalMapProduct.setDeletedState(false);
                technologicalMapProduct.setTechnologicalMap(technologicalMap);
                //technologicalMapProducts.add(technologicalMapProduct);
                technologicalMap.addTechnologicalMapProduct(technologicalMapProduct);
            }
            technologicalMapProducts = technologicalMap.getTechnologicalMapProduct();
        }
    }

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapCreatePage.class);
    private TechnologicalMap technologicalMap;
    private List<ProductItem> products = new LinkedList<ProductItem>();
    private TechnologicalMapProduct currTechnologicalMapProduct;
    private List<TechnologicalMapProduct> technologicalMapProducts;

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

    public List<ProductItem> getProducts() {
        return products;
    }

    @Override
    public void onShow() throws Exception {
         technologicalMap=new TechnologicalMap();
         technologicalMapProducts = new LinkedList<TechnologicalMapProduct>();
    }

    public String getPageFilename() {
        return "option/technologicalMap/create";
    }

    public TechnologicalMap getTechnologicalMap() {
        return technologicalMap;
    }

    public void createTechnologicalMap() {
        try{
            technologicalMap.setCreatedDate(new Date());
            technologicalMap.setDeletedState(false);
            technologicalMap.setGlobalVersion(0L);
            UUID tmUUID = UUID.randomUUID();
            technologicalMap.setGuid(tmUUID.toString());
            for (TechnologicalMapProduct technologicalMapProduct: technologicalMap.getTechnologicalMapProduct()){
                technologicalMapProduct.setCreatedDate(new Date());
                technologicalMapProduct.setGuid(UUID.randomUUID().toString());
            }
            DAOService.getInstance().persistEntity(technologicalMap);
            printMessage("Новая технологическая карта создана успешно.");
            technologicalMap = new TechnologicalMap();
            technologicalMapProducts = new LinkedList<TechnologicalMapProduct>();
        } catch (Exception e){
            printError("Ошибка при создании новой технологической карты.");
            logger.error("Error by create Technological Map.", e);
        }
    }

    public Object deleteProduct(){
        technologicalMapProducts.remove(currTechnologicalMapProduct);
        return null;
    }

    public Object showProducts() throws Exception {
        //TODO continue
        /*List<Product> productList = DAOService.getInstance().getDistributedObjects(Product.class);
        products.clear();
        for (Product product: productList){
            products.add(new ProductItem(false,product));
        }*/
        RuntimeContext.getAppContext().getBean(ProductItemsPanel.class).reload(new LinkedList<TechnologicalMapProduct>());
        RuntimeContext.getAppContext().getBean(ProductItemsPanel.class).pushCompleteHandlerList(RuntimeContext.getAppContext().getBean(getClass()));
        return null;
    }

    public Object addProducts() {
        //TODO continue
        for (ProductItem productItem: products){
            if(productItem.getChecked()){
                TechnologicalMapProduct technologicalMapProduct = new TechnologicalMapProduct();
              //  technologicalMapProduct.setIdOfProduct(productItem.product.getGlobalId());
                technologicalMapProduct.setProduct(productItem.getProduct());
             //   technologicalMapProduct.setNameOfProduct(productItem.product.getProductName());
                technologicalMapProduct.setDeletedState(false);
                technologicalMapProduct.setTechnologicalMap(technologicalMap);
                technologicalMapProducts.add(technologicalMapProduct);
                technologicalMap.addTechnologicalMapProduct(technologicalMapProduct);
            }
        }
        return null;
    }

    public static class ProductItem{
        private Boolean checked=false;
        private Product product;

        public ProductItem(Boolean checked, Product product) {
            this.checked = checked;
            this.product = product;
        }

        public Boolean getChecked() {
            return checked;
        }

        public void setChecked(Boolean checked) {
            this.checked = checked;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }
    }

}
