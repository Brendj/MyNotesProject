/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.doGroups;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 15.08.13
 * Time: 18:51
 */

public class DOSyncClass implements Comparable<DOSyncClass> {

    // Экземпляр класса синхронизируемого РО.
    private Class<? extends DistributedObject> doClass;

    // Приоритет синхронизации для данного класса РО.
    private int priority;

    public DOSyncClass(Class<? extends DistributedObject> doClass, int priority) {
        this.doClass = doClass;
        this.priority = priority;
    }

    public Class<? extends DistributedObject> getDoClass() {
        return doClass;
    }

    public void setDoClass(Class<? extends DistributedObject> doClass) {
        this.doClass = doClass;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(DOSyncClass o) {
        int res = this.priority - o.priority;
        if (res == 0)
            res = this.doClass.getName().compareTo(o.doClass.getName());
        return res;
    }
}
