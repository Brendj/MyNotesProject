/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.07.13
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
public class LimitFilter implements Filter {
    private final int limit = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_REQUEST_SYNC_LIMITS);
    private final int retry_after = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_REQUEST_SYNC_RETRY_AFTER);
    private int count;
    private Object lock = new Object();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        try {
            boolean ok;
            synchronized (lock) {
                ok = count++ < limit;
            }
            if (ok) {
                // let the request through and process as usual
                chain.doFilter(request, response);
            } else {
                HttpServletResponse httpServletResponse = (HttpServletResponse)response;
                httpServletResponse.setContentType("text/html");
                httpServletResponse.addHeader("Retry-After", String.valueOf(retry_after));
                httpServletResponse.sendError(429, "Too Many Requests");
            }
        } finally {
            synchronized (lock) {
                count--;
            }
        }
    }
}
