/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SecurityJournalAuthenticate;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by nuc on 07.12.2018.
 */
public class UserGroupCreatePage extends UserCreatePage {

    private static final Logger logger = LoggerFactory.getLogger(UserGroupCreatePage.class);

    @Override
    public String getPageFilename() {
        return "option/user/create_group";
    }

    @Override
    public void fill(Session session) throws Exception {
        this.functionSelector.fill(session);
    }

    public void createGroup() throws Exception {
        Session session = null;
        Transaction transaction = null;
        HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
        User currentUser = DAOReadonlyService.getInstance().getUserFromSession();
        String currentUserName = (currentUser == null) ? null : currentUser.getUserName();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            if (StringUtils.isEmpty(roleName)) {
                this.printError("Заполните название роли");
                throw new RuntimeException("Rolename field is null");
            }
            User u = DAOUtils.findUser(session, roleName);
            if (u != null) {
                throw new Exception("Такая роль уже существует");
            }
            User user = new User(roleName);
            user.setBlocked(false);
            user.setPasswordDate(new Date(System.currentTimeMillis()));
            user.setFunctions(functionSelector.getSelected(session));
            user.setNeedChangePassword(false);
            session.save(user);

            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.CREATE_USER, request.getRemoteAddr(), currentUserName,
                            currentUser, true, null, String.format("Создана пользовательская роль %s", userName));
            DAOService.getInstance().writeAuthJournalRecord(record);
            transaction.commit();
            transaction = null;
            printMessage("Роль создана");
        } catch (Exception e) {
            String comment = String.format("Ошибка при создании пользовательской роли с именем %s. Текст ошибки: %s", roleName, e.getMessage());
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.CREATE_USER, request.getRemoteAddr(),
                            currentUserName, currentUser, false,
                            SecurityJournalAuthenticate.DenyCause.USER_EDIT_BAD_PARAMETERS.getIdentification(), comment);
            DAOService.getInstance().writeAuthJournalRecord(record);
            printError(comment);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }
}
