/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.auth.LoginBean;
import ru.axetta.ecafe.processor.web.ui.service.msk.NSIOrgRegistrySyncPageBase;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 07.10.13
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class NSIOrgRegistrySyncPage extends NSIOrgRegistrySyncPageBase {
    @Override
    public long getIdOfOrg() {
        return RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg().getIdOfOrg();
    }

    @Override
    public boolean getShowErrorEditPanel () {
        return false;
    }
}
