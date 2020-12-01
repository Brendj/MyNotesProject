<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.BankItem" %>
<%@ page import="java.util.List" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.BanksData" %>

<%  final Long RC_CLIENT_NOT_FOUND = 110L;
    final Long RC_SEVERAL_CLIENTS_WERE_FOUND = 120L;
    final Long RC_INTERNAL_ERROR = 100L, RC_OK = 0L;
    final Long RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS = 130L;
    final Long RC_CLIENT_HAS_THIS_SNILS_ALREADY = 140L;
    final Long RC_INVALID_DATA = 150L;
    final Long RC_NO_CONTACT_DATA = 160L;
    final Long RC_PARTNER_AUTHORIZATION_FAILED = -100L;
    final Long RC_CLIENT_AUTHORIZATION_FAILED = -101L;

    final String RC_OK_DESC="OK";
    final String RC_CLIENT_NOT_FOUND_DESC="Клиент не найден";
    final String RC_SEVERAL_CLIENTS_WERE_FOUND_DESC="По условиям найден более одного клиента";
    final String RC_CLIENT_AUTHORIZATION_FAILED_DESC="Ошибка авторизации клиента";
    final String RC_INTERNAL_ERROR_DESC="Внутренняя ошибка";
    final String RC_NO_CONTACT_DATA_DESC="У лицевого счета нет контактных данных";

    boolean haveDataToShow=true;
    ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);

    ru.axetta.ecafe.processor.web.bo.client.ClientRoomController port=clientAuthToken.getPort();
    BanksData banksData=port.getBanks();

    haveDataToShow=RC_OK.equals(banksData.getResultCode());

    if(haveDataToShow){

   List<BankItem> banks=banksData.getBanksList().getBanks();
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
                                       href="<%=StringEscapeUtils.escapeHtml(String.format("/processor2/client-room/payform.jsp?contractId=%s", ContractIdFormat.format(clientAuthToken.getContractId())))%>">ссылке
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
                <%
                    for(BankItem bank:banks){

                %>
                <tr>
                    <td>
                        <img src="<%=StringEscapeUtils.escapeHtml(bank.getLogoUrl())%>"
                             alt="Банк <%=bank.getIdOfBank().toString()%>" />

                        <div class="output-text"></div>
                    </td>
                    <td>
                        <div class="output-text"><b><%=StringEscapeUtils.escapeHtml(bank.getName())%></b><br /><a class="command-link"
                                                                            href="<%=StringEscapeUtils.escapeHtml(bank.getTerminalsUrl())%>">Адреса
                            филиалов и банкоматов</a></div>
                    </td>
                    <td>
                        <div class="output-text"><%=StringEscapeUtils.escapeHtml(bank.getRate().toString())%>%, но не менее <%=StringEscapeUtils.escapeHtml(bank.getMinRate().toString())%> руб. (при оплате в банкомате <font color="red"><%=StringEscapeUtils.escapeHtml(bank.getEnrollmentType())%></font>)</div>
                    </td>
                </tr>

                 <%}%>



            </table>
        </td>
    </tr>
</table>
    <%}else{

        %>
<div class="error-output-text"> Не удалось отобразить данные банков </div>
<%
    }%>