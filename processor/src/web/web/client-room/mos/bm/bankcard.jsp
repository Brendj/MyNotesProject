<%@ page import="ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.utils.DAOService" %>
<%@ page import="ru.axetta.ecafe.processor.core.persistence.Client" %>
<%@ page import="ru.axetta.ecafe.processor.core.RuntimeContext" %>
<%@ page import="java.net.URI" %>
<%@ page import="ru.axetta.ecafe.util.UriUtils" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Random" %>
<%@ page import="ru.axetta.ecafe.util.DigitalSignatureUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String action=request.getParameter("action");
    String errorMessage=null;
    if (action!=null && action.equals("complete")) {
        /*Map parameters = request.getParameterMap();
        for(Object parameter : parameters.keySet()) {
            String[] v =(String[])parameters.get(parameter);
            for (int n=0;n<v.length;++n) out.println(parameter+"."+n+"="+v[n]+";");
        } */
        action = null;
    }
    else if (action!=null && action.equals("pay")) {
        String contractId=request.getParameter("contractId"), sum=request.getParameter("sum");
        long lContractId=0;
        try {
            lContractId=Long.parseLong(contractId);
        } catch (Exception e) {
            errorMessage="Номер лицевого счета должен быть цифровой";
        }
        long lSum=0;
        try {
            if (sum==null || sum.length()==0) throw new Exception();
            lSum = CurrencyStringUtils.rublesToCopecks(sum);
            if (lSum<=0) throw new Exception("");
        } catch (Exception e) {
            errorMessage = "Некорректная сумма";
        }

        if (errorMessage==null) {
            Client client = null;
            try {
                client = DAOService.getInstance().getClientByContractId(lContractId);
                if (client==null) throw new Exception();
            } catch (Exception e) {
                errorMessage= "Лицевой счет "+lContractId+" не найден";
            }
            if (client!=null) {
                String amount = ((lSum/100)+"."+(lSum%100));
                String currency = "RUR";
                String redirectUrl = RuntimeContext.getInstance().getPropertiesValue("bankOfMoscow.paymentGateway.url", "http://3ds2.mmbank.ru/cgi-bin/cgi_link");
                String terminalId = RuntimeContext.getInstance().getPropertiesValue("bankOfMoscow.paymentGateway.terminal", "30000077");
                String paymentId = RuntimeContext.getInstance().getPropertiesValue("bankOfMoscow.paymentGateway.payment", "906");
                String merchName = RuntimeContext.getInstance().getPropertiesValue("bankOfMoscow.paymentGateway.merchant.name", "Информационный город");
                String merchURL = RuntimeContext.getInstance().getPropertiesValue("bankOfMoscow.paymentGateway.merchant.name", "dit.mos.ru");
                String paymentDesc = "Пополнение л/с "+client.getPerson().getSurnameAndFirstLetters()+" ("+lContractId+")";
                String order = ""+System.currentTimeMillis();
                String trType = "6";
                String nonce = "";
                Random r = new Random(System.currentTimeMillis());
                for (int n=0;n<16;++n) nonce+=Integer.toHexString(r.nextInt(15));
                URI url = new URI(request.getRequestURL().toString());
                url = UriUtils.getURIWithNoParams(url);
                url = UriUtils.putParam(url, "action", "complete");
                String backRef = url.toString();
                ///
                String hmacInput = amount.length()+amount+currency.length()+currency+order.length()+order+paymentDesc.length()+paymentDesc+merchName.length()+merchName+merchURL.length()+merchURL+"-"+terminalId.length()+terminalId+"-"+trType.length()+trType+"---"+nonce.length()+nonce+backRef.length()+backRef;
                String key = RuntimeContext.getInstance().getPropertiesValue("bankOfMoscow.paymentGateway.key", "1423E4AE3874B0342D164AC25E79EADB");
                String hmac = DigitalSignatureUtils.generateHmac("HmacSHA1", key, hmacInput);
%>
<html><head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <title>Secure Card Authorization</title>
</head><body>
<p>Перенаправление на платежный шлюз банка...</p>
<form name="redirectForm" action="<%=redirectUrl%>" method="POST">
<input name="AMOUNT" value="<%=amount%>" size="12" maxlength="12" type="HIDDEN">
<input name="CURRENCY" value="<%=currency%>" size="3" maxlength="3" type="HIDDEN">
<input name="DESC" value="<%=paymentDesc%>" size="50" maxlength="50" type="HIDDEN">
<input name="TERMINAL" size="8" value="<%=terminalId%>" maxlength="8" type="HIDDEN">
<input name="ORDER" value="<%=order%>" type="HIDDEN"/>
<input name="BACKREF" size="30" value="<%=backRef%>" maxlength="100" type="HIDDEN">
<input name="TRTYPE" size="2" value="<%=trType%>" maxlength="2" type="HIDDEN">
<input name="PAYMENT" size="3" value="<%=paymentId%>" maxlength="3" type="HIDDEN">
<input name="PAYMENT_TO" size="10" value="200485" maxlength="40" type="HIDDEN">
<input name="MERCH_NAME" size="15" value="<%=merchName%>" maxlength="15" type="HIDDEN">
<input name="MERCH_URL" size="15" value="<%=merchURL%>" maxlength="15" type="HIDDEN">
<input name="NONCE" value="<%=nonce%>" type="HIDDEN">
<input name="P_SIGN" value="<%=hmac%>" type="HIDDEN">
</form>
<script>
document.redirectForm.submit();
</script>

</body></html>
            <%}
        }
    }
    if (action==null || errorMessage!=null) {
%>

<html>
<style>
.box {
border-radius:20px;
background-color:#F5F3E1;
-moz-border-radius: 20px;
border-style: solid;
border-width: 8px;
border-color: #999999;
}
.greygrad {
background: rgb(255,255,255); /* Old browsers */
background: -moz-linear-gradient(top,  rgba(255,255,255,1) 0%, rgba(229,229,229,1) 100%); /* FF3.6+ */
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,rgba(255,255,255,1)), color-stop(100%,rgba(229,229,229,1))); /* Chrome,Safari4+ */
background: -webkit-linear-gradient(top,  rgba(255,255,255,1) 0%,rgba(229,229,229,1) 100%); /* Chrome10+,Safari5.1+ */
background: -o-linear-gradient(top,  rgba(255,255,255,1) 0%,rgba(229,229,229,1) 100%); /* Opera 11.10+ */
background: -ms-linear-gradient(top,  rgba(255,255,255,1) 0%,rgba(229,229,229,1) 100%); /* IE10+ */
background: linear-gradient(top,  rgba(255,255,255,1) 0%,rgba(229,229,229,1) 100%); /* W3C */
filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#ffffff', endColorstr='#e5e5e5',GradientType=0 ); /* IE6-9 */
}
input {
  border: 1px solid #ccc;
  -moz-border-radius: 10px;
  -webkit-border-radius: 10px;
  border-radius: 10px;
  -moz-box-shadow: 2px 2px 3px #666;
  -webkit-box-shadow: 2px 2px 3px #666;
  box-shadow: 2px 2px 3px #666;
  font-size: 20px;
  padding: 4px 7px;
  outline: 0;
  -webkit-appearance: none;
}

