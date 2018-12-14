/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

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
public class UserViewPage extends BasicWorkspacePage {

    protected Long idOfUser;
    protected String userName;
    protected String phone;
    protected String email;
    protected String firstName;
    protected String surname;
    protected String secondName;
    protected Date updateTime;
    protected final FunctionViewer functionViewer = new FunctionViewer();

    public String getPageFilename() {
        return "option/user/view";
    }

    public Long getIdOfUser() {
        return idOfUser;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public FunctionViewer getFunctionViewer() {
        return functionViewer;
    }

    public void fill(Session session, Long idOfUser) throws Exception {
        User user = (User) session.load(User.class, idOfUser);
        this.idOfUser = user.getIdOfUser();
        this.userName = user.getUserName();
        this.updateTime = user.getUpdateTime();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.functionViewer.fill(user);
        if(user.getPerson() != null){
            this.firstName = user.getPerson().getFirstName();
            this.surname = user.getPerson().getSurname();
            this.secondName = user.getPerson().getSecondName();
        } else {
            this.firstName = this.surname = this.secondName = "";
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
}