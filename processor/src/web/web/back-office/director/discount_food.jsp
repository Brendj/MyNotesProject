<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Akhmetov
  Date: 26.04.16
  Time: 18:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="directorDiscountFoodGrid" binding="#{directorPage.discountFoodPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText value="#{directorPage.discountFoodPage.helloText}"/>
</h:panelGrid>
