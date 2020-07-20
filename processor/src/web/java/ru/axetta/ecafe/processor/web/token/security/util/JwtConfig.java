/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.util;

import ru.axetta.ecafe.processor.core.RuntimeContext;


public final class JwtConfig {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static String getSecretKey(){
        return RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.schoolapi.JwtAuth.secretKey", null);
    }

    public static Integer getExpirationTime(){
        return RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.schoolapi.JwtAuth.expiration_time", 0);
    }

    public static Integer getExpirationLongTime(){
        return RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.schoolapi.JwtAuth.expiration_long_time",0);
    }
}
