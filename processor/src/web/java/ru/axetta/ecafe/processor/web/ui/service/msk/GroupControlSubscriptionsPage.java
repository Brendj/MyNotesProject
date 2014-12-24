/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 22.12.14
 * Time: 13:38
 */

@Component
@Scope("session")
public class GroupControlSubscriptionsPage extends BasicWorkspacePage {

    @Override
    public String getPageFilename() {
        return "service/msk/group_control_subscription";
    }
}

