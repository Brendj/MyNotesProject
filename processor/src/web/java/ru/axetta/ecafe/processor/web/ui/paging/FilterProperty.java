/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.paging;

import jxl.write.DateTime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 25.07.12
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface FilterProperty {

    final int EQUALITY = 1;
    final int LIKE = 2;
    final int GREATER_OR_EQUAL = 3;
    final int GREATER = 4;
    final int LESS = 5;
    final int LESS_OR_EQUAL = 6;

    int filteringType() default EQUALITY;
}
