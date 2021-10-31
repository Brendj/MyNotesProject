<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
    taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
    taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
    taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
    taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>
<%--@elvariable id="menuLoadPage" type="ru.axetta.ecafe.processor.web.ui.org.menu.MenuLoadPage"--%>
<%
    response.setHeader("Content-disposition", "inline;filename=menu_load_results.csv");
%><f:view>
    <a4j:repeat value="#{menuLoadPage.lineResults}" var="item" rowKeyVar="row">
        <h:outputText escape="false"
                      value="#{row+1};#{item.resultCode};#{item.message};#{item.description};#{item.id}#{mainPage.endOfLine}"/>
    </a4j:repeat>
</f:view>