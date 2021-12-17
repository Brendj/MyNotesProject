/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.Interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Component
public class RestApiInterceptor extends HandlerInterceptorAdapter {
    private final Logger log = LoggerFactory.getLogger(RestApiInterceptor.class);

    @Value(value = "${spring.rest.apikey}")
    private String currentApiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if(request.getRequestURI().contains("swagger") || request.getRequestURI().contains("api-docs")){
            return true;
        }

        log.info("[preHandle][" + request + "]" + "[" + request.getMethod() + "]" + request.getRequestURI()
                + getParameters(request));

        String apiKey = request.getHeader("API-KEY");
        if(!currentApiKey.equals(apiKey)){
            throw new IllegalArgumentException("Invalid API-KEY:"  + apiKey);
        }
        return true;
    }

    private String getParameters(HttpServletRequest request) {
        StringBuilder posted = new StringBuilder();
        Enumeration<String> e = request.getParameterNames();
        if (e != null) {
            posted.append("?");
        }
        while (e.hasMoreElements()) {
            if (posted.length() > 1) {
                posted.append("&");
            }
            String curr = e.nextElement();
            posted.append(curr).append("=");
            posted.append(request.getParameter(curr));
        }
        String ip = request.getHeader("X-FORWARDED-FOR");
        String ipAddr = (ip == null) ? getRemoteAddr(request) : ip;
        if (ipAddr!=null && !ipAddr.equals("")) {
            posted.append("&_ip=").append(ipAddr);
        }
        return posted.toString();
    }

    private String getRemoteAddr(HttpServletRequest request) {
        String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
        if (ipFromHeader != null && ipFromHeader.length() > 0) {
            return ipFromHeader;
        }
        return request.getRemoteAddr();
    }
}
