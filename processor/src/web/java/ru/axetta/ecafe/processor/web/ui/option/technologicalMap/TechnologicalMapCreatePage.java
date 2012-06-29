/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.technologicalMap;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.LoggerFactory;
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
public class TechnologicalMapCreatePage extends BasicWorkspacePage {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapCreatePage.class);
    private TechnologicalMap technologicalMap;
    private List<ProductItem> products = new LinkedList<ProductItem>();
    private List<TechnologicalMapProduct> technologicalMapProducts = new LinkedList<TechnologicalMapProduct>();

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
            DAOService.getInstance().persistEntity(technologicalMap);
            for (TechnologicalMapProduct technologicalMapProduct: technologicalMap.getTechnologicalMapProduct()){
                technologicalMapProduct.setCreatedDate(new Date());
                DAOService.getInstance().persistEntity(technologicalMapProduct);
            }
            /*for (TechnologicalMapProduct technologicalMapProduct: technologicalMapProducts){
                technologicalMapProduct.setIdOfTechnoMap(technologicalMap.getGlobalId());
                technologicalMapProduct.setCreatedDate(new Date());
                technologicalMapProduct.setDeletedState(false);
                DAOService.getInstance().persistEntity(technologicalMapProduct);
            }*/
            printMessage("Новая технологическая карта создана успешно."+technologicalMap.toString());
            technologicalMap = new TechnologicalMap();
        } catch (Exception e){
            printError("Ошибка при создании новой технологической карты.");
            logger.error("Error by create Technological Map.", e);
        }
    }

    public Object showProducts() {
        //TODO continue
        List<Product> productList = DAOService.getInstance().getDistributedObjects(Product.class);
        products.clear();
        for (Product product: productList){
            products.add(new ProductItem(false,product));
        }
        return null;
    }

    public Object addProducts() {
        //TODO continue
        for (ProductItem productItem: products){
            if(productItem.getChecked()){
                TechnologicalMapProduct technologicalMapProduct = new TechnologicalMapProduct();
                technologicalMapProduct.setIdOfProduct(productItem.product.getGlobalId());
                technologicalMapProduct.setNameOfProduct(productItem.product.getProductName());

               /* technologicalMapProduct.setIdOfTechnoMap(technologicalMap.getGlobalId());*/

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
