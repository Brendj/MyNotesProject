/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
    private final List<MenuSupplier> resMenuSupplierItems;

    public MenuSupplierProcessor(Session persistenceSession, MenuSupplier menuSupplier) {
        super(persistenceSession);
        this.menuSupplier = menuSupplier;
        resMenuSupplierItems = new ArrayList<MenuSupplier>();
    }

    @Override
    public ResMenuSupplier process() throws Exception {
        ResMenuSupplier result = new ResMenuSupplier();
        List<MenuSupplier> items = new ArrayList<MenuSupplier>();
        try {
            MenuSupplier resItem = null;
            boolean errorFound = false;
            //for (MenuSupplierItem item : menuSupplier.getItems()) {
            //
            //    errorFound = !item.getResCode().equals(MenuSupplierItem.ERROR_CODE_ALL_OK);
            //    if (!errorFound) {
            //
            //        WtOrgGroup orgGroup = item.getOrgGroup();
            //        WtCategoryItem categoryItem = item.getCategoryItem();
            //        WtTypeOfProductionItem typeOfProduction = item.getTypeOfProduction();
            //        WtAgeGroupItem ageGroupItem = item.getAgeGroupItem();
            //        WtDietType dietType = item.getDietType();
            //        WtComplexGroupItem complexGroupItem = item.getComplexGroupItem();
            //        WtGroupItem groupItem = item.getGroupItem();
            //        WtDish dish = item.getDish();
            //        WtMenuGroup menuGroup = item.getMenuGroup();
            //        WtMenu menu = item.getMenu();
            //        WtComplex complex = item.getComplex();
            //
            //        MenuSupplier menuSupplier = new MenuSupplier(orgGroup, categoryItem, typeOfProduction, ageGroupItem,
            //                dietType, complexGroupItem, groupItem, dish, menuGroup, menu, complex);
            //
            //        session.saveOrUpdate(menuSupplier);
            //
            //        resItem = new ResMenuSupplierItem(menuSupplier, item.getResCode());
            //    }
            //    if (errorFound) {
            //        resItem = new ResMenuSupplierItem();
            //        resItem.setResultCode(item.getResCode());
            //        resItem.setErrorMessage(item.getErrorMessage());
            //    }
            //    items.add(resItem);
            //}
            session.flush();
        } catch (Exception e) {
            logger.error("Error saving menuSupplier", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    public MenuSupplierData processData() throws Exception {
        MenuSupplierData result = new MenuSupplierData();
        List<MenuSupplier> items = new ArrayList<MenuSupplier>();
        MenuSupplier resItem;
        //List<MenuSupplier> list = DAOUtils
        //        .getMenuSupplierForOrgSinceVersion(session, menuSupplier.getIdOfOrgOwner(),
        //                menuSupplier.getMaxVersion());
        //for (MenuSupplier taloon : list) {
        //    if (taloon != null) {
        //        resItem = new ResMenuSupplierItem(taloon);
        //        items.add(resItem);
        //    }
        //}

        result.setItems(items);
        return result;
    }

    public List<MenuSupplier> getResMenuSupplierItems() {
        return resMenuSupplierItems;
    }
}
