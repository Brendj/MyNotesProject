package ru.axetta.ecafe.processor.web.token.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationErrorResponse;
import ru.axetta.ecafe.processor.web.token.security.util.JwtAuthenticationException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final JWTAuthenticationManager authenticationManager;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, JWTAuthenticationManager authenticationManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        try {
            if (StringUtils.isNotEmpty(token) && jwtTokenProvider.validateToken(token)) {
                JWTAuthentication authentication = jwtTokenProvider.getAuthentication(token);
                authenticationManager.authenticate(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            WebApplicationErrorResponse apiError =
                    new WebApplicationErrorResponse(e.getCode().toString(), HttpServletResponse.SC_UNAUTHORIZED,
                                                    e.getMessage(), ExceptionUtils.getStackTrace(e),
                                                    servletRequest.getRequestURI());
            servletResponse.setContentType("application/json;charset=UTF-8");
            servletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(servletResponse.getOutputStream(), apiError);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String servletPath = request.getServletPath();
        if (servletPath != null && !servletPath.toLowerCase().startsWith("/school/api")) {
            /*
            * используем данный фильтр (SecurityContextHolder.getContext().setAuthentication(authentication);)
            * только для адресов school/api
            * */
            return true;
        }
        return super.shouldNotFilter(request);
    }
}

