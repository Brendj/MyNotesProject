/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.w3c.dom.Document;
/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 12.07.12
 * Time: 1:07
 * To change this template use File | Settings | File Templates.
 */
public interface IDistributedObjectProcessor {

    public void process(DistributedObject distributedObject, long currentMaxVersion, Long idOfOrg, Document document);

}
