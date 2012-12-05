<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="ru.axetta.ecafe.processor.core.client.ContractIdFormat" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Card" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.utils.AbbreviationUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="ru.axetta.ecafe.processor.web.bo.client.ClientSummaryExt" %>
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="java.util.Vector" %>
<%@ page import="ru.axetta.ecafe.processor.web.ServletUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="windows-1251" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<html lang="ru">
<head>
<%
	String fieldReceiver="��� &quot;��������&quot;";
	String fieldAccount="40702810662260004883";
	String fieldINN="1656057429";
	String fieldBank="��������� ����� ��������� � 8610  �.������";
	String fieldBIK="049205603";
	String fieldCorrAcc="30101810600000000603";

	String paySum=(String)request.getParameter("paySum");

    ClientSummaryExt client = (ClientSummaryExt)session.getAttribute("__payform.client");
    String officialName=client.getOfficialName();
     Vector<String>names=new Vector<String>();

    if(officialName!=null){
    StringTokenizer stringTokenizer=new StringTokenizer(officialName," ");

      while(stringTokenizer.hasMoreTokens()){names.add(stringTokenizer.nextToken());}


    }
       String firstName=names.size()>0?names.get(0):null;
       String surname=names.size()>1?names.get(1):null;
       String secondName=names.size()>2?names.get(2):null;

    //Person person = client.getContractPerson();
    String clientAbbreviation = AbbreviationUtils
                    .buildAbbreviation(firstName, surname,secondName);
    String clientAddress = client.getAddress();
    Long contractId = client.getContractId();
/////
/*    if (client!=null && client.getOrg()!=null && client.getOrg().getIdOfOrg()==1) {
        fieldReceiver="��� &quot;���������-��������&quot;";
        fieldAccount="40702810100010001801";
        fieldINN="1656045695";
    } */
/////
        String stringForBarcode=fieldINN+"L"+String.format("%08d",contractId);

%>

<table class="ramka" cellspacing="0" style="width: 180mm;">
<tr>
<td style="width: 50mm; height: 65mm; border-bottom: black 1.5px solid;">
    <table style="width: 50mm; height: 100%;" cellspacing="0">

        <tr>
            <td class="kassir" style="vertical-align: top; letter-spacing: 0.2em;">���������</td>
        </tr>
        <tr>
            <td class="kassir" style="vertical-align: bottom;">������</td>
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
                <td class="stext7" style="text-align: right; vertical-align: middle;"><i>����� &#8470;
                    ��-4</i></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td align="center">

        <img src="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "/processor", "barcode?data="+stringForBarcode+"&rotate=0"))%>" />
        <p align="center"><%=stringForBarcode%></p>
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
    <td class="subscript nowr">(������������ ���������� �������)</td>
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
                <td class="subscript nowr">(��� ���������� �������)</td>
                <td class="subscript">&nbsp;</td>
                <td class="subscript nowr">(����� ����� ���������� �������)</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td width="2%" class="stext">�</td>
                <td width="64%" class="string"><span class="nowr"><%=fieldBank%></span>
                </td>
                <td width="7%" class="stext" align="right">���&nbsp;</td>
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
                <td class="subscript nowr">(������������ ����� ���������� �������)</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext7 nowr" width="40%">����� ���./��. ����� ���������� �������</td>
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
                <td class="string" width="55%"><span class="nowr">�� ������ �������</span></td>
                <td class="stext7" width="5%">&nbsp;</td>
                <td class="string" width="40%"><span class="nowr">
                    <%if (null != contractId) {%>
                        <%=StringEscapeUtils.escapeHtml(ContractIdFormat.format(contractId))%>
                    <%}%>
                </span></td>
            </tr>
            <tr>
                <td class="subscript nowr">(������������ �������)</td>
                <td class="subscript">&nbsp;</td>
                <td class="subscript nowr">(����� �������� ����� (���) �����������)</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext" width="1%">�.�.�&nbsp;�����������&nbsp;</td>
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
                <td class="stext" width="1%">�����&nbsp;�����������&nbsp;</td>
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
                <td class="stext" width="1%">�����&nbsp;�������&nbsp;</td>
                <td class="string" width="8%"><%=paySum==null?"&nbsp":paySum%></td>
                <td class="stext" width="1%">&nbsp;���.&nbsp;</td>
                <td class="string" width="8%"><%=paySum==null?"&nbsp":"00"%></td>
                <td class="stext" width="1%">&nbsp;���.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;�����&nbsp;�����&nbsp;��&nbsp;������&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;���.&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;���.</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext" width="5%">�����&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="5%">&nbsp;���.&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="5%">&nbsp;���.&nbsp;</td>
                <td class="stext" width="20%" align="right">&laquo;&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;&raquo;&nbsp;</td>
                <td class="string" width="20%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td class="stext" width="3%">&nbsp;20&nbsp;</td>
                <td class="string" width="5%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;�.</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td class="stext7" style="text-align: justify">� ��������� ������ ��������� � ��������� ���������
        �����, � �.�. � ������ ��������� ����� ��&nbsp;������ �����,&nbsp;����������&nbsp;�&nbsp;��������.
    </td>
