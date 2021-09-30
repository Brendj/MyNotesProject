/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
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
import java.util.ArrayList;
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
public class GoodEditPage extends BasicWorkspacePage implements GoodGroupSelect, ProductSelect, TechnologicalMapSelect {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodEditPage.class);
    private Good currentGood;
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
    @Autowired
    private SelectedGoodGroupPage selectedGoodGroupPage;
    @Autowired
    private GoodListPage goodListPage;
    @Autowired
    private DAOReadonlyService daoReadonlyService;
    private Integer unitScale;

    @Override
    public void onShow() throws Exception {
        selectedGoodGroupPage.onShow();
        currentGood = selectedGoodGroupPage.getCurrentGood();
        if(currentGood.getGoodGroup()!=null){
            currentGoodGroup = currentGood.getGoodGroup();
        }
        if(currentGood.getTechnologicalMap()!=null){
            currentTechnologicalMap = currentGood.getTechnologicalMap();
        }
        if(currentGood.getProduct()!=null){
            currentProduct = currentGood.getProduct();
        }
        unitScale = currentGood.getUnitsScale().ordinal();
        this.selectItemList = new ArrayList<SelectItem>();
        for (UnitScale unitScale: UnitScale.values()){
            this.selectItemList.add(new SelectItem(unitScale.ordinal(),unitScale.toString()));
        }
    }

    public Object onSave(){
        try {
            if(currentGood.getNameOfGood()==null || currentGood.getNameOfGood().equals("")){
                printWarn("Поле 'Полное наименование пищевого продукта' обязательное.");
                return null;
            }
            Good g = daoReadonlyService.getGood(currentGood.getGlobalId());
            currentGood.setUnitsScale(UnitScale.fromInteger(unitScale));
            g.fill(currentGood);
            g.setLastUpdate(new Date());
            g.setDeletedState(currentGood.getDeletedState());

            MainPage mainPage = MainPage.getSessionInstance();
            if(g.getDeletedState().equals(Boolean.TRUE) && currentGood.getDeletedState().equals(Boolean.FALSE)){
                g.setUserDelete(mainPage.getCurrentUser());
            } else {
                g.setUserEdit(mainPage.getCurrentUser());
            }

            g.setGoodGroup(currentGoodGroup);
            g.setIdOfConfigurationProvider(currentGoodGroup.getIdOfConfigurationProvider());
            g.setGlobalVersion(daoService.updateVersionByDistributedObjects(Good.class.getSimpleName()));
            daoService.mergeDistributedObject(g, g.getGlobalVersion()+1);
            currentGood = daoReadonlyService.getGood(currentGood.getGlobalId());
            selectedGoodGroupPage.setCurrentGood(currentGood);
            printMessage("Товар сохранен успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении продукта.");
            LOGGER.error("Error saved Product", e);
        }
        return null;
    }

    public Object remove(){
        removeGood();
        return null;
    }

    private void removeGood() {
        if(!currentGood.getDeletedState()) {
            printError("Товар не может быть удален.");
            return;
        }
        try{
            daoService.removeGood(currentGood);
            goodListPage.reload();
            printMessage("Товар успешно удален.");
        }  catch (Exception e){
            printError("Ошибка при удалении товара.");
            LOGGER.error("Error by delete Good.", e);
        }
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
        goodGroupItemsPanel.setSelectGoodGroup(currentGoodGroup);
        goodGroupItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(GoodGroup goodGroup) {
        currentGoodGroup = goodGroup;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/good/edit";
    }

    public Integer getUnitScale() {
        return unitScale;
    }

    public void setUnitScale(Integer unitScale) {
        this.unitScale = unitScale;
    }

    public GoodGroup getCurrentGoodGroup() {
        return currentGoodGroup;
    }

    public Good getCurrentGood() {
        return currentGood;
    }

    public void setCurrentGood(Good currentGood) {
        this.currentGood = currentGood;
    }

    public List<SelectItem> getSelectItemList() {
        return selectItemList;
    }

    public void setSelectItemList(List<SelectItem> selectItemList) {
        this.selectItemList = selectItemList;
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
}
