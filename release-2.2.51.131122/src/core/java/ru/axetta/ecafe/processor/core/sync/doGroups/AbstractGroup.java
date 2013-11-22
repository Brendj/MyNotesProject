/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.doGroups;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 14.08.13
 * Time: 12:50
 */

abstract class AbstractGroup implements IDOGroup {

    // Ключ - идентификатор класса, значение - объект, содержащий информацию о классе РО для синхронизации.
    protected final Map<String, DOSyncClass> doClassMap = new HashMap<String, DOSyncClass>();

    public AbstractGroup() {
        fill();
    }

    abstract protected void fill();

    @Override
    public boolean isHasClass(String classId) {
        return doClassMap.containsKey(classId);
    }

    @Override
    public DOSyncClass getDOSyncClass(String classId) {
        return doClassMap.get(classId);
    }
}
