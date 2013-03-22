/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good;


import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupSelect;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.ProductPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.ProductSelect;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.TechnologicalMapPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.TechnologicalMapSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
public class GoodCreatePage extends BasicWorkspacePage implements GoodGroupSelect, ProductSelect,
        TechnologicalMapSelect {
    private static final Logger logger = LoggerFactory.getLogger(GoodCreatePage.class);
    private Good good;
    private GoodGroup currentGoodGroup;
    private Product currentProduct;
    private TechnologicalMap currentTechnologicalMap;
    private List<SelectItem> selectItemList = new LinkedList<SelectItem>();
    @Autowired
    private GoodGroupItemsPanel goodGroupItemsPanel;
    @Autowired
    private ProductPanel productPanel;
    @Autowired
    private TechnologicalMapPanel technologicalMapPanel;
    @Autowired
    private DAOService daoService;

    @Override
    public void onShow() throws Exception {
        reload();
    }

    private void reload() {
        good = new Good();
        currentProduct = null;
        currentTechnologicalMap =null;
        currentGoodGroup = null;
        this.selectItemList = new LinkedList<SelectItem>();
        for (Integer i=0;i<Good.UNIT_SCALES.length; i++){
            this.selectItemList.add(new SelectItem(i,Good.UNIT_SCALES[i]));
        }
    }

    public Object onSave(){
        try {
            if(currentGoodGroup==null){
                printError("Поле 'Группа товаров' обязательное.");
                return null;
            }
            if(currentProduct == null && currentTechnologicalMap == null){
                printError("Не выбран 'Продукт' или 'Технологическая карта' обязательное.");
                return null;
            }
            if(good.getNameOfGood()==null || good.getNameOfGood().equals("")){
                printError("Поле 'Наименование' обязательное.");
                return null;
            }
            good.setCreatedDate(new Date());
            good.setDeletedState(true);
            good.setGuid(UUID.randomUUID().toString());
            good.setGlobalVersion(daoService.updateVersionByDistributedObjects(Good.class.getSimpleName()));
            good.setOrgOwner(currentGoodGroup.getOrgOwner());

            MainPage mainPage = MainPage.getSessionInstance();
            good.setUserCreate(mainPage.getCurrentUser());
            good.setGoodGroup(currentGoodGroup);
            good.setProduct(currentProduct);
            good.setTechnologicalMap(currentTechnologicalMap);
            daoService.persistEntity(good);
            reload();
            printMessage("Товар сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при созданиии товара.");
            logger.error("Error create good",e);
        }
        return null;
    }

    @Override
    public void select(Product product) {
        currentProduct = product;
    }

    public Object selectProduct() throws Exception{
        productPanel.reload();
        if(currentProduct!=null){
            productPanel.setSelectProduct(currentProduct);
        }
        productPanel.pushCompleteHandler(this);
        return null;
    }

    public Object selectTechnologicalMap() throws Exception{
        technologicalMapPanel.reload();
        if(currentTechnologicalMap!=null){
            technologicalMapPanel.setSelectTechnologicalMap(currentTechnologicalMap);
        }
        technologicalMapPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(TechnologicalMap technologicalMap) {
        currentTechnologicalMap = technologicalMap;
    }

    public Object selectGoodGroup() throws Exception{
        goodGroupItemsPanel.reload();
        if(currentGoodGroup!=null){
            goodGroupItemsPanel.setSelectGoodGroup(currentGoodGroup);
        }
        goodGroupItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(GoodGroup goodGroup) {
        currentGoodGroup = goodGroup;
    }

    public GoodGroup getCurrentGoodGroup() {
        return currentGoodGroup;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/good/create";
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public Product getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
    }

    public TechnologicalMap getCurrentTechnologicalMap() {
        return currentTechnologicalMap;
    }

    public void setCurrentTechnologicalMap(TechnologicalMap currentTechnologicalMap) {
        this.currentTechnologicalMap = currentTechnologicalMap;
    }

    public List<SelectItem> getSelectItemList() {
        return selectItemList;
    }

    public void setSelectItemList(List<SelectItem> selectItemList) {
        this.selectItemList = selectItemList;
    }
}
