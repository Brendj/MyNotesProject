<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Card" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Person" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.AbbreviationUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.HibernateUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.hibernate.Criteria" %>
<%@ page import="org.hibernate.Transaction" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.criterion.Restrictions" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.net.URI" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.nio.charset.Charset" %>
<%@ page import="org.apache.commons.lang.CharEncoding" %>
<html lang="ru">
<head>
<%
    if (StringUtils.isEmpty(request.getCharacterEncoding())) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

	 String fieldReceiver=(request.getParameter("fieldReceiver")!=null?request.getParameter("fieldReceiver"):"");
	String fieldAccount=(request.getParameter("fieldAccount")!=null?request.getParameter("fieldAccount"):"");
	String fieldINN=(request.getParameter("fieldINN")!=null?request.getParameter("fieldINN"):"");
	String fieldBank=(request.getParameter("fieldBank")!=null?request.getParameter("fieldBank"):"");
	String fieldBIK=(request.getParameter("fieldBIK")!=null?request.getParameter("fieldBIK"):"");
	String fieldCorrAcc=(request.getParameter("fieldCorrAcc")!=null?request.getParameter("fieldCorrAcc"):"");


	String paySum=(String)request.getParameter("paySum");

    Client client = (Client)session.getAttribute("__payform.client");
    Person person = client.getContractPerson();
    String clientAbbreviation = AbbreviationUtils
                    .buildAbbreviation(person.getFirstName(), person.getSurname(), person.getSecondName());
    String clientAddress = client.getAddress();
    Long contractId = client.getContractId();

/////
/*    if (client!=null && client.getOrg()!=null && client.getOrg().getIdOfOrg()==1) {
        fieldReceiver="ООО &quot;ЕвроШкола-Поволжье&quot;";
        fieldAccount="40702810100010001801";
        fieldINN="1656045695";
    } */
/////
%>

<table class="ramka" cellspacing="0" style="width: 180mm;">
<tr>
<td style="width: 50mm; height: 65mm; border-bottom: black 1.5px solid;">
    <table style="width: 50mm; height: 100%;" cellspacing="0">

        <tr>
            <td class="kassir" style="vertical-align: top; letter-spacing: 0.2em;">Извещение</td>
        </tr>
        <tr>
            <td class="kassir" style="vertical-align: bottom;">Кассир</td>
        </tr>
    </table>
</td>
<td style="width: 130mm; height: 65mm; padding: 0mm 4mm 0mm 3mm; border-left: black 1.5px solid; border-bottom: black 1.5px solid;">

