/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 09.07.12
 * Time: 12:04
 * To change this template use File | Settings | File Templates.
 * Класс содержащий метож для сортировки элеметов множества
 */
public class DistributedObjectsEnumComparator implements Comparator<DistributedObjectsEnum> {

    @Override
    public int compare(DistributedObjectsEnum o1, DistributedObjectsEnum o2) {
        return o1.getPriority() - o2.getPriority();
    }
}
