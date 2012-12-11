<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="goodGroupCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good.group.GoodGroupCreatePage"--%>
<h:panelGrid id="productGroupCreatePanelGrid" binding="#{goodGroupCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Организация поставщик" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{goodGroupCreatePage.shortName}" readonly="true" styleClass="input-text"
                         style="margin-right: 2px; width: 374px; float: left;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                               styleClass="command-link" style="width: 25px; float: right;" />
        </h:panelGroup>
        <h:outputText escape="true" value="Наименование группы" styleClass="output-text required-field" />
        <h:inputTextarea value="#{goodGroupCreatePage.goodGroup.nameOfGoodsGroup}" cols="128" rows="4" styleClass="input-text long-field" />

    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Создать группу" action="#{goodGroupCreatePage.onSave}"
                       reRender="goodGroupCreatePanelGrid" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>