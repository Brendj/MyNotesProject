<%-- ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved. --%>
<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<% response.setHeader("Content-disposition", "inline;filename=getOrgBasicStats.csv"); %>
<%--@elvariable id="dashboardPage" type="ru.axetta.ecafe.processor.web.ui.monitoring.DashboardPage"--%>
<f:view>
    <h:outputText value="��.;" escape="false"/>
    <h:outputText value="�����;" escape="false"/>
    <%--<h:outputText value="������������;" escape="false"/>
    <h:outputText value="�����. �����. ���.;" escape="false"/>--%>
    <h:outputText value="����;" escape="false"/>
    <h:outputText value="��������;" escape="false"/>
    <h:outputText value="������. � ��.;" escape="false"/>
    <h:outputText value="��� ����;" escape="false"/>
    <h:outputText value="������� ��������;" escape="false"/>
    <%--<h:outputText value="������ ������;" escape="false" />--%>
    <h:outputText value="%�;" escape="false"/>
    <h:outputText value="%�;" escape="false"/>
    <h:outputText value="�������� �������;" escape="false"/>
    <%--<h:outputText value="������ �����. ������. �������;" escape="false" />
    <h:outputText value="%�;" escape="false"/>
    <h:outputText value="%�;" escape="false"/>--%>
    <h:outputText value="������� �������;" escape="false"/>
    <%--<h:outputText value="������ �����. �����. �������;" escape="false" />
    <h:outputText value="%�;" escape="false"/>
    <h:outputText value="%�;" escape="false"/>
    <h:outputText value="������� �������;" escape="false"/>--%>
    <h:outputText value="�����;" escape="false"/>
    <h:outputText value="�������;" escape="false"/>
    <h:outputText value="����;" escape="false"/>
    <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    <a4j:repeat value="#{dashboardPage.orgBasicStats.orgBasicStatItems}" var="item">
        <h:outputText value="#{item.idOfOrg};" escape="false"/>
        <h:outputText value="#{item.orgNameNumber};" escape="false"/>
        <%--<h:outputText value="#{item.orgName};" escape="false"/>
        <h:outputText value="#{item.lastSuccessfulBalanceSyncTime};" escape="false"/>--%>
        <h:outputText value="#{item.numberOfChildrenClients};" escape="false"/>
        <h:outputText value="#{item.numberOfParentsClients};" escape="false"/>
        <h:outputText value="#{item.numberOfNonStudentClients};" escape="false"/>
        <h:outputText value="#{item.numberOfClientsWithoutCard};" escape="false"/>
        <h:outputText value="#{item.numberOfEnterEvents};" escape="false"/>
        <%--<h:outputText value="#{item.lastEnterEvent};" escape="false"/>--%>
        <h:outputText value="#{item.numberOfStudentsWithEnterEventsPercent};" escape="false">
            <f:convertNumber type="percent"/>
        </h:outputText>
        <h:outputText value="#{item.numberOfEmployeesWithEnterEventsPercent};" escape="false">
            <f:convertNumber type="percent"/>
        </h:outputText>
        <h:outputText value="#{item.numberOfDiscountOrders};" escape="false"/>
        <%--<h:outputText value="#{item.firstDiscountOrderDate};" escape="false"/>
        <h:outputText value="#{item.numberOfStudentsWithDiscountOrdersPercent};" escape="false">
            <f:convertNumber type="percent"/>
        </h:outputText>
        <h:outputText value="#{item.numberOfEmployeesWithDiscountOrdersPercent};" escape="false">
            <f:convertNumber type="percent"/>
        </h:outputText>--%>
        <h:outputText value="#{item.numberOfPayOrders};" escape="false"/>
        <%--<h:outputText value="#{item.firstPayOrderDate};" escape="false"/>
        <h:outputText value="#{item.numberOfStudentsWithPayedOrdersPercent};" escape="false">
            <f:convertNumber type="percent"/>
        </h:outputText>
        <h:outputText value="#{item.numberOfEmployeesWithPayedOrdersPercent};" escape="false">
            <f:convertNumber type="percent"/>
        </h:outputText>
        <h:outputText value="#{item.numberOfVendingOrders};" escape="false"/>--%>
        <h:outputText value="#{item.orgDistrict};" escape="false"/>
        <h:outputText value="#{item.orgLocation};" escape="false"/>
        <h:outputText value="#{item.orgTag};" escape="false"/>
        <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>
