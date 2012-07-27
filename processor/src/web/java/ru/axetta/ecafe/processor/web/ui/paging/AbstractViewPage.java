/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.paging;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 26.07.12
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
public class AbstractViewPage extends BasicWorkspacePage {

    protected final Class itemClazz;
    protected final Class entityClazz;
    private AbstractItem item;
    private Map<String, Object> wideFields = null;

    private AbstractViewPage() {
        itemClazz = null;
        entityClazz = null;
    }

    public AbstractViewPage(Class itemClazz, Class entityClazz) {
        this.itemClazz = itemClazz;
        this.entityClazz = entityClazz;
    }

    public void fill(Session session, Serializable id)
            throws NoSuchMethodException, InstantiationException, InvocationTargetException, IllegalAccessException {
        Object object = session.load(entityClazz, id);
        Constructor constructor = itemClazz.getConstructor(entityClazz);
        item = (AbstractItem) constructor.newInstance(object);
        wideFields = item.getWideFields();
    }

    public List<String> getWideFieldKeys() {
        List<String> wideFieldKeys = new LinkedList<String>();
        wideFieldKeys.addAll(wideFields.keySet());
        return wideFieldKeys;
    }

    public Map getWideFields() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return item.getWideFields();
    }

    @Override
    public String getPageFilename() {
        return "test/view";
    }


}
