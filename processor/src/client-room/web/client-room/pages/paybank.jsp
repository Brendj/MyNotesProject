<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>
<%@ page import="javax.xml.ws.BindingProvider" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.BankItem" %>
<%@ page import="java.util.List" %>

<%
    ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
    /*ClientRoomControllerWSService service = new ClientRoomControllerWSService();
    ClientRoomController port
            = service.getClientRoomControllerWSPort();
    ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/processor/soap/client");
*/
    ru.axetta.ecafe.processor.web.bo.client.ClientRoomController port=clientAuthToken.getPort();

   List<BankItem> banks=port.getBanks().getBanksList().getBanks();
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
