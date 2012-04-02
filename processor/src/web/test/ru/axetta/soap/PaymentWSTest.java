/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.soap;

import com.sun.xml.internal.ws.developer.JAXWSProperties;
import junit.framework.TestCase;

import java.io.FileInputStream;
import java.lang.Exception;
import java.security.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.net.ssl.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;

import ru.axetta.ecafe.processor.core.utils.ConversionUtils;
import ru.axetta.ecafe.util.DigitalSignatureUtils;
import ru.axetta.soap.test.PaymentController;
import ru.axetta.soap.test.PaymentControllerWSService;
import ru.axetta.soap.test.PaymentResult;

/**
 *
 * @author rumil
 */
public class PaymentWSTest extends TestCase {

    public PaymentWSTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {}
            }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void tearDown() throws Exception {
    }

    String PRIV_KEY="MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALEyuOL4fdt8B+tmSNzpsaQrecstv4qlS+mYQ2ScT/SciRWMi9Yw3yBOons5ztc/4ialxq1Fv10KEzlAOu7wrxc7joAGfhtRlf1XTE0YwQlE+0sBB14Qhran1Bxy49bUMdxVuCQgLLd44BPztDb0YenY8hNGMvjyY93gDp0DBuDxAgMBAAECgYEAgE2Gu6lLkAnNvi+woGyB2Ko2JNy6LQyk274JRic8aZSSWc0LT4rRdJYbZfgkgYzbFjrAkaPH/PkXlEOiqHIThB1sf1pPatHIcP46ozStV/IgxCqiTkKpp0ATLScW++CUvRhq+Bz8AARi0j6wly5OGFZ+3BDppu0vYVDJQMhWfrECQQDamO8czIRhDjlaFMQ++rBpEneDVQ6wbWYh22X6/qhOAJGvgPm42q07vVQw099ilpP3KtL8PE2JQPAIvulYyfstAkEAz4Rn3AC/OhkU2S0ao2yr0sLtDgr+9eLkZCTNp1/OVOb4Ee7v4YSKcyek5zHx1xFMHTkCLJ4HPfcrRroxPKFHVQJAFl7+YZEgnxoojmp/pv5a3XXxWzRyO2YGxMJCToyPRuRSBIcLh3qBrhJzMkgMnXdRj0MHsp6tRLWrmwmGsfqBxQJBAIK7UE7aLZ5lRKwY7So9gPWzFXJ+XOb8/JNWWDT0d2EnbOqnU3oIbMxlEk8QOOIbpI7YZlVDbR6Ngzb4f6JJnE0CQQCzfdWdzgDG2AK1ECWhPU8HsXxG3foUPE9vp7Yy5vXwOxykv1S0j4lmKoK1qjneBZSxF7dJ4iMDaKpmpwCeEJXy";
    //String PUB_KEY="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxMrji+H3bfAfrZkjc6bGkK3nLLb+KpUvpmENknE/0nIkVjIvWMN8gTqJ7Oc7XP+ImpcatRb9dChM5QDru8K8XO46ABn4bUZX9V0xNGMEJRPtLAQdeEIa2p9QccuPW1DHcVbgkICy3eOAT87Q29GHp2PITRjL48mPd4A6dAwbg8QIDAQAB";
    String PUB_KEY="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxMrji+H3bfAfrZkjc6bGkK3nLLb+KpUvpmENknE/0nIkVjIvWMN8gTqJ7Oc7XP+ImpcatRb9dChM5QDru8K8XO46ABn4bUZX9V0xNGMEJRPtLAQdeEIa2p9QccuPW1DHcVbgkICy3eOAT87Q29GHp2PITRjL48mPd4A6dAwbg8QIDAQAB";

    public class TestHostnameVerifier implements HostnameVerifier {
        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }
    }

    public void testEcafeSoap() throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream("C:\\Work\\Projects\\Москва-образование\\Сертификаты\\test\\testpay.pfx"), "1".toCharArray());
        //PrivateKey privateKey = DigitalSignatureUtils.convertToPrivateKey(PRIV_KEY);
        PrivateKey privateKey = (PrivateKey)ks.getKey("b5bfceff-c935-4203-93bc-f9499be1c63c", "1".toCharArray());

        System.out.println("Start");
        try {
            PublicKey publicKey = DigitalSignatureUtils.convertToPublicKey(PUB_KEY);
            //PrivateKey privateKey = DigitalSignatureUtils.convertToPrivateKey(PRIV_KEY);
            PaymentControllerWSService service = new PaymentControllerWSService();
            PaymentController port
                    = service.getPaymentControllerWSPort();
            ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8111/processor/soap/payment");
            ((BindingProvider)port).getRequestContext().put(JAXWSProperties.HOSTNAME_VERIFIER, new TestHostnameVerifier());

            String pid ="sber_msk";
            String clientId = "00200485";
            //String opId = ""+System.currentTimeMillis(), termId="0";
            String opId = "44445555", termId="1";
            String sum="1000", time="20120221000000";
            String balanceRequest = "PID=" + pid + "&CLIENTID=" + clientId + "&OPID=" + opId + "&TERMID=" + termId;
            balanceRequest = sign(privateKey, balanceRequest);
            if (!verify(publicKey, balanceRequest)) throw new Exception("Signature check failed");
            String commitPaymentRequest = "PID=" + pid + "&CLIENTID=" + clientId + "&SUM=" + sum + "&TIME=" + time + "&OPID=" + opId + "&TERMID=" + termId;
            commitPaymentRequest = sign(privateKey, commitPaymentRequest);
            System.out.println("- Balance request: "+balanceRequest);
            PaymentResult r = port.process(balanceRequest);
            printResult(r);
            //System.out.println("- Commit payment: "+commitPaymentRequest);
            //r = port.process(commitPaymentRequest);
            //printResult(r);

            // GetSummary by contract id

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    String SIGNATURE_PARAM="&SIGNATURE=";

    private boolean verify(PublicKey pk, String rq) throws Exception {
        int pos=rq.indexOf(SIGNATURE_PARAM);
        if (pos==-1) throw new Exception("Signature missing");
        String payload=rq.substring(0, pos), signData=rq.substring(pos+SIGNATURE_PARAM.length());
        Signature sign=Signature.getInstance("SHA1withRSA");
        sign.initVerify(pk);
        byte[] signBytes=ConversionUtils.hex2ByteArray(signData);
        sign.update(payload.getBytes());
        return sign.verify(signBytes);

    }

    private String sign(PrivateKey pk, String rq) throws Exception {
        Signature sign=Signature.getInstance("SHA1withRSA");
        sign.initSign(pk);
        sign.update((rq).getBytes());
        return rq+"&SIGNATURE="+ConversionUtils.byteArray2Hex(sign.sign());

    }

    private void printResult(PaymentResult r) {
        System.out.println("RES="+r.getRes()+"; DESC="+r.getDesc()+"; FIO="+r.getClientFIO()+"; BAL="+r.getBal());
        System.out.println("RESPONSE="+r.getResponse());
    }

    private XMLGregorianCalendar toXmlDateTime(Date date) throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar xc = DatatypeFactory.newInstance()
                .newXMLGregorianCalendarDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                        c.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        xc.setTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        return xc;
    }
}
