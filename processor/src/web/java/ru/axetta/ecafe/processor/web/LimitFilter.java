/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import org.springframework.beans.factory.annotation.Autowired;
import ru.axetta.ecafe.processor.config.LimitFilterParams;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.utils.SyncCollector;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.07.13
 * Time: 10:42
 */
public class LimitFilter implements Filter {

    public static final int SC_TOO_MANY_REQUESTS = 429;
    private final String SC_TOO_MANY_REQUESTS_MESSAGE = "Too Many Requests";
    private int count;
    private Object lock = new Object();

    public static LimitFilterParams getLimitFilterParams() {
        return (LimitFilterParams)RuntimeContext.getAppContext().getBean("limitFilterParams");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            boolean ok;
            synchronized (lock) {
                ok = count++ < getLimitFilterParams().getSyncLimit();
            }
            if (ok) {
                // let the request through and process as usual
                chain.doFilter(request, response);
            } else {
                Long syncTime = new Date().getTime();
                SyncCollector.registerSyncStart(syncTime);

                String remoteAddress = request.getRemoteAddr();
                String errorMsg =
                        ((Integer) SC_TOO_MANY_REQUESTS).toString() + ": " + SC_TOO_MANY_REQUESTS_MESSAGE + ": "
                                + "Retry-After - " + String.valueOf(getLimitFilterParams().getSyncRetryAfter()) + ", Remote address : "
                                + remoteAddress;

                SyncCollector.setErrMessage(syncTime, errorMsg);
                SyncCollector.registerSyncEnd(syncTime);

                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setContentType("text/html");
                httpServletResponse.addHeader("Retry-After", String.valueOf(getLimitFilterParams().getSyncRetryAfter()));
                httpServletResponse.sendError(SC_TOO_MANY_REQUESTS, SC_TOO_MANY_REQUESTS_MESSAGE);
            }
        } finally {
            synchronized (lock) {
                count--;
            }
        }
    }
}
