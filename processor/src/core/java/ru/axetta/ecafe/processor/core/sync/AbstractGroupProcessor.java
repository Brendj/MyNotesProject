/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import org.hibernate.SessionFactory;

/**
 * User: akmukov
 * Date: 05.04.2016
 */
public abstract class AbstractGroupProcessor<RES extends AbstractToElement> {
    protected final SessionFactory sessionFactory;

    public AbstractGroupProcessor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public abstract <RES> RES process() throws Exception;


}
