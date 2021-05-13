<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.User" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="javax.faces.application.FacesMessage" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%
    try {
        if (StringUtils.isNotEmpty(request.getRemoteUser()) && User.needEnterSmsCode(request.getRemoteUser())) {
            String mainPage;
            if (User.isSuccessfullySendEMP()) {
                mainPage = ServletUtils.getHostRelativeResourceUri(request, "back-office/confirm-sms.faces");
            }
            else
            {
                mainPage = ServletUtils.getHostRelativeResourceUri(request, "back-office/emp_server_not_answer.faces");
            }
            response.sendRedirect(mainPage);
            return;
        }
    } catch (Exception e) {
            String mainPage = ServletUtils.getHostRelativeResourceUri(request, "back-office/confirm-sms.faces");
            response.sendRedirect(mainPage);
    }
    try {
        if (StringUtils.isNotEmpty(request.getRemoteUser()) && User.isNeedChangePassword(request.getRemoteUser())) {
            String mainPage = ServletUtils.getHostRelativeResourceUri(request, "back-office/change-password.faces");
            response.sendRedirect(mainPage);
            return;
        }
    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                e.getMessage(), null));
    }
%>
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
    <!--<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>-->
</head>

<body style="margin: 4px; padding: 0;">

    <rich:simpleTogglePanel rendered="false" />

    <rich:fileUpload rendered="false" />

    <rich:calendar rendered="false" />

    <f:subview id="employeeSelectSubView">
        <c:import url="include/option/employees/employee/select.jsp" />
    </f:subview>

    <f:subview id="dogmSelectSubView">
        <c:import url="include/visitorsdogm/visitordogm/select.jsp" />
    </f:subview>

    <f:subview id="bankDeleteSubView">
        <c:import url="include/option/confirm_delete_bank.jsp" />
    </f:subview>

    <f:subview id="abstractConfirmDeletePageSubView">
        <c:import url="include/abstractpage/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="confirmDeletePageSubView">
        <c:import url="include/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="technologicalMapSelectSubView">
        <c:import url="include/commodity_accounting/configuration_provider/technologicalMap/select.jsp" />
    </f:subview>

    <f:subview id="productSelectSubView">
        <c:import url="include/commodity_accounting/configuration_provider/product/select.jsp" />
    </f:subview>

    <f:subview id="cardRegistrationConfirmSubView">
        <c:import url="include/cardoperator/card_create_confirm.jsp" />
    </f:subview>

    <f:subview id="technologicalMapGroupSelectSubView">
        <c:import url="include/commodity_accounting/configuration_provider/technologicalMap/group/select.jsp" />
    </f:subview>

    <f:subview id="productGroupSelectSubView">
        <c:import url="include/commodity_accounting/configuration_provider/product/group/select.jsp" />
    </f:subview>

    <f:subview id="goodGroupSelectSubView">
        <c:import url="include/commodity_accounting/configuration_provider/good/group/select.jsp" />
    </f:subview>


    <f:subview id="configurationProviderSelectSubView">
        <c:import url="include/commodity_accounting/configuration_provider/select.jsp" />
    </f:subview>

    <f:subview id="configurationProviderSelectListSubView">
        <c:import url="include/commodity_accounting/configuration_provider/select_list.jsp" />
    </f:subview>

    <f:subview id="basicGoodSelectListSubView">
        <c:import url="include/commodity_accounting/configuration_provider/basicGood/select_list.jsp" />
    </f:subview>

    <f:subview id="goodSelectSubView">
        <c:import url="include/commodity_accounting/configuration_provider/good/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="goodGroupsSelectSubView">
        <c:import url="include/commodity_accounting/configuration_provider/good/group/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="productDeleteSelectSubView">
        <c:import url="include/commodity_accounting/configuration_provider/product/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="productGroupsSelectSubView">
        <c:import url="include/commodity_accounting/configuration_provider/product/group/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="technologicalMapGroupsSelectSubView">
        <c:import url="include/commodity_accounting/configuration_provider/technologicalMap/group/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="technologicalMapProductsSelectSubView">
        <c:import url="include/commodity_accounting/configuration_provider/technologicalMap/product/select.jsp" />
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

    <f:subview id="contragentListSelectSubView">
        <c:import url="include/contragent/select_list.jsp" />
    </f:subview>

    <f:subview id="complexListSelectSubView">
        <c:import url="include/webtechnolog/complex/select_list.jsp" />
    </f:subview>

    <f:subview id="dishListSelectSubView">
        <c:import url="include/webtechnolog/dish/select_list.jsp" />
    </f:subview>

    <f:subview id="contractSelectSubView">
        <c:import url="include/contragent/contract/select.jsp" />
    </f:subview>

    <f:subview id="clientSelectSubView">
        <c:import url="include/client/select.jsp" />
    </f:subview>

    <f:subview id="clientSelectListSubView">
        <c:import url="include/client/select_list.jsp" />
    </f:subview>

    <f:subview id="clientGroupListSelectSubView">
        <c:import url="include/client/select_group_list.jsp" />
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
        <c:import url="include/commodity_accounting/configuration_provider/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="technologicalMapItemDeleteSubView">
        <c:import url="include/commodity_accounting/configuration_provider/technologicalMap/confirm_delete.jsp" />
    </f:subview>

    <f:subview id="syncErrorsSubView">
        <c:import url="include/monitoring/sync_errors_window.jsp" />
    </f:subview>

    <f:subview id="userSelectSubView">
        <c:import url="include/option/user/select.jsp" />
    </f:subview>

    <f:subview id="orgMainBuildingListSelectSubView">
        <c:import url="include/org/select_mainbuilding_list.jsp" />
    </f:subview>

    <f:subview id="userListSelectSubView">
        <c:import url="include/option/user/select_list.jsp" />
    </f:subview>

    <table width="100%" cellspacing="4px" cellpadding="0" class="main-grid">
        <tr>
            <td colspan="2">
                    <%-- Заголовок страницы --%>
                <a4j:form id="headerForm" styleClass="borderless-form" eventsQueue="mainFormEventsQueue">
                    <a4j:jsFunction name="sayHello" action="#{mainPage.userSaidHello}"/>
                    <a4j:jsFunction name="sayGoodbye" action="#{mainPage.userSaidGoodbye}"/>
                    <rich:panel styleClass="header-panel" bodyClass="header-panel-body">
                        <h:panelGroup style="text-align: right; float: right;">
                            <h:outputText escape="true" value="Версия #{runtimeContext.currentDBSchemaVersion}"
                                          styleClass="output-text" /><br />
                            <a4j:commandLink value="Мои настройки" binding="#{userSettings.mainMenuComponent}"
                                           action="#{userSettings.show}" styleClass="command-link" reRender="workspaceForm"/>
                            &nbsp;&nbsp;&nbsp;
                            <h:outputText id="sysuser" escape="true" value="#{request.remoteUser}" styleClass="output-text" />
                            <rich:toolTip for="sysuser" followMouse="true" direction="top-right" showDelay="500" styleClass="tooltip" rendered="#{not empty mainPage.userContragentsList}">
                                <span  style="white-space:nowrap">
                                    <h:outputText styleClass="output-text" style="font-weight: bold" value="Доступные контрагенты:" /><br />
                                    <h:outputText escape="false" value="#{mainPage.userContragentsList}" /><br /><br />
                                    <h:outputText styleClass="output-text" style="font-weight: bold" value="Доступные регионы:" /><br />
                                    <h:outputText escape="false" value="#{mainPage.userRegionsList}" />
                                </span>
                            </rich:toolTip>
                            <h:outputText value=" - " styleClass="output-text"/>
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
