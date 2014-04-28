/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.web.ClientAuthToken;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 27.11.13
 * Time: 18:13
 */

public class SubFeedingAuthFilter implements Filter {

    private List<String> operationsList;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        operationsList = Arrays.asList("/activate", "/suspend", "/view", "/reopen", "/logout", "/plan", "/edit",
                "/transfer", "/cancel");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        ClientAuthToken token = ClientAuthToken.loadFrom(req.getSession());
        String path = req.getPathInfo();
        if (token == null) {
            if (operationsList.contains(path)) {
                ((HttpServletResponse) servletResponse)
                        .sendRedirect(String.format("%s%s%s", req.getContextPath(), req.getServletPath(), "/index"));
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } else {
            if ("/index".equals(path) || "/".equals(path)) {
                ((HttpServletResponse) servletResponse)
                        .sendRedirect(String.format("%s%s%s", req.getContextPath(), req.getServletPath(), "/view"));
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }

    @Override
    public void destroy() {
    }
}
