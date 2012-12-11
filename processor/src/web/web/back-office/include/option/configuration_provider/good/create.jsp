<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="goodCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good.GoodCreatePage"--%>
<h:panelGrid id="goodCreatePanelGrid" binding="#{goodCreatePage.pageComponent}" styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Группа товаров" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:outputText value="#{goodCreatePage.currentGoodGroup.nameOfGoodsGroup}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
            <a4j:commandButton value="..." action="#{goodCreatePage.selectGoodGroup}" reRender="goodGroupSelectModalPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('goodGroupSelectModalPanel')}.show();"
                               styleClass="command-link" style="width: 25px; float: right;" />

        </h:panelGroup>

        <h:outputText escape="true" value="Продукт" styleClass="output-text required-field" rendered="#{goodCreatePage.currentTechnologicalMap==null}"/>
        <h:panelGroup styleClass="borderless-div" rendered="#{goodCreatePage.currentTechnologicalMap==null}">
            <h:outputText value="#{goodCreatePage.currentProduct.productName}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
            <a4j:commandButton value="..." action="#{goodCreatePage.selectProduct}" reRender="productSelectModalPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('productSelectModalPanel')}.show();"
                               styleClass="command-link" style="width: 25px; float: right;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Технологическая карта" styleClass="output-text required-field" rendered="#{goodCreatePage.currentProduct==null}"/>
        <h:panelGroup styleClass="borderless-div" rendered="#{goodCreatePage.currentProduct==null}">
            <h:outputText value="#{goodCreatePage.currentTechnologicalMap.nameOfTechnologicalMap}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
            <a4j:commandButton value="..." action="#{goodCreatePage.selectTechnologicalMap}" reRender="technologicalMapSelectModalPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('technologicalMapSelectModalPanel')}.show();"
                               styleClass="command-link" style="width: 25px; float: right;" />
        </h:panelGroup>


        <h:outputText escape="true" value="Наименование" styleClass="output-text required-field" />
        <h:inputText value="#{goodCreatePage.good.nameOfGood}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Полное наименование пищевого продукта" styleClass="output-text" />
        <h:inputText value="#{goodCreatePage.good.fullName}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Код" styleClass="output-text" />
        <h:inputText value="#{goodCreatePage.good.goodsCode}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Единица измерения" styleClass="output-text" />
        <h:selectOneMenu value="#{goodCreatePage.good.unitsScale}">
            <f:selectItems value="#{goodCreatePage.selectItemList}"/>
        </h:selectOneMenu>
        <h:outputText escape="true" value="Масса нетто" styleClass="output-text" />
        <h:inputText value="#{goodCreatePage.good.netWeight}" maxlength="32" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Срок годности (в минутах)" styleClass="output-text" />
        <h:inputText value="#{goodCreatePage.good.lifeTime}" maxlength="32" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Наценка" styleClass="output-text" />
        <h:inputText value="#{goodCreatePage.good.margin}" maxlength="32" styleClass="input-text long-field" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <a4j:commandButton value="Создать продукт" action="#{goodCreatePage.onSave}"
                           reRender="goodCreatePanelGrid" styleClass="command-button" />
    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>