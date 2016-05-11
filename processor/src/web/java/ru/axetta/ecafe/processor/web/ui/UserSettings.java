/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.UserNotificationType;
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
public class UserSettings extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList{

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
    protected List<Long> orgItemsCanceled = new ArrayList<Long>(0);

    private String orgFilter = "Не выбрано";
    private String orgFilterCanceled = "Не выбрано";

    private UserNotificationType selectOrgType;

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
        orgItemsCanceled.clear();

        selectOrgType = UserNotificationType.GOOD_REQUEST_CHANGE_NOTIFY;
        Map<Long, String> orgList = daoService.getUserOrgses(currUser.getIdOfUser(), selectOrgType);
        completeOrgListSelection(orgList);

        selectOrgType = UserNotificationType.ORDER_STATE_CHANGE_NOTIFY;
        Map<Long, String> orgListCanceled = daoService.getUserOrgses(currUser.getIdOfUser(), selectOrgType);
        completeOrgListSelection(orgListCanceled);
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
            if (StringUtils.isEmpty(phone)) {
                printError("Необходимо указать номер телефона");
                return false;
            } else {
                String mobile = Client.checkAndConvertMobile(phone);
                if (mobile == null) {
                    printError("Неверный формат контактного (мобильного) телефона");
                }
                phone = mobile;
            }
            currUser.setUserName(userName);
            currUser.setPhone(phone);
            currUser.setEmail(email);
            currUser = daoService.setUserInfo(currUser);

            daoService.updateInfoCurrentUser(this.orgItems, this.orgItemsCanceled, currUser);
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
        if (!User.passwordIsEnoughComplex(plainPassword)) {
            printError("Пароль не удовлетворяет требованиям безопасности: минимальная длина - 6 символов, должны присутствовать прописные и заглавные латинские буквы + хотя бы одна цифра или спецсимвол");
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
        switch (selectOrgType){
            case GOOD_REQUEST_CHANGE_NOTIFY: {
                if (orgMap != null) {
                    orgItems = new ArrayList<Long>();
                    if (orgMap.isEmpty()) {
                        orgFilter = "Не выбрано";
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Long idOfOrg : orgMap.keySet()) {
                            orgItems.add(idOfOrg);
                            stringBuilder.append(orgMap.get(idOfOrg)).append("; ");
                        }
                        orgFilter = stringBuilder.substring(0, stringBuilder.length() - 2);
                    }
                }
            } break;
            case ORDER_STATE_CHANGE_NOTIFY: {
                if (orgMap != null) {
                    orgItemsCanceled = new ArrayList<Long>();
                    if (orgMap.isEmpty()) {
                        orgFilterCanceled = "Не выбрано";
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Long idOfOrg : orgMap.keySet()) {
                            orgItemsCanceled.add(idOfOrg);
                            stringBuilder.append(orgMap.get(idOfOrg)).append("; ");
                        }
                        orgFilterCanceled = stringBuilder.substring(0, stringBuilder.length() - 2);
                    }
                }
            } break;
        }
    }

    public Object showOrgListSelectPage(){
        selectOrgType = UserNotificationType.GOOD_REQUEST_CHANGE_NOTIFY;
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    public Object showOrgListSelectCancelPage(){
        selectOrgType = UserNotificationType.ORDER_STATE_CHANGE_NOTIFY;
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    public String getGetStringIdOfOrgList() {
        switch (selectOrgType){
            case GOOD_REQUEST_CHANGE_NOTIFY: return orgItems.toString().replaceAll("[^0-9,]","");
            case ORDER_STATE_CHANGE_NOTIFY: return orgItemsCanceled.toString().replaceAll("[^0-9,]","");
        }
        return "";
    }

    public void setSelectOrgType(int id) {
        this.selectOrgType = UserNotificationType.values()[id];
    }

    public String getOrgFilter() {
        return orgFilter;
    }

    public String getOrgFilterCanceled() {
        return orgFilterCanceled;
    }

    public void setOrgFilter(String orgFilter) {
        this.orgFilter = orgFilter;
    }

    public void setOrgFilterCanceled(String orgFilterCanceled) {
        this.orgFilterCanceled = orgFilterCanceled;
    }

}
