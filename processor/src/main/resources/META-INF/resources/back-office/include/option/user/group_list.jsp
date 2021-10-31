<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка пользователей --%>
<h:panelGrid id="userGroupListPage" binding="#{mainPage.userGroupListPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid id="userGroupListFilter" styleClass="borderless-grid">
        <rich:simpleTogglePanel label="Фильтр (#{mainPage.userGroupListPage.userFilter.status})" switchType="client"
                                eventsQueue="mainFormEventsQueue" opened="true" headerClass="filter-panel-header">

            <h:panelGrid columns="1" styleClass="borderless-grid">

                <h:panelGroup>
                    <h:outputText escape="true" value="Название группы" styleClass="output-text" />
                    <h:inputText value="#{mainPage.userGroupListPage.userFilter.userName}" maxlength="10"
                                 styleClass="input-text" style="margin-left: 51px;" />
                </h:panelGroup>
            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid">
                <a4j:commandButton value="Применить" action="#{mainPage.showUserGroupListPage}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />
                <a4j:commandButton value="Очистить" action="#{mainPage.userGroupListPage.clearUserListPageFilter}"
                                   reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
            </h:panelGrid>
        </rich:simpleTogglePanel>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <rich:dataTable id="userGroupListTable"
                    value="#{mainPage.userGroupListPage.items}" var="item" rows="20"
                    columnClasses="left-aligned-column, left-aligned-column, left-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Группа" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.userName}" action="#{mainPage.showUserGroupViewPage}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item.idOfUser}" target="#{mainPage.selectedIdOfUserGroup}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink reRender="workspaceForm" action="#{mainPage.showUserGroupEditPage}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfUser}" target="#{mainPage.selectedIdOfUserGroup}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToDeleteUsers}">
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" />
            </f:facet>

            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('removedUserGroupDeletePanel')}.show()" reRender="removedUserGroupDeletePanel">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfUser}" target="#{mainPage.removedIdOfUserGroup}" />
            </a4j:commandLink>

        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="userGroupListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <rich:modalPanel id="removedUserGroupDeletePanel" autosized="true" width="200" headerClass="modal-panel-header">
        <f:facet name="header">
            <h:outputText value="Удаление группы" styleClass="output-text" />
        </f:facet>
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: center;">
                    <h:outputText value="Вы уверены в том, что хотите удалить эту группу?"
                                  styleClass="output-text" />
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Да" action="#{mainPage.removeUserGroup}"
                                           oncomplete="#{rich:component('removedUserGroupDeletePanel')}.hide();"
                                           styleClass="command-button" reRender="userGroupListTable" />
                        <a4j:commandButton value="Отмена" styleClass="command-button"
                                           onclick="#{rich:component('removedUserGroupDeletePanel')}.hide();return false;" />
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </rich:modalPanel>
</h:panelGrid>


