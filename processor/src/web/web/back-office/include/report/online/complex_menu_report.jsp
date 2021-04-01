<%--
  ~ Copyright (c) 2021. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Artem Saparov
  Date: 19.03.2021
  Time: 11:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="dishMenuReportPanel" binding="#{mainPage.complexMenuReportPage.pageComponent}"
             styleClass="borderless-grid" columns="1">

    <h:panelGrid id="filterComplexMenuReportPanel" columns="2">

        <h:outputText escape="true" value="Контрагент" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div" id="contragentPreordersReportPageSelectContragentPanel">
            <h:inputText value="#{mainPage.complexMenuReportPage.filter}" readonly="true"
                         styleClass="input-text" style="margin-right: 2px; width: 275px;" />
            <a4j:commandButton value="..."
                               action="#{mainPage.showContragentSelectPage}"
                               reRender="modalContragentSelectorPanel,registerStampReportPanelGrid"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="#{mainPage.complexMenuReportPage.classTypeTSP}" target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div" id="contragentPreordersReportPageSelectOrgsPanel">
            <a4j:commandButton value="..." action="#{mainPage.complexMenuReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                               style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.complexMenuReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.complexMenuReportPage.orgFilter}}" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Выбор типа питания" />
        <h:selectOneMenu value="#{mainPage.complexMenuReportPage.selectIdTypeFoodId}" >
            <f:selectItems value="#{mainPage.complexMenuReportPage.getTypesOfComplexFood()}"/>
        </h:selectOneMenu>

        <h:outputText styleClass="output-text" escape="true" value="Выбор рациона" />
        <h:selectOneMenu value="#{mainPage.complexMenuReportPage.selectDiet}" >
            <f:selectItems value="#{mainPage.complexMenuReportPage.getTypesOfDiet()}"/>
        </h:selectOneMenu>

        <h:outputText styleClass="output-text" escape="true" value="Выбор возрастной группы" />
        <h:selectOneMenu value="#{mainPage.complexMenuReportPage.selectIdAgeGroup}" >
            <f:selectItems value="#{mainPage.complexMenuReportPage.getAgeGroup()}"/>
        </h:selectOneMenu>

        <h:outputText styleClass="output-text" escape="true" value="Архивные" />
        <h:selectOneMenu value="#{mainPage.complexMenuReportPage.selectArchived}" >
            <f:selectItems value="#{mainPage.complexMenuReportPage.getArchived()}"/>
        </h:selectOneMenu>

    </h:panelGrid>

    <h:panelGrid columns="2">

        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.complexMenuReportPage.buildForJsf}"
                           reRender="complexMenuReportTablePanel" styleClass="command-button" status="reportGenerateStatus" />

        <h:commandButton value="Генерировать отчет в Excel"
                         action="#{mainPage.complexMenuReportPage.exportToXLS}"
                         styleClass="command-button" />
    </h:panelGrid>

    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid" id="complexMenuReportTablePanel">
        <c:if test="${not empty mainPage.complexMenuReportPage.htmlReport}">
            <h:outputText escape="true" value="Отчет по комплексам" styleClass="output-text" />
            <f:verbatim>
                <div class="htmlReportContent"> ${mainPage.complexMenuReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>

    <rich:dataTable id="complexMenuReportTable" value="#{mainPage.complexMenuReportPage.result}"
                    var="item" rows="25"
                    footerClass="data-table-footer" >

        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="ОО" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Количество" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Тип питания" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Возрастная категория" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Комплекс" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Цена" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Передавать внешним системам" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Даты действия" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Циклическое наполнение" />
                </rich:column>
            </rich:columnGroup>
        </f:facet>

        <rich:subTable value="#{item.complexItem}" var="complex" rowKeyVar="rowComplexKey">
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="item.idOfOrg" />
                    </f:facet>
                    <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
                </rich:column>
            </rich:subTable>
    </rich:dataTable>

</h:panelGrid>