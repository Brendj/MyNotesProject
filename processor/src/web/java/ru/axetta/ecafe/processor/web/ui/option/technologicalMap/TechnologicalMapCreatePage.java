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
    private TechnologicalMap technologicalMap = new TechnologicalMap();
    private List<ProductItem> products = new LinkedList<ProductItem>();

    public List<ProductItem> getProducts() {
        return products;
    }

    @Override
    public void onShow() throws Exception {

    }

    public TechnologicalMapCreatePage() {

    }

    public String getPageFilename() {
        return "option/technologicalMap/create";
    }

    public TechnologicalMap getTechnologicalMap() {
        return technologicalMap;
    }

    public void createTechnologicalMap() {
        try{
            DAOService.getInstance().persistEntity(technologicalMap);
            printMessage("Новая технологическая карта создана успешно."+technologicalMap.toString());

        } catch (Exception e){
            printError("Ошибка при создании новой технологической карты.");
            logger.error("Error by create Technological Map.", e);
        }
    }

    public Object showProducts() {
        //TODO continue
        List<Product> productList = DAOService.getInstance().getDistributedObjects(Product.class);
        for (Product product: productList){
            products.add(new ProductItem(false,product));
        }
        return null;
    }

    public Object addProducts() {
        //TODO continue

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
