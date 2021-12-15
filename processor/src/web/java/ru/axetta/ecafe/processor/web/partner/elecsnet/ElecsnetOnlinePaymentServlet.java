package ru.axetta.ecafe.processor.web.partner.elecsnet;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentRequestParser;
import ru.axetta.ecafe.processor.web.partner.OnlinePaymentServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Игорь
 * Date: 18.12.2010
 * Time: 0:21:43
 * To change this template use File | Settings | File Templates.
 */
@WebServlet(
        name = "ElecsnetOnlinePaymentServlet",
        description = "ElecsnetOnlinePaymentServlet",
        urlPatterns = {"/payment-elecsnet"}
)
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

