/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.persistence.User;

/**
 * Created by i.semenov on 14.08.2018.
 */
public interface IAuthorizeUserBySms {
    String sendCodeAndGetError(User user, String code);
}
