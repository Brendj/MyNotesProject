<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 25.01.12
  Time: 12:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid styleClass="borderless-grid" id="journalViewPanel"
             binding="#{mainPage.journalViewPage.pageComponent}">
    <rich:dataTable id="journalTable" value="#{journalViewPage.journal}" var="item" rows="20"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Ид." />
            </f:facet>
            <h:outputText value="#{item.idOfTransactionJournal}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата" />
            </f:facet>
            <h:outputText value="#{item.transDate}" converter="timeConverter" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Ид. орг" />
            </f:facet>
            <h:outputText value="#{item.idOfOrg}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="ОГРН" />
            </f:facet>
            <h:outputText value="#{item.OGRN}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Ид. операции" />
            </f:facet>
            <h:outputText value="#{item.idOfInternalOperation}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Код услуги" />
            </f:facet>
            <h:outputText value="#{item.serviceCode}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Код операции" />
            </f:facet>
            <h:outputText value="#{item.transactionCode}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Тип карты" />
            </f:facet>
            <h:outputText value="#{item.cardTypeCode}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Код ид. карты" />
            </f:facet>
            <h:outputText value="#{item.cardIdentityCode}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Ид. карты" />
            </f:facet>
            <h:outputText value="#{item.cardIdentityName}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="СНИЛС" />
            </f:facet>
            <h:outputText value="#{item.clientSan}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Договор" />
            </f:facet>
            <h:outputText value="#{item.contractId}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Тип клиента" />
            </f:facet>
            <h:outputText value="#{item.clientType}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Вх. группа" />
            </f:facet>
            <h:outputText value="#{item.clientType}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Сумма" />
            </f:facet>
            <h:outputText value="#{item.financialAmount}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата учета" />
            </f:facet>
            <h:outputText value="#{item.accountingDate}" />
        </rich:column>
    </rich:dataTable>

</h:panelGrid>
