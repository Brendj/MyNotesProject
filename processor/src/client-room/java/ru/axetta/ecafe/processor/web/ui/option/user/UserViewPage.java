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

/*@Component
@Scope("singleton")*/
public class UserViewPage extends BasicWorkspacePage {
 /*   @PersistenceContext
    private EntityManager entityManager;*/
    private Long idOfUser;
    private String userName;
    private String phone;
    private String email;
    private Date updateTime;
    private final FunctionViewer functionViewer = new FunctionViewer();

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
         DAOService daoService= DAOService.getInstance();
        // User user = (User) session.load(User.class, idOfUser);
       // Query q=entityManager.createQuery("from User where idOfUser=:idOfUser");
      //  q.setParameter("idOfUser",idOfUser);
        User user = daoService.getUser(idOfUser).get(0);

        this.idOfUser = user.getIdOfUser();
        this.userName = user.getUserName();
        this.updateTime = user.getUpdateTime();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.functionViewer.fill(user);
    }

}