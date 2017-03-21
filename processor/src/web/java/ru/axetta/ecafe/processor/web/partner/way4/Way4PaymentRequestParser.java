/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.way4;


import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.client.ContractIdGenerator;
import ru.axetta.ecafe.processor.core.logic.PaymentProcessResult;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadExternalsService;
import ru.axetta.ecafe.processor.core.utils.Base64;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public class Way4PaymentRequestParser extends OnlinePaymentRequestParser {
    final static String TERMID_PREFIX_INTERNET_ACQUIRING="2";
    
    boolean bPayRequest;
    String rrn;

    protected boolean isBlockedTerminal(String termId, String[] blockedTerminals) {
        if(termId == null || termId.trim().length() < 1) {
            return false;
        }
        if(blockedTerminals == null || blockedTerminals.length < 1) {
            return false;
        }

        for(String blockedTermId : blockedTerminals) {
            if(blockedTermId.trim().length() < 1) {
                continue;
            }
            if(blockedTermId.equals(termId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public OnlinePaymentProcessor.PayRequest parsePayRequest(long defaultContragentId, HttpServletRequest httpRequest)
            throws Exception {
        ParseResult parseResult = getRequestParams();

        String function = parseResult.getParam("function");
        String opId = rrn = parseResult.getParam("RRN");
        long clientId = parseResult.getReqLongParam("PHONE");
        String bmId = parseResult.getParam("PCODE");
        if (bmId != null) {
            Contragent c = DAOReadExternalsService.getInstance().findContragentByClient(clientId);
            if (c != null && !bmId.equals(c.getBMID())) {
                throw new Exception(
                        String.format("Некорректный код поставщика (BMID), необходимо использовать %s", c.getBMID()));
            }
        }
        long sum=CurrencyStringUtils.rublesToCopecks(parseResult.getReqParam("AMOUNT"));
        String currency = parseResult.getParam("CURRENCY");
        if (!currency.equals("RUR") && !currency.equals("RUB")) throw new Exception("Invalid currency: "+currency);
        String termId = parseResult.getParam("TERMINAL");
        int paymentMethod=ClientPayment.ATM_PAYMENT_METHOD;
        if (termId!=null && termId.startsWith(TERMID_PREFIX_INTERNET_ACQUIRING)) {
            paymentMethod=ClientPayment.INTERNET_ACQUIRING_METHOD;
        }
        //linkConfig = new StdPayConfig.LinkConfig();
        //linkConfig.name = "Billing Gateway";
        //linkConfig.remoteAddressMask = ".*";
        //linkConfig.idOfContragent = 12;
        //linkConfig.authType= 0;
        if (function.equals("bank_account") ||
            isBlockedTerminal(termId, linkConfig.blockedTerminals)
            /*linkConfig.checkOnly*/) {
            return new OnlinePaymentProcessor.PayRequest(OnlinePaymentProcessor.PayRequest.V_0, true,
                    linkConfig.idOfContragent, null, paymentMethod, clientId,
                    ""+opId, null, 0L, false, bmId);

        } else if (function.equals("bank_payment")) {
            bPayRequest = true;
            String date=parseResult.getReqParam("DATE")+parseResult.getReqParam("TIME");
            return new OnlinePaymentProcessor.PayRequest(OnlinePaymentProcessor.PayRequest.V_0, false,
                    linkConfig.idOfContragent, null, paymentMethod, clientId,
                    opId, date+"/"+termId+"/"+opId, sum, false, bmId);
        } else {
            throw new Exception("Invalid function requested: "+function);
        }
    }

    StdPayConfig.LinkConfig linkConfig;
    public void setLinkConfig(StdPayConfig.LinkConfig linkConfig) {
        this.linkConfig = linkConfig;
    }

    @Override
    public void serializeResponse(OnlinePaymentProcessor.PayResponse response, HttpServletResponse httpResponse)
            throws Exception {
        ParseResult parseResult = getRequestParams();
        String stan = parseResult.getParam("STAN");

        String rspCode = translateResultCode(response.getResultCode());
        String infoSection="";
        if (!bPayRequest) {
            if (response.getResultCode() == PaymentProcessResult.OK.getCode()) {

                int subBalanceNum = 0;
                String contractIdstr = String.valueOf(response.getClientId());
                if(ContractIdGenerator.luhnTest(contractIdstr)){
                    subBalanceNum = 0;
                } else {
                    int len = contractIdstr.length();
                    if(len>2 && ContractIdGenerator.luhnTest(contractIdstr.substring(0, len - 2))){
                        subBalanceNum = Integer.parseInt(contractIdstr.substring(len - 2));
                    }
                }

                final String fullName = Base64.encodeBytes(response.getClientFullName().getBytes("UTF-8"));
                switch (subBalanceNum){
                    case 0: {
                        final String format = "<AccountInfo><RRN>%s</RRN><Account>%d</Account><Currency>RUR</Currency><Phone>%d</Phone><Info2>%s</Info2></AccountInfo>";
                        final String balance = toStringFromLong(response.getBalance());
                        final String info2 = String.format("PARAM3=%s;PARAM4=%s;", fullName, balance);
                        infoSection=String.format(format,rrn, response.getClientId(), response.getClientId(), info2);
                    }break;
                    case 1: {
                        /*
                        <Info>STRRUS1= Абонемент на питание:<Info>
                        <Info2>PARAM3= Колесник Юрий Николаевич;PARAM4=259,00<Info2>
                        * */
                        final Long subBalance1 = response.getSubBalance1()==null?0:response.getSubBalance1();
                        final String fullSubBalance1 = toStringFromLong(subBalance1);
                        final String strrus1 = Base64.encodeBytes(String.valueOf("Абонемент на питание:").getBytes("UTF-8"));
                        final String info="STRRUS1="+strrus1+";";
                        final String info2 = String.format("PARAM3=%s;PARAM4=%s;", fullName, fullSubBalance1);
                        infoSection=String.format("<AccountInfo><RRN>%s</RRN><Account>%d</Account><Currency>RUR</Currency><Phone>%d</Phone><Info>%s</Info><Info2>%s</Info2></AccountInfo>",
                                rrn,
                                response.getClientId(),
                                response.getClientId(), info, info2);
                    }break;
                }
            }
        } else {

            if (response.getResultCode()==PaymentProcessResult.OK.getCode()) {
                infoSection=String.format("<Payment><RRN>%s</RRN><Date>%s</Date><Time>%s</Time><Phone>%d</Phone><Account>%d</Account><Amount>%s</Amount><Currency>RUR</Currency></Payment>",
                        rrn, parseResult.getParam("DATE"), parseResult.getParam("TIME"), response.getClientId(),
                        response.getClientId(), parseResult.getParam("AMOUNT"));
            }
        }
        String rsp = String.format("<XML><mBilling Version=\"1.0\"><STAN>%s</STAN><Response>%s</Response>%s</mBilling></XML>", stan, rspCode, infoSection);
        printToStream(rsp, httpResponse);
    }

    private String toStringFromLong(Long subBalance1) throws UnsupportedEncodingException {
        return Base64.encodeBytes(CurrencyStringUtils.copecksToRubles(subBalance1).getBytes("UTF-8"));
    }

    @Override
    public ParseResult parseRequest(HttpServletRequest httpRequest) throws Exception {
        return parsePostedUrlEncodedParams(httpRequest);
    }

    private String translateResultCode(int resultCode) {
        if (resultCode== PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getCode()) return "ERROR";
        if (resultCode== PaymentProcessResult.CLIENT_NOT_FOUND.getCode()) return "ERR_PHONE";
        if (resultCode==PaymentProcessResult.CARD_NOT_FOUND.getCode()) return "ERR_PHONE";
        if (resultCode==PaymentProcessResult.OK.getCode()) return "OK";
        return "ERROR";
    }

}


