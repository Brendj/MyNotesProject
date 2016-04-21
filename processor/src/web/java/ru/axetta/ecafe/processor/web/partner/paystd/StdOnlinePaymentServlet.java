/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paystd;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.stdpay.StdPayConfig;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentServlet;
import ru.axetta.ecafe.processor.web.partner.integra.soap.PaymentControllerWS;
import ru.axetta.ecafe.processor.web.partner.sberbank_rt.SBRTOnlinePaymentRequestParser;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;

public class StdOnlinePaymentServlet extends OnlinePaymentServlet {

    private static final Logger logger = LoggerFactory.getLogger(StdOnlinePaymentServlet.class);
    public static final String ATTR_PAY_RESPONSE = "payResponse";

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected OnlinePaymentRequestParser createParser() {
        return new StdOnlinePaymentRequestParser();
    }

    @Override
    protected String getAuthenticatedRemoteAddressMasks(RuntimeContext runtimeContext, HttpServletRequest httpRequest,
            OnlinePaymentRequestParser requestParser) throws Exception {
        String partnerName = requestParser.getRequestParams().getParam("PID");
        StdPayConfig.LinkConfig linkConfig = null;
        if (partnerName == null) {
            // try auth by ssl cert
            String DNs = "";
            X509Certificate[] certificates = (X509Certificate[]) httpRequest
                    .getAttribute("javax.servlet.request.X509Certificate");
            if (certificates != null && certificates.length > 0) {
                for (int n = 0; n < certificates.length; ++n) {
                    String dn = certificates[0].getSubjectDN().getName();
                    linkConfig = runtimeContext.getPartnerStdPayConfig().getLinkConfigByCertDN(dn);
                    if (linkConfig != null) {
                        break;
                    }
                    DNs += dn + ";";
                }
            }
            // try auth by login and password
            if (linkConfig == null) {
                AuthorizationPolicy authorizationPolicy = (AuthorizationPolicy) httpRequest
                        .getAttribute(PaymentControllerWS.AUTH_POLICY_KEY);
                if (authorizationPolicy != null && authorizationPolicy.getUserName() != null) {
                    linkConfig = runtimeContext.getPartnerStdPayConfig()
                            .getLinkConfigWithAuthTypeBasicMatching(authorizationPolicy.getUserName(),
                                    authorizationPolicy.getPassword());
                }
            }
            if (linkConfig == null) {
                throw new Exception("PID parameter missing and invalid client certificatew: DNs: " + DNs
                        + " and authentication by login and password failed");
            }

        } else {
            partnerName = requestParser.getRequestParams().getParam("PID").toLowerCase();
            linkConfig = runtimeContext.getPartnerStdPayConfig().getLinkConfig(partnerName);
        }
        if (linkConfig == null) {
            throw new Exception("Invalid PID");
        }
        ((StdOnlinePaymentRequestParser) requestParser).setLinkConfig(linkConfig);
        ///
        if (linkConfig.checkSignature && linkConfig.partnerPublicKey == null) {
            linkConfig.partnerPublicKey = DAOUtils.getContragentPublicKey(runtimeContext, linkConfig.idOfContragent);
        }

        return linkConfig.remoteAddressMask;
    }

    @Override
    protected long getDefaultIdOfContragent(RuntimeContext runtimeContext) {
        return -1;
    }

}
