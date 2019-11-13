/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager.modifier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConflict;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;

/**
 * Created by i.semenov on 29.10.2019.
 */
public abstract class DistributedObjectModifier {

    public abstract void modifyDO(Session persistenceSession, DistributedObject distributedObject,
            Long currentMaxVersion, DistributedObject currentDO, Long idOfOrg, Document conflictDocument) throws Exception;

    protected DOConflict createConflict(DistributedObject distributedObject, DistributedObject currentDO, Long idOfOrg, Document conflictDocument)
            throws Exception {
        DOConflict conflict = new DOConflict();
        conflict.setValueInc(createStringElement(conflictDocument, distributedObject));
        conflict.setgVersionInc(distributedObject.getGlobalVersion());
        conflict.setIdOfOrg(idOfOrg);
        conflict.setDistributedObjectClassName(distributedObject.getClass().getSimpleName());
        conflict.setgVersionCur(currentDO.getGlobalVersion());
        conflict.setgVersionResult(currentDO.getGlobalVersion());
        conflict.setValueCur(createStringElement(conflictDocument, currentDO));
        return conflict;
    }

    private String createStringElement(Document document, DistributedObject distributedObject)
            throws TransformerException {
        Element element = document.createElement("O");
        element = distributedObject.toElement(element);
        return XMLUtils.nodeToString(element);
    }
}
