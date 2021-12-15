/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.way4;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

@WebServlet(
        name = "Way4PaymentServlet",
        description = "Way4PaymentServlet",
        urlPatterns = {"/payment-way4"}
)
public class Way4PaymentServlet extends OnlinePaymentServlet {
    private static final Logger logger = LoggerFactory.getLogger(Way4PaymentServlet.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected OnlinePaymentRequestParser createParser() {
        return new Way4PaymentRequestParser();
    }

    @Override
    protected String getAuthenticatedRemoteAddressMasks(RuntimeContext runtimeContext, HttpServletRequest httpRequest, OnlinePaymentRequestParser requestParser)  throws Exception {
        StdPayConfig.LinkConfig linkConfig = null;

        X509Certificate[] certificates = (X509Certificate[]) httpRequest.getAttribute("javax.servlet.request.X509Certificate");
        if (certificates==null || certificates.length==0) throw new Exception("Client certificate missing in request");

        String DNs="";
        for (int n=0;n<certificates.length;++n) {
            String dn = certificates[0].getSubjectDN().getName();
            linkConfig =  runtimeContext.getPartnerStdPayConfig().getLinkConfigByCertDN(dn);
            if (linkConfig!=null) break;
            DNs+=dn+";";
        }
        if (linkConfig==null) throw new Exception("Invalid client: DNs: "+DNs);
        ((Way4PaymentRequestParser)requestParser).setLinkConfig(linkConfig);
        ///
        return linkConfig.remoteAddressMask;
        //return httpRequest.getRemoteAddr();
    }

    @Override
    protected long getDefaultIdOfContragent(RuntimeContext runtimeContext) {
        return -1;
    }

}
