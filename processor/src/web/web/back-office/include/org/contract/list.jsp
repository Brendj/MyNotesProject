<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Панель просмотра списка организаций --%>
<%--@elvariable id="contractListPage" type="ru.axetta.ecafe.processor.web.ui.org.contract.ContractListPage"--%>
<%--@elvariable id="contractViewPage" type="ru.axetta.ecafe.processor.web.ui.org.contract.ContractViewPage"--%>
<%--@elvariable id="contractEditPage" type="ru.axetta.ecafe.processor.web.ui.org.contract.ContractEditPage"--%>
<%--@elvariable id="contractDeletePage" type="ru.axetta.ecafe.processor.web.ui.org.contract.ContractDeletePage"--%>
<%--@elvariable id="selectedContractGroupPage" type="ru.axetta.ecafe.processor.web.ui.org.contract.SelectedContractGroupPage"--%>
<h:panelGrid id="contractListPanelGrid" binding="#{contractListPage.pageComponent}" styleClass="borderless-grid">

    <rich:dataTable id="contractListTable" value="#{contractListPage.entityList}"
                    var="item" rows="20" footerClass="data-table-footer"
                    columnClasses="center-aligned-column">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Идентификатор" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfContract}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер" />
            </f:facet>
            <h:commandLink value="#{item.contractNumber}" action="#{contractViewPage.show}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item.idOfContract}" target="#{selectedContractGroupPage.currentEntityId}" />
            </h:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Исполнитель" />
            </f:facet>
            <h:outputText escape="true" value="#{item.performer}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Заказчик" />
            </f:facet>
            <h:outputText escape="true" value="#{item.customer}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.contractState}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Срок действия" />
            </f:facet>
            <h:outputText escape="true" value="#{item.dateOfClosing}" styleClass="output-text" >
                <f:convertDateTime pattern="dd.MM.yyyy"/>
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата заключения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.dateOfConclusion}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy"/>
            </h:outputText>
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <h:commandLink action="#{contractEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfContract}" target="#{selectedContractGroupPage.currentEntityId}" />
            </h:commandLink>
        </rich:column>

        <rich:column style="text-align:center">
            <f:facet name="header">
                <h:outputText value="Удалить" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('removedContractItemDeletePanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item}" target="#{contractDeletePage.currentEntity}" />
            </a4j:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="contractListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>