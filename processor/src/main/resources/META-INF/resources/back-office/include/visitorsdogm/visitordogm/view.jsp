<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="visitorDogmViewPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmViewPage"--%>
<%--@elvariable id="visitorDogmEditPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmEditPage"--%>
<%--@elvariable id="visitorDogmCardEditPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardEditPage"--%>
<%--@elvariable id="visitorDogmCardViewPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardViewPage"--%>
<%--@elvariable id="visitorDogmCardGroupPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmCardGroupPage"--%>
<h:panelGrid id="visitorDogmViewGrid" binding="#{visitorDogmViewPage.pageComponent}" styleClass="borderless-grid">

     <h:panelGrid columns="2">

         <h:outputLabel escape="true" value="Фамилия" styleClass="output-text" />
         <h:inputText value="#{visitorDogmViewPage.visitorDogm.surname}" readonly="true" styleClass="input-text" />

         <h:outputLabel escape="true" value="Имя" styleClass="output-text" />
         <h:inputText value="#{visitorDogmViewPage.visitorDogm.firstName}" readonly="true" styleClass="input-text" />

         <h:outputLabel escape="true" value="Отчество" styleClass="output-text" />
         <h:inputText value="#{visitorDogmViewPage.visitorDogm.secondName}" readonly="true" styleClass="input-text" />

         <h:outputLabel escape="true" value="Должность" styleClass="output-text" />
         <h:inputText value="#{visitorDogmViewPage.visitorDogm.position}" styleClass="input-text" maxlength="128" />

         <h:outputLabel escape="true" value="Дата выдачи паспорт" styleClass="output-text" />
         <h:inputText value="#{visitorDogmViewPage.visitorDogm.passportDate}" converter="dateConverter" readonly="true" styleClass="input-text" />
         <h:outputLabel escape="true" value="Серийный номер паспорта" styleClass="output-text" />
         <h:inputText value="#{visitorDogmViewPage.visitorDogm.passportNumber}" readonly="true" styleClass="input-text" />

         <h:outputLabel escape="true" value="Дата выдачи водительского удостоверения" styleClass="output-text" />
         <h:inputText value="#{visitorDogmViewPage.visitorDogm.driverLicenceDate}" converter="dateConverter" readonly="true" styleClass="input-text" />
         <h:outputLabel escape="true" value="Серийный номер водительского удостоверения" styleClass="output-text" />
         <h:inputText value="#{visitorDogmViewPage.visitorDogm.driverLicenceNumber}" readonly="true" styleClass="input-text" />

         <h:outputLabel escape="true" value="Дата выдачи военного билета" styleClass="output-text" />
         <h:inputText value="#{visitorDogmViewPage.visitorDogm.warTicketDate}" converter="dateConverter" readonly="true" styleClass="input-text" />
         <h:outputLabel escape="true" value="Серийный номер военного билета" styleClass="output-text" />
         <h:inputText value="#{visitorDogmViewPage.visitorDogm.warTicketNumber}" readonly="true" styleClass="input-text" />

     </h:panelGrid>
    <rich:panel headerClass="workspace-panel-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Карты (#{visitorDogmViewPage.visitorDogm.countCardItems})" />
        </f:facet>
        <rich:dataTable id="visitorDogmCardTable" value="#{visitorDogmViewPage.visitorDogm.cardItems}" var="card"
                        rows="8"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column"
                        footerClass="data-table-footer">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Номер карты" />
                </f:facet>
                <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{visitorDogmCardViewPage.show}" styleClass="command-link">
                    <h:outputText escape="true" value="#{card.cardNo}" converter="cardNoConverter" styleClass="output-text" />
                    <f:setPropertyActionListener value="#{card}" target="#{visitorDogmCardGroupPage.currentCard}"/>
                </a4j:commandLink>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Дата создания" />
                </f:facet>
                <h:outputText escape="true" value="#{card.createDate}" converter="timeConverter" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Редактировать" />
                </f:facet>
                <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{visitorDogmCardEditPage.show}" styleClass="command-link">
                    <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                    <f:setPropertyActionListener value="#{card}" target="#{visitorDogmCardGroupPage.currentCard}"/>
                </a4j:commandLink>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="visitorDogmCardTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                                   stepControls="auto" boundaryControls="hide">
                    <f:facet name="previous">
                        <h:graphicImage value="/images/16x16/left-arrow.png" />
                    </f:facet>
                    <f:facet name="next">
                        <h:graphicImage value="/images/16x16/right-arrow.png" />
                    </f:facet>
                </rich:datascroller>
            </f:facet>
        </rich:dataTable>
    </rich:panel>

    <h:panelGrid styleClass="borderless-grid">
        <a4j:commandButton value="Редактировать" action="#{visitorDogmEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>