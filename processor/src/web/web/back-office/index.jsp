<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<f:view>

<html>
<head>
    <title><h:outputText value="Новая школа#{runtimeContext.instanceNameDecorated}: #{mainPage.userRole}"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="Content-Language" content="ru">
    <link rel="icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="shortcut icon" href="<c:url value="/images/ecafe-favicon.png"/>" type="image/x-icon">
    <link rel="stylesheet" href="<c:url value="/back-office/styles.css"/>" type="text/css">
</head>

<body style="margin: 4px; padding: 0;">

    <rich:simpleTogglePanel rendered="false" />

    <rich:fileUpload rendered="false" />

    <rich:calendar rendered="false" />


    <f:subview id="bankDeleteSubView">
        <c:import url="include/option/confirm_delete_bank.jsp" />
    </f:subview>


    <f:subview id="abstractConfirmDeletePageSubView">
        <c:import url="include/abstractpage/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="confirmDeletePageSubView">
        <c:import url="include/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="technologicalMapGroupSelectSubView">
        <c:import url="include/option/configuration_provider/technologicalMap/group/select.jsp" />
    </f:subview>

    <f:subview id="productGroupSelectSubView">
        <c:import url="include/option/configuration_provider/product/group/select.jsp" />
    </f:subview>

    <f:subview id="goodGroupSelectSubView">
        <c:import url="include/option/configuration_provider/good/group/select.jsp" />
    </f:subview>


    <f:subview id="configurationProviderSelectSubView">
        <c:import url="include/option/configuration_provider/select.jsp" />
    </f:subview>


<f:subview id="productSelectSubView">
        <c:import url="include/option/configuration_provider/product/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="productGroupsSelectSubView">
        <c:import url="include/option/configuration_provider/product/group/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="technologicalMapGroupsSelectSubView">
        <c:import url="include/option/configuration_provider/technologicalMap/group/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="technologicalMapProductsSelectSubView">
        <c:import url="include/option/configuration_provider/technologicalMap/product/select.jsp" />
    </f:subview>

    <f:subview id="orgSelectSubView">
        <c:import url="include/org/select.jsp" />
    </f:subview>

    <f:subview id="orgListSelectSubView">
        <c:import url="include/org/select_list.jsp" />
    </f:subview>

    <f:subview id="contragentSelectSubView">
        <c:import url="include/contragent/select.jsp" />
    </f:subview>

    <f:subview id="clientSelectSubView">
        <c:import url="include/client/select.jsp" />
    </f:subview>

    <f:subview id="clientGroupSelectSubView">
        <c:import url="include/client/select_group.jsp" />
    </f:subview>

    <f:subview id="categorySelectSubView">
        <c:import url="include/option/categorydiscount/select.jsp" />
    </f:subview>

    <f:subview id="categoryListSelectSubView">
        <c:import url="include/option/categorydiscount/select_list.jsp" />
    </f:subview>

    <f:subview id="categoryOrgListSelectSubView">
        <c:import url="include/option/categoryorg/select_list.jsp" />
    </f:subview>

    <f:subview id="ruleListSelectSubView">
        <c:import url="include/option/discountrule/select_list.jsp" />
    </f:subview>

    <f:subview id="ccAccountDeleteSubView">
        <c:import url="include/contragent/ccaccount/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="reportJobDeleteSubView">
        <c:import url="include/report/job/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="reportRuleDeleteSubView">
        <c:import url="include/report/rule/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="eventNotificationDeleteSubView">
        <c:import url="include/event_notification/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="PosDeleteSubView">
        <c:import url="include/contragent/pos/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="settlementDeleteSubView">
        <c:import url="include/contragent/settlement/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="addPaymentDeleteSubView">
        <c:import url="include/contragent/addpayment/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="categoryDeleteSubView">
        <c:import url="include/option/confirm_delete.jsp" />
    </f:subview>
	
	<f:subview id="templateDeleteSubView">
        <c:import url="include/option/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="configurationProviderItemDeleteSubView">
        <c:import url="include/option/configuration_provider/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="technologicalMapItemDeleteSubView">
        <c:import url="include/option/configuration_provider/technologicalMap/confirm_delete.jsp" />
    </f:subview>

    <table width="100%" cellspacing="4px" cellpadding="0" class="main-grid">
        <tr>
            <td colspan="2">
                    <%-- Заголовок страницы --%>
                <a4j:form id="headerForm" styleClass="borderless-form" eventsQueue="mainFormEventsQueue">
                    <rich:panel styleClass="header-panel" bodyClass="header-panel-body">
                        <h:panelGroup style="text-align: right; float: right;">
                            <h:outputText escape="true" value="Версия #{runtimeContext.currentDBSchemaVersion}"
                                          styleClass="output-text" /><br />
                            <a4j:commandLink value="Мои настройки" binding="#{userSettings.mainMenuComponent}"
                                           action="#{userSettings.show}" styleClass="command-link" reRender="mainMenu, workspaceForm"/>
                            &nbsp;&nbsp;&nbsp;
                            <h:outputText escape="true" value="#{request.remoteUser} - " styleClass="output-text" />
                            <a4j:commandLink value="Выход" action="#{mainPage.logout}" styleClass="command-link" />
                        </h:panelGroup>
                        <h:panelGroup style="text-align: left;">
                            <h:graphicImage value="/images/ecafe-favicon.png"
                                            style="border: 0; margin: 0 8px 0 0; vertical-align: middle; " />
                            <h:outputText escape="true" id="headerText" value="Новая школа#{runtimeContext.instanceNameDecorated}: #{mainPage.userRole}"
                                          styleClass="page-header-text" />
                        </h:panelGroup>
                    </rich:panel>
                </a4j:form>
            </td>
        </tr>
            <%-- Центральная область --%>
        <tr>
            <td style="min-width: 210px; vertical-align: top;" width="215px">
                    <%-- Главное меню --%>
                <f:subview id="mainMenuSubView">
                    <c:import url="/back-office/include/main_menu.jsp" />
                </f:subview>
            </td>
            <td style="vertical-align: top;" width="*">
                    <%-- Рабочая область --%>
                <f:subview id="workspaceSubView">
                    <c:import url="/back-office/include/workspace.jsp" />
                </f:subview>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                    <%-- Нижний колонтитул --%>
                <h:panelGrid width="100%" cellspacing="4px" cellpadding="0" styleClass="borderless-grid"
                             columnClasses="right-aligned-column">
                </h:panelGrid> <%-- Нижний колонтитул --%>
            </td>
        </tr>
    </table>

</body>

</html>

</f:view>
