<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<%--@elvariable id="groupCreatePanel" type="ru.axetta.ecafe.processor.web.ui.modal.group.GroupCreatePanel"--%>
<rich:modalPanel domElementAttachment="" id="groupCreatePanel" width="400" height="150" resizeable="false" moveable="false" binding="#{groupCreatePanel.pageComponent}">

    <%--<a4j:support event="onshow" ajaxSingle="true" action="#{groupCreatePanel.doOnShow}" reRender="groupCreateDataPanel, groupCreateInfoPanel" />
    <a4j:support event="onhide" ajaxSingle="true" action="#{groupCreatePanel.doOnShow}" reRender="groupCreateDataPanel, groupCreateInfoPanel" />--%>

    <f:facet name="header">
        <h:panelGroup>
            <h:outputText value="Добавление группы"></h:outputText>
        </h:panelGroup>
    </f:facet>
    <f:facet name="controls">
        <h:panelGroup>
            <h:graphicImage value="images/icon/close.png" styleClass="hidelink" id="hidelink"/>
            <rich:componentControl for="groupCreatePanel" attachTo="hidelink" operation="hide" event="onclick"/>
        </h:panelGroup>
    </f:facet>

    <a4j:form>
        <a4j:region>
            <h:panelGrid styleClass="borderless-grid" id="groupCreateInfoPanel" style="padding-bottom: 5px;">
                <h:outputText escape="true" value="#{groupCreatePanel.errorMessages}" rendered="#{not empty groupCreatePanel.errorMessages}" styleClass="error-messages" />
                <h:outputText escape="true" value="#{groupCreatePanel.infoMessages}" rendered="#{not empty groupCreatePanel.infoMessages}" styleClass="info-messages" />
            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid" id="groupCreateDataPanel">
                <h:outputText escape="true" value="Наименование группы" styleClass="output-text-mod" />
                <h:inputText id="createGroupInput" value="#{groupCreatePanel.groupName}" styleClass="output-text" style="width: 200px;" />

                <a4j:status id="groupCreateStatus">
                    <f:facet name="start">
                        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                    </f:facet>
                </a4j:status>
                <h:panelGrid columns="2" styleClass="borderless-grid">
                    <a4j:commandButton value="Создать" action="#{groupCreatePanel.doApply}"
                                       status="groupCreateStatus"
                                       reRender="groupCreateDataPanel, groupCreateInfoPanel"
                                       styleClass="command-button" style="width: 80px;"/>
                    <a4j:commandButton value="Закрыть" action="#{groupCreatePanel.doClose}"
                                       reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('groupCreatePanel')}.hide();"
                                       styleClass="command-button" style="width: 80px;">
                    </a4j:commandButton>
                </h:panelGrid>
            </h:panelGrid>
        </a4j:region>
    </a4j:form>
</rich:modalPanel>