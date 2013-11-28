/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.login;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOClientRoomService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 29.05.2009
 * Time: 12:17:44
 * To change this template use File | Settings | File Templates.
 */
public class JBossLoginClientRoomModule implements LoginModule {

    private static final Logger logger = LoggerFactory.getLogger(JBossLoginClientRoomModule.class);

    private static class PrincipalImpl implements Principal, Serializable {

        private static final long serialVersionUID = 1L;
        private final String name;

        public PrincipalImpl(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            PrincipalImpl principal = (PrincipalImpl) o;

            return !(name != null ? !name.equals(principal.name) : principal.name != null);
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

    private static class GroupImpl implements Group, Serializable {

        private static final long serialVersionUID = 1L;
        private final String name;
        private final Set<Principal> users = new HashSet<Principal>();

        public GroupImpl(String name) {
            this.name = name;
        }

        public boolean addMember(Principal user) {
            return users.add(user);
        }

        public boolean removeMember(Principal user) {
            return users.remove(user);
        }

        public boolean isMember(Principal member) {
            return users.contains(member);
        }

        public Enumeration<? extends Principal> members() {
            return Collections.enumeration(users);
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            GroupImpl group = (GroupImpl) o;

            if (name != null ? !name.equals(group.name) : group.name != null) {
                return false;
            }
            return !(users != null ? !users.equals(group.users) : group.users != null);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (users != null ? users.hashCode() : 0);
            return result;
        }
    }

    private static final Principal[] ROLES = {new PrincipalImpl("AuthorizedUser")};

    private Subject subject;
    private CallbackHandler callbackHandler;
    private boolean loginSucceeded;
    private String username;
    private Principal userPrincipal;

    public JBossLoginClientRoomModule() {
        loginSucceeded = false;
    }

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
            Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    public boolean login() throws LoginException {
        loginSucceeded = false;
        Callback[] callbacks = new Callback[]{new NameCallback("Username"), new PasswordCallback("Password", false)};
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
        NameCallback nameCallback = (NameCallback) callbacks[0];
        username = nameCallback.getName();
        PasswordCallback passwordCallback = (PasswordCallback) callbacks[1];
        String plainPassword = new String(passwordCallback.getPassword());
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("User \"%s\": try to login", username));
        }
        if (StringUtils.isEmpty(username)) {
            throw new LoginException("Username missing");
        }
        if (StringUtils.isEmpty(plainPassword)) {
            throw new LoginException(String.format("User \"%s\": password missing", username));
        }
        try {
            checkUserCredentials(plainPassword);
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
        return loginSucceeded;
    }

    private void checkUserCredentials(String plainPassword) throws Exception {
        try {
            List<User>users= DAOClientRoomService.getInstance().getUserByName(username);
            User user=null;
            if(users!=null&&users.size()!=0){
                user = users.get(0);
            }
            loginSucceeded = null != user && user.hasPassword(plainPassword);
            if (loginSucceeded) {
                userPrincipal = new PrincipalImpl(username);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("User \"%s\": password validation successful", username));
                }
            }
        } finally {
        }
    }

    public boolean commit() throws LoginException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("User \"%s\": successfully logged in", username));
        }
        Group group = new GroupImpl("Roles");
        for (Principal role : ROLES) {
            group.addMember(role);
        }
        subject.getPrincipals().add(userPrincipal);
        subject.getPrincipals().add(group);
        return true;
    }

    public boolean abort() throws LoginException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("User \"%s\": login aborted", username));
        }
        boolean result = loginSucceeded;
        loginSucceeded = false;
        return result;
    }

    public boolean logout() throws LoginException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("User \"%s\": logged out", username));
        }
        loginSucceeded = false;
        return true;
    }

}