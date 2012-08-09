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
<%--@elvariable id="contractCreatePage" type="ru.axetta.ecafe.processor.web.ui.org.contract.ContractCreatePage"--%>
<h:panelGrid id="contractCreateGrid" binding="#{contractCreatePage.pageComponent}" styleClass="borderless-grid"
             columns="2">


    <h:outputText escape="true" value="Исполнитель" styleClass="output-text required-field" />
    <h:inputText value="#{contractCreatePage.currentEntity.performer}" maxlength="128" styleClass="input-text" required="true" requiredMessage="Введите данные исполнителя."/>
    <h:outputText escape="true" value="Заказчик" styleClass="output-text required-field" />
    <h:inputText value="#{contractCreatePage.currentEntity.customer}" maxlength="128" styleClass="input-text" required="true" requiredMessage="Введите данные заказчика."/>
    <h:outputText escape="true" value="Номер" styleClass="output-text required-field" />
    <h:inputText value="#{contractCreatePage.currentEntity.contractNumber}" maxlength="50" styleClass="input-text" required="true" requiredMessage="Введите номер контракта."/>
    <h:outputText escape="true" value="Статус" styleClass="output-text" />
    <h:selectOneMenu value="#{contractCreatePage.currentEntity.contractState}" styleClass="input-text">
        <f:selectItem itemLabel="Не активен" itemValue="false"/>
        <f:selectItem itemLabel="Активен" itemValue="true"/>
    </h:selectOneMenu>
    <h:outputText escape="true" value="Срок действия" styleClass="output-text required-field" />
    <rich:calendar value="#{contractCreatePage.currentEntity.dateOfClosing}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" required="true" requiredMessage="Укажите срок действия контракта."/>
    <h:outputText escape="true" value="Дата заключения" styleClass="output-text required-field" />
    <rich:calendar value="#{contractCreatePage.currentEntity.dateOfConclusion}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" required="true" requiredMessage="Укажите дату заключения контракта."/>
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton id="createContract"  eventsQueue="createContractEventsQueue"  value="Зарегистрировать новый контракт" action="#{contractCreatePage.save}"
                       styleClass="command-button" />
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>