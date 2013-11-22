/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.auth;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 10.09.13
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
public class LoginFilter implements Filter {
    private static final boolean debug = true;
    private FilterConfig filterConfig = null;



    public LoginFilter() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String url = req.getRequestURL().toString();
        LoginBean login = RuntimeContext.getAppContext().getBean(LoginBean.class);


        //  Если это страница авторизации и форма закоммичена,
        //  то устанавливаем введенные значения и проводим авторизацию
        if (url.contains("login.jsf")) {
            String doLogin = req.getParameter("doLogin");
            if (doLogin != null && doLogin.equals("1")) {
                String username = req.getParameter("username");
                String password = req.getParameter("password");
                login.setUsername(username);
                login.setPassword(password);
                login.doLogin();
            }
        }


        //  Проверяем авторизацию и URL запроса, делаем соответствующие перенаправления
        if (login == null || !login.isLoggedIn()) {
            if (url.contains("index.jsf")) {
                resp.sendRedirect("login.jsf");
                return;
            }
        } else if (url.contains("login.jsf")) {
            resp.sendRedirect("index.jsf");
            return;
        }

        try {
            chain.doFilter(request, response);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig) {
    }
}
