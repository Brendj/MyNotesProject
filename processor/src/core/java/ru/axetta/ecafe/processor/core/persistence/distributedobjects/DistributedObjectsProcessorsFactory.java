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
        //distributedObjectToProcessorMapping.put(Publication.class, LibraryDistributedObjectProcessor.class);
        distributedObjectToProcessorMapping.put(Circulation.class, LibraryDistributedObjectProcessor.class);
    }

    private DistributedObjectsProcessorsFactory() {

    }

    public static synchronized DistributedObjectsProcessorsFactory getInstance() {
        if (instance == null) {
            instance = new DistributedObjectsProcessorsFactory();
        }
        return instance;
    }

    public AbstractDistributedObjectProcessor createProcessor(Class clazz) {
        Class distributedObjectProcessorClass = distributedObjectToProcessorMapping.get(clazz);
        if (distributedObjectProcessorClass != null) {
            return (AbstractDistributedObjectProcessor) RuntimeContext.getAppContext().getBean(distributedObjectProcessorClass);
        } else {
            return (AbstractDistributedObjectProcessor) RuntimeContext.getAppContext().getBean(DefaultDistributedObjectProcessor.class);
        }

    }

}
