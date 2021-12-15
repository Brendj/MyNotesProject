package ru.axetta.ecafe.processor.web.partner.sberbank_rt;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentServlet;
import ru.axetta.ecafe.processor.web.partner.paystd.StdOnlinePaymentServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@WebServlet(
        name = "SBRTOnlinePaymentServlet",
        description = "SBRTOnlinePaymentServlet",
        urlPatterns = {"/payment-sbrt"}
)
public class SBRTOnlinePaymentServlet extends OnlinePaymentServlet {
    private static final Logger logger = LoggerFactory.getLogger(SBRTOnlinePaymentServlet.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected OnlinePaymentRequestParser createParser() {
        return new SBRTOnlinePaymentRequestParser();
    }

    @Override
    protected String getAuthenticatedRemoteAddressMasks(RuntimeContext runtimeContext, HttpServletRequest httpRequest, OnlinePaymentRequestParser requestParser) {
        return runtimeContext.getPartnerSBRTConfig().getRemoteAddressMasks();
    }

    @Override
    protected long getDefaultIdOfContragent(RuntimeContext runtimeContext) {
        return runtimeContext.getPartnerSBRTConfig().getIdOfContragent();
    }

}
