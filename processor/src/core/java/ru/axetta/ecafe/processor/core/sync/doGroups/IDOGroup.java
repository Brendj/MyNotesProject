/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.doGroups;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 13.08.13
 * Time: 15:23
 */

public interface IDOGroup {

    DOSyncClass getDOSyncClass(String classId);

    boolean isHasClass(String classId);
}
