<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="technologicalMapViewPage" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.TechnologicalMapViewPage"--%>
<%--@elvariable id="productViewPage" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.ProductViewPage"--%>
<%--@elvariable id="productItemsPanel" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.productitemspanel"--%>
<h:panelGrid id="technologicalMapEditPanel" binding="#{technologicalMapViewPage.pageComponent}"
             styleClass="borderless-grid" columns="1">


    <h:panelGrid columns="2">

        <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text" />
        <h:commandLink value="#{technologicalMapViewPage.currentConfigurationProvider.name}" action="#{configurationProviderViewPage.show}" styleClass="command-link">
            <f:setPropertyActionListener value="#{technologicalMapViewPage.currentConfigurationProvider}" target="#{selectedConfigurationProviderGroupPage.selectConfigurationProvider}" />
        </h:commandLink>

        <h:outputText escape="true" value="Группа" styleClass="output-text" />
        <h:commandLink value="#{technologicalMapViewPage.currentTechnologicalMapGroup.nameOfGroup}" action="#{technologicalMapGroupViewPage.show}" styleClass="command-link"
                       style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px;">
            <f:setPropertyActionListener value="#{technologicalMapViewPage.currentTechnologicalMapGroup}" target="#{selectedTechnologicalMapGroupGroupPage.currentTechnologicalMapGroup}" />
        </h:commandLink>

        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{technologicalMapViewPage.currentTechnologicalMap.deletedState}" readonly="true" disabled="true"/>

        <h:outputText escape="true" value="Наименование технологической карты" styleClass="output-text" />
        <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.nameOfTechnologicalMap}" readonly="true" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Номер технологической карты" styleClass="output-text" />
        <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.numberOfTechnologicalMap}" readonly="true" maxlength="128" styleClass="input-text long-field" />

    </h:panelGrid>


    <rich:dataTable id="productsTable" value="#{technologicalMapViewPage.technologicalMapProducts}" var="technologicalMapProduct" >

        <f:facet name="header">
            <rich:columnGroup>
                <rich:column rowspan="2" headerClass="center-aligned-column column-header" width="200px">
                    <h:outputText escape="true" value="Наименование продукта" />
                </rich:column>
                <rich:column colspan="2" headerClass="center-aligned-column column-header">
                    <h:outputText value="Норма расхода продуктов на 1 порцию массой нетто 100 г" escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header"  breakBefore="true">
                    <h:outputText value="Масса брутто, г." escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header" >
                    <h:outputText value="Масса нетто, г." escape="true"/>
                </rich:column>
            </rich:columnGroup>
        </f:facet>
        <rich:column>
            <h:commandLink value="#{technologicalMapProduct.product.productName}" action="#{productViewPage.show}" styleClass="command-link">
                <f:setPropertyActionListener value="#{technologicalMapProduct.product}" target="#{selectedProductGroupPage.currentProduct}" />
            </h:commandLink>
        </rich:column>
        <rich:column>
            <h:inputText value="#{technologicalMapProduct.grossWeight}" readonly="true"/>
        </rich:column>
        <rich:column >
            <h:inputText value="#{technologicalMapProduct.netWeight}" readonly="true"/>
        </rich:column>
    </rich:dataTable>

    <h:panelGrid columns="2">
        <f:facet name="header">
            <h:outputText escape="true" value="В 100 граммах данного блюда содержится:" styleClass="output-text" />
        </f:facet>
        <h:panelGrid columns="3">
            <f:facet name="header">
                <h:outputText escape="true" value="Пищевые вещества, г" styleClass="output-text" />
            </f:facet>
            <h:outputText escape="true" value="Белки" styleClass="output-text" />
            <h:outputText escape="true" value="Жиры" styleClass="output-text" />
            <h:outputText escape="true" value="Углеводы" styleClass="output-text" />
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.proteins}" styleClass="input-text"
                         validatorMessage="Количсество белков должно быть числом." style="width: 4em" readonly="true"/>
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.fats}" styleClass="input-text"
                         validatorMessage="Количсество жиров должно быть числом." style="width: 4em" readonly="true"/>
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.carbohydrates}" styleClass="input-text"
                         validatorMessage="Количсество углеводов должно быть числом." style="width: 4em" readonly="true"/>
        </h:panelGrid>

        <h:panelGrid columns="4">
            <f:facet name="header">
                <h:outputText escape="true" value="Минеральные вещества, г" styleClass="output-text"/>
            </f:facet>
            <h:outputText escape="true" value="Ca" styleClass="output-text" />
            <h:outputText escape="true" value="Mg" styleClass="output-text" />
            <h:outputText escape="true" value="P" styleClass="output-text" />
            <h:outputText escape="true" value="Fe" styleClass="output-text" />

            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.microElCa}" styleClass="input-text"
                         validatorMessage="Количсество магния должно быть числом." style="width: 4em" readonly="true"/>
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.microElMg}" styleClass="input-text"
                         validatorMessage="Количсество фосфора должно быть числом." style="width: 4em" readonly="true"/>
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.microElP}" styleClass="input-text"
                         validatorMessage="Количсество фосфора должно быть числом." style="width: 4em" readonly="true"/>
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.microElFe}" styleClass="input-text"
                         validatorMessage="Количсество железа должно быть числом." style="width: 4em" readonly="true"/>
        </h:panelGrid>

        <h:panelGrid columns="6">
            <f:facet name="header">
                <h:outputText escape="true" value="Витамины, мг" styleClass="output-text"/>
            </f:facet>
            <h:outputText escape="true" value="A" styleClass="output-text" />
            <h:outputText escape="true" value="B1" styleClass="output-text" />
            <h:outputText escape="true" value="B2" styleClass="output-text" />
            <h:outputText escape="true" value="PP" styleClass="output-text" />
            <h:outputText escape="true" value="C" styleClass="output-text" />
            <h:outputText escape="true" value="E" styleClass="output-text" />
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.vitaminA}" styleClass="input-text"
                         validatorMessage="Количсество белков должно быть числом." style="width: 4em" readonly="true"/>
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.vitaminB1}" styleClass="input-text"
                         validatorMessage="Количсество жиров должно быть числом." style="width: 4em" readonly="true"/>
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.vitaminB2}" styleClass="input-text"
                         validatorMessage="Количсество углеводов должно быть числом." style="width: 4em" readonly="true"/>
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.vitaminPp}" styleClass="input-text"
                         validatorMessage="Количсество белков должно быть числом." style="width: 4em" readonly="true"/>
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.vitaminC}" styleClass="input-text"
                         validatorMessage="Количсество жиров должно быть числом." style="width: 4em" readonly="true"/>
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.vitaminE}" styleClass="input-text"
                         validatorMessage="Количсество углеводов должно быть числом." style="width: 4em" readonly="true"/>
        </h:panelGrid>

        <h:panelGrid columns="1">
            <f:facet name="header">
                <h:outputText escape="true" value="Энергетическая ценность (ккал)" styleClass="output-text"/>
            </f:facet>
            <h:outputText escape="true" value="" />
            <h:inputText value="#{technologicalMapViewPage.currentTechnologicalMap.energyValue}" styleClass="input-text"
                         validatorMessage="Количсество белков должно быть числом." style="width: 4em" readonly="true"/>
        </h:panelGrid>
    </h:panelGrid>

    <h:outputText value="Технология приготовления:" styleClass="output-text" escape="true"/>
    <h:inputTextarea value="#{technologicalMapViewPage.currentTechnologicalMap.technologyOfPreparation}" rows="3" readonly="true"/>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">

    <a4j:commandButton value="Редактировать" action="#{technologicalMapEditPage.show}"
                       reRender="workspaceTogglePanel, mainMenu" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>