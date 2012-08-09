<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditOrgs())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания организации --%>
<%--@elvariable id="contractViewPage" type="ru.axetta.ecafe.processor.web.ui.org.contract.ContractViewPage"--%>
<%--@elvariable id="contractEditPage" type="ru.axetta.ecafe.processor.web.ui.org.contract.ContractEditPage"--%>
<h:panelGrid id="contractViewGrid" binding="#{contractViewPage.pageComponent}" styleClass="borderless-grid"
             columns="2">


    <h:outputText escape="true" value="Исполнитель" styleClass="output-text" />
    <h:inputText value="#{contractViewPage.currentEntity.performer}" maxlength="128" styleClass="input-text long-field" readonly="true" disabled="true"/>
    <h:outputText escape="true" value="Заказчик" styleClass="output-text" />
    <h:inputText value="#{contractViewPage.currentEntity.customer}" maxlength="128" styleClass="input-text long-field" readonly="true" disabled="true"/>
    <h:outputText escape="true" value="Номер" styleClass="output-text" />
    <h:inputText value="#{contractViewPage.currentEntity.contractNumber}" maxlength="50" styleClass="input-text long-field" readonly="true" disabled="true"/>
    <h:outputText escape="true" value="Организации" styleClass="output-text" />
    <h:outputText styleClass="output-text" id="categoryOrgEditFilter" escape="true" value=" {#{contractEditPage.currentEntity.orgNames}}" />
    <h:outputText escape="true" value="Статус" styleClass="output-text" />
    <h:inputText value="#{contractViewPage.currentEntity.contractState==true?'Активен':'Не активен'}" maxlength="50" styleClass="input-text long-field" readonly="true" disabled="true"/>
    <h:outputText escape="true" value="Дата заключения" styleClass="output-text" />
    <h:inputText readonly="true" disabled="true" value="#{contractViewPage.currentEntity.dateOfConclusion}" styleClass="input-text long-field">
        <f:convertDateTime pattern="dd.MM.yyyy"/>
    </h:inputText>
    <h:outputText escape="true" value="Срок действия" styleClass="output-text" />
    <h:inputText readonly="true" disabled="true" value="#{contractViewPage.currentEntity.dateOfClosing}" styleClass="input-text long-field">
        <f:convertDateTime pattern="dd.MM.yyyy"/>
    </h:inputText>
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{contractEditPage.show}"
                       styleClass="command-button" reRender="mainMenu, workspaceTogglePanel" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>