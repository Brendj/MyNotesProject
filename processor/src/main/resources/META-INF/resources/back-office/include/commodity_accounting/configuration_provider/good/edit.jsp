<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="goodEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.GoodEditPage"--%>
<h:panelGrid id="goodEditPanelGrid" binding="#{goodEditPage.pageComponent}" styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">

        <h:outputText escape="true" value="Группа товаров" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:outputText value="#{goodEditPage.currentGoodGroup.nameOfGoodsGroup}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
            <a4j:commandButton value="..." action="#{goodEditPage.selectGoodGroup}" reRender="goodGroupSelectModalPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('goodGroupSelectModalPanel')}.show();"
                               styleClass="command-link" style="width: 25px; float: right;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Продукт" styleClass="output-text required-field" rendered="#{goodEditPage.currentTechnologicalMap==null}"/>
        <h:panelGroup styleClass="borderless-div" rendered="#{goodEditPage.currentTechnologicalMap==null}">
            <h:outputText value="#{goodEditPage.currentProduct.productName}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
            <a4j:commandButton value="..." action="#{goodEditPage.selectProduct}" reRender="productSelectModalPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('productSelectModalPanel')}.show();"
                               styleClass="command-link" style="width: 25px; float: right;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Технологическая карта" styleClass="output-text required-field" rendered="#{goodEditPage.currentProduct==null}"/>
        <h:panelGroup styleClass="borderless-div" rendered="#{goodEditPage.currentProduct==null}">
            <h:outputText value="#{goodEditPage.currentTechnologicalMap.nameOfTechnologicalMap}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
            <a4j:commandButton value="..." action="#{goodEditPage.selectTechnologicalMap}" reRender="technologicalMapSelectModalPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('technologicalMapSelectModalPanel')}.show();"
                               styleClass="command-link" style="width: 25px; float: right;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectOneListbox value="#{goodEditPage.currentGood.deletedState}" size="1">
            <f:selectItem itemLabel="Не удален" itemValue="false"/>
            <f:selectItem itemLabel="Удален" itemValue="true"/>
        </h:selectOneListbox>

        <h:outputText escape="true" value="Наименование" styleClass="output-text required-field" />
        <h:inputText value="#{goodEditPage.currentGood.nameOfGood}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Полное наименование пищевого продукта" styleClass="output-text" />
        <h:inputText value="#{goodEditPage.currentGood.fullName}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Код" styleClass="output-text" />
        <h:inputText value="#{goodEditPage.currentGood.goodsCode}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Единица измерения" styleClass="output-text" />
        <h:selectOneMenu value="#{goodEditPage.unitScale}">
            <f:selectItems value="#{goodEditPage.selectItemList}"/>
        </h:selectOneMenu>
        <h:outputText escape="true" value="Масса нетто" styleClass="output-text" />
        <h:inputText value="#{goodEditPage.currentGood.netWeight}" maxlength="32" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Срок годности (в минутах)" styleClass="output-text" />
        <h:inputText value="#{goodEditPage.currentGood.lifeTime}" maxlength="32" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Наценка" styleClass="output-text" />
        <h:inputText value="#{goodEditPage.currentGood.margin}" maxlength="32" styleClass="input-text long-field" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить" action="#{goodEditPage.onSave}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />

        <a4j:commandButton value="Восстановить" action="#{goodEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>