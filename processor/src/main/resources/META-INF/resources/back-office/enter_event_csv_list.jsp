<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
        taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
        taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
        taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
        taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%
    response.setHeader("Content-disposition", "inline;filename=enterEvent.csv");
%><f:view>
    <h:outputText escape="false" value="№;Номер учреждения;Название учреждения;Наименование входа;Адрес турникета;Направление прохода;Код события;Номер договора;Фамилия и Имя учащегося;Дата события" />
    <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    <a4j:repeat value="#{mainPage.enterEventReportPage.enterEventReport.enterEventItems}" var="item">
        <h:outputText escape="false" value="#{item.idofenterevent};#{item.idoforg};#{item.officialname};#{item.entername};#{item.turnstileaddr};#{item.passdirection};#{item.eventCode};#{item.docserialnum};#{item.personFullName};" />
        <h:outputText escape="false" value="#{item.evtdatetime}" converter="timeConverter" />
        <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>
