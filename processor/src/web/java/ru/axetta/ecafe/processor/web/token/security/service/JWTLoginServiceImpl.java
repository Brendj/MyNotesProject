/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.service;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.SecurityJournalAuthenticate;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.token.security.util.login.JwtLoginErrors;
import ru.axetta.ecafe.processor.web.token.security.util.login.JwtLoginException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Date;


public class JWTLoginServiceImpl implements JWTLoginService {

    private static final Logger logger = LoggerFactory.getLogger(JWTLoginServiceImpl.class);


    private JwtUserDetailsService userDetailsService;

    public JWTLoginServiceImpl(){
        userDetailsService = new JwtUserDetailsService();
    }

    @Override
    public boolean login(String username, String password, String remoteAddr, Session persistenceSession)
            throws Exception {
        boolean loginSuccsess = false;
        logger.debug("User \"{}\": try to login", username);
        if (StringUtils.isEmpty(username)) {
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createLoginFaultRecord(remoteAddr, null, null,
                            SecurityJournalAuthenticate.DenyCause.LOGIN_MISSING.getIdentification());
            DAOService.getInstance().writeAuthJournalRecord(record);
            throw new JwtLoginException(JwtLoginErrors.USERNAME_IS_NULL.getErrorCode(),
                    JwtLoginErrors.USERNAME_IS_NULL.getErrorMessage());
        }
        if (StringUtils.isEmpty(password)) {
            try {
                User user = ((JwtUserDetailsImpl) userDetailsService.loadUserByUsername(username)).getUser();
                processBadPassword(user, remoteAddr);
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createLoginFaultRecord(remoteAddr, username, user,
                                SecurityJournalAuthenticate.DenyCause.PASSWORD_MISSING.getIdentification());
                DAOService.getInstance().writeAuthJournalRecord(record);

                throw new JwtLoginException(JwtLoginErrors.INVALID_PASSWORD.getErrorCode(),
                        JwtLoginErrors.INVALID_PASSWORD.getErrorMessage() + String.format(" for user \"%s\"", username));
            }
            catch (UsernameNotFoundException e){
                throw new JwtLoginException(JwtLoginErrors.USER_NOT_FOUND.getErrorCode(),
                        JwtLoginErrors.USER_NOT_FOUND.getErrorMessage());
            }
        }
        try {
            loginSuccsess = checkUserCredentials(username, password, remoteAddr);
        } catch (JwtLoginException e) {
            throw e;
        }
        return loginSuccsess;
    }

    private void processBadPassword(User user, String remoteAddr) throws Exception {
        if ((user.isBlocked()) && (user.blockedDateExpired())) {
            user.setAttemptNumber(0); //при успешном логине сбрасываем количество ошибочных попыток входа
        }
        user = user.incAttemptNumbersAndBlock();
        if (user.getAttemptNumber() > RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SECURITY_MAX_AUTH_FAULT_COUNT)) {
            String message = String.format("Пользователь %s заблокирован на %s минут по причине превышения максимально допустимого количества неудачных попыток входа",
                    user.getUserName(),
                    RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SECURITY_TMP_BLOCK_ACC_TIME));
            String mess = String
                    .format("User \"%s\" is blocked after maximum fault login attempts (%s). Access denied.", user.getUserName(),
                            RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SECURITY_MAX_AUTH_FAULT_COUNT));
            logger.debug(mess);
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.BLOCK_USER, remoteAddr,
                            user.getUserName(), user, true, null, message);
            DAOService.getInstance().writeAuthJournalRecord(record);
            throw new JwtLoginException(JwtLoginErrors.USER_IS_BLOCKED.getErrorCode(), mess);
        }
    }

    private boolean checkUserCredentials(String username, String password, String remoteAddr) throws Exception {
        try {
            User user = ((JwtUserDetailsImpl) userDetailsService.loadUserByUsername(username)).getUser();
            if (user == null) {
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createLoginFaultRecord(remoteAddr, username, null,
                                SecurityJournalAuthenticate.DenyCause.USER_NOT_FOUND.getIdentification());
                DAOService.getInstance().writeAuthJournalRecord(record);
                throw new JwtLoginException(JwtLoginErrors.USER_NOT_FOUND.getErrorCode(),
                        JwtLoginErrors.USER_NOT_FOUND.getErrorMessage());
            }
            if ((user.isBlocked()) && (!user.blockedDateExpired())) {
                String mess = String.format("User \"%s\" is blocked. Access denied.", username);
                logger.debug(mess);
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createLoginFaultRecord(remoteAddr, username, user,
                                SecurityJournalAuthenticate.DenyCause.USER_BLOCKED.getIdentification());
                DAOService.getInstance().writeAuthJournalRecord(record);

                throw new JwtLoginException(JwtLoginErrors.USER_IS_BLOCKED.getErrorCode(), mess);
            }
            if (!user.loginAllowed()) {
                String mess = String.format("User \"%s\" is blocked . Access denied.", username);
                logger.info(mess);
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createLoginFaultRecord(remoteAddr, username, user,
                                SecurityJournalAuthenticate.DenyCause.LONG_INACTIVITY.getIdentification());
                DAOService.getInstance().writeAuthJournalRecord(record);

                throw new JwtLoginException(JwtLoginErrors.USER_IS_BLOCKED.getErrorCode(), mess);
            }
            if (!user.isWebArmUser()) {
                String mess = String.format("User \"%s\" has wrong role. Access denied.", username);
                logger.info(mess);
                throw new JwtLoginException(JwtLoginErrors.USER_INVALID_ROLE.getErrorCode(), mess);
            }
            final Integer idOfRole = user.getIdOfRole();
            /*final String userRole = request.getParameter("ecafeUserRole");
            if (!loginFromCorrectURL(userRole, idOfRole)) {
                request.setAttribute("errorMessage", AUTH_ERROR_THROUGH_CURRENT_URL);
                final String message = String.format("%s Login: %s.", AUTH_ERROR_THROUGH_CURRENT_URL, username);
                logger.debug(message);
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createLoginFaultRecord(request.getRemoteAddr(), username, user,
                                SecurityJournalAuthenticate.DenyCause.WRONG_AUTH_URL.getIdentification());
                DAOService.getInstance().writeAuthJournalRecord(record);

                throw new LoginException(message);
            }*/
            if (user.hasPassword(password)) {
                //userPrincipal = new JBossLoginModule.PrincipalImpl(username);
                user.setLastEntryIP(remoteAddr);
                user.setLastEntryTime(new Date());
                user.setAttemptNumber(0); //при успешном логине сбрасываем количество ошибочных попыток входа
                user = DAOService.getInstance().setUserInfo(user);
                logger.debug("User \"{}\": password validation successful", username);
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createSuccessAuthRecord(remoteAddr, username, user);
                DAOService.getInstance().writeAuthJournalRecord(record);
            } else {
                processBadPassword(user, remoteAddr);
                SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                        .createLoginFaultRecord(remoteAddr, username, user,
                                SecurityJournalAuthenticate.DenyCause.WRONG_PASSWORD.getIdentification());
                DAOService.getInstance().writeAuthJournalRecord(record);

                throw new JwtLoginException(JwtLoginErrors.INVALID_PASSWORD.getErrorCode(),
                        JwtLoginErrors.INVALID_PASSWORD.getErrorMessage());
            }
            return true;
        }
        catch (UsernameNotFoundException e){
            throw new JwtLoginException(JwtLoginErrors.USER_NOT_FOUND.getErrorCode(),
                    JwtLoginErrors.USER_NOT_FOUND.getErrorMessage());
        }
        catch (Exception e) {
            throw e;
        }
    }

}
