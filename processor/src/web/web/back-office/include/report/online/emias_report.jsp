<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<h:panelGrid id="blockunblockReportPanel" binding="#{mainPage.emiasReportPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.emiasReportPage.filter}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Клиенты" />
        <h:panelGroup id="clientFilter">
            <a4j:commandButton value="..."
                               action="#{mainPage.showClientSelectListPage(mainPage.emiasReportPage.getClientList())}"
                               reRender="modalClientListSelectorPanel,selectedClientList"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                    #{rich:component('modalClientListSelectorPanel')}.show();" styleClass="command-link"
                               style="width: 25px;" id="clientFilterButton">
                <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                <f:setPropertyActionListener value="#{mainPage.emiasReportPage.getStringClientList}"
                                             target="#{mainPage.clientSelectListPage.clientFilter}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                          value=" {#{mainPage.emiasReportPage.filterClient}}" />
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="false" value="Построить по всем дружественным организациям" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.emiasReportPage.allFriendlyOrgs}" styleClass="output-text">
        </h:selectBooleanCheckbox>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.emiasReportPage.buildReportHTML}"
                           reRender="blockunblockReportPanel" styleClass="command-button"
                           status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.emiasReportPage.generateXLS}"
                         styleClass="command-button" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid columns="1" columnClasses="valign, valign">
        <rich:dataTable id="emiasTable" value="#{mainPage.emiasReportPage.items}" var="item" rows="50"
                        footerClass="data-table-footer" columnClasses="center-aligned-column" reRender="lastOrgUpdateTime">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText escape="false" value="Идентификатор"/><br/>
                        <h:outputText escape="false" value="события в «ИС ПП»"/>
                    </h:panelGroup>
                </f:facet>
                <h:outputText escape="true" value="#{item.emiasID}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText escape="false" value="ФИО обучающегося"/>
                    </h:panelGroup>
                </f:facet>
                <h:outputText escape="true" value="#{item.lastname} #{item.firstname} #{item.middlename}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText escape="false" value="Дата события"/>
                    </h:panelGroup>
                </f:facet>
                <h:outputText escape="true" value="#{item.dateLiberation}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText escape="false" value="Дата начала"/><br/>
                        <h:outputText escape="false" value="освобождения"/>
                    </h:panelGroup>
                </f:facet>
                <h:outputText escape="true" value="#{item.dateStartLiberation}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText escape="false" value="Дата окончания"/><br/>
                        <h:outputText escape="false" value="освобождения"/>
                    </h:panelGroup>
                </f:facet>
                <h:outputText escape="true" value="#{item.dateEndLiberation}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText escape="false" value="Отметка о принятии"/><br/>
                        <h:outputText escape="false" value="к сведения информации"/>
                    </h:panelGroup>
                </f:facet>
                <h:outputText escape="true" value="#{item.accepted}" styleClass="output-text" />
            </rich:column>

            <f:facet name="footer">
                <rich:datascroller for="emiasTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
    </h:panelGrid>
</h:panelGrid>

