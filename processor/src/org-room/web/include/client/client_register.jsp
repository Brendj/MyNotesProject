<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: chirikov
  Date: 30.07.13
  Time: 13:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .output-text-mod {
        font-family: Tahoma, Arial, Sans-Serif;
        font-size: 10pt;
        color: #000;
        white-space: nowrap;
        padding-right: 10px;
    }
</style>

<%-- Событие для добавления строки по изменению поля
<a4j:support event="onchange" actionListener="#{clientRegisterPage.doModifyClient}" reRender="clientsToRegister"/>
--%>


<%--@elvariable id="clientRegisterPage" type="ru.axetta.ecafe.processor.web.ui.client.ClientRegisterPage"--%>
<a4j:form>
<h:panelGrid id="clientRegisterGrid" binding="#{clientRegisterPage.pageComponent}" styleClass="borderless-grid">

    <a4j:region>
    <h:panelGrid columns="4" styleClass="borderless-grid">
        <a4j:commandButton image="/images/icon/plus.png" action="#{clientRegisterPage.doAddEmptyClient}"
                           reRender="clientsToRegister" styleClass="command-button" status="registerStatus" />
        <a4j:commandLink value="Добавить клиента" action="#{clientRegisterPage.doAddEmptyClient}"
                         reRender="clientsToRegister" styleClass="command-button" status="registerStatus" />

        <a4j:commandButton image="/images/icon/cancel.png" action="#{clientRegisterPage.doClearClients}" style="padding-left: 20px;"
                           reRender="clientsToRegister" styleClass="command-button" status="registerStatus" />
        <a4j:commandLink value="Очистить" action="#{clientRegisterPage.doClearClients}"
                         reRender="clientsToRegister" styleClass="command-button" status="registerStatus" />
    </h:panelGrid>


    <h:panelGrid columns="2" styleClass="borderless-grid">
        <rich:panel style="width:1000px; height: 500px; overflow:auto; background: none;">
            <rich:dataTable id="clientsToRegister" value="#{clientRegisterPage.clientsForRegister}"
                            var="client" rows="10" width="100%">

                <rich:column style="text-align: center; background-color: #{client.color};">
                    <a4j:commandButton image="/images/icon/cancel.png" action="#{clientRegisterPage.doRemoveClient(client)}" style="padding-bottom: 10px"
                                       reRender="clientsToRegister" styleClass="command-button" />
                </rich:column>
                <rich:column style="text-align: center; background-color: #{client.color};">
                    <f:facet name="header">
                        <h:outputText value="Фамилия"/>
                    </f:facet>
                    <h:inputText value="#{client.surname}" style="width:150px;" disabled="#{client.added}">
                    </h:inputText>
                </rich:column>
                <rich:column style="text-align: center; background-color: #{client.color};">
                    <f:facet name="header">
                        <h:outputText value="Имя"/>
                    </f:facet>
                    <h:inputText value="#{client.firstName}" style="width:150px;" disabled="#{client.added}">
                    </h:inputText>
                </rich:column>
                <rich:column style="text-align: center; background-color: #{client.color};">
                    <f:facet name="header">
                        <h:outputText value="Отчество"/>
                    </f:facet>
                    <h:inputText value="#{client.secondName}" style="width:150px;" disabled="#{client.added}">
                    </h:inputText>
                </rich:column>
                <rich:column style="text-align: center; background-color: #{client.color};">
                    <f:facet name="header">
                        <h:outputText value="Группа"/>
                    </f:facet>
                    <h:selectOneMenu id="clientGroup" value="#{client.clientGroup}" style="width:150px;" disabled="#{client.added}" >
                        <f:selectItems value="#{clientRegisterPage.groups}"/>
                    </h:selectOneMenu>
                </rich:column>
                <rich:column style="text-align: center; background-color: #{client.color};" rendered="#{clientRegisterPage.showAddress}">
                    <f:facet name="header">
                        <h:outputText value="Адрес"/>
                    </f:facet>
                    <h:inputText value="#{client.address}" style="width:150px;" disabled="#{client.added}">
                    </h:inputText>
                </rich:column>
                <rich:column style="text-align: center; background-color: #{client.color};" rendered="#{clientRegisterPage.showPhone}">
                    <f:facet name="header">
                        <h:outputText value="Телефон"/>
                    </f:facet>
                    <rich:jQuery selector="#phoneInput" query="mask('(999) 999-9999')" timing="immediate"/>
                    <h:inputText value="#{client.phone}" style="width:150px;" disabled="#{client.added}" id="phoneInput">
                    </h:inputText>
                </rich:column>
                <rich:column style="text-align: center; background-color: #{client.color};" rendered="#{clientRegisterPage.showMobile}">
                    <f:facet name="header">
                        <h:outputText value="Моб. телефон"/>
                    </f:facet>
                    <rich:jQuery selector="#mobileInput" query="mask('(999) 999-9999')" timing="immediate"/>
                    <h:inputText id="mobileInput" value="#{client.mobile}" style="width:150px;" disabled="#{client.added}">
                    </h:inputText>
                </rich:column>
                <rich:column style="text-align: center; background-color: #{client.color};" rendered="#{clientRegisterPage.showMobile}">
                    <f:facet name="header">
                        <h:outputText value="Уведомлять по СМС"/>
                    </f:facet>
                    <h:selectBooleanCheckbox value="#{client.notifyViaSMS}" styleClass="output-text" disabled="#{client.added}" >
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column style="text-align: center; background-color: #{client.color};" rendered="#{clientRegisterPage.showFax}">
                    <f:facet name="header">
                        <h:outputText value="Факс"/>
                    </f:facet>
                    <rich:jQuery selector="#faxInput" query="mask('(999) 999-9999')" timing="immediate"/>
                    <h:inputText id="faxInput" value="#{client.fax}" style="width:150px;" disabled="#{client.added}">
                    </h:inputText>
                </rich:column>
                <rich:column style="text-align: center; background-color: #{client.color};" rendered="#{clientRegisterPage.showEmail}">
                    <f:facet name="header">
                        <h:outputText value="E-mail"/>
                    </f:facet>
                    <h:inputText value="#{client.email}" style="width:150px;" disabled="#{client.added}">
                    </h:inputText>
                </rich:column>
                <rich:column style="text-align: center; background-color: #{client.color};" rendered="#{clientRegisterPage.showEmail}">
                    <f:facet name="header">
                        <h:outputText value="Уведомлять по E-mail"/>
                    </f:facet>
                    <h:selectBooleanCheckbox value="#{client.notifyViaEmail}" disabled="#{client.added}" styleClass="output-text" >
                    </h:selectBooleanCheckbox>
                </rich:column>
                <rich:column style="text-align: center; background-color: #{client.color};" rendered="#{clientRegisterPage.showRemarks}">
                    <f:facet name="header">
                        <h:outputText value="Примечание"/>
                    </f:facet>
                    <h:inputText value="#{client.remarks}" style="width:150px;" disabled="#{client.added}">
                    </h:inputText>
                </rich:column>


                <rich:subTable var="msg" value="#{client.messages}">
                    <rich:column colspan="13" style="text-align: center; background-color: #{client.color};"><h:outputText styleClass="output-text" value="#{msg.message}" /></rich:column>
                </rich:subTable>
            </rich:dataTable>
        </rich:panel>

        <rich:simpleTogglePanel label="Отображаемые поля" switchType="client" style="width: 200px"
                                opened="true" headerClass="filter-panel-header">
            <a4j:region>
            <h:panelGrid columns="2" styleClass="borderless-grid" style="padding-bottom: 20px">
                <h:outputText escape="true" value="Адрес" styleClass="output-text-mod" />
                <h:selectBooleanCheckbox value="#{clientRegisterPage.showAddress}" styleClass="output-text" >
                    <a4j:support event="onchange" reRender="clientsToRegister" />
                </h:selectBooleanCheckbox>
                <h:outputText escape="true" value="Телефон" styleClass="output-text-mod" />
                <h:selectBooleanCheckbox value="#{clientRegisterPage.showPhone}" styleClass="output-text" >
                    <a4j:support event="onchange" reRender="clientsToRegister" />
                </h:selectBooleanCheckbox>
                <h:outputText escape="true" value="Моб. телефон" styleClass="output-text-mod" />
                <h:selectBooleanCheckbox value="#{clientRegisterPage.showMobile}" styleClass="output-text" >
                    <a4j:support event="onchange" reRender="clientsToRegister" />
                </h:selectBooleanCheckbox>
                <h:outputText escape="true" value="Факс" styleClass="output-text-mod" />
                <h:selectBooleanCheckbox value="#{clientRegisterPage.showFax}" styleClass="output-text" >
                    <a4j:support event="onchange" reRender="clientsToRegister" />
                </h:selectBooleanCheckbox>
                <h:outputText escape="true" value="E-mail" styleClass="output-text-mod" />
                <h:selectBooleanCheckbox value="#{clientRegisterPage.showEmail}" styleClass="output-text" >
                    <a4j:support event="onchange" reRender="clientsToRegister" />
                </h:selectBooleanCheckbox>
                <h:outputText escape="true" value="Примечания" styleClass="output-text-mod" />
                <h:selectBooleanCheckbox value="#{clientRegisterPage.showRemarks}" styleClass="output-text" >
                    <a4j:support event="onchange" reRender="clientsToRegister" />
                </h:selectBooleanCheckbox>
            </h:panelGrid>
            </a4j:region>
        </rich:simpleTogglePanel>
    </h:panelGrid>
    </a4j:region>

    <rich:panel style="width:1000px; background: none">
    <table cellpadding="0" cellspacing="0">
        <tr>
            <td style="width: 99%">
                <h:panelGrid styleClass="borderless-grid" columns="2" style="width: 70%">
                    <h:selectBooleanCheckbox value="#{clientRegisterPage.registerTwins}" styleClass="output-text" ></h:selectBooleanCheckbox>
                    <h:outputText escape="true" value="Регистрировать клиентов с одинаковыми данными" styleClass="output-text-mod" />
                </h:panelGrid>
            </td>
            <td>
                <a4j:status id="registerStatus">
                    <f:facet name="start">
                        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                    </f:facet>
                </a4j:status>
            </td>
            <td>
                <a4j:commandButton value="Зарегистрировать" action="#{clientRegisterPage.doApply}" reRender="clientRegisterGrid" />
            </td>
        </tr>
    </table>
    </rich:panel>
</h:panelGrid>
</a4j:form>