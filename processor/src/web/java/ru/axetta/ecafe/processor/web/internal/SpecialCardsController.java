/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 * Created by i.semenov on 18.01.2018.
 */
@WebService(targetNamespace = "http://ru.axetta.ecafe")
public class SpecialCardsController extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(SpecialCardsController.class);
    @Resource
    private WebServiceContext context;

    private final static String PARAMETER_USERNAME = "ecafe.processor.specialcards.service.username";
    private final static String PARAMETER_PASSWORD = "ecafe.processor.specialcards.service.password";

    @WebMethod(operationName = "registerSpecialCard")
    public  ResponseItem registerSpecialCard(@WebParam(name = "orgId") long idOfOrg,
            @WebParam(name = "cardNo") long cardNo, @WebParam(name = "cardPrintedNo") long cardPrintedNo,
            @WebParam(name = "type") int type) {

        if (!authenticateRequest()) {
            return new ResponseItem(ResponseItem.ERROR_AUTHENTICATOIN_FAILED, ResponseItem.ERROR_AUTHENTICATION_FAILED_MESSAGE);
        }

        CardService cardService = CardService.getInstance();
        if (!(type >=0 && type < Card.TYPE_NAMES.length)) {
            return new ResponseItem(ResponseItem.ERROR_INVALID_TYPE, ResponseItem.ERROR_INVALID_TYPE_MESSAGE);
        }
        try{
            cardService.registerNewSpecial(idOfOrg, cardNo, cardPrintedNo, type, null, null);
        } catch (Exception e){
            if (e.getMessage().contains("ConstraintViolationException")) {
                return new ResponseItem(ResponseItem.ERROR_DUPLICATE, ResponseItem.ERROR_DUPLICATE_CARD_MESSAGE);
            } else if (e.getMessage().contains("cardNo not found")) {
                return new ResponseItem(ResponseItem.ERROR_SPECIAL_CARD_NOT_FOUND, ResponseItem.ERROR_SPECIAL_CARD_NOT_FOUND_MESSAGE);
            } else if (e.getMessage().contains("Org not found")) {
                return new ResponseItem(ResponseItem.ERROR_ORG_NOT_FOUND, ResponseItem.ERROR_ORG_NOT_FOUND_MESSAGE);
            }
            logger.error("Error in registerSpecialCard: ", e);
            return new ResponseItem(ResponseItem.ERROR_INTERNAL, ResponseItem.ERROR_INTERNAL_MESSAGE);
        }
        return new ResponseItem();
    }

    private boolean authenticateRequest() {
        MessageContext jaxwsContext = context.getMessageContext();
        AuthorizationPolicy authorizationPolicy = (AuthorizationPolicy) jaxwsContext.get("org.apache.cxf.configuration.security.AuthorizationPolicy");
        if (authorizationPolicy != null && authorizationPolicy.getUserName() != null && authorizationPolicy.getPassword() != null) {
            String userName = RuntimeContext.getInstance().getPropertiesValue(PARAMETER_USERNAME, null);
            String password = RuntimeContext.getInstance().getPropertiesValue(PARAMETER_PASSWORD, null);
            if (userName == null || password == null) return false;
            return userName.equals(authorizationPolicy.getUserName()) && password.equals(authorizationPolicy.getPassword());
        }
        return false;
    }
}
