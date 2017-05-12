/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import java.util.*;

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

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ) {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}
