/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.oku;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.ErrorResult;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.net.HttpURLConnection;
import java.util.List;

@Provider
@ServerInterceptor
@Precedence("SECURITY")
public class OkuRequestInterceptor implements PreProcessInterceptor {
    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethodInvoker method) throws Failure,
            WebApplicationException {

        String apiKey = RuntimeContext.getInstance().getOkuApiKey();

        HttpHeaders headers = request.getHttpHeaders();
        List<String> requestHeaderKey = headers.getRequestHeader("Authorization");

        if (CollectionUtils.isEmpty(requestHeaderKey) || !requestHeaderKey.get(0).equals(apiKey)){
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_UNAUTHORIZED)
                    .entity(ErrorResult.unauthorized())
                    .build());
        }
        return null;
    }

}
