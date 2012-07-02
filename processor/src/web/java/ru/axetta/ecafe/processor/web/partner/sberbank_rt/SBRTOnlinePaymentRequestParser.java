package ru.axetta.ecafe.processor.web.partner.sberbank_rt;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;

public class SBRTOnlinePaymentRequestParser extends OnlinePaymentRequestParser {

    @Override
    public ParseResult parseRequest(HttpServletRequest httpRequest) throws Exception {
        return parseGetParams(httpRequest);
    }

    public OnlinePaymentProcessor.PayRequest parsePayRequest(long sbrtContragentId, HttpServletRequest httpRequest) throws Exception {
        ParseResult parseResult = getRequestParams();
        long clientId=parseResult.getReqLongParam("CLIENTID");
        long opId=parseResult.getReqLongParam("OPID");
        long termId=parseResult.getReqLongParam("TERMID");

        long contragentId=sbrtContragentId;
        int paymentMethod= ClientPayment.ATM_PAYMENT_METHOD;
        
        String sum=parseResult.getParam("SUM");
        if (sum==null) {
            return new OnlinePaymentProcessor.PayRequest(OnlinePaymentProcessor.PayRequest.V_0, true,
                    contragentId, null, paymentMethod, clientId,
                    ""+opId, ""+termId, 0L, false);
        } else {
            String time=parseResult.getReqParam("TIME");
            return new OnlinePaymentProcessor.PayRequest(OnlinePaymentProcessor.PayRequest.V_0, false,
                    contragentId, null, paymentMethod, clientId,
                    ""+opId, termId+"/"+time, parseResult.getReqLongParam("SUM"), false);
        }
    }
    public void serializeResponse(OnlinePaymentProcessor.PayResponse response, HttpServletResponse httpResponse)
            throws Exception {
        LinkedList<Object> vals=new LinkedList<Object>();
        vals.addLast("CLIENTID");
        vals.addLast(response.getClientId());
        vals.addLast("OPID");
        vals.addLast(response.getPaymentId());
        vals.addLast("RES");
        vals.addLast(response.getResultCode());
        vals.addLast("DESC");
        vals.addLast(response.getResultDescription());
        if (response.getBalance()!=null) {
            vals.addLast("BAL");
            vals.addLast(response.getBalance());
        }
        if (response.getClientFullName()!=null) {
            vals.addLast("CLIENTFIO");
            vals.addLast(response.getClientFullName());
        }

        super.serializeResponse(vals, httpResponse);
    }
}
