/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.02.14
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection collection){
        return collection==null || collection.isEmpty();
    }

    public static boolean isEmpty(List list){
        return isEmpty((Collection) list) || list.get(0)==null;
    }

    private CollectionUtils() {}
}
