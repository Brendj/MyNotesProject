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
        return new ResMenuSupplier(menuSupplier);
    }

    public MenuSupplier getMenuSupplier() {
        return menuSupplier;
    }
}
