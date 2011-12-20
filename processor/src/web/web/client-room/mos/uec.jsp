<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ru.axetta.ecafe.processor.web.ClientAuthToken" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%
    boolean isLoggedIn=true;
    if (null == ClientAuthToken.loadFrom(session)) {
%>
	<jsp:include page="../pages/login_check.jsp" />

<%
	    if (null == ClientAuthToken.loadFrom(session)) {
		isLoggedIn=false;
	    }
    } else {
        String pageName = request.getParameter("page");
      	 if (pageName!=null && pageName.equals("logout")) {
       	     isLoggedIn = false;
	 }
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ru" lang="ru">
<head>
	<!-- initialize keyboard (required) -->
     <title>УЭК - Образование</title>
     <link rel="icon" href="favicon.ico"/>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
     <link href="styles.css" type="text/css" rel="stylesheet" />

	<!-- jQuery & jQuery UI + theme (required) -->
	<link href="css/jquery-ui.css" rel="stylesheet">
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.6/jquery.min.js"></script>
	<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>


	<!-- keyboard widget css & script (required) -->
	<link href="css/keyboard.css" rel="stylesheet"/ >
	<script src="js/jquery.keyboard.js"></script>
	<script src="js/russian.js"></script>
<script>
<%
if (!isLoggedIn) {
%>
$(function(){
var k = {
layout: 'custom',
display: {
'a': 'Да',
'c': 'X'
},
customLayout: {
'default' : [
'{clear} {bksp}',
'7 8 9',
'4 5 6',
'1 2 3',
'  0  ',
'{a} {c}'
]},
restrictInput : true,
autoAccept : true
};
$(':text').keyboard(k);
$(':password').keyboard(k);
});
<%
} else {
%>
$(function(){
var k = {
layout: 'russian-qwerty',
restrictInput : true,
autoAccept : true
};
$(':text').keyboard(k);
$(':password').keyboard(k);
});
<%
}
%>

 
</script>
</head>
<body id="<%=isLoggedIn?"innerbody":"outerbody"%>">
<%
if (!isLoggedIn) {
%>
<style>
.ui-widget textarea, .ui-widget button {
	font-family: Tahoma;
    font-size: 1.4em;
}
.output-text {
    font-size: 1.3em;
}
.input-text {
    font-size: 1.3em;
}
.command-button {
     padding-top: 15px;
     padding-bottom: 15px;
     font-size: 1.4em;
     padding-left: 10px;
     padding-right: 10px;
     margin-top: 30px;
     margin-bottom: 10px;
}
</style>

<div class="pagebg">
<div class="main">
	<div id="s5"></div>
	<div id="content">
<table>
<c:import url="../no-styles-inlinecabinet.jsp"/>
</table>
</div>
	</div>
<% } else { %>
<div class="topbg">
	<div class="topmenu">
		<div class="logo"></div>
		<div class="toplinks" style="padding-left: 0px;"></div>
		<div class="toplinks">
			<img alt="������ �������" src="images/personal_room_inner.png ">
		</div>
		<div class="toplinks lastbg" style="padding: 17px 32px 0px 0px; float: right;"></div>
	</div>
</div>
<table cellspacing="0" cellpadding="0" border="0" style="width: 100%; border-collapse: collapse; margin-bottom: 20px;">
<tbody>
<tr>
<td class="a_td">
<div class="shapkabgleft"></div>
</td>
<td style="width: 1200px;">
<h1 class="head_title">�������������� ������ ������������� ����������� �����</h1>
</td>
<td class="b_td">
<div class="shapkabgright"></div>
</td>
</tr>
</tbody>
</table>

<div class="firstbg">
<div class="secondbg">
<div class="innerpage">
	<div id="content">
<table>
<%
request.setAttribute("hidePages", "show-library;show-diary");
%>

<c:import url="../no-styles-inlinecabinet.jsp"/>
</table>
</div>
</div>
</div>
</div>
</div>
<% } %>
<div id="footer"/>
	<div class="copyright">
		<span>&copy; 2011. Универсальная электронная карта в образовании</span>
		<span class="rights">Все права защищены</span>
<div class="login-page" style="display:none" id="infgorod"></div>	</div>

</div>
</div>




</body>
