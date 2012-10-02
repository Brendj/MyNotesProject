/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.option.city.*;
import ru.axetta.ecafe.processor.web.ui.option.user.*;


import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.richfaces.component.html.HtmlPanelMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 05.06.2009
 * Time: 14:49:47
 * To change this template use File | Settings | File Templates.
 */
public class MainPage {

    private Long removedIdOfUser;
    private Long removedIdOfCity;
    /*  private final ReportJobCreatePage reportJobCreatePage = new ReportJobCreatePage();*/
    private final UserCreatePage userCreatePage = new UserCreatePage();
    private final CityCreatePage cityCreatePage = new CityCreatePage();

    private Long selectedIdOfUser;
    private Long selectedIdOfCity;
    private final UserEditPage userEditPage = new UserEditPage();
    private final CityEditPage cityEditPage = new CityEditPage();
    private final UserViewPage userViewPage = new UserViewPage();
    private final CityViewPage cityViewPage = new CityViewPage();

    private final SelectedUserGroupPage selectedUserGroupPage = new SelectedUserGroupPage();
    private final SelectedCityGroupPage selectedCityGroupPage = new SelectedCityGroupPage();
    private static final Logger logger = LoggerFactory.getLogger(MainPage.class);
    private final UserListPage userListPage = new UserListPage();
    private final CityListPage cityListPage = new CityListPage();
    private final BasicWorkspacePage userGroupPage = new BasicWorkspacePage();
    private final BasicWorkspacePage cityGroupPage = new BasicWorkspacePage();
    private BasicWorkspacePage currentWorkspacePage = new DefaultWorkspacePage();
    private HtmlPanelMenu mainMenu;
    private final BasicWorkspacePage optionGroupPage = new BasicWorkspacePage();

    public HtmlPanelMenu getMainMenu() {
        return mainMenu;
    }

