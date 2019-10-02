/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.exceptions;

import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.ProcessResultEnum;

public class AmbiguityConcreteTimeAndEverySecondsValues extends SyncProcessException {
    private static final String MESSAGE = "Ambiguous values of variables ConcreteTime and EverySeconds";

    public AmbiguityConcreteTimeAndEverySecondsValues(){
        super(MESSAGE);
    }

    @Override
    public ProcessResultEnum getExceptionRes() {
        return ProcessResultEnum.AMBIGUITY_IN_CONCRETE_TIME_AND_EVERY_SECOND;
    }
}
