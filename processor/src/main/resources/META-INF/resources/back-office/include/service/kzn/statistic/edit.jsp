<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .bordered {
        border-top: 2px solid #000000;
    }
</style>
<script type="text/javascript">
    function onstartloading() {
        jQuery(".command-button").attr('disabled', 'disabled');
    }
    function onstoploading() {
        jQuery(".command-button").attr('disabled', '');
        updateWidth();
    }
    jQuery(document).ready(function () {
        updateWidth();
    });
</script>

<%--@elvariable id="kznClientsStatisticEditPage" type="ru.axetta.ecafe.processor.web.ui.service.kzn.KznClientsStatisticEditPage"--%>
<h:panelGrid id="kznClientsStatisticEditPanelGrid" binding="#{kznClientsStatisticEditPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid id="orgFilter" columns="3">
        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <a4j:commandButton value="..." action="#{kznClientsStatisticEditPage.showOrgSelectPage()}"
                           reRender="modalOrgSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;">
            <f:setPropertyActionListener value="0" target="#{mainPage.orgSelectPage.filterMode}" />
            <f:setPropertyActionListener value="#{kznClientsStatisticEditPage.getStringIdOfOrgList}"
                                         target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
        </a4j:commandButton>
        <h:outputText styleClass="output-text" escape="true"
                      value=" {#{kznClientsStatisticEditPage.filter}}" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Найти" action="#{kznClientsStatisticEditPage.find()}"
                           reRender="kznClientsStatisticEditPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" id="findButton" />
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Общее количество учащихся" styleClass="output-text" />
        <h:inputText value="#{kznClientsStatisticEditPage.studentsCountTotal}" styleClass="input-text"
                     onkeypress="if(event.which < 48 || event.which > 57 ) if(event.which != 8) return false;" />
        <h:outputText escape="true" value="Количество учеников 1-4 классов" styleClass="output-text" />
        <h:inputText value="#{kznClientsStatisticEditPage.studentsCountYoung}" styleClass="input-text"
                     onkeypress="if(event.which < 48 || event.which > 57 ) if(event.which != 8) return false;" />
        <h:outputText escape="true" value="Количество учеников 5-9 классов" styleClass="output-text" />
        <h:inputText value="#{kznClientsStatisticEditPage.studentsCountMiddle}" styleClass="input-text"
                     onkeypress="if(event.which < 48 || event.which > 57 ) if(event.which != 8) return false;" />
        <h:outputText escape="true" value="Количество учеников 10-11 классов" styleClass="output-text" />
        <h:inputText value="#{kznClientsStatisticEditPage.studentsCountOld}" styleClass="input-text"
                     onkeypress="if(event.which < 48 || event.which > 57 ) if(event.which != 8) return false;" />
        <h:outputText escape="true" value="Количество льготников 1-4 классов" styleClass="output-text" />
        <h:inputText value="#{kznClientsStatisticEditPage.benefitStudentsCountYoung}"
                     styleClass="input-text" onkeypress="if(event.which < 48 || event.which > 57 ) if(event.which != 8) return false;" />
        <h:outputText escape="true" value="Количество льготников 5-9 классов" styleClass="output-text" />
        <h:inputText value="#{kznClientsStatisticEditPage.benefitStudentsCountMiddle}"
                     styleClass="input-text" onkeypress="if(event.which < 48 || event.which > 57 ) if(event.which != 8) return false;" />
        <h:outputText escape="true" value="Количество льготников 10-11 классов" styleClass="output-text" />
        <h:inputText value="#{kznClientsStatisticEditPage.benefitStudentsCountOld}" styleClass="input-text"
                     onkeypress="if(event.which < 48 || event.which > 57 ) if(event.which != 8) return false;" />
        <h:outputText escape="true" value="Количество льготников всего" styleClass="output-text" />
        <h:inputText value="#{kznClientsStatisticEditPage.benefitStudentsCountTotal}"
                     styleClass="input-text" onkeypress="if(event.which < 48 || event.which > 57 ) if(event.which != 8) return false;" />
        <h:outputText escape="true" value="Количество сотрудников, администрации и прочих групп"
                      styleClass="output-text" />
        <h:inputText value="#{kznClientsStatisticEditPage.employeeCount}" styleClass="input-text"
                     onkeypress="if(event.which < 48 || event.which > 57 ) if(event.which != 8) return false;" />
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить" action="#{kznClientsStatisticEditPage.save()}"
                           reRender="kznClientsStatisticEditPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" id="saveButton" />
        <a4j:commandButton value="Удалить" action="#{kznClientsStatisticEditPage.delete()}"
                           reRender="kznClientsStatisticEditPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" id="deleteButton" />
    </h:panelGrid>

</h:panelGrid>