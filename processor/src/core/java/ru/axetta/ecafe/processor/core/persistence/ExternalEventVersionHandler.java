/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;

/**
 * Created by i.semenov on 01.08.2017.
 */
public class ExternalEventVersionHandler implements ISetExternalEventVersion {
    private final Session session;

    public ExternalEventVersionHandler(Session session) {
        this.session = session;
    }

    @Override
    public long getVersion() {
        return DAOUtils.nextVersionByExternalEvent(session);
    }
}
