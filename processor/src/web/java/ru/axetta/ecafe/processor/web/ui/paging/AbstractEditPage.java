/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.paging;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 27.07.12
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */
public class AbstractEditPage extends BasicWorkspacePage {

    /*public void fill(Session session, Serializable id)
            throws NoSuchMethodException, InstantiationException, InvocationTargetException, IllegalAccessException {
        Object object = session.load(entityClazz, id);
        Constructor constructor = itemClazz.getConstructor(entityClazz);
        item = (AbstractItem) constructor.newInstance(object);
        wideFields = item.getWideFields();
    } */
}
