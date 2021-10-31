<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2020. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="wtSettingsReportPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.WtSettingsReportPage"--%>
<h:panelGrid columns="2">
    <h:outputText escape="true" value="Количество дней, запрещенных для редактирования" styleClass="output-text" />
    <h:inputText value="#{wtSettingsReportPage.daysForbid}" styleClass="input-text" converterMessage="В поле количества дней допустимы только цифры." />
</h:panelGrid>
<a4j:commandButton value="Сохранить изменения" action="#{wtSettingsReportPage.save()}" reRender="wtSettingsReportPage" />

<h:panelGrid columns="1">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>