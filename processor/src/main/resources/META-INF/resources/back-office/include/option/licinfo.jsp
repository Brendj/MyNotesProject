<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:dataTable id="licInfoTable"
                footerClass="data-table-footer" value="#{licInfoPage.licInfos}" var="item" rows="20"
                columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Ид." />
        </f:facet>
        <h:outputText value="#{item.id}"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Выдана" />
        </f:facet>
        <h:outputText value="#{item.org}"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Локация" />
        </f:facet>
        <h:outputText value="#{item.location}"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Лицензия" />
        </f:facet>
        <h:outputText value="#{item.info}"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Выдана" />
        </f:facet>
        <h:outputText value="#{item.issuedDate}"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Срок действия" />
        </f:facet>
        <h:outputText value="#{item.expiryDate}"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Валидность" />
        </f:facet>
        <h:outputText value="#{item.validInfo}"/>
    </rich:column>

</rich:dataTable>