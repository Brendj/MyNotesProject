<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<%--@elvariable id="SpbRegistrySynchErrorPage" type="ru.axetta.ecafe.processor.web.ui.service.spb.SpbRegistrySynchErrorPage"--%>
<rich:modalPanel id="SpbRegistrySynchErrorPage" width="450" height="280" resizeable="false" moveable="false" binding="#{SpbRegistrySynchErrorPage.pageComponent}">

    <f:facet name="header">
        <h:panelGroup>
            <h:outputText value="Оповещение об ошибке"></h:outputText>
        </h:panelGroup>
    </f:facet>
    <f:facet name="controls">
        <h:panelGroup>
            <h:graphicImage value="images/icon/close.png" styleClass="hidelink" id="hidelink"/>
            <rich:componentControl for="SpbRegistrySynchErrorPage" attachTo="hidelink" operation="hide" event="onclick"/>
        </h:panelGroup>
    </f:facet>

    <a4j:form>
        <a4j:region>
            <h:panelGrid styleClass="borderless-grid" id="errorCreateInfoPanel" style="padding-bottom: 5px;">
                <h:outputText escape="true" value="#{SpbRegistrySynchErrorPage.errorMessages}" rendered="#{not empty SpbRegistrySynchErrorPage.errorMessages}" styleClass="error-messages" style="font-size: 10pt;" />
                <h:outputText escape="true" value="#{SpbRegistrySynchErrorPage.infoMessages}" rendered="#{not empty SpbRegistrySynchErrorPage.infoMessages}" styleClass="info-messages" style="font-size: 10pt;" />
            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid" id="errorCreateDataPanel">
                <h:outputText escape="true" value="Ошибка" styleClass="output-text" />
                <h:selectOneListbox id="subscriptions"
                                    value="#{SpbRegistrySynchErrorPage.errorType}" style="width:400px;" size="5">
                    <f:selectItems value="#{SpbRegistrySynchErrorPage.errors}"/>
                </h:selectOneListbox>
                <h:outputText escape="true" value="Комментарий" styleClass="output-text" />
                <h:inputTextarea value="#{SpbRegistrySynchErrorPage.errorDetails}" style="width:400px; height: 100px"/>

                <a4j:status id="createStatus">
                    <f:facet name="start">
                        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                    </f:facet>
                </a4j:status>
                <h:panelGrid columns="2" styleClass="borderless-grid">
                    <a4j:commandButton value="Создать" action="#{SpbRegistrySynchErrorPage.doApply}"
                                       status="createStatus"
                                       reRender="errorCreateDataPanel, errorCreateInfoPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null && empty SpbRegistrySynchErrorPage.errorMessages}) #{rich:component('SpbRegistrySynchErrorPage')}.hide();"
                                       styleClass="command-button" style="width: 80px;"/>
                    <a4j:commandButton value="Закрыть" action="#{SpbRegistrySynchErrorPage.doClose}"
                                       reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('SpbRegistrySynchErrorPage')}.hide();"
                                       styleClass="command-button" style="width: 80px;">
                    </a4j:commandButton>
                </h:panelGrid>
            </h:panelGrid>
        </a4j:region>
    </a4j:form>
</rich:modalPanel>