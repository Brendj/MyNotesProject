/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxPreorder;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxPreorderAvailable;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxChanged.FoodBoxPreorderChanged;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxChanged.FoodBoxPreorderChangedItem;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxDishRemain.FoodBoxAvailableItem;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxDishRemain.FoodBoxDishRemain;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.ResFoodBoxChanged.ResFoodBoxChanged;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.ResFoodBoxChanged.ResFoodBoxChangedItem;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 10.02.20
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */

public class MenuSupplierProcessor extends AbstractProcessor<ResMenuSupplier> {

    private static final Logger logger = LoggerFactory.getLogger(MenuSupplierProcessor.class);
    private final MenuSupplier menuSupplier;

    public MenuSupplierProcessor(Session persistenceSession, MenuSupplier menuSupplier) {
        super(persistenceSession);
        this.menuSupplier = menuSupplier;
    }

    @Override
    public ResMenuSupplier process() throws Exception {
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        //Получение количества доступной продукции
        FoodBoxDishRemain foodBoxDishRemain = menuSupplier.getFoodBoxDishRemain();
        if (foodBoxDishRemain != null) {
              Org org = daoReadonlyService.findOrg(menuSupplier.getIdOfOrg());
//            Long maxVersionFromARM = foodBoxDishRemain.getMaxVersion();
//            Long currentMaxVersion = daoReadonlyService.getMaxVersionOfFoodBoxPreorderAvailable(org);
//            if (currentMaxVersion == null || maxVersionFromARM > currentMaxVersion) {
                DAOService.getInstance().deleteOldFoodBoxAvailable(org);
                for (FoodBoxAvailableItem foodBoxAvailableItem : foodBoxDishRemain.getItems()) {
                    FoodBoxPreorderAvailable foodBoxPreorderAvailable = new FoodBoxPreorderAvailable();
                    foodBoxPreorderAvailable.setCreateDate(new Date());
                    foodBoxPreorderAvailable.setAvailableQty(foodBoxAvailableItem.getAvailableQty());
                    foodBoxPreorderAvailable.setIdOfDish(foodBoxAvailableItem.getIdOfDish());
                    foodBoxPreorderAvailable.setVersion(0L);
                    foodBoxPreorderAvailable.setOrg(org);
                    session.persist(foodBoxPreorderAvailable);
                //}
            }
        }
        //Получение измений по заказам
        FoodBoxPreorderChanged foodBoxPreorderChanged = menuSupplier.getFoodBoxPreorderChanged();
        if (foodBoxPreorderChanged != null) {
            ResFoodBoxChanged resFoodBoxChanged = new ResFoodBoxChanged();
            for (FoodBoxPreorderChangedItem foodBoxPreorderChangedItem : foodBoxPreorderChanged.getItems()) {
                Long version = daoReadonlyService.getMaxVersionOfFoodBoxPreorder();
                FoodBoxPreorder foodBoxPreorder = daoReadonlyService.findFoodBoxPreorder(foodBoxPreorderChangedItem.getId());
                foodBoxPreorder.setError(foodBoxPreorderChangedItem.getError());
                foodBoxPreorder.setIdOfFoodBox(foodBoxPreorderChangedItem.getIdOfFoodBox());
                foodBoxPreorder.setCellNumber(foodBoxPreorderChangedItem.getCellNumber());
                foodBoxPreorder.setState(foodBoxPreorderChangedItem.getState());
                foodBoxPreorder.setIdOfOrder(foodBoxPreorderChangedItem.getIdOfOrder());
                foodBoxPreorder.setCancelReason(foodBoxPreorderChangedItem.getCancelReason());
                foodBoxPreorder.setVersion(version+1);
                session.merge(foodBoxPreorder);
                ResFoodBoxChangedItem resFoodBoxChangedItem = new ResFoodBoxChangedItem();
                resFoodBoxChangedItem.setError("");
                resFoodBoxChangedItem.setId(foodBoxPreorder.getIdFoodBoxPreorder());
                resFoodBoxChangedItem.setRes(0);
                resFoodBoxChangedItem.setVersion(version+1);
                resFoodBoxChanged.getItems().add(resFoodBoxChangedItem);
            }
            //Подтверждение получения новых заказов
            menuSupplier.setResFoodBoxChanged(resFoodBoxChanged);
        }
        return new ResMenuSupplier(menuSupplier);
    }

    public MenuSupplier getMenuSupplier() {
        return menuSupplier;
    }
}
