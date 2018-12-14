/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.User;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserGroupViewPage extends UserViewPage {

    public String getPageFilename() {
        return "option/user/group_view";
    }

    public void fill(Session session, Long idOfUser) throws Exception {
        User user = (User) session.load(User.class, idOfUser);
        this.idOfUser = user.getIdOfUser();
        this.userName = user.getUserName();
        this.updateTime = user.getUpdateTime();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.functionViewer.fill(user);
        this.firstName = this.surname = this.secondName = "";
    }

}