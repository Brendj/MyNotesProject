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
        width: 20px;
        height: 250px;
    }
    .clientsgrid_col1 {
        width: 1px;
    }
    .clientsgrid_col2 {
        width: 400px;
    }
    .clientsgrid_col3 {
        width: 70px;
    }
    .clientsgrid_col4 {
        width: 20px;
    }
    .clientsgrid_col5 {
        width: 20px;
    }
    .clientsgrid_col6 {
        width: 20px;
    }
    .clientsgrid_col7 {
        width: 20px;
    }
    .clientsgrid_col8 {
        width: 20px;
    }
    .clientsgrid_col9 {
        width: 20px;
    }
    .clientsgrid_col10 {
        width: 20px;
    }
</style>


<%--@elvariable id="setupDiscountPage" type="ru.axetta.ecafe.processor.web.ui.discount.SetupDiscountPage"--%>
<a4j:form>
    <h:panelGrid id="setupDiscountGrid" binding="#{setupDiscountPage.pageComponent}" styleClass="borderless-grid" style="width: 100%;">

    <a4j:region>
        <rich:panel id="clients" style="height: 550px; overflow: auto;">
            <rich:dataTable value="#{setupDiscountPage.clients}" var="cl" id="table" rowKeyVar="row"
                            columnClasses="clientsgrid_col1,clientsgrid_col2,clientsgrid_col3,clientsgrid_col4,clientsgrid_col5,clientsgrid_col6,clientsgrid_col7,clientsgrid_col8,clientsgrid_col9,clientsgrid_col10">
                <rich:column headerClass="center-aligned-column">
                    <f:facet name="header">
                        <h:outputText value="№"></h:outputText>
                    </f:facet>
                    <h:outputText value="#{row+1}" styleClass="output-text"></h:outputText>
                </rich:column>
                <rich:column headerClass="center-aligned-column" style="width: 150px;">
                    <f:facet name="header">
                        <h:outputText value="Клиент"></h:outputText>
                    </f:facet>
                    <h:outputText value="#{cl.fullName}" styleClass="output-text-mod"></h:outputText>
                </rich:column>
                <rich:columns value="#{setupDiscountPage.columns}" var="col" styleClass="left-aligned-column"
                              index="ind" headerClass="thin-center-aligned-column" width="30px" >
                    <f:facet name="header">
                        <h:outputText escape="true" value="#{col.title}" />
                    </f:facet>
                    <h:selectBooleanCheckbox value="#{cl.rules[col.id]}" styleClass="output-text" rendered="#{cl.input}" />
                    <h:outputText value="#{cl.values[col.id]}" styleClass="output-text-mod" rendered="#{!cl.input}" />
                </rich:columns>
            </rich:dataTable>
        </rich:panel>

        <table cellpadding="0" cellspacing="0">
            <tr>
                <td>
                    <h:panelGrid id="groups" styleClass="borderless-grid">
                        <h:selectOneMenu id="group" value="#{setupDiscountPage.group}" style="width:150px;">
                            <f:selectItems value="#{setupDiscountPage.groups}"/>
                            <a4j:support status="loadingStatus" event="onchange" actionListener="#{setupDiscountPage.doChangeGroup}" reRender="clients"/>
                        </h:selectOneMenu>
                    </h:panelGrid>
                </td>
                <td>
                    <h:panelGrid id="categories" styleClass="borderless-grid">
                        <h:selectOneMenu id="category" value="#{setupDiscountPage.category}" style="width:350px;">
                            <f:selectItems value="#{setupDiscountPage.categories}"/>
                            <a4j:support status="loadingStatus" event="onchange" actionListener="#{setupDiscountPage.doChangeCategory}" reRender="clients"/>
                        </h:selectOneMenu>
                    </h:panelGrid>
                </td>
                <td style="text-align: right; width: 99%">
                    <a4j:region>
                    <a4j:status id="loadingStatus">
                        <f:facet name="start">
                            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                        </f:facet>
                    </a4j:status>

                    <a4j:commandButton value="Применить" reRender="clients,messages" action="#{setupDiscountPage.doApply}" status="loadingStatus">
                    </a4j:commandButton>

                    <a4j:commandButton style="padding-left: 10px" value="Отменить" reRender="clients,messages" action="#{setupDiscountPage.doCancel}" status="loadingStatus">
                    </a4j:commandButton>
                    </a4j:region>
                </td>
            </tr>
            <tr>
                <td colspan="3">
                    <h:panelGrid id="messages" styleClass="borderless-grid">
                        <h:outputText escape="true" value="#{setupDiscountPage.errorMessages}" rendered="#{not empty setupDiscountPage.errorMessages}" styleClass="error-messages" />
                        <h:outputText escape="true" value="#{setupDiscountPage.infoMessages}" rendered="#{not empty setupDiscountPage.infoMessages}" styleClass="info-messages" />
                    </h:panelGrid>
                </td>
            </tr>
        </table>
        </a4j:region>
    </h:panelGrid>
</a4j:form>