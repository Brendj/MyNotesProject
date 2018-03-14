/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by i.semenov on 06.03.2018.
 */
public class PreorderServiceFilter implements Filter {

    public void doFilter ( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException,
            ServletException {
        //HttpServletRequest httpRequest = (HttpServletRequest) request;
        boolean tokenFound = false;
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("access_token")) {
                tokenFound = true;
            }
        }
        if (!tokenFound) {
            //redirect to sudir login page
        }
        /*String access_token = httpRequest.getHeader("access_token");
        if (access_token != null) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("access_token", access_token);
        }*/
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
