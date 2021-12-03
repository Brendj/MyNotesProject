package ru.axetta.ecafe.processor.web.util;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.EndpointSelectionInterceptor;
import org.apache.cxf.binding.soap.interceptor.ReadHeadersInterceptor;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import java.util.List;
import java.util.Map;

public class RemoveSoapActionInterceptor extends AbstractSoapInterceptor {
    public RemoveSoapActionInterceptor() {
        super(Phase.RECEIVE);
    }

    @Override
    public void handleMessage(SoapMessage soapMessage) throws Fault {
        Map<String, List<String>> headers = CastUtils.cast((Map)soapMessage.get(Message.PROTOCOL_HEADERS));
        if (headers != null) {
            headers.remove("SOAPAction");
        }
    }
}
