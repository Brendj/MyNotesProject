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

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.06.2010
 * Time: 14:23:22
 * To change this template use File | Settings | File Templates.
 */
/*@Component
      @Scope("singleton")*/
public class SelectedUserGroupPage extends BasicWorkspacePage {
    /*@PersistenceContext
    private EntityManager entityManager;*/

    private String userName;

    public String getUserName() {
        return userName;
    }

    /*public void fill(Session session, Long idOfUser) throws Exception {
        User user = (User) session.load(User.class, idOfUser);
        if (null == user) {
            this.userName = null;
        } else {
            this.userName = user.getUserName();
        }
    }*/

    public void fill(Session session, Long idOfUser) throws Exception {
         DAOService daoService= DAOService.getInstance();
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