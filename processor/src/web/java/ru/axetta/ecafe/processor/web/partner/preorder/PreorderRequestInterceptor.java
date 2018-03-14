/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Created by i.semenov on 05.03.2018.
 */
@Provider
@ServerInterceptor
@Precedence("SECURITY")
public class PreorderRequestInterceptor implements PreProcessInterceptor {
    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {

        HttpHeaders headers = request.getHttpHeaders();
        List<String> header = headers.getRequestHeader("access_token");
        if (header != null && header.size() > 0) {
            System.out.println(header.get(0));
        } else {
            //throw new WebApplicationException();
        }

        return null;
    }

}
