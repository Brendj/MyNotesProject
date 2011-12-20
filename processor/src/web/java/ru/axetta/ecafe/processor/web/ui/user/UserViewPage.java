/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.user;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.classic.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserViewPage extends BasicWorkspacePage {

    private Long idOfUser;
    private String userName;
    private String phone;
    private Date updateTime;
    private final FunctionViewer functionViewer = new FunctionViewer();

    public String getPageFilename() {
        return "user/view";
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
        this.functionViewer.fill(user);
    }

}