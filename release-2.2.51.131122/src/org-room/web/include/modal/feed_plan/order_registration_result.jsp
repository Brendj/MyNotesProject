<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<style>
.orderRegistrationResultPanel_close1 {
    width: 99%;
}
.orderRegistrationResultPanel_close2 {
    width: 1%;
}

.successMessage {
    background-color: #CCFFCC;
}
.failMessage {
    background-color: #FFCCCC;
}

.messagesTable_col1 {
    width: 10%;
}
.messagesTable_col2{
    width: 30%;
}
.messagesTable_col3 {
    width: 60%;
}
</style>


<%--@elvariable id="orderRegistrationResultPanel" type="ru.axetta.ecafe.processor.web.ui.modal.feed_plan.OrderRegistrationResultPanel"--%>
<rich:modalPanel id="orderRegistrationResultPanel" width="750" height="400" resizeable="false" moveable="false" binding="#{orderRegistrationResultPanel.pageComponent}">
    <f:facet name="header">
        <h:panelGroup>
            <h:outputText value="Оплата"></h:outputText>
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
            <rich:panel id="claimsCalendar" style="height: 300px; width: 100%; overflow: auto;">
                <rich:dataTable id="messagesTable" value="#{orderRegistrationResultPanel.infos}" var="info" style="width: 100%"
                        columnClasses="messagesTable_col1,messagesTable_col2,messagesTable_col3">
                    <rich:column styleClass="#{info.styleClass}">
                        <f:facet name="header">
                            <h:outputText escape="false" styleClass="output-text" value="ID"/>
                        </f:facet>
                        <h:outputText escape="false" styleClass="output-text" value="#{info.idofclient}"/>
                    </rich:column>
                    <rich:column styleClass="#{info.styleClass}">
                        <f:facet name="header">
                            <h:outputText escape="false" styleClass="output-text" value="ФИО"/>
                        </f:facet>
                        <h:outputText escape="false" styleClass="output-text" value="#{info.name}"/>
                    </rich:column>
                    <rich:column styleClass="#{info.styleClass}">
                        <f:facet name="header">
                            <h:outputText escape="false" styleClass="output-text" value="Результат"/>
                        </f:facet>
                        <h:outputText escape="false" styleClass="output-text" value="#{info.message}"/>
                    </rich:column>
                </rich:dataTable>
            </rich:panel>

            <h:panelGrid columns="2" columnClasses="orderRegistrationResultPanel_close1,orderRegistrationResultPanel_close2" style="width:100%;">
                <h:outputText value="Всего #{orderRegistrationResultPanel.totalCount}, из них оплачено #{orderRegistrationResultPanel.successCount}, с ошибками #{orderRegistrationResultPanel.failCount}" styleClass="output-text"/>
                <a4j:commandButton value="Закрыть" action="#{orderRegistrationResultPanel.doClose}"
                                   reRender="#{mainPage.currentWorkspacePage.pageComponent.id}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('orderRegistrationResultPanel')}.hide();"
                                   styleClass="command-button" style="width: 80px;"/>
            </h:panelGrid>
        </a4j:region>
    </a4j:form>
</rich:modalPanel>