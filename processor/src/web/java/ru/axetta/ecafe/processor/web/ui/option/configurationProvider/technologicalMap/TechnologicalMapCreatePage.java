/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderMenu;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.group.TechnologicalMapGroupMenu;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct.ProductItem;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct.ProductItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct.ProductSelect;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
public class TechnologicalMapCreatePage extends BasicWorkspacePage implements ProductSelect,
        ConfigurationProviderSelect {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapCreatePage.class);

    private TechnologicalMap technologicalMap;
    private List<ProductItem> products = new LinkedList<ProductItem>();
    private TechnologicalMapProduct currTechnologicalMapProduct;
    private List<TechnologicalMapProduct> technologicalMapProducts;
    private TechnologicalMapGroupMenu technologicalMapGroupMenu = new TechnologicalMapGroupMenu();
    private Long currentIdOfTechnologicalMapGroup;
    private Long currentIdOfConfigurationProvider;
    private ConfigurationProviderMenu configurationProviderMenu = new ConfigurationProviderMenu();
    private List<ConfigurationProvider> configurationProviderList;
    private List<TechnologicalMapGroup> technologicalMapGroupList;

    @Autowired
    private DAOService daoService;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;
    @Autowired
    private ProductItemsPanel productItemsPanel;

    @Override
    public void onShow() throws Exception {
        technologicalMap=new TechnologicalMap();
        technologicalMapProducts = new LinkedList<TechnologicalMapProduct>();
        technologicalMapGroupList = daoService.getDistributedObjects(TechnologicalMapGroup.class);
        configurationProviderList = daoService.getDistributedObjects(ConfigurationProvider.class);
        if(getRendered()){
            configurationProviderMenu.readAllItems(configurationProviderList);
            technologicalMapGroupMenu.readAllItems(technologicalMapGroupList);
        } else {
            printError("Отсутсвуют группы технологических карт.");
        }
    }

    public boolean getRendered(){
        return !(configurationProviderList==null || configurationProviderList.isEmpty() || technologicalMapGroupList==null || technologicalMapGroupList.isEmpty());
    }

    @Override
    public void select(List<ProductItem> productItemList) {
        if (!(productItemList == null || productItemList.isEmpty())) {
            for (ProductItem productItem: productItemList){
                TechnologicalMapProduct technologicalMapProduct = new TechnologicalMapProduct();
                technologicalMapProduct.setProduct(productItem.getProduct());
                technologicalMapProduct.setDeletedState(false);
                technologicalMapProduct.setTechnologicalMap(technologicalMap);
                technologicalMapProducts.add(technologicalMapProduct);
            }
        }
    }

    @Override
    public void select(ConfigurationProvider configurationProvider) {
        technologicalMap.setIdOfConfigurationProvider(configurationProvider.getIdOfConfigurationProvider());
    }

    public Object showConfigurationProviderSelection() throws Exception{
        configurationProviderItemsPanel.reload();
        configurationProviderItemsPanel.pushCompleteHandler(this);
        return null;
    }

    public String getPageFilename() {
        return "option/configuration_provider/technologicalMap/create";
    }

    public void createTechnologicalMap() {
        try{
            if(technologicalMap.getNameOfTechnologicalMap()==null || technologicalMap.getNameOfTechnologicalMap().equals("")){
                printError("Введите имя технологической карты.");
                return;
            }
            if(technologicalMap.getNumberOfTechnologicalMap()==null || technologicalMap.getNumberOfTechnologicalMap().equals("")){
                printError("Введите номер технологической карты.");
                return;
            }
            /*if(technologicalMap.getTechnologicalMapGroup()==null){
                printError("Введите группу для технологической карты.");
                return;
            }*/
            technologicalMap.setCreatedDate(new Date());
            technologicalMap.setDeletedState(false);
            technologicalMap.setGlobalVersion(daoService.getVersionByDistributedObjects(TechnologicalMap.class));
            UUID tmUUID = UUID.randomUUID();
            technologicalMap.setGuid(tmUUID.toString());

            MainPage mainPage = MainPage.getSessionInstance();
            technologicalMap.setUserCreate(mainPage.getCurrentUser());

            technologicalMap.setTechnologicalMapGroup(daoService.findRefDistributedObject(TechnologicalMapGroup.class, currentIdOfTechnologicalMapGroup));
            daoService.persistEntity(technologicalMap);

            daoService.setConfigurationProviderInDO(TechnologicalMapGroup.class,currentIdOfTechnologicalMapGroup, currentIdOfConfigurationProvider);

            for (TechnologicalMapProduct technologicalMapProduct: technologicalMapProducts){
                technologicalMapProduct.setCreatedDate(new Date());
                technologicalMapProduct.setGuid(UUID.randomUUID().toString());
                technologicalMapProduct.setTechnologicalMap(technologicalMap);
                technologicalMapProduct.setIdOfConfigurationProvider(currentIdOfConfigurationProvider);
                technologicalMapProduct.setGlobalVersion(daoService.getVersionByDistributedObjects(TechnologicalMapProduct.class));
                daoService.persistEntity(technologicalMapProduct);
            }



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
        productItemsPanel.reload(new LinkedList<TechnologicalMapProduct>());
        productItemsPanel.pushCompleteHandlerList(this);
        return null;
    }

    public ConfigurationProviderMenu getConfigurationProviderMenu() {
        return configurationProviderMenu;
    }

    public void setConfigurationProviderMenu(ConfigurationProviderMenu configurationProviderMenu) {
        this.configurationProviderMenu = configurationProviderMenu;
    }

    public Long getCurrentIdOfConfigurationProvider() {
        return currentIdOfConfigurationProvider;
    }

    public void setCurrentIdOfConfigurationProvider(Long currentIdOfConfigurationProvider) {
        this.currentIdOfConfigurationProvider = currentIdOfConfigurationProvider;
    }

    public Long getCurrentIdOfTechnologicalMapGroup() {
        return currentIdOfTechnologicalMapGroup;
    }

    public void setCurrentIdOfTechnologicalMapGroup(Long currentIdOfTechnologicalMapGroup) {
        this.currentIdOfTechnologicalMapGroup = currentIdOfTechnologicalMapGroup;
    }

    public TechnologicalMapGroupMenu getTechnologicalMapGroupMenu() {
        return technologicalMapGroupMenu;
    }

    public void setTechnologicalMapGroupMenu(TechnologicalMapGroupMenu technologicalMapGroupMenu) {
        this.technologicalMapGroupMenu = technologicalMapGroupMenu;
    }

    public TechnologicalMap getTechnologicalMap() {
        return technologicalMap;
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

    public TechnologicalMapProduct getCurrTechnologicalMapProduct() {
        return currTechnologicalMapProduct;
    }

}