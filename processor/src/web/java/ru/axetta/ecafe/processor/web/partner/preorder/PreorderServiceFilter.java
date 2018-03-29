/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by i.semenov on 06.03.2018.
 */
public class PreorderServiceFilter implements Filter {

    public void doFilter ( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException,
            ServletException {
        chain.doFilter(request, response);
        //Защита на основе cookie выключена
        /*if (!RuntimeContext.getAppContext().getBean(SudirClientService.class).SECURITY_ON) {
            chain.doFilter(request, response);
            return;
        }
        boolean tokenFound = false;
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        if (((HttpServletRequest) request).getPathInfo().contains("login") || ((HttpServletRequest) request).getPathInfo().contains("clientsummary")) {
            chain.doFilter(request, response);
            return;
        }
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access_token")) {
                    tokenFound = true;
                    RuntimeContext.getAppContext().getBean(TokenService.class).setToken(cookie.getValue());
                    break;
                }
            }
        }
        if (!tokenFound) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.sendRedirect("/processor/preorder/login");
            return;
        }
        chain.doFilter(request, response);*/
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
