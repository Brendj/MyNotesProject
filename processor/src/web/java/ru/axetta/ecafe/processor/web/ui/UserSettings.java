/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.04.12
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */

@Component

public class UserSettings extends BasicWorkspacePage {

    /* Properties */
    private String userName;
    private boolean changePassword;
    private String currPlainPassword;
    private String plainPassword;
    private String plainPasswordConfirmation;
    private String phone;
    private String email;
    private User currUser = null;

    @Autowired
    RuntimeContext runtimeContext;

    @PersistenceContext
    private EntityManager entityManager;

    public String getPageFilename() {
        return "user_setting";
    }

    public String getPageTitle() {
        return "Мои настройки";
    }

    @Override
    public void onShow() throws Exception {
        if(currUser == null){
            currUser = getCurrentUser();
        }
        userName = currUser.getUserName();
        phone = currUser.getPhone();
        email = currUser.getEmail();
    }

    @Transactional
    public User getCurrentUser() throws Exception {
        FacesContext context = FacesContext.getCurrentInstance();
        String userName = context.getExternalContext().getRemoteUser();
        return DAOUtils.findUser(entityManager,userName);
    }

    @Transactional
    public boolean updateInfoCurrentUser() throws Exception{
        boolean success=false;
        if(checkCurrentPassword(currPlainPassword)){
            if(changePassword){
                if(checkPasswordInfo(plainPassword, plainPasswordConfirmation)){
                    currUser.setPassword(plainPassword);
                } else {
                    return false;
                }
            }
            currUser.setUserName(userName);
            currUser.setPhone(phone);
            currUser.setEmail(email);
            currUser = DAOService.getInstance().setUserInfo(currUser);
            success = true;
        }
        return success;
    }

    public Object save() throws Exception {
        boolean success = updateInfoCurrentUser();
        onShow();
        if(success) printMessage("Настройки сохранены");
        return null;
    }

    public Object restore() throws Exception {
        onShow();
        printMessage("Настройки  восстановлены");
        return null;
    }

    /* private method */
    private boolean checkCurrentPassword(String currPlainPassword) throws Exception{
        if(!currUser.hasPassword(currPlainPassword)) {
            printError("Текущий пароль неверный");
            return false;
        }
        return true;
    }

    private boolean checkPasswordInfo(String plainPassword,String plainPasswordConfirmation) throws Exception{
        if (StringUtils.isEmpty(plainPassword)) {
            printError("Недопустимое значение для нового пароля");
            return false;
        }
        if(!StringUtils.equals(plainPassword,plainPasswordConfirmation)) {
            printError("Новый пароль и подтверждение не совпадают");
            return false;
        }
        return true;
    }

    /* Getter and Setters */
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

    public String getCurrPlainPassword() {
        return currPlainPassword;
    }

    public void setCurrPlainPassword(String currPlainPassword) {
        this.currPlainPassword = currPlainPassword;
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
}
