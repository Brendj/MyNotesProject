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



<a4j:jsFunction name="updateList" reRender="orgUnsychMonitorListTable,lastOrgUpdateTime"></a4j:jsFunction>
<script type="text/javascript">
var inter = setInterval (updateList, 1000 * 60 * 10);
</script>

<%-- Панель просмотра списка организаций --%>
<%--@elvariable id="orgSynchMonitorPage" type="ru.axetta.ecafe.processor.web.ui.org.OrgSynchMonitorPage"--%>
<%--@elvariable id="contractViewPage" type="ru.axetta.ecafe.processor.web.ui.org.contract.ContractViewPage"--%>
<%--@elvariable id="contractEditPage" type="ru.axetta.ecafe.processor.web.ui.org.contract.ContractEditPage"--%>
<h:panelGrid id="contractListPanelGrid" binding="#{orgSynchMonitorPage.pageComponent}" styleClass="borderless-grid">


    <a4j:commandButton value="Обновить" reRender="orgUnsychMonitorListTable,lastOrgUpdateTime" ></a4j:commandButton>
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <h:outputText id="lastOrgUpdateTime" styleClass="output-text" value="Последнее обновление произведено: #{orgSynchMonitorPage.lastUpdate}" converter="timeConverter" />
    <rich:dataTable id="orgUnsychMonitorListTable" value="#{orgSynchMonitorPage.itemList}"
                    var="item" footerClass="data-table-footer"
                    columnClasses="center-aligned-column">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Организация" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortName}" styleClass="output-text"
                          style="#{orgSynchMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10 ? 'color=red' : ''}" />
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Последняя успешная синхронизация балансов" />
            </f:facet>
            <h:outputText escape="true" value="#{item.lastSuccessfulBalanceSync}"
                          style="#{orgSynchMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time  > 1000 * 60 * 10 ? 'color=red' : ''}"
                          styleClass="output-text" converter="timeConverter" />
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Последняя неудачная синхронизация балансов" />
            </f:facet>
            <h:outputText escape="true" value="#{item.lastUnSuccessfulBalanceSync}"
                          style="#{orgSynchMonitorPage.currentTimeMillis - item.lastSuccessfulBalanceSync.time > 1000 * 60 * 10 ? 'color=red' : ''}"
                          styleClass="output-text" converter="timeConverter" />
        </rich:column>
    </rich:dataTable>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>