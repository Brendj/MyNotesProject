/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.core.utils.ParameterStringUtils;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentServlet;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.PaymentResult;
import ru.axetta.ecafe.processor.web.partner.paystd.StdOnlinePaymentServlet;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.io.*;

@WebService()
public class PaymentControllerWS extends HttpServlet implements PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentControllerWS.class);
    private static final int STD_PAYMENT = 1;
    public static final String AUTH_POLICY_KEY = "paymentAuthPolicyKey";

    StdPayConfig.LinkConfig linkConfig;
    public void setLinkConfig(StdPayConfig.LinkConfig linkConfig) {
        this.linkConfig = linkConfig;
    }

    @Override
    public PaymentResult process(String request) throws Exception {
        return sendRequestToPaymentSystem(request, STD_PAYMENT);
    }

    private PaymentResult sendRequestToPaymentSystem(String requestParams, int paymentSystem) throws Exception {
        String path = null;
        switch (paymentSystem) {
            //case ELECSNET_PAYMENT : {
            //    path = "payment-elecsnet";
            //}
            //break;
            case STD_PAYMENT: {
                path = "payment-std";
            }
            break;
            //case SBRT_PAYMENT: {
            //    path = "payment-sbrt";
            //}
            //break;
            default: throw new Exception("Invalid payment system: "+paymentSystem);
        }
        PaymentResult paymentResult = new PaymentResult();
        try {
            ServletContext servletContext = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
            HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
            HttpServletResponse response = (HttpServletResponse) context.getMessageContext().get(MessageContext.SERVLET_RESPONSE);
            BufferResponseWrapper bufResponse = new BufferResponseWrapper(response);
            request.setAttribute(StdOnlinePaymentServlet.ATTR_SOAP_REQUEST, requestParams);

            AuthorizationPolicy authorizationPolicy = (AuthorizationPolicy) context.getMessageContext()
                    .get("org.apache.cxf.configuration.security.AuthorizationPolicy");
            if (authorizationPolicy != null && authorizationPolicy.getUserName() != null) {
                request.setAttribute(AUTH_POLICY_KEY, authorizationPolicy);
            }

            servletContext.getRequestDispatcher("/"+path).include(request, bufResponse);
            paymentResult.response = bufResponse.getBuffer("UTF-8");
            //checkSignature(paymentResult.response);
            OnlinePaymentProcessor.PayResponse payResponse = (OnlinePaymentProcessor.PayResponse)request.getAttribute(StdOnlinePaymentServlet.ATTR_PAY_RESPONSE);
            if (payResponse == null) {
                final String paymentServletError = (String)request.getAttribute(OnlinePaymentServlet.ONLINE_PS_ERROR);
                final String errorString = paymentServletError != null ? paymentServletError : "Internal server error.";
                throw new Exception(errorString);
            }
            paymentResult.clientId = payResponse.getClientId();
            paymentResult.opId = payResponse.getPaymentId();
            paymentResult.res = payResponse.getResultCode();
            paymentResult.desc = payResponse.getResultDescription();
            paymentResult.bal = payResponse.getBalance();
            if (paymentResult.response.contains("*")) {
                paymentResult.clientFIO = payResponse.getClientBlurWithoutClientFirstName();
            } else {
                paymentResult.clientFIO = payResponse.getClientFullName();
            }
            paymentResult.tspContragentId = payResponse.getTspContragentId();
            paymentResult.addInfo = payResponse.getAddInfo()==null?null:ParameterStringUtils.toString(payResponse.getAddInfo());
            payResponse.getTspContragentId();
        }
        catch (Exception e) {
            paymentResult.res = 100;
            paymentResult.desc = "Error: "+e;
            logger.error("Failed to process SOAP payment request", e);
        }
        return paymentResult;
    }

    /* // test signature procedure
    private boolean checkSignature(String data) throws Exception {
        String SIGNATURE_PARAM="&SIGNATURE=";
        int pos=data.indexOf(SIGNATURE_PARAM);
        if (pos==-1) throw new Exception("Signature missing");
        String payload=data.substring(0, pos), signData=data.substring(pos+SIGNATURE_PARAM.length());
        Signature sign=Signature.getInstance("SHA1withRSA");
        sign.initVerify(DigitalSignatureUtils.convertToPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDsuUGFoSW/giuxNEyZ4SITd/jJzlR3piPtR01BW4ih30ddX1IOaxDu90k84VB20NUgqzz1BIUbemiQ0HVUY/j+g2UFcVWpwQgJZkonay6JmkiNqbADesocu1Jx6EP/felYRL4XufnEPIJA3CD+Gzl9m89ukvj/WVGLw1owv0IUPwIDAQAB"));
        //byte[] signBytes=Base64.decodeBase64(signData.getBytes("UTF-8"));
        byte[] signBytes= ConversionUtils.hex2ByteArray(signData);
        sign.update(payload.getBytes("UTF-8"));
        return sign.verify(signBytes);
    }
    */

    @Resource
    private WebServiceContext context;



    public static class BufferResponseWrapper extends HttpServletResponseWrapper {
        PrintWriter pw;
        ServletOutputStream sos;
        private StringWriter writerBuffer;
        private ByteArrayOutputStream streamBuffer;

        HttpServletResponse originalResponse;

        private String redirect;

        public BufferResponseWrapper(HttpServletResponse httpServletResponse) {
            super(httpServletResponse);
            originalResponse = httpServletResponse;
        }

        public PrintWriter getWriter() throws IOException {
            if (writerBuffer == null) {
                writerBuffer = new StringWriter();
                pw = new PrintWriter(writerBuffer);
            }
            return pw;
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (streamBuffer == null) {
                streamBuffer = new ByteArrayOutputStream();
                sos = new ServletOutputStream() {
                    public void write(int b) throws IOException {
                        streamBuffer.write(b);
                    }

                    public void write(byte b[]) throws IOException {
                        streamBuffer.write(b);
                    }

                    public void write(byte b[], int off, int len) throws IOException {
                        streamBuffer.write(b, off, len);
                    }

                    public boolean isReady() {
                        return true;
                    }

                    public void setWriteListener(WriteListener var1) {

                    }
                };
            }
            return sos;
        }

        /**
         * Outputs the content to OutputStream if the application is using it or to the Writer otherwise.
         */
        public void output(String content) throws IOException{
            if (streamBuffer!=null){
                streamBuffer.write(content.getBytes());
            } else {
                writerBuffer.write(content);
            }
        }

        public String getBuffer(String encoding) throws UnsupportedEncodingException {

                if (streamBuffer != null) {
                    //try {
                    //   return streamBuffer.toString(originalResponse.getCharacterEncoding());
                    //} catch (UnsupportedEncodingException e) {
                       return new String(streamBuffer.toByteArray(), encoding);
                    //}
                }
                else if (writerBuffer != null)
                    return writerBuffer.toString();
                else
                    return "";
        }

        public HttpServletResponse getOriginalResponse() {
            return originalResponse;
        }

        public String getRedirect() {
            return redirect;
        }

        public void sendRedirect(String redirect) throws IOException {
            String key = "aaxmlrequest=true";
            int pos = redirect.indexOf(key);
            if (pos !=-1)
                redirect = redirect.substring(0,pos)+redirect.substring(pos+key.length());

            this.redirect = redirect;
        }

        public String findSubstring(String firstDelimiter, String lastDelimiter){
            String content;
            if (streamBuffer!=null){
                try {
                    content = streamBuffer.toString("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    content = streamBuffer.toString();
                }
            } else if (writerBuffer!=null) {
                content = writerBuffer.toString();
            } else {
                return null;
            }


            int p1 = content.indexOf(firstDelimiter);
            if (p1 != -1) {
                p1+=firstDelimiter.length();
                int p2 = content.indexOf(lastDelimiter, p1);
                if (p2!=-1){
                    return content.substring(p1, p2);
                }
            }
            return null;
        }

        public void setContentType(String string) {
            // do nothing
        }

        public void flushBuffer() throws IOException {
            // do nothing
        }

        public void setCharacterEncoding(String string) {
            // do nothing
        }

        public void setDateHeader(String string, long l) {
            //do nothing
        }

        public void addDateHeader(String string, long l) {
            //do nothing
        }

        public void setHeader(String string, String string1) {
            //do nothing
        }

        public void addHeader(String string, String string1) {
            //do nothing
        }

        public void setIntHeader(String string, int i) {
            //do nothing
        }

        public void addIntHeader(String string, int i) {
            //do nothing
        }

        public StringWriter getWriterBuffer() {
            return writerBuffer;
        }

        public ByteArrayOutputStream getStreamBuffer() {
            return streamBuffer;
        }
    }

}
