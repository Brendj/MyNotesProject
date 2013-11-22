<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
    taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
    taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
    taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
    taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%
    response.setHeader("Content-disposition", "inline;filename=card_load_results.csv");
%><f:view>
    <a4j:repeat value="#{mainPage.cardFileLoadPage.lineResults}" var="item">
        <h:outputText escape="false"
                      value="#{item.lineNo};#{item.resultCode};#{item.message};#{item.idOfCard}#{mainPage.endOfLine}"/>
    </a4j:repeat>
</f:view>