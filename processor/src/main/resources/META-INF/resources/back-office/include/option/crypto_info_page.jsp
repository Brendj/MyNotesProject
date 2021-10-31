<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="cryptoInfoPage" type="ru.axetta.ecafe.processor.web.ui.option.CryptoInfoPage"--%>
<h:panelGrid id="cryptoInfoGrid" binding="#{cryptoInfoPage.pageComponent}" styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Информация о крипто-провайдере" switchType="client"
                            opened="true" headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">

                <h:outputText escape="true" value="Провайдер:" styleClass="output-text" />
                <h:inputText value="#{cryptoInfoPage.cryptoProviderName}" maxlength="30"
                             styleClass="input-text" readonly="true" />

                <h:outputText escape="true" value="Версия:" styleClass="output-text" />
                <h:inputText value="#{cryptoInfoPage.cryptoLibVersion}" maxlength="30"
                             styleClass="input-text" readonly="true" />

                <h:outputText escape="true" value="Релиз:" styleClass="output-text" />
                <h:inputText value="#{cryptoInfoPage.cryptoLibRelease}" maxlength="30"
                             styleClass="input-text" readonly="true" />
        </h:panelGrid>
    </rich:simpleTogglePanel>
</h:panelGrid>


<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>