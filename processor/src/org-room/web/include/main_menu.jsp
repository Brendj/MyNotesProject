<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<style>
    .pic {
        margin-bottom: -4px;
        margin-right: 2px;
    }
    .search .rich-menu-item -hover{
        background-color: transparent;
        border-color: transparent;
    }
</style>

<h:form>
    <%-- Главное меню --%>
    <rich:toolBar>
        <%-- Управление клиентами --%>
        <rich:dropDownMenu>
            <f:facet name="label">
                <h:panelGroup>
                    <h:graphicImage value="/images/icon/user-group-icon.png" styleClass="pic"/>
                    <h:outputText value="Клиенты"/>
                    <%-- <h:commandLink action="#{mainPage.doShowclientListEditPage}" value="Клиенты"
                                   style="text-decoration: none"/> --%>
                </h:panelGroup>
            </f:facet>
            <rich:menuItem submitMode="ajax" id="clientListEditPageMenuItem"
                           binding="#{clientListEditPage.mainMenuComponent}" value="Клиенты"
                           action="#{clientListEditPage.show}" reRender="workspaceForm" />
            <rich:menuItem submitMode="ajax" id="clientRegisterPageMenuItem"
                           binding="#{clientRegisterPage.mainMenuComponent}" value="Регистрация"
                           action="#{clientRegisterPage.show}" reRender="workspaceForm" />
            <rich:menuItem submitMode="ajax" id="discountEditPageMenuItem"
                           binding="#{setupDiscountPage.mainMenuComponent}" value="Установка льгот"
                           action="#{setupDiscountPage.show}" reRender="workspaceForm" />
            <rich:menuItem submitMode="ajax" id="registrySynch"
                           binding="#{NSIOrgRegistrySynchPage.mainMenuComponent}" value="Синхронизация с Реестрами"
                           action="#{NSIOrgRegistrySynchPage.show}" reRender="workspaceForm" />
        </rich:dropDownMenu>

        <%-- Управление заявками на питание --%>
        <rich:dropDownMenu>
            <f:facet name="label">
                <h:panelGroup>
                    <h:graphicImage value="/images/icon/calendar.png" styleClass="pic"/>
                    <h:outputText value="Заявки"/>
                    <%-- <h:commandLink action="#{mainPage.doShowclientListEditPage}" value="Клиенты"
                                   style="text-decoration: none"/> --%>
                </h:panelGroup>
            </f:facet>
            <rich:menuItem submitMode="ajax" id="claimEditPageMenuItem"
                           binding="#{claimCalendarEditPage.mainMenuComponent}" value="Заявки на питание"
                           action="#{claimCalendarEditPage.show}" reRender="workspaceForm" />
        </rich:dropDownMenu>

        <%-- Групповое питание --%>
        <rich:dropDownMenu>
            <f:facet name="label">
                <h:panelGroup>
                    <h:graphicImage value="/images/icon/table.png" styleClass="pic"/>
                    <h:outputText value="Групповое питание"/>
                </h:panelGroup>
            </f:facet>
            <rich:menuItem submitMode="ajax" id="feedPlanEditPageMenuItem"
                           binding="#{feedPlanPage.mainMenuComponent}" value="План питания"
                           action="#{feedPlanPage.show}" reRender="workspaceForm" />
        </rich:dropDownMenu>

        <%-- Отчеты --%>
        <rich:dropDownMenu>
            <f:facet name="label">
                <h:panelGroup>
                    <h:graphicImage value="/images/icon/graph.png" styleClass="pic"/>
                    <h:outputText value="Отчеты"/>
                </h:panelGroup>
            </f:facet>
            <rich:menuItem submitMode="ajax" id="reportMenuItem"
                           binding="#{reportPage.mainMenuComponent}" value="Выполнить отчет"
                           action="#{reportPage.show}" reRender="workspaceForm" />
        </rich:dropDownMenu>
    </rich:toolBar>
</h:form>