/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.jwt;

import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String AUTHORIZATION_HEADER = "Auhorization";

    public JWTAuthenticationFilter() {
        super("/school/api/v1/");
        setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                    HttpServletResponse response, Authentication authentication)
                    throws IOException, ServletException {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.getRequestDispatcher(request.getServletPath() + request.getPathInfo())
                        .forward(request, response);
            }
        });
        setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request,
                    HttpServletResponse response, AuthenticationException authenticationException)
                    throws IOException, ServletException {
                response.setStatus(401);
                response.setContentType("application/json");
                response.getOutputStream().print(authenticationException.getMessage());
            }
        });
        super.setAuthenticationManager(new JWTAuthenticationManager());
        afterPropertiesSet();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (token == null || !token.startsWith("Bearer ")){
            throw new JwtAuthenticationException("Token invalid");
        }
        JWTAuthentication JWTAuthentication = new JWTAuthentication(token.substring(7));
        Authentication authentication = getAuthenticationManager().authenticate(JWTAuthentication);
        return authentication;
    }

    /*@Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult)
            throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
    }*/

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response){
        String uri = request.getRequestURI();
        if("".equals(request.getContextPath()) ? uri.startsWith(getFilterProcessesUrl()+"authorization")
                : uri.startsWith(request.getContextPath() + getFilterProcessesUrl()+"authorization"))
            return false;
        return "".equals(request.getContextPath()) ? uri.startsWith(getFilterProcessesUrl()) : uri.startsWith(request.getContextPath() + getFilterProcessesUrl());
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        super.doFilter(req,res,chain);
    }
}


