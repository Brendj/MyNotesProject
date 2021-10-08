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
import ru.axetta.ecafe.processor.core.persistence.RefreshToken;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;
import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsImpl;
import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsService;
import ru.axetta.ecafe.processor.web.token.security.util.*;
import ru.axetta.ecafe.processor.web.token.security.util.login.JwtLoginErrors;
import ru.axetta.ecafe.processor.web.token.security.util.login.JwtLoginException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("singleton")
public class JwtTokenProvider {

    public String createToken(String username) throws Exception {
        if (username == null)
            throw new Exception("Authentication error");
        Map<String, Object> tokenData = new LinkedHashMap<String, Object>();
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
        calendar.add(Calendar.MILLISECOND, JwtConfig.getExpirationTime().intValue());
        Date tokenExpiration = calendar.getTime();
        tokenData.put(JwtClaimsConstant.TOKEN_EXPIRATION_DATE, tokenExpiration);
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setExpiration(tokenExpiration);
        jwtBuilder.setHeaderParam(JwtClaimsConstant.TOKEN_TYPE, "JWT");
        jwtBuilder.setClaims(tokenData);
        String token = jwtBuilder.signWith(SignatureAlgorithm.HS512, JwtConfig.getSecretKey()).compact();
        return token;
    }

    public RefreshToken refreshTokenIsValid(String refreshToken, String remoteAddress, Session persistenceSession) throws Exception{
        Criteria refreshTokenCriteria = persistenceSession.createCriteria(RefreshToken.class);
        refreshTokenCriteria.add(Restrictions.eq("refreshTokenHash",refreshToken));
        RefreshToken refreshTokenEntity = (RefreshToken) refreshTokenCriteria.uniqueResult();
        if(refreshTokenEntity == null)
            throw new JwtLoginException(JwtLoginErrors.INVALID_REFRESH_TOKEN.getErrorCode(),
                    JwtLoginErrors.INVALID_REFRESH_TOKEN.getErrorMessage());
        if(refreshTokenEntity.getExpiresIn().before(new Date()) || refreshTokenEntity.getUser() == null
                || !refreshTokenEntity.getIpAddress().equals(remoteAddress))
            throw new JwtLoginException(JwtLoginErrors.INVALID_REFRESH_TOKEN.getErrorCode(),
                    JwtLoginErrors.INVALID_REFRESH_TOKEN.getErrorMessage());
        if(refreshTokenEntity.getUser().isBlocked())
            throw new JwtLoginException(JwtLoginErrors.USER_IS_BLOCKED.getErrorCode(),
                    JwtLoginErrors.USER_IS_BLOCKED.getErrorMessage());
        return refreshTokenEntity;
    }

    public Boolean getUserNeedChangePassword(String username) throws Exception {
        return User.isNeedChangePassword(username);
    }

    public Boolean getUserNeedEnterSmsCode(String username) throws Exception {
        return User.needEnterSmsCode(username);
    }

    public String createRefreshToken(String username, String remoteAddress, Session persistenceSession)
            throws Exception {
        User user = DAOUtils.findUser(persistenceSession, username);
        closeAllUserRefreshTokenSessions(user, persistenceSession);
        String refreshTokenHash = CryptoUtils.MD5(username+System.currentTimeMillis()+JwtConfig.REFRESH_TOKEN_KEY);
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setRefreshTokenHash(refreshTokenHash);
        newRefreshToken.setUser(user);
        newRefreshToken.setIpAddress(remoteAddress);
        newRefreshToken.setCreatedAt(new Date());
        newRefreshToken.setExpiresIn(new Date(System.currentTimeMillis()+JwtConfig.getExpirationLongTime()));
        persistenceSession.save(newRefreshToken);
        return newRefreshToken.getRefreshTokenHash();
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
            Collection<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(claims);
            Number idOfUserNum = ((Number) claims.get(JwtClaimsConstant.ID_OF_USER)).longValue();
            Long idOfUser = null;
            if(idOfUserNum != null){
                idOfUser = idOfUserNum.longValue();
            }
            String username = (String) claims.get(JwtClaimsConstant.USERNAME);
            Boolean isEnabled = (Boolean) claims.get(JwtClaimsConstant.USER_IS_ENABLED);
            String surname = (String) claims.get(JwtClaimsConstant.SURNAME);
            String firstname = (String) claims.get(JwtClaimsConstant.FIRSTNAME);
            String secondname = (String) claims.get(JwtClaimsConstant.SECONDNAME);
            Number idOfOrgNum = ((Number) claims.get(JwtClaimsConstant.ID_OF_ORG));
            Long idOfOrg = null;
            if(idOfOrgNum != null)
                idOfOrg = idOfOrgNum.longValue();
            String shortOrgName = (String) claims.get(JwtClaimsConstant.SHORT_ORG_NAME);
            Number contractIdNum = ((Number) claims.get(JwtClaimsConstant.CONTRACT_ID));
            Long contractId = null;
            if(contractIdNum != null)
                contractId = contractIdNum.longValue();
            Integer idOfRole = (Integer) claims.get(JwtClaimsConstant.ID_OF_ROLE);
            String roleName = (String) claims.get(JwtClaimsConstant.ROLE_NAME);
            if(username == null || isEnabled == null)
                throw new JwtAuthenticationException(new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.TOKEN_INVALID.getErrorCode(),
                        JwtAuthenticationErrors.TOKEN_INVALID.getErrorMessage()));
            if(!isEnabled.booleanValue())
                throw new JwtAuthenticationException(new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.USER_DISABLED.getErrorCode(),
                        JwtAuthenticationErrors.USER_DISABLED.getErrorMessage()));
            JwtUserDetailsImpl jwtUserDetails = new JwtUserDetailsImpl(grantedAuthorities, isEnabled, isEnabled, username,
                    surname, firstname, secondname, idOfUser, idOfRole, roleName, contractId, idOfOrg, shortOrgName);
            return jwtUserDetails;
        }
        catch (Exception ex){
            throw  new JwtAuthenticationException(new JwtAuthenticationErrorDTO(JwtAuthenticationErrors.TOKEN_INVALID.getErrorCode(),
                    JwtAuthenticationErrors.TOKEN_INVALID.getErrorMessage()));
        }
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(DefaultClaims claims) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        Collection<?> items = (Collection<?>) claims.get(JwtClaimsConstant.AUTHORITIES);
        if (items != null) {
            for (Object item : items) {
                if (item instanceof GrantedAuthority) {
                    grantedAuthorities.add((GrantedAuthority) item);
                }
                else if (item instanceof Map){
                    for (Object o : ((Map) item).values()) {
                        if (o instanceof String) {
                            grantedAuthorities.add(new GrantedAuthorityImpl((String) o));
                        }
                    }
                }
            }
        }
        return grantedAuthorities;
    }

    public void closeAllUserRefreshTokenSessions(User user, Session persistenceSession) throws Exception {
        Criteria refreshTokenCriteria = persistenceSession.createCriteria(RefreshToken.class);
        refreshTokenCriteria.add(Restrictions.eq("user",user));
        List<RefreshToken> refreshTokenList = refreshTokenCriteria.list();
        for(RefreshToken refreshToken: refreshTokenList){
            persistenceSession.delete(refreshToken);
        }
    }


}

