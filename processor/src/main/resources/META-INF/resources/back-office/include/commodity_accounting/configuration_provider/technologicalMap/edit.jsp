<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="technologicalMapEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.TechnologicalMapEditPage"--%>
<%--@elvariable id="productItemsPanel" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.productitemspanel"--%>
<h:panelGrid id="technologicalMapEditPanel" binding="#{technologicalMapEditPage.pageComponent}"
             styleClass="borderless-grid" columns="1">


    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Группа технологической карты" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:outputText value="#{technologicalMapEditPage.currentTechnologicalMapGroup.nameOfGroup}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
            <a4j:commandButton value="..." action="#{technologicalMapEditPage.selectTechnologicalMapGroup}" reRender="technologicalMapGroupSelectModalPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('technologicalMapGroupSelectModalPanel')}.show();"
                               styleClass="command-link" style="width: 25px; float: right;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Наименование технологической карты" styleClass="output-text required-field" />
        <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.nameOfTechnologicalMap}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Номер технологической карты" styleClass="output-text required-field" />
        <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.numberOfTechnologicalMap}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Статус технологической карты" styleClass="output-text" />
        <h:selectOneListbox value="#{technologicalMapEditPage.currentTechnologicalMap.deletedState}" size="1">
            <f:selectItem itemLabel="Не удален" itemValue="false"/>
            <f:selectItem itemLabel="Удален" itemValue="true"/>
        </h:selectOneListbox>
    </h:panelGrid>

    <h:panelGrid>
        <rich:dataTable id="productsTableEdit" value="#{technologicalMapEditPage.technologicalMapProducts}" var="technologicalMapProduct" >

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
                <h:outputText value="#{technologicalMapProduct.product.productName}"/>
            </rich:column>
            <rich:column>
                <h:inputText value="#{technologicalMapProduct.grossWeight}"/>
            </rich:column>
            <rich:column >
                <h:inputText value="#{technologicalMapProduct.netWeight}"/>
            </rich:column>
            <rich:column style="text-align:center">
                <a4j:commandLink ajaxSingle="true" styleClass="command-link" action="#{technologicalMapEditPage.deleteProduct}" reRender="productsTableEdit">
                    <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                    <f:setPropertyActionListener value="#{technologicalMapProduct}"
                                                 target="#{technologicalMapEditPage.currentTechnologicalMapProduct}" />
                </a4j:commandLink>
            </rich:column>
        </rich:dataTable>

        <a4j:commandButton value="Добавить продукт"  action="#{technologicalMapEditPage.showProducts}" reRender="modalTechnologicalMapListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalTechnologicalMapListSelectorPanel')}.show();"
                           styleClass="command-button">
            <f:setPropertyActionListener value="#{technologicalMapEditPage.currentTechnologicalMap}"
                                         target="#{productItemsPanel.technologicalMap}" />
        </a4j:commandButton>
    </h:panelGrid>

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
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.proteins}" styleClass="input-text"
                         validatorMessage="Количсество белков должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.fats}" styleClass="input-text"
                         validatorMessage="Количсество жиров должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.carbohydrates}" styleClass="input-text"
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

            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.microElCa}" styleClass="input-text"
                         validatorMessage="Количсество кальция должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.microElMg}" styleClass="input-text"
                         validatorMessage="Количсество магния должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.microElP}" styleClass="input-text"
                         validatorMessage="Количсество фосфора должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.microElFe}" styleClass="input-text"
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
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.vitaminA}" styleClass="input-text"
                         validatorMessage="Количсество витамина А должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.vitaminB1}" styleClass="input-text"
                         validatorMessage="Количсество витамина B1 должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.vitaminB2}" styleClass="input-text"
                         validatorMessage="Количсество витамина B2 должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.vitaminPp}" styleClass="input-text"
                         validatorMessage="Количсество витамина PP должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.vitaminC}" styleClass="input-text"
                         validatorMessage="Количсество витамина C должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.vitaminE}" styleClass="input-text"
                         validatorMessage="Количсество витамина E должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
        </h:panelGrid>

        <h:panelGrid columns="1">
            <f:facet name="header">
                <h:outputText escape="true" value="Энергетическая ценность (ккал)" styleClass="output-text"/>
            </f:facet>
            <h:outputText escape="true" value="" />
            <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.energyValue}" styleClass="input-text"
                         validatorMessage="Количсество килокаллорий должно быть числом." style="width: 4em">
                <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
            </h:inputText>
        </h:panelGrid>
    </h:panelGrid>

    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Срок годности (мин.)" styleClass="output-text"/>
        <h:inputText value="#{technologicalMapEditPage.currentTechnologicalMap.lifeTime}" styleClass="input-text"
                     validatorMessage="Число должно быть целым.">
            <f:validateDoubleRange minimum="0" maximum="99999999" />
        </h:inputText>
    </h:panelGrid>

    <h:outputText value="Технология приготовления:" styleClass="output-text" escape="true"/>
    <h:inputTextarea value="#{technologicalMapEditPage.currentTechnologicalMap.technologyOfPreparation}" rows="10" cols="50" />

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid" columns="2">
    <a4j:commandButton value="Сохранить" action="#{technologicalMapEditPage.save}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{technologicalMapEditPage.show}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>