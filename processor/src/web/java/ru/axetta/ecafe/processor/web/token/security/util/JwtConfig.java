/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.util;

import ru.axetta.ecafe.processor.core.RuntimeContext;


public final class JwtConfig {
    public static final String REFRESH_TOKEN_KEY = "zW5bJg8mFh";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static String getSecretKey(){
        return RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.schoolapi.JwtAuth.secretKey", null);
    }

    public static Long getExpirationTime(){
        return new Long(RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.schoolapi.JwtAuth.expiration_time", 0) * 1000);
    }

    public static Long getExpirationLongTime(){
        return new Long(RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.schoolapi.JwtAuth.expiration_long_time",0) * 1000);
    }
}
