package ru.axetta.ecafe.processor.web.partner;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Игорь
 * Date: 18.12.2010
 * Time: 0:25:08
 * To change this template use File | Settings | File Templates.
 */
abstract public class OnlinePaymentServlet extends HttpServlet {
    protected abstract Logger getLogger();

    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws ServletException, IOException {
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            OnlinePaymentRequestParser requestParser=createParser();
            try {
                if (!authenticate(httpRequest, requestParser)) {
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } catch (Exception e) {
                getLogger().error("Failed to authenticate request", e);
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            OnlinePaymentProcessor.PayRequest payRequest=null;
            long contragentId=getDefaultIdOfContragent(runtimeContext);
            try {
                payRequest = requestParser.parsePayRequest(contragentId, httpRequest);
            } catch (Exception e) {
                getLogger().error("Failed to parse request", e);
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            getLogger().info(String.format("New request: %s", payRequest.toString()));
            OnlinePaymentProcessor.PayResponse response;
            try {
                response = runtimeContext.getOnlinePaymentProcessor().processPayRequest(payRequest);
            } catch (Exception e) {
                getLogger().error("Failed to process request", e);
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            getLogger().info(String.format("Request (%s) processed: %s", requestParser.toString(), response.toString()));
            try {
                requestParser.serializeResponse(response, httpResponse);
            } catch (Exception e) {
                getLogger().error("Failed to serialize response", e);
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        } catch (Exception e) {
            getLogger().error("Failed", e);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected abstract OnlinePaymentRequestParser createParser();
    protected abstract String getAuthenticatedRemoteAddressMasks(RuntimeContext runtimeContext,
            HttpServletRequest httpRequest,
            OnlinePaymentRequestParser requestParser) throws Exception;
    protected abstract long getDefaultIdOfContragent(RuntimeContext runtimeContext);


    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws ServletException, IOException {
        doPost(httpRequest, httpResponse);
    }

    private boolean authenticate(HttpServletRequest httpRequest, OnlinePaymentRequestParser requestParser) throws Exception {
        String remoteAddress = httpRequest.getRemoteAddr();
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(String.format("remoteAddress: %s", remoteAddress));
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String remoteAddressMasks = getAuthenticatedRemoteAddressMasks(runtimeContext, httpRequest, requestParser);
        if (remoteAddress.matches(remoteAddressMasks)) {
            return true;
        }
        getLogger().error(String.format("Authentication failed for: %s", remoteAddress));
        return false;
    }



}
