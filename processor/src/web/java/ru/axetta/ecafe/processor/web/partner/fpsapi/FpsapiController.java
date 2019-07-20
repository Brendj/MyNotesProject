/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;

@Path(value = "")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Controller
public class FpsapiController {

    private Logger logger = LoggerFactory.getLogger(FpsapiController.class);

    @GET
    @Path(value = "/netrika/mobile/v1/sales")
    public Response getSales (@QueryParam(value="regID") String regID,
            @QueryParam(value="DateFrom") Long dateFrom,
            @QueryParam(value="DateTo") Long dateTo)throws Exception{
        ResponseSales responseSales = new ResponseSales();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        //Вычисление результата запроса
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            responseSales.setserverTimestamp(new Date());
            Client client = DAOService.getInstance().getClientByIacregid(regID);
            if (client == null)
                throw new IllegalArgumentException("Client with regID = " + regID +" is not found");

            List<Order> orders = DAOUtils.findOrdersbyIdofclientandBetweenTime(persistenceSession, client, new Date(dateFrom), new Date(dateTo));
            if (orders.isEmpty())
                return resultOK(null);
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return resultOK(responseSales);
        } catch (IllegalArgumentException e)
        {
            logger.error("Can't find client", e);
            return resultBadArgs(responseSales);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

    }

    private <T extends IfpsapiBase> Response resultOK(T result) {
        result.getResult().resultCode = ResponseCodes.RC_OK.getCode();
        result.getResult().description = ResponseCodes.RC_OK.toString();
        return Response.status(HttpURLConnection.HTTP_OK)
                .entity(result)
                .build();
    }

    private Response resultBadArgs(ResponseSales result) {
        result.getResult().resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
        result.getResult().description = ResponseCodes.RC_INTERNAL_ERROR.toString();
        return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                .entity(result)
                .build();
    }

}
