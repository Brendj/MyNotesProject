<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра правила обработки автоматических отчетов --%>
<%--@elvariable id="manualReportRunnerPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ManualReportRunnerPage"--%>
<h:panelGrid id="manualReportRunnerViewGrid" binding="#{manualReportRunnerPage.pageComponent}" styleClass="borderless-grid">
    <%--%><rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />--%>
    <h:outputText value="#{manualReportRunnerPage.errorMessage}" style="color: #FF0000" styleClass="messages" rendered="#{not empty manualReportRunnerPage.errorMessage}"/>
    <h:outputText value="#{manualReportRunnerPage.infoMessage}" styleClass="messages" rendered="#{not empty manualReportRunnerPage.infoMessage}"/>
    <rich:simpleTogglePanel label="Настройки ручного запуска" switchType="client" style="width: 800px; max-width: 800px"
                            opened="#{manualReportRunnerPage.displaySettings}" headerClass="filter-panel-header">

        <f:verbatim>
            <style type="text/css">
                .topAligned {
                    vertical-align: top;
                }
            </style>
        </f:verbatim>
        <h:panelGrid styleClass="borderless-grid" columns="2" columnClasses="topAligned">
            <a4j:region>
                <h:panelGrid styleClass="borderless-grid" columnClasses="topAligned">
                    <h:outputText value="Выберите отчет:" styleClass="output-text"/>
                    <h:selectOneListbox id="subscriptions" valueChangeListener="#{manualReportRunnerPage.valueChangeListener}"
                                         value="#{manualReportRunnerPage.ruleItem}" style="width:300px; height: 300px" >
                        <f:selectItems value="#{manualReportRunnerPage.ruleItems}"/>
                        <%-- workspaceSubView:workspaceForm:workspacePageSubView:manualMainParams --%>
                        <a4j:support event="onselect" reRender="workspaceSubView:workspaceForm:workspacePageSubView:manualParamHints,workspaceSubView:workspaceForm:workspacePageSubView:manualMainParams" />
                        <a4j:support event="onchange" reRender="workspaceSubView:workspaceForm:workspacePageSubView:manualParamHints,workspaceSubView:workspaceForm:workspacePageSubView:manualMainParams" />
                    </h:selectOneListbox>
                </h:panelGrid>
            </a4j:region>
            <%--<rich:comboBox width="320" inputClass="input-text" itemClass="output-text"
                           itemSelectedClass="output-text-font"
                           suggestionValues="#{manualReportRunnerPage.ruleItems}" defaultLabel="#{manualReportRunnerPage.ruleItem}"
                           value="#{manualReportRunnerPage.ruleItem}"
                           valueChangeListener="#{manualReportRunnerPage.valueChangeListener}">
                <a4j:support event="onselect" reRender="#{mainPage.topMostPage.pageComponent.id},manualMainParams" />
                <a4j:support event="onchange" reRender="#{mainPage.topMostPage.pageComponent.id},manualMainParams" />
            </rich:comboBox>--%>


            <h:panelGrid styleClass="borderless-grid">
                <h:panelGrid id="manualMainParams" styleClass="borderless-grid" columns="2">
                    <h:outputText value="Дата выборки от:" styleClass="output-text"/>
                    <rich:calendar value="#{manualReportRunnerPage.generateStartDate}" popup="true"/>
                    <h:outputText value="Дата выборки до:" styleClass="output-text"/>
                    <rich:calendar value="#{manualReportRunnerPage.generateEndDate}" popup="true"/>

                    <h:outputText escape="true" value="Формат отчета" styleClass="output-text" />
                    <h:selectOneMenu value="#{manualReportRunnerPage.documentFormat}" styleClass="input-text">
                        <f:selectItems value="#{manualReportRunnerPage.reportFormatMenu.items}" />
                    </h:selectOneMenu>
                </h:panelGrid>


                <h:panelGrid id="manualParamHints" styleClass="borderless-grid">
                    <rich:dataTable value="#{manualReportRunnerPage.paramHints}" var="item"
                                    columnClasses="left-aligned-column, left-aligned-column">
                        <f:facet name="header">
                            <h:outputText escape="true" value="Параметры отчета" styleClass="output-text" style="color: #FFFFFF" />
                        </f:facet>
                        <rich:column>
                            <h:outputText escape="true" value="#{item.hint.paramHint.name}" styleClass="output-text" style="#{manualReportRunnerPage.displayElement(item)};" />
                            <h:outputText escape="true" value="*" style="color: #FF0000; font-weight: bold; #{manualReportRunnerPage.displayElement(item)};" rendered="#{item.hint.required}" styleClass="output-text" />
                        </rich:column>
                        <rich:column>
                            <h:outputText escape="true" value="#{item.hint.paramHint.description}" styleClass="output-text" style="#{manualReportRunnerPage.displayElement(item)};" />
                        </rich:column>
                        <rich:column>
                            <h:selectOneMenu rendered="#{item.type=='combobox'}" style="#{manualReportRunnerPage.displayElement(item)};"
                                             styleClass="output-text" value="#{item.value}">
                                <f:selectItems value="#{item.listItems}"/>
                            </h:selectOneMenu>

                            <h:selectOneRadio rendered="#{item.type=='radio'}" style="#{manualReportRunnerPage.displayElement(item)};" styleClass="output-text" value="#{item.value}">
                                <f:selectItems value="#{item.listItems}"/>
                            </h:selectOneRadio>

                            <h:selectManyCheckbox rendered="#{item.type=='checkbox'}" style="#{manualReportRunnerPage.displayElement(item)};" styleClass="output-text" value="#{item.valueItems}">
                                <f:selectItems value="#{item.listItems}"/>
                            </h:selectManyCheckbox>

                            <h:inputText value="#{item.value}" rendered="#{item.type=='input'}" style="#{manualReportRunnerPage.displayElement(item)};" styleClass="output-text" />

                            <h:outputText escape="true" value="#{item.value}" style="#{manualReportRunnerPage.displayElement(item)};" rendered="#{item.type=='output'}" styleClass="output-text" />

                            <h:panelGroup styleClass="borderless-div" rendered="#{item.type=='contragent'}" style="#{manualReportRunnerPage.displayElement(item)};">
                                <h:inputText value="#{manualReportRunnerPage.contragentFilter.contragent.contragentName}" readonly="true"
                                             styleClass="input-text" style="margin-right: 2px;" />
                                <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                                                   reRender="modalContragentSelectorPanel"
                                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                                                   styleClass="command-link" style="width: 25px;">
                                    <f:setPropertyActionListener value="0"
                                                                 target="#{mainPage.multiContrFlag}" />
                                    <f:setPropertyActionListener value="2"
                                                                 target="#{mainPage.classTypes}" />
                                </a4j:commandButton>
                            </h:panelGroup>

                            <h:panelGroup styleClass="borderless-div" rendered="#{item.type=='contract'}" style="#{manualReportRunnerPage.displayElement(item)};">
                                <h:inputText value="#{manualReportRunnerPage.contractFilter.contract.contractName}" readonly="true"
                                             styleClass="input-text" style="margin-right: 2px;" />
                                <a4j:commandButton value="..." action="#{mainPage.showContractSelectPage}"
                                                   reRender="modalContractSelectorPanel"
                                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContractSelectorPanel')}.show();"
                                                   styleClass="command-link" style="width: 25px;">
                                    <f:setPropertyActionListener value="0"
                                                                 target="#{mainPage.multiContrFlag}" />
                                    <f:setPropertyActionListener value=""
                                                                 target="#{mainPage.classTypes}" />
                                </a4j:commandButton>
                            </h:panelGroup>

                            <h:panelGroup rendered="#{item.type=='org'}" style="#{manualReportRunnerPage.displayElement(item)};">
                                <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                                   styleClass="command-link" style="width: 25px;" />
                                <h:outputText styleClass="output-text" escape="true" value=" {#{manualReportRunnerPage.filter}}" />
                            </h:panelGroup>
                        </rich:column>
                    </rich:dataTable>
                </h:panelGrid>
            </h:panelGrid>


            <h:panelGrid columns="2">
            <h:commandButton value="Сформировать" action="#{manualReportRunnerPage.triggerJob}"
                             styleClass="command-button" onclick="document.getElementById('workspaceSubView:workspaceForm:workspacePageSubView:manualReport_waiting').style.display='block';"/>
            <h:graphicImage id="manualReport_waiting" value="/images/gif/waiting.gif" alt="waiting" style="display: none" />
            </h:panelGrid>
        </h:panelGrid>
    </rich:simpleTogglePanel>



    <h:commandLink value="Сохранить сформированный отчет в Excel" style="color: #0000ff;" action="#{manualReportRunnerPage.triggerXLSRequest}"
                   styleClass="command-button" rendered="#{not empty manualReportRunnerPage.previousPrint}"/>

    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty manualReportRunnerPage.htmlResult}" >

            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${manualReportRunnerPage.htmlResult}
                </div>
            </f:verbatim>

        </c:if>
    </h:panelGrid>
</h:panelGrid>