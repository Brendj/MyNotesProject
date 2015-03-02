<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.PublicationInstancesItem" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.PublicationItemList" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.dataflow.PublicationListResult" %>
<%@ page import="ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.util.Arrays" %>

<%
    final Logger logger = LoggerFactory.getLogger("ru.axetta.ecafe.processor.web.client-room.pages.show-menu_jsp");
    final String PROCESS_PARAM = "submit";
    final String SEARCH_CONDITION = "search-condition";
    final String OFFSET = "offset";
    final String PARAMS_TO_REMOVE[] = {PROCESS_PARAM};

    try {
        ClientAuthToken clientAuthToken = ClientAuthToken.loadFrom(session);
        URI formAction;
        try {
            formAction = UriUtils
                    .removeParams(ServletUtils.getHostRelativeUriWithQuery(request), Arrays.asList(PARAMS_TO_REMOVE));
        } catch (Exception e) {
            logger.error("Failed to build form action", e);
            throw new ServletException(e);
        }

        boolean haveDataToProcess = StringUtils.isNotEmpty(request.getParameter(PROCESS_PARAM));
        String searchCondition = "";
        int limit = 10;
        int offset = 0;
        int amountForCondition = 0;

        try {
            searchCondition = StringUtils.defaultString(request.getParameter(SEARCH_CONDITION));
            offset = Integer.parseInt(StringUtils.defaultString(request.getParameter(OFFSET)));
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to read data", e);
            }
        }
%>

<form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formAction.toString()))%>" method="post"
      enctype="application/x-www-form-urlencoded" class="borderless-form">
    <table>
        <tr>
            <td>
                <div class="output-text">Условие поиска в каталоге библиотеки:</div>
            </td>
            <td>
                <input type="text" name="<%=SEARCH_CONDITION%>" size="64" maxlength="128" class="input-text"
                       value="<%=searchCondition%>" />
            </td>
        </tr>
        </tr>
        <tr>
            <td>
                <input type="submit" name="<%=PROCESS_PARAM%>" value="Показать" class="command-button" />
            </td>
        </tr>
    </table>
</form>
<%
    URI currentUri = UriUtils
            .removeParams(ServletUtils.getHostRelativeUriWithQuery(request), Arrays.asList(PARAMS_TO_REMOVE));
    ClientRoomController port=clientAuthToken.getPort();
    PublicationListResult publicationListResult=port.getPublicationListSimple(clientAuthToken.getContractId(),searchCondition, limit, offset);
    amountForCondition = publicationListResult.amountForCondition;
    PublicationItemList pubList = publicationListResult.publicationList;
    String prev_disabled = "disabled";
    String next_disabled = "";
    if (offset > 0) {
        prev_disabled = "";
    }
    if (amountForCondition - offset < limit) {
        next_disabled = "disabled";
    }
%>
<div class="output-text">Найдено книг:<%=amountForCondition%>
</div>
<table>
    <tr>
        <td>
            <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formAction.toString()))%>" method="post"
                  enctype="application/x-www-form-urlencoded" class="borderless-form">
                <input type="submit" name="<%=PROCESS_PARAM%>" value="Предыдущая страница" class="command-button" <%=prev_disabled%> />
                <input type="hidden" name="<%=SEARCH_CONDITION%>" value="<%=searchCondition%>" />
                <input type="hidden" name="<%=OFFSET%>" value="<%=offset-limit%>" />
            </form>
        </td>
        <td>
            <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formAction.toString()))%>" method="post"
                  enctype="application/x-www-form-urlencoded" class="borderless-form">
                <input type="submit" name="<%=PROCESS_PARAM%>" value="Следующая страница" class="command-button" <%=next_disabled%> />
                <input type="hidden" name="<%=SEARCH_CONDITION%>" value="<%=searchCondition%>" />
                <input type="hidden" name="<%=OFFSET%>" value="<%=offset+limit%>" />
            </form>
        </td>
        <td>
            <a class="command-link" href="<%=StringEscapeUtils.escapeHtml(response.encodeURL(UriUtils.putParam(currentUri, "page", "show-publications-advanced").toString()))%>">Расширенный поиск</a>
        </td>
    </tr>
</table>

<table>
    <tr>
        <td>
            <div class="output-text">Автор</div>
        </td>
        <td>
            <div class="output-text">Заглавие</div>
        </td>
        <td>
            <div class="output-text">Продолжение заглавия</div>
        </td>
        <td>
            <div class="output-text">Дата издания</div>
        </td>
        <td>
            <div class="output-text">Издатель</div>
        </td>
        <td>
            <div class="output-text">Всего экземпляров</div>
        </td>
        <td>
            <div class="output-text">Доступно к выдаче</div>
        </td>
    </tr>
<%
    for (PublicationInstancesItem publicationIns : pubList.getC()) {
%>

    <tr>
        <td>
            <%=publicationIns.getPublication().getAuthor()%>
        </td>
        <td>
            <%=publicationIns.getPublication().getTitle()%>
        </td>
        <td>
            <%=publicationIns.getPublication().getTitle2()%>
        </td>
        <td>
            <%=publicationIns.getPublication().getPublicationDate()%>
        </td>
        <td>
            <%=publicationIns.getPublication().getPublisher()%>
        </td>
        <td>
            <%=publicationIns.getInstancesAmount().toString()%>
        </td>
        <td>
            <%=publicationIns.getInstancesAvailable().toString()%>
        </td>
    </tr>
<%
        }
    } catch (RuntimeContext.NotInitializedException e) {
        throw new UnavailableException(e.getMessage());
    }
%>
</table>