<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<style>
.complexesTableCol1 {
    width: 70%;
}
.complexesTableCol2 {
    width: 30%;
}
</style>


<%--@elvariable id="disableComplexPanel" type="ru.axetta.ecafe.processor.web.ui.modal.feed_plan.DisableComplexPanel"--%>
<rich:modalPanel id="disableComplexPanel" width="300" height="400" resizeable="false" moveable="false" binding="#{disableComplexPanel.pageComponent}">
    <f:facet name="header">
        <h:panelGroup>
            <h:outputText value="Исключить комплекс"></h:outputText>
        </h:panelGroup>
    </f:facet>
    <f:facet name="controls">
        <h:panelGroup>
            <h:graphicImage value="images/icon/close.png" styleClass="hidelink" id="hidelink"/>
            <rich:componentControl for="disableComplexPanel" attachTo="hidelink" operation="hide" event="onclick"/>
        </h:panelGroup>
    </f:facet>

    <a4j:form>
        <a4j:region>
            <rich:panel id="claimsCalendar" style="height: 300px; width: 100%; overflow: auto;">
            <rich:dataTable id="complexesTable" value="#{disableComplexPanel.complexes}" columnClasses="complexesTableCol1,complexesTableCol2" var="complex" style="width: 100%">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText escape="false" styleClass="output-text" value="Комплекс"/>
                    </f:facet>
                    <h:outputText escape="false" styleClass="output-text" value="Комплекс №#{complex.complex}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText escape="false" styleClass="output-text" value="Отображать"/>
                    </f:facet>
                    <a4j:commandButton image="/images/icon/#{complex.icon}.png" styleClass="command-button" >
                        <a4j:support event="onclick" action="#{complex.doChangeDisabled}" reRender="complexesTable"/>
                    </a4j:commandButton>
                </rich:column>
            </rich:dataTable>
            </rich:panel>

            <h:panelGrid columns="3" columnClasses="submit_col1" style="width:100%;">
                <a4j:commandButton value="Принять" action="#{disableComplexPanel.doApply}"
                                   reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('disableComplexPanel')}.hide();"
                                   styleClass="command-button" style="width: 80px;"/>
                <a4j:commandButton value="Отменить" action="#{disableComplexPanel.doCancel}"
                                   reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('disableComplexPanel')}.hide();"
                                   styleClass="command-button" style="width: 80px;"/>
            </h:panelGrid>
        </a4j:region>
    </a4j:form>
</rich:modalPanel>