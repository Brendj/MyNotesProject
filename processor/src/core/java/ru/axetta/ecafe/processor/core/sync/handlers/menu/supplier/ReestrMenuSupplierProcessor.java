/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.persistence.MenuSupplier;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
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

public class ReestrMenuSupplierProcessor extends AbstractProcessor<ResReestrMenuSupplier> {

    private static final Logger logger = LoggerFactory.getLogger(ReestrMenuSupplierProcessor.class);
    private final ReestrMenuSupplier reestrMenuSupplier;
    private final List<ResMenuSupplierItem> resMenuSupplierItems;

    public ReestrMenuSupplierProcessor(Session persistenceSession, ReestrMenuSupplier reestrMenuSupplier) {
        super(persistenceSession);
        this.reestrMenuSupplier = reestrMenuSupplier;
        resMenuSupplierItems = new ArrayList<ResMenuSupplierItem>();
    }

    @Override
    public ResReestrMenuSupplier process() throws Exception {
        ResReestrMenuSupplier result = new ResReestrMenuSupplier();
        List<ResMenuSupplierItem> items = new ArrayList<ResMenuSupplierItem>();
        try {
            ResMenuSupplierItem resItem = null;
            boolean errorFound = false;
            for (MenuSupplierItem item : reestrMenuSupplier.getItems()) {

                errorFound = !item.getResCode().equals(MenuSupplierItem.ERROR_CODE_ALL_OK);
                if (!errorFound) {

                    WtOrgGroup orgGroup = item.getOrgGroup();
                    WtCategoryItem categoryItem = item.getCategoryItem();
                    WtTypeOfProductionItem typeOfProduction = item.getTypeOfProduction();
                    WtAgeGroupItem ageGroupItem = item.getAgeGroupItem();
                    WtDietType dietType = item.getDietType();
                    WtComplexGroupItem complexGroupItem = item.getComplexGroupItem();
                    WtGroupItem groupItem = item.getGroupItem();
                    WtDish dish = item.getDish();
                    WtMenuGroup menuGroup = item.getMenuGroup();
                    WtMenu menu = item.getMenu();
                    WtComplex complex = item.getComplex();

                    MenuSupplier menuSupplier = new MenuSupplier(orgGroup, categoryItem, typeOfProduction, ageGroupItem,
                            dietType, complexGroupItem, groupItem, dish, menuGroup, menu, complex);

                    session.saveOrUpdate(menuSupplier);

                    resItem = new ResMenuSupplierItem(menuSupplier, item.getResCode());
                }
                if (errorFound) {
                    resItem = new ResMenuSupplierItem();
                    resItem.setResultCode(item.getResCode());
                    resItem.setErrorMessage(item.getErrorMessage());
                }
                items.add(resItem);
            }
            session.flush();
        } catch (Exception e) {
            logger.error("Error saving ReestrMenuSupplier", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    public ReestrMenuSupplierData processData() throws Exception {
        ReestrMenuSupplierData result = new ReestrMenuSupplierData();
        List<ResMenuSupplierItem> items = new ArrayList<ResMenuSupplierItem>();
        ResMenuSupplierItem resItem;
        //List<MenuSupplier> list = DAOUtils
        //        .getMenuSupplierForOrgSinceVersion(session, reestrMenuSupplier.getIdOfOrgOwner(),
        //                reestrMenuSupplier.getMaxVersion());
        //for (MenuSupplier taloon : list) {
        //    if (taloon != null) {
        //        resItem = new ResMenuSupplierItem(taloon);
        //        items.add(resItem);
        //    }
        //}

        result.setItems(items);
        return result;
    }

    public List<ResMenuSupplierItem> getResMenuSupplierItems() {
        return resMenuSupplierItems;
    }
}
