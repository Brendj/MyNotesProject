/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paystd;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentServlet;
import ru.axetta.ecafe.processor.web.partner.sberbank_rt.SBRTOnlinePaymentRequestParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 02.04.11
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public class StdOnlinePaymentServlet extends OnlinePaymentServlet {
    private static final Logger logger = LoggerFactory.getLogger(StdOnlinePaymentServlet.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected OnlinePaymentRequestParser createParser() {
        return new StdOnlinePaymentRequestParser();
    }

    @Override
    protected String getAuthenticatedRemoteAddressMasks(RuntimeContext runtimeContext, HttpServletRequest httpRequest, OnlinePaymentRequestParser requestParser)  throws Exception {
        String partnerName = httpRequest.getParameter("PID");
        if (partnerName==null) throw new Exception("Parameter missing: PID");
        StdPayConfig.LinkConfig linkConfig =  runtimeContext.getPartnerStdPayConfig().getLinkConfig(partnerName);
        if (linkConfig==null) throw new Exception("Invalid PID");
        ((StdOnlinePaymentRequestParser)requestParser).setLinkConfig(linkConfig);
        ///
        if (linkConfig.checkSignature && linkConfig.partnerPublicKey==null) {
            linkConfig.partnerPublicKey = DAOUtils.getContragentPublicKey(runtimeContext, linkConfig.idOfContragent);
        }

        return linkConfig.remoteAddressMask;
    }

    @Override
    protected long getDefaultIdOfContragent(RuntimeContext runtimeContext) {
        return -1;
    }

}
