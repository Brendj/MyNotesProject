<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<h:panelGrid id="technologicalMapCreatePanel" binding="#{technologicalMapCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="1">


    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Наименование технологической карты" styleClass="output-text" />
        <h:inputText value="#{technologicalMapCreatePage.technologicalMap.nameOfTechnologicalMap}" maxlength="128" styleClass="input-text long-field" />
    </h:panelGrid>
    <%--<h:panelGrid columns="1">--%>
        <%--<h:outputText escape="true" value="Продукт" styleClass="output-text" />--%>

    <%--</h:panelGrid>--%>

    <rich:modalPanel id="technologicalMapProductModalPanel" autosized="true" width="200" headerClass="modal-panel-header">
        <f:facet name="header">
            <h:outputText value="Выберите продукты" styleClass="output-text" />
        </f:facet>
        <a4j:form id="technologicalMapProductModalForm" styleClass="borderless-form"
                  eventsQueue="technologicalMapProductSelectorFormEventsQueue">
                <rich:dataTable value="#{technologicalMapCreatePage.products}" var="item" rowKeyVar="row">
                    <rich:column headerClass="column-header">
                        <f:facet name="header">
                            <h:outputText escape="true" value="№" />
                        </f:facet>
                        <h:outputText value="#{row+1}"/>
                    </rich:column>
                     <rich:column headerClass="column-header">
                         <f:facet name="header">
                             <h:outputText escape="true" value="" />
                         </f:facet>
                         <h:selectBooleanCheckbox value="#{item.checked}"/>
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <f:facet name="header">
                            <h:outputText escape="true" value="Наименование продукта" />
                        </f:facet>
                        <h:outputText value="#{item.product.productName}"/>
                    </rich:column>
                </rich:dataTable>
            <h:panelGrid columns="2">
                <a4j:commandButton value="Добавить" action="#{technologicalMapCreatePage.addProducts}"
                                   reRender="productsTable"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('technologicalMapProductModalPanel')}.hide();"
                                   styleClass="command-button"/>

                <a4j:commandButton value="Отмена" styleClass="command-button"
                                   onclick="#{rich:component('technologicalMapProductModalPanel')}.hide();return false;" />
            </h:panelGrid>
        </a4j:form>
    </rich:modalPanel>

    <rich:dataTable id="productsTable" value="#{technologicalMapCreatePage.technologicalMap.technologicalMapProduct}" var="item" >
        <rich:column headerClass="column-header" >
            <f:facet name="header">
                <h:outputText escape="true" value="Наименование продукта" />
            </f:facet>

            <h:panelGrid columns="1">
                <%--combobox with autocomplete--%>
                <%--[combobox with autocomplete]--%>
                <%--[combobox with autocomplete]--%>
            </h:panelGrid>
        </rich:column>
        <rich:column headerClass="column-header" >
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" />
            </f:facet>
            <%--pict with action delete product--%>
        </rich:column>
    </rich:dataTable>
    <a4j:commandButton value="Добавить продукт"  action="#{technologicalMapCreatePage.showProducts}" reRender="technologicalMapProductModalPanel"
                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('technologicalMapProductModalPanel')}.show();"
                       styleClass="command-button"/>

    <h:panelGrid columns="3">
        <f:facet name="">
            <h:outputText escape="true" value="Пищевые вещества, г" />
        </f:facet>
        <h:outputText escape="true" value="Белки" styleClass="output-text" />
        <h:outputText escape="true" value="Жиры" styleClass="output-text" />
        <h:outputText escape="true" value="Углеводы" styleClass="output-text" />
        <h:inputText value="#{technologicalMapCreatePage.technologicalMap.proteins}" styleClass="input-text"
                validatorMessage="Количсество белков должно быть числом.">
            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
        </h:inputText>
        <h:inputText value="#{technologicalMapCreatePage.technologicalMap.fats}" styleClass="input-text"
                validatorMessage="Количсество жиров должно быть числом.">
            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
        </h:inputText>
        <h:inputText value="#{technologicalMapCreatePage.technologicalMap.carbohydrates}" styleClass="input-text"
                    validatorMessage="Количсество углеводов должно быть числом.">
            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
        </h:inputText>
    </h:panelGrid>

    <h:panelGrid columns="4">
        <f:facet name="">
            <h:outputText escape="true" value="Минеральные вещества, г" />
        </f:facet>
        <h:outputText escape="true" value="Ca" styleClass="output-text" />
        <h:outputText escape="true" value="Mg" styleClass="output-text" />
        <h:outputText escape="true" value="P" styleClass="output-text" />
        <h:outputText escape="true" value="Fe" styleClass="output-text" />

        <h:inputText value="#{technologicalMapCreatePage.technologicalMap.microElCa}" styleClass="input-text"
                validatorMessage="Количсество магния должно быть числом.">
            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
        </h:inputText>
        <h:inputText value="#{technologicalMapCreatePage.technologicalMap.microElMg}" styleClass="input-text"
                validatorMessage="Количсество фосфора должно быть числом.">
            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
        </h:inputText>
        <h:inputText value="#{technologicalMapCreatePage.technologicalMap.microElP}" styleClass="input-text"
                    validatorMessage="Количсество фосфора должно быть числом.">
            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
        </h:inputText>
        <h:inputText value="#{technologicalMapCreatePage.technologicalMap.microElFe}" styleClass="input-text"
                    validatorMessage="Количсество железа должно быть числом.">
            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
        </h:inputText>

    </h:panelGrid>

    <h:inputTextarea value="#{technologicalMapCreatePage.technologicalMap.technologyOfPreparation}" rows="3" />


    <%--<h:outputText escape="true" value="Массва брутто (г)" styleClass="output-text" />--%>
    <%--<h:inputText value="#{mainPage.productCreatePage.grossMass}" maxlength="32" styleClass="input-text"--%>
            <%--validatorMessage="Масса брутто должно быть числом.">--%>
            <%--<f:validateDoubleRange minimum="0.00" maximum="99999999.00" />--%>
    <%--</h:inputText>--%>

    <%--<h:outputText escape="true" value="Массва нетто (г)" styleClass="output-text" />--%>
    <%--<h:inputText value="#{mainPage.productCreatePage.netMass}" maxlength="32" styleClass="input-text"--%>
            <%--validatorMessage="Масса нетто должно быть числом.">--%>
            <%--<f:validateDoubleRange minimum="0.00" maximum="99999999.00" />--%>
    <%--</h:inputText>--%>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Создать технологическую карту" action="#{technologicalMapCreatePage.createTechnologicalMap}"
                       reRender="technologicalMapCreatePanel" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>