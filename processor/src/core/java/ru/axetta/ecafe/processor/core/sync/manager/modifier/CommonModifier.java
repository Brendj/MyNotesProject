/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager.modifier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConflict;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;

import org.hibernate.Session;
import org.w3c.dom.Document;

import java.util.Date;

/**
 * Created by i.semenov on 29.10.2019.
 */
public class CommonModifier extends DistributedObjectModifier {

    public void modifyDO(Session persistenceSession, DistributedObject distributedObject,
            Long currentMaxVersion, DistributedObject currentDO, Long idOfOrg, Document conflictDocument) throws Exception {
        Long currentVersion = currentDO.getGlobalVersion();
        Long objectVersion = distributedObject.getGlobalVersion();
        currentDO.fill(distributedObject);
        currentDO.setDeletedState(distributedObject.getDeletedState());
        currentDO.setLastUpdate(new Date());
        currentDO.setGlobalVersion(currentMaxVersion);
        DOConflict doConflict = null;
        // Проверка на наличие конфликта версионности.
        if (objectVersion != null && currentVersion != null && !objectVersion.equals(currentVersion)) {
            doConflict = createConflict(distributedObject, currentDO, idOfOrg, conflictDocument);
            persistenceSession.persist(doConflict);
        }
        currentDO.setTagName("M");
        currentDO.preProcess(persistenceSession, idOfOrg);
        currentDO.updateVersionFromParent(persistenceSession);
        persistenceSession.update(currentDO);
        distributedObject.setGlobalVersion(currentMaxVersion);
        distributedObject.setTagName("M");
    }
}
