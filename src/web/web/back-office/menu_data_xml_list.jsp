<?xml version="1.0" encoding="windows-1251"?>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>


<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 13.01.12
  Time: 0:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/xml; charset=windows-1251" language="java" pageEncoding="windows-1251" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<f:view>
    <menus>
    <%-- --%>
    <a4j:repeat value="#{mainPage.menuViewPage.items}" var="item">
        <menu>
            <idOfMenu><h:outputText value="#{item.idOfMenu}"/></idOfMenu>
            <menuDate><h:outputText value="#{item.menuDate}" converter="timeConverter" /></menuDate>
            <createTime><h:outputText value="#{item.createTime}" converter="timeConverter" /></createTime>
            <menuSource><h:outputText value="#{item.menuSource}"/></menuSource>
            <flag><h:outputText value="#{item.flag}"/></flag>
        </menu>
    </a4j:repeat> 
    </menus>
</f:view>
