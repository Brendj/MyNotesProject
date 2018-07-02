/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpSessionJournal {
    private Map<String, HttpSession> sessions;
    private boolean enableMultipleAuthorizations;
    
    public HttpSessionJournal(boolean enableMultipleAuthorizations){
        this.enableMultipleAuthorizations = enableMultipleAuthorizations;
        this.sessions = new HashMap<String, HttpSession>();
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
                    return true;
                } else {
                    return false;
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
}
