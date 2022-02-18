/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.meals;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxPreorder;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxPreorderDish;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxStateTypeEnum;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtCategory;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtCategoryItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDish;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.meals.ResponseCodes;
import ru.axetta.ecafe.processor.web.partner.meals.Result;
import ru.axetta.ecafe.processor.web.partner.meals.models.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

@Path(value = "")
@Controller
@ApplicationPath("/ispp/meals/")
public class MealsController extends Application {

    private Logger logger = LoggerFactory.getLogger(MealsController.class);
    public static final String KEY_FOR_MEALS = "ecafe.processor.meals.key";
    public static final String BUFFET_OPEN_TIME = "ecafe.processor.meals.buffetOpenTime";
    public static final String BUFFET_CLOSE_TIME = "ecafe.processor.meals.buffetCloseTime";

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
            if (currParam.getKey().equals("from")) {
                fromStr = currParam.getValue();
                fromStr = fromStr.replace("%3A", ":");
                fromStr = fromStr.replace("%20", " ");
            }
            if (currParam.getKey().equals("to")) {
                toStr = currParam.getValue();
                toStr = toStr.replace("%3A", ":");
                toStr = toStr.replace("%20", " ");
            }
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
                from = new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss")).parse(fromStr);
            }
        } catch (Exception e) {}
        try {
            if (!toStr.isEmpty()) {
                to = new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss")).parse(toStr);
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
        try {
            if (typeWork == 1) {
                FoodBoxPreorder foodBoxPreorder = daoReadonlyService.getFoodBoxPreorderByExternalId(foodboxOrderNumber);
                if (foodBoxPreorder == null)
                {
                    logger.error(String.format("Не найден заказ с externalid %s", foodboxOrderNumber));
                    result.setErrorCode(ResponseCodes.RC_NOT_FOUND_FOODBOX.getCode().toString());
                    result.setErrorMessage(ResponseCodes.RC_NOT_FOUND_FOODBOX.toString());
                    return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
                }
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
        } catch (Exception e) {
            logger.error("Ошибка при получении заказа для Фудбокса", e);
            result.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        return null;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "menu/buffet")
    @Transactional
    public Response getInfoFoodBoxMenu(@Context HttpServletRequest request) {
        Result result = new Result();
        String contractIdStr = "";
        String onDateStr = "";
        Date onDate = null;
        Map<String, String> params = parseParams(request);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            if (currParam.getKey().equals("contractId"))
                contractIdStr = currParam.getValue();
            if (currParam.getKey().equals("onDate")) {
                onDateStr = currParam.getValue();
                onDateStr = onDateStr.replace("%3A", ":");
                onDateStr = onDateStr.replace("%20", " ");
            }
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        String securityKey = "";
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
        try {
            if (!onDateStr.isEmpty()) {
                onDate = new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss")).parse(onDateStr);
            }
        } catch (Exception e) {
            logger.error("Неверный формат onDate");
            result.setErrorCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_REQUST.toString());
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
        //Собираем данные для орг
        List<WtDish> wtDishes = daoReadonlyService.getWtDishesByOrgandDate(client.getOrg(), onDate);
        //Тут будет фильтр по остаткам

        //
        //Расскидываем по классам
        PersonBuffetMenu personBuffetMenu = new PersonBuffetMenu();
        personBuffetMenu.setBuffetIsOpen(true);
        personBuffetMenu.setBuffetOpenTime(getBuffetOpenTime());
        personBuffetMenu.buffetCloseTime(getBuffetCloseTime());
        if (!wtDishes.isEmpty())
            personBuffetMenu.setDishesAmount((long) wtDishes.size());
        for (WtDish wtDish: wtDishes)
        {
            //Находим все подкатегории
            List<WtCategoryItem> wtCategoryItemList =  daoReadonlyService.getCategoryItemsByWtDish(wtDish.getIdOfDish());
            for (WtCategoryItem wtCategoryItem: wtCategoryItemList)
            {
                WtCategory wtCategory = wtCategoryItem.getWtCategory();

                Dish dish = new Dish();
                ///
                dish.setId(wtDish.getIdOfDish());
                dish.setCode(wtDish.getBarcode());
                dish.setName(wtDish.getDishName());
                dish.setPrice(wtDish.getPrice().longValue());
                dish.setIngredients(wtDish.getComponentsOfDish());
                dish.setCalories(wtDish.getCalories());
                dish.setWeight(wtDish.getQty());
                dish.setProtein(wtDish.getProtein());
                dish.setFat(wtDish.getFat());
                dish.setCarbohydrates(wtDish.getCarbohydrates());
                ///
                PersonBuffetMenuBuffetCategoriesItem buffetMenuBuffetCategoriesItem = null;
                for (PersonBuffetMenuBuffetCategoriesItem p : personBuffetMenu.getBuffetCategoriesItem())
                {
                    if (p.getId().equals(wtCategory.getIdOfCategory()))
                    {
                        buffetMenuBuffetCategoriesItem = p;
                        break;
                    }
                }
                if (buffetMenuBuffetCategoriesItem == null)
                {
                    buffetMenuBuffetCategoriesItem = new PersonBuffetMenuBuffetCategoriesItem();
                    buffetMenuBuffetCategoriesItem.setId(wtCategory.getIdOfCategory());
                    buffetMenuBuffetCategoriesItem.setName(wtCategory.getDescription());
                    personBuffetMenu.getBuffetCategoriesItem().add(buffetMenuBuffetCategoriesItem);
                }
                ////
                PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem buffetSubcategoriesItem = null;
                for (PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem g : buffetMenuBuffetCategoriesItem.getBuffetSubcategoriesItem())
                {
                    if (g.getId().equals(wtCategoryItem.getIdOfCategoryItem()))
                    {
                        buffetSubcategoriesItem = g;
                        break;
                    }
                }
                if (buffetSubcategoriesItem == null)
                {
                    buffetSubcategoriesItem = new PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem();
                    buffetSubcategoriesItem.setId(wtCategoryItem.getIdOfCategoryItem());
                    buffetSubcategoriesItem.setName(wtCategoryItem.getDescription());
                    buffetMenuBuffetCategoriesItem.getBuffetSubcategoriesItem().add(buffetSubcategoriesItem);
                }
                buffetSubcategoriesItem.getMenuDishesItem().add(dish);
            }
        }
        return Response.status(HttpURLConnection.HTTP_OK).entity(personBuffetMenu).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "foodbox/info")
    public Response setFoodboxAvailability(@Context HttpServletRequest request) {
        Result result = new Result();
        String contractIdStr = "";
        String securityKey = "";
        String foodBoxAvailableStr = "true";
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                if (header.toLowerCase().equals("x-api-key"))
                {
                    securityKey = request.getHeader(header);
                }
                if (header.toLowerCase().equals("contractid"))
                {
                    contractIdStr = request.getHeader(header);
                }
                if (header.toLowerCase().equals("foodboxavailability"))
                {
                    foodBoxAvailableStr = request.getHeader(header);
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
//        Boolean errorOrg = false;
//        try {
//            if (!client.getOrg().getUsedFoodbox())
//            {
//                errorOrg = true;
//            }
//        } catch (Exception e) {
//            errorOrg = true;
//        }
//        if (errorOrg)
//        {
//            logger.error("У организации не включен функционал фудбокса");
//            result.setErrorCode(ResponseCodes.RC_NOT_FOUND_ORG.getCode().toString());
//            result.setErrorMessage(ResponseCodes.RC_NOT_FOUND_ORG.toString());
//            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
//        }
        Boolean foodBoxAvailable = true;
        try {
            foodBoxAvailable = Boolean.parseBoolean(foodBoxAvailableStr);
        } catch (Exception e) {
            logger.error("Неверный формат foodBoxAvailable");
            result.setErrorCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            client.setFoodboxAvailability(foodBoxAvailable);
            persistenceSession.merge(client);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Ошибка при сохранении флага для клиента", e);
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
    @Path(value = "foodbox/info")
    @Transactional
    public Response getFoodboxAvailability(@Context HttpServletRequest request) {
        Result result = new Result();
        String contractIdStr = "";
        String securityKey = "";
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                if (header.toLowerCase().equals("x-api-key"))
                {
                    securityKey = request.getHeader(header);
                }
                if (header.toLowerCase().equals("contractid"))
                {
                    contractIdStr = request.getHeader(header);
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
        GetFoodboxInfo getFoodboxInfo = new GetFoodboxInfo();
        getFoodboxInfo.setFoodboxAvailability(client.getFoodboxAvailability());
        getFoodboxInfo.setFoodboxAvailabilityForEO(client.getOrg().getUsedFoodbox());
        return Response.status(HttpURLConnection.HTTP_OK).entity(getFoodboxInfo).build();
    }


    private FoodboxOrderInfo convertData (FoodBoxPreorder foodBoxPreorder)
    {
        FoodboxOrderInfo foodboxOrderInfo = new FoodboxOrderInfo();
        foodboxOrderInfo.setExpiresAt(new Date(foodBoxPreorder.getCreateDate().getTime() + 7200000));
        foodboxOrderInfo.setFoodboxOrderNumber(foodBoxPreorder.getIdOfFoodBox());
        if(foodBoxPreorder.getState() != null) {
            foodboxOrderInfo.setStatus(foodBoxPreorder.getState().getDescription());
        }
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

    private String getBuffetOpenTime() {
        String openTime = RuntimeContext.getInstance().getConfigProperties().getProperty(BUFFET_OPEN_TIME, "");
        return openTime;
    }

    private String getBuffetCloseTime() {
        String closeTime = RuntimeContext.getInstance().getConfigProperties().getProperty(BUFFET_CLOSE_TIME, "");
        return closeTime;
    }

    private boolean validateAccess(String key) {
        String keyinternal = RuntimeContext.getInstance().getConfigProperties().getProperty(KEY_FOR_MEALS, "");
        if (!key.isEmpty() && key.equals(keyinternal))
            return true;
        return false;
    }
}
