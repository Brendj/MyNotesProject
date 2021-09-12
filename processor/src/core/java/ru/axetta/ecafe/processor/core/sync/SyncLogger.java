/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import org.w3c.dom.Document;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 31.07.2009
 * Time: 12:40:14
 * To change this template use File | Settings | File Templates.
 */
public interface SyncLogger {

    void registerSyncRequest(Document requestDocument, long idOfOrg, String idOfSync);

    void registerSyncResponse(Document responseDocument, long idOfOrg, String idOfSync);

    void registerSyncRequestInDb(long idOfOrg, String idOfSync);

    void queueSyncRequestAsync(long idOfOrg, String idOfSync);

    void runRegisterSyncRequestInDb();
}
