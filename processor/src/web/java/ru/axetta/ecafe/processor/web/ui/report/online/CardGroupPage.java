/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Almaz
 * Date: 29.12.16
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class CardGroupPage extends BasicWorkspacePage {

    public boolean getEligibleToWorkCard() throws Exception {
        return true;
    }

}
