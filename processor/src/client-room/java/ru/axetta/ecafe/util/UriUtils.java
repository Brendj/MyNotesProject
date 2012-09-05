/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Утилиты для сервлетов
 */
public final class UriUtils {
    private static final Logger logger = LoggerFactory.getLogger(UriUtils.class);
    private static final String URL_CHARSET = "UTF-8";

    private UriUtils() {
        // Not instantiable
    }

    public static URI getURIWithNoParams(URI uri) throws Exception {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getPath();
        String fragment = uri.getFragment();
        return new URI(scheme, null, host, port, path, null, fragment);
    }

    public static URI putParam(URI uri, String paramName, String paramValue) throws Exception {
        logger.info("begin putParam");
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getPath();
        String rawQuery = uri.getRawQuery();
        Map<String, String> params = parseQuery(rawQuery);
        params.put(paramName, paramValue);
        String fragment = uri.getFragment();
        logger.info("end putParam");
        return new URI(scheme, null, host, port, path, buildRawQuery(params), fragment);
    }

    public static URI removeParam(URI uri, String paramName) throws Exception {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getPath();
        String rawQuery = uri.getRawQuery();
        Map<String, String> params = parseQuery(rawQuery);
        params.remove(paramName);
        String query = buildRawQuery(params);
        if (StringUtils.isEmpty(query)) {
            query = null;
        }
        String fragment = uri.getFragment();
        return new URI(scheme, null, host, port, path, query, fragment);
    }

    public static URI removeParams(URI uri, List<String> paramNames) throws Exception {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getPath();
        String rawQuery = uri.getRawQuery();
        Map<String, String> uriParams = parseQuery(rawQuery);
        for (String currParamName : paramNames) {
            uriParams.remove(currParamName);
        }
        String query = buildRawQuery(uriParams);
        if (StringUtils.isEmpty(query)) {
            query = null;
        }
        String fragment = uri.getFragment();
        return new URI(scheme, null, host, port, path, query, fragment);
    }

    private static Map<String, String> parseQuery(String rawQuery) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        if (StringUtils.isNotEmpty(rawQuery)) {
            StringTokenizer paireTokenizer = new StringTokenizer(rawQuery, "&");
            while (paireTokenizer.hasMoreTokens()) {
                String paramPaire = paireTokenizer.nextToken();
                StringTokenizer valueTokenizer = new StringTokenizer(paramPaire, "=");
                if (valueTokenizer.hasMoreTokens()) {
                    String key = URLDecoder.decode(valueTokenizer.nextToken(), URL_CHARSET);
                    String value = "";
                    if (valueTokenizer.hasMoreElements()) {
                        value = valueTokenizer.nextToken();
                    }
                    if (StringUtils.isNotEmpty(value)) {
                        value = URLDecoder.decode(value, URL_CHARSET);
                    }
                    params.put(key, value);
                }
            }
        }
        return params;
    }

    private static String buildRawQuery(Map<String, String> params) throws Exception {
        StringBuilder stringBuilder = new StringBuilder(params.entrySet().size() * 8);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            stringBuilder.append(URLEncoder.encode(currParam.getKey(), URL_CHARSET)).append("=");
            stringBuilder.append(URLEncoder.encode(currParam.getValue(), URL_CHARSET)).append("&");
        }
        int lastCharEnum = stringBuilder.length();
        if (lastCharEnum > 0) {
            stringBuilder.deleteCharAt(lastCharEnum - 1);
        }
        return stringBuilder.toString();
    }

}
