/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 12.07.12
 * Time: 1:04
 * To change this template use File | Settings | File Templates.
 */
public class DistributedObjectsProcessorsFactory {

    private static DistributedObjectsProcessorsFactory instance;
    
    private static ConcurrentHashMap<Class, Class> distributedObjectToProcessorMapping = new ConcurrentHashMap<Class, Class>();
    
    static {
        distributedObjectToProcessorMapping.put(Publication2.class, LibraryDistributedObjectProcessor.class);
        distributedObjectToProcessorMapping.put(Circulation2.class, LibraryDistributedObjectProcessor.class);
    }

    private DistributedObjectsProcessorsFactory() {

    }

    public static synchronized DistributedObjectsProcessorsFactory getInstance() {
        if (instance == null) {
            instance = new DistributedObjectsProcessorsFactory();
        }
        return instance;
    }

    public IDistributedObjectProcessor createProcessor(Class clazz) {
        Class distributedObjectProcessorClass = distributedObjectToProcessorMapping.get(clazz);
        if (distributedObjectProcessorClass != null) {
            return (IDistributedObjectProcessor) RuntimeContext.getAppContext().getBean(distributedObjectProcessorClass);
        } else {
            return (IDistributedObjectProcessor) RuntimeContext.getAppContext().getBean(DefaultDistributedObjectProcessor.class);
        }

    }

}
