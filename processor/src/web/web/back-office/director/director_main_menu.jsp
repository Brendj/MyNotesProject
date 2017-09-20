<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Akhmetov
  Date: 26.04.16
  Time: 18:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<a4j:form id="directorMenuForm" styleClass="borderless-form" eventsQueue="directorFormEventsQueue">
    <rich:panelMenu id="directorMenu" binding="#{directorPage.mainMenu}" styleClass="main-menu" expandSingle="true"
                    disabledGroupClass="main-menu-disabled-group" disabledItemClass="main-menu-disabled-item"
                    groupClass="main-menu-group" itemClass="main-menu-item" hoveredGroupClass="main-menu-hovered-group"
                    hoveredItemClass="main-menu-hovered-item" topGroupClass="main-menu-top-group"
                    topItemClass="main-menu-top-item" iconCollapsedGroup="triangle" iconExpandedGroup="triangleDown"
                    iconItem="none" mode="ajax">
        <rich:panelMenuGroup id="directorGroupMenu" label="Личный кабинет директора" expanded="true">
            <rich:panelMenuItem id="directorUseCardsMenuItem" label="Использование электронных носителей" binding="#{directorPage.directorUseCardsPage.mainMenuComponent}"
                                action="#{directorPage.showUseCardsPage}" reRender="workspaceForm" />
            <rich:panelMenuItem id="directorDiscountFoodMenuItem" label="Предоставление льготного питания" binding="#{directorPage.discountFoodPage.mainMenuComponent}"
                                action="#{directorPage.showDiscountFoodPage}" reRender="workspaceForm" />
            <rich:panelMenuItem id="directorLogout" label="Выход" action="#{directorPage.logout}" reRender="workspaceForm" />
        </rich:panelMenuGroup>
    </rich:panelMenu>
</a4j:form>
