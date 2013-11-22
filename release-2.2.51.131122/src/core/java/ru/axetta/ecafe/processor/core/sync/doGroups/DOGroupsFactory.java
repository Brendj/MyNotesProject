/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.doGroups;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 13.08.13
 * Time: 15:21
 */

public class DOGroupsFactory {

    private static Logger logger = LoggerFactory.getLogger(DOGroupsFactory.class);

    private final static String packageName = "ru.axetta.ecafe.processor.core.sync.doGroups.";

    private final static Map<String, IDOGroup> groupCache = new ConcurrentHashMap<String, IDOGroup>();

    private DOGroupsFactory() {}

    public static IDOGroup getGroup(String className) {
        IDOGroup group = groupCache.get(className);
        if (group == null)
            group = addGroup(className);
        return group;
    }

    private synchronized static IDOGroup addGroup(String className) {
        IDOGroup group = groupCache.get(className);
        if (group == null)
            try {
                Class<?> groupClass = Class.forName(packageName + className);
                group = (IDOGroup) groupClass.newInstance();
                groupCache.put(className, group);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new RuntimeException(ex.getMessage());
            }
        return group;
    }
}
