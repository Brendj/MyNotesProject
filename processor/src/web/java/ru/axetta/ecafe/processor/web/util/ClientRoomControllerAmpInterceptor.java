package ru.axetta.ecafe.processor.web.util;

import org.apache.cxf.phase.Phase;

public class ClientRoomControllerAmpInterceptor extends AmpersandInterceptor {

    public ClientRoomControllerAmpInterceptor(){
        super();
    }

    protected String changeOutboundMessage(String currentEnvelopeMessage) {
        try {
            String result = currentEnvelopeMessage.replaceAll("&amp;quot;", "&quot;")
            .replaceAll("&amp;lt;", "&lt;")
            .replaceAll("&amp;gt;", "&gt;")
            .replaceAll("&amp;amp;", "&amp;");
            return result;
        } catch (Exception ignore){
            return null;
        }
    }

}
