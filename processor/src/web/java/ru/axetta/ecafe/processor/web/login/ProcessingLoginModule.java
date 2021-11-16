/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.login;

import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.SecurityJournalAuthenticate;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.utils.RequestUtils;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
@Component
public class ProcessingLoginModule implements LoginModule {

    private static final Logger logger = LoggerFactory.getLogger(ProcessingLoginModule.class);
    private static final String AUTH_ERROR_THROUGH_CURRENT_URL = "Запрашиваемый ресурс аутентификации недоступен пользователю с данной ролью.";
    private static final String AUTH_USER_ROLE_ATTRIBUTE_NAME = "ru.axetta.ecafe.userRole";
    public static final String ROLENAME_ADMIN = "admin";
    public static final String ROLENAME_DIRECTOR = "director";

    /**
     * Created by IntelliJ IDEA.
     * User: Developer
     * Date: 29.05.2009
     * Time: 13:17:55
     * To change this template use File | Settings | File Templates.
     */
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

            return Objects.equals(name, principal.name);
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

    /**
     * Created by IntelliJ IDEA.
     * User: Developer
     * Date: 29.05.2009
     * Time: 13:20:17
     * To change this template use File | Settings | File Templates.
     */
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

            if (!Objects.equals(name, group.name)) {
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

    public ProcessingLoginModule() {
        loginSucceeded = false;
    }

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
            Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    public boolean login() throws LoginException {
        // чтобы вызывалось при каждом логине нужно в standalone-ha.xml в секции
        // <security-domain name="ecafe" cache-type="default"> удалить [cache-type="default"]
        loginSucceeded = false;
        Callback[] callbacks = new Callback[]{new NameCallback("Username"), new PasswordCallback("Password", false)};
        HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
        try {
            callbackHandler.handle(callbacks);
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
        NameCallback nameCallback = (NameCallback) callbacks[0];
        username = nameCallback.getName();
        PasswordCallback passwordCallback = (PasswordCallback) callbacks[1];
        String plainPassword = new String(passwordCallback.getPassword());
        logger.debug("User \"{}\": try to login", username);
        if (StringUtils.isEmpty(username)) {
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createLoginFaultRecord(request.getRemoteAddr(), null, null,
                            SecurityJournalAuthenticate.DenyCause.LOGIN_MISSING.getIdentification());
            DAOService.getInstance().writeAuthJournalRecord(record);

            throw new LoginException("Username missing");
        }
        if (StringUtils.isEmpty(plainPassword)) {
            User user;
            try {
                user = getUserByName(username);
            } catch (Exception e) {
                throw new LoginException("User not found" + "\n" + e.getMessage());
            }
            processBadPassword(user, request);
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createLoginFaultRecord(request.getRemoteAddr(), username, null,
                            SecurityJournalAuthenticate.DenyCause.PASSWORD_MISSING.getIdentification());
            DAOService.getInstance().writeAuthJournalRecord(record);

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
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            User user = getUserByName(username);
            if (user == null) {
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createLoginFaultRecord(request.getRemoteAddr(), username, null,
                                SecurityJournalAuthenticate.DenyCause.USER_NOT_FOUND.getIdentification());
                DAOService.getInstance().writeAuthJournalRecord(record);

                throw new LoginException("User not found");
            }

            request.setAttribute(AUTH_USER_ROLE_ATTRIBUTE_NAME, request.getParameter("ecafeUserRole"));//for auth fault

            if ((user.isBlocked()) && (!user.blockedDateExpired())) {
                request.setAttribute("errorMessage",
                        String.format("Пользователь с именем \"%s\" заблокирован", username));
                String mess = String.format("User \"%s\" is blocked. Access denied.", username);
                logger.debug(mess);
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createLoginFaultRecord(request.getRemoteAddr(), username, user,
                                SecurityJournalAuthenticate.DenyCause.USER_BLOCKED.getIdentification());
                DAOService.getInstance().writeAuthJournalRecord(record);

                throw new LoginException(mess);
            }
            if (!user.loginAllowed()) {
                request.setAttribute("errorMessage", String.format(
                        "Пользователь с именем \"%s\" заблокирован по причине длительного неиспользования учетной записи",
                        username));
                String mess = String.format("User \"%s\" is blocked . Access denied.", username);
                logger.debug(mess);
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createLoginFaultRecord(request.getRemoteAddr(), username, user,
                                SecurityJournalAuthenticate.DenyCause.LONG_INACTIVITY.getIdentification());
                DAOService.getInstance().writeAuthJournalRecord(record);

                throw new LoginException(mess);
            }
            final Integer idOfRole = user.getIdOfRole();
            final String userRole = request.getParameter("ecafeUserRole");
            /*if (!loginFromCorrectURL(userRole, idOfRole) || user.isWebArmUser()) {
                request.setAttribute("errorMessage", AUTH_ERROR_THROUGH_CURRENT_URL);
                final String message = String.format("%s Login: %s.", AUTH_ERROR_THROUGH_CURRENT_URL, username);
                logger.debug(message);
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createLoginFaultRecord(request.getRemoteAddr(), username, user,
                                SecurityJournalAuthenticate.DenyCause.WRONG_AUTH_URL.getIdentification());
                DAOService.getInstance().writeAuthJournalRecord(record);

                throw new LoginException(message);
            }*/
            if (user.hasPassword(plainPassword)) {
                userPrincipal = new PrincipalImpl(username);
                user.setLastEntryIP(request.getRemoteAddr());
                user.setLastEntryTime(new Date());
                user.setAttemptNumber(0); //при успешном логине сбрасываем количество ошибочных попыток входа
                user = DAOService.getInstance().setUserInfo(user);
                logger.debug("User \"{}\": password validation successful", username);
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createSuccessAuthRecord(request.getRemoteAddr(), username, user);
                DAOService.getInstance().writeAuthJournalRecord(record);
            } else {
                processBadPassword(user, request);
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createLoginFaultRecord(request.getRemoteAddr(), username, user,
                                SecurityJournalAuthenticate.DenyCause.WRONG_PASSWORD.getIdentification());
                DAOService.getInstance().writeAuthJournalRecord(record);

                throw new LoginException("Password is invalid");
            }
            HttpSession httpSession = request.getSession(true);
            httpSession.setAttribute(User.USER_ID_ATTRIBUTE_NAME, user.getIdOfUser());
            httpSession.setAttribute(User.USER_IP_ADDRESS_ATTRIBUTE_NAME, request.getRemoteAddr());
            loginSucceeded = true;
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private User getUserByName(String username) throws Exception {
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria userCriteria = persistenceSession.createCriteria(User.class);
            userCriteria.add(Restrictions.eq("userName", username));
            userCriteria.add(Restrictions.eq("deletedState", false));
            return (User) userCriteria.uniqueResult();
        }
        finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void processBadPassword(User user, HttpServletRequest request) throws LoginException {
        if ((user.isBlocked()) && (user.blockedDateExpired())) {
            user.setAttemptNumber(0); //при успешном логине сбрасываем количество ошибочных попыток входа
        }
        user = user.incAttemptNumbersAndBlock();
        if (user.getAttemptNumber() > RuntimeContext.getInstance().getOptionValueInt(
                Option.OPTION_SECURITY_MAX_AUTH_FAULT_COUNT)) {
            String message = String.format("Пользователь %s заблокирован на %s минут по причине превышения максимально допустимого количества неудачных попыток входа",
                    username, RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SECURITY_TMP_BLOCK_ACC_TIME));
            request.setAttribute("errorMessage", message);
            String mess = String.format("User \"%s\" is blocked after maximum fault login attempts (%s). Access denied.", username,
                    RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SECURITY_MAX_AUTH_FAULT_COUNT));
            logger.debug(mess);
            /*SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createLoginFaultRecord(request.getRemoteAddr(), username, user,
                            SecurityJournalAuthenticate.DenyCause.MAX_FAULT_LOGIN_ATTEMPTS.getIdentification());
            DAOService.getInstance().writeAuthJournalRecord(record);*/
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.BLOCK_USER, request.getRemoteAddr(),
                            username, user, true, null, message);
            DAOService.getInstance().writeAuthJournalRecord(record);
            throw new LoginException(mess);
        }
    }

    public boolean commit() throws LoginException {
        logger.debug("User \"{}\": successfully logged in", username);
        Group group = new GroupImpl("Roles");
        for (Principal role : ROLES) {
            group.addMember(role);
        }
        subject.getPrincipals().add(userPrincipal);
        subject.getPrincipals().add(group);
        return true;
    }

    public boolean abort() throws LoginException {
        logger.debug("User \"{}\": login aborted", username);
        boolean result = loginSucceeded;
        loginSucceeded = false;
        return result;
    }

    public boolean logout() throws LoginException {
        logger.debug("User \"{}\": logged out", username);
        loginSucceeded = false;
        return true;
    }

    private boolean loginFromCorrectURL(String userRole, Integer idOfRole) {
        boolean isAdminLoginAttempt = userRole == null ? false : userRole.equals(ROLENAME_ADMIN);
        boolean isDirectorLoginAttempt = userRole == null ? false : userRole.equals(ROLENAME_DIRECTOR);
        boolean isCommonUserLoginAttempt = (userRole == null) || !(userRole.equals(ROLENAME_ADMIN) || userRole.equals(ROLENAME_DIRECTOR));

        return (isAdminLoginAttempt && (User.DefaultRole.ADMIN.getIdentification().equals(idOfRole) || User.DefaultRole.ADMIN_SECURITY.getIdentification().equals(idOfRole)))
                ||
                (isDirectorLoginAttempt && User.DefaultRole.DIRECTOR.getIdentification().equals(idOfRole))
                ||
                (isCommonUserLoginAttempt && !User.DefaultRole.ADMIN.getIdentification().equals(idOfRole) &&
                        !User.DefaultRole.ADMIN_SECURITY.getIdentification().equals(idOfRole) && !User.DefaultRole.DIRECTOR.getIdentification().equals(idOfRole));
    }
}