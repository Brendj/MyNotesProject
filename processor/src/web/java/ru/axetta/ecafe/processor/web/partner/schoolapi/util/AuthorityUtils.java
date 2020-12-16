/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
public class AuthorityUtils {

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

    private UserDetails getUserDetails() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return  (UserDetails) principal;
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
