/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.SecurityJournalAuthenticate;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserGroupEditPage extends UserEditPage {

    private final static Logger logger = LoggerFactory.getLogger(UserGroupEditPage.class);

    public void updateUser(Session session, Long idOfUser) throws Exception {
        HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
        User currentUser = DAOReadonlyService.getInstance().getUserFromSession();
        String currentUserName = (currentUser == null) ? null : currentUser.getUserName();

        try {
            if (StringUtils.isEmpty(userName)) {
                this.printError("Заполните имя роли");
                throw new RuntimeException("Username field is null");
            }

            User user = (User) session.get(User.class, idOfUser);
            user.setUserName(userName);
            user.setUpdateTime(new Date());
            user.setFunctions(functionSelector.getSelected(session));
            session.update(user);

            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("idOfGroup", user.getIdOfUser()));
            List<User> list = criteria.list();
            for (User u : list) {
                u.setFunctions(new HashSet<Function>(user.getFunctions()));
                session.update(u);
            }

            session.flush();

            SecurityJournalAuthenticate.EventType eventType;
            eventType = SecurityJournalAuthenticate.EventType.MODIFY_USER;
            String comment = String.format("Отредактированы данные роли %s", userName);
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(eventType, request.getRemoteAddr(), currentUserName,
                            currentUser, true, null, comment);
            DAOService.getInstance().writeAuthJournalRecord(record);
        } catch (Exception e) {
            String comment = String.format("Ошибка при изменении данных роли с именем %s. Текст ошибки: %s", userName, e.getMessage());
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.MODIFY_USER, request.getRemoteAddr(),
                            currentUserName, currentUser, false,
                            SecurityJournalAuthenticate.DenyCause.USER_EDIT_BAD_PARAMETERS.getIdentification(), comment);
            DAOService.getInstance().writeAuthJournalRecord(record);
            throw e;
        }
    }

    public Boolean getIsDefault(){
        return true;
    }

    protected void fill(Session session, User user) throws Exception {
        this.idOfUser = user.getIdOfUser();
        this.userName = user.getUserName();
        this.functionSelector.fill(session, user.getFunctions());
    }

    public String getPageFilename() {
        return "option/user/group_edit";
    }
}