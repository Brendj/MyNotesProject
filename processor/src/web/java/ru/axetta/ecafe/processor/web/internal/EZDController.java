/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
import ru.axetta.ecafe.processor.core.service.RecoverableService;
import ru.axetta.ecafe.processor.web.partner.ezd.ResponseFromEzd;
import ru.axetta.ecafe.processor.web.partner.ezd.Result;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import java.util.List;

@WebService
public class EZDController extends HttpServlet {

    @WebMethod(operationName = "dataFromEZD")
    public Result setDataFromEZD ( @WebParam(name = "orders") List<ResponseFromEzd> orders) {

        for (ResponseFromEzd responseFromEzd: orders)
        {
            responseFromEzd.getGuidOrg();



        }


        return "OK";
    }
}
