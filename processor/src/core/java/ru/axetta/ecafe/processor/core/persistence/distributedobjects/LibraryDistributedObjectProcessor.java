/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.w3c.dom.Document;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 12.07.12
 * Time: 1:13
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("prototype")
public class LibraryDistributedObjectProcessor extends AbstractDistributedObjectProcessor {

    private Logger logger = LoggerFactory.getLogger(LibraryDistributedObjectProcessor.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(DistributedObject distributedObject, long currentMaxVersion, Long idOfOrg, Document document) {
        try {
            super.processDistributedObject(distributedObject, currentMaxVersion, idOfOrg, document);
        } catch (Exception e) {
            // Произошла ошибка при обрабоке одного объекта - нужна как то сообщить об этом пользователю
            ErrorObject errorObject = new ErrorObject();
            errorObject.setClazz(distributedObject.getClass());
            errorObject.setGuid(distributedObject.getGuid());
            errorObject.setMessage(e.getMessage());
            if (e instanceof DistributedObjectException) {
                errorObject.setType(((DistributedObjectException) e).getType());
            }
            DistributedObjectsEnumComparator.getErrorObjectList().add(errorObject);
            logger.error(errorObject.toString(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Override
    protected DistributedObject createDistributedObject(DistributedObject distributedObject, long currentVersion)
            throws DistributedObjectException {
        
        long id = getGlobalIDByGUID(distributedObject);
        if (id > 0) {
            throw new DistributedObjectException(1);
        }
        distributedObject.setCreatedDate(new Date());
        distributedObject.setGlobalVersion(currentVersion);
        Circulation2 circulation = (Circulation2) distributedObject;
        
        Client client = entityManager.find(Client.class, circulation.getIdofclient());
        Org org = entityManager.find(Org.class, circulation.getIdoforg());
        Publication2 publication = entityManager.find(Publication2.class, circulation.getIdofpubl());

        circulation.setClient(client);
        circulation.setOrg(org);
        circulation.setPublication(publication);
        return entityManager.merge(distributedObject);
    }
}
