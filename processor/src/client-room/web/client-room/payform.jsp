<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientRoomController" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientSummaryExt" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientSummaryResult" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="windows-1251" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<html lang="ru">
<head>
<%

    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.payform_jsp");

    final Long RC_CLIENT_NOT_FOUND = 110L;
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


    Long contractId = null;
    boolean haveDataToShow=true;
    if (StringUtils.isNotEmpty(request.getParameter("contractId"))) {
        try {
            contractId = Long.parseLong(request.getParameter("contractId"));
        } catch (NumberFormatException e) {
            logger.error("Failed to get contractId", e);
        }
    }

    if (null != contractId) {

        try {

             ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);

             ClientRoomController port=clientAuthToken.getPort();
             ClientSummaryResult summaryResult= port.getSummary(contractId);

              if(!RC_OK.equals(summaryResult.getResultCode())){
              haveDataToShow=false;
              throw new Exception(summaryResult.getDescription());

              }

             ClientSummaryExt summaryExt=summaryResult.getClientSummary();

            session.setAttribute("__payform.client", summaryExt);

        } catch (RuntimeContext.NotInitializedException e) {
            throw new UnavailableException(e.getMessage());
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {

        }
    }

    if(haveDataToShow){
%>
<title>Новая школа - Печать квитанции на пополнение счета</title>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1251">
<meta http-equiv="Content-Language" content="ru">
<jsp:include page="payform_css.jsp"/>
<body>

<div id="toolbox">
    <p>Прежде чем отправлять документ на печать, воспользуйтесь предварительным просмотром (<i>Print preview</i>)
        и убедитесь в корректном отображении документа.
        Обычно квитанция формы «№&nbsp;ПД-4» свободно располагается
        на&nbsp;листе формата А4 и&nbsp;не&nbsp;требует особых настроек
        печати. В&nbsp;редких случаях может потребоваться уменьшить
        боковые поля листа до&nbsp;10–15&nbsp;мм или&nbsp;изменить
        ориентацию страницы на&nbsp;горизонтальную (<i>landscape</i>), чтобы квитанция полностью поместилась в&nbsp;печатное
        поле.</p>
    <input value="Напечатать" onclick="window.print();" type="button" />
    <input value="Закрыть" onclick="window.close();" type="button" />
    <center><span style="font-size: 80%;">информационный блок от начала страницы до пунктирной линии на печать не выводится</span>
    </center>
</div>

<jsp:include page="payform_receipt.jsp"/>

</body>
</html>
<%}else{

 %>
<div class="error-output-text"> Не удалось отобразить квитанцию </div>
<%
}%>