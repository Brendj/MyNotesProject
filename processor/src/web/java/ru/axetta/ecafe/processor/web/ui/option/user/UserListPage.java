/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserListPage extends BasicWorkspacePage {

    public static class Item {

        private final Long idOfUser;
        private final String userName;
        private final Set<Long> functions;
        private final Date updateTime;
        private final String roleName;
        private final String contragents;

        public Item(User user) {
            this.idOfUser = user.getIdOfUser();
            this.userName = user.getUserName();
            this.updateTime = user.getUpdateTime();
            this.roleName = user.getRoleName();
            Set<Long> itemFunctions = new HashSet<Long>();
            Set<Function> userFunctions = user.getFunctions();
            for (Function function : userFunctions) {
                itemFunctions.add(function.getIdOfFunction());
            }
            this.functions = itemFunctions;
            this.contragents = getUserContragents(user.getContragents());
        }

        private String getUserContragents(Set<Contragent> cSet) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Contragent c : cSet) {
                i++;
                sb.append(c.getContragentName()).append(i == cSet.size() ? "" : "; ");
            }
            return sb.toString();
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

        public String getRoleName() {
            return roleName;
        }

        public String getContragents() {
            return contragents;
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
        List<Item> items = new LinkedList<Item>();
        Criteria criteria = session.createCriteria(User.class);
        List users = criteria.list();
        for (Object object : users) {
            User user = (User) object;
            items.add(new Item(user));
        }
        this.items = items;
    }

    public void removeUser(Session session, Long idOfUser) throws Exception {
        User user = (User) session.load(User.class, idOfUser);
        session.delete(user);
        fill(session);
    }
}