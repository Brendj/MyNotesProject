package ru.axetta.ecafe.processor.web.partner.elecsnet;

import sun.security.rsa.RSACore;
import sun.security.rsa.RSAPadding;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.ConversionUtils;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentServlet;
import ru.axetta.ecafe.processor.web.partner.sberbank_rt.SBRTOnlinePaymentRequestParser;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAKey;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Игорь
 * Date: 18.12.2010
 * Time: 0:21:43
 * To change this template use File | Settings | File Templates.
 */
public class ElecsnetOnlinePaymentServlet extends OnlinePaymentServlet {
    private static final Logger logger = LoggerFactory.getLogger(ElecsnetOnlinePaymentServlet.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected OnlinePaymentRequestParser createParser() {
        return new ElecsnetOnlinePaymentRequestParser();
    }

    @Override
    protected String getAuthenticatedRemoteAddressMasks(RuntimeContext runtimeContext, HttpServletRequest httpRequest,
            OnlinePaymentRequestParser requestParser) throws Exception {
        return runtimeContext.getPartnerElecsnetConfig().getRemoteAddressMasks();
    }

    @Override
    protected long getDefaultIdOfContragent(RuntimeContext runtimeContext) {
        return runtimeContext.getPartnerElecsnetConfig().getIdOfContragent();
    }


}

