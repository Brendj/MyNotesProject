package ru.axetta.ecafe.processor.web.partner.meals.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class MealsUserDetails implements UserDetails {

    private String msh;
    private final Collection<GrantedAuthority> grantedAuthorities;

    public MealsUserDetails(String msh) {
        this.msh = msh;
        this.grantedAuthorities = new ArrayList<>();
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getMsh() {
        return msh;
    }

    public void setMsh(String msh) {
        this.msh = msh;
    }

    public Collection<GrantedAuthority> getGrantedAuthorities() {
        return grantedAuthorities;
    }
}
