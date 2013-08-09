/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.discount;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 30.07.13
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */
public class SetupDiscountPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(SetupDiscountPage.class);


    public String getPageFilename() {
        return "discount/setup_discount";
    }

    public String getPageTitle() {
        return "Управление льготами";
    }

    public void fill(Session session) throws Exception {
    }
}
