/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import javax.servlet.http.HttpSession;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;


public class HttpSessionJournal {
    private Map<String, HttpSession> sessions;
    private boolean enableMultipleAuthorizations;
    private Map<String, Date> mapOfInactiveUsers;
    private final long ONE_MINUTE_IN_MILLIS = 60000;
    private final int WAITING_TIME = 3;
    private final long WAITING_TIME_IN_MILLIS = ONE_MINUTE_IN_MILLIS * WAITING_TIME;

    private static final Logger logger = LoggerFactory.getLogger(HttpSessionJournal.class);
    
    public HttpSessionJournal(boolean enableMultipleAuthorizations){
        this.enableMultipleAuthorizations = enableMultipleAuthorizations;
        this.sessions = new HashMap<String, HttpSession>();
        this.mapOfInactiveUsers = new HashMap<String, Date>();
    }

    public boolean isEnableMultipleAuthorizations() {
        return enableMultipleAuthorizations;
    }

    public HttpSession getHttpSessionByUsername(String username){
        HttpSession session = this.sessions.get(username);
        try{
            long lifeTime = new Date().getTime() - session.getCreationTime();
            long validTime = session.getMaxInactiveInterval();
            if(lifeTime >= validTime){
                this.sessions.remove(username);
                return null;
            } else {
                return session;
            }
        } catch (IllegalStateException e){
            this.sessions.remove(username);
            return null;
        }
    }

    public boolean userIsAuth(String username){
        if(this.sessions.containsKey(username)){
            HttpSession session = this.sessions.get(username);
            try{
                long lifeTime = new Date().getTime() - session.getCreationTime();
                long validTime = session.getMaxInactiveInterval() * 1000;
                if(lifeTime >= validTime){
                    this.sessions.remove(username);
                    return false;
                } else {
                    return true;
                }
            } catch (IllegalStateException e){
                this.sessions.remove(username);
                return false;
            }
        } else {
            return false;
        }
    }

    public void putNewHttpSession(String username, HttpSession session){
        this.sessions.put(username, session);
    }

    public void removeSessionFromJournal(String username){
        this.sessions.remove(username);
    }

    public void putNewInactivityUser(String user){
        this.mapOfInactiveUsers.put(user, new Date());
    }

    public void removeInactivityUser(String user){
        this.mapOfInactiveUsers.remove(user);
    }

    public boolean userIsInactive(String user){
        return this.mapOfInactiveUsers.containsKey(user);
    }

    //@Scheduled(fixedRate = 5000) //TODO Ддобавить в конфиг
   /* public void checkAndKillSessionsInactiveUsers() {
        if (this.enableMultipleAuthorizations || this.mapOfInactiveUsers.isEmpty()) {
            return;
        }
        String userLogin = "";

        for (Map.Entry<String, Date> entry : this.mapOfInactiveUsers.entrySet()) {
            try {
                userLogin = entry.getKey();
                Date lastActive = entry.getValue();
                Date now = new Date();
                if (now.getTime() - lastActive.getTime() >= WAITING_TIME_IN_MILLIS) {
                    HttpSession httpSession = this.getHttpSessionByUsername(userLogin);
                    if(httpSession == null){
                        throw new Exception("user httpSession is null");
                    }
                    if (StringUtils.isNotEmpty(userLogin)) {
                        httpSession.invalidate();
                        //RuntimeContext.getInstance().getHttpSessionJournal().removeSessionFromJournal(userLogin);
                    }
                }
            } catch (Exception e) {
                logger.error("Can't logout user" + userLogin + " : " + e.getMessage());
            }
        }
    }*/
}
