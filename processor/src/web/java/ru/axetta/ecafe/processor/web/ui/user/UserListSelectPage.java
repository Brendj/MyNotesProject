/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.user;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.report.security.UserSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.util.*;

public class UserListSelectPage extends BasicWorkspacePage {

    private final Stack<CompleteHandlerList> completeHandlerLists = new Stack<CompleteHandlerList>();
    private Map<Long, String> selectedUsers = new HashMap<Long, String>();
    protected List<UserSelectPage.UserShortItem> items = Collections.emptyList();
    private User.DefaultRole roleFilter = null;
    private String filter = "";

    public void pushCompleteHandlerList(CompleteHandlerList handlerList) {
        completeHandlerLists.push(handlerList);
    }

    public void completeUserListSelection(boolean ok) throws Exception {
        Map<Long, String> userMap = null;
        if (ok) {
            //updateSelectedUsers();
            userMap = new HashMap<Long, String>();
            userMap.putAll(selectedUsers);
            /*for (Item item : items) {
                if (item.getSelected()) {
                    orgMap.put(item.getIdOfOrg(), item.getShortName());
                }
            }*/
        }
        if (!completeHandlerLists.empty()) {
            completeHandlerLists.peek().completeUserListSelection(userMap);
            completeHandlerLists.pop();
        }
    }

    public synchronized void fill(Session session, Set<Long> idOfUserList, User.DefaultRole roleFilter,
            Boolean isUpdate, MainPage mainPage, String userFilter) throws Exception {
        if (isUpdate) {
            updateSelectedUsers();
            mainPage.setUserFilterOfSelectUserListSelectPage(StringUtils.join(selectedUsers.keySet(), ","));
        } else {
            selectedUsers.clear();
        }

        String[] idOfUser = userFilter.split(",");
        Set<String> longSet = new HashSet<String>(Arrays.asList(idOfUser));
        ///
        for (String sId : longSet) {
            try {
                Long id = Long.parseLong(sId.trim());
                if (selectedUsers.containsKey(id)) {
                    continue;
                }
                User user = (User) session.get(User.class, id);
                String userName = user.getUserName();
                if (user.getPerson() != null)
                    userName += " (" + user.getPerson().getSurnameAndFirstLetters() + ")";
                selectedUsers.put(id, userName);
            } catch (Exception ignored) {
            }
        }

        if (null != idOfUserList) {
            for (Long id : idOfUserList) {
                User user = (User) session.load(User.class, id);
                String userName = user.getUserName();
                if (user.getPerson() != null)
                    userName += " (" + user.getPerson().getSurnameAndFirstLetters() + ")";
                selectedUsers.put(user.getIdOfUser(), userName);
            }
        }

        this.roleFilter = roleFilter;
        List<UserSelectPage.UserShortItem> items = UserSelectPage.retrieveUsersByRole(session, filter, this.roleFilter);
        for (UserSelectPage.UserShortItem userShortItem : items) {
            userShortItem.setSelected(selectedUsers.containsKey(userShortItem.getIdOfUser()));
        }
        this.items = items;
    }

    public synchronized void fill(Session session)
            throws Exception {
        selectedUsers.clear();
        List<UserSelectPage.UserShortItem> items = UserSelectPage.retrieveUsersByRole(session, filter, roleFilter);
        for (UserSelectPage.UserShortItem userShortItem : items) {
            userShortItem.setSelected(selectedUsers.containsKey(userShortItem.getIdOfUser()));
        }
        this.items = items;
    }

    private void updateSelectedUsers() {
        for (UserSelectPage.UserShortItem i : this.getItems()) {
            if (i.getSelected()) {
                selectedUsers.put(i.getIdOfUser(), i.getUserName() + " (" +i.getSurnameAndFirstLetters()+ ")");
            } else {
                selectedUsers.remove(i.getIdOfUser());
            }
        }
    }

    public interface CompleteHandlerList {

        void completeUserListSelection(Map<Long, String> userMap) throws Exception;
    }

    public Map<Long, String> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(Map<Long, String> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

    public String getSelectedUsersString() {
        String s = "";
        for (String user : getSelectedUsers().values()) {
            s = s + user + ", ";
        }
        if (s.length() > 2) {
            return s.substring(0, s.length() - 2);
        }
        return s;
    }

    public void deselectAllItems() {
        for (UserSelectPage.UserShortItem item : getItems()) {
            item.setSelected(false);
        }
    }

    public void selectAllItems() {
        for (UserSelectPage.UserShortItem item : getItems()) {
            item.setSelected(true);
        }
    }

    public List<UserSelectPage.UserShortItem> getItems() {
        return items;
    }

    public void setItems(List<UserSelectPage.UserShortItem> items) {
        this.items = items;
    }

    public User.DefaultRole getRoleFilter() {
        return roleFilter;
    }

    public void setRoleFilter(User.DefaultRole roleFilter) {
        this.roleFilter = roleFilter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
