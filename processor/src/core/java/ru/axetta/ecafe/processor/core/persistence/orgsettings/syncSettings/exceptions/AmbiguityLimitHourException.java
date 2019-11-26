/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.exceptions;

import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.ProcessResultEnum;

public class AmbiguityLimitHourException extends SyncProcessException {
    private static final String MESSAGE = "Ambiguous values of variables LimitStartHour and LimitEndHour";

    public AmbiguityLimitHourException(){
        super(MESSAGE);
    }

    @Override
    public ProcessResultEnum getExceptionRes() {
        return ProcessResultEnum.AMBIGUITY_IN_START_AND_END_OF_LIMIT_HOUR;
    }
}
