/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.exceptions;

import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.ProcessResultEnum;

public class ContentTypeIsNullException extends SyncProcessException {
    private static final String MESSAGE = "ContentType is NULL";

    public ContentTypeIsNullException(){
        super(MESSAGE);
    }

    @Override
    public ProcessResultEnum getExceptionRes() {
        return ProcessResultEnum.CONTENT_TYPE_IS_NULL;
    }
}
