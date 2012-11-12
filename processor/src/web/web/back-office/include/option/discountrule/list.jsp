<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Правила --%>
<h:panelGrid id="ruleListPanel" binding="#{ruleListPage.pageComponent}" styleClass="borderless-grid">
    <rich:dataTable id="ruleTable" width="700" var="item" value="#{ruleListPage.items}" rows="20"
                    columnClasses="center-aligned-column"
                    footerClass="data-table-footer">
           <%--
    <rich:column rowspan="2"  headerClass="column-header">
        <f:facet name="header">
            <h:outputText value="Идентификатор" styleClass="output-text" escape="true"/>
        </f:facet>
        <h:outputText styleClass="output-text" value="#{item.idOfRule}" />
    </rich:column>
    <rich:column rowspan="2"  headerClass="column-header">
        <f:facet name="header">
            <h:outputText value="Приоритет" styleClass="output-text" escape="true"/>
        </f:facet>
        <h:outputText styleClass="output-text" value="#{item.priority}" />
    </rich:column>
    <rich:column rowspan="2"  headerClass="column-header">
        <f:facet name="header">
            <h:outputText value="Описание" styleClass="output-text" escape="true"/>
        </f:facet>
        <h:outputText styleClass="output-text" value="#{item.description}" />
    </rich:column>
    <rich:column rowspan="2"  headerClass="column-header">
        <f:facet name="header">
            <h:outputText value="Категории" styleClass="output-text" escape="true"/>
        </f:facet>
        <h:outputText styleClass="output-text" value="#{item.categoryDiscounts}" />
    </rich:column>
    <rich:column rowspan="2"  headerClass="column-header">
        <f:facet name="header">
            <h:outputText value="Тип условия" styleClass="output-text" escape="true"/>
        </f:facet>
        <h:outputText value="#{item.operationor?'ИЛИ':'И'}"/>
    </rich:column>
    <rich:column colspan="4" headerClass="column-header">
        <f:facet name="header">
            <h:outputText value="Комплексы" styleClass="output-text" escape="true"/>
        </f:facet>
    </rich:column>

    <rich:column headerClass="column-header" breakBefore="true">
        <f:facet name="header">
            <h:outputText value="0" styleClass="output-text" escape="true"/>
        </f:facet>
        <h:outputText styleClass="output-text" value="#{item.complex0}" />
    </rich:column>
    <rich:column headerClass="column-header" breakBefore="true">
        <f:facet name="header">
            <h:outputText value="1" styleClass="output-text" escape="true"/>
        </f:facet>
        <h:outputText styleClass="output-text" value="#{item.complex1}" />
    </rich:column>
    <rich:column headerClass="column-header" breakBefore="true">
        <f:facet name="header">
            <h:outputText value="2" styleClass="output-text" escape="true"/>
        </f:facet>
        <h:outputText styleClass="output-text" value="#{item.complex2}" />
    </rich:column>
    <rich:column headerClass="column-header" breakBefore="true">
        <f:facet name="header">
            <h:outputText value="3" styleClass="output-text" escape="true"/>
        </f:facet>
        <h:outputText styleClass="output-text" value="#{item.complex3}" />
    </rich:column>

    <rich:column breakBefore="false">
        <f:facet name="header">
            <h:outputText value="Редактировать" styleClass="output-text" escape="true"/>
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showRuleEditPage}" styleClass="command-link">
            <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfRule}" target="#{mainPage.selectedIdOfRule}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column style="text-align:center" breakBefore="false">
        <f:facet name="header">
            <h:outputText value="Удалить" styleClass="output-text" escape="true"/>
        </f:facet>
        <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                         oncomplete="#{rich:component('ruleDeletePanel')}.show();">
            <h:graphicImage value="/images/16x16/delete.png" />
            <f:setPropertyActionListener value="#{item.idOfRule}"
                                         target="#{mainPage.selectedIdOfRule}" />
        </a4j:commandLink>
    </rich:column>

    <f:facet name="footer">
        <rich:datascroller for="ruleTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                           stepControls="auto" boundaryControls="hide">
            <f:facet name="previous">
                <h:graphicImage value="/images/16x16/left-arrow.png" />
            </f:facet>
            <f:facet name="next">
                <h:graphicImage value="/images/16x16/right-arrow.png" />
            </f:facet>
        </rich:datascroller>
    </f:facet>  --%>

        <f:facet name="header">
            <rich:columnGroup>
                <rich:column rowspan="2" headerClass="center-aligned-column column-header" >
                    <h:outputText value="Идентификатор" escape="true"/>
                </rich:column>
                <rich:column rowspan="2" headerClass="center-aligned-column column-header" >
                    <h:outputText value="Приоритет" escape="true"/>
                </rich:column>
                <rich:column rowspan="2" headerClass="center-aligned-column column-header" >
                    <h:outputText value="Описание" escape="true"/>
                </rich:column>
                <rich:column rowspan="2" headerClass="center-aligned-column column-header">
                    <h:outputText value="Категории клиентов" escape="true"/>
                </rich:column>
                <rich:column rowspan="2" headerClass="center-aligned-column column-header">
                    <h:outputText value="Категории организаций" escape="true"/>
                </rich:column>
                <rich:column rowspan="2" headerClass="center-aligned-column column-header">
                    <h:outputText value="Тип условия" escape="true"/>
                </rich:column>
                <rich:column colspan="10" headerClass="center-aligned-column column-header" width="200%">
                    <h:outputText value="Комплексы" escape="true"/>
                </rich:column>
                <rich:column rowspan="2" headerClass="center-aligned-column column-header">
                    <h:outputText value="Редактировать" escape="true"/>
                </rich:column>
                <rich:column rowspan="2" headerClass="center-aligned-column column-header">
                    <h:outputText value="Удалить" escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header"  breakBefore="true" width="200%">
                    <h:outputText value="0" escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header" >
                    <h:outputText value="1" escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header">
                    <h:outputText value="2" escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header">
                    <h:outputText value="3" escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header">
                    <h:outputText value="4" escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header">
                    <h:outputText value="5" escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header">
                    <h:outputText value="6" escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header">
                    <h:outputText value="7" escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header">
                    <h:outputText value="8" escape="true"/>
                </rich:column>
                <rich:column headerClass="center-aligned-column column-header">
                    <h:outputText value="9" escape="true"/>
                </rich:column>
            </rich:columnGroup>
        </f:facet>
        <rich:column >
            <h:outputText styleClass="output-text" value="#{item.idOfRule}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.priority}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.description}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.categoryDiscounts}" />
        </rich:column>
           <rich:column>
               <h:outputText styleClass="output-text" value="#{item.categoryOrgs}" />
           </rich:column>
        <rich:column>
            <h:outputText value="#{item.operationor?'ИЛИ':'И'}"/>
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.complex0}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.complex1}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.complex2}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.complex3}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.complex4}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.complex5}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.complex6}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.complex7}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.complex8}" />
        </rich:column>
        <rich:column>
            <h:outputText styleClass="output-text" value="#{item.complex9}" />
        </rich:column>
        <rich:column>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{ruleEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.entity}" target="#{ruleEditPage.entity}" />
            </a4j:commandLink>
        </rich:column>
       <rich:column style="text-align:center">
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('confirmDeletePanel')}.show();">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{ruleListPage}"
                                             target="#{confirmDeletePage.listener}" />
                <f:setPropertyActionListener value="#{item.entity}"
                                             target="#{confirmDeletePage.entity}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="ruleTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                               stepControls="auto" boundaryControls="hide">
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>
    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>
    <!--h:commandButton value="Выгрузить в CSV" action="{mainPage.showRuleCSVList}" styleClass="command-button" /-->
</h:panelGrid>
