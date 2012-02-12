package ru.axetta.ecafe.processor.web.partner.elecsnet;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.utils.ConversionUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.PublicKey;
import java.security.Signature;
import java.util.LinkedList;

public class ElecsnetOnlinePaymentRequestParser extends OnlinePaymentRequestParser {
    final static int TYPE_CHECK=1, TYPE_PAY=2;

    @Override
    protected String prepareRequestForParsing(String requestBody) throws Exception {
        if (!checkSignature(requestBody)) throw new Exception("Signature check failed");
        return requestBody;
    }

    @Override
    public OnlinePaymentProcessor.PayRequest parsePayRequest(long contragentId, HttpServletRequest httpRequest)
            throws Exception {

        ParseResult parseResult = parsePostedUrlEncodedParams(httpRequest);


        int requestType=parseResult.getReqIntParam("type");
        long clientId=parseResult.getReqLongParam("reqid");
        String opId="CHECK_ONLY";
        int paymentMethod=ClientPayment.KIOSK_PAYMENT_METHOD;
        if (requestType==TYPE_CHECK) {
            return new OnlinePaymentProcessor.PayRequest(true,
                    contragentId, paymentMethod, clientId,
                    ""+opId, null, 0L, false);

        } else {
            opId=parseResult.getReqParam("auth_code");
            int currency=parseResult.getReqIntParam("currency");
            long sum=parseResult.getReqLongParam("amount");
            String date=parseResult.getReqParam("date");
            return new OnlinePaymentProcessor.PayRequest(false,
                    contragentId, paymentMethod, clientId,
                    opId, date+"/"+currency, sum, false);
        }
    }

    @Override
    public void serializeResponse(OnlinePaymentProcessor.PayResponse response, HttpServletResponse httpResponse)
            throws Exception {
        LinkedList<Object> vals=new LinkedList<Object>();
        vals.addLast("ans_code");
        String ansCode=translateResultCode(response.getResultCode());
        vals.addLast(ansCode);
        if (RC_INTERNAL_ERROR.equals(ansCode)) {
            vals.addLast("ansid");
            vals.addLast("Ошибка._Обратитесь_в_службу_поддержки_Новая_школа.");
        } else {
            if (response.isCheckOnly()) {
                vals.addLast("ansid");
                vals.addLast(getAnswerId(response));
            }
        }
        vals.addLast("message");
        vals.addLast(response.getResultDescription()==null?"":response.getResultDescription().substring(0, response.getResultDescription().length()>=100?100:response.getResultDescription().length()));
        ////
        serializeResponseUrlEncoded(vals, httpResponse);
    }

    @Override
    protected String prepareResponseForOutputUrlEncoded(String responseData) throws Exception {
        return responseData+SIGNATURE_PARAM+getSignature(responseData);
    }

    final static String RC_INTERNAL_ERROR="62";
    private String translateResultCode(int resultCode) {
        if (resultCode==Processor.PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getCode()) return "01";
        if (resultCode== Processor.PaymentProcessResult.CLIENT_NOT_FOUND.getCode()) return "48";
        if (resultCode==Processor.PaymentProcessResult.CARD_NOT_FOUND.getCode()) return "42";
        if (resultCode==Processor.PaymentProcessResult.OK.getCode()) return "00";
        return RC_INTERNAL_ERROR; // Произвольная ошибка. При получении этого кода на терминале отображается сообщение, содержащееся в первом подполе ansid. Далее происходит выход из платёжного сценария (возврат в главное меню).
    }

    private String getAnswerId(OnlinePaymentProcessor.PayResponse response) {
        String s="";
        if (response.getClientSurname()!=null) s+=convertAnswerField(response.getClientSurname())+"[b]"+
                convertAnswerField(response.getClientFirstName()+(response.getClientSecondName()==null?"":"_"+response.getClientSecondName()));
        if (response.getBalance()!=null) s+="-БАЛАНС:"+ convertAnswerField(CurrencyStringUtils.copecksToRubles(response.getBalance()));
        return s;
    }

    private String convertAnswerField(String s) {
        s=s.replace('-', '=').replace(' ', '_');
        return s;
    }

    final static String ELECSNET_PUB_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5vSZ1JC8KcCw3gmKdmx4RZ/aYU6iN8dLmwsDCEJKUXXeo/Tf1ocfrthzu5CdrMFib3ztG6wTpmP8JiKI6ohR+XAhhavTNuxgqDkUdymWC1ow1TBXFl166rEI+aditzVC76d3Pjv7HTW2YxBXY7xEPEiUqFs06OzdNytBVGXHTGzA7zAO8mF3OCbvH3mrZsfZDvDymWj0zHVTODYHgCaYTmuOlsqSmd97ckXcOA7JKPBNo+tAvZkrwKkSoW1/xMJlnfGDY47FBDL9Rw/ldZVjBiBOmn0vXwXWDkYJ6c6hpVwRqPKkcXUikqz67NUyPdwYwGC0OQSFc0jM3Q3Uq3XXiwIDAQAB";

    final static String SIGNATURE_PARAM="&signature=";
    PublicKey partnerPublicKey;

    boolean checkSignature(String data) throws Exception {
        int pos=data.indexOf(SIGNATURE_PARAM);
        if (pos==-1) throw new Exception("Signature missing");
        String payload=data.substring(0, pos), signData=data.substring(pos+SIGNATURE_PARAM.length());
        if (partnerPublicKey==null) partnerPublicKey = DigitalSignatureUtils.convertToPublicKey(ELECSNET_PUB_KEY);
        Signature sign=Signature.getInstance("MD5withRSA");
        sign.initVerify(partnerPublicKey);
        sign.update(payload.getBytes());
        return sign.verify(ConversionUtils.hex2ByteArray(signData));
    }

    String getSignature(String data) throws Exception {
        Signature sign=Signature.getInstance("MD5withRSA");
        sign.initSign(getOurDefaultPrivateKey());
        sign.update(data.getBytes("windows-1251"));
        return ConversionUtils.byteArray2Hex(sign.sign());
    }

    /*
    public static void main(String[] args) throws Exception {
        String data="type=1&reqid=00101493&signature=046DFBE69067E2F646795D46994473378F8A8E7B6E3B03339963C48717AB8A69196D7D148D7F3D8A3E819CA03CCC26DAF0D2E709DD66A5B4FCFE2C6B0570A85494B090E5DA985CCEC1B5DDD746DF7F4F346A6CDCF5D98F8BA305D5D1F33A9111D29B76F0A678215A7A031AAF59A15FF933B971B6E71F7CEA78ED0C6ADEBA3FEB6B2B60148DE4E450E85BA42F956D82E14DD319A3279C7822CB49218A4606FC01558FAD859FC151D5758B30EF26D765427F0982B0C38EE785FF6DED9276BAAD10713415B40CD443C93924C9661DC2E1C0947779092DEF1B9579B678CE2B1209886765D1ACDC21F49243A220AAFE4023AF73C68955D7B8B1A9C811D701AABAD8F7";
        ElecsnetOnlinePaymentServlet p=new ElecsnetOnlinePaymentServlet();
        System.out.println("verified="+p.checkSignature(data));
        System.out.println("sign="+data+"&signature="+p.getSignature(data));
    }*/

}
