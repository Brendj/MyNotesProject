/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.SecurityJournalAuthenticate;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.RequestUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.cxf.common.util.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserListPage extends BasicWorkspacePage {

    protected static final Logger logger = LoggerFactory.getLogger(UserListPage.class);

    public static class Item {

        private final Long idOfUser;
        private final String userName;
        private final Set<Long> functions;
        private final Date lastEntryTime;
        private final Date updateTime;
        private final String lastEntryIP;
        private final String roleName;
        private final boolean blocked;
        private final List<Contragent> contragentList;
        private boolean phoneEmpty;
        private Date blockedUntilDate;

        public Item(User user) {
            this.idOfUser = user.getIdOfUser();
            this.userName = user.getUserName();
            this.updateTime = user.getUpdateTime();
            this.roleName = user.getRoleName();
            this.lastEntryTime = user.getLastEntryTime();
            this.lastEntryIP = user.getLastEntryIP();
            this.blocked = user.isBlocked() != null && user.isBlocked();
            Set<Long> itemFunctions = new HashSet<Long>();
            Set<Function> userFunctions = user.getFunctions();
            for (Function function : userFunctions) {
                itemFunctions.add(function.getIdOfFunction());
            }
            this.functions = itemFunctions;
            this.contragentList = new ArrayList<Contragent>(user.getContragents());
            this.phoneEmpty = StringUtils.isEmpty(user.getPhone()) || user.getPhone().equals("''");
            this.blockedUntilDate = user.getBlockedUntilDate();
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

        public Date getLastEntryTime() {
            return lastEntryTime;
        }

        public List<Contragent> getContragentList() {
            return contragentList;
        }

        public String getLastEntryIP() {
            return lastEntryIP;
        }

        public boolean isBlocked() {
            return blocked;
        }

        public boolean isPhoneEmpty() {
            return phoneEmpty;
        }

        public Date getBlockedUntilDate() {
            return blockedUntilDate;
        }
    }

    protected List<Item> items = Collections.emptyList();

    protected final UserFilter userFilter = new UserFilter();

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        Criteria criteria = session.createCriteria(User.class);
        userFilter.addFilter(criteria);
        criteria.add(Restrictions.eq("deletedState", false));
        criteria.add(Restrictions.eq("isGroup", false));
        List users = criteria.list();
        for (Object object : users) {
            User user = (User) object;
            items.add(new Item(user));
        }
        this.items = items;
    }

    public void removeUser(Session session, Long idOfUser) throws Exception {
        HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
        User currentUser = DAOReadonlyService.getInstance().getUserFromSession();
        String currentUserName = (currentUser == null) ? null : currentUser.getUserName();

        User user = (User) session.load(User.class, idOfUser);
        if (FacesContext.getCurrentInstance().getExternalContext().getRemoteUser().equals(user.getUserName())) {
            String comment = "Невозможно удалить пользователя, под которым осуществлен вход в систему";
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.DELETE_USER, request.getRemoteAddr(),
                            currentUserName, currentUser, false,
                            SecurityJournalAuthenticate.DenyCause.BAD_OPERATION.getIdentification(), comment);
            DAOService.getInstance().writeAuthJournalRecord(record);
            throw new Exception(comment);
        }
        user.setDeletedState(true);
        user.setDeleteDate(RuntimeContext.getInstance().getDefaultLocalCalendar(null).getTime());
        session.save(user);
        fill(session);
        SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                .createUserEditRecord(SecurityJournalAuthenticate.EventType.DELETE_USER, request.getRemoteAddr(), currentUserName,
                        currentUser, true, null, String.format("Удален пользователь %s", user.getUserName()));
        DAOService.getInstance().writeAuthJournalRecord(record);
    }

    public Object clearUserListPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            getUserFilter().clear();
            fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for user list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка пользователей: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);


        }
        return null;
    }

    public UserFilter getUserFilter() {
        return userFilter;
    }

    public String getPageFilename() {
        return "option/user/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }
}