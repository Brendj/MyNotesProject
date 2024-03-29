/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paystd;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadExternalsService;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;
import ru.axetta.ecafe.util.ConversionUtils;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Signature;
import java.util.LinkedList;
import java.util.Map;

public class StdOnlinePaymentRequestParser extends OnlinePaymentRequestParser {
    final static String SIGNATURE_PARAM="&SIGNATURE=";

    @Override
    public ParseResult parseRequest(HttpServletRequest httpRequest) throws Exception {
        return parseGetParams(httpRequest);
    }

    @Override
    public boolean checkRequestSignature(HttpServletRequest httpRequest) throws Exception {
        if (linkConfig.checkSignature) {
            return checkSignature(getQueryString(httpRequest));
        }
        return true;
    }

    @Override
    protected String prepareResponseForOutputUrlEncoded(String responseData) throws Exception {
        return responseData+SIGNATURE_PARAM+getSignature(responseData);
    }

    public OnlinePaymentProcessor.PayRequest parsePayRequest(long defaultContragentId, HttpServletRequest httpRequest) throws Exception {
        long clientId;
        ParseResult parseResult = getRequestParams();
        int protoVersion = 0;
        if (parseResult.getParam("V")!=null) {
            protoVersion = parseResult.getReqIntParam("V");
        }
        
        if (parseResult.getParam("CARDID")!=null) {
            Long cardId = Long.decode(parseResult.getReqParam("CARDID"));
            Long clId = DAOReadExternalsService.getInstance().getContractIdByCardNo(cardId);
            if (clId == null) throw new CardNotFoundException("Card not found: "+cardId);
            clientId = clId;
        } else {
            clientId=parseResult.getReqLongParam("CLIENTID");
        }
        String opId=parseResult.getReqParam("OPID");
        String termId=parseResult.getReqParam("TERMID");
        Long tspContragentId = null;
        if (protoVersion>0) {
            String sTspContragentId=parseResult.getParam("TSPID"); // идентификатор контрагента-получателя платежа (ТСП)
            tspContragentId = sTspContragentId==null?null:(Long.parseLong(sTspContragentId));
        }
        long contragentId=linkConfig.idOfContragent; // идентификатор контрагента-агента по приему платежей

        int paymentMethod = ClientPayment.ATM_PAYMENT_METHOD;

        if (linkConfig.idOfAllowedClientOrgsList!=null) {
            if (!RuntimeContext.getInstance().getOnlinePaymentProcessor().checkPaymentEligibility(clientId, linkConfig.idOfAllowedClientOrgsList)) {
                throw new Exception("Payment not eligible - org");
            }
        }

        String sum=parseResult.getParam("SUM");
        if (sum==null) {
            return new OnlinePaymentProcessor.PayRequest(protoVersion, true,
                    contragentId, tspContragentId, paymentMethod, clientId,
                    ""+opId, ""+termId, 0L, false, linkConfig.allowedTSPIds);
        } else {
            String time=parseResult.getReqParam("TIME");
            String paymentAdditionalId = termId+"/"+time;
            if (tspContragentId!=null) paymentAdditionalId+="/TSP"+tspContragentId;
            boolean isRollback=parseResult.getParam("ROLLBACK")!=null && parseResult.getReqIntParam("ROLLBACK")==1;
            return new OnlinePaymentProcessor.PayRequest(protoVersion, false,
                    contragentId, tspContragentId, paymentMethod, clientId,
                    ""+opId, paymentAdditionalId, parseResult.getReqLongParam("SUM"), isRollback, linkConfig.allowedTSPIds);
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
        if(response.getResultCode()==140){
            vals.addLast(0);
        } else {
            vals.addLast(response.getResultCode());
        }
        vals.addLast("DESC");
        vals.addLast(response.getResultDescription());
        if (response.getBalance()!=null) {
            vals.addLast("BAL");
            vals.addLast(response.getBalance());
        }

        if (linkConfig.screening) {
            if (response.getClientBlurName() != null) {
                vals.addLast("CLIENTFIO");
                vals.addLast(response.getClientBlurWithoutClientFirstName());
            }
        } else {
            if (response.getClientFullName() != null) {
                vals.addLast("CLIENTFIO");
                vals.addLast(response.getClientFullName());
            }
        }

        if (response.getProtoVersion()>0 && response.getTspContragentId()!=null) {
            vals.addLast("TSPID");
            vals.addLast(response.getTspContragentId());
        }
        if (response.getProtoVersion()>0 && response.getAddInfo()!=null) {
            for (Map.Entry<String, String> e : response.getAddInfo().entrySet()) {
                vals.addLast(e.getKey());
                vals.addLast(e.getValue());
            }
        }
        if (!StringUtils.isEmpty(response.getInn())) {
            vals.addLast("INN");
            vals.addLast(response.getInn());
        }
        if (!StringUtils.isEmpty(response.getNazn())) {
            vals.addLast("NameContragent");
            vals.addLast(response.getNazn());
        }
        if (!StringUtils.isEmpty(response.getCorrAccount())) {
            vals.addLast("corrAccount");
            vals.addLast(response.getCorrAccount());
        }
        if (!StringUtils.isEmpty(response.getBic())) {
            vals.addLast("bic");
            vals.addLast(response.getBic().trim());
        }
        if (!StringUtils.isEmpty(response.getRasch())) {
            vals.addLast("settlAccount");
            vals.addLast(response.getRasch());
        }
        if (!StringUtils.isEmpty(response.getBank())) {
            vals.addLast("bank");
            vals.addLast(response.getBank());
        }
        if (!StringUtils.isEmpty(response.getKpp())) {
            vals.addLast("kpp");
            vals.addLast(response.getKpp());
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
        sign.update(data.getBytes(getRequestEncoding()));
        return ConversionUtils.byteArray2Hex(sign.sign());
    }


}

