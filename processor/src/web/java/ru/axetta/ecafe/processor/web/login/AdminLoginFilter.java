/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.login;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Akhmetov
 * Date: 25.04.16
 * Time: 16:37
 * To change this template use File | Settings | File Templates.
 */
public class AdminLoginFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setAttribute("ru.axetta.ecafe.userRole", "admin");
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/back-office/login.faces");
        requestDispatcher.forward(request, response);
    }

    @Override
    public void destroy() {}
}