<table cellspacing="0" align="center" style="width: 123mm; height: 100%">
<tr>
    <td>
        <table width="100%" cellspacing="0">
            <tr>
                <td style="height: 5mm;"></td>
                <td class="stext7" style="text-align: right; vertical-align: middle;"><i>Форма &#8470;
                    ПД-4</i></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table style="width: 100%; height: 100%;" cellspacing="0">
            <tr>
                <td class="string"><span class="nowr"><%=fieldReceiver%></span></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td class="subscript nowr">(наименование получателя платежа)</td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td width="30%" class="floor">
                    <table class="cells" cellspacing="0">
                        <tr>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(0)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(1)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(2)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(3)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(4)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(5)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(6)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(7)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(8)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(9)%></td>
                        </tr>
                    </table>
                </td>
                <td width="10%" class="stext7">&nbsp;</td>
                <td width="60%" class="floor">
                    <table class="cells" cellspacing="0">
                        <tr>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(0)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(1)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(2)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(3)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(4)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(5)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(6)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(7)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(8)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(9)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(10)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(11)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(12)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(13)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(14)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(15)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(16)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(17)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(18)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(19)%></td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="subscript nowr">(ИНН получателя платежа)</td>
                <td class="subscript">&nbsp;</td>
                <td class="subscript nowr">(номер счета получателя платежа)</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td width="2%" class="stext">в</td>
                <td width="64%" class="string"><span class="nowr"><%=fieldBank%></span>
                </td>
                <td width="7%" class="stext" align="right">БИК&nbsp;</td>
                <td width="27%" class="floor">
                    <table class="cells" cellspacing="0">
                        <tr>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(0)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(1)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(2)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(3)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(4)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(5)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(6)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(7)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(8)%></td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="subscript">&nbsp;</td>
                <td class="subscript nowr">(наименование банка получателя платежа)</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext7 nowr" width="40%">Номер кор./сч. банка получателя платежа</td>
                <td width="60%" class="floor">
                    <table class="cells" cellspacing="0">
                        <tr>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(0)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(1)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(2)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(3)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(4)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(5)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(6)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(7)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(8)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(9)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(10)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(11)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(12)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(13)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(14)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(15)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(16)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(17)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(18)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(19)%></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="string" width="55%"><span class="nowr">За оплату питания</span></td>
                <td class="stext7" width="5%">&nbsp;</td>
                <td class="string" width="40%"><span class="nowr">
                    <%if (null != contractId) {%>
                        <%=StringEscapeUtils.escapeHtml(ContractIdFormat.format(contractId))%>
                    <%}%>
                </span></td>
            </tr>
            <tr>
                <td class="subscript nowr">(наименование платежа)</td>
                <td class="subscript">&nbsp;</td>
                <td class="subscript nowr">(номер лицевого счета (код) плательщика)</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext" width="1%">Ф.И.О&nbsp;плательщика&nbsp;</td>
                <td class="string"><span class="nowr"><%=StringEscapeUtils
                        .escapeHtml(StringUtils.defaultString(clientAbbreviation))%></span></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext" width="1%">Адрес&nbsp;плательщика&nbsp;</td>
                <td class="string"><span class="nowr"><%=StringEscapeUtils
                        .escapeHtml(StringUtils.defaultString(clientAddress))%></span></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext" width="1%">Сумма&nbsp;платежа&nbsp;</td>
                <td class="string" width="8%"><%=paySum==null?"&nbsp":paySum%></td>
                <td class="stext" width="1%">&nbsp;руб.&nbsp;</td>
                <td class="string" width="8%"><%=paySum==null?"&nbsp":"00"%></td>
                <td class="stext" width="1%">&nbsp;коп.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Сумма&nbsp;платы&nbsp;за&nbsp;услуги&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;руб.&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;коп.</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext" width="5%">Итого&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="5%">&nbsp;руб.&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="5%">&nbsp;коп.&nbsp;</td>
                <td class="stext" width="20%" align="right">&laquo;&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;&raquo;&nbsp;</td>
                <td class="string" width="20%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td class="stext" width="3%">&nbsp;20&nbsp;</td>
                <td class="string" width="5%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;г.</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td class="stext7" style="text-align: justify">С условиями приема указанной в платежном документе
        суммы, в т.ч. с суммой взимаемой платы за&nbsp;услуги банка,&nbsp;ознакомлен&nbsp;и&nbsp;согласен.
    </td>
</tr>
<tr>
    <td style="padding-bottom: 0.5mm;">
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext7" width="50%">&nbsp;</td>
                <td class="stext7" width="1%"><b>Подпись&nbsp;плательщика&nbsp;</b></td>
                <td class="string" width="40%">&nbsp;</td>
            </tr>
        </table>
    </td>
</tr>
</table>

</td>
</tr>
<tr>
<td style="width: 50mm; height: 80mm; vertical-align: bottom;" class="kassir">Квитанция<br><br>Кассир</td>
<td style="width: 130mm; height: 80mm; padding: 0mm 4mm 0mm 3mm; border-left: black 1.5px solid;">

<table cellspacing="0" align="center" style="width: 123mm; height: 100%">
<tr>
    <td style="height: 8mm;">
        <table style="width: 100%; height: 100%;" cellspacing="0">
            <tr>
                <td class="string"><span class="nowr"><%=fieldReceiver%></span></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td class="subscript nowr">(наименование получателя платежа)</td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td width="30%" class="floor">
                    <table class="cells" cellspacing="0">
                        <tr>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(0)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(1)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(2)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(3)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(4)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(5)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(6)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(7)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(8)%></td>
                            <td class="cell" style="width: 10%;"><%=fieldINN.charAt(9)%></td>
                        </tr>
                    </table>
                </td>
                <td width="10%" class="stext7">&nbsp;</td>
                <td width="60%" class="floor">
                    <table class="cells" cellspacing="0">
                        <tr>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(0)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(1)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(2)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(3)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(4)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(5)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(6)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(7)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(8)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(9)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(10)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(11)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(12)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(13)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(14)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(15)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(16)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(17)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(18)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldAccount.charAt(19)%></td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="subscript nowr">(ИНН получателя платежа)</td>
                <td class="subscript">&nbsp;</td>
                <td class="subscript nowr">(номер счета получателя платежа)</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td width="2%" class="stext">в</td>
                <td width="64%" class="string"><span class="nowr"><%=fieldBank%></span>
                </td>
                <td width="7%" class="stext" align="right">БИК&nbsp;</td>
                <td width="27%" class="floor">
                    <table class="cells" cellspacing="0">
                        <tr>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(0)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(1)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(2)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(3)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(4)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(5)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(6)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(7)%></td>
                            <td class="cell" style="width: 11%;"><%=fieldBIK.charAt(8)%></td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="subscript">&nbsp;</td>
                <td class="subscript nowr">(наименование банка получателя платежа)</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext7 nowr" width="40%">Номер кор./сч. банка получателя платежа</td>
                <td width="60%" class="floor">
                    <table class="cells" cellspacing="0">
                        <tr>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(0)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(1)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(2)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(3)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(4)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(5)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(6)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(7)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(8)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(9)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(10)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(11)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(12)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(13)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(14)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(15)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(16)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(17)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(18)%></td>
                            <td class="cell" style="width: 5%;"><%=fieldCorrAcc.charAt(19)%></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="string" width="55%"><span class="nowr">За оплату питания</span></td>
                <td class="stext7" width="5%">&nbsp;</td>
                <td class="string" width="40%"><span class="nowr">
                    <%if (null != contractId) {%>
                        <%=StringEscapeUtils.escapeHtml(ContractIdFormat.format(contractId))%>
                    <%}%>
                </span></td>
            </tr>
            <tr>
                <td class="subscript nowr">(наименование платежа)</td>
                <td class="subscript">&nbsp;</td>
                <td class="subscript nowr">(номер лицевого счета (код) плательщика)</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext" width="1%">Ф.И.О&nbsp;плательщика&nbsp;</td>
                <td class="string"><span class="nowr"><%=StringEscapeUtils
                        .escapeHtml(StringUtils.defaultString(clientAbbreviation))%></span></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext" width="1%">Адрес&nbsp;плательщика&nbsp;</td>
                <td class="string"><span class="nowr"><%=StringEscapeUtils
                        .escapeHtml(StringUtils.defaultString(clientAddress))%></span></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext" width="1%">Сумма&nbsp;платежа&nbsp;</td>
                <td class="string" width="8%"><%=paySum==null?"&nbsp":paySum%></td>
                <td class="stext" width="1%">&nbsp;руб.&nbsp;</td>
                <td class="string" width="8%"><%=paySum==null?"&nbsp":"00"%></td>
                <td class="stext" width="1%">&nbsp;коп.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Сумма&nbsp;платы&nbsp;за&nbsp;услуги&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;руб.&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;коп.</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext" width="5%">Итого&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="5%">&nbsp;руб.&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="5%">&nbsp;коп.&nbsp;</td>
                <td class="stext" width="20%" align="right">&laquo;&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;&raquo;&nbsp;</td>
                <td class="string" width="20%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td class="stext" width="3%">&nbsp;20&nbsp;</td>
                <td class="string" width="5%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;г.</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td class="stext7" style="text-align: justify">С условиями приема указанной в платежном документе
        суммы, в т.ч. с суммой взимаемой платы за&nbsp;услуги банка,&nbsp;ознакомлен&nbsp;и&nbsp;согласен.
    </td>
</tr>
<tr>
    <td style="padding-bottom: 0.5mm;">
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext7" width="50%">&nbsp;</td>
                <td class="stext7" width="1%"><b>Подпись&nbsp;плательщика&nbsp;</b></td>
                <td class="string" width="40%">&nbsp;</td>
            </tr>
        </table>
    </td>
</tr>
</table>

</td>
</tr>
</table>
