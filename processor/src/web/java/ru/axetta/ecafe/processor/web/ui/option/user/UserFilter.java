/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 28.11.13
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class UserFilter {

    private String userName;

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    private boolean isEmpty() {
        return StringUtils.isEmpty(userName);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void addFilter(Criteria criteria) {
        if (!isEmpty()){
            criteria.add(Restrictions.like("userName", userName, MatchMode.ANYWHERE));
        }
    }

    public void clear() {
        userName = null;
    }
}
