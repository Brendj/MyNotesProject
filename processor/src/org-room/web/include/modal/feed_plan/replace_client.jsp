<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<style>
.selected {
    background-color: #CCDBFF;
    cursor: pointer;
}
.disabled {
    background-color: #E3E3E3;
    cursor: default;
}
.replaceClientPanel_control1 {
    width: 1%
}
.replaceClientPanel_control2 {
    width: 98%
}
.replaceClientPanel_control3 {
    width: 1%
}
.output-text-replacement {
    font-size: 8pt;
    padding-left: 50px;
}
</style>


<%--@elvariable id="replaceClientPanel" type="ru.axetta.ecafe.processor.web.ui.modal.feed_plan.ReplaceClientPanel"--%>
<rich:modalPanel id="replaceClientPanel" width="550" height="400" resizeable="false" moveable="false" binding="#{replaceClientPanel.pageComponent}">
    <f:facet name="header">
        <h:panelGroup>
            <h:outputText value="Выбор замены для #{replaceClientPanel.nameOfTargetClient}"></h:outputText>
        </h:panelGroup>
    </f:facet>
    <%--<f:facet name="controls">
        <h:panelGroup>
            <h:graphicImage value="images/icon/close.png" styleClass="hidelink" id="hidelink"/>
            <rich:componentControl for="orderRegistrationResultPanel" attachTo="hidelink" operation="hide" event="onclick"/>
        </h:panelGroup>
    </f:facet>--%>

    <a4j:form>
        <a4j:region>
            <rich:panel id="replacePanel" style="height: 300px; width: 100%; overflow: auto;">
                <rich:dataTable id="replaceTable" value="#{replaceClientPanel.replaceClients}" var="rCl" style="width: 100%">
                    <rich:column styleClass="#{replaceClientPanel.getStyleClass(rCl)}">
                        <f:facet name="header">
                            <h:outputText escape="false" styleClass="output-text" value="ID"/>
                        </f:facet>
                        <h:outputText escape="false" styleClass="output-text" value="#{rCl.idofclient}"/>
                        <a4j:support event="onclick" action="#{replaceClientPanel.doSelect(rCl)}" reRender="replaceTable"/>
                    </rich:column>
                    <rich:column styleClass="#{replaceClientPanel.getStyleClass(rCl)}">
                        <f:facet name="header">
                            <h:outputText escape="false" styleClass="output-text" value="ФИО"/>
                        </f:facet>
                        <h:panelGrid>
                        <h:outputText escape="false" styleClass="output-text" value="#{rCl.fullName}"/>
                        <h:outputText escape="false" styleClass="output-text-replacement"
                                      rendered="#{not empty replaceClientPanel.getReplaceTargetName(rCl)}"
                                      value="заменяет #{replaceClientPanel.getReplaceTargetName(rCl)}"/>
                        </h:panelGrid>
                        <a4j:support event="onclick" action="#{replaceClientPanel.doSelect(rCl)}" reRender="replaceTable"/>
                    </rich:column>
                </rich:dataTable>
            </rich:panel>

            <h:panelGrid columns="3" style="width:100%;" columnClasses="replaceClientPanel_control1,replaceClientPanel_control2,replaceClientPanel_control3">
                <a4j:status id="replaceClientStatus">
                    <f:facet name="start">
                        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                    </f:facet>
                </a4j:status>
                <a4j:commandButton value="Сбросить" action="#{replaceClientPanel.doReset}" reRender="replacePanel" status="replaceClientStatus"
                                   styleClass="command-button" style="width: 80px;"/>
                <a4j:commandButton value="Принять" action="#{replaceClientPanel.doClose}" status="replaceClientStatus"
                                   reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('replaceClientPanel')}.hide();"
                                   styleClass="command-button" style="width: 80px;"/>
            </h:panelGrid>
        </a4j:region>
    </a4j:form>
</rich:modalPanel>