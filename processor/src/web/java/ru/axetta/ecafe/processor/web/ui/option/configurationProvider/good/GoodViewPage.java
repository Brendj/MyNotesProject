/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class GoodViewPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(GoodViewPage.class);
    private Good currentGood;
    private Product currentProduct;
    private TechnologicalMap currentTechnologicalMap;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private SelectedGoodGroupPage selectedGoodGroupPage;

    @Override
    public void onShow() throws Exception {
        selectedGoodGroupPage.onShow();
        currentGood = selectedGoodGroupPage.getCurrentGood();
        currentTechnologicalMap = null;
        currentProduct = null;
        reload();
    }

    @Transactional
    protected void reload(){
        if(currentGood!=null){
            if(currentGood.getTechnologicalMap()!=null){
                currentTechnologicalMap = entityManager.merge(currentGood.getTechnologicalMap());
            }
            if(currentGood.getProduct()!=null){
                currentProduct = entityManager.merge(currentGood.getProduct());
            }
        }
    }

    public String getPageFilename() {
        return "option/configuration_provider/good/view";
    }

    public Good getCurrentGood() {
        return currentGood;
    }

    public void setCurrentGood(Good currentGood) {
        this.currentGood = currentGood;
    }

    public Product getCurrentProduct() {
        return currentProduct;
    }

    public TechnologicalMap getCurrentTechnologicalMap() {
        return currentTechnologicalMap;
    }
}
