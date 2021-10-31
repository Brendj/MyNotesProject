<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="NSIRegistrySyncPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.NSIRegistrySyncPage"--%>

<h:panelGrid id="NSIRegistrySyncPage" styleClass="borderless-grid" binding="#{NSIRegistrySyncPage.pageComponent}">
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" />
        </f:facet>
    </a4j:status>


    <rich:simpleTogglePanel label="Введите параметры" switchType="client" opened="true"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{NSIRegistrySyncPage.orgName}" readonly="true" styleClass="input-text"
                             style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>
            <h:outputText escape="true" value="Вносить изменения при синхронизации" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{NSIRegistrySyncPage.performChanges}"/>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">

            <a4j:commandButton value="Провести синхронизацию" action="#{NSIRegistrySyncPage.performSync}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />

        </h:panelGrid>
    </rich:simpleTogglePanel>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGroup style="text-align: right">
        <h:inputTextarea readonly="true" cols="140" rows="40" value="#{NSIRegistrySyncPage.syncLog}"/>
    </h:panelGroup>

</h:panelGrid>
