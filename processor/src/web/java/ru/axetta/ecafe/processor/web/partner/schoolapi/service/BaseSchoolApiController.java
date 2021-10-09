/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.partner.schoolapi.util.AuthorityUtils;

import javax.ws.rs.core.Application;

public class BaseSchoolApiController extends Application {
    protected User getUser() {
        AuthorityUtils authorityUtils = RuntimeContext.getAppContext().getBean(AuthorityUtils.class);
        return authorityUtils.findCurrentUser();
    }

    protected boolean hasAnyRole(String... role) {
        AuthorityUtils authorityUtils = RuntimeContext.getAppContext().getBean(AuthorityUtils.class);
        return authorityUtils.hasAnyRole(role);
    }

    protected boolean isWebArmAnyRole() {
        AuthorityUtils authorityUtils = RuntimeContext.getAppContext().getBean(AuthorityUtils.class);
        return authorityUtils.hasAnyRole(User.WebArmRole.WA_OEE.name(), User.WebArmRole.WA_OPP.name(), User.WebArmRole.WA_OPP_OEE
                .name());
    }
}