body {
font-family: tahoma;
}
  div.centered{
    display:block;

    /*set the div in the center of the screen*/
    width:600px;
    height:240px;
  }

</style>
<body>
<div style="position:absolute;left:0px;top:0px;height:100%;width:100%" class="greygrad"></div>
<div style="position:absolute;left:0px;top:0px;height:180px;width:100%;background-image:url('bg.png');z-index:100"></div>
<table width="96%" style="position:absolute;top:300px;">
<tr><td width="100%" align="center">
<div class="box centered">
    <form name="form" action="" method="post">
        <input type="hidden" name="action" value="pay"/>
<table cellspacing="10">
<tr><td colspan="2" style="border-radius:6px;padding:10px"><b>Пополнение лицевого счета карты учащегося</b></td></tr>
    <%
        if (errorMessage!=null) {%>
    <tr><td colspan="2" align="center"><div class="errorMessage"><%=errorMessage%></div></td></tr>
        <%}
    %>
    <tr><td>Номер лицевого счета:</td><td><input name="contractId" type="text" size="10" maxlength="10"/></td></tr>
    <tr><td>Сумма пополнения (руб.):</td><td><input name="sum" type="text" size="10" maxlength="10"/></td></tr>
    <tr><td colspan="2" align="center"><input type="submit" value="Далее"/></td></tr>
</table>
</form>
</div>
</td></tr>
</table>
<script>document.form.contractId.focus()</script>
</body>
</html>
<%
    }
%>