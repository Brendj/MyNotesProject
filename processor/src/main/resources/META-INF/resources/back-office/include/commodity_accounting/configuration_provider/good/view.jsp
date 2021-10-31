<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="goodViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.GoodViewPage"--%>
<%--@elvariable id="goodEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.GoodEditPage"--%>
<%--@elvariable id="goodGroupViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupViewPage"--%>
<%--@elvariable id="selectedGoodGroupGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.SelectedGoodGroupGroupPage"--%>
<h:panelGrid id="goodViewPanelGrid" binding="#{goodViewPage.pageComponent}" styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">

        <h:outputText escape="true" value="Группа товаров" styleClass="output-text" />
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{goodViewPage.currentGood.goodGroup.nameOfGoodsGroup}" action="#{goodGroupViewPage.show}" styleClass="command-link"
                       style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px;">
            <f:setPropertyActionListener value="#{goodViewPage.currentGood.goodGroup}" target="#{selectedGoodGroupGroupPage.currentGoodGroup}" />
        </a4j:commandLink>

        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{goodViewPage.currentGood.deletedState}" readonly="true" disabled="true"/>

        <h:outputText escape="true" value="Продукт" styleClass="output-text required-field" rendered="#{goodViewPage.currentProduct!=null}"/>
        <h:inputText value="#{goodViewPage.currentProduct.productName}" maxlength="128" readonly="true" styleClass="input-text long-field" rendered="#{goodViewPage.currentProduct!=null}"/>

        <h:outputText escape="true" value="Технологическая карта" styleClass="output-text required-field" rendered="#{goodViewPage.currentTechnologicalMap!=null}"/>
        <h:inputText value="#{goodViewPage.currentTechnologicalMap.nameOfTechnologicalMap}" maxlength="128" readonly="true" styleClass="input-text long-field" rendered="#{goodViewPage.currentTechnologicalMap!=null}"/>

        <h:outputText escape="true" value="Наименование" styleClass="output-text required-field" />
        <h:inputText value="#{goodViewPage.currentGood.nameOfGood}" maxlength="128" readonly="true" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Полное наименование пищевого продукта" styleClass="output-text" />
        <h:inputText value="#{goodViewPage.currentGood.fullName}" maxlength="128" readonly="true" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Код" styleClass="output-text" />
        <h:inputText value="#{goodViewPage.currentGood.goodsCode}" maxlength="128" readonly="true" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Единица измерения" styleClass="output-text" />
        <h:inputText value="#{goodViewPage.currentGood.unitsScale}" maxlength="128" readonly="true" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Масса нетто" styleClass="output-text" />
        <h:inputText value="#{goodViewPage.currentGood.netWeight}" maxlength="32" readonly="true" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Срок годности (в минутах)" styleClass="output-text" />
        <h:inputText value="#{goodViewPage.currentGood.lifeTime}" maxlength="32" readonly="true" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Наценка" styleClass="output-text" />
        <h:inputText value="#{goodViewPage.currentGood.margin}" maxlength="32" readonly="true" styleClass="input-text long-field" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="1">

        <a4j:commandButton value="Редактировать" action="#{goodEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
    </h:panelGrid>

    <a4j:status id="sGoodViewPageStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
