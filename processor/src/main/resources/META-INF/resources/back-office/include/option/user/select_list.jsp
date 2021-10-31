<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<script type="text/javascript">
    function printObjectsSelected(output, sgcomponent) {
        output.innerHTML = sgcomponent.getSelectedItems().pluck('state');
    }
</script>

<style type="text/css">
    .imageNone {
        background-image: none;
        background-color: #f3f2f2;
    }

    .borderNone {
        border: 0;
    }

    .linkClass {
        text-decoration: underline;
        color: #0000ff;
    }

</style>

<rich:modalPanel id="modalUserListSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalUserListSelectorPanel')}.hide();return false;" />
    <rich:hotKey key="ctrl+a" handler="selectAll();return false;" />
    <rich:hotKey key="ctrl+d" handler="deselectAll();return false;" />

    <f:facet name="header">
        <h:outputText escape="true" value="Выбор пользователей" />
    </f:facet>
    <a4j:form id="modalUserListSelectorForm" binding="#{mainPage.userListSelectPage.pageComponent}"
              styleClass="borderless-form" eventsQueue="modalUserListSelectorFormEventsQueue">
        <a4j:jsFunction name="selectAll" action="#{mainPage.selectAllUserListSelectedItemsList}"
                        reRender="modalUserListSelectorForm" />
        <a4j:jsFunction name="deselectAll" action="#{mainPage.clearUserListSelectedItemsList}"
                        reRender="modalUserListSelectorForm" />
        <table class="borderless-grid" width="480">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid columns="3" styleClass="borderless-grid">
                        <h:panelGrid columns="1" styleClass="borderless-grid">
                            <h:panelGrid id="filterTestPanel" columns="1" styleClass="borderless-grid">
                                <h:panelGrid columns="1" styleClass="borderless-grid">
                                    <a4j:outputPanel id="myPanel" ajaxRendered="true">
                                        <h:inputText id="selectedUsersString"
                                                     value="#{mainPage.userListSelectPage.selectedUsersString}"
                                                     readonly="true" size="100" styleClass="input-text" />
                                    </a4j:outputPanel>
                                </h:panelGrid>
                            </h:panelGrid>
                        </h:panelGrid>
                    </h:panelGrid>
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                        <h:inputText value="#{mainPage.userListSelectPage.filter}" size="48" maxlength="128"
                                     styleClass="input-text">
                            <a4j:support event="onkeyup" action="#{mainPage.updateUserListSelectPage}"
                                         reRender="modalUserListSelectorTable, myPanel" />
                        </h:inputText>
                        <h:outputText escape="true" value="Отобразить фильтр по подразделениям"
                                      styleClass="output-text" />
                        <h:selectBooleanCheckbox value="#{mainPage.userListSelectPage.showDetail}" styleClass="output-text">
                            <a4j:support event="onclick" reRender="modalUserListSelectorForm" ajaxSingle="true" />
                        </h:selectBooleanCheckbox>
                        <h:panelGroup layout="block" style="height: 150px; overflow-y: scroll;"
                                      rendered="#{mainPage.userListSelectPage.showDetail}">
                        <h:selectManyCheckbox id="userListSelectorTitles"
                                              value="#{mainPage.userListSelectPage.preferentialTitleDepartment}"
                                              layout="pageDirection" styleClass="output-text"
                                              rendered="#{mainPage.userListSelectPage.showDetail}">
                            <a4j:support requestDelay="1000" event="onclick" reRender="modalUserListSelectorForm" action="#{mainPage.updateUserListSelectPage}"/>
                        <f:selectItems value="#{mainPage.userListSelectPage.availableTitleDepartment}"/>
                       </h:selectManyCheckbox>
                        </h:panelGroup>
                    </h:panelGrid>
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <a4j:commandButton action="#{mainPage.selectAllUserListSelectedItemsList}"
                                           reRender="modalUserListSelectorTable, myPanel" styleClass="command-link"
                                           value="Выбрать все" />
                        <a4j:commandButton action="#{mainPage.clearUserListSelectedItemsList}"
                                           reRender="modalUserListSelectorTable, myPanel" styleClass="command-link"
                                           value="Очистить выбор" />
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalUserListSelectorTable"
                                    value="#{mainPage.userListSelectPage.items}" var="item" rows="8"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <rich:column headerClass="column-header">
                            <h:selectBooleanCheckbox value="#{item.selected}">
                                <a4j:support event="onchange" action="#{mainPage.updateUserListSelectPage}"
                                             reRender="selectedUsersString" />
                            </h:selectBooleanCheckbox>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Ид." />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.idOfUser}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Имя пользователя" />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.userName}"
                                          styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Подразделение" />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.department}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="ФИО" />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.surnameAndFirstLetters}" styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalUserListSelectorTable" renderIfSinglePage="false"
                                               maxPages="5" fastControls="hide" stepControls="auto"
                                               boundaryControls="hide">
                                <a4j:support event="onpagechange" />
                                <f:facet name="previous">
                                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                                </f:facet>
                                <f:facet name="next">
                                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                                </f:facet>
                            </rich:datascroller>
                        </f:facet>
                    </rich:dataTable>
                </td>
            </tr>
            <tr>
                <td style="text-align: right;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Ok" action="#{mainPage.completeUserListSelectionOk}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}, selectedUser"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalUserListSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.completeUserListSelectionCancel}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}, selectedUser"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalUserListSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px;">
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>