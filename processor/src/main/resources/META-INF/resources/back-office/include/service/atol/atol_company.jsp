<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="atolCompanyPage" type="ru.axetta.ecafe.processor.web.ui.service.atol.AtolCompanyPage"--%>
<h:panelGrid id="atolCompanyPanel" binding="#{atolCompanyPage.pageComponent}">
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="E-mail организации" styleClass="output-text" />
        <h:inputText value="#{atolCompanyPage.atolCompany.emailOrg}" style="width:500px;" styleClass="input-text" />

        <h:outputText escape="true" value="Тип налогообложения" styleClass="output-text" />
        <h:inputText value="#{atolCompanyPage.atolCompany.taxType}" style="width:500px;" styleClass="input-text" />

        <h:outputText escape="true" value="ИНН организации" styleClass="output-text" />
        <h:inputText value="#{atolCompanyPage.atolCompany.inn}" style="width:500px;" styleClass="input-text" maxlength="12" />

        <h:outputText escape="true" value="Место расчетов" styleClass="output-text" />
        <h:inputText value="#{atolCompanyPage.atolCompany.place}" style="width:500px;" styleClass="input-text" />

        <h:outputText escape="true" value="E-mail отправки чеков" styleClass="output-text" />
        <h:inputText value="#{atolCompanyPage.atolCompany.emailCheck}" style="width:500px;" styleClass="input-text" />

        <h:outputText escape="true" value="Список контрагентов" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <a4j:commandButton value="..."
                               action="#{mainPage.showContragentSelectPageOwn(false)}"
                               reRender="modalContragentListSelectorPanel, atolCompanyPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="1" target="#{mainPage.contragentListSelectPage.classTypesString}" />
                <f:setPropertyActionListener value="#{atolCompanyPage.contragentIds}" target="#{mainPage.contragentListSelectPage.selectedIds}" />
            </a4j:commandButton>
            <h:outputText value=" {#{atolCompanyPage.contragentFilter}}" escape="true" styleClass="output-text" />
        </h:panelGroup>

        <a4j:commandButton value="Сохранить" action="#{atolCompanyPage.doSave}" status="updateStatus"
                           reRender="atolCompanyPanel" />
        <a4j:commandButton value="Восстановить" action="#{atolCompanyPage.doRefresh}" status="updateStatus"
                           reRender="atolCompanyPanel" />
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>