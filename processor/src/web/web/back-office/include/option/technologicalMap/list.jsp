<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<h:panelGrid id="technologicalMapListPanel" binding="#{technologicalMapListPage.pageComponent}"
             styleClass="borderless-grid" columns="1">
            <h:outputText value="Техническая карта"/>
            <rich:dataGrid id="technologicalMapListTable" value="#{technologicalMapListPage.technologicalMapList}" var="technologicalMap" columns="5" elements="5" width="600px">
                <rich:panel bodyClass="pbody">
                    <f:facet name="header">
                        <h:outputText value="#{technologicalMap.nameOfTechnologicalMap} - №#{technologicalMap.numberOfTechnologicalMap}"/>
                    </f:facet>
                    <h:dataTable value="#{technologicalMap.technologicalMapProduct}" var="technologicalMapProduct">
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Наименование продукта"/>
                            </f:facet>
                            <h:outputText value="#{technologicalMapProduct.nameOfProduct}" styleClass="output-text"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="масса брутто"/>
                            </f:facet>
                            <h:outputText value="#{technologicalMapProduct.grossWeight}" styleClass="output-text"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="масса нетто"/>
                            </f:facet>
                            <h:outputText value="#{technologicalMapProduct.netWeight}" styleClass="output-text"/>
                        </h:column>
                    </h:dataTable>


                    <h:panelGrid columns="3">
                        <f:facet name="header">
                            <h:outputText escape="true" value="Пищевые вещества, г" />
                        </f:facet>
                        <h:outputText escape="true" value="Белки" styleClass="output-text" />
                        <h:outputText escape="true" value="Жиры" styleClass="output-text" />
                        <h:outputText escape="true" value="Углеводы" styleClass="output-text" />
                        <h:inputText value="#{technologicalMap.proteins}" styleClass="input-text"
                                     validatorMessage="Количсество белков должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                        <h:inputText value="#{technologicalMap.fats}" styleClass="input-text"
                                     validatorMessage="Количсество жиров должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                        <h:inputText value="#{technologicalMap.carbohydrates}" styleClass="input-text"
                                     validatorMessage="Количсество углеводов должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                    </h:panelGrid>

                    <h:panelGrid columns="4">
                        <f:facet name="header">
                            <h:outputText escape="true" value="Минеральные вещества, г" />
                        </f:facet>
                        <h:outputText escape="true" value="Ca" styleClass="output-text" />
                        <h:outputText escape="true" value="Mg" styleClass="output-text" />
                        <h:outputText escape="true" value="P" styleClass="output-text" />
                        <h:outputText escape="true" value="Fe" styleClass="output-text" />

                        <h:inputText value="#{technologicalMap.microElCa}" styleClass="input-text"
                                     validatorMessage="Количсество магния должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                        <h:inputText value="#{technologicalMap.microElMg}" styleClass="input-text"
                                     validatorMessage="Количсество фосфора должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                        <h:inputText value="#{technologicalMap.microElP}" styleClass="input-text"
                                     validatorMessage="Количсество фосфора должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                        <h:inputText value="#{technologicalMap.microElFe}" styleClass="input-text"
                                     validatorMessage="Количсество железа должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                    </h:panelGrid>

                    <h:panelGrid columns="6">
                        <f:facet name="header">
                            <h:outputText escape="true" value="Витамины, мг" />
                        </f:facet>
                        <h:outputText escape="true" value="A" styleClass="output-text" />
                        <h:outputText escape="true" value="B1" styleClass="output-text" />
                        <h:outputText escape="true" value="B2" styleClass="output-text" />
                        <h:outputText escape="true" value="PP" styleClass="output-text" />
                        <h:outputText escape="true" value="C" styleClass="output-text" />
                        <h:outputText escape="true" value="E" styleClass="output-text" />
                        <h:inputText value="#{technologicalMap.vitaminA}" styleClass="input-text"
                                     validatorMessage="Количсество белков должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                        <h:inputText value="#{technologicalMap.vitaminB1}" styleClass="input-text"
                                     validatorMessage="Количсество жиров должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                        <h:inputText value="#{technologicalMap.vitaminB2}" styleClass="input-text"
                                     validatorMessage="Количсество углеводов должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                        <h:inputText value="#{technologicalMap.vitaminPp}" styleClass="input-text"
                                     validatorMessage="Количсество белков должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                        <h:inputText value="#{technologicalMap.vitaminC}" styleClass="input-text"
                                     validatorMessage="Количсество жиров должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                        <h:inputText value="#{technologicalMap.vitaminE}" styleClass="input-text"
                                     validatorMessage="Количсество углеводов должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                    </h:panelGrid>

                    <h:panelGrid columns="1">
                        <f:facet name="header">
                            <h:outputText escape="true" value="Энергетическая ценность (ккал)" />
                        </f:facet>
                        <h:outputText escape="true" value="Белки" styleClass="output-text" />
                        <h:inputText value="#{technologicalMapCreatePage.technologicalMap.energyValue}" styleClass="input-text"
                                     validatorMessage="Количсество белков должно быть числом.">
                            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
                        </h:inputText>
                    </h:panelGrid>

                </rich:panel>
                <f:facet name="footer">
                    <rich:datascroller for="technologicalMapListTable" renderIfSinglePage="false" maxPages="5"
                                       fastControls="hide" stepControls="auto" boundaryControls="hide">
                        <a4j:support event="onpagechange" />
                        <f:facet name="previous">
                            <h:graphicImage value="/images/16x16/left-arrow.png" />
                        </f:facet>
                        <f:facet name="next">
                            <h:graphicImage value="/images/16x16/right-arrow.png" />
                        </f:facet>
                    </rich:datascroller>
                </f:facet>
            </rich:dataGrid>
</h:panelGrid>