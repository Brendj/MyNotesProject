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
<%
    response.setContentType("text/xml");
    response.setHeader("Content-disposition", "inline;filename=Menu_data.xml");
%>
<f:view>
    <h:outputText escape="false" value="#{mainPage.selectedMenuDataXML}" />
</f:view>
