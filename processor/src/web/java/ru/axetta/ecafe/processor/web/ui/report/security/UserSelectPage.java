/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.security;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 03.07.2009
 */
public class UserSelectPage extends BasicWorkspacePage {

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private UserShortItem selectedItem = new UserShortItem();
    private List<UserShortItem> items = Collections.emptyList();
    private String filter;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeUserSelection(Session session) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeUserSelection(session, getSelectedItem().getIdOfUser());
            completeHandlers.pop();
        }
    }

    public void cancelUserSelection() {
        completeHandlers.clear();
    }

    public Object cancelFilter() {
        setSelectedItem(new UserShortItem());
        MainPage.getSessionInstance().updateUserSelectPage();
        return null;
    }

    public void fill(Session session, Long idOfUser) throws Exception {
        fill(session, idOfUser, null);
    }

    public void fill(Session session, Long idOfUser, User.DefaultRole role) throws Exception {
        List<UserShortItem> items = retrieveUsersByRole(session, getFilter(), role);
        UserShortItem selectedItem = new UserShortItem();
        if (null != idOfUser) {
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("idOfUser", idOfUser));
            criteria.setProjection(Projections.projectionList().add(Projections.property("idOfUser"), "idOfUser")
                    .add(Projections.property("userName"), "userName"));
            criteria.setCacheMode(CacheMode.NORMAL);
            criteria.setCacheable(true);
            criteria.setResultTransformer(Transformers.aliasToBean(UserShortItem.class));
            selectedItem = (UserShortItem) criteria.uniqueResult();
        }
        this.setItems(items);
        this.setSelectedItem(selectedItem);
    }

    public UserShortItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(UserShortItem selected) {
        if (null == selected) {
            this.selectedItem = new UserShortItem();
        } else {
            this.selectedItem = selected;
        }
    }

    public List<UserShortItem> getItems() {
        return items;
    }

    public void setItems(List<UserShortItem> items) {
        this.items = items;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public static List<UserShortItem> retrieveUsersByRoleAndDepartment(Session session, String filter, User.DefaultRole roleFilter,
            List<String> departmentFilter) throws HibernateException{
        Criteria userCriteria = session.createCriteria(User.class);
        userCriteria.addOrder(Order.asc("idOfUser"));

        if (StringUtils.isNotEmpty(filter)) {
            userCriteria.add(Restrictions.or(Restrictions.ilike("userName", filter, MatchMode.ANYWHERE),
                    Restrictions.ilike("userName", filter, MatchMode.ANYWHERE)));
        }

        if (null != roleFilter) {
            userCriteria.add(Restrictions.eq("idOfRole", roleFilter.getIdentification()));
        }

        if(!departmentFilter.isEmpty()){
            userCriteria.add(Restrictions.in("department", departmentFilter));
        }

        userCriteria.createAlias("person", "p", JoinType.LEFT_OUTER_JOIN);
        userCriteria.setProjection(
                Projections.projectionList().add(Projections.distinct(Projections.property("idOfUser")), "idOfUser")
                        .add(Projections.property("userName"), "userName")
                        .add(Projections.property("department"), "department")
                        .add(Projections.property("p.firstName"), "firstName")
                        .add(Projections.property("p.surname"), "surname")
                        .add(Projections.property("p.secondName"), "secondName"));
        userCriteria.setCacheMode(CacheMode.NORMAL);
        userCriteria.setCacheable(true);
        userCriteria.setResultTransformer(Transformers.aliasToBean(UserShortItem.class));
        userCriteria.addOrder(Order.asc("idOfUser"));
        return (List<UserShortItem>) userCriteria.list();
    }

    public interface CompleteHandler {
        void completeUserSelection(Session session, Long idOfUser) throws Exception;
    }

    public static List<UserShortItem> retrieveUsersByRole(Session session, String filter, User.DefaultRole role) throws HibernateException {
        Criteria userCriteria = session.createCriteria(User.class);
        userCriteria.addOrder(Order.asc("idOfUser"));

        if (StringUtils.isNotEmpty(filter)) {
            userCriteria.add(Restrictions.or(Restrictions.ilike("userName", filter, MatchMode.ANYWHERE),
                    Restrictions.ilike("userName", filter, MatchMode.ANYWHERE)));
        }

        if (null != role) {
            userCriteria.add(Restrictions.eq("idOfRole", role.getIdentification()));
        }

        userCriteria.createAlias("person", "p", JoinType.LEFT_OUTER_JOIN);
        userCriteria.setProjection(
                Projections.projectionList().add(Projections.distinct(Projections.property("idOfUser")), "idOfUser")
                        .add(Projections.property("userName"), "userName")
                        .add(Projections.property("department"), "department")
                        .add(Projections.property("p.firstName"), "firstName")
                        .add(Projections.property("p.surname"), "surname")
                        .add(Projections.property("p.secondName"), "secondName"));
        userCriteria.setCacheMode(CacheMode.NORMAL);
        userCriteria.setCacheable(true);
        userCriteria.setResultTransformer(Transformers.aliasToBean(UserShortItem.class));
        userCriteria.addOrder(Order.asc("idOfUser"));
        return (List<UserShortItem>) userCriteria.list();
    }

    public static List<UserShortItem> retrieveUsers(Session session, String filter) throws HibernateException {
        return retrieveUsersByRole(session, filter, null);
    }

    public static class UserShortItem {

        private Long idOfUser;
        private String userName;
        private String firstName;
        private String secondName;
        private String surname;
        private String department;
        private Boolean selected = false;

        public UserShortItem() {
            setSelected(false);
        }

        public UserShortItem(Long idOfUser, String userName) {
            this.idOfUser = idOfUser;
            this.userName = userName;
        }

        public Long getIdOfUser() {
            return idOfUser;
        }

        public void setIdOfUser(Long idOfUser) {
            this.idOfUser = idOfUser;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getSurnameAndFirstLetters() {
            String n = getSurname();
            if (null == n) return "";
            String fn = getFirstName();
            if ((null == fn) || (fn.length()==0)) return n;
            fn = fn.substring(0, 1)+".";
            String sn = getSecondName();
            if ((null == sn) || (sn.length()!=0)) sn=sn.substring(0, 1)+".";
            return n+" "+fn+sn;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }
    }
}