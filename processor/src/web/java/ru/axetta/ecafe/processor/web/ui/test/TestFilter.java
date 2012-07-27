/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.test;

import ru.axetta.ecafe.processor.web.ui.paging.AbstractFilter;
import ru.axetta.ecafe.processor.web.ui.paging.FilterProperty;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 26.07.12
 * Time: 23:52
 * To change this template use File | Settings | File Templates.
 */
public class TestFilter extends AbstractFilter {


    @FilterProperty(filteringType = FilterProperty.LIKE)
    protected String contragentName;

    public TestFilter(Class clazz) {
        super(clazz);
    }

    public boolean isEmpty() {
        return (contragentName == null) || (contragentName.length() == 0);
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }
}
