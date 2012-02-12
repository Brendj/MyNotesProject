/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.PaymentResult;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

//TODO: Требуются тесты!!!!!!!

public class PaymentControllerWS implements PaymentController {
    private static final int STD_PAYMENT = 1;

    public PaymentResult balanceRequest(String pid, Long clientId, Long opId, Long termId, int paymentSystem)
            throws Exception {
        PaymentResult paymentResult = new PaymentResult();
        String requestParams = "&PID=" + pid + "&CLIENTID=" + clientId + "&OPID=" + opId + "&TERMID=" + termId;
        paymentResult.desc = sendRequestToPaymentSystem(requestParams, paymentSystem);
        return paymentResult;
    }

    public PaymentResult commitPaymentRequest(String pid, Long clientId, Long sum, String time, Long opId, Long termId, int paymentSystem)
            throws Exception {
        PaymentResult paymentResult = new PaymentResult();
        String requestParams = "&PID=" + pid + "&CLIENTID=" + clientId + "&SUM=" + sum + "&TIME=" + time + "&OPID=" + opId + "&TERMID=" + termId;
        paymentResult.desc = sendRequestToPaymentSystem(requestParams, paymentSystem);
        return paymentResult;
    }

    private String sendRequestToPaymentSystem(String requestParams, int paymentSystem) throws Exception {
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
        ServletContext servletContext = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        HttpServletResponse response = (HttpServletResponse) context.getMessageContext().get(MessageContext.SERVLET_RESPONSE);
        BufferResponseWrapper bufResponse = new BufferResponseWrapper(response);
        request.setAttribute("soap", "1");
        servletContext.getRequestDispatcher("/processor/"+path).include(request, bufResponse);
        String responseString = null;
        try {
            responseString = bufResponse.getBuffer();
            //URL paymentURL = new URL(requestUrl);
            //URLConnection conn = paymentURL.openConnection();
            //BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            //response = in.toString();
            //in.close();
        }
        catch (Exception e) {
            return e.getMessage();
        }
        return responseString;
    }

    @Resource
    private WebServiceContext context;



    public class BufferResponseWrapper extends HttpServletResponseWrapper {
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
                };
            }
            return sos;
        }

        /**
         * Outputs the content to OutputStream if the application is using it or to the Writer otherwise.
         */
        public void output(String content) throws IOException{
            if (streamBuffer!=null){
                streamBuffer.write(content.getBytes(originalResponse.getCharacterEncoding()));
            } else {
                writerBuffer.write(content);
            }
        }

        public String getBuffer() {

                if (streamBuffer != null) {
                    try {
                       return streamBuffer.toString(originalResponse.getCharacterEncoding());
                    } catch (UnsupportedEncodingException e) {
                       return streamBuffer.toString();
                    }
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
