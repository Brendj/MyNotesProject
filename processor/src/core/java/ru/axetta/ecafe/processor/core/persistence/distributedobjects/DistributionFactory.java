/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.06.12
 * Time: 10:52
 * To change this template use File | Settings | File Templates.
 */
public class DistributionFactory {

    public static DistributedObject createDistributedObject(DistributedObjectsEnum distributedObjectsEnum){
        DistributedObject distributedObject=null;
        switch (distributedObjectsEnum){
            case ProductGuide: distributedObject = new ProductGuide(); break;
            case TechnologicalMap: distributedObject = new TechnologicalMap(); break;
        }
        return distributedObject;
    }

    public static List<DistributedObject> findDistributedObjectByVersion(Long version){
        List<DistributedObject> distributedObjectList = new LinkedList<DistributedObject>();
        for (DistributedObjectsEnum distributedObjectsEnum: DistributedObjectsEnum.values()){
            String className = distributedObjectsEnum.name();
        }
        return distributedObjectList;
    }
}
