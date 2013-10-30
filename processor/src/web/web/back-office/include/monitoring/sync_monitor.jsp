<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" uri="http://java.sun.com/jstl/fmt" %>


<a4j:jsFunction name="updateList" action="#{syncMonitorPage.update}"
                reRender="orgUnsychMonitorListTable,lastOrgUpdateTime"></a4j:jsFunction>
<script type="text/javascript">
    var inter = setInterval(updateList, 1000 * 120);
</script>

<%-- Панель просмотра списка организаций --%>
<%--@elvariable id="syncMonitorPage" type="ru.axetta.ecafe.processor.web.ui.monitoring.SyncMonitorPage"--%>
<%--@elvariable id="contractViewPage" type="ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractViewPage"--%>
<%--@elvariable id="contractEditPage" type="ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractEditPage"--%>
<h:panelGrid id="contractListPanelGrid" binding="#{syncMonitorPage.pageComponent}" styleClass="borderless-grid">


    <a4j:commandButton value="Обновить" action="#{syncMonitorPage.update}"
                       reRender="orgUnsychMonitorListTable,lastOrgUpdateTime"/>
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <h:outputText styleClass="output-text" value="Последнее обновление произведено: " /><h:outputText
        id="lastOrgUpdateTime" styleClass="output-text" value="#{syncMonitorPage.lastUpdate}"
        converter="timeConverter" />

    <h:panelGrid columns="2" columnClasses="valign, valign">
        <rich:dataTable id="orgUnsychMonitorListTable" value="#{syncMonitorPage.itemList}" var="item"
                        footerClass="data-table-footer" columnClasses="center-aligned-column">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Организация" />
                </f:facet>
                <h:outputText escape="true" value="#{item.orgName}" styleClass="output-text"
                              style="#{(item.lastSuccessfulBalanceSync!=null and syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Тэги" />
                </f:facet>
                <h:outputText escape="false" value="#{item.tags}" styleClass="output-text"
                              style="#{(item.lastSuccessfulBalanceSync!=null and syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}" />
            </rich:column>

            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="false" value="Посл. успешная <br/>синхр. балансов" />
                </f:facet>
                <h:outputText escape="true" value="#{item.lastSuccessfulBalanceSync}"
                              style="#{(item.lastSuccessfulBalanceSync!=null and syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}"
                              styleClass="output-text" converter="timeConverter" />
            </rich:column>

            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="false" value="Посл. неудачная <br/>синхр. балансов" />
                </f:facet>
                <h:outputText escape="true" value="#{item.lastUnSuccessfulBalanceSync}"
                              style="#{(item.lastSuccessfulBalanceSync!=null and syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}"
                              styleClass="output-text" converter="timeConverter" />
            </rich:column>

            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Версия клиента" />
                </f:facet>
                <h:outputText escape="true" value="#{item.version}"
                              style="#{(item.lastSuccessfulBalanceSync!=null and syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}"
                              styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="IP-адрес" />
                </f:facet>
                <h:outputText escape="true" value="#{item.remoteAddr}"
                              style="#{(item.lastSuccessfulBalanceSync!=null and syncMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10) ? 'color:red' : ''}"
                              styleClass="output-text" />
            </rich:column>
        </rich:dataTable>

        <h:panelGrid>
            <rich:dataTable id="paramsTable" value="#{syncMonitorPage.namedParams}" var="item"
                            footerClass="data-table-footer" columnClasses="left-aligned-column">
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Наименование параметра" />
                    </f:facet>
                    <h:outputLink value="#" id="createGroupCommandLink" styleClass="command-button" rendered="#{not empty item.href}">
                        <h:outputText value="#{item.paramName}" styleClass="output-text"/>
                        <a4j:support event="onclick" action="#{item.hrefBean.show}" reRender="workspaceForm"/>
                    </h:outputLink>
                    <h:outputText escape="true" value="#{item.paramName}" styleClass="output-text" rendered="#{empty item.href}"/>
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Значение" />
                    </f:facet>
                    <h:outputText escape="true" value="#{item.stringValue}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>

            <rich:dataTable id="payMonitorTable" value="#{syncMonitorPage.payStatItems}" var="item"
                            footerClass="data-table-footer" columnClasses="center-aligned-column">
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Контрагент" />
                    </f:facet>
                    <h:outputText escape="true" value="#{item.contragentName}" styleClass="output-text"/>
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Посл. транзакция" />
                    </f:facet>
                    <h:outputText escape="true" value="#{item.lastOperationTime}" styleClass="output-text" converter="timeConverter" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Транзакций сегодня" />
                    </f:facet>
                    <h:outputText escape="true" value="#{item.numOfOperations}" styleClass="output-text"/>
                </rich:column>
            </rich:dataTable>

            <rich:dataTable id="menuLastLoad" value="#{syncMonitorPage.lastLoadItems}" var="item"
                            footerClass="data-table-footer" columnClasses="center-aligned-column">
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Контрагент ТСП" />
                    </f:facet>
                    <h:outputText escape="true" value="#{item.contragent}" styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Посл. загрузка меню" />
                    </f:facet>
                    <h:outputText escape="true" value="#{item.lastLoadTime}" styleClass="output-text"
                                  converter="timeConverter" />
                </rich:column>
            </rich:dataTable>

        </h:panelGrid>

    </h:panelGrid>


</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>