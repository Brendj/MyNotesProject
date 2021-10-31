<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка пользователей --%>
<h:panelGrid id="userListPage" binding="#{mainPage.userListPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid id="userListFilter" styleClass="borderless-grid">
        <rich:simpleTogglePanel label="Фильтр (#{mainPage.userListPage.userFilter.status})" switchType="client"
                                eventsQueue="mainFormEventsQueue" opened="true" headerClass="filter-panel-header">

            <h:panelGrid columns="1" styleClass="borderless-grid">

                <h:panelGroup>
                    <h:outputText escape="true" value="Имя пользователя" styleClass="output-text" />
                    <h:inputText value="#{mainPage.userListPage.userFilter.userName}" maxlength="10"
                                 styleClass="input-text" style="margin-left: 51px;" />
                </h:panelGroup>
            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid">
                <a4j:commandButton value="Применить" action="#{mainPage.showUserListPage}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />
                <a4j:commandButton value="Очистить" action="#{mainPage.userListPage.clearUserListPageFilter}"
                                   reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
            </h:panelGrid>
        </rich:simpleTogglePanel>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <rich:dataTable id="userListTable"
                    value="#{mainPage.userListPage.items}" var="item" rows="20"
                    columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Пользователь" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.userName}" action="#{mainPage.showUserViewPage}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item.idOfUser}" target="#{mainPage.selectedIdOfUser}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Права" />
            </f:facet>
            <h:outputText escape="true" value="#{item.functions}" converter="functionSetConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Роль" />
            </f:facet>
            <h:outputText escape="true" value="#{item.roleName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" width="250">
            <f:facet name="header">
                <h:outputText escape="true" value="Контрагент" />
            </f:facet>
            <a4j:repeat value="#{item.contragentList}" var="contragent">
                <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{contragent.contragentName}" action="#{mainPage.showContragentViewPage}" styleClass="command-link">
                    <f:setPropertyActionListener value="#{contragent.idOfContragent}" target="#{mainPage.selectedIdOfContragent}" />
                </a4j:commandLink>
                <h:outputText escape="true" value="; " styleClass="output-text" />
            </a4j:repeat>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Последние изменения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.updateTime}" converter="timeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Последний вход" />
            </f:facet>
            <h:outputText escape="true" value="#{item.lastEntryTime}" rendered="#{item.lastEntryTime!=null}"
                          converter="timeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="IP-адрес входа" />
            </f:facet>
            <h:outputText escape="true" value="#{item.lastEntryIP}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Заблокирован" />
            </f:facet>
            <h:outputText escape="true" value="#{item.blocked ? 'Да' : 'Нет'}"
                          styleClass="output-text #{item.blocked ? 'warn-messages' : ''}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата разблокирования" />
            </f:facet>
            <h:outputText escape="true" value="#{item.blockedUntilDate}" converter="dateConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Код активации" />
            </f:facet>
            <a4j:commandLink reRender="workspaceForm" action="#{mainPage.userSendActivationCode(item.userName)}" styleClass="command-link"
                    rendered="#{!item.phoneEmpty}">
                <h:graphicImage value="/images/16x16/mobile.png" style="border: 0;" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink reRender="workspaceForm" action="#{mainPage.showUserEditPage}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfUser}" target="#{mainPage.selectedIdOfUser}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToDeleteUsers}">
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" />
            </f:facet>

            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('removedUserDeletePanel')}.show()" reRender="removedUserDeletePanel">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfUser}" target="#{mainPage.removedIdOfUser}" />
            </a4j:commandLink>

        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="userListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                               stepControls="auto" boundaryControls="hide">
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>

    <rich:modalPanel id="removedUserDeletePanel" autosized="true" width="200" headerClass="modal-panel-header">
        <f:facet name="header">
            <h:outputText value="Удаление пользователя" styleClass="output-text" />
        </f:facet>
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: center;">
                    <h:outputText value="Вы уверены в том, что хотите удалить этого пользователя?"
                                  styleClass="output-text" />
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Да" action="#{mainPage.removeUser}"
                                           oncomplete="#{rich:component('removedUserDeletePanel')}.hide();"
                                           styleClass="command-button" reRender="userListTable" />
                        <a4j:commandButton value="Отмена" styleClass="command-button"
                                           onclick="#{rich:component('removedUserDeletePanel')}.hide();return false;" />
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </rich:modalPanel>
</h:panelGrid>


