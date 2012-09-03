/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_kgd;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.09.12
 * Time: 12:53
 * To change this template use File | Settings | File Templates.
 */
public class SBKGDOnlinePaymentServlet extends OnlinePaymentServlet {

    private static final Logger logger = LoggerFactory.getLogger(SBKGDOnlinePaymentServlet.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected OnlinePaymentRequestParser createParser() {
        return new SBKGDOnlinePaymentRequestParser();
    }


    @Override
    protected String getAuthenticatedRemoteAddressMasks(RuntimeContext runtimeContext, HttpServletRequest httpRequest, OnlinePaymentRequestParser requestParser) {
        return runtimeContext.getPartnerSBRTConfig().getRemoteAddressMasks();
    }

    @Override
    protected long getDefaultIdOfContragent(RuntimeContext runtimeContext) {
        return runtimeContext.getPartnerSBRTConfig().getIdOfContragent();
    }
    //
    //@Override
    //protected String getAuthenticatedRemoteAddressMasks(RuntimeContext runtimeContext, HttpServletRequest httpRequest,
    //        OnlinePaymentRequestParser requestParser) throws Exception {
    //  // StdPayConfig.LinkConfig linkConfig = null;
    //  //
    //  // X509Certificate[] certificates = (X509Certificate[]) httpRequest.getAttribute("javax.servlet.request.X509Certificate");
    //  //// if (certificates==null || certificates.length==0) throw new Exception("Client certificate missing in request");
    //  //
    //  // String DNs="";
    //  // for (int n=0;n<certificates.length;++n) {
    //  //      String dn = certificates[0].getSubjectDN().getName();
    //  //      linkConfig =  runtimeContext.getPartnerStdPayConfig().getLinkConfigByCertDN(dn);
    //  //      if (linkConfig!=null) break;
    //  //      DNs+=dn+";";
    //  // }
    //  // if (linkConfig==null) throw new Exception("Invalid client: DNs: "+DNs);
    //  //  ((SBKGDOnlinePaymentRequestParser)requestParser).setLinkConfig(linkConfig);
    //  // // ///
    //    //return linkConfig.remoteAddressMask;
    //    return httpRequest.getRemoteAddr();
    //}
    //
    //@Override
    //protected long getDefaultIdOfContragent(RuntimeContext runtimeContext) {
    //    return -1;
    //}
}
