<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="goodGroupEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupEditPage"--%>
<h:panelGrid id="productGroupEditPanelGrid" binding="#{goodGroupEditPage.pageComponent}"
             styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Организация поставщик" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{goodGroupEditPage.shortName}" readonly="true" styleClass="input-text"
                         style="margin-right: 2px; width: 374px; float: left;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                               styleClass="command-link" style="width: 25px; float: right;">
                <f:setPropertyActionListener value="2" target="#{mainPage.orgSelectPage.filterMode}" />
            </a4j:commandButton>
        </h:panelGroup>
        <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:outputText value="#{goodGroupEditPage.currentConfigurationProvider.name}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
            <a4j:commandButton value="..." action="#{goodGroupEditPage.selectConfigurationProvider}" reRender="configurationProviderSelectModalPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('configurationProviderSelectModalPanel')}.show();"
                               styleClass="command-link" style="width: 25px; float: right;" />
        </h:panelGroup>
        <h:outputText escape="true" value="Наименование группы" styleClass="output-text required-field" />
        <h:inputTextarea value="#{goodGroupEditPage.currentGoodGroup.nameOfGoodsGroup}" cols="128" rows="4" styleClass="input-text long-field" />

        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectOneListbox value="#{goodGroupEditPage.currentGoodGroup.deletedState}" size="1">
            <f:selectItem itemLabel="Не удален" itemValue="false"/>
            <f:selectItem itemLabel="Удален" itemValue="true"/>
        </h:selectOneListbox>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <a4j:commandButton value="Сохранить группу" action="#{goodGroupEditPage.onSave}"
                           reRender="goodGroupEditPanelGrid, mainMenu" styleClass="command-button" />
    </h:panelGrid>
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>