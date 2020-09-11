/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.service;

import org.hibernate.Session;

public interface JWTLoginService {
    boolean login(String username, String password, String remoteAddr, Session persistenceSession) throws Exception;

}
