/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils.report;

public interface DynamicBean {
    public Object getValue(String propertyName, Class clazz) throws Exception;
}
