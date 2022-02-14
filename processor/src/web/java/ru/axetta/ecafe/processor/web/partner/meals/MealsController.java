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
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxDishRemain.FoodBoxDishRemain;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.library.ResponseCodes;
import ru.axetta.ecafe.processor.web.partner.library.Result;
import ru.axetta.ecafe.processor.web.partner.meals.models.FoodboxOrder;
import ru.axetta.ecafe.processor.web.partner.meals.models.FoodboxOrderInfo;
import ru.axetta.ecafe.processor.web.partner.meals.models.OrderDish;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Path(value = "")
@Controller
@ApplicationPath("/ispp/meals/")
public class MealsController extends Application {

    private Logger logger = LoggerFactory.getLogger(MealsController.class);
    public static final String KEY_FOR_LIBRARY = "ecafe.processor.meals.key";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "orders/foodbox")
    public Response createNewFoodBoxPreorder(@Context HttpServletRequest request, FoodboxOrder foodboxOrder) {
        Result result = new Result();
        String meshGuid = "";
        Map<String, String> params = parseParams(request);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            if (currParam.getKey().equals("personId"))
                meshGuid = currParam.getValue();
        }
        if (meshGuid.isEmpty()) {
            logger.error("Отсутствует personId");
            result.setErrorCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        Client client = daoReadonlyService.getClientByMeshGuid(meshGuid);
        if (client == null) {
            logger.error("Клиент не найден");
            result.setErrorCode(ResponseCodes.RC_NOT_FOUND_CLIENT.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_NOT_FOUND_CLIENT.toString());
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
            persistenceSession.persist(foodBoxPreorder);
            for (OrderDish orderDish : foodboxOrder.getDishes()) {
                FoodBoxPreorderDish foodBoxPreorderDish = new FoodBoxPreorderDish();
                foodBoxPreorderDish.setFoodBoxPreorder(foodBoxPreorder);
                foodBoxPreorderDish.setIdOfDish(orderDish.getDishId());
                foodBoxPreorderDish.setCreateDate(new Date());
                foodBoxPreorderDish.setPrice(orderDish.getPrice().intValue());
                foodBoxPreorderDish.setQty(orderDish.getAmount());
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
        FoodboxOrderInfo foodboxOrderInfo = new FoodboxOrderInfo();
        Long foodboxOrderNumber = null;
        String meshGuid = "";
        Map<String, String> params = parseParams(request);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            if (currParam.getKey().equals("foodboxOrderNumber"))
                foodboxOrderNumber = Long.parseLong(currParam.getValue());
            if (currParam.getKey().equals("clientId"))
                meshGuid = currParam.getValue();
        }
        if (meshGuid.isEmpty()) {
            logger.error("Отсутствует personId");
            result.setErrorCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        Client client = daoReadonlyService.getClientByMeshGuid(meshGuid);
        if (client == null) {
            logger.error("Клиент не найден");
            result.setErrorCode(ResponseCodes.RC_NOT_FOUND_CLIENT.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_NOT_FOUND_CLIENT.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            FoodBoxPreorder foodBoxPreorder = daoReadonlyService.getFoodBoxPreorderByExternalId(foodboxOrderNumber);
            foodboxOrderInfo.setExpiresAt(new Date(foodBoxPreorder.getCreateDate().getTime() + 3600000));
            foodboxOrderInfo.setFoodboxOrderNumber(foodBoxPreorder.getIdOfFoodBox());
            foodboxOrderInfo.setStatus(foodBoxPreorder.getState().getDescription());
            foodboxOrderInfo.setTimeOrder(foodBoxPreorder.getCreateDate());
            foodboxOrderInfo.setId(foodBoxPreorder.getIdFoodBoxExternal());
            Long sum = 0L;
            for (FoodBoxPreorderDish foodBoxPreorderDish: DAOReadonlyService.getInstance().getFoodBoxPreordersDishes(foodBoxPreorder))
            {
                OrderDish orderDish = new OrderDish();
                orderDish.setAmount(foodBoxPreorderDish.getQty());
                orderDish.setDishId(foodBoxPreorderDish.getIdOfDish());
                orderDish.setPrice(foodBoxPreorderDish.getPrice().longValue());
                sum += foodBoxPreorderDish.getPrice();
                foodboxOrderInfo.getDishes().add(orderDish);
            }

            foodboxOrderInfo.setOrderPrice(sum);
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
//        result.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
//        result.setErrorMessage(ResponseCodes.RC_OK.toString());
        return Response.status(HttpURLConnection.HTTP_OK).entity(foodboxOrderInfo).build();
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
