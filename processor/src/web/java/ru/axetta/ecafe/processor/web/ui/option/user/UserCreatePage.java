/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserCreatePage extends BasicWorkspacePage implements ContragentListSelectPage.CompleteHandler{

    private Long idOfUser;
    private String userName;
    private String plainPassword;
    private String plainPasswordConfirmation;
    private String phone;
    private String email;
    private List<ContragentItem> contragentItems = new ArrayList<ContragentItem> ();
    private final FunctionSelector functionSelector = new FunctionSelector();
    private Integer idOfRole;
    private String roleName;
    private final UserRoleEnumTypeMenu userRoleEnumTypeMenu = new UserRoleEnumTypeMenu();
    //private Contragent currentContragent;
    private String contragentFilter;
    private String contragentIds;

    public void setIdOfRole(Integer idOfRole) {
        this.idOfRole = idOfRole;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getIdOfRole() {
        return idOfRole;
    }

    public String getRoleName() {
        return roleName;
    }

    public UserRoleEnumTypeMenu getUserRoleEnumTypeMenu() {
        return userRoleEnumTypeMenu;
    }

    public String getPageFilename() {
        return "option/user/create";
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

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getPlainPasswordConfirmation() {
        return plainPasswordConfirmation;
    }

    public void setPlainPasswordConfirmation(String plainPasswordConfirmation) {
        this.plainPasswordConfirmation = plainPasswordConfirmation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*public ContragentItem getContragentItem() {
        return contragentItem;
    }

    public void setContragentItem(ContragentItem contragentItem) {
        this.contragentItem = contragentItem;
    }*/

    public String getContragentFilter() {
        return contragentFilter;
    }

    public void setContragentFilter(String contragentFilter) {
        this.contragentFilter = contragentFilter;
    }

    public String getContragentIds() {
        return contragentIds;
    }

    public void setContragentIds(String contragentIds) {
        this.contragentIds = contragentIds;
    }

    private void setContragentFilterInfo(List<ContragentItem> contragentItems) {
        StringBuilder str = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        for (ContragentItem it : contragentItems) {
            if (str.length() > 0) {
                str.append("; ");
                ids.append(",");
            }
            str.append(it.getContragentName());
            ids.append(it.getIdOfContragent());
        }
        contragentFilter = str.toString();
        contragentIds = ids.toString();
    }

    public FunctionSelector getFunctionSelector() {
        return functionSelector;
    }

    public void fill(Session session) throws Exception {
        this.functionSelector.fill(session);
        this.idOfRole = User.DefaultRole.DEFAULT.getIdentification();
    }

    public void createUser(Session session) throws Exception {
        if (StringUtils.isEmpty(userName)) {
            this.printError("Заполните имя пользователя");
            throw new RuntimeException("Username field is null");
        }
        User user = new User(userName, plainPassword, phone, new Date());
        user.setEmail(email);
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        user.setIdOfRole(idOfRole);
        user.setBlocked(false);
        if (User.DefaultRole.DEFAULT.equals(role)) {
            if (StringUtils.isEmpty(roleName)) {
                this.printError("Заполните имя роли");
                throw new Exception("Role name fields is null");
            }
            user.setRoleName(this.roleName);
            user.setFunctions(functionSelector.getSelected(session));
        }
        if (role.equals(User.DefaultRole.SUPPLIER)) {
            user.setFunctions(functionSelector.getSupplierFunctions(session));
            user.setRoleName(role.toString());
            if (contragentItems.isEmpty()) {
                this.printError("Список контрагентов пуст.");
                throw new RuntimeException("Contragent list is empty");
            }
        }
        user.getContragents().clear();
        for (ContragentItem it : this.contragentItems) {
            Contragent contragent = (Contragent) session.load(Contragent.class, it.getIdOfContragent());
            user.getContragents().add(contragent);
        }
        if(role.equals(User.DefaultRole.MONITORING)){
            user.setFunctions(functionSelector.getMonitoringFunctions(session));
            user.setRoleName(role.toString());
        }
        if(role.equals(User.DefaultRole.ADMIN)){
            user.setFunctions(functionSelector.getAdminFunctions(session));
            user.setRoleName(role.toString());
        }
        session.save(user);
    }

    public Boolean getIsDefault(){
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.DEFAULT);
    }

    @Override
    public void completeContragentListSelection(Session session, List<Long> idOfContragentList, int multiContrFlag,
            String classTypes) throws Exception {
        contragentItems.clear();
        for (Long idOfContragent : idOfContragentList) {
            Contragent currentContragent = (Contragent) session.load(Contragent.class, idOfContragent);
            ContragentItem contragentItem = new ContragentItem(currentContragent);
            contragentItems.add(contragentItem);
        }
        setContragentFilterInfo(contragentItems);
    }

    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }

}