<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: chirikov
  Date: 30.07.13
  Time: 13:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .output-text-mod {
        font-family: Tahoma, Arial, Sans-Serif;
        font-size: 10pt;
        color: #000;
        white-space: nowrap;
        padding-right: 10px;
    }

    .rotate_text {
        font-family: Tahoma, Arial, Sans-Serif;
        font-size: 10pt;
        color: #000;
        white-space: nowrap;
        padding-right: 10px;
        -moz-transform: rotate(-90.0deg);  /* FF3.5+ */
        -o-transform: rotate(-90.0deg);  /* Opera 10.5 */
        -webkit-transform: rotate(-90.0deg);  /* Saf3.1+, Chrome */
        filter:  progid:DXImageTransform.Microsoft.BasicImage(rotation=0.083);  /* IE6,IE7 */
        -ms-filter: "progid:DXImageTransform.Microsoft.BasicImage(rotation=0.083)"; /* IE8 */
    }

    .thin-center-aligned-column {
        text-align: center;
        vertical-align: middle;
        width: 1%;
        height: 250px;
        line-height: 0.9;
    }

    .clientsgrid_col1 {
        width: 21px !important;
        min-width: 21px !important;
        max-width: 21px !important;
    }

    .clientsgrid_col3, .clientsgrid_col4, .clientsgrid_col5, .clientsgrid_col6,
    .clientsgrid_col7, .clientsgrid_col8, .clientsgrid_col9, .clientsgrid_col10,
    .clientsgrid_col11, .clientsgrid_col12, .clientsgrid_col13, .clientsgrid_col14{
        width: 20px !important;
        min-width: 20px !important;
        max-width: 20px !important;
    }
</style>

<script type="text/javascript">
function onstartloading(){
    jQuery(".checkboxes").attr('disabled', 'disabled');
    jQuery(".groupSelect").attr('disabled', 'disabled');
    jQuery(".categorySelect").attr('disabled', 'disabled');
}
function onstoploading(){
    jQuery(".checkboxes").attr('disabled', '');
    jQuery(".groupSelect").attr('disabled', '');
    jQuery(".categorySelect").attr('disabled', '');
    updateWidth();
}
jQuery(document).ready(function(){
    updateWidth();
});
function updateWidth() {
    var counter = 0;
    jQuery(".clientsTable tr.rich-table-firstrow td").each(function(){
        var width = jQuery('.clientsTable tr.rich-table-firstrow td:eq(' + counter + ')').width();
        jQuery('.clientsTableHead th:eq(' + counter + ')').width(width);
        counter++;
    });
}
</script>


