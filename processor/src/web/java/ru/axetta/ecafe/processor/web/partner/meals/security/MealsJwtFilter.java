package ru.axetta.ecafe.processor.web.partner.meals.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.axetta.ecafe.processor.web.partner.meals.ResponseCodes;
import ru.axetta.ecafe.processor.web.partner.meals.Result;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.util.StringUtils.hasText;

@Component
public class MealsJwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION = "Authorization";
    public static final String MEALS_AUTHORIZATION = "auth-token";

    @Autowired
    private MealsJwtProvider jwtProvider;

    @Override
    public void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = getMealsTokenFromRequest(servletRequest);
        try {
            jwtProvider.validateToken(token);
            String msh = jwtProvider.getMshFromToken(token);
            MealsUserDetails customUserDetails = new MealsUserDetails(msh);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (MealsInvalidToken e) {
            SecurityContextHolder.clearContext();
            servletResponse.setContentType("application/json;charset=UTF-8");
            servletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Result result = new Result();
            result.setErrorCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_KEY.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(servletResponse.getOutputStream(), result);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String servletPath = request.getServletPath();
        if (servletPath != null && !servletPath.toLowerCase().startsWith("/ispp/meals")) {
            return true;
        }
        return super.shouldNotFilter(request);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private String getMealsTokenFromRequest(HttpServletRequest request) {
        return request.getHeader(MEALS_AUTHORIZATION);
    }
}
