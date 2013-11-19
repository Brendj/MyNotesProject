/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodViewPage.class);
    private Good currentGood;
    private Product currentProduct;
    private TechnologicalMap currentTechnologicalMap;
    @Autowired
    private SelectedGoodGroupPage selectedGoodGroupPage;
    @Autowired
    private DAOService daoService;

    @Override
    public void onShow() throws Exception {
        selectedGoodGroupPage.onShow();
        currentGood = selectedGoodGroupPage.getCurrentGood();
        currentTechnologicalMap = null;
        currentProduct = null;
        reload();
    }

    protected void reload(){
        if(currentGood!=null){
            if(currentGood.getTechnologicalMap()!=null){
                currentTechnologicalMap = daoService.saveEntity(currentGood.getTechnologicalMap());
            }
            if(currentGood.getProduct()!=null){
                currentProduct = daoService.saveEntity(currentGood.getProduct());
            }
        }
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/good/view";
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
