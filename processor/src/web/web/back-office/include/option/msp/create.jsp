<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditCategory())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания категории --%>
<%--@elvariable id="codeMSPCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.msp.CodeMSPCreatePage"--%>
<h:panelGrid id="CodeMSPCreatePanel" binding="#{codeMSPCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Код МСП" styleClass="output-text required-field" />
    <h:inputText value="#{codeMSPCreatePage.code}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Описание кода МСП" styleClass="output-text" />
    <h:inputTextarea value="#{codeMSPCreatePage.description}" styleClass="input-text" rows="5" cols="64" />
    <h:outputText value="Льгота ИС ПП" styleClass="output-text"/>
    <h:selectOneListbox id="discountSelectCreate" value="#{codeMSPCreatePage.selectedDiscount}" size="1"
                        styleClass="output-text">
        <f:selectItems value="#{codeMSPCreatePage.discounts}" />
    </h:selectOneListbox>
    <h:outputText value="Возрастные группы" styleClass="output-text"/>
    <h:selectManyCheckbox value="#{codeMSPCreatePage.selectedTypes}" layout="pageDirection">
        <f:selectItems value="#{codeMSPCreatePage.ageTypeGroups}"/>
    </h:selectManyCheckbox>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Зарегистрировать код" action="#{codeMSPCreatePage.onSave()}"
                       reRender="CodeMSPCreatePanel" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>