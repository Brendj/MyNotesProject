/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.*;
import java.io.IOException;

public class ResteasyCleanupFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        request.getServletContext().setAttribute(ResteasyProviderFactory.class.getName(), null);
        request.getServletContext().setAttribute(Dispatcher.class.getName(), null);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
