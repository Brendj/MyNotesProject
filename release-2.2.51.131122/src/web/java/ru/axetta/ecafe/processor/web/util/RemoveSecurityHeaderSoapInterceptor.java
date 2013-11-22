package ru.axetta.ecafe.processor.web.util;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;

public class RemoveSecurityHeaderSoapInterceptor extends AbstractSoapInterceptor {

    Logger logger = LoggerFactory.getLogger(RemoveSecurityHeaderSoapInterceptor.class);
    
    public RemoveSecurityHeaderSoapInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(SoapMessage soapMessage) throws Fault {
        Header header = soapMessage.getHeader(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security"));
        if (header!=null) {
            soapMessage.getHeaders().remove(header);
        }
    }


}
