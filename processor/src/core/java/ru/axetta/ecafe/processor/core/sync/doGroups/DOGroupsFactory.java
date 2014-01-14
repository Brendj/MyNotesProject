/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.doGroups;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 13.08.13
 * Time: 15:21
 */

public class DOGroupsFactory {

    private static Logger logger = LoggerFactory.getLogger(DOGroupsFactory.class);

    private final static String packageName = "ru.axetta.ecafe.processor.core.sync.doGroups.";

    public IDOGroup createGroup(String className) {
        IDOGroup group;
        try {
            Class<?> groupClass = Class.forName(packageName + className);
            group = (IDOGroup) groupClass.newInstance();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
        return group;
    }
}
