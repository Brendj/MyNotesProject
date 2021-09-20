<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<script>
    function disableButtons(value) {
        var element = document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:clientButtonCardShow");
        if (element != null) {
            element.disabled=value;
        }
        element = document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:applyButtonCardShow");
        if (element != null) {
            element.disabled=value;
        }
        element = document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:clearButtonCardShow");
        if (element != null) {
            element.disabled=value;
        }
        element = document.getElementById("clientSelectSubView:modalClientSelectorForm:orgButtonSelectClient");
        if (element != null) {
            element.disabled=value;
        }
        element = document.getElementById("clientSelectSubView:modalClientSelectorForm:applyButtonSelectClient");
        if (element != null) {
            element.disabled=value;
        }
        element = document.getElementById("clientSelectSubView:modalClientSelectorForm:clearButtonSelectClient");
        if (element != null) {
            element.disabled=value;
        }
        for (i = 0; i < 10; i++) {
            var nameInTableBlock = "workspaceSubView:workspaceForm:workspacePageSubView:cardOperatorTable:".concat(i.toString(), ":blockCardButtonCardShow");
            var nameInTableCancel = "workspaceSubView:workspaceForm:workspacePageSubView:cardOperatorTable:".concat(i.toString(), ":cancelCardButtonCardShow");
            element = document.getElementById(nameInTableBlock);
            if (element != null) {
                element.disabled = value;
            }
            element = document.getElementById(nameInTableCancel);
            if (element != null) {
                element.disabled = value;
            }
        }
    }
</script>
<rich:modalPanel id="modalClientSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalClientSelectorPanel')}.hide();return false;"/>
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор клиента" />
    </f:facet>
    <a4j:form id="modalClientSelectorForm" styleClass="borderless-form" eventsQueue="modalClientSelectorFormEventsQueue"
              binding="#{mainPage.clientSelectPage.pageComponent}">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid">
                        <h:inputText value="#{mainPage.clientSelectPage.selectedItem.caption}" size="100" readonly="true"
                                     styleClass="input-text" />
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td>
                    <rich:simpleTogglePanel id="modalClientSelectorFilterPanel"
                                            label="Фильтр (#{mainPage.clientSelectPage.clientFilter.status})"
                                            switchType="client" eventsQueue="mainFormEventsQueue" opened="true"
                                            headerClass="filter-panel-header">
                        <h:panelGrid columns="2" styleClass="borderless-grid">
                            <h:outputText escape="true" value="Организация" styleClass="output-text" />
                            <h:panelGroup styleClass="borderless-div">
                                <h:inputText id="modalClientSelectorOrgFilter"
                                             value="#{mainPage.clientSelectPage.clientFilter.org.shortName}"
                                             readonly="true" styleClass="input-text long-field" style="margin-right: 2px;" />
                                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}"
                                                   reRender="modalOrgSelectorPanel" id="orgButtonSelectClient"
                                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show(); disableButtons(false);"
                                                   styleClass="command-link" style="width: 25px;"
                                                   onclick="disableButtons(true);"/>
                            </h:panelGroup>
                            <h:outputText escape="true" value="Номер лицевого счета" styleClass="output-text" />
                            <h:inputText value="#{mainPage.clientSelectPage.clientFilter.contractId}" maxlength="16"
                                         styleClass="input-text" />
                            <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
                            <h:inputText value="#{mainPage.clientSelectPage.clientFilter.person.surname}"
                                         maxlength="128" styleClass="input-text" />
                            <h:outputText escape="true" value="Имя" styleClass="output-text" />
                            <h:inputText value="#{mainPage.clientSelectPage.clientFilter.person.firstName}"
                                         maxlength="64" styleClass="input-text" />
                            <h:outputText escape="true" value="Отчество" styleClass="output-text" />
                            <h:inputText value="#{mainPage.clientSelectPage.clientFilter.person.secondName}"
                                         maxlength="128" styleClass="input-text" />
                        </h:panelGrid>
                        <h:panelGrid columns="2" styleClass="borderless-grid">
                            <a4j:commandButton value="Применить" action="#{mainPage.updateClientSelectPage}"
                                               reRender="modalClientSelectorForm" styleClass="command-button"
                                               onclick="disableButtons(true);" oncomplete="disableButtons(false)"
                                               id="applyButtonSelectClient"/>
                            <a4j:commandButton value="Очистить" action="#{mainPage.clearClientSelectPageFilter}"
                                               reRender="modalClientSelectorForm" ajaxSingle="true"
                                               styleClass="command-button"
                                               onclick="disableButtons(true);" oncomplete="disableButtons(false)"
                                               id="clearButtonSelectClient"/>
                        </h:panelGrid>
                    </rich:simpleTogglePanel>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalClientSelectorTable"
                                    value="#{mainPage.clientSelectPage.items}" var="item" rows="8"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <a4j:support event="onRowClick" reRender="modalClientSelectorForm">
                            <f:setPropertyActionListener value="#{item}"
                                                         target="#{mainPage.clientSelectPage.selectedItem}" />
                        </a4j:support>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="ФИО" />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.caption}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Организация" />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.org.shortNameInfoService}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Группа" />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.groupName}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Баланс" />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.balance}" styleClass="output-text" converter="copeckSumConverter" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalClientSelectorTable" renderIfSinglePage="false" maxPages="5"
                                               fastControls="hide" stepControls="auto" boundaryControls="hide">
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
                        <a4j:commandButton value="Ok" action="#{mainPage.completeClientSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="#{rich:component('modalClientSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.completeClientSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px;">
                            <f:setPropertyActionListener value="#{null}"
                                                         target="#{mainPage.clientSelectPage.selectedItem}" />
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>