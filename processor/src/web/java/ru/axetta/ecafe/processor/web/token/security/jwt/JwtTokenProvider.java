/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.jwt;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsImpl;
import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsService;
import ru.axetta.ecafe.processor.web.token.security.util.*;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("singleton")
public class JwtTokenProvider {

    public String getToken(String username) throws Exception {
        if (username == null)
            throw new Exception("Authentication error");
        Map<String, Object> tokenData = new HashMap<>();
        UserDetailsService userDetailsService = RuntimeContext.getAppContext().getBean(JwtUserDetailsService.class);
        JwtUserDetailsImpl user = (JwtUserDetailsImpl) userDetailsService.loadUserByUsername(username);
        tokenData.put(JwtClaimsConstant.CLIENT_TYPE, "user");
        tokenData.put(JwtClaimsConstant.AUTHORITIES, user.getAuthorities());
        tokenData.put(JwtClaimsConstant.ID_OF_USER, user.getIdOfUser());
        tokenData.put(JwtClaimsConstant.USERNAME, user.getUsername());
        tokenData.put(JwtClaimsConstant.USER_IS_ENABLED, user.isEnabled());
        tokenData.put(JwtClaimsConstant.SURNAME, user.getSurname());
        tokenData.put(JwtClaimsConstant.FIRSTNAME, user.getFirstname());
        tokenData.put(JwtClaimsConstant.SECONDNAME, user.getSecondname());
        tokenData.put(JwtClaimsConstant.ID_OF_ORG, user.getIdOfOrg());
        tokenData.put(JwtClaimsConstant.SHORT_ORG_NAME, user.getShortOrgName());
        tokenData.put(JwtClaimsConstant.CONTRACT_ID, user.getContractId());
        tokenData.put(JwtClaimsConstant.ID_OF_ROLE, user.getIdOfRole());
        tokenData.put(JwtClaimsConstant.ROLE_NAME, user.getRoleName());
        tokenData.put(JwtClaimsConstant.TOKEN_CREATE_DATE, new Date().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, JwtConfig.getExpirationTime());
        Date tokenExpiration = calendar.getTime();
        tokenData.put(JwtClaimsConstant.TOKEN_EXPIRATION_DATE, tokenExpiration);
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setExpiration(tokenExpiration);
        jwtBuilder.setHeaderParam(JwtClaimsConstant.TOKEN_TYPE, "JWT");
        jwtBuilder.setClaims(tokenData);
        String token = jwtBuilder.signWith(SignatureAlgorithm.HS512, JwtConfig.getSecretKey()).compact();
        return token;
    }

    public String getRefreshToken(){
        return null;
    }

    public Jwt validateToken(String token) throws AuthenticationException{
        Jwt jwt;
        DefaultClaims claims;
        try{
            jwt = Jwts.parser().setSigningKey(JwtConfig.getSecretKey()).parse(token);
            claims = (DefaultClaims) jwt.getBody();
        }
        catch (Exception ex){
            throw new JwtAuthenticationException(new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.TOKEN_CORRUPTED.getErrorCode(),
                    JwtAuthenticationErrors.TOKEN_CORRUPTED.getErrorMessage()));
        }

        if (claims.get(JwtClaimsConstant.TOKEN_EXPIRATION_DATE) == null)
            throw new JwtAuthenticationException(new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.TOKEN_INVALID.getErrorCode(),
                    JwtAuthenticationErrors.TOKEN_INVALID.getErrorMessage()));
        Long expiredDate = (Long) claims.get(JwtClaimsConstant.TOKEN_EXPIRATION_DATE);
        if (new Date().getTime() > expiredDate)
            throw new JwtAuthenticationException(new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.TOKEN_EXPIRED.getErrorCode(),
                    JwtAuthenticationErrors.TOKEN_EXPIRED.getErrorMessage()));
        else
            return jwt;
    }

    public UserDetails getUserDetailsFromToken(DefaultClaims claims) throws AuthenticationException {
        try {
            Collection<GrantedAuthority> grantedAuthorities = (Collection<GrantedAuthority>) claims.get(JwtClaimsConstant.AUTHORITIES);
            Long idOfUser = (Long) claims.get(JwtClaimsConstant.ID_OF_USER);
            String username = (String) claims.get(JwtClaimsConstant.USERNAME);
            Boolean isEnabled = (Boolean) claims.get(JwtClaimsConstant.USER_IS_ENABLED);
            String surname = (String) claims.get(JwtClaimsConstant.SURNAME);
            String firstname = (String) claims.get(JwtClaimsConstant.FIRSTNAME);
            String secondname = (String) claims.get(JwtClaimsConstant.SECONDNAME);
            Long idOfOrg = (Long) claims.get(JwtClaimsConstant.ID_OF_ORG);
            String shortOrgName = (String) claims.get(JwtClaimsConstant.SHORT_ORG_NAME);
            Long contractId = (Long) claims.get(JwtClaimsConstant.CONTRACT_ID);
            Integer idOfRole = (Integer) claims.get(JwtClaimsConstant.ID_OF_ROLE);
            String roleName = (String) claims.get(JwtClaimsConstant.ROLE_NAME);
            if(username == null || isEnabled == null || idOfRole == null || contractId == null || idOfOrg == null || idOfUser == null)
                throw  new JwtAuthenticationException(new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.TOKEN_INVALID.getErrorCode(),
                        JwtAuthenticationErrors.TOKEN_INVALID.getErrorMessage()));
            JwtUserDetailsImpl jwtUserDetails = new JwtUserDetailsImpl(grantedAuthorities, isEnabled, isEnabled, username,
                    surname, firstname, secondname, idOfUser, idOfRole, roleName, contractId, idOfOrg, shortOrgName,
                    null, null);
            return jwtUserDetails;
        }
        catch (Exception ex){
            throw  new JwtAuthenticationException(new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.TOKEN_INVALID.getErrorCode(),
                    JwtAuthenticationErrors.TOKEN_INVALID.getErrorMessage()));
        }
    }


}

