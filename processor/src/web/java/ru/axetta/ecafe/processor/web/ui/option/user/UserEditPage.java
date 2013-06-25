/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserEditPage extends BasicWorkspacePage implements ContragentListSelectPage.CompleteHandler{

    private Long idOfUser;
    private String userName;
    private boolean changePassword = false;
    private String plainPassword;
    private String plainPasswordConfirmation;
    private String phone;
    private String email;
    //private ContragentItem contragentItem;
    private List<ContragentItem> contragentItems = new ArrayList<ContragentItem>();
    private Integer idOfRole;
    private String roleName;
    private final UserRoleEnumTypeMenu userRoleEnumTypeMenu = new UserRoleEnumTypeMenu();
    private FunctionSelector functionSelector = new FunctionSelector();

    public UserRoleEnumTypeMenu getUserRoleEnumTypeMenu() {
        return userRoleEnumTypeMenu;
    }

    public String getPageFilename() {
        return "option/user/edit";
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

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
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
    }/*

    public ContragentItem getContragentItem() {
        return contragentItems;
    }

    public void setContragentItem(ContragentItem contragentItem) {
        this.contragentItem = contragentItem;
    }*/

    public String getContragentsFilter () {
        if (contragentItems == null) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        for (ContragentItem it : contragentItems) {
            if (str.length() > 0) {
                str.append("; ");
            }
            str.append(it.getContragentName());
        }
        return str.toString();
    }

    public Integer getIdOfRole() {
        return idOfRole;
    }

    public void setIdOfRole(Integer idOfRole) {
        this.idOfRole = idOfRole;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public FunctionSelector getFunctionSelector() {
        return functionSelector;
    }

    public void setFunctionSelector(FunctionSelector functionSelector) {
        this.functionSelector = functionSelector;
    }

    public void fill(Session session, Long idOfUser) throws Exception {
        User user = (User) session.load(User.class, idOfUser);
        fill(session, user);
    }

    public void updateUser(Session session, Long idOfUser) throws Exception {
        User user = (User) session.load(User.class, idOfUser);
        user.setUserName(userName);
        if (changePassword) {
            user.setPassword(plainPassword);
        }
        user.setPhone(phone);
        user.setEmail(email);
        user.setUpdateTime(new Date());
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        user.setIdOfRole(role.getIdentification());
        if(User.DefaultRole.SUPPLIER.equals(role)){
            user.setFunctions(functionSelector.getSupplierFunctions(session));
            /*
            if(contragentItem==null){
                throw new Exception("Contragent fields is null");
            }
            Contragent contragent = (Contragent) session.get(Contragent.class,contragentItem.getIdOfContragent());
            user.setContragent(contragent);*/
            if(contragentItems==null || contragentItems.size() < 1){
                throw new Exception("Contragents list is empty");
            }
            Set<Contragent> contragents = new HashSet<Contragent>();
            for (ContragentItem it : this.contragentItems) {
                Contragent contragent = (Contragent) session.load(Contragent.class, it.getIdOfContragent());
                contragents.add(contragent);
            }
            user.setContragents(contragents);
            user.setRoleName(role.toString());
        }
        if(role.equals(User.DefaultRole.MONITORING)){
            user.setFunctions(functionSelector.getMonitoringFunctions(session));
            user.setRoleName(role.toString());
        }
        if(role.equals(User.DefaultRole.ADMIN)){
            user.setFunctions(functionSelector.getAdminFunctions(session));
            user.setRoleName(role.toString());
        }
        if(User.DefaultRole.DEFAULT.equals(role)){
            if(this.roleName==null || this.roleName.isEmpty()){
                throw new Exception("Role name fields is null");
            }
            user.setFunctions(functionSelector.getSelected(session));
            user.setRoleName(this.roleName);
        }
        session.update(user);
        fill(session, user);
    }

    private void fill(Session session, User user) throws Exception {
        this.contragentItems.clear();
        this.idOfUser = user.getIdOfUser();
        this.userName = user.getUserName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.functionSelector.fill(session, user.getFunctions());
        if(user.getContragents()!=null){
            Set <Contragent> contragents = user.getContragents();
            for (Contragent c : contragents) {
                this.contragentItems.add(new ContragentItem(c));
            }
        }
        this.idOfRole = user.getIdOfRole();
        this.roleName = user.getRoleName();
    }

    public Boolean getIsSupplier(){
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.SUPPLIER);
    }

    public Boolean getIsDefault(){
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.DEFAULT);
    }


    public void completeContragentListSelection(Session session, List<Long> idOfContragentList, int multiContrFlag, String classTypes) throws Exception {
        if (null != idOfContragentList) {
            contragentItems.clear();
            StringBuilder str = new StringBuilder();
            for (Long idOfContragent : idOfContragentList) {
                Contragent currentContragent = (Contragent) session.load(Contragent.class, idOfContragent);
                ContragentItem contragentItem = new ContragentItem(currentContragent);
                contragentItems.add(contragentItem);
            }

        }
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