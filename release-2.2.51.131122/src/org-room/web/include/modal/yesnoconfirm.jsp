<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<%--@elvariable id="yesNoConfirmPanel" type="ru.axetta.ecafe.processor.web.ui.modal.YesNoConfirmPanel"--%>
<rich:modalPanel id="yesNoConfirmPanel" width="250" height="150" resizeable="false" moveable="false">

    <f:facet name="header">
        <h:panelGroup>
            <h:outputText value="Внимание"></h:outputText>
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
        <h:panelGrid>
            <h:panelGrid>
                <h:outputText styleClass="output-text" value="#{yesNoConfirmPanel.message}" />
            </h:panelGrid>
            <h:panelGrid columns="2">
                <a4j:commandButton value="Да" action="#{yesNoConfirmPanel.doYes}"
                                   reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('yesNoConfirmPanel')}.hide();"
                                   styleClass="command-button" style="width: 80px;" rendered="#{empty yesNoConfirmPanel.nodePanel}"/>
                <a4j:commandButton value="Да" action="#{yesNoConfirmPanel.doYes}"
                                   reRender="#{yesNoConfirmPanel.nodePanel}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) { #{rich:component('yesNoConfirmPanel')}.hide(); #{rich:component(yesNoConfirmPanel.nodePanel)}.show(); }"
                                   styleClass="command-button" style="width: 80px;" rendered="#{not empty yesNoConfirmPanel.nodePanel}"/>
                <a4j:commandButton value="Нет" action="#{yesNoConfirmPanel.doNo}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('yesNoConfirmPanel')}.hide();"
                                   styleClass="command-button" style="width: 80px;"/>
            </h:panelGrid>
        </h:panelGrid>
        </a4j:region>
    </a4j:form>
</rich:modalPanel>