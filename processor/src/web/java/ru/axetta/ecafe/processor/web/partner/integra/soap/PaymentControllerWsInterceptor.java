package ru.axetta.ecafe.processor.web.partner.integra.soap;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.phase.Phase;
import org.richfaces.log.LogFactory;
import org.richfaces.log.Logger;
import ru.axetta.ecafe.processor.web.util.AmpersandInterceptor;

import java.io.InputStream;
import java.io.OutputStream;

public class PaymentControllerWsInterceptor extends AmpersandInterceptor {
    private final Logger log = LogFactory.getLogger(PaymentControllerWsInterceptor.class);

    protected String changeOutboundMessage(String currentEnvelopeMessage) {
        try {
            return currentEnvelopeMessage.replaceAll("&amp;amp;", "&amp;");
        } catch (Exception ignore){
            return null;
        }
    }
}
