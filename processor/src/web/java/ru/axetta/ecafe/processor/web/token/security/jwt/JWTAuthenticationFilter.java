/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.jwt;

import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationErrorResponse;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrorDTO;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationErrors;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;
import ru.axetta.ecafe.processor.web.token.security.util.JwtConfig;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
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

    public JWTAuthenticationFilter() {
        super("/school/api/v1/");
        setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.getRequestDispatcher(request.getServletPath() + request.getPathInfo())
                        .forward(request, response);
            }
        });
        setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                    AuthenticationException e) throws IOException, ServletException {

                String type = "401";
                if (e instanceof JwtAuthenticationException) {
                    type = ((JwtAuthenticationException) e).getCode().toString();
                }
                WebApplicationErrorResponse apiError = new WebApplicationErrorResponse(type,
                        HttpServletResponse.SC_UNAUTHORIZED, e.getMessage(),
                        ExceptionUtils.getStackTrace(e), request.getRequestURI());

                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(response.getOutputStream(), apiError);
            }
        });
        super.setAuthenticationManager(new JWTAuthenticationManager());
        afterPropertiesSet();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String token = request.getHeader(JwtConfig.TOKEN_HEADER);
        if (token == null) {
            throw new JwtAuthenticationException(
                    new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.TOKEN_INVALID.getErrorCode(),
                            JwtAuthenticationErrors.TOKEN_INVALID.getErrorMessage()));
        }
        JWTAuthentication JWTAuthentication = new JWTAuthentication(token);
        Authentication authentication = getAuthenticationManager().authenticate(JWTAuthentication);
        return authentication;
    }

    //TODO разобраться с getFilterProcessesUrl
    private String getFilterProcessesUrl() {
        return getServletContext().getContextPath();
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        if ("".equals(request.getContextPath()) ? uri.startsWith(getFilterProcessesUrl() + "authorization")
                : uri.startsWith(request.getContextPath() + getFilterProcessesUrl() + "authorization")) {
            return false;
        }
        if (uri.contains("payments/")){
            return false;
        }
        return "".equals(request.getContextPath()) ? uri.startsWith(getFilterProcessesUrl())
                : uri.startsWith(request.getContextPath() + getFilterProcessesUrl());
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        super.doFilter(req, res, chain);
    }


}


