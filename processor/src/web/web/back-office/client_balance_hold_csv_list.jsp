<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
        taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
        taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
        taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
        taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%
    response.setHeader("Content-disposition", "inline;filename=client_balance_hold.csv");
%><f:view>
    <h:outputText escape="false" value="Дата и время заявления;Номер л/с обучающегося;ФИО обучающегося;Номер л/с заявителя;Телефон заявителя;ФИО заявителя;ИНН заявителя;Р/с заявителя;Наименование банка;БИК банка;Корр. счет банка;Размер баланса;Сумма возврата;Статус заявления"/>
    <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    <a4j:repeat value="#{clientBalanceHoldPage.items}" var="item">
        <h:outputText escape="false" value="#{item.createdDateStr};" />
        <h:outputText escape="false" value="#{item.contractId};#{item.fio};#{item.declarerContractId};#{item.declarerPhone};#{item.declarerFio};" />
        <h:outputText escape="false" value="#{item.inn};" />
        <h:outputText escape="false" value="#{item.rs};" />
        <h:outputText escape="false" value="#{item.bank};" />
        <h:outputText escape="false" value="#{item.bik};" />
        <h:outputText escape="false" value="#{item.korr};" />
        <h:outputText escape="false" value="#{item.balanceStr};" />
        <h:outputText escape="false" value="#{item.balanceHoldStr};" />
        <h:outputText escape="false" value="#{item.requestStatus}" />
        <h:outputText escape="false" value="#{mainPage.endOfLine}" />
    </a4j:repeat>
</f:view>