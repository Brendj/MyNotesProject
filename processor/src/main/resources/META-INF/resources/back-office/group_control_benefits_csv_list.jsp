<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
        taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
        taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
        taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
        taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%
    response.setHeader("Content-disposition", "inline;filename=groupControlBenefits.csv");
%><f:view>
    <h:outputText escape="false" value="№;Наименование ОУ;Группа (класс);Фамилия;Имя;Отчество;Номер Л/с;Льготы;Результат" />
    <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    <a4j:repeat value="#{mainPage.groupControlBenefitsPage.groupControlBenefitsItems}" var="item">
        <h:outputText escape="false" value="#{item.rowNum};#{item.orgName};#{item.groupName};#{item.surname};#{item.firstName};#{item.secondName};#{item.contractId};#{item.benefits};#{item.result};" />
        <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>