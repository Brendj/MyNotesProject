/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import ru.axetta.ecafe.processor.web.token.security.service.JWTLoginService;
import ru.axetta.ecafe.processor.web.token.security.service.JWTLoginServiceImpl;
import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsImpl;
import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsService;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class GetTokenServiceImpl {

    private Logger logger = LoggerFactory.getLogger(GetTokenServiceImpl.class);

    private final String key = "testKey";

    private final int tokenExpiration = 60;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTLoginService JWTLoginServiceImpl;

    public String getToken(Session persistenceSession, String username, String password, String remoteAddr) throws Exception {
        if (username == null || password == null || remoteAddr == null)
            throw new Exception("Authentication error");
        Map<String, Object> tokenData = new HashMap<>();
        JWTLoginServiceImpl = new JWTLoginServiceImpl();
        userDetailsService = new JwtUserDetailsService();
        if (JWTLoginServiceImpl.login(username, password, remoteAddr,persistenceSession)) {
            JwtUserDetailsImpl user = (JwtUserDetailsImpl) userDetailsService.loadUserByUsername(username);
            tokenData.put("clientType", "user");
            tokenData.put("user_role", user.getAuthorities());
            tokenData.put("username", user.getUsername());
            tokenData.put("user_is_enabled",user.isEnabled());
            tokenData.put("token_create_date", new Date().getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, tokenExpiration);
            tokenData.put("token_expiration_date", calendar.getTime());
            JwtBuilder jwtBuilder = Jwts.builder();
            jwtBuilder.setExpiration(calendar.getTime());
            jwtBuilder.setClaims(tokenData);
            String token = jwtBuilder.signWith(SignatureAlgorithm.HS512, key).compact();
            return token;
        } else {
            throw new Exception("Authentication error");
        }
    }

}

