/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good.group.GoodGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good.group.GoodGroupSelect;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.ProductPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.ProductSelect;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.TechnologicalMapPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.TechnologicalMapSelect;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GoodEditPage.class);
    private Good currentGood;
    private GoodGroup currentGoodGroup;
    private Product currentProduct;
    private TechnologicalMap currentTechnologicalMap;
    private List<SelectItem> selectItemList = new LinkedList<SelectItem>();
    @PersistenceContext
    private EntityManager entityManager;
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
        this.selectItemList = new LinkedList<SelectItem>();
        this.selectItemList.add(new SelectItem(0,Good.UNIT_SCALES[currentGood.getUnitsScale()]));
        for (Integer i=0;i<Good.UNIT_SCALES.length; i++){
            if(!i.equals(currentGood.getUnitsScale())) {
                this.selectItemList.add(new SelectItem(i,Good.UNIT_SCALES[i]));
            }
        }
    }

    public Object onSave(){
        try {
            if(currentGood.getNameOfGood()==null || currentGood.getNameOfGood().equals("")){
                printWarn("Поле 'Полное наименование пищевого продукта' обязательное.");
                return null;
            }
            //if(currentGood.ge()==null || currentGood.getProductName().equals("")){
            //    printError("Поле 'Товарное название' обязательное.");
            //    return null;
            //}
            //Product p = entityManager.find(Product.class, currentProduct.getGlobalId());
            Good g = entityManager.find(Good.class, currentGood.getGlobalId());
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
            g.setGlobalVersion(daoService.updateVersionByDistributedObjects(Good.class.getSimpleName()));
            daoService.mergeDistributedObject(g, g.getGlobalVersion()+1);
            currentGood = entityManager.find(Good.class, currentGood.getGlobalId());
            selectedGoodGroupPage.setCurrentGood(currentGood);
            printMessage("Товар сохранен успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении продукта.");
            logger.error("Error saved Product", e);
        }
        return null;
    }

    @Transactional
    public void remove(){
        if(!currentGood.getDeletedState()) {
            printError("Товар не может быть удален.");
            return;
        }
        try{
            Good g = entityManager.getReference(Good.class, currentGood.getGlobalId());
            entityManager.remove(g);
            printMessage("Товар успешно удален.");
        }  catch (Exception e){
            printError("Ошибка при удалении товара.");
            logger.error("Error by delete Good.", e);
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
        return "option/configuration_provider/good/edit";
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
