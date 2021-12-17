/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import ru.axetta.ecafe.processor.core.persistence.SecurityJournalAuthenticate;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created with IntelliJ IDEA.
 * User: Akhmetov
 * Date: 28.04.16
 * Time: 3:32
 * To change this template use File | Settings | File Templates.
 */
public class LogoutSessionListener implements HttpSessionListener {
    private static final Logger logger = LoggerFactory.getLogger(LogoutSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        final HttpSession session = httpSessionEvent.getSession();
        Long userId = (Long) session.getAttribute(User.USER_ID_ATTRIBUTE_NAME);
        String ipAddress = (String) session.getAttribute(User.USER_IP_ADDRESS_ATTRIBUTE_NAME);
        if (userId == null) {
            return;
        }
        try {
            User user = DAOReadonlyService.getInstance().findUserById(userId);
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate.createSuccessLogout(ipAddress, user.getUserName(), user);
            DAOService.getInstance().writeAuthJournalRecord(record);

        } catch (Exception ex) {
            logger.error("Error during authJournal writhing before session invalidate", ex);
        }
    }
}
