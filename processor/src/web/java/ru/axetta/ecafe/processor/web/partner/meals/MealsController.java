/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.meals;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxOrgReq;
import ru.axetta.ecafe.processor.core.service.CancelledFoodBoxService;
import ru.axetta.ecafe.processor.core.service.MealsService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.meals.models.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/ispp/meals/v1/")
public class MealsController extends Application {

    @Autowired
    private ResponseResult responseResult;

    @Autowired
    private MealsService mealsService;

    private Logger logger = LoggerFactory.getLogger(MealsController.class);

    public enum MealsFunctions {
        CREATE_FOODBOX("Создать заказ"),
        GET_FOODBOX("Получить список фудбокс-заказов по одному обучающемуся"),
        GET_FOODBOX_BY_ID("Получить заказ по идентификатору"),
        GET_BUFET("Получить буфетное меню"),
        SET_FOODBOX_ALLOWED("Изменение разрешений по фудбоксу для одного обучающемуся"),
        GET_FOODBOX_ALLOWED("Получить данные по клиенту"),
        ;

        private final String description;

        private MealsFunctions(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path(value = "contract.yaml")
    public Response getFile() throws IOException {
        File file = File.createTempFile("prefix-", "-suffix");
        MealsService mealsService = new MealsService();
        mealsService.copyResourceToFile("swagger/foodbox.yaml", file);
        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=contract.yaml")
                .build();
    }

    @PostMapping(value = "orders/foodbox", produces = APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> addPersonFoodboxOrder(@Context HttpServletRequest request, FoodboxOrder foodboxOrders) throws InterruptedException {
        Long id = new Date().getTime();
        Long idT = Thread.currentThread().getId();
        logger.info("new id = " + id + " th = " + idT);
        //Контроль безопасности
        if (!mealsService.validateAccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseResult.errorAuth());
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
        boolean val;
        long counter = 1L;
        //Получаем клиента
        MealsPOJO mealsPOJO = mealsService.getClient(contractIdStr);
        if (mealsPOJO.getResponseEntity() != null)
            return mealsPOJO.getResponseEntity();
        Client client = mealsPOJO.getClient();
        Org org = client.getOrg();
        try {
            do {
                FoodBoxOrgReq foodBoxOrgReq = mealsService.getFoodBoxOrgReqNew(org);
                logger.info("start id = " + id + " th = " + idT + " count = " + counter);
                RuntimeContext runtimeContext = RuntimeContext.getInstance();
                Session persistenceSession = null;
                Transaction persistenceTransaction = null;
                try {
                    persistenceSession = runtimeContext.createPersistenceSession();
                    persistenceTransaction = persistenceSession.beginTransaction();

                    //Логика проверки корректности запроса
                    mealsPOJO = mealsService.validateByFormalInfo(xrequestStr, mealsPOJO);
                    if (mealsPOJO.getResponseEntity() != null)
                        return mealsPOJO.getResponseEntity();

                    //Логика всех проверок по клиенту
                    mealsPOJO = mealsService.validateByClientInfo(client, MealsFunctions.CREATE_FOODBOX, mealsPOJO);
                    if (mealsPOJO.getResponseEntity() != null)
                        return mealsPOJO.getResponseEntity();

                    //Логика запросов к бд
                    mealsService.getDataFromDAO(client, MealsFunctions.CREATE_FOODBOX);

                    //Логика обработки самого заказа
                    mealsPOJO = mealsService.mainLogicNewPreorder(persistenceSession, foodboxOrders, client, xrequestStr, mealsPOJO);
                    if (!mealsPOJO.getCreated())
                        return mealsPOJO.getResponseEntity();
                    mealsService.setFoodBoxOrgReqCurr(persistenceSession, foodBoxOrgReq, foodBoxOrgReq.getCurrentversion() + 1);
                    persistenceTransaction.commit();
                    CancelledFoodBoxService.currentFoodBoxPreorders.put(mealsPOJO.getFoodBoxPreorder().getIdFoodBoxPreorder(),
                            mealsPOJO.getFoodBoxPreorder().getCreateDate());
                    val = true;
                    return mealsPOJO.getResponseEntity();
                } catch (Exception e) {
                    val = false;
                    HibernateUtils.rollback(persistenceTransaction, logger);
                } finally {
                    HibernateUtils.close(persistenceSession, logger);
                }
                logger.info("end id = " + id + " th = " + idT + " count = " + counter);
                counter++;
                if (counter > 1000)
                    val = true;
            } while (!val);
            logger.info("Не должны попасть, только есть не более 1000 запросов");
        } catch (Exception e) {
            System.out.println("test");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                body(responseResult.internalError(null, 0));
    }

//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path(value = "orders/foodbox")
//    public Response getPersonFoodboxOrders(@Context HttpServletRequest request) {
//        //Контроль безопасности
//        if (!mealsService.validateAccess()) {
//            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).
//                    entity(responseResult.errorAuth()).build();
//        }
//        String contractIdStr = "";
//        String fromStr = "";
//        String toStr = "";
//        String sortStr = "";
//        Long contract = null;
//        Map<String, String> params = parseParams(request);
//        for (Map.Entry<String, String> currParam : params.entrySet()) {
//            if (currParam.getKey().toLowerCase().equals("from")) {
//                fromStr = currParam.getValue();
//                fromStr = fromStr.replace("%3A", ":");
//                fromStr = fromStr.replace("%20", " ");
//                continue;
//            }
//            if (currParam.getKey().toLowerCase().equals("to")) {
//                toStr = currParam.getValue();
//                toStr = toStr.replace("%3A", ":");
//                toStr = toStr.replace("%20", " ");
//                continue;
//            }
//            if (currParam.getKey().toLowerCase().equals("sort")) {
//                sortStr = currParam.getValue();
//                continue;
//            }
//            if (currParam.getKey().toLowerCase().equals("contractid")) {
//                contractIdStr = currParam.getValue();
//                continue;
//            }
//        }
//        //Логика проверки корректности запроса
//        MealsPOJO mealsPOJO = mealsService.validateByFormalInfoGetFoodbox(contractIdStr, fromStr, toStr, sortStr);
//        if (mealsPOJO.getResponse() != null)
//            return mealsPOJO.getResponse();
//        //Логика всех проверок по клиенту
//        mealsPOJO = mealsService.verifyClient(mealsPOJO.getContractId());
//        if (mealsPOJO.getResponse() != null)
//            return mealsPOJO.getResponse();
//
//        //Логика обработки самого заказа
//       return mealsService.getPreordersForDates(mealsPOJO.getClient(),
//               mealsPOJO.getFrom(), mealsPOJO.getTo(), mealsPOJO.getSortDesc());
//    }
//
//
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path(value = "orders/foodbox/{foodboxOrderId}")
//    public Response getPersonFoodboxOrder(@Context HttpServletRequest request, @PathParam("foodboxOrderId") String foodboxOrderId) {
//        //Контроль безопасности
//        if (!mealsService.validateAccess()) {
//            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).
//                    entity(responseResult.errorAuth()).build();
//        }
//
//        //Логика проверки корректности запроса
//        MealsPOJO mealsPOJO = mealsService.validateByFormalInfoGetFoodbox(foodboxOrderId);
//        if (mealsPOJO.getResponse() != null)
//            return mealsPOJO.getResponse();
//
//        //Логика обработки самого заказа
//        return mealsService.getPreorderById(mealsPOJO.getIsppIdFoodbox());
//    }
//
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path(value = "menu/buffet")
//    @Transactional
//    public Response getPersonBuffetMenu(@Context HttpServletRequest request) {
//        //Контроль безопасности
//        if (!mealsService.validateAccess()) {
//            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).
//                    entity(responseResult.errorAuth()).build();
//        }
//
//        String contractIdStr = "";
//        String onDateStr = "";
//
//        Map<String, String> params = parseParams(request);
//        for (Map.Entry<String, String> currParam : params.entrySet()) {
//            if (currParam.getKey().toLowerCase().equals("contractid")) {
//                contractIdStr = currParam.getValue();
//                continue;
//            }
//            if (currParam.getKey().toLowerCase().equals("ondate")) {
//                {
//                    onDateStr = currParam.getValue();
//                    onDateStr = onDateStr.replace("%3A", ":");
//                    onDateStr = onDateStr.replace("%20", " ");
//                    continue;
//                }
//            }
//        }
//
//        //Логика проверки корректности запроса
//        MealsPOJO mealsPOJO = mealsService.validateByFormalInfoForBuffet4(onDateStr, contractIdStr);
//        if (mealsPOJO.getResponse() != null)
//            return mealsPOJO.getResponse();
//
//        //Логика всех проверок по клиенту
//        mealsPOJO = mealsService.validateByClientInfo(mealsPOJO.getContractId(), MealsFunctions.GET_BUFET);
//        if (mealsPOJO.getResponse() != null)
//            return mealsPOJO.getResponse();
//
//        //Логика запросов к бд
//        mealsService.getDataFromDAO(mealsPOJO.getClient(), MealsFunctions.GET_BUFET);
//
//        //Логика получения самого меню
//        return mealsService.getBuffetInfo();
//    }
//
//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path(value = "clients/foodboxAllowed")
//    public Response setPersonFoodboxAllowed(@Context HttpServletRequest request) {
//        Result result = new Result();
//        //Контроль безопасности
//        if (!mealsService.validateAccess()) {
//            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).
//                    entity(responseResult.errorAuth()).build();
//        }
//
//        String contractIdStr = "";
//        String foodBoxAvailableStr = "";
//        Map<String, String> params = parseParams(request);
//        for (Map.Entry<String, String> currParam : params.entrySet()) {
//            if (currParam.getKey().toLowerCase().equals("contractid")) {
//                contractIdStr = currParam.getValue();
//                continue;
//            }
//            if (currParam.getKey().toLowerCase().equals("foodboxallowed")) {
//                foodBoxAvailableStr = currParam.getValue();
//                continue;
//            }
//        }
//
//        //Логика проверки корректности запроса
//        MealsPOJO mealsPOJO = mealsService.validateByFormalInfoClientAllowed(contractIdStr, foodBoxAvailableStr, MealsFunctions.SET_FOODBOX_ALLOWED);
//        if (mealsPOJO.getResponse() != null)
//            return mealsPOJO.getResponse();
//        Boolean foodAv = mealsPOJO.getFoodBoxAvailable();
//        //Логика всех проверок по клиенту
//        mealsPOJO = mealsService.validateByClientAllowed(mealsPOJO.getContractId());
//        if (mealsPOJO.getResponse() != null)
//            return mealsPOJO.getResponse();
//
//        //Логика установки флага
//        return mealsService.setFoodboxAllowed(mealsPOJO.getClient(), foodAv);
//    }
//
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path(value = "clients")
//    public Response getClientData(@Context HttpServletRequest request) {
//        Result result = new Result();
//        //Контроль безопасности
//        if (!mealsService.validateAccess()) {
//            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).
//                    entity(responseResult.errorAuth()).build();
//        }
//        String contractIdStr = "";
//        Map<String, String> params = parseParams(request);
//        for (Map.Entry<String, String> currParam : params.entrySet()) {
//            if (currParam.getKey().toLowerCase().equals("contractid")) {
//                contractIdStr = currParam.getValue();
//                continue;
//            }
//        }
//        //Логика проверки корректности запроса
//        MealsPOJO mealsPOJO = mealsService.validateByFormalInfoClientAllowed(contractIdStr, null, MealsFunctions.GET_FOODBOX_ALLOWED);
//        if (mealsPOJO.getResponse() != null)
//            return mealsPOJO.getResponse();
//
//        //Логика всех проверок по клиенту
//        mealsPOJO = mealsService.validateByClientAllowed(mealsPOJO.getContractId());
//        if (mealsPOJO.getResponse() != null)
//            return mealsPOJO.getResponse();
//
//        //Логика получения флага
//        return mealsService.getFoodBoxAllowed(mealsPOJO.getClient(), mealsPOJO.getFoodBoxAvailable());
//    }

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
