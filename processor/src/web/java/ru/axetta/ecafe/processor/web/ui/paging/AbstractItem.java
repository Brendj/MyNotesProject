/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.paging;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 26.07.12
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractItem {

    public static final String GETTER_PREFIX = "get";

    public String createGetterName(String fieldName) {
        String getterName = GETTER_PREFIX + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return getterName;
    }

    public Map<String, Object> getNarrowFields()
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Map<String, Object> properties = new HashMap<String, Object>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(NarrowProperty.class)) {
                Method method = this.getClass().getMethod(createGetterName(field.getName()));
                properties.put(field.getName(), method.invoke(this));
            }
        }

        return properties;
    }

    public Map<String, Object> getWideFields()
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Map<String, Object> properties = new HashMap<String, Object>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(WideProperty.class)) {
                WideProperty wideProperty = field.getAnnotation(WideProperty.class);
                Method method = this.getClass().getMethod(createGetterName(field.getName()));
                properties.put(wideProperty.name(), method.invoke(this));
            }
        }

        return properties;
    }
    
    public abstract Object getId();
}
