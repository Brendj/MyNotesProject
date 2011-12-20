<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>

<%
    ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
%>
<table class="borderless-grid">
    <tr>
        <td>
            <div class="output-text">Оплата через банк</div>
        </td>
    </tr>
    <tr>
        <td>
            <div class="output-text">Для оплаты через банк Вам необходимо распечатать бланк квитанции. Для формирования
                бланка перейдите по <a class="command-link"
                                       href="<%=StringEscapeUtils.escapeHtml(String.format("/processor/client-room/payform.jsp?contractId=%s", ContractIdFormat.format(clientAuthToken.getContractId())))%>">ссылке
                    - сформировать квитанцию</a></div>
            <table class="borderless-grid">
                <tr>
                    <td align="center" colspan="2">
                        <div class="output-text">Наименование</div>
                    </td>
                    <td align="center">
                        <div class="output-text">Размер комиссии</div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <img src="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "/processor", "images/banks/sber.png"))%>"
                             alt="Сбербанк России" />

                        <div class="output-text"></div>
                    </td>
                    <td>
                        <div class="output-text"><b>Сбербанк России (Татарстан)</b><br /><a class="command-link"
                                                                            href="http://sberbank.ru/tatarstan/ru/about/branch/list_branch/">Адреса
                            филиалов и банкоматов</a></div>
                    </td>
                    <td>
                        <div class="output-text">0.5%, но не менее 10 руб. (при оплате в банкомате <font color="red">онлайн-зачисление средств</font>)</div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <img src="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "/processor", "images/banks/tfb.png"))%>"
                             alt="Татфондбанк" />
                    </td>
                    <td>
                        <div class="output-text"><b>Татфондбанк</b><br><a class="command-link"
                                                                          href="http://tfb.ru/index.php?page=content&id=2513">Адреса
                            филиалов</a></div>
                    </td>
                    <td>
                        <div class="output-text">1.5%, но не менее 20 руб.</div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <img src="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "/processor", "images/banks/abb.png"))%>"
                             alt="Ак барс Банк" />
                    </td>
                    <td>
                        <div class="output-text"><b>Ак Барс Банк</b><br><a class="command-link"
                                                                           href="http://www.akbars.ru/about/branches/rt/kazan/">Адреса
                            филиалов</a></div>
                    </td>
                    <td>
                        <div class="output-text">2%, но не менее 25 руб.</div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
