/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

/**
 * Утилиты для сервлетов
 */
public final class ServletUtils {

    private static final String URL_PATH_SEPARATOR = "/";

    private ServletUtils() {
        // Not instantiable
    }

    public static URI getHostRelativeUriWithQuery(HttpServletRequest request) throws Exception {
        URI hostRelative = new URI(request.getRequestURL().toString());
        String path = hostRelative.getPath();
        return new URI(null, null, null, -1, path, request.getQueryString(), null);
    }

    public static URI getFullSpecifiedUriWithQuery(HttpServletRequest request) throws Exception {
        URI src = new URI(request.getRequestURL().toString());
        return new URI(src.getScheme(), src.getUserInfo(), src.getHost(), src.getPort(), src.getPath(),
                request.getQueryString(), null);
    }

    public static String getHostRelativeResourceUri(HttpServletRequest request, String appContextRelativeResourcePath) {
        String contextPath = request.getContextPath();
        StringBuilder stringBuilder = new StringBuilder(contextPath);
        if (!StringUtils.endsWith(contextPath, URL_PATH_SEPARATOR)) {
            stringBuilder.append(URL_PATH_SEPARATOR);
        }
        stringBuilder.append(appContextRelativeResourcePath);
        return stringBuilder.toString();
    }

    public static String getHostRelativeResourceUri(HttpServletRequest request, String defaultContext,
            String appContextRelativeResourcePath) {
        String contextPath = request.getContextPath();
        if (StringUtils.isEmpty(contextPath)) {
            contextPath = defaultContext;
        }
        StringBuilder stringBuilder = new StringBuilder(contextPath);
        if (!StringUtils.endsWith(contextPath, URL_PATH_SEPARATOR)) {
            stringBuilder.append(URL_PATH_SEPARATOR);
        }
        stringBuilder.append(appContextRelativeResourcePath);
        return stringBuilder.toString();
    }

}