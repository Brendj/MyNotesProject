/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SudirToken;
import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBaseListResult;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.MenuListWithComplexesResult;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWS;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Date;

/**
 * Created by i.semenov on 22.02.2018.
 */


//@Path("/client")
@Path("")
public class PreorderService {
    private static final Logger logger = LoggerFactory.getLogger(PreorderService.class);

    @Autowired
    PreorderDAOService daoService;

    @GET
    @Path("login")
    public Response login() throws Exception {
        String url = String.format("%s?redirect_uri=%s&response_type=code&client_id=%s",
                RuntimeContext.getAppContext().getBean(SudirClientService.class).SUDIR_AUTHORIZE_ADDRESS,
                RuntimeContext.getAppContext().getBean(SudirClientService.class).REDIRECT_URI,
                RuntimeContext.getAppContext().getBean(SudirClientService.class).CLIENT_ID);
        URI uri = new URI(url);
        return Response.temporaryRedirect(uri).build();
    }

    @GET
    @Path("clientsummary/{authCode}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response clientSummary(@PathParam("authCode") String authCode) throws Exception {
        String mobile;
        NewCookie cookie = null;
        if (authCode != null && authCode.length() > 16) {
            //Получаем токен в СУДИР
            SudirToken token = RuntimeContext.getInstance().getAppContext().getBean(SudirClientService.class).getToken(authCode);
            if (token == null) {
                URI uri = new URI("http://localhost:8000/notfound");
                return Response.temporaryRedirect(uri).build();
            }

            Cookie cook = new Cookie("access_token", token.getAccess_token(), "/", null);
            cookie = new NewCookie(cook, null, token.getExpires_in(), false);
            Response.ok().cookie(cookie);

            //Тут сохраняем токен в БД
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).saveToken(token);

            //Получаем данные пользователя в СУДИР
            SudirPersonData personData = RuntimeContext.getInstance().getAppContext().getBean(SudirClientService.class).getPersonData(token.getAccess_token());
            if (personData == null) {
                URI uri = new URI("http://localhost:8000/notfound");
                return Response.temporaryRedirect(uri).build();
            }

            //по полученному номеру телефона вытаскиваем getSummaryByGuardMobileMin
            mobile = personData.getPhone();
        } else {
            mobile = authCode;
        }

        ClientRoomControllerWS controller = new ClientRoomControllerWS();
        ClientSummaryBaseListResult clientSummary = controller.getSummaryByGuardMobileMin(PhoneNumberCanonicalizator.canonicalize(mobile));
        PreorderClientSummaryBaseListResult result = new PreorderClientSummaryBaseListResult(clientSummary);
        if (cookie == null) {
            return Response.ok(result.toString()).build();
        } else {
            return Response.ok(result.toString()).cookie(cookie).build();
        }
    }

    @GET
    @Path("client/specialmenu/{contractId}/{value}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response setPreorder(@PathParam("contractId") Long contractId, @PathParam("value") Integer value) {
        try {
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).setSpecialMenuFlag(contractId, value);
            return Response.ok().build();
        } catch (Exception e) {
            logger.error(String.format("Not found client for set preorder flag. ContractId=%s, flag=%s", contractId, value));
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("client/specialmenu/{contractId}/{value}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response setPreorderPut(@PathParam("contractId") Long contractId, @PathParam("value") Integer value) {
        return setPreorder(contractId, value);
    }

    @GET
    @Path("client/complexes/{contractId}/{date}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response getMenuListWithComplexes2(@PathParam("contractId") Long contractId, @PathParam("date") String date) {
        ClientRoomControllerWS controller = new ClientRoomControllerWS();
        try {
            Date ddate = CalendarUtils.parseDate(date);
            PreorderListWithComplexesResult res = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getPreorderComplexesWithMenuList(contractId, ddate);
                    //controller.getMenuListWithComplexes(contractId, CalendarUtils.startOfDay(ddate), CalendarUtils.endOfDay(ddate));
            return Response.ok(res).build();
        } catch (Exception e) {
            logger.error("Error getMenuListWithComplexes2: ", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("complexes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MenuListWithComplexesResult getMenuListWithComplexes(@QueryParam("filter") MenuListParam value) {
        ClientRoomControllerWS controller = new ClientRoomControllerWS();
        try {
            return controller.getMenuListWithComplexes(value.getContractId(), value.getStartDate(), value.getEndDate());
        } catch (Exception e) {

        }
        return null;
    }

    @POST
    @Path("complexes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveComplex(ComplexIdWrapper param) {
        String res = param.getComplexId().toString();
        String[] ss = param.getBu().split(",");
        for (String s : ss) {
            res += "|" + s;
        }
        return Response.status(200).entity(res).build();
    }
}
