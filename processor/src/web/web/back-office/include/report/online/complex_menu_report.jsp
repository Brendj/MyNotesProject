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

<rich:modalPanel id="orgList" autosized="true" minWidth="1100">
    <f:facet name="header">
        <h:outputText
                value="Список организаций" />
    </f:facet>
    <rich:dataTable id="orgs"  value="#{mainPage.complexMenuReportPage.complexOrgItem}"
                    var="item" rows="15" footerClass="data-table-footer">
        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="ИД ОО" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Краткое официальное наименование ОО" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Адрес здания ОО" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Наименование ОО для ПП" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Округ" />
                </rich:column>
            </rich:columnGroup>
        </f:facet>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.shortNameInfoService}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.address}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.shortName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <h:outputText escape="true" value="#{item.district}" styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="orgs" renderIfSinglePage="false"
                               maxPages="5" fastControls="hide" stepControls="auto"
                               boundaryControls="hide">
                <a4j:support event="onpagechange" />
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>
    <rich:spacer height="20px" />
    <tr>
        <td style="text-align: right;">
            <a4j:commandButton value="Закрыть"  onclick="Richfaces.hideModalPanel('orgList')"
                       style="width: 180px; margin-right: 8px; margin-bottom: 8px"  ajaxSingle="true" />
         </td>
    </tr>
</rich:modalPanel>

<h:panelGrid id="dishMenuReportPanel" binding="#{mainPage.complexMenuReportPage.pageComponent}"
             styleClass="borderless-grid" columns="1">

    <h:panelGrid id="filterComplexMenuReportPanel" columns="2">

        <h:outputText escape="true" value="Контрагент" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div" id="contragentComplexMenuPanel">
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
        <h:panelGroup styleClass="borderless-div" id="complexMenuOrgPanel">
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

        <h:outputText escape="true" value="Показать комплексы на дату" styleClass="output-text" />
        <rich:calendar value="#{mainPage.complexMenuReportPage.selectDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{mainPage.complexMenuReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText escape="true" value="Блюдо" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.complexDishSelectPage.selectedItem.dishName}" readonly="true" styleClass="input-text"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showDishSelectPage}" reRender="modalDishSelectorForm"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalDishSelectorForm')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="#{null}" target="#{mainPage.complexDishSelectPage.filter}" />
            </a4j:commandButton>
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid columns="2">

        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.complexMenuReportPage.buildForJsf}"
                           reRender="complexMenuReportTable" styleClass="command-button" status="reportGenerateStatus" />

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
                    var="item" rows="40"
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
                    <h:outputText escape="true" value="Рацион" />
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
                    <h:outputText escape="true" value="Дата начала"/>
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Дата окончания" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText escape="true" value="Циклическое наполнение" />
                </rich:column>
            </rich:columnGroup>
        </f:facet>

        <rich:subTable value="#{item.complexItem}" var="complexItem" rowKeyVar="rowKey"
                       columnClasses="center-aligned-column, center-aligned-column, center-aligned-column">

            <rich:column headerClass="column-header" rowspan="#{item.complexItem.size()}" rendered="#{rowKey eq 0}">
                <a4j:commandButton value="Список" reRender="orgList" ajaxSingle="true"
                                   oncomplete="Richfaces.showModalPanel('orgList');">
                    <f:setPropertyActionListener value="#{item.complexOrgItem}"
                                                 target="#{mainPage.complexMenuReportPage.complexOrgItem}" />
                </a4j:commandButton>
            </rich:column>

            <rich:column headerClass="column-header" rowspan="#{item.complexItem.size()}" rendered="#{rowKey eq 0}">
                <h:outputText escape="true" value="#{item.orgCount}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" style="white-space: nowrap" value="#{complexItem.complexGroupItem}" styleClass="output-text"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complexItem.dietType}" styleClass="output-text"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complexItem.ageGroupItem}" styleClass="output-text"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" style="white-space: nowrap" value="#{complexItem.complexName}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complexItem.price}" styleClass="output-text"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complexItem.isPortal}" styleClass="output-text"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" style="white-space: nowrap" value="#{complexItem.startDate}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" style="white-space: nowrap" value="#{complexItem.endDate}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{complexItem.cycle}" styleClass="output-text"/>
            </rich:column>
        </rich:subTable>

        <f:facet name="footer">
            <rich:datascroller for="complexMenuReportTable" renderIfSinglePage="false"
                               maxPages="5" fastControls="hide" stepControls="auto"
                               boundaryControls="hide">
                <a4j:support event="onpagechange" />
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