/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */

public class UserEditPage extends BasicWorkspacePage {

    private Long idOfUser;
    private String userName;
    private boolean changePassword = false;
    private String plainPassword;
    private String plainPasswordConfirmation;
    private String phone;
    private String email;
    private FunctionSelector functionSelector = new FunctionSelector();

    public String getPageFilename() {
        return "option/user/edit";
    }

    public Long getIdOfUser() {
        return idOfUser;
    }

    public void setIdOfUser(Long idOfUser) {
        this.idOfUser = idOfUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getPlainPasswordConfirmation() {
        return plainPasswordConfirmation;
    }

    public void setPlainPasswordConfirmation(String plainPasswordConfirmation) {
        this.plainPasswordConfirmation = plainPasswordConfirmation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public FunctionSelector getFunctionSelector() {
        return functionSelector;
    }

    public void setFunctionSelector(FunctionSelector functionSelector) {
        this.functionSelector = functionSelector;
    }

    public void fill(Session session, Long idOfUser) throws Exception {
        DAOService daoService= DAOService.getInstance();
        //Query q=entityManager.createQuery("from User where idOfUser=:idOfUser");
       // q.setParameter("idOfUser",idOfUser);
        User user =daoService.getUser(idOfUser).get(0);
        //User user = (User) session.load(User.class, idOfUser);
        fill(session, user);
    }

    public void updateUser(Session session, Long idOfUser) throws Exception {
       // User user = (User) session.load(User.class, idOfUser);
        DAOService daoService= DAOService.getInstance();
       // Query q=entityManager.createQuery("from User where idOfUser=:idOfUser");
        //q.setParameter("idOfUser",idOfUser);
        User user = daoService.getUser(idOfUser).get(0);

        user.setUserName(userName);
        if (changePassword) {
            user.setPassword(plainPassword);
        }
        user.setPhone(phone);
        user.setEmail(email);
        user.setFunctions(functionSelector.getSelected(session));
        user.setUpdateTime(new Date());
       // session.update(user);
       daoService.updateUser(user);
        fill(session, user);
    }

    private void fill(Session session, User user) throws Exception {
        this.idOfUser = user.getIdOfUser();
        this.userName = user.getUserName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.functionSelector.fill(session, user.getFunctions());
    }
}