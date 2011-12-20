/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paystd;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.utils.ConversionUtils;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 02.04.11
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
public class StdOnlinePaymentRequestParser extends OnlinePaymentRequestParser {
    final static String SIGNATURE_PARAM="&SIGNATURE=";

    @Override
    protected String prepareRequestForParsing(String requestBody) throws Exception {
        if (linkConfig.checkSignature) {
            if (!checkSignature(requestBody)) throw new Exception("Signature check failed");
        }
        return requestBody;
    }

    @Override
    protected String prepareResponseForOutputUrlEncoded(String responseData) throws Exception {
        return responseData+SIGNATURE_PARAM+getSignature(responseData);
    }

    public OnlinePaymentProcessor.PayRequest parsePayRequest(long defaultContragentId, HttpServletRequest httpRequest) throws Exception {
        ParseResult parseResult = parseGetParams(httpRequest);
        long clientId=parseResult.getReqLongParam("CLIENTID");
        long opId=parseResult.getReqLongParam("OPID");
        long termId=parseResult.getReqLongParam("TERMID");
        long contragentId=linkConfig.idOfContragent;

        int paymentMethod = ClientPayment.ATM_PAYMENT_METHOD;

        if (linkConfig.idOfAllowedClientOrgsList!=null) {
            if (!RuntimeContext.getInstance().getOnlinePaymentProcessor().checkPaymentEligibility(clientId, linkConfig.idOfAllowedClientOrgsList)) {
                throw new Exception("Payment not eligible - org");
            }
        }

        String sum=parseResult.getParam("SUM");
        if (sum==null) {
            return new OnlinePaymentProcessor.PayRequest(true,
                    contragentId, paymentMethod, clientId,
                    ""+opId, ""+termId, 0L);
        } else {
            String time=parseResult.getReqParam("TIME");
            return new OnlinePaymentProcessor.PayRequest(false,
                    contragentId, paymentMethod, clientId,
                    ""+opId, termId+"/"+time, parseResult.getReqLongParam("SUM"));
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

        super.serializeResponseUrlEncoded(vals, httpResponse);
    }

    StdPayConfig.LinkConfig linkConfig;
    public void setLinkConfig(StdPayConfig.LinkConfig linkConfig) {
        this.linkConfig = linkConfig;
    }

    boolean checkSignature(String data) throws Exception {
        if (!linkConfig.checkSignature) return true;
        int pos=data.indexOf(SIGNATURE_PARAM);
        if (pos==-1) throw new Exception("Signature missing");
        String payload=data.substring(0, pos), signData=data.substring(pos+SIGNATURE_PARAM.length());
        Signature sign=Signature.getInstance("SHA1withRSA");
        sign.initVerify(linkConfig.partnerPublicKey);
        //byte[] signBytes=Base64.decodeBase64(signData.getBytes("UTF-8"));
        byte[] signBytes=ConversionUtils.hex2ByteArray(signData);
        sign.update(payload.getBytes());
        return sign.verify(signBytes);
    }

    String getSignature(String data) throws Exception {
        Signature sign=Signature.getInstance("SHA1withRSA");
        sign.initSign(getOurDefaultPrivateKey());
        sign.update(data.getBytes("windows-1251"));
        return ConversionUtils.byteArray2Hex(sign.sign());
    }


}

