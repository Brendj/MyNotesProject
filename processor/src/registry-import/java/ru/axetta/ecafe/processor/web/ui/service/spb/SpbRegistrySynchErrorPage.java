/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.spb;

import generated.registry.manual_synch.FrontController;

import ru.axetta.ecafe.processor.web.ui.service.msk.NSIOrgRegistrySynchErrorPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */
@Component("SpbRegistrySynchErrorPage")
@Scope("session")
public class SpbRegistrySynchErrorPage extends NSIOrgRegistrySynchErrorPage {
    Logger logger = LoggerFactory.getLogger(SpbRegistrySynchErrorPage.class);

    protected FrontController getController() {
        return SpbRegistrySynchPageBase.createController(logger);
    }

}
