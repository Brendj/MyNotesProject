/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 12.07.12
 * Time: 1:12
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("prototype")
public class DefaultDistributedObjectProcessor extends AbstractDistributedObjectProcessor {



    private Logger logger = LoggerFactory.getLogger(DefaultDistributedObjectProcessor.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(DistributedObject distributedObject, long currentMaxVersion, Long idOfOrg, Document document) {
        try {
            processDistributedObject(distributedObject, currentMaxVersion, idOfOrg, document);
        } catch (Exception e) {
            // Произошла ошибка при обрабоке одного объекта - нужно как то сообщить об этом пользователю
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
}
