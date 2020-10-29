/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.web.partner.smartwatch.security.SmartWatchApiKeyManager;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.net.HttpURLConnection;
import java.util.List;

@Provider
@ServerInterceptor
@Precedence("SECURITY")
public class SmartWatchRequestInterceptor implements PreProcessInterceptor {

    private static final Logger logger = LoggerFactory.getLogger("SmartWatchRequestInterceptor");

    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod method)
            throws Failure, WebApplicationException {
        try {
            SmartWatchApiKeyManager manager = RuntimeContext.getAppContext().getBean(SmartWatchApiKeyManager.class);
            StringBuilder sb = new StringBuilder();
            UriInfo info = request.getUri();

            sb.append(String.format("Try process request: %s | Inputted QueryParam:\n", info.getPath()));
            for (String key : info.getQueryParameters().keySet()) {
                sb.append(key).append(" : ").append(info.getQueryParameters().get(key));
                sb.append("\n");
            }
            logger.info(sb.toString());

            HttpHeaders headers = request.getHttpHeaders();
            List<String> requestHeaderKey = headers.getRequestHeader("key");

            if (CollectionUtils.isEmpty(requestHeaderKey) || requestHeaderKey.get(0) == null) {
                throw new WebApplicationException(
                        Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("NOT_VALID_API_KEY").build());
            }

            String vendor = manager.getVendorByApiKey(requestHeaderKey.get(0));
            if (vendor == null){
                throw new WebApplicationException(
                        Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("NOT_VALID_API_KEY").build());
            }

            request.getHttpHeaders().getRequestHeaders().add("vendor", vendor);

            return null;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        }
    }
}
