<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Главное меню --%>
<a4j:form id="mainMenuForm" styleClass="borderless-form" eventsQueue="mainFormEventsQueue">
<rich:panelMenu id="mainMenu" binding="#{mainPage.mainMenu}" styleClass="main-menu" expandSingle="true"
                disabledGroupClass="main-menu-disabled-group" disabledItemClass="main-menu-disabled-item"
                groupClass="main-menu-group" itemClass="main-menu-item" hoveredGroupClass="main-menu-hovered-group"
                hoveredItemClass="main-menu-hovered-item" topGroupClass="main-menu-top-group"
                topItemClass="main-menu-top-item" iconCollapsedGroup="triangle" iconExpandedGroup="triangleDown"
                iconItem="none" mode="ajax">

<rich:panelMenuGroup id="optionGroupMenu" label="Настройки" binding="#{mainPage.optionGroupPage.mainMenuComponent}"
                     rendered="#{mainPage.eligibleToEditOptions}">
    <a4j:support event="onclick" action="#{mainPage.showOptionGroupPage}" reRender="workspaceForm" />

    <rich:panelMenuGroup id="userGroupMenu" label="Пользователи" binding="#{mainPage.userGroupPage.mainMenuComponent}"
                         rendered="#{mainPage.eligibleToViewUsers}">
        <a4j:support event="onclick" action="#{mainPage.showUserGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="showUserListMenuItem" binding="#{mainPage.userListPage.mainMenuComponent}" label="Список"
                            action="#{mainPage.showUserListPage}" reRender="workspaceForm"/>

        <rich:panelMenuGroup id="selectedUserGroupMenu" label="#{mainPage.selectedUserGroupPage.userName}"
                             binding="#{mainPage.selectedUserGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{mainPage.showSelectedUserGroupPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="viewUserMenuItem" binding="#{mainPage.userViewPage.mainMenuComponent}" label="Просмотр"
                                action="#{mainPage.showUserViewPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editUserMenuItem" binding="#{mainPage.userEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{mainPage.showUserEditPage}" reRender="workspaceForm" />
        </rich:panelMenuGroup>

        <rich:panelMenuItem id="createUserMenuItem" binding="#{mainPage.userCreatePage.mainMenuComponent}" label="Создание"
                            action="#{mainPage.showUserCreatePage}" reRender="workspaceForm" />

    </rich:panelMenuGroup>

    <rich:panelMenuGroup id="cityGroupMenu" label="Города" binding="#{mainPage.cityGroupPage.mainMenuComponent}"
                         rendered="true">
        <a4j:support event="onclick" action="#{mainPage.showCityGroupPage}" reRender="workspaceForm" />

        <rich:panelMenuItem id="showCityListMenuItem" binding="#{mainPage.cityListPage.mainMenuComponent}" label="Список"
                            action="#{mainPage.showCityListPage}" reRender="workspaceForm"/>

        <rich:panelMenuGroup id="selectedCityGroupMenu" label="#{mainPage.selectedCityGroupPage.cityName}"
                             binding="#{mainPage.selectedCityGroupPage.mainMenuComponent}" rendered="false">
            <a4j:support event="onclick" action="#{mainPage.showSelectedCityGroupPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="viewCityMenuItem" binding="#{mainPage.cityViewPage.mainMenuComponent}" label="Просмотр"
                                action="#{mainPage.showCityViewPage}" reRender="workspaceForm" />

            <rich:panelMenuItem id="editCityMenuItem" binding="#{mainPage.cityEditPage.mainMenuComponent}"
                                label="Редактирование" action="#{mainPage.showCityEditPage}" reRender="workspaceForm" />
        </rich:panelMenuGroup>

        <rich:panelMenuItem id="createCityMenuItem" binding="#{mainPage.cityCreatePage.mainMenuComponent}" label="Создание"
                            action="#{mainPage.showCityCreatePage}" reRender="workspaceForm" />

    </rich:panelMenuGroup>
</rich:panelMenuGroup>
</rich:panelMenu>
</a4j:form> <%-- Главное меню --%>