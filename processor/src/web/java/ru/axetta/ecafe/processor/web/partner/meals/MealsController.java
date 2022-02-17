/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.meals;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxPreorder;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxPreorderDish;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxStateTypeEnum;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.library.ResponseCodes;
import ru.axetta.ecafe.processor.web.partner.library.Result;
import ru.axetta.ecafe.processor.web.partner.meals.models.FoodboxOrder;
import ru.axetta.ecafe.processor.web.partner.meals.models.FoodboxOrderInfo;
import ru.axetta.ecafe.processor.web.partner.meals.models.HistoryFoodboxOrderInfo;
import ru.axetta.ecafe.processor.web.partner.meals.models.OrderDish;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Path(value = "")
@Controller
@ApplicationPath("/ispp/meals/")
public class MealsController extends Application {

    private Logger logger = LoggerFactory.getLogger(MealsController.class);
    public static final String KEY_FOR_MEALS = "ecafe.processor.meals.key";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "orders/foodbox")
    public Response createNewFoodBoxPreorder(@Context HttpServletRequest request, FoodboxOrder foodboxOrder) {
        Result result = new Result();
        String contractIdStr = "";
        String securityKey = "";
        Map<String, String> params = parseParams(request);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            if (currParam.getKey().equals("contractId"))
                contractIdStr = currParam.getValue();
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                if (header.toLowerCase().equals("x-api-key"))
                {
                    securityKey = request.getHeader(header);
                    break;
                }
            }
        }
        //Контроль безопасности
        if (!validateAccess(securityKey)) {
            logger.error("Неверный ключ доступа");
            result.setErrorCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_KEY.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        if (contractIdStr.isEmpty()) {
            logger.error("Отсутствует contractId");
            result.setErrorCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        Long contractId;
        try {
            contractId = Long.parseLong(contractIdStr);
        } catch (Exception e)
        {
            logger.error(String.format("Неверный формат contractId %s", contractIdStr), e);
            result.setErrorCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        Client client = daoReadonlyService.getClientByContractId(contractId);
        if (client == null) {
            logger.error("Клиент не найден");
            result.setErrorCode(ResponseCodes.RC_NOT_FOUND_CLIENT.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_NOT_FOUND_CLIENT.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        Boolean errorOrg = false;
        try {
            if (!client.getOrg().getUsedFoodbox())
            {
                errorOrg = true;
            }
        } catch (Exception e) {
            errorOrg = true;
        }
        if (errorOrg)
        {
            logger.error("У организации не включен функционал фудбокса");
            result.setErrorCode(ResponseCodes.RC_NOT_FOUND_ORG.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_NOT_FOUND_ORG.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            FoodBoxPreorder foodBoxPreorder = new FoodBoxPreorder();
            foodBoxPreorder.setVersion(daoReadonlyService.getMaxVersionOfFoodBoxPreorder() + 1);
            foodBoxPreorder.setClient(client);
            foodBoxPreorder.setState(FoodBoxStateTypeEnum.NEW);
            foodBoxPreorder.setOrg(client.getOrg());
            foodBoxPreorder.setInitialDateTime(foodboxOrder.getCreatedAt());
            foodBoxPreorder.setCreateDate(new Date());
            foodBoxPreorder.setIdFoodBoxExternal(foodboxOrder.getId());
            foodBoxPreorder.setOrderPrice(foodboxOrder.getOrderPrice());
            persistenceSession.persist(foodBoxPreorder);
            for (OrderDish orderDish : foodboxOrder.getDishes()) {
                FoodBoxPreorderDish foodBoxPreorderDish = new FoodBoxPreorderDish();
                foodBoxPreorderDish.setFoodBoxPreorder(foodBoxPreorder);
                foodBoxPreorderDish.setIdOfDish(orderDish.getDishId());
                foodBoxPreorderDish.setPrice(orderDish.getPrice().intValue());
                foodBoxPreorderDish.setQty(orderDish.getAmount());
                foodBoxPreorderDish.setName(orderDish.getName());
                foodBoxPreorderDish.setBuffetCategoriesId(orderDish.getBuffetCategoriesId());
                foodBoxPreorderDish.setBuffetCategoriesName(orderDish.getBuffetCategoriesName());
                foodBoxPreorderDish.setCreateDate(new Date());
                persistenceSession.persist(foodBoxPreorderDish);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Ошибка при сохранении заказа для Фудбокса", e);
            result.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        result.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
        result.setErrorMessage(ResponseCodes.RC_OK.toString());
        return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "orders/foodbox")
    public Response getInfoFoodBoxPreorder(@Context HttpServletRequest request) {
        Result result = new Result();
        String foodboxOrderNumberStr = "";
        Long foodboxOrderNumber = null;
        String contractIdStr = "";
        String fromStr = "";
        String toStr = "";
        String sortStr = "";
        Long contract = null;
        Date from = null;
        Date to = null;
        Boolean sortDesc = true;
        Map<String, String> params = parseParams(request);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            if (currParam.getKey().equals("from"))
                fromStr = currParam.getValue();
            if (currParam.getKey().equals("to"))
                toStr = currParam.getValue();
            if (currParam.getKey().equals("sort"))
                sortStr = currParam.getValue();
            if (currParam.getKey().equals("foodboxOrderNumber"))
                foodboxOrderNumberStr = currParam.getValue();
            if (currParam.getKey().equals("contractId"))
                contractIdStr = currParam.getValue();
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        String securityKey = "";
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                if (header.toLowerCase().equals("X-Api-Key"))
                {
                    securityKey = request.getHeader(header);
                    break;
                }
            }
        }
        //Контроль безопасности
        if (!validateAccess(securityKey)) {
            logger.error("Неверный ключ доступа");
            result.setErrorCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_KEY.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        try {
            if (!foodboxOrderNumberStr.isEmpty()) {
                foodboxOrderNumber = Long.parseLong(foodboxOrderNumberStr);
            }
        } catch (Exception e) {}
        try {
            if (!contractIdStr.isEmpty()) {
                contract = Long.parseLong(contractIdStr);
            }
        } catch (Exception e) {}
        try {
            if (!fromStr.isEmpty()) {
                from = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(fromStr);
            }
        } catch (Exception e) {}
        try {
            if (!toStr.isEmpty()) {
                to = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(toStr);
            }
        } catch (Exception e) {}
        try {
            if (!sortStr.isEmpty()) {
                if (sortStr.equals("asc"))
                    sortDesc = false;
            }
        } catch (Exception e) {}
        Integer typeWork = 0;
        if (foodboxOrderNumber != null)
            typeWork = 1;
        else
        {
            if (from == null || to == null || contract != null)
                typeWork = 2;
        }
        if (typeWork == 0)
        {
            logger.error("Не определен тип запроса ввиду отсутсвия корректных параметров");
            result.setErrorCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        Client client = null;
        if (typeWork == 2) {
            client = daoReadonlyService.getClientByContractId(contract);
            if (client == null) {
                logger.error("Клиент не найден");
                result.setErrorCode(ResponseCodes.RC_NOT_FOUND_CLIENT.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_NOT_FOUND_CLIENT.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
            }
        }
        HistoryFoodboxOrderInfo historyFoodboxOrderInfo = new HistoryFoodboxOrderInfo();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (typeWork == 1) {
                FoodBoxPreorder foodBoxPreorder = daoReadonlyService.getFoodBoxPreorderByExternalId(foodboxOrderNumber);
                FoodboxOrderInfo foodboxOrderInfo = convertData(foodBoxPreorder);
                historyFoodboxOrderInfo.getOrdersInfo().add(foodboxOrderInfo);
                historyFoodboxOrderInfo.setOrdersAmount(foodboxOrderInfo.getOrderPrice());
                historyFoodboxOrderInfo.setFoodboxAvailability(true);
                try {
                    historyFoodboxOrderInfo.setFoodboxAvailabilityForEO(foodBoxPreorder.getClient().getOrg().getUsedFoodbox());
                } catch (Exception e) {
                    historyFoodboxOrderInfo.setFoodboxAvailabilityForEO(false);
                }
                return Response.status(HttpURLConnection.HTTP_OK).entity(historyFoodboxOrderInfo).build();
            }
            if (typeWork == 2)
            {
                List<FoodBoxPreorder> foodBoxPreorders = daoReadonlyService.getFoodBoxPreorderByForClient(client, from, to);
                List<FoodboxOrderInfo> foodboxOrderInfos = new ArrayList<>();
                Long sum = 0L;
                for (FoodBoxPreorder foodBoxPreorder: foodBoxPreorders)
                {
                    FoodboxOrderInfo foodboxOrderInfo = convertData(foodBoxPreorder);
                    sum += foodboxOrderInfo.getOrderPrice();
                    foodboxOrderInfos.add(foodboxOrderInfo);
                }
                if (sortDesc)
                    Collections.sort(foodboxOrderInfos);
                else
                    Collections.reverse(foodboxOrderInfos);

                historyFoodboxOrderInfo.getOrdersInfo().addAll(foodboxOrderInfos);
                historyFoodboxOrderInfo.setOrdersAmount(sum);
                historyFoodboxOrderInfo.setFoodboxAvailability(true);
                try {
                    historyFoodboxOrderInfo.setFoodboxAvailabilityForEO(client.getOrg().getUsedFoodbox());
                } catch (Exception e) {
                    historyFoodboxOrderInfo.setFoodboxAvailabilityForEO(false);
                }
                return Response.status(HttpURLConnection.HTTP_OK).entity(historyFoodboxOrderInfo).build();
            }
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Ошибка при получении заказа для Фудбокса", e);
            result.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    private FoodboxOrderInfo convertData (FoodBoxPreorder foodBoxPreorder)
    {
        FoodboxOrderInfo foodboxOrderInfo = new FoodboxOrderInfo();
        foodboxOrderInfo.setExpiresAt(new Date(foodBoxPreorder.getCreateDate().getTime() + 7200000));
        foodboxOrderInfo.setFoodboxOrderNumber(foodBoxPreorder.getIdOfFoodBox());
        foodboxOrderInfo.setStatus(foodBoxPreorder.getState().getDescription());
        foodboxOrderInfo.setTimeOrder(foodBoxPreorder.getCreateDate());
        foodboxOrderInfo.setId(foodBoxPreorder.getIdFoodBoxExternal());
        Long sum = 0L;
        for (FoodBoxPreorderDish foodBoxPreorderDish : DAOReadonlyService.getInstance().getFoodBoxPreordersDishes(foodBoxPreorder)) {
            OrderDish orderDish = new OrderDish();
            orderDish.setAmount(foodBoxPreorderDish.getQty());
            orderDish.setDishId(foodBoxPreorderDish.getIdOfDish());
            orderDish.setPrice(foodBoxPreorderDish.getPrice().longValue());
            orderDish.setName(foodBoxPreorderDish.getName());
            orderDish.setBuffetCategoriesId(foodBoxPreorderDish.getBuffetCategoriesId());
            orderDish.setBuffetCategoriesName(foodBoxPreorderDish.getBuffetCategoriesName());
            sum += foodBoxPreorderDish.getPrice();
            foodboxOrderInfo.getDishes().add(orderDish);
        }
        foodboxOrderInfo.setOrderPrice(sum);
        return foodboxOrderInfo;
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

    private boolean validateAccess(String key) {
        String keyinternal = RuntimeContext.getInstance().getConfigProperties().getProperty(KEY_FOR_MEALS, "");
        if (!key.isEmpty() && key.equals(keyinternal))
            return true;
        return false;
    }
}
