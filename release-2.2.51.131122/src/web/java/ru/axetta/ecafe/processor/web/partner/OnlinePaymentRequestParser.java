package ru.axetta.ecafe.processor.web.partner;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.util.*;

public abstract class OnlinePaymentRequestParser {
    public static class CardNotFoundException extends Exception {

        public CardNotFoundException(String message) {
            super(message);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(OnlinePaymentRequestParser.class);
    String requestEncoding=CHARSET_NAME;

    public abstract OnlinePaymentProcessor.PayRequest parsePayRequest(long defaultContragentId, HttpServletRequest httpRequest) throws Exception;
    public abstract void serializeResponse(OnlinePaymentProcessor.PayResponse response, HttpServletResponse httpResponse)
            throws Exception;

    public void serializeResponse(LinkedList<Object> vals, HttpServletResponse httpResponse) throws
            Exception {
        printToStream(prepareResponseForOutput(serializeValsToString(vals)), httpResponse);
    }
    public void serializeResponseUrlEncoded(LinkedList<Object> vals, HttpServletResponse httpResponse) throws
            Exception {
        printToStream(prepareResponseForOutputUrlEncoded(serializeValsToUrlEncodedString(vals)), httpResponse);
    }

    protected String prepareResponseForOutput(String responseData) throws Exception { return responseData; }
    protected String prepareResponseForOutputUrlEncoded(String responseData) throws Exception { return responseData; }


    protected void printToStream(String s, HttpServletResponse httpResponse) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteStream, true, getRequestEncoding());
        try {
            printStream.print(s);
        } catch (Exception e) {
            logger.error("Failed to serialize response", e);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        printStream.close();
        httpResponse.setCharacterEncoding(getRequestEncoding());
        httpResponse.setContentLength(byteStream.size());
        byteStream.writeTo(httpResponse.getOutputStream());
    }


    protected static String serializeValsToString(LinkedList<Object> vals) {
        StringBuilder sb=new StringBuilder();
        for (int n=0;n<vals.size()/2;++n) {
            if (n!=0) sb.append(PARAM_DELIMITER);
            sb.append(vals.get(n*2).toString());
            sb.append(PARAM_VALUE_DELIMITER);
            Object v=vals.get(n*2+1);
            if (v!=null) sb.append(v);
        }
        return sb.toString();
    }

    protected static String serializeValsToUrlEncodedString(LinkedList<Object> vals) {
        StringBuilder sb=new StringBuilder();
        for (int n=0;n<vals.size()/2;++n) {
            if (n!=0) sb.append("&");
            sb.append(vals.get(n*2).toString());
            sb.append('=');
            Object v=vals.get(n*2+1);
            if (v!=null) sb.append(v);
        }
        return sb.toString();
    }

    public static class ParseResult {

        private final Map<String, String> params;

        public ParseResult(Map<String, String> params) {
            this.params = params;
        }

        public int getReqIntParam(String param)
                throws Exception {
            String textValue = getReqParam(param);
            return Integer.parseInt(textValue);
        }

        public long getReqLongParam(String param)
                throws Exception {
            String textValue = getReqParam(param);
            return Long.parseLong(textValue);
        }

        public String getReqParam(String param) {
            param=param.toLowerCase();
            if (!params.containsKey(param)) {
                throw new IllegalArgumentException(String.format("Required parameter not found: %s", param));
            }
            return (String)params.get(param);
        }

        public String getParam(String param) {
            return (String)params.get(param.toLowerCase());
        }

    }

    private static final String REQUEST_PARAM_NAME = "inputmessage";
    public static final String CHARSET_NAME = "windows-1251";
    public static final String PARAM_VALUE_DELIMITER = "=";
    public static final String PARAM_DELIMITER = "\r\n";

    ParseResult requestParams;

    public ParseResult getRequestParams() {
        return requestParams;
    }

    public ParseResult parse(HttpServletRequest httpRequest) throws Exception {
        final String requestText = httpRequest.getParameter(REQUEST_PARAM_NAME);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got request text: %s", requestText));
        }
        Map<String, String> params = parseRequest(requestText);
        return new ParseResult(params);
    }

    public abstract ParseResult parseRequest(HttpServletRequest httpRequest) throws Exception;
    public void setRequestParams(HttpServletRequest httpRequest) throws Exception {
        requestParams = parseRequest(httpRequest);
    }
    public boolean checkRequestSignature(HttpServletRequest httpRequest) throws Exception {
        return true;
    }

    public ParseResult parseGetParams(HttpServletRequest httpRequest) throws Exception {
        prepareRequestForParsing(getQueryString(httpRequest));
        Map<String, String> params=new HashMap<String, String>();
        params = parseUrlParameterString(getQueryString(httpRequest));
        //for (Object param : httpRequest.getParameterMap().keySet()) {
        //    params.put(((String)param).toLowerCase(), (String)httpRequest.getParameter((String)param));
        //}
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got request text: %s", getQueryString(httpRequest)));
        }
        return new ParseResult(params);
    }

    protected String getQueryString(HttpServletRequest httpRequest) {
        if (httpRequest.getAttribute(OnlinePaymentServlet.ATTR_SOAP_REQUEST)!=null) {
            requestEncoding = "UTF-8";
            return (String)httpRequest.getAttribute(OnlinePaymentServlet.ATTR_SOAP_REQUEST);
        } else {
            requestEncoding = "windows-1251";
            return httpRequest.getQueryString();
        }
    }

    public String getRequestEncoding() {
        return requestEncoding;
    }

    private Map<String, String> parseRequest(String text) throws Exception {
        Map<String, String> params=new HashMap<String, String>();
        String[] tokenLines = StringUtils.splitByWholeSeparator(StringUtils.defaultString(text), PARAM_DELIMITER);
        for (String line : tokenLines) {
            int valueDelimiter = StringUtils.indexOf(line, PARAM_VALUE_DELIMITER);
            if (valueDelimiter > 0) {
                params.put(StringUtils.substring(line, 0, valueDelimiter).toLowerCase(),
                        StringUtils.substring(line, valueDelimiter + PARAM_VALUE_DELIMITER.length()));
            } else {
                params.put(line.toLowerCase(), null);
            }
        }
        return params;
    }

    ///////////////


    public ParseResult parsePostedUrlEncodedParams(HttpServletRequest httpRequest) throws Exception {
        String requestBody = readRequestBody(httpRequest.getInputStream());
        requestBody=prepareRequestForParsing(requestBody);

        return new ParseResult(parseUrlParameterString(requestBody));
    }

    protected String prepareRequestForParsing(String requestBody) throws Exception {
        return requestBody;
    }

    private Map<String, String> parseUrlParameterString(String requestText) {
        if (requestText.endsWith("\r\n")) requestText=requestText.substring(0, requestText.length()-2);
        else if (requestText.endsWith("\n")) requestText=requestText.substring(0, requestText.length()-1);
        String[] v=requestText.split("&");
        Map<String, String> vals=new HashMap<String, String>();
        for (int n=0;n<v.length;++n) {
            int p=v[n].indexOf('=');
            if (p==-1) vals.put(v[n].toLowerCase(), "");
            else {
                vals.put(v[n].substring(0, p).toLowerCase(), v[n].length()>p?v[n].substring(p+1):"");
            }
        }
        return vals;
    }

    private String readRequestBody(ServletInputStream inputStream) throws Exception {
        StringBuilder b=new StringBuilder();
        byte[] bytes=new byte[1024];
        for (;;) {
            int c=inputStream.read(bytes);
            if (c==-1) break;
            b.append(new String(bytes, 0, c, getRequestEncoding()));
        }
        return b.toString();
    }

    //final static String OUR_PRIV_KEY="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCzqZ3II63JmM/GQ0Hb9RLw4QohtrCFSi9J85/iI/BCkWudd5W/3lA0mJ0Vs/iLIoVqjon1qfKechPxDCBWgJkuApqOPr627A0j27xRSH5DiV8lcuH7Y1U30YHIarzdGCfD5xNdQvJsUXtncSbWbthpOi5JhoxF6rSprrpJpzeO6SHR2FmGvCLe+eBcxODnsSnklKqhQqtbk0LAFhnYsOYYH7ZRKlOGAmqFEZxMG+BV7P6aTed+3Tu2DwaTOuZoP1tBZNMPwM9k2ERVXIi4Ze8l1yeON1sHAHJlaLeMwgWiBWY4wFv2Z0yk+gSharD1MPDV1qVB7tl7omjt4ELpuROJAgMBAAECggEAdBZC1xEGt94/UpIeuetOw8oE6+fJgzZ9+8CpLEugcMLZ0DN21TTmijWaOm3BSNWTc1iKl+up0utPK090jbAmSe5wwzyTqko361WOBs9alnTqv53NmsuCQsDipQci5ZE4EIL8piQd1BOKMzqPp7qTptprEiMX/A09ku5NfE3JvlKbwo1cItx3+DtL1yvryfSMYQ1nN9RVfRLZkTVp8Agmny+8nmcXz38b9EKrnnXtjLq+prVdbtuEnlHEmFtyxNWaN/IVeIxZG+vPnAN7+r7BgYIKRGXUPsZ4aILAX2krSpOljOojtwTe2GdKPP2XUvmnMe1a7K29isK44PucAMR5qQKBgQDcQ6wpdba7IReEysdXU5yrFl7irtajd0HjyIn/gJD2zA0moz0C1m4d62JNA8bVtUeHWhT9cVAwnRoVepdOKeLIDXKEA34ES4n6aknnNeVdSTDxd66wx4mOHfz8wGJ6rbiJa1yDLH8DDzod2tVR2Dx8NNZ3saeqP5fDw4nxWJmwEwKBgQDQz50cwYtOfeREgpmjB8y75zDuU/lN/5GR0iHXZoMpG4y3a9cN+hoTcEH5AeGlbEdtQUlFuX1N4FhSvcm49bSQ5XNk+hbkutir7Yt3k8aP8zB73J41AtxTs9hAgqI+bsZsYMR8QBFil+B1U0xN8+aGn2bAdJLohitK7tjesHJ5cwKBgHR4HCr4IZ2li+gdyXZ1sdwNwjKfPMPHJcIqoj3GX+EAWNvbaUF83VFYy+vpWVRbvOznRz4UktCB4e8FmcgtAcvCwJDpv9LJlrYhSd7Gcvf24bhtDqRPfn71gHaty+UOwwt3B74c4zRc8uAifLYAX0tWLSmxePTfF4LefKxvqySdAoGARhX5SQaZaG4O6QPg53ydtlAB92yOGCT4yjX6j97jlS+fCo2SsClLCU07h4WfJDP0wXIRUurQaQws+RCknYUy5xNsqO7cMkeYpBRmJGugUP6yPlCtdWORajLKGdT++e+agBP2vzGN5EbP2vFrghqkPHlSNrSp/ovSCiOi/RPCSIsCgYEAqn6jy7Mi1w2Xuj6rTUyIC5x+Nf4JLoVwMfXqhodAvgdcFKxO99uK6XRTs/bmsJ8o6TN0mwRIvGxlR5ATSIywGbYnMMJJHMJq+6uANKSk1bJrwnstj8VrXvoYoHxg6I4G7VkVBvoDL3PvMtFTQGeATZxZ8ETgxk2K4o7Fh3zZ/P4=";
    static PrivateKey ourPrivateKey;

    public PrivateKey getOurDefaultPrivateKey() throws Exception {
        if (ourPrivateKey==null) {
            ourPrivateKey= RuntimeContext.getInstance().getPaymentPrivateKey();
        }
        return ourPrivateKey;
    }

}
