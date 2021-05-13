<%--
  ~ Copyright (c) 2021. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: artem saparov
  Date: 05.05.2021
  Time: 8:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<h:panelGrid id="complexExtendedMenuReportPanel" binding="#{mainPage.complexExtendedReportPage.pageComponent}" onclick="#{mainPage.complexExtendedReportPage.buildHTMLReport}"
             styleClass="borderless-grid" columns="1">

    <h:panelGrid id="filterComplexMenuReportPanel" columns="4" >

        <h:outputText escape="true" value="Контрагент:" styleClass="output-text" />
        <h:outputText escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).contragent}" styleClass="output-text" />

        <h:outputText styleClass="output-text" escape="true" value="Период действия комплекса:"  style="margin-left: 50px"/>
        <h:outputText styleClass="output-text" escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).complexDate}" />

        <h:outputText styleClass="output-text" escape="true" value="Комплекс:" />
        <h:outputText styleClass="output-text" escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).complexName}" />

        <h:outputText escape="true" value="Кол-во дней в цикле:" styleClass="output-text" style="margin-left: 50px" />
        <h:outputText escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).dayInCycle}" styleClass="output-text" />

        <h:outputText escape="true" value="Вид рациона:" styleClass="output-text" />
        <h:outputText escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).dietType}" styleClass="output-text" />

        <h:outputText escape="true" value="Движение цикла:" styleClass="output-text" style="margin-left: 50px" />
        <h:outputText escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).cycleMotion}" styleClass="output-text" />

        <h:outputText escape="true" value="Возрастная категория:" styleClass="output-text"  />
        <h:outputText escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).ageGroupItem}" styleClass="output-text" />

        <h:outputText escape="true" value="Стартовый день цикла:" styleClass="output-text" style="margin-left: 50px" />
        <h:outputText escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).startDay}" styleClass="output-text" />

        <h:outputText escape="true" value="Тип комплекса:" styleClass="output-text" />
        <h:outputText escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).complexType}" styleClass="output-text" />

        <h:outputText escape="true" value="Пропуск дней в цикле:" styleClass="output-text" style="margin-left: 50px" />
        <h:outputText escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).passDay}" styleClass="output-text" />

        <h:outputText escape="true" value="Передать внешним системам:" styleClass="output-text" />
        <h:outputText escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).isPortal}" styleClass="output-text" />

        <h:outputText escape="true" value="Примечание:" styleClass="output-text" style="margin-left: 50px"  />
        <h:outputText escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).note}" styleClass="output-text" />

        <h:outputText escape="true" value="Штрихкод:" styleClass="output-text" />
        <h:outputText escape="true" value="#{mainPage.complexExtendedReportPage.report.get(0).barCode}" styleClass="output-text" />

    </h:panelGrid>

    <h:panelGrid columns="2">

        <a4j:commandButton value="Вернуться назад" reRender="workspaceForm" ajaxSingle="true" action="#{mainPage.showComplexMenuReportPage}"
                           styleClass="command-button"  />

        <h:commandButton value="Сохранить в excel"
                         action="#{mainPage.complexExtendedReportPage.exportToXLS}"
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
            <h:outputText escape="true" value="Детальный отчет по комплексам" styleClass="output-text" />
            <f:verbatim>
                <div class="htmlReportContent"> ${mainPage.complexMenuReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>


    <rich:dataTable id="complex" value="#{mainPage.complexExtendedReportPage.report}"
                    var="citem" rows="100" footerClass="data-table-footer" >

        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="День цикла" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Блюда" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Цена" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Состав" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Категория" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Подкатегория" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Калории" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Белки" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Жиры" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Углеводы" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Код поставщика" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Масса" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Дата начала действия" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Дата окончания действия" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Код ИСПП" />
                </rich:column>
            </rich:columnGroup>
        </f:facet>

        <rich:subTable id="complexSubTable"  value="#{citem.dish}" var="complex" rows="100" rowKeyVar="rowKeys"
                       columnClasses="center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column,
                       center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column,
                       center-aligned-column, center-aligned-column, center-aligned-column">

            <rich:column headerClass="column-header" rowspan="#{citem.dish.size()}" rendered="#{rowKeys eq 0}">
                <h:outputText escape="true" value="#{citem.day}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.dish}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.price}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.structure}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.category}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.subCategory}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.calories}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.proteins}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.fats}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.carbohydrates}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.code}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.weight}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.beginDate}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.endDate}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complex.idOfDish}" styleClass="output-text" />
            </rich:column>
        </rich:subTable>

    </rich:dataTable>

</h:panelGrid>