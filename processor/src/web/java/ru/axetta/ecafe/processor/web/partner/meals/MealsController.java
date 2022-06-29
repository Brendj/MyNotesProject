/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.meals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.axetta.ecafe.processor.core.service.MealsService;
import ru.axetta.ecafe.processor.web.partner.meals.models.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;

@Path(value = "")
@Controller
@ApplicationPath("/ispp/meals/v1/")
public class MealsController extends Application {

    @Autowired
    private ResponseResult responseResult;

    @Autowired
    private MealsService mealsService;

    private Logger logger = LoggerFactory.getLogger(MealsController.class);

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path(value = "contract.yaml")
    public Response getFile() throws IOException {
        File file = File.createTempFile("prefix-", "-suffix");
        MealsService mealsService = new MealsService();
        mealsService.copyResourceToFile("swagger/foodbox.yaml", file);
        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=contract.yaml" )
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "orders/foodbox")
    public Response addPersonFoodboxOrder(@Context HttpServletRequest request, FoodboxOrder foodboxOrders) {
        //Контроль безопасности
        if (!mealsService.validateAccess()) {
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).
                    entity(responseResult.errorAuth()).build();
        }

        String contractIdStr = "";
        Map<String, String> params = parseParams(request);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            if (currParam.getKey().toLowerCase().equals("contractid")) {
                contractIdStr = currParam.getValue();
                break;
            }
        }

        String xrequestStr = "";
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                if (header.toLowerCase().equals("x-request-id")) {
                    xrequestStr = request.getHeader(header);
                    break;
                }
            }
        }

        MealsPOJO mealsPOJO = new MealsPOJO();
        //Логика проверки корректности запроса
        mealsPOJO = mealsService.validateByFormalInfo(contractIdStr,xrequestStr);
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO.getResponse();

        //Логика всех проверок по клиенту
        mealsPOJO = mealsService.validateByClientInfo(mealsPOJO.getContractId(), 1);
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO.getResponse();

        //Логика запросов к бд
        mealsService.getDataFromDAO(mealsPOJO.getClient(), 1);

        //Логика обработки самого заказа
        return mealsService.mainLogicNewPreorder(foodboxOrders, mealsPOJO.getClient(), xrequestStr, mealsPOJO.getAvailableMoney());
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "orders/foodbox")
    public Response getPersonFoodboxOrders(@Context HttpServletRequest request) {
        Result result = new Result();
        //Контроль безопасности
        if (!mealsService.validateAccess()) {
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).
                    entity(responseResult.errorAuth()).build();
        }
        String contractIdStr = "";
        String fromStr = "";
        String toStr = "";
        String sortStr = "";
        Long contract = null;
        Map<String, String> params = parseParams(request);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            if (currParam.getKey().toLowerCase().equals("from")) {
                fromStr = currParam.getValue();
                fromStr = fromStr.replace("%3A", ":");
                fromStr = fromStr.replace("%20", " ");
                continue;
            }
            if (currParam.getKey().toLowerCase().equals("to")) {
                toStr = currParam.getValue();
                toStr = toStr.replace("%3A", ":");
                toStr = toStr.replace("%20", " ");
                continue;
            }
            if (currParam.getKey().toLowerCase().equals("sort")) {
                sortStr = currParam.getValue();
                continue;
            }
            if (currParam.getKey().toLowerCase().equals("contractid")) {
                contractIdStr = currParam.getValue();
                continue;
            }
        }
        //Логика проверки корректности запроса
        MealsPOJO mealsPOJO = mealsService.validateByFormalInfo2(contractIdStr, fromStr, toStr, sortStr);
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO.getResponse();
        //Логика всех проверок по клиенту
        mealsPOJO = mealsService.validateByClientInfo2(mealsPOJO.getContractId());
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO.getResponse();

        //Логика обработки самого заказа
       return mealsService.mainLogicNewPreorder2(mealsPOJO.getClient(),
               mealsPOJO.getFrom(), mealsPOJO.getTo(), mealsPOJO.getSortDesc());
    }


    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "orders/foodbox/{foodboxOrderId}")
    public Response getPersonFoodboxOrder(@Context HttpServletRequest request, @PathParam("foodboxOrderId") String foodboxOrderId) {
        //Контроль безопасности
        if (!mealsService.validateAccess()) {
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).
                    entity(responseResult.errorAuth()).build();
        }

        //Логика проверки корректности запроса
        MealsPOJO mealsPOJO = mealsService.validateByFormalInfo3(foodboxOrderId);
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO.getResponse();

        //Логика обработки самого заказа
        return mealsService.mainLogicNewPreorder3(mealsPOJO.getIsppIdFoodbox());
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "menu/buffet")
    public Response getPersonBuffetMenu(@Context HttpServletRequest request) {
        Result result = new Result();
        //Контроль безопасности
        if (!mealsService.validateAccess()) {
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).
                    entity(responseResult.errorAuth()).build();
        }

        String contractIdStr = "";
        String onDateStr = "";

        Map<String, String> params = parseParams(request);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            if (currParam.getKey().toLowerCase().equals("contractid")) {
                contractIdStr = currParam.getValue();
                continue;
            }
            if (currParam.getKey().toLowerCase().equals("ondate")) {
                {
                    onDateStr = currParam.getValue();
                    onDateStr = onDateStr.replace("%3A", ":");
                    onDateStr = onDateStr.replace("%20", " ");
                    continue;
                }
            }
        }

        //Логика проверки корректности запроса
        MealsPOJO mealsPOJO = mealsService.validateByFormalInfo4(onDateStr, contractIdStr);
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO.getResponse();

        //Логика всех проверок по клиенту
        mealsPOJO = mealsService.validateByClientInfo(mealsPOJO.getContractId(), 2);
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO.getResponse();

        //Логика запросов к бд
        mealsService.getDataFromDAO(mealsPOJO.getClient(), 2);

        //Логика получения самого меню
        return mealsService.mainLogicNewPreorder4();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "clients/foodboxAllowed")
    public Response setPersonFoodboxAllowed(@Context HttpServletRequest request) {
        Result result = new Result();
        //Контроль безопасности
        if (!mealsService.validateAccess()) {
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).
                    entity(responseResult.errorAuth()).build();
        }

        String contractIdStr = "";
        String foodBoxAvailableStr = "";
        Map<String, String> params = parseParams(request);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            if (currParam.getKey().toLowerCase().equals("contractid")) {
                contractIdStr = currParam.getValue();
                continue;
            }
            if (currParam.getKey().toLowerCase().equals("foodboxallowed")) {
                foodBoxAvailableStr = currParam.getValue();
                continue;
            }
        }

        //Логика проверки корректности запроса
        MealsPOJO mealsPOJO = mealsService.validateByFormalInfo5(contractIdStr, foodBoxAvailableStr, 1);
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO.getResponse();

        //Логика всех проверок по клиенту
        mealsPOJO = mealsService.validateByClientInfo3(mealsPOJO.getContractId());
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO.getResponse();

        //Логика установки флага
        return mealsService.mainLogicNewPreorder5(mealsPOJO.getClient(), mealsPOJO.getFoodBoxAvailable());
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "clients")
    public Response getClientData(@Context HttpServletRequest request) {
        Result result = new Result();
        //Контроль безопасности
        if (!mealsService.validateAccess()) {
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).
                    entity(responseResult.errorAuth()).build();
        }
        String contractIdStr = "";
        Map<String, String> params = parseParams(request);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            if (currParam.getKey().toLowerCase().equals("contractid")) {
                contractIdStr = currParam.getValue();
                continue;
            }
        }
        //Логика проверки корректности запроса
        MealsPOJO mealsPOJO = mealsService.validateByFormalInfo5(contractIdStr, null,2);
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO.getResponse();

        //Логика всех проверок по клиенту
        mealsPOJO = mealsService.validateByClientInfo3(mealsPOJO.getContractId());
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO.getResponse();

        //Логика получения флага
        return mealsService.mainLogicNewPreorder6(mealsPOJO.getClient(), mealsPOJO.getFoodBoxAvailable());
    }

    private Map<String, String> parseParams(HttpServletRequest httpRequest) {
        Map<String, String> map = new HashMap<>();
        String paramString = httpRequest.getQueryString();
        if (paramString != null) {
            String[] arr = paramString.split("&");
            for (String param : arr) {
                String[] arr2 = param.split("=");
                map.put(arr2[0], arr2[1]);
            }
        }
        return map;
    }
}
