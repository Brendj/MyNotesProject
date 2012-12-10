/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good;


import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good.group.GoodGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good.group.GoodGroupSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class GoodCreatePage extends BasicWorkspacePage implements GoodGroupSelect {
    private static final Logger logger = LoggerFactory.getLogger(GoodCreatePage.class);
    private Good good;
    private GoodGroup currentGoodGroup;
    @Autowired
    private GoodGroupItemsPanel goodGroupItemsPanel;
    @Autowired
    private DAOService daoService;

    @Override
    public void onShow() throws Exception {
        reload();
    }

    private void reload() {
        good = new Good();
        currentGoodGroup = null;
    }

    public Object onSave(){
        try {
            if(currentGoodGroup==null){
                printError("Поле 'Группа товаров' обязательное.");
                return null;
            }
            if(good.getNameOfGood()==null || good.getNameOfGood().equals("")){
                printError("Поле 'Наименование' обязательное.");
                return null;
            }
            good.setCreatedDate(new Date());
            good.setDeletedState(false);
            good.setGuid(UUID.randomUUID().toString());
            good.setGlobalVersion(daoService.updateVersionByDistributedObjects(Good.class.getSimpleName()));
            good.setOrgOwner(currentGoodGroup.getOrgOwner());

            MainPage mainPage = MainPage.getSessionInstance();
            good.setUserCreate(mainPage.getCurrentUser());
            good.setGoodGroup(currentGoodGroup);

            daoService.persistEntity(good);
            reload();
            printMessage("Товар сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при созданиии товара.");
            logger.error("Error create good",e);
        }
        return null;
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
        return "option/configuration_provider/good/create";
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }
}
