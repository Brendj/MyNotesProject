<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="goodGroupViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupViewPage"--%>
<%--@elvariable id="goodGroupEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupEditPage"--%>
<h:panelGrid id="goodGroupViewPanelGrid" binding="#{goodGroupViewPage.pageComponent}"
             styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Наименование группы" styleClass="output-text" />
        <h:inputTextarea value="#{goodGroupViewPage.currentGoodGroup.nameOfGoodsGroup}" cols="128" rows="4" styleClass="input-text long-field" readonly="true" />
        <h:outputText escape="true" value="Организация поставщик" styleClass="output-text" />
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{goodGroupViewPage.currentOrg.shortName}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
            <f:setPropertyActionListener value="#{goodGroupViewPage.currentOrg.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
        </a4j:commandLink>
        <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text" />
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{goodGroupViewPage.currentConfigurationProvider.name}" action="#{configurationProviderViewPage.show}" styleClass="command-link">
            <f:setPropertyActionListener value="#{goodGroupViewPage.currentConfigurationProvider}" target="#{selectedConfigurationProviderGroupPage.selectConfigurationProvider}" />
        </a4j:commandLink>
        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{goodGroupViewPage.currentGoodGroup.deletedState}" readonly="true" disabled="true"/>
        <h:outputText escape="true" value="Товары (количество)" styleClass="output-text" />
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{goodGroupViewPage.countGoods}" action="#{goodGroupViewPage.showGoods}" styleClass="command-link"/>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Редактировать" action="#{goodGroupEditPage.show}"
                           reRender="workspaceTogglePanel, mainMenu" styleClass="command-button" />
    </h:panelGrid>
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>