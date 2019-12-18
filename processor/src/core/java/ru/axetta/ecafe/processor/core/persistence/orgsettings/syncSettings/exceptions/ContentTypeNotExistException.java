/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.exceptions;

import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.ProcessResultEnum;

public class ContentTypeNotExistException extends SyncProcessException {
    private static final String MESSAGE = "ContentType is not exist";

    public ContentTypeNotExistException(){
        super(MESSAGE);
    }

    @Override
    public ProcessResultEnum getExceptionRes() {
        return ProcessResultEnum.CONTENT_TYPE_NOT_EXIST;
    }
}
