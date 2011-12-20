/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.user;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserCreatePage extends BasicWorkspacePage {

    private Long idOfUser;
    private String userName;
    private String plainPassword;
    private String plainPasswordConfirmation;
    private String phone;
    private final FunctionSelector functionSelector = new FunctionSelector();

    public String getPageFilename() {
        return "user/create";
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

    public FunctionSelector getFunctionSelector() {
        return functionSelector;
    }

    public void fill(Session session) throws Exception {
        this.functionSelector.fill(session);
    }

    public void createUser(Session session) throws Exception {
        User user = new User(userName, plainPassword, phone, new Date());
        user.setFunctions(functionSelector.getSelected(session));
        session.save(user);
    }
}