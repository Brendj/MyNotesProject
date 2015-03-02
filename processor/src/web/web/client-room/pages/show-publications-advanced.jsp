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
    final String AUTHOR = "author";
    final String TITLE = "title";
    final String TITLE2 = "title2";
    final String PUBLICATION_DATE = "publication-date";
    final String PUBLISHER = "publisher";
    final String ISBN = "isbn";
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
        String author = "";
        String title = "";
        String title2 = "";
        String publicationDate = "";
        String publisher = "";
        String isbn = "";
        int limit = 10;
        int offset = 0;
        int amountForCondition = 0;

        if (haveDataToProcess) {
            try {
                author = StringUtils.defaultString(request.getParameter(AUTHOR));
                title = StringUtils.defaultString(request.getParameter(TITLE));
                title2 = StringUtils.defaultString(request.getParameter(TITLE2));
                publicationDate = StringUtils.defaultString(request.getParameter(PUBLICATION_DATE));
                publisher = StringUtils.defaultString(request.getParameter(PUBLISHER));
                isbn = StringUtils.defaultString(request.getParameter(ISBN));
                offset = Integer.parseInt(StringUtils.defaultString(request.getParameter(OFFSET)));
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to read data", e);
                }
            }
        } else {
        }
%>

<div class="output-text">Расширенный поиск в каталоге библиотеки</div>
<form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formAction.toString()))%>" method="post"
      enctype="application/x-www-form-urlencoded" class="borderless-form">
    <table>
        <tr>
            <td>
                <div class="output-text">Автор:</div>
            </td>
            <td>
                <input type="text" name="<%=AUTHOR%>" size="64" maxlength="128" class="input-text"
                       value="<%=author%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Название:</div>
            </td>
            <td>
                <input type="text" name="<%=TITLE%>" size="64" maxlength="128" class="input-text"
                       value="<%=title%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Продолжение названия:</div>
            </td>
            <td>
                <input type="text" name="<%=TITLE2%>" size="64" maxlength="128" class="input-text"
                       value="<%=title2%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Год издания:</div>
            </td>
            <td>
                <input type="text" name="<%=PUBLICATION_DATE%>" size="32" maxlength="32" class="input-text"
                       value="<%=publicationDate%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">Издательство:</div>
            </td>
            <td>
                <input type="text" name="<%=PUBLISHER%>" size="64" maxlength="128" class="input-text"
                       value="<%=publisher%>" />
            </td>
        </tr>
        <tr>
            <td>
                <div class="output-text">ISBN:</div>
            </td>
            <td>
                <input type="text" name="<%=ISBN%>" size="32" maxlength="32" class="input-text"
                       value="<%=isbn%>" />
            </td>
        </tr>
        <tr>
            <td>
                <input type="submit" name="<%=PROCESS_PARAM%>" value="Показать" class="command-button" />
            </td>
        </tr>
    </table>
</form>
<%
    ClientRoomController port=clientAuthToken.getPort();
    PublicationListResult publicationListResult=port.getPublicationListAdvanced(clientAuthToken.getContractId(),
            author, title, title2, publicationDate, publisher, isbn, limit, offset);
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
                <input type="hidden" name="<%=AUTHOR%>" value="<%=author%>" />
                <input type="hidden" name="<%=TITLE%>" value="<%=title%>" />
                <input type="hidden" name="<%=TITLE2%>" value="<%=title2%>" />
                <input type="hidden" name="<%=PUBLICATION_DATE%>" value="<%=publicationDate%>" />
                <input type="hidden" name="<%=PUBLISHER%>" value="<%=publisher%>" />
                <input type="hidden" name="<%=ISBN%>" value="<%=isbn%>" />
                <input type="hidden" name="<%=OFFSET%>" value="<%=offset-limit%>" />
            </form>
        </td>
        <td>
            <form action="<%=StringEscapeUtils.escapeHtml(response.encodeURL(formAction.toString()))%>" method="post"
                  enctype="application/x-www-form-urlencoded" class="borderless-form">
                <input type="submit" name="<%=PROCESS_PARAM%>" value="Следующая страница" class="command-button" <%=next_disabled%> />
                <input type="hidden" name="<%=AUTHOR%>" value="<%=author%>" />
                <input type="hidden" name="<%=TITLE%>" value="<%=title%>" />
                <input type="hidden" name="<%=TITLE2%>" value="<%=title2%>" />
                <input type="hidden" name="<%=PUBLICATION_DATE%>" value="<%=publicationDate%>" />
                <input type="hidden" name="<%=PUBLISHER%>" value="<%=publisher%>" />
                <input type="hidden" name="<%=ISBN%>" value="<%=isbn%>" />
                <input type="hidden" name="<%=OFFSET%>" value="<%=offset+limit%>" />
            </form>
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