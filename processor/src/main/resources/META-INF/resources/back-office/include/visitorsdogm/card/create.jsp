<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="visitorDogmCardCreatePage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardCreatePage"--%>
<%--@elvariable id="visitorDogmSelectPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmSelectPage"--%>
<h:panelGrid id="visitorDogmCardCreateGrid" binding="#{visitorDogmCardCreatePage.pageComponent}" styleClass="borderless-grid">

     <h:panelGrid columns="2">

         <h:outputText escape="true" value="Сотрудник" styleClass="output-text" />
         <h:panelGroup styleClass="borderless-div">
             <h:inputText value="#{visitorDogmCardCreatePage.card.visitorItem.shortFullName}" readonly="true" styleClass="input-text"
                          style="margin-right: 2px;" />
             <a4j:commandButton value="..." action="#{visitorDogmSelectPage.show}" reRender="modalVisitorDogmSelectorPanel"
                                oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalVisitorDogmSelectorPanel')}.show();"
                                styleClass="command-link" style="width: 25px;"/>
         </h:panelGroup>

         <h:outputLabel escape="true" value="Номер карты" styleClass="output-text" />
         <h:inputText value="#{visitorDogmCardCreatePage.card.cardNo}" styleClass="input-text" />

         <h:outputLabel escape="true" value="Номер, нанесенный на карту" styleClass="output-text" />
         <h:inputText value="#{visitorDogmCardCreatePage.card.cardPrintedNo}" styleClass="input-text" />

     </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить" action="#{visitorDogmCardCreatePage.save}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>