</tr>
<tr>
    <td style="padding-bottom: 0.5mm;">
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext7" width="50%">&nbsp;</td>
                <td class="stext7" width="1%"><b>�������&nbsp;�����������&nbsp;</b></td>
                <td class="string" width="40%">&nbsp;</td>
            </tr>
        </table>
    </td>
</tr>
</table>

</td>
</tr>
<tr>
<td style="width: 50mm; height: 80mm; vertical-align: bottom;" class="kassir">���������<br><br>������</td>
<td style="width: 130mm; height: 80mm; padding: 0mm 4mm 0mm 3mm; border-left: black 1.5px solid;">

<table cellspacing="0" align="center" style="width: 123mm; height: 100%">
<tr>
    <td align="center">
        <br/>
        <img src="<%=StringEscapeUtils.escapeHtml(ServletUtils.getHostRelativeResourceUri(request, "/processor", "barcode?data="+stringForBarcode+"&rotate=0"))%>" />
        <p align="center"><%=stringForBarcode%></p>
    </td>
</tr>
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
    <td class="subscript nowr">(������������ ���������� �������)</td>
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
                <td class="subscript nowr">(��� ���������� �������)</td>
                <td class="subscript">&nbsp;</td>
                <td class="subscript nowr">(����� ����� ���������� �������)</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td width="2%" class="stext">�</td>
                <td width="64%" class="string"><span class="nowr"><%=fieldBank%></span>
                </td>
                <td width="7%" class="stext" align="right">���&nbsp;</td>
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
                <td class="subscript nowr">(������������ ����� ���������� �������)</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext7 nowr" width="40%">����� ���./��. ����� ���������� �������</td>
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
                <td class="string" width="55%"><span class="nowr">�� ������ �������</span></td>
                <td class="stext7" width="5%">&nbsp;</td>
                <td class="string" width="40%"><span class="nowr">
                    <%if (null != contractId) {%>
                        <%=StringEscapeUtils.escapeHtml(ContractIdFormat.format(contractId))%>
                    <%}%>
                </span></td>
            </tr>
            <tr>
                <td class="subscript nowr">(������������ �������)</td>
                <td class="subscript">&nbsp;</td>
                <td class="subscript nowr">(����� �������� ����� (���) �����������)</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext" width="1%">�.�.�&nbsp;�����������&nbsp;</td>
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
                <td class="stext" width="1%">�����&nbsp;�����������&nbsp;</td>
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
                <td class="stext" width="1%">�����&nbsp;�������&nbsp;</td>
                <td class="string" width="8%"><%=paySum==null?"&nbsp":paySum%></td>
                <td class="stext" width="1%">&nbsp;���.&nbsp;</td>
                <td class="string" width="8%"><%=paySum==null?"&nbsp":"00"%></td>
                <td class="stext" width="1%">&nbsp;���.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;�����&nbsp;�����&nbsp;��&nbsp;������&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;���.&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;���.</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext" width="5%">�����&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="5%">&nbsp;���.&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="5%">&nbsp;���.&nbsp;</td>
                <td class="stext" width="20%" align="right">&laquo;&nbsp;</td>
                <td class="string" width="8%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;&raquo;&nbsp;</td>
                <td class="string" width="20%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td class="stext" width="3%">&nbsp;20&nbsp;</td>
                <td class="string" width="5%">&nbsp;</td>
                <td class="stext" width="1%">&nbsp;�.</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td class="stext7" style="text-align: justify">� ��������� ������ ��������� � ��������� ���������
        �����, � �.�. � ������ ��������� ����� ��&nbsp;������ �����,&nbsp;����������&nbsp;�&nbsp;��������.
    </td>
</tr>
<tr>
    <td style="padding-bottom: 0.5mm;">
        <table cellspacing="0" width="100%">
            <tr>
                <td class="stext7" width="50%">&nbsp;</td>
                <td class="stext7" width="1%"><b>�������&nbsp;�����������&nbsp;</b></td>
                <td class="string" width="40%">&nbsp;</td>
            </tr>
        </table>
    </td>
</tr>
</table>

</td>
</tr>
</table>
