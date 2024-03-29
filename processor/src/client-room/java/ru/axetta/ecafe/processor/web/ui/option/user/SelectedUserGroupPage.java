/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOClientRoomService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.06.2010
 * Time: 14:23:22
 * To change this template use File | Settings | File Templates.
 */

public class SelectedUserGroupPage extends BasicWorkspacePage {


    private String userName;

    public String getUserName() {
        return userName;
    }


    public void fill(Session session, Long idOfUser) throws Exception {
         DAOClientRoomService daoService= DAOClientRoomService.getInstance();
       // Query q=entityManager.createQuery("from User where idOfUser=:idOfUser");
       // q.setParameter("idOfUser",idOfUser);
        User user = daoService.getUser(idOfUser).get(0) ;
        if (null == user) {
            this.userName = null;
        } else {
            this.userName = user.getUserName();
        }
    }

}