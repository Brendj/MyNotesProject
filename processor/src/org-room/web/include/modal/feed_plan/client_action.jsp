<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<%--@elvariable id="clientFeedActionPanel" type="ru.axetta.ecafe.processor.web.ui.modal.feed_plan.ClientFeedActionPanel"--%>
<rich:modalPanel id="clientFeedActionPanel" width="400" height="150" resizeable="false" moveable="false" binding="#{clientFeedActionPanel.pageComponent}">
    <a4j:form>
        <a4j:region>
            <h:panelGrid columns="3">
                <a4j:commandButton value="БЛОК" action="#{clientFeedActionPanel.doBlock}"
                                   reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"
                                   styleClass="command-button" />
                <a4j:commandButton value="ОПЛАТА" action="#{clientFeedActionPanel.doPay}"
                                   reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"
                                   styleClass="command-button" />
                <a4j:commandButton value="СБРОС" action="#{clientFeedActionPanel.doRelease}"
                                   reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"
                                   styleClass="command-button" />
            </h:panelGrid>
            <h:panelGrid columns="3">
                <a4j:commandButton value="БЛОК" action="#{clientFeedActionPanel.doBlock}"
                                   reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"
                                   styleClass="command-button" />
                <a4j:commandButton value="ОПЛАТА" action="#{clientFeedActionPanel.doPay}"
                                   reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"
                                   styleClass="command-button" />
                <a4j:commandButton value="СБРОС" action="#{clientFeedActionPanel.doRelease}"
                                   reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('clientFeedActionPanel')}.hide();"
                                   styleClass="command-button" />
            </h:panelGrid>
        </a4j:region>
    </a4j:form>
</rich:modalPanel>