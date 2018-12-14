/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.User;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by nuc on 28.11.2018.
 */
public class UserGroupListPage extends UserListPage {

    @Override
    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        Criteria criteria = session.createCriteria(User.class);
        userFilter.addFilter(criteria);
        criteria.add(Restrictions.eq("deletedState", false));
        criteria.add(Restrictions.eq("isGroup", true));
        List users = criteria.list();
        for (Object object : users) {
            User user = (User) object;
            items.add(new Item(user));
        }
        this.items = items;
    }

    public void removeUser(Session session, Long idOfUser) throws Exception {
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq("idOfGroup", idOfUser));
        if (criteria.list().size() > 0) {
            throw new Exception("К данной роли привязаны пользователи");
        }
        super.removeUser(session, idOfUser);
    }

    @Override
    public String getPageFilename() {
        return "option/user/group_list";
    }
}