<%--@elvariable id="setupDiscountPage" type="ru.axetta.ecafe.processor.web.ui.feed.SetupDiscountPage"--%>
<a4j:form id="setupDiscountForm">
    <h:panelGrid id="setupDiscountGrid" binding="#{setupDiscountPage.pageComponent}" styleClass="borderless-grid" style="width: 100%;">

    <a4j:region>
        <h:panelGrid columns="2">
            <h:outputText value="Группа: " styleClass="output-text-mod"/>
            <h:panelGrid id="groups" styleClass="borderless-grid">
                <h:selectOneMenu id="group" value="#{setupDiscountPage.group}" style="width:200px;" styleClass="groupSelect">
                    <f:selectItems value="#{setupDiscountPage.groups}"/>
                    <a4j:support status="loadingStatus" event="onchange" actionListener="#{setupDiscountPage.doChangeGroup}" reRender="clients, emptyClients"/>
                </h:selectOneMenu>
            </h:panelGrid>

            <h:outputText value="Категория: " styleClass="output-text-mod"/>
            <h:panelGrid id="categories" styleClass="borderless-grid">
                <h:selectOneMenu id="category" value="#{setupDiscountPage.category}" style="width:200px;" styleClass="categorySelect">
                    <f:selectItems value="#{setupDiscountPage.categories}"/>
                    <a4j:support status="loadingStatus" event="onchange" actionListener="#{setupDiscountPage.doChangeCategory}" reRender="clients, emptyClients"/>
                </h:selectOneMenu>
            </h:panelGrid>
        </h:panelGrid>

        <rich:panel id="emptyClients" style="margin: 0px; padding: 0px; border-bottom: none">
            <rich:dataTable value="#{setupDiscountPage.emptyClients}" var="cl" id="table_head" rowKeyVar="row"styleClass="clientsTableHead"
                            headerClass="clientsgrid_col1,clientsgrid_col2,clientsgrid_col3,clientsgrid_col4,clientsgrid_col5,clientsgrid_col6,clientsgrid_col7,clientsgrid_col8,clientsgrid_col9,clientsgrid_col10,clientsgrid_col11,clientsgrid_col12,clientsgrid_col13,clientsgrid_col14"
                            columnClasses="clientsgrid_col1,clientsgrid_col2,clientsgrid_col3,clientsgrid_col4,clientsgrid_col5,clientsgrid_col6,clientsgrid_col7,clientsgrid_col8,clientsgrid_col9,clientsgrid_col10,clientsgrid_col11,clientsgrid_col12,clientsgrid_col13,clientsgrid_col14">
                <rich:column headerClass="center-aligned-column">
                    <f:facet name="header">
                        <h:outputText value="№"></h:outputText>
                    </f:facet>
                </rich:column>
                <rich:column headerClass="center-aligned-column" style="width: 99%;">
                    <f:facet name="header">
                        <h:outputText value="Клиент"></h:outputText>
                    </f:facet>
                </rich:column>
                <rich:columns value="#{setupDiscountPage.columns}" var="col" styleClass="left-aligned-column"
                              index="ind" headerClass="thin-center-aligned-column" width="1%" style="width: 1%;" >
                    <f:facet name="header">
                        <h:outputText escape="false" value="#{col.title}" />
                    </f:facet>
                </rich:columns>
            </rich:dataTable>
        </rich:panel>

        <rich:panel id="clients" style="height: 500px; overflow: auto;">
            <rich:dataTable value="#{setupDiscountPage.clients}" var="cl" id="table" rowKeyVar="row" styleClass="clientsTable"
                            headerClass="clientsgrid_col1,clientsgrid_col2,clientsgrid_col3,clientsgrid_col4,clientsgrid_col5,clientsgrid_col6,clientsgrid_col7,clientsgrid_col8,clientsgrid_col9,clientsgrid_col10,clientsgrid_col11,clientsgrid_col12,clientsgrid_col13,clientsgrid_col14"
                            columnClasses="clientsgrid_col1,clientsgrid_col2,clientsgrid_col3,clientsgrid_col4,clientsgrid_col5,clientsgrid_col6,clientsgrid_col7,clientsgrid_col8,clientsgrid_col9,clientsgrid_col10,clientsgrid_col11,clientsgrid_col12,clientsgrid_col13,clientsgrid_col14">
                <rich:column headerClass="center-aligned-column">
                    <h:outputText value="#{row+1}" styleClass="output-text"></h:outputText>
                </rich:column>
                <rich:column headerClass="center-aligned-column" style="width: 99%;">
                    <h:outputText value="#{cl.fullName}" styleClass="output-text-mod"></h:outputText>
                </rich:column>
                <rich:columns value="#{setupDiscountPage.columns}" var="col" styleClass="left-aligned-column"
                              index="ind" headerClass="thin-center-aligned-column" width="1%" style="width: 1%;" >
                    <h:selectBooleanCheckbox value="#{cl.rules[col.id]}" styleClass="checkboxes" rendered="#{cl.input}" >
                        <a4j:support event="onclick" status="loadingStatus" actionListener="#{setupDiscountPage.doChangeDiscount}" reRender="messages,clients">
                            <!-- use property that uniquely identifies a row -->
                            <f:attribute name="idofclient" value="#{cl.idofclient}"/>
                        </a4j:support>
                    </h:selectBooleanCheckbox>
                    <h:outputText value="#{cl.values[col.id]}" styleClass="output-text-mod" rendered="#{!cl.input}" />
                </rich:columns>
            </rich:dataTable>
        </rich:panel>

        <h:panelGrid id="messages" styleClass="borderless-grid" columns="2">
            <a4j:status id="loadingStatus" onstart="onstartloading()" onstop="onstoploading()">
                <f:facet name="start">
                    <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                </f:facet>
            </a4j:status>
            <h:outputText escape="true" value="#{setupDiscountPage.errorMessages}" rendered="#{not empty setupDiscountPage.errorMessages}" styleClass="error-messages" />
            <h:outputText escape="true" value="#{setupDiscountPage.infoMessages}" rendered="#{not empty setupDiscountPage.infoMessages}" styleClass="info-messages" />
        </h:panelGrid>

        </a4j:region>
    </h:panelGrid>
</a4j:form>