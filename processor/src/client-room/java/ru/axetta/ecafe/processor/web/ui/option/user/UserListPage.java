/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOClientRoomService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */

/*@Component
@Scope("singleton")*/
public class UserListPage extends BasicWorkspacePage {
    final Logger logger = LoggerFactory
            .getLogger(UserListPage.class);



    public static class Item {



        private final Long idOfUser;
        private final String userName;
        private final Set<Long> functions;
        private final Date updateTime;

        public Item(User user) {
            this.idOfUser = user.getIdOfUser();
            this.userName = user.getUserName();
            this.updateTime = user.getUpdateTime();
            Set<Long> itemFunctions = new HashSet<Long>();
            Set<Function> userFunctions = user.getFunctions();
            for (Function function : userFunctions) {
                itemFunctions.add(function.getIdOfFunction());
            }
            this.functions = itemFunctions;
        }

        public Long getIdOfUser() {
            return idOfUser;
        }

        public String getUserName() {
            return userName;
        }

        public Set<Long> getFunctions() {
            return functions;
        }

        public Date getUpdateTime() {
            return updateTime;
        }
    }

    private List<Item> items = Collections.emptyList();

    public String getPageFilename() {
        return "option/user/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }



    public void fill(Session session) throws Exception {
         DAOClientRoomService daoService= DAOClientRoomService.getInstance();
        List<Item> items = new LinkedList<Item>();
        //Criteria criteria = session.createCriteria(User.class);
         //logger.info("entityManager: "+entityManager);
        // Query q=entityManager.createQuery("from User") ;
        List users = daoService.getUser(null);
        for (Object object : users) {
            User user = (User) object;
            items.add(new Item(user));
        }
        this.items = items;
    }

    public void removeUser(Session session, Long idOfUser) throws Exception {
        DAOClientRoomService daoService= DAOClientRoomService.getInstance();
       // User user = (User) session.load(User.class, idOfUser);
       // Query q=entityManager.createQuery("from User where idOfUser=:idOfUser");
       // q.setParameter("idOfUser",idOfUser);
        User user = daoService.getUser(idOfUser).get(0);
        //session.delete(user);
        daoService.deleteUser(user);
        fill(session);
    }
}