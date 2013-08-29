<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: chirikov
  Date: 30.07.13
  Time: 13:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.output-text-mod {
font-family: Tahoma, Arial, Sans-Serif;
font-size: 10pt;
color: #000;
white-space: nowrap;
padding-right: 10px;
}
</style>

<%--@elvariable id="setupDiscountPage" type="ru.axetta.ecafe.processor.web.ui.feed.FeedPlanPage"--%>
<a4j:form id="setupDiscountForm">
    <h:panelGrid id="setupDiscountGrid" binding="#{setupDiscountPage.pageComponent}" styleClass="borderless-grid" style="width: 100%;">
    </h:panelGrid>
</a4j:form>