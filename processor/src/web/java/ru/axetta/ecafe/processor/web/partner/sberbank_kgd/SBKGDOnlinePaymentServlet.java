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

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.09.12
 * Time: 12:53
 * To change this template use File | Settings | File Templates.
 */
@WebServlet(
        name = "SBKGDOnlinePaymentServlet",
        description = "SBKGDOnlinePaymentServlet",
        urlPatterns = {"/payment-sbkgd"}
)
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
    protected String getAuthenticatedRemoteAddressMasks(RuntimeContext runtimeContext, HttpServletRequest httpRequest,
            OnlinePaymentRequestParser requestParser) throws Exception {
        StdPayConfig.LinkConfig linkConfig = runtimeContext.getPartnerStdPayConfig().getLinkConfigByAdapter("sber_kgd");
        if (linkConfig==null) throw new Exception("Link config with adapter type sber_kgd  not configured.");
        ((SBKGDOnlinePaymentRequestParser)requestParser).setLinkConfig(linkConfig);
        return linkConfig.remoteAddressMask;
    }

    @Override
    protected long getDefaultIdOfContragent(RuntimeContext runtimeContext) {
        return -1;
    }
}
