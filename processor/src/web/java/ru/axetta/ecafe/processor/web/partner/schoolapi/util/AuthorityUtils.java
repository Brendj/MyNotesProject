/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.util;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
public class AuthorityUtils {
    private final Logger logger = LoggerFactory.getLogger(AuthorityUtils.class);

    public final boolean hasRole(String role) {
        UserDetails userDetails = getUserDetails();
        if (userDetails != null) {
            return anyRolePresent(userDetails.getAuthorities(), Arrays.asList(role));
        }
        return false;
    }

    public final boolean hasAnyRole(String... role) {
        UserDetails userDetails = getUserDetails();
        if (userDetails != null) {
            return anyRolePresent(userDetails.getAuthorities(), Arrays.asList(role));
        }
        return false;
    }

    public final User findCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            JwtUserDetailsImpl principal = (JwtUserDetailsImpl) authentication.getPrincipal();
            try {
                return DAOService.getInstance().findUserById(principal.getIdOfUser());
            } catch (Exception e) {
                logger.error("Error when find user, not set idOfUser, ", e);
            }
        }
        return null;
    }

    private UserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return  (UserDetails) principal;
            }
        }
        return null;
    }

    private boolean anyRolePresent(Collection<GrantedAuthority> authorities, Collection<String> roles) {
        if (authorities == null) return false;
        for (GrantedAuthority grantedAuthority : authorities) {
            for (String role : roles) {
                if (grantedAuthority.getAuthority().equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }


}
