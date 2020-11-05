/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.axetta.ecafe.processor.core.persistence.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class JwtUserDetailsImpl implements UserDetails {

    private final User user;
    private final Collection<GrantedAuthority> grantedAuthorities;
    private final boolean isEnabled;
    private final boolean isAccountNonExpired;
    private final String username;
    private final String surname;
    private final String firstname;
    private final String secondname;
    private final Long idOfUser;
    private final Integer idOfRole;
    private final String roleName;
    private final Long contractId;
    private final Long idOfOrg;
    private final String shortOrgName;



    public JwtUserDetailsImpl(final User user) {
        this.user = user;
        this.username = user.getUserName();
        if(user.getPerson() != null){
            this.surname = user.getPerson().getSurname();
            this.firstname = user.getPerson().getFirstName();
            this.secondname = user.getPerson().getSecondName();
        }
        else{
            this.surname = null;
            this.firstname = null;
            this.secondname = null;
        }
        this.isEnabled = !user.isBlocked();
        this.isAccountNonExpired = user.blockedDateExpired();
        this.grantedAuthorities = new ArrayList<>();
        this.grantedAuthorities.add(new SimpleGrantedAuthority(User.DefaultRole.parse(user.getIdOfRole()).name()));
        this.idOfUser = user.getIdOfUser();
        this.idOfRole = user.getIdOfRole();
        this.roleName = user.getRoleName();
        if(user.getClient() == null)
            this.contractId = null;
        else
            this.contractId = user.getClient().getContractId();

        if(user.getClient() != null && user.getClient().getOrg() != null){
            this.idOfOrg = user.getClient().getOrg().getIdOfOrg();
            this.shortOrgName = user.getClient().getOrg().getShortName();
        }
        else if(user.getOrg() != null){
            this.idOfOrg = user.getOrg().getIdOfOrg();
            this.shortOrgName = user.getOrg().getShortName();
        }
        else {
            this.idOfOrg = null;
            this.shortOrgName = null;
        }
    }

    public JwtUserDetailsImpl(final Collection<GrantedAuthority> authorities, final Boolean isEnabled,
            final Boolean isAccountNonExpired,final String username, final String surname, final String firstname,
            final String secondname, final Long idOfUser, final Integer idOfRole, final String roleName,
            final Long contractId, final Long idOfOrg, final String shortOrgName){
        user = null;
        if(isEnabled != null)
            this.isEnabled = isEnabled.booleanValue();
        else
            this.isEnabled = false;
        if(isAccountNonExpired != null)
            this.isAccountNonExpired = isAccountNonExpired.booleanValue();
        else
            this.isAccountNonExpired = false;
        this.username = username;
        this.surname = surname;
        this.firstname = firstname;
        this.secondname = secondname;
        this.grantedAuthorities = authorities;
        this.idOfUser = idOfUser;
        this.idOfRole = idOfRole;
        this.roleName = roleName;
        this.contractId = contractId;
        this.idOfOrg = idOfOrg;
        this.shortOrgName = shortOrgName;
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
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
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
        return isEnabled;
    }

    public User getUser() { return user; }

    public String getSurname() {
        return surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSecondname() {
        return secondname;
    }

    public Long getIdOfUser() {
        return idOfUser;
    }

    public Integer getIdOfRole() {
        return idOfRole;
    }

    public String getRoleName() {
        return roleName;
    }

    public Long getContractId() {
        return contractId;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public String getShortOrgName() {
        return shortOrgName;
    }
}
