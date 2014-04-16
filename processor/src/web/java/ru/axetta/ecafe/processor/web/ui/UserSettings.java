/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.04.12
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */

@Component
@Scope("session")
public class UserSettings extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {

    /* Properties */
    private String userName;
    private boolean changePassword;
    private String currPlainPassword;
    private String plainPassword;
    private String plainPasswordConfirmation;
    private String phone;
    private String email;
    private User currUser = null;
    protected List<Long> orgItems = new ArrayList<Long>(0);
    private String orgFilter = "Не выбрано";
    private String orgIds;

    @Autowired
    private RuntimeContext runtimeContext;

    @Autowired
    private DAOService daoService;

    //@PersistenceContext
    //private EntityManager entityManager;

    public String getPageFilename() {
        return "user_setting";
    }

    public String getPageTitle() {
        return "Мои настройки";
    }

    @Override
    public void onShow() throws Exception {
        if(currUser == null){
            currUser = getCurrentUser();
        }
        userName = currUser.getUserName();
        phone = currUser.getPhone();
        email = currUser.getEmail();
        orgItems.clear();
        Map<Long, String> orgList = daoService.getUserOrgses(currUser);
        completeOrgListSelection(orgList);
    }

    public User getCurrentUser() throws Exception {
        FacesContext context = FacesContext.getCurrentInstance();
        String userName = context.getExternalContext().getRemoteUser();
        //return DAOUtils.findUser(entityManager,userName);
        return daoService.findUserByUserName(userName);
    }

    public boolean updateInfoCurrentUser() throws Exception{
        boolean success=false;
        if(checkCurrentPassword(currPlainPassword)){
            if(changePassword){
                if(checkPasswordInfo(plainPassword, plainPasswordConfirmation)){
                    currUser.setPassword(plainPassword);
                } else {
                    return false;
                }
            }
            currUser.setUserName(userName);
            currUser.setPhone(phone);
            currUser.setEmail(email);
            currUser = daoService.setUserInfo(currUser);

            daoService.updateInfoCurrentUser(this.orgItems, currUser);
            success = true;
        }
        return success;
    }

    public Object save() throws Exception {
        boolean success = updateInfoCurrentUser();
        onShow();
        if(success) printMessage("Настройки сохранены");
        return null;
    }

    public Object restore() throws Exception {
        onShow();
        printMessage("Настройки  восстановлены");
        return null;
    }

    /* private method */
    private boolean checkCurrentPassword(String currPlainPassword) throws Exception{
        if(!currUser.hasPassword(currPlainPassword)) {
            printError("Текущий пароль неверный");
            return false;
        }
        return true;
    }

    private boolean checkPasswordInfo(String plainPassword,String plainPasswordConfirmation) throws Exception{
        if (StringUtils.isEmpty(plainPassword)) {
            printError("Недопустимое значение для нового пароля");
            return false;
        }
        if(!StringUtils.equals(plainPassword,plainPasswordConfirmation)) {
            printError("Новый пароль и подтверждение не совпадают");
            return false;
        }
        return true;
    }

    /* Getter and Setters */
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

    public String getCurrPlainPassword() {
        return currPlainPassword;
    }

    public void setCurrPlainPassword(String currPlainPassword) {
        this.currPlainPassword = currPlainPassword;
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

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            orgItems = new ArrayList<Long>();
            if (orgMap.isEmpty()) {
                orgFilter = "Не выбрано";
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (Long idOfOrg : orgMap.keySet()) {
                    //orgItems.add(new OrgItem(idOfOrg, orgMap.get(idOfOrg)));
                    orgItems.add(idOfOrg);
                    stringBuilder.append(orgMap.get(idOfOrg)).append("; ");
                }
                orgFilter = stringBuilder.substring(0, stringBuilder.length() - 2);
            }
        }
    }

    public String getGetStringIdOfOrgList() {
        return orgItems.toString().replaceAll("[^0-9,]","");
    }

    public String getOrgFilter() {
        return orgFilter;
    }

    public void setOrgFilter(String orgFilter) {
        this.orgFilter = orgFilter;
    }

    public String getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(String orgIds) {
        this.orgIds = orgIds;
    }
}
