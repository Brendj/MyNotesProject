/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.Contract;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class UserCreatePage extends BasicWorkspacePage implements ContragentSelectPage.CompleteHandler{

    private Long idOfUser;
    private String userName;
    private String plainPassword;
    private String plainPasswordConfirmation;
    private String phone;
    private String email;
    private ContragentItem contragentItem;
    private final FunctionSelector functionSelector = new FunctionSelector();
    private Integer idOfRole;
    private String roleName;
    private final UserRoleEnumTypeMenu userRoleEnumTypeMenu = new UserRoleEnumTypeMenu();
    private Contragent currentContragent;

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

    public ContragentItem getContragentItem() {
        return contragentItem;
    }

    public void setContragentItem(ContragentItem contragentItem) {
        this.contragentItem = contragentItem;
    }

    public FunctionSelector getFunctionSelector() {
        return functionSelector;
    }

    public void fill(Session session) throws Exception {
        this.functionSelector.fill(session);
        this.idOfRole = User.DefaultRole.DEFAULT.getIdentification();
    }

    public void createUser(Session session) throws Exception {
        User user = new User(userName, plainPassword, phone, new Date());
        user.setEmail(email);
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        user.setIdOfRole(idOfRole);
        if(User.DefaultRole.DEFAULT.equals(role)){
            if(this.roleName==null || this.roleName.isEmpty()){
                throw new Exception("Role name fields is null");
            }
            user.setRoleName(this.roleName);
            user.setFunctions(functionSelector.getSelected(session));
        }
        if(role.equals(User.DefaultRole.SUPPLIER)){
            user.setFunctions(FunctionSelector.SUPPLIER_FUNCTIONS);
            if(currentContragent==null){
                throw new Exception("Contragent fields is null");
            }
            user.setContragent(currentContragent);
        }
        if(role.equals(User.DefaultRole.MONITORING)){
            user.setFunctions(FunctionSelector.MONITORING_FUNCTIONS);
        }
        if(role.equals(User.DefaultRole.ADMIN)){
            user.setFunctions(FunctionSelector.MONITORING_FUNCTIONS);
        }
        session.save(user);
    }

    public Boolean getIsSupplier(){
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.SUPPLIER);
    }

    public Boolean getIsDefault(){
        User.DefaultRole role = User.DefaultRole.parse(idOfRole);
        return role.equals(User.DefaultRole.DEFAULT);
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        if (null != idOfContragent) {
            currentContragent = (Contragent) session.load(Contragent.class, idOfContragent);
            this.contragentItem = new ContragentItem(currentContragent);
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