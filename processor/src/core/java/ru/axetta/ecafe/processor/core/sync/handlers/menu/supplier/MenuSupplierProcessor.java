/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 10.02.20
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */

public class MenuSupplierProcessor extends AbstractProcessor<ResMenuSupplier> {

    private static final Logger logger = LoggerFactory.getLogger(MenuSupplierProcessor.class);
    private final MenuSupplier menuSupplier;

    public MenuSupplierProcessor(Session persistenceSession, MenuSupplier menuSupplier) {
        super(persistenceSession);
        this.menuSupplier = menuSupplier;
    }

    @Override
    public ResMenuSupplier process() throws Exception {
        return new ResMenuSupplier(menuSupplier);
    }

    public MenuSupplier getMenuSupplier() {
        return menuSupplier;
    }
}
