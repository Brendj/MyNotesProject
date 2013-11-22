/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

import org.w3c.dom.Document;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 07.11.12
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public interface IntegroLogger {

    void registerIntegroRequest(Document requestDocument, long idOfOrg, String idOfSync);
    void registerIntegroResponse(Document responseDocument, long idOfOrg, String idOfSync);

}