    public void setMainMenu(HtmlPanelMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public BasicWorkspacePage getOptionGroupPage() {
        return optionGroupPage;
    }

    public boolean isEligibleToEditOptions() throws Exception {
        //  return getCurrentUser().hasFunction(Function.FUNC_WORK_OPTION);
        return true;
    }

    public Object showOptionGroupPage() {
        currentWorkspacePage = optionGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    void updateSelectedMainMenu() {
        UIComponent mainMenuComponent = currentWorkspacePage.getMainMenuComponent();
        if (null != mainMenuComponent) {
            mainMenu.setValue(mainMenuComponent.getId());
        }
    }

    public BasicWorkspacePage getUserGroupPage() {
        return userGroupPage;
    }

    public BasicWorkspacePage getCityGroupPage() {
        return cityGroupPage;
    }

    public boolean isEligibleToViewUsers() throws Exception {
        //return getCurrentUser().hasFunction(Function.FUNC_USER_VIEW);
        return true;
    }

    public Object showUserGroupPage() {
        currentWorkspacePage = userGroupPage;
        updateSelectedMainMenu();
        return null;
    }

    public Object showCityGroupPage() {
        currentWorkspacePage = cityGroupPage;
        updateSelectedMainMenu();
        return null;
    }


    public UserListPage getUserListPage() {
        return userListPage;
    }

    public CityListPage getCityListPage() {
        return cityListPage;
    }

    public Object showUserListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {

            userListPage.fill(persistenceSession);

            currentWorkspacePage = userListPage;
        } catch (Exception e) {
            logger.error("Failed to fill user list page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы списка пользователей",
                            null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showCityListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {

            cityListPage.fill(persistenceSession);

            currentWorkspacePage = cityListPage;
        } catch (Exception e) {
            logger.error("Failed to fill user list page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы списка пользователей",
                            null));
        }
        updateSelectedMainMenu();
        return null;
    }


    public SelectedCityGroupPage getSelectedCityGroupPage() {
        return selectedCityGroupPage;
    }

    public SelectedUserGroupPage getSelectedUserGroupPage() {
        return selectedUserGroupPage;
    }

    public Object showSelectedUserGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {

            selectedUserGroupPage.fill(persistenceSession, selectedIdOfUser);

            currentWorkspacePage = selectedUserGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected user group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы пользователя",
                            null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showSelectedCityGroupPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {

            selectedCityGroupPage.fill(persistenceSession, selectedIdOfUser);

            currentWorkspacePage = selectedCityGroupPage;
        } catch (Exception e) {
            logger.error("Failed to fill selected city group page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке общей страницы города", null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public UserViewPage getUserViewPage() {
        return userViewPage;
    }

    public CityViewPage getCityViewPage() {
        return cityViewPage;
    }

    public Object showUserViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {

            selectedUserGroupPage.fill(persistenceSession, selectedIdOfUser);
            userViewPage.fill(persistenceSession, selectedIdOfUser);

            selectedUserGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = userViewPage;
        } catch (Exception e) {
            logger.error("Failed to fill user view page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра данных пользователя", null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showCityViewPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {

            selectedCityGroupPage.fill(persistenceSession, selectedIdOfCity);
            cityViewPage.fill(persistenceSession, selectedIdOfCity);

            selectedCityGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = cityViewPage;
        } catch (Exception e) {
            logger.error("Failed to fill city view page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы просмотра данных города", null));
        } finally {


        }
        updateSelectedMainMenu();
        return null;
    }

    public UserEditPage getUserEditPage() {
        return userEditPage;
    }

    public CityEditPage getCityEditPage() {
        return cityEditPage;
    }

    public Object showUserEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {

            selectedUserGroupPage.fill(persistenceSession, selectedIdOfUser);
            userEditPage.fill(persistenceSession, selectedIdOfUser);

            selectedUserGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = userEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill user edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования данных пользователя", null));
        }
        updateSelectedMainMenu();
        return null;
    }

    public Object showCityEditPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {

            selectedCityGroupPage.fill(persistenceSession, selectedIdOfCity);
            cityEditPage.fill(persistenceSession, selectedIdOfCity);

            selectedCityGroupPage.showAndExpandMenuGroup();
            currentWorkspacePage = cityEditPage;
        } catch (Exception e) {
            logger.error("Failed to fill city edit page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы редактирования данных города", null));
        }
        updateSelectedMainMenu();
        return null;
    }


    public UserCreatePage getUserCreatePage() {
        return userCreatePage;
    }

    public CityCreatePage getCityCreatePage() {
        return cityCreatePage;
    }

    public Object showUserCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {

            userCreatePage.fill(persistenceSession);

            currentWorkspacePage = userCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show user create page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы создания пользователя", null));
        }
        updateSelectedMainMenu();
        return null;
    }


    public Object showCityCreatePage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {

            cityCreatePage.fill(persistenceSession);

            currentWorkspacePage = cityCreatePage;
        } catch (Exception e) {
            logger.error("Failed to show city create page", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке страницы создания города",
                            null));
        }
        updateSelectedMainMenu();
        return null;
    }


    public BasicWorkspacePage getCurrentWorkspacePage() {
        return currentWorkspacePage;
    }

    public static MainPage getSessionInstance() throws Exception {
        FacesContext context = FacesContext.getCurrentInstance();
        return (MainPage) context.getApplication().createValueBinding("#{mainPage}").getValue(context);
    }

    public void setCurrentWorkspacePage(BasicWorkspacePage page) {
        this.currentWorkspacePage = page;
        updateSelectedMainMenu();
    }

    public boolean isEligibleToEditUsers() throws Exception {
        //return getCurrentUser().hasFunction(Function.FUNC_USER_EDIT);
        return true;
    }

    public Object createUser() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!StringUtils.equals(userCreatePage.getPlainPassword(), userCreatePage.getPlainPasswordConfirmation())) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Пароль и подтверждение пароля не совпадают", null));
        } else {
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {

                userCreatePage.createUser(persistenceSession);

                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Пользователь создан успешно", null));
            } catch (Exception e) {
                logger.error("Failed to create user", e);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при создании пользователя", null));
            }
        }
        return null;
    }

    public Object createCity() {
        FacesContext facesContext = FacesContext.getCurrentInstance();


        {
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {

                cityCreatePage.createCity(persistenceSession);

                facesContext
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Город создан успешно", null));
            } catch (Exception e) {
                logger.error("Failed to create city", e);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при создании города", null));
            }
        }
        return null;
    }

    public Object updateUser() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (userEditPage.isChangePassword() && !StringUtils
                .equals(userEditPage.getPlainPassword(), userEditPage.getPlainPasswordConfirmation())) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Пароль и подтверждение пароля не совпадают", null));
        } else {
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {

                userEditPage.updateUser(persistenceSession, selectedIdOfUser);
                selectedUserGroupPage.fill(persistenceSession, selectedIdOfUser);

                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные пользователя обновлены успешно", null));
            } catch (Exception e) {
                logger.error("Failed to update user", e);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении данных пользователя",
                                null));
            }
        }
        return null;
    }

    public Object updateCity() {
        FacesContext facesContext = FacesContext.getCurrentInstance();


        {
            RuntimeContext runtimeContext = null;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {

                cityEditPage.updateCity(persistenceSession, selectedIdOfCity);
                selectedCityGroupPage.fill(persistenceSession, selectedIdOfCity);

                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные города обновлены успешно", null));
            } catch (Exception e) {
                logger.error("Failed to update user", e);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при изменении данных города", null));
            }
        }
        return null;
    }

    User currentUser;

    public User getCurrentUser() throws Exception {
        if (currentUser == null) {
            FacesContext context = FacesContext.getCurrentInstance();

            String userName = context.getExternalContext().getRemoteUser();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            RuntimeContext runtimeContext = null;


                DAOService daoService = DAOService.getInstance();
                /*currentUser = DAOUtils.findUser(persistenceSession, userName);*/
                currentUser = daoService.getUserByName(userName).get(0);



        }
        /////
        return currentUser;
    }

    public boolean isEligibleToDeleteUsers() throws Exception {
        //  return getCurrentUser().hasFunction(Function.FUNC_USER_DELETE);
        return true;
    }

    public Long getSelectedIdOfUser() {
        return selectedIdOfUser;
    }

    public Long getSelectedIdOfCity() {
        return selectedIdOfCity;
    }

    public void setSelectedIdOfCity(Long selectedIdOfCity) {
        this.selectedIdOfCity = selectedIdOfCity;
    }

    public Object removeUser() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {

            userListPage.removeUser(persistenceSession, removedIdOfUser);

            if (removedIdOfUser.equals(selectedIdOfUser)) {
                selectedIdOfUser = null;
                selectedUserGroupPage.hideMenuGroup();
            }
        } catch (Exception e) {
            logger.error("Failed to remove user", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении пользователя", null));
        }
        return null;
    }

    public Object removeCity() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {

            logger.info("from MainPage: removedIdOfCity=" + removedIdOfCity);
            cityListPage.removeCity(persistenceSession, removedIdOfCity);

            if (removedIdOfCity.equals(selectedIdOfCity)) {
                selectedIdOfCity = null;
                selectedCityGroupPage.hideMenuGroup();
            }
        } catch (Exception e) {
            logger.error("Failed to remove city", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при удалении города", null));
        }
        return null;
    }

    public Long getRemovedIdOfUser() {
        return removedIdOfUser;
    }

    public Long getRemovedIdOfCity() {
        return removedIdOfCity;
    }

    public void setRemovedIdOfCity(Long removedIdOfCity) {
        this.removedIdOfCity = removedIdOfCity;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setSelectedIdOfUser(Long selectedIdOfUser) {
        this.selectedIdOfUser = selectedIdOfUser;
    }

    public void setRemovedIdOfUser(Long removedIdOfUser) {
        this.removedIdOfUser = removedIdOfUser;
    }
    public String logout() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext facesExternalContext = facesContext.getExternalContext();
        HttpSession httpSession = (HttpSession) facesExternalContext.getSession(false);
        if (null != httpSession && StringUtils.isNotEmpty(facesExternalContext.getRemoteUser())) {
            httpSession.invalidate();
        }
        return "logout";
    }

}


