<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
    taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
    taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
    taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
    taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%
    response.setHeader("Content-disposition", "inline;filename=new_visitor_load_result_csv_list.csv");
%><f:view>
    <a4j:repeat value="#{mainPage.visitorDogmLoadPage.lineResults}" var="item">
        <h:outputText escape="false"
                      value="#{item.lineNo};#{item.resultCode};#{item.message};#{item.idOfVisitor}#{mainPage.endOfLine}"/>
    </a4j:repeat>
</f:view>