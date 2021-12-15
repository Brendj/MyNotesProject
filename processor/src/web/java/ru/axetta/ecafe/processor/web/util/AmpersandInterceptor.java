package ru.axetta.ecafe.processor.web.util;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.phase.Phase;

import java.io.InputStream;
import java.io.OutputStream;

public class AmpersandInterceptor extends AbstractSoapInterceptor {
    public AmpersandInterceptor(){
        super(Phase.MARSHAL_ENDING);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        try {
            OutputStream outputStream = message.getContent(OutputStream.class);
            CachedOutputStream cachedOutputStream = new CachedOutputStream();
            message.setContent(OutputStream.class, cachedOutputStream);

            message.getInterceptorChain().doIntercept(message);

            cachedOutputStream.flush();
            cachedOutputStream.close();

            CachedOutputStream newCachedOutputStream = (CachedOutputStream) message.getContent(OutputStream.class);
            String currentResponse = IOUtils.toString(newCachedOutputStream.getInputStream(), "UTF-8");
            newCachedOutputStream.flush();
            newCachedOutputStream.close();

            String newResponse = changeOutboundMessage(currentResponse);
            String result = newResponse == null ? currentResponse : newResponse;

            InputStream replaceInputStream = IOUtils.toInputStream(result, "UTF-8");

            IOUtils.copy(replaceInputStream, outputStream);
            replaceInputStream.close();

            message.setContent(OutputStream.class, outputStream);
            outputStream.flush();
            outputStream.close();

            message.put("disable.outputstream.optimization", Boolean.TRUE);
        } catch (Exception e){
            throw new Fault(e);
        }
    }

    protected String changeOutboundMessage(String currentEnvelopeMessage) {
        try {
            return currentEnvelopeMessage.replaceAll("&amp;amp;", "&amp;");
        } catch (Exception ignore){
            return null;
        }
    }
}
