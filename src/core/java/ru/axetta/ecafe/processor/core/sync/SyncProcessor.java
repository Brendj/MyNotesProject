package ru.axetta.ecafe.processor.core.sync;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 28.07.2009
 * Time: 10:33:22
 * To change this template use File | Settings | File Templates.
 */
public interface SyncProcessor {

    SyncResponse processSyncRequest(SyncRequest request) throws Exception;
}
