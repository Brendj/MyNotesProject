/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.admin;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 24.08.12
 * Time: 13:34
 * To change this template use File | Settings | File Templates.
 */

@Component
@Scope("session")
public class LoginPage {
   private Boolean loginSuccess=false;
    private String rightPassword="admin";
    private String rightUserName="admin";
    private String userName;
    private String password;
    private Boolean loginPressed=false;
    private Boolean rendered;


    public Object login(){
          loginPressed=true;
        if(password.equals(rightPassword)&&userName.equals(rightUserName)){
            loginSuccess=true;

            return null;

        }
        loginSuccess=false;

        return null;

    }

    public Boolean getRendered() {
        return loginPressed&&!loginSuccess;
    }

    public void setRendered(Boolean rendered) {
        this.rendered = rendered;
    }

    public Boolean getLoginPressed() {
        return loginPressed;
    }

    public void setLoginPressed(Boolean loginPressed) {
        this.loginPressed = loginPressed;
    }

    public Boolean getLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(Boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public String getRightPassword() {
        return rightPassword;
    }

    public void setRightPassword(String rightPassword) {
        this.rightPassword = rightPassword;
    }

    public String getRightUserName() {
        return rightUserName;
    }

    public void setRightUserName(String rightUserName) {
        this.rightUserName = rightUserName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object logout(){
        loginSuccess=false;
        loginPressed=false;
        return null;

    }
}
