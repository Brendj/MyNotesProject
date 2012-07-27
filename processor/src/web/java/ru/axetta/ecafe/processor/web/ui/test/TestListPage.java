/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.test;

import ru.axetta.ecafe.processor.web.ui.paging.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 23.07.12
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
public class TestListPage extends AbstractListPage {

    public TestListPage(Class itemClazz, Class entityClazz, Class filterClazz)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(itemClazz, entityClazz, filterClazz);
    }

    public String getPageFilename() {
        return "test/test-list";
    }


}
