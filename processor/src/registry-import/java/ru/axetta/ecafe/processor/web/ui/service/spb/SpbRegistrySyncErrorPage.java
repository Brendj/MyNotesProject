/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.spb;

import ru.axetta.ecafe.processor.web.ui.service.msk.NSIOrgRegistrySyncErrorPage;

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
@Component("SpbRegistrySyncErrorPage")
@Scope("session")
public class SpbRegistrySyncErrorPage extends NSIOrgRegistrySyncErrorPage {
    Logger logger = LoggerFactory.getLogger(SpbRegistrySyncErrorPage.class);
}
