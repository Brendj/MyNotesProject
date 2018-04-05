/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SudirToken;
import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBaseListResult;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
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


@Path("")
public class PreorderService {
    private static final Logger logger = LoggerFactory.getLogger(PreorderService.class);

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
    @Path("logout")
    public Response logout() throws Exception {
        String url = String.format("%s?logoutExitPage=%s",
                RuntimeContext.getAppContext().getBean(SudirClientService.class).SUDIR_LOGOUT_ADDRESS,
                RuntimeContext.getAppContext().getBean(SudirClientService.class).REDIRECT_LOGOUT_URI);
        URI uri = new URI(url);
        return Response.temporaryRedirect(uri).build();
    }

    private void authorize(Long contractId, String access_token) throws PreorderAccessDeniedException {
        if (!RuntimeContext.getAppContext().getBean(SudirClientService.class).SECURITY_ON) {
            return;
        }
        if (!RuntimeContext.getAppContext().getBean(PreorderDAOService.class).matchToken(access_token, contractId)) {
            throw new PreorderAccessDeniedException();
        }
    }

    @GET
    @Path("clientsummary/{authCode}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response clientSummary(@PathParam("authCode") String authCode) throws Exception {
        String mobile;
        NewCookie cookie = null;
        SudirToken token = null;
        String tok = "";
        if (authCode != null && authCode.length() > 16) {
            //Получаем токен в СУДИР
            token = RuntimeContext.getInstance().getAppContext().getBean(SudirClientService.class).getToken(authCode);
            if (token == null) {
                logger.error("Cannot get token " + authCode);
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Cookie cook = new Cookie("access_token", token.getAccess_token(), "/processor/preorder", null);
            cookie = new NewCookie(cook, null, -1, true);
            tok = token.getAccess_token();

            //Получаем данные пользователя в СУДИР
            SudirPersonData personData = RuntimeContext.getInstance().getAppContext().getBean(SudirClientService.class).getPersonData(token.getAccess_token());
            if (personData == null) {
                logger.error("Cannot get person data " + authCode + " " + token.getAccess_token());
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            //по полученному номеру телефона вытаскиваем getSummaryByGuardMobileMin
            mobile = personData.getPhone();
        } else {
            mobile = authCode;
        }
        ClientSummaryBaseListResult clientSummary = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                .getClientSummaryByGuardianMobileWithPreorderEnableFilter(PhoneNumberCanonicalizator.canonicalize(mobile));
        PreorderClientSummaryBaseListResult result = new PreorderClientSummaryBaseListResult(clientSummary);
        if (cookie == null) {
            return Response.ok(result.toString()).header("Mobile", mobile).build();
        } else {
            //сохраняем токен и связанные л/с в БД
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).saveToken(token, clientSummary);
            return Response.ok(result.toString()).cookie(cookie).header("Authorization", tok).header("Mobile", mobile).build();
        }
    }

    @GET
    @Path("client/specialmenu/{contractId}/{value}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response setPreorder(@PathParam("contractId") Long contractId, @PathParam("value") Integer value) {
        try {
            authorize(contractId, RuntimeContext.getAppContext().getBean(TokenService.class).getToken());
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).setSpecialMenuFlag(contractId, value);
            return Response.ok().build();
        } catch (PreorderAccessDeniedException e1) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (Exception e) {
            logger.error(String.format("Not found client for set preorder flag. ContractId=%s, flag=%s", contractId, value));
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("client/complexes/{contractId}/{date}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response getMenuListWithComplexes(@PathParam("contractId") Long contractId, @PathParam("date") String date) {
        try {
            authorize(contractId, RuntimeContext.getAppContext().getBean(TokenService.class).getToken());
            Date ddate = CalendarUtils.parseDate(date);
            PreorderListWithComplexesGroupResult res = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getPreorderComplexesWithMenuList(contractId, ddate);
            return Response.ok(res).build();
        } catch (PreorderAccessDeniedException e1) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (NoResultException e2) {
            logger.info(String.format("Not found client with contractId=%s in getMenuListWithComplexes", contractId));
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error getMenuListWithComplexes: ", e);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("client/complexes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveComplex(PreorderSaveListParam list) {
        SaveComplexResult res = new SaveComplexResult() ;
        try {
            authorize(list.getContractId(), RuntimeContext.getAppContext().getBean(TokenService.class).getToken());
            logRequest(list);
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).savePreorderComplexes(list);
            res.setCode(0);
            res.setMessage("OK");
        } catch (PreorderAccessDeniedException e1) {
            res.setCode(403);
            res.setMessage("Access denied");
        } catch (Exception e) {
            logger.error("Error saveComplex: ", e);
            res.setCode(500);
            res.setMessage("Internal server error");
        }
        return Response.ok(res).build();
    }

    private void logRequest(PreorderSaveListParam list) {
        logger.info("Incoming request save preorder: " + list.toString());
    }
}
