/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectPage;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 18.11.13
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ThinClientUserEditPage extends BasicWorkspacePage implements ClientSelectPage.CompleteHandler {
    private Long idOfClient;
    private String username;
    private String roleName;
    private Person person;
    private Client cl;
    private Org org;
    private String orgName;
    private long role;
    private boolean changePassword;
    private String password;
    private String passwordRepeat;
    
    private String infoMessages;
    private String errorMessages;
    
    private static final Logger logger = LoggerFactory.getLogger(ThinClientUserViewPage.class);
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;



    public String getPageTitle() {
        return super.getPageTitle() + username;
    }

    public String getPageFilename() {
        return "option/user/thin_client/edit";
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Person getPerson() {
        return person;
    }

    public Client getCl() {
        return cl;
    }

    public Org getOrg() {
        return org;
    }

    public long getRole() {
        return role;
    }

    public boolean isChangePassword() {
        changePassword = idOfClient == null ? true : false;
        return idOfClient == null ? true : changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }




    public void doChangePasswordShow (ActionEvent event) {

    }

    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        if (null != idOfClient) {
            cl = (Client) session.load(Client.class, idOfClient);
            person = cl.getPerson();
            org = DAOService.getInstance().findOrById(cl.getOrg().getIdOfOrg()); // почему-то LazyInit если напрямую
        }
    }

    public void doSave () {
        RuntimeContext.getAppContext().getBean(ThinClientUserEditPage.class).save();
    }

    @Transactional
    public void save() {
        clearMessages();
        Session session = null;
        try {
            if (username == null || username.length() < 1) {
                errorMessages = "Необходимо указать имя пользователя";
                return;
            }
            if (cl == null) {
                errorMessages = "Необходимо выбрать клиента";
                return;
            }
            if (changePassword && (password == null || password.length() < 1)) {
                errorMessages = "Необходимо указать новый пароль";
                return;
            }
            if (changePassword && (passwordRepeat == null || passwordRepeat.length() < 1)) {
                errorMessages = "Необходимо указать повторить новый пароль";
                return;
            }
            if (changePassword && !password.equals(passwordRepeat)) {
                errorMessages = "Пароли не совпадают";
                return;
            }


            session = (Session) entityManager.getDelegate();
            //  Шифрование пароля
            String encPassword = "";
            try {
                encPassword = new sun.misc.BASE64Encoder().encode(password.getBytes());
            } catch (Exception e) {
            }


            if (idOfClient == null) {
                //  Если id клиента нет, значит создаем
                createUser(session, encPassword);
                idOfClient = null;
                cl = null;
                org = null;
                person = null;
                username = null;
                password = null;
                passwordRepeat = null;
            } else {
                //  Иначе - редактируем
                modifyUser(session, encPassword);
                idOfClient = cl.getIdOfClient();
            }
            fill(session);
            infoMessages = "Изменения успешно внесены";
        } catch (Exception e) {
            logger.error("Failed to load thin client users", e);
        } finally {
        }
    }

    private void createUser(Session session, String encPassword) throws Exception {
        Query q = session.createSQLQuery("insert into cf_thin_client_users values "
                                    + "(:idofclient, :username, :password, :role, :creationDate, :modificationDate)");
        q.setLong("idofclient", cl.getIdOfClient());
        q.setString("username", username);
        q.setString("password", encPassword);
        q.setInteger("role", 1);
        q.setLong("creationDate", System.currentTimeMillis());
        q.setLong("modificationDate", System.currentTimeMillis());
        q.executeUpdate();
    }

    private void modifyUser(Session session, String encPassword) throws Exception {
        Query q = session.createSQLQuery("update cf_thin_client_users set username=:username, password=:password, "
                                        + "role=:role, modificationDate=:modificationDate, idofclient=:newIdofclient "
                                        + "where idofclient=:lastIdofclient");
        q.setLong("newIdofclient", cl.getIdOfClient());
        q.setString("username", username);
        q.setString("password", encPassword);
        q.setInteger("role", 1);
        q.setLong("modificationDate", System.currentTimeMillis());
        q.setLong("lastIdofclient", idOfClient);
        q.executeUpdate();
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(ThinClientUserEditPage.class).fill();
    }

    @Transactional
    public void fill() throws Exception {
        clearMessages();
        Session session = null;
        try {
            //  Если id клиента нет, значит учетная запись только создается
            if (idOfClient == null) {
                return;
            }

            session = (Session) entityManager.getDelegate();
            fill(session);
        } catch (Exception e) {
            logger.error("Failed to load thin client users", e);
        } finally {
        }
    }

    public void fill(Session session) throws Exception {
        Query q = session.createSQLQuery("select cf_thin_client_users.idofclient, cf_clients.idoforg, "
                + "cf_thin_client_users.role, cf_thin_client_users.username "
                + "from cf_thin_client_users "
                + "left join cf_clients on cf_thin_client_users.idofclient=cf_clients.idofclient "
                + "where cf_thin_client_users.idofclient=:idofclient "
                + "order by cf_clients.idoforg");
        q.setLong("idofclient", idOfClient);
        List users = q.list();
        for (Object o : users) {
            Object entry [] = (Object []) o;
            long idOfClient = ((BigInteger) entry [0]).longValue();
            long idOfOrg    = ((BigInteger) entry [1]).longValue();
            role            = ((Integer) entry [2]).intValue();
            username        = ((String) entry [3]).trim();

            cl = DAOService.getInstance().findClientById(idOfClient); cl = (Client) session.merge(cl);
            person = cl.getPerson();
            org = DAOService.getInstance().getOrg(idOfOrg);
            roleName = ThinClientUserListPage.DEFAULT_ROLE;
        }
    }

    public String getInfoMessages() {
        return infoMessages;
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    public void clearMessages() {
        errorMessages = "";
        infoMessages = "";
    }
}
