/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.auth;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 10.09.13
 * Time: 12:29
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class LoginBean extends BasicWorkspacePage {
    private static final String PASSWORD_ENCODE_ALGORITHM = "base64";
    private static final Logger logger = LoggerFactory.getLogger(LoginBean.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    private boolean loggedIn = false;
    private Long idofuser;
    private Long idoforg;
    private Integer role;
    private String username = "";
    private String password = "";
    private String errorMessage;
    private User user;
    private Org org;




    /**
     * ****************************************************************************************************************
     * Загрузка данных из БД
     * ****************************************************************************************************************
     */
    public void doLogout () {
        loggedIn = false;
        idofuser = null;
        idoforg  = null;
        role     = null;
        username = null;
        password = null;
        user     = null;
        org      = null;
    }

    @Transactional
    public void doLogin () {
        resetMessages();
        if (username == null || username.length() < 1) {
            errorMessage = "Необходимо заполнить поле Пользователь";
            loggedIn = false;
            return;
        }
        if (password == null || password.length() < 1) {
            errorMessage = "Необходимо указать Пароль";
            loggedIn = false;
            return;
        }

        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            doLogin(session);
        } catch (Exception e) {
            errorMessage = "Не удалось пройти авторизацию, пожалуйста, повторите попытку позже";
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void doLogin (Session session) {
        String encPassword = "";
        try {
            encPassword = new sun.misc.BASE64Encoder().encode(password.getBytes());
        } catch (Exception e) {

        }
        org.hibernate.Query q = session.createSQLQuery(
                          "select cf_thin_client_users.idofuser, idoforg, role "
                        + "from cf_thin_client_users "
                        + "left join cf_users on cf_thin_client_users.idofuser=cf_users.idofuser "
                        + "where cf_users.username=:username and cf_users.password=:password");
        q.setString("username", username);
        q.setString("password", encPassword);
        List resultList = q.list();
        if (resultList.size() > 1) {
            errorMessage = "Не удалось пройти авторизацию, пожалуйста, повторите попытку позже";
            logger.error("Doubled keys in cf_thin_client_users");
            return;
        } else if (resultList.size() < 1) {
            errorMessage = "Не верные данные авторизации";
            return;
        } else {
            Object o[] = (Object[]) resultList.get(0);
            idofuser   = HibernateUtils.getDbLong(o[0]);
            idoforg    = HibernateUtils.getDbLong(o[1]);
            role       = HibernateUtils.getDbInt(o[2]);
            loggedIn = true;
        }


        //  Заполняем объекты
        org = loadOrg(session);
        //loadUser(session);
    }

    @Transactional
    public Org loadOrg () {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            return loadOrg(session);
        } catch (Exception e) {
            logger.error("Failed to load binded for user {" + idofuser + "} org {" + idoforg + "}");
        } finally {
            //HibernateUtils.close(session, logger);
        }
        logger.error("Org with id {" + idoforg + "} was no found");
        return null;
    }

    public Org loadOrg (Session session) {
        try {
            return (Org) session.get(Org.class, 6L);
        } catch (Exception e) {
            logger.error("failed to load", e);
        }
        return null;
    }

    @Transactional
    public User loadUser () {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            return (User) session.get(User.class, idofuser.longValue());
        } catch (Exception e) {
            logger.error("Failed to load user {" + idofuser + "}");
        } finally {
            //HibernateUtils.close(session, logger);
        }
        logger.error("User with id {" + idofuser + "} was no found");
        return null;
    }








    /**
     * ****************************************************************************************************************
     * GUI
     * ****************************************************************************************************************
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }








    /**
     * ****************************************************************************************************************
     * Вспомогательные методы и классы
     * ****************************************************************************************************************
     */
    public boolean isLoggedIn () {
        return loggedIn;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void resetMessages() {
        errorMessage = null;
    }

    public Org getOrg() {
        if (org == null) {
            org = RuntimeContext.getAppContext().getBean(LoginBean.class).loadOrg();
        }
        return org;
    }

    public Org getOrg(Session session) {
        if (org == null) {
            org = loadOrg(session);
        }
        return org;
    }

    public User getUser() {
        if (user == null) {
            user = RuntimeContext.getAppContext().getBean(LoginBean.class).loadUser();
        }
        return user;
    }
}
