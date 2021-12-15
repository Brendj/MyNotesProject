<%@ page contentType="text/csv; charset=windows-1251" language="java" pageEncoding="windows-1251" %><%@
        taglib prefix="f" uri="http://java.sun.com/jsf/core" %><%@
        taglib prefix="h" uri="http://java.sun.com/jsf/html" %><%@
        taglib prefix="rich" uri="http://richfaces.org/rich" %><%@
        taglib prefix="a4j" uri="http://richfaces.org/a4j" %><%
    response.setHeader("Content-disposition", "inline;filename=loadingElementsOfBasicGoods.csv");
%><f:view>
    <h:outputText escape="false" value="№;Производственная конфигурация;Наименование базового товара;Единица измерения;Масса нетто (грамм);Результат;"/>
    <h:outputText escape="false" value="#{mainPage.endOfLine}"/>
    <a4j:repeat value="#{mainPage.loadingElementsOfBasicGoodsPage.loadingElementsOfBasicGoodsItems}" var="item">
        <h:outputText escape="false"
                      value="#{item.rowNum};#{item.nameOfGood};#{item.configurationProviderName};#{item.unitsScale};#{item.netWeight};#{item.result};"/>
        <h:outputText escape="false" value="#{mainPage.endOfLine}"/>
    </a4j:repeat>
</f:view>

