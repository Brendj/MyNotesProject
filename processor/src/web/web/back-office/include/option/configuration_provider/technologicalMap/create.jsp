<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="technologicalMapCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.TechnologicalMapCreatePage"--%>
<h:panelGrid id="technologicalMapCreatePanel" binding="#{technologicalMapCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="1">


    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text required-field" />
        <h:selectOneMenu id="selectCurrentConfigurationProvider" value="#{technologicalMapCreatePage.currentIdOfConfigurationProvider}" styleClass="input-text long-field" >
            <f:selectItems value="#{technologicalMapCreatePage.configurationProviderMenu.items}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Наименование технологической карты" styleClass="output-text required-field" />
        <h:inputText value="#{technologicalMapCreatePage.technologicalMap.nameOfTechnologicalMap}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Номер технологической карты" styleClass="output-text required-field" />
        <h:inputText value="#{technologicalMapCreatePage.technologicalMap.numberOfTechnologicalMap}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Группа технологической карты" styleClass="output-text required-field" />
        <h:selectOneMenu id="selectCurrentProductGroup" value="#{technologicalMapCreatePage.currentIdOfTechnologicalMapGroup}" styleClass="input-text long-field">
            <f:selectItems value="#{technologicalMapCreatePage.technologicalMapGroupMenu.items}" />
        </h:selectOneMenu>
    </h:panelGrid>

    <rich:dataTable id="productsTable" value="#{technologicalMapCreatePage.technologicalMapProducts}" var="technologicalMapProduct" >

        <f:facet name="header">
            <rich:columnGroup>
                <rich:column rowspan="2" headerClass="center-aligned-column column-header" width="200px">
                    <h:outputText escape="true" value="Наименование продукта" />
                </rich:column>
                <rich:column colspan="2" headerClass="center-aligned-column column-header">
                    <h:outputText value="Норма расхода продуктов на 1 порцию массой нетто 100 г" escape="true"/>
                </rich:column>
                <rich:column rowspan="2" headerClass="center-aligned-column column-header">
                    <h:outputText value="Удалить" escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header"  breakBefore="true">
                    <h:outputText value="Масса брутто, г." escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header" >
                    <h:outputText value="Масса нетто, г." escape="true"/>
                </rich:column>
            </rich:columnGroup>
        </f:facet>
        <rich:column >
            <h:outputText value="#{technologicalMapProduct.product.fullName}"/>
        </rich:column>
        <rich:column>
            <h:inputText value="#{technologicalMapProduct.grossWeight}"/>
        </rich:column>
        <rich:column >
            <h:inputText value="#{technologicalMapProduct.netWeight}"/>
        </rich:column>
        <rich:column style="text-align:center">
            <a4j:commandLink ajaxSingle="true" styleClass="command-link" action="#{technologicalMapCreatePage.deleteProduct}" reRender="productsTable">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{technologicalMapProduct}"
                                             target="#{technologicalMapCreatePage.currTechnologicalMapProduct}" />
            </a4j:commandLink>
        </rich:column>
    </rich:dataTable>

    <a4j:commandButton value="Добавить продукт"  action="#{technologicalMapCreatePage.showProducts}" reRender="modalTechnologicalMapListSelectorPanel"
                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalTechnologicalMapListSelectorPanel')}.show();"
                       styleClass="command-button"/>

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
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.proteins}" styleClass="input-text"
                         validatorMessage="Количсество белков должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.fats}" styleClass="input-text"
                         validatorMessage="Количсество жиров должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.carbohydrates}" styleClass="input-text"
                         validatorMessage="Количсество углеводов должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
        </h:panelGrid>

        <h:panelGrid columns="4">
            <f:facet name="header">
                <h:outputText escape="true" value="Минеральные вещества, г" styleClass="output-text"/>
            </f:facet>
            <h:outputText escape="true" value="Ca" styleClass="output-text" />
            <h:outputText escape="true" value="Mg" styleClass="output-text" />
            <h:outputText escape="true" value="P" styleClass="output-text" />
            <h:outputText escape="true" value="Fe" styleClass="output-text" />

            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.microElCa}" styleClass="input-text"
                         validatorMessage="Количсество магния должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.microElMg}" styleClass="input-text"
                         validatorMessage="Количсество фосфора должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.microElP}" styleClass="input-text"
                         validatorMessage="Количсество фосфора должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.microElFe}" styleClass="input-text"
                         validatorMessage="Количсество железа должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
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
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.vitaminA}" styleClass="input-text"
                         validatorMessage="Количсество белков должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.vitaminB1}" styleClass="input-text"
                         validatorMessage="Количсество жиров должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.vitaminB2}" styleClass="input-text"
                         validatorMessage="Количсество углеводов должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.vitaminPp}" styleClass="input-text"
                         validatorMessage="Количсество белков должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.vitaminC}" styleClass="input-text"
                         validatorMessage="Количсество жиров должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.vitaminE}" styleClass="input-text"
                         validatorMessage="Количсество углеводов должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
        </h:panelGrid>

        <h:panelGrid columns="1">
            <f:facet name="header">
                <h:outputText escape="true" value="Энергетическая ценность (ккал)" styleClass="output-text"/>
            </f:facet>
            <h:outputText escape="true" value="" />
            <h:inputText value="#{technologicalMapCreatePage.technologicalMap.energyValue}" styleClass="input-text"
                         validatorMessage="Количсество белков должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
        </h:panelGrid>
    </h:panelGrid>

    <h:outputText value="Технология приготовления:" styleClass="output-text" escape="true"/>
    <h:inputTextarea value="#{technologicalMapCreatePage.technologicalMap.technologyOfPreparation}" rows="10" cols="50"/>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Создать технологическую карту" action="#{technologicalMapCreatePage.createTechnologicalMap}"
                       reRender="technologicalMapCreatePanel" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>