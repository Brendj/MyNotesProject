<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="dashboardPage" type="ru.axetta.ecafe.processor.web.ui.monitoring.DashboardPage"--%>
<h:panelGrid id="dashboardPanelGrid" binding="#{dashboardPage.pageComponent}" styleClass="borderless-grid">
    <h:outputText escape="true" value="Организация" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{dashboardPage.filterOrgName}" readonly="true"
                     styleClass="input-text" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
    </h:panelGroup>


    <h:outputText escape="true" value="Дата:" styleClass="output-text" />
    <rich:calendar value="#{dashboardPage.reportDate}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
    <rich:spacer width="15"/>

    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" />
        </f:facet>
    </a4j:status>

    <rich:tabPanel>
         <rich:tab label="Организации (общая)">
             <a4j:commandButton value="Обновить" action="#{dashboardPage.updateOrgBasicStats}" reRender="dashboardPanelGrid"/>

             <rich:extendedDataTable id="orgBasicStatsTable" value="#{dashboardPage.orgBasicStats.orgBasicStatItems}" var="item"
                                     rows="500"
                                     sortMode="multi" selectionMode="single" width="1500" height="900"
                                     footerClass="data-table-footer">
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.idOfOrg}"  width="35px" filterBy="#{item.idOfOrg}" filterEvent="onkeyup">
                     <f:facet name="header">
                         <h:outputText value="Ид." styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.idOfOrg}"/>
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.orgName}"  width="60px" filterBy="#{item.orgName}" filterEvent="onkeyup">
                     <f:facet name="header">
                         <h:outputText value="Номер" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.orgNameNumber}"/>
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.orgNameNumber}"  width="200px" filterBy="#{item.orgNameNumber}" filterEvent="onkeyup">
                     <f:facet name="header">
                         <h:outputText value="Наименование" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.orgName}"/>
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.lastSuccessfulBalanceSyncTime}" width="150px">
                     <f:facet name="header">
                         <h:outputText value="После. синхр. бал." styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.lastSuccessfulBalanceSyncTime}" converter="timeMinuteConverter"/>
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.numberOfStudentClients}" width="120px">
                     <f:facet name="header">
                         <h:outputText value="Учащихся" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <f:facet name="header">
                         <h:outputText value="Сотруд. и др." styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.numberOfNonStudentClients}"/>
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.numberOfClientsWithoutCard}" width="120px">
                     <f:facet name="header">
                         <h:outputText value="Без карт" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.numberOfClientsWithoutCard}"/>
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.numberOfEnterEvents}" width="120px">
                     <f:facet name="header">
                         <h:outputText value="Событий проходов" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.numberOfEnterEvents}"/> (<h:outputText value="#{item.numberOfStudentEnterEventsPercent}"><f:convertNumber type="percent"/></h:outputText>У - <h:outputText value="#{item.numberOfEmployeeEnterEventsPercent}"><f:convertNumber type="percent"/></h:outputText>С)
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.numberOfDiscountOrders}" width="120px">
                     <f:facet name="header">
                         <h:outputText value="Льготных заказов" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.numberOfDiscountOrders}"/>
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.numberOfPayOrders}" width="120px">
                     <f:facet name="header">
                         <h:outputText value="Платных заказов" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.numberOfPayOrders}"/> (<h:outputText value="#{item.numberOfPayedOrdersPercent}"><f:convertNumber type="percent"/></h:outputText>)
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.orgDistrict}" width="70px">
                     <f:facet name="header">
                         <h:outputText value="Район" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.orgDistrict}"/>
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.orgLocation}" width="70px">
                     <f:facet name="header">
                         <h:outputText value="Локация" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.orgLocation}"/>
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.orgTag}" width="120px">
                     <f:facet name="header">
                         <h:outputText value="Тэги" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.orgTag}"/>
                 </rich:column>
             </rich:extendedDataTable>
         </rich:tab>
         <rich:tab label="Платежные системы">
             <a4j:commandButton value="Обновить" action="#{dashboardPage.updatePaySysStatus}" reRender="dashboardPanelGrid"/>

             <rich:extendedDataTable id="paySysStatusTable" value="#{dashboardPage.psStatus.paymentSystemItemInfos}" var="item"
                                     rows="500"
                                     sortMode="multi" selectionMode="single" width="1500" height="900"
                                     footerClass="data-table-footer">
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.idOfContragent}"  width="30px" filterBy="#{item.idOfContragent}" filterEvent="onkeyup">
                     <f:facet name="header">
                         <h:outputText value="Ид." styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.idOfContragent}"/>
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" sortBy="#{item.contragentName}"  width="200px" filterBy="#{item.contragentName}" filterEvent="onkeyup">
                     <f:facet name="header">
                         <h:outputText value="Наименование" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.contragentName}"/>
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" width="200px">
                     <f:facet name="header">
                         <h:outputText value="Посл. операция" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.lastOperationTime}" converter="timeMinuteConverter"/>
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" width="200px">
                     <f:facet name="header">
                         <h:outputText value="Кол-во операций/сутки" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.numOfOperations}" />
                 </rich:column>
                 <rich:column headerClass="column-header" sortable="true" width="200px">
                     <f:facet name="header">
                         <h:outputText value="Ошибки" styleClass="output-text" escape="true"/>
                     </f:facet>
                     <h:outputText value="#{item.error}" />
                 </rich:column>
             </rich:extendedDataTable>

         </rich:tab>
    </rich:tabPanel>


</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>