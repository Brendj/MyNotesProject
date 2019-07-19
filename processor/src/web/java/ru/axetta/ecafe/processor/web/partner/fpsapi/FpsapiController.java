/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;
import ru.axetta.ecafe.processor.web.partner.smartwatch.IJsonBase;
import ru.axetta.ecafe.processor.web.partner.smartwatch.ResponseCodes;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

@Path(value = "")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Controller
public class FpsapiController {

    @GET
    @Path(value = "/netrika/mobile/v1/sales")
    public Response getSales (@QueryParam(value="regID") String regID,
            @QueryParam(value="DateFrom") Long DateFrom,
            @QueryParam(value="DateTo") Long DateTo)throws Exception{
        ResponseSales responseSales = new ResponseSales();
        //Вычисление результата запроса

        //
        return resultOK(responseSales);
    }

    private <T extends IJsonBase> Response resultOK(T result) {
        result.getResult().resultCode = ResponseCodes.RC_OK.getCode();
        result.getResult().description = ResponseCodes.RC_OK.toString();
        return Response.status(HttpURLConnection.HTTP_OK)
                .entity(result)
                .build();
    }

}
