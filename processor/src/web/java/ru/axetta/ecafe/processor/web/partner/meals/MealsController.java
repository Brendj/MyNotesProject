/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.meals;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.foodbox.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtCategory;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtCategoryItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDish;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.meals.models.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Path(value = "")
@Controller
@ApplicationPath("/ispp/meals/")
public class MealsController extends Application {

    private Logger logger = LoggerFactory.getLogger(MealsController.class);
    public static final String BUFFET_OPEN_TIME = "ecafe.processor.meals.buffetOpenTime";
    public static final String BUFFET_CLOSE_TIME = "ecafe.processor.meals.buffetCloseTime";
    public static final Integer MAX_COUNT_DISH = 5;
    public static final int HTTP_UNPROCESSABLE_ENTITY = 422;

    public static final Integer MAX_COUNT = 40;//Максимальное количество блюд для возвращения в методе
    protected static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat((DATE_FORMAT));

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "orders/foodbox")
    @Transactional
    public Response createNewFoodBoxPreorder(@Context HttpServletRequest request, FoodboxOrder foodboxOrder) {
        Result result = new Result();
        String contractIdStr = "";
        Map<String, String> params = parseParams(request);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            if (currParam.getKey().toLowerCase().equals("contractid")) {
                contractIdStr = currParam.getValue();
                break;
            }
        }
        //Контроль безопасности
        if (!validateAccess()) {
            logger.error("Не удалось авторизовать пользователя");
            result.setCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_KEY.toString());
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).entity(result).build();
        }
        Long createTime = new Date().getTime() - CalendarUtils.startOfDay(new Date()).getTime();
        DateFormat format = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        try {
            if (createTime < (CalendarUtils.convertdateInLocal(format.parse(getBuffetOpenTime())).getTime()) ||
                    createTime > (CalendarUtils.convertdateInLocal(format.parse(getBuffetCloseTime())).getTime())) {
                logger.error("Заказ пришел на время закрытия буфета");
                OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
                orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_TIME.getCode());
                orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_TIME.toString());
                orderErrorInfo.setBuffetOpenAt(getBuffetOpenTime());
                orderErrorInfo.setBuffetCloseAt(getBuffetCloseTime());
                return Response.status(HTTP_UNPROCESSABLE_ENTITY).entity(orderErrorInfo).build();
            }
        } catch (Exception e) {
        }
        ;


        if (contractIdStr.isEmpty()) {
            logger.error("Отсутствует contractId");
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        Long contractId;
        try {
            contractId = Long.parseLong(contractIdStr);
        } catch (Exception e) {
            logger.error(String.format("Неверный формат contractId %s", contractIdStr), e);
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
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
        if (xrequestStr.isEmpty()) {
            logger.error("Отсутствует x-request-id");
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        Client client = daoReadonlyService.getClientByContractId(contractId);
        if (client == null) {
            logger.error("Клиент не найден");
            result.setCode(ResponseCodes.RC_NOT_FOUND_CLIENT.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_CLIENT.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        Boolean errorOrg = false;
        try {
            if (!client.getOrg().getUsedFoodbox()) {
                errorOrg = true;
            }
        } catch (Exception e) {
            errorOrg = true;
        }
        if (errorOrg) {
            logger.error("У организации не включен функционал фудбокса");
            result.setCode(ResponseCodes.RC_NOT_FOUND_ORG.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_ORG.toString());
            return Response.status(HttpURLConnection.HTTP_FORBIDDEN).entity(result).build();
        }
        if (!client.getFoodboxAvailability()) {
            logger.error("У клиента не включен функционал фудбокса");
            result.setCode(ResponseCodes.RC_NOT_FOUND_AVAILABLE_CLIENT.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_AVAILABLE_CLIENT.toString());
            return Response.status(HttpURLConnection.HTTP_FORBIDDEN).entity(result).build();
        }
        FoodBoxPreorder foodBoxPreorderDB = daoReadonlyService.getFoodBoxPreorderByExternalId(xrequestStr);
        if (foodBoxPreorderDB != null) {
            logger.error(String.format("Заказ с данным идентификатором уже зарегистрирвоан в системе. externalid = %s", xrequestStr));
            result.setCode(ResponseCodes.RC_FOUND_FOODBOX.getCode().toString());
            result.setDescription(ResponseCodes.RC_FOUND_FOODBOX.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        List<FoodBoxPreorder> foodBoxPreorders = daoReadonlyService.getActiveFoodBoxPreorderForClient(client);
        if (foodBoxPreorders != null && !foodBoxPreorders.isEmpty()) {
            logger.error("У клиента имеются необработанные заказы");
            OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
            orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_HAVE_PREORDER.getCode());
            orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_HAVE_PREORDER.toString());
            orderErrorInfo.setFoodboxOrderId(foodBoxPreorders.get(0).getIdOfOrder());
            return Response.status(HTTP_UNPROCESSABLE_ENTITY).entity(orderErrorInfo).build();
        }
        Long availableMoney = 0L;
        if (client.getExpenditureLimit() != null && client.getExpenditureLimit() != 0) {
            //Получаем истраченную сумму по кассе
            Long usedSuminDay = daoReadonlyService.getSumForOrdersbyClientOnPeriod(client.getIdOfClient(), CalendarUtils.startOfDay(new Date()), CalendarUtils.endOfDay(new Date()));
            Long usedSuminDayFoodBox = daoReadonlyService.getUsedMoneyFoodBoxPreorderForClient(client, CalendarUtils.startOfDay(new Date()), CalendarUtils.endOfDay(new Date()));
            availableMoney = client.getExpenditureLimit() - usedSuminDay - usedSuminDayFoodBox;
            if (availableMoney < 0) {
                logger.error("Сумма заказа превышает дневной лимит трат");
                OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
                orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_LIMIT.getCode());
                orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_LIMIT.toString());
                orderErrorInfo.setBalanceLimit(client.getExpenditureLimit());
                return Response.status(HTTP_UNPROCESSABLE_ENTITY).entity(orderErrorInfo).build();
            }
        }
        Set<FoodBoxCells> foodBoxCells = daoReadonlyService.getFoodBoxCellsByOrg(client.getOrg());
        Integer countunlocketed = daoReadonlyService.getFoodBoxPreordersUnallocated(client.getOrg());
        Integer countFree = 0;
        for (FoodBoxCells foodBoxCells1 : foodBoxCells) {
            //Считаем количество свободных ячеек в фудбоксах
            countFree += (foodBoxCells1.getTotalcellscount() - foodBoxCells1.getBusycells());
        }
        CurrentFoodboxOrderInfo currentFoodboxOrderInfo = new CurrentFoodboxOrderInfo();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        //Собираем данные для орг
        List<WtDish> wtDishes = daoReadonlyService.getWtDishesByOrgandDate(client.getOrg(), new Date());
        //Получаем количество доступных блюд для орг
        List <FoodBoxPreorderAvailable> foodBoxPreorderAvailables = daoReadonlyService.getFoodBoxPreorderAvailable(client.getOrg());
        //Получаем список активных заказов футбокса
        Map<Long,Integer> orders = daoReadonlyService.getDishesCountActiveFoodBoxPreorderForOrg(client.getOrg());
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
            foodBoxPreorder.setCreateDate(new Date());
            foodBoxPreorder.setIdFoodBoxExternal(xrequestStr);
            foodBoxPreorder.setPosted(0);
            persistenceSession.persist(foodBoxPreorder);
            currentFoodboxOrderInfo.setFoodboxOrderId(foodBoxPreorder.getIdFoodBoxPreorder());
            currentFoodboxOrderInfo.setStatus(FoodBoxStateTypeEnum.NEW.getDescription());
            currentFoodboxOrderInfo.setExpiredAt(simpleDateFormat.format(new Date(new Date().getTime() + 3600000)) + "Z");
            currentFoodboxOrderInfo.setCreatedAt(simpleDateFormat.format(new Date()) + "Z");
            currentFoodboxOrderInfo.setBalance(client.getBalance());
            currentFoodboxOrderInfo.setBalanceLimit(client.getExpenditureLimit());
            Long priceAll = 0L;
            boolean havegoodDish = false;
            Integer countDish = 0;
            for (OrderDish orderDish : foodboxOrder.getDishes()) {
                try {
                    if (!wtDishes.contains(daoReadonlyService.getWtDishById(orderDish.getDishId()))) {
                        continue;
                    }
                    Integer countAvailableinOrg = 0;
                    for (FoodBoxPreorderAvailable foodBoxPreorderAvailable: foodBoxPreorderAvailables)
                    {
                        if (foodBoxPreorderAvailable.getIdOfDish().equals(orderDish.getDishId())) {
                            countAvailableinOrg = foodBoxPreorderAvailable.getAvailableQty();
                            break;
                        }
                    }
                    //Получаем список заказов, которые могут использовать данный заказ
                    Integer correntValinOrders = orders.get(orderDish.getDishId());
                    if (correntValinOrders == null)
                    {
                        //т.е. нет активных заказов на это блюдо
                        if (countAvailableinOrg < orderDish.getAmount())
                        {
                            orderDish.setAmount(countAvailableinOrg);
                        }
                    } else
                    {
                        Integer availableCount = countAvailableinOrg - correntValinOrders;
                        if (availableCount < 1)
                            continue;
                        if (availableCount < orderDish.getAmount())
                        {
                            orderDish.setAmount(availableCount);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Ошибка при сохранении заказа для Фудбокса", e);
                    result.setCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
                    result.setDescription(ResponseCodes.RC_INTERNAL_ERROR.toString());
                    return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(result).build();
                }
                havegoodDish = true;
                FoodBoxPreorderDish foodBoxPreorderDish = new FoodBoxPreorderDish();
                foodBoxPreorderDish.setFoodBoxPreorder(foodBoxPreorder);
                foodBoxPreorderDish.setIdOfDish(orderDish.getDishId());
                foodBoxPreorderDish.setPrice(orderDish.getPrice().intValue());
                foodBoxPreorderDish.setQty(orderDish.getAmount());
                if (orderDish.getPrice() != null)
                    priceAll += orderDish.getPrice();
                foodBoxPreorderDish.setName(orderDish.getName());
                foodBoxPreorderDish.setBuffetCategoriesId(orderDish.getBuffetCategoryId());
                foodBoxPreorderDish.setBuffetCategoriesName(orderDish.getBuffetCategoryName());
                foodBoxPreorderDish.setCreateDate(new Date());
                persistenceSession.persist(foodBoxPreorderDish);
                currentFoodboxOrderInfo.getDishes().add(orderDish);
                countDish += orderDish.getAmount();
                if (countDish > MAX_COUNT_DISH) {
                    logger.error(String.format("Блюд заказано больше допустимого idOfDish: %s", orderDish.getDishId()));
                    OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
                    orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_DISH_COUNT.getCode());
                    orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_DISH_COUNT.toString());
                    return Response.status(HTTP_UNPROCESSABLE_ENTITY).entity(orderErrorInfo).build();
                }
            }
            if (!havegoodDish) {
                logger.error(String.format("Все блюда из заказа не доступны foodBoxid: %s", xrequestStr));
                OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
                orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_NO_DISH.getCode());
                orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_NO_DISH.toString());
                return Response.status(HTTP_UNPROCESSABLE_ENTITY).entity(orderErrorInfo).build();
            }
            foodBoxPreorder.setOrderPrice(priceAll);
            persistenceSession.merge(foodBoxPreorder);
            currentFoodboxOrderInfo.setTotalPrice(priceAll);
            if (client.getBalance() != null && client.getBalance() != 0) {
                if (priceAll > client.getBalance()) {
                    logger.error("Сумма заказа превышает баланс клиента");
                    OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
                    orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_NOMONEY.getCode());
                    orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_NOMONEY.toString());
                    orderErrorInfo.setBalanceLimit(client.getExpenditureLimit());
                    return Response.status(HTTP_UNPROCESSABLE_ENTITY).entity(orderErrorInfo).build();
                }
            }
            if (client.getExpenditureLimit() != null && client.getExpenditureLimit() != 0) {
                if (priceAll > availableMoney) {
                    logger.error("Сумма заказа превышает дневной лимит трат");
                    OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
                    orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_LIMIT.getCode());
                    orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_LIMIT.toString());
                    orderErrorInfo.setBalanceLimit(client.getExpenditureLimit());
                    return Response.status(HTTP_UNPROCESSABLE_ENTITY).entity(orderErrorInfo).build();
                }
            }
            //Есди количество свободных ячеек не позволяет создавать новый заказ
            if (countFree <= countunlocketed) {
                logger.error(String.format("У организации с id = %s нет свободных ячеек фудбокса", client.getOrg().getIdOfOrg()));
                OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
                orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_CELL.getCode());
                orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_CELL.toString());
                return Response.status(HTTP_UNPROCESSABLE_ENTITY).entity(orderErrorInfo).build();
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Ошибка при сохранении заказа для Фудбокса", e);
            result.setCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
            result.setDescription(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(result).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return Response.status(HttpURLConnection.HTTP_CREATED).entity(currentFoodboxOrderInfo).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "orders/foodbox")
    @Transactional
    public Response getInfoFoodBoxPreorder(@Context HttpServletRequest request) {
        Result result = new Result();
        //Контроль безопасности
        if (!validateAccess()) {
            logger.error("Не удалось авторизовать пользователя");
            result.setCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_KEY.toString());
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).entity(result).build();
        }
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
        try {
            contract = Long.parseLong(contractIdStr);
        } catch (Exception e) {
            logger.error(String.format("Неверный формат contract %s", contractIdStr), e);
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        try {
            from = simpleDateFormat.parse(fromStr);
        } catch (Exception e) {
            logger.error(String.format("Неверный формат from %s", fromStr), e);
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        try {
            to = simpleDateFormat.parse(toStr);
        } catch (Exception e) {
            logger.error(String.format("Неверный формат to %s", toStr), e);
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        try {
            if (!sortStr.isEmpty()) {
                if (sortStr.equals("asc"))
                    sortDesc = false;
            }
        } catch (Exception e) {
            logger.error(String.format("Неверный формат sort %s", sortStr), e);
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        Client client = null;
        client = daoReadonlyService.getClientByContractId(contract);
        if (client == null) {
            logger.error("Клиент не найден");
            result.setCode(ResponseCodes.RC_NOT_FOUND_CLIENT.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_CLIENT.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        HistoryFoodboxOrderInfo historyFoodboxOrderInfo = new HistoryFoodboxOrderInfo();
        try {
            List<FoodBoxPreorder> foodBoxPreorders = daoReadonlyService.getFoodBoxPreorderByForClient(client, from, to);
            List<FoodboxOrderInfo> foodboxOrderInfos = new ArrayList<>();
            Long sum = 0L;
            for (FoodBoxPreorder foodBoxPreorder : foodBoxPreorders) {
                FoodboxOrderInfo foodboxOrderInfo = convertData(foodBoxPreorder);
                sum += 1L;
                foodboxOrderInfos.add(foodboxOrderInfo);
            }
            if (sortDesc)
                Collections.sort(foodboxOrderInfos);
            else
                Collections.reverse(foodboxOrderInfos);

            historyFoodboxOrderInfo.getInfo().addAll(foodboxOrderInfos);
            historyFoodboxOrderInfo.setOrders(sum);
            return Response.status(HttpURLConnection.HTTP_OK).entity(historyFoodboxOrderInfo).build();
        } catch (Exception e) {
            logger.error("Ошибка при получении заказа для Фудбокса", e);
            result.setCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
            result.setDescription(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(result).build();
        }
    }


    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "orders/foodbox/{foodboxOrderId}")
    @Transactional
    public Response getInfoFoodBoxPreorder(@Context HttpServletRequest request, @PathParam("foodboxOrderId") String foodboxOrderId) {
        Result result = new Result();
        //Контроль безопасности
        if (!validateAccess()) {
            logger.error("Не удалось авторизовать пользователя");
            result.setCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_KEY.toString());
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).entity(result).build();
        }
        Long isppIdFoodbox;
        try {
            isppIdFoodbox = Long.parseLong(foodboxOrderId);
        } catch (Exception e) {
            logger.error(String.format("Неверный формат isppIdFoodbox %s", foodboxOrderId), e);
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        Client client = null;
        HistoryFoodboxOrderInfo historyFoodboxOrderInfo = new HistoryFoodboxOrderInfo();
        try {
            FoodBoxPreorder foodBoxPreorder = daoReadonlyService.findFoodBoxPreorderById(isppIdFoodbox);
            if (foodBoxPreorder == null) {
                logger.error(String.format("Не найден заказ с id %s", isppIdFoodbox));
                result.setCode(ResponseCodes.RC_NOT_FOUND_FOODBOX.getCode().toString());
                result.setDescription(ResponseCodes.RC_NOT_FOUND_FOODBOX.toString());
                return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
            }
            FoodboxOrderInfo foodboxOrderInfo = convertData(foodBoxPreorder);
            historyFoodboxOrderInfo.getInfo().add(foodboxOrderInfo);
            historyFoodboxOrderInfo.setOrders(1L);
            return Response.status(HttpURLConnection.HTTP_OK).entity(historyFoodboxOrderInfo).build();
        } catch (Exception e) {
            logger.error("Ошибка при получении заказа для Фудбокса", e);
            result.setCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
            result.setDescription(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(result).build();
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "menu/buffet")
    @Transactional
    public Response getInfoFoodBoxMenu(@Context HttpServletRequest request) {
        Result result = new Result();
        //Контроль безопасности
        if (!validateAccess()) {
            logger.error("Не удалось авторизовать пользователя");
            result.setCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_KEY.toString());
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).entity(result).build();
        }
        String contractIdStr = "";
        String onDateStr = "";
        Date onDate = null;
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
        try {
            if (!onDateStr.isEmpty()) {
                onDate = new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss")).parse(onDateStr);
            }
        } catch (Exception e) {
            logger.error("Неверный формат onDate");
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        if (contractIdStr.isEmpty()) {
            logger.error("Отсутствует contractId");
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        Long contractId;
        try {
            contractId = Long.parseLong(contractIdStr);
        } catch (Exception e) {
            logger.error(String.format("Неверный формат contractId %s", contractIdStr), e);
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        Client client = daoReadonlyService.getClientByContractId(contractId);
        if (client == null) {
            logger.error("Клиент не найден");
            result.setCode(ResponseCodes.RC_NOT_FOUND_CLIENT.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_CLIENT.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        Boolean errorOrg = false;
        try {
            if (!client.getOrg().getUsedFoodbox()) {
                errorOrg = true;
            }
        } catch (Exception e) {
            errorOrg = true;
        }
        if (errorOrg) {
            logger.error("У организации не включен функционал фудбокса");
            result.setCode(ResponseCodes.RC_NOT_FOUND_ORG.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_ORG.toString());
            return Response.status(HttpURLConnection.HTTP_FORBIDDEN).entity(result).build();
        }
        if (!client.getFoodboxAvailability()) {
            logger.error("У клиента не включен функционал фудбокса");
            result.setCode(ResponseCodes.RC_NOT_FOUND_AVAILABLE_CLIENT.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_AVAILABLE_CLIENT.toString());
            return Response.status(HttpURLConnection.HTTP_FORBIDDEN).entity(result).build();
        }
        //Собираем данные для орг
        List<WtDish> wtDishes = daoReadonlyService.getWtDishesByOrgandDate(client.getOrg(), onDate);
        //Тут будет фильтр по остаткам

        //
        //Расскидываем по классам
        PersonBuffetMenu personBuffetMenu = new PersonBuffetMenu();
        personBuffetMenu.setBuffetIsOpen(true);
        personBuffetMenu.setBuffetOpenAt(getBuffetOpenTime());
        personBuffetMenu.buffetCloseTime(getBuffetCloseTime());
        Integer corCount = 0;
        for (WtDish wtDish : wtDishes) {
            //Находим все подкатегории
            List<WtCategoryItem> wtCategoryItemList = daoReadonlyService.getCategoryItemsByWtDish(wtDish.getIdOfDish());
            if (wtCategoryItemList.isEmpty()) {
                WtCategory wtCategory = wtDish.getWtCategory();
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
                for (PersonBuffetMenuBuffetCategoriesItem p : personBuffetMenu.getCategories()) {
                    if (p.getId().equals(wtCategory.getIdOfCategory())) {
                        buffetMenuBuffetCategoriesItem = p;
                        break;
                    }
                }
                if (buffetMenuBuffetCategoriesItem == null) {
                    buffetMenuBuffetCategoriesItem = new PersonBuffetMenuBuffetCategoriesItem();
                    buffetMenuBuffetCategoriesItem.setId(wtCategory.getIdOfCategory());
                    buffetMenuBuffetCategoriesItem.setName(wtCategory.getDescription());
                    personBuffetMenu.getCategories().add(buffetMenuBuffetCategoriesItem);
                }
                buffetMenuBuffetCategoriesItem.getDishes().add(dish);
            }
            for (WtCategoryItem wtCategoryItem : wtCategoryItemList) {
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
                for (PersonBuffetMenuBuffetCategoriesItem p : personBuffetMenu.getCategories()) {
                    if (p.getId().equals(wtCategory.getIdOfCategory())) {
                        buffetMenuBuffetCategoriesItem = p;
                        break;
                    }
                }
                if (buffetMenuBuffetCategoriesItem == null) {
                    buffetMenuBuffetCategoriesItem = new PersonBuffetMenuBuffetCategoriesItem();
                    buffetMenuBuffetCategoriesItem.setId(wtCategory.getIdOfCategory());
                    buffetMenuBuffetCategoriesItem.setName(wtCategory.getDescription());
                    personBuffetMenu.getCategories().add(buffetMenuBuffetCategoriesItem);
                }
                ////
                PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem buffetSubcategoriesItem = null;
                for (PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem g : buffetMenuBuffetCategoriesItem.getSubcategories()) {
                    if (g.getId().equals(wtCategoryItem.getIdOfCategoryItem())) {
                        buffetSubcategoriesItem = g;
                        break;
                    }
                }
                if (buffetSubcategoriesItem == null) {
                    buffetSubcategoriesItem = new PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem();
                    buffetSubcategoriesItem.setId(wtCategoryItem.getIdOfCategoryItem());
                    buffetSubcategoriesItem.setName(wtCategoryItem.getDescription());
                    buffetMenuBuffetCategoriesItem.getSubcategories().add(buffetSubcategoriesItem);
                }
                buffetSubcategoriesItem.getDishes().add(dish);
            }
            //Тупая обрезка всего, что больше порога (меня заставили)
            corCount++;
            if (corCount == MAX_COUNT)
                break;
        }
        return Response.status(HttpURLConnection.HTTP_OK).entity(personBuffetMenu).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "clients/foodboxAllowed")
    @Transactional
    public Response setFoodboxAvailability(@Context HttpServletRequest request) {
        Result result = new Result();
        //Контроль безопасности
        if (!validateAccess()) {
            logger.error("Не удалось авторизовать пользователя");
            result.setCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_KEY.toString());
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).entity(result).build();
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
//        Enumeration<String> headerNames = request.getHeaderNames();
//        if (headerNames != null) {
//            while (headerNames.hasMoreElements()) {
//                String header = headerNames.nextElement();
//                if (header.toLowerCase().equals("contractid"))
//                {
//                    contractIdStr = request.getHeader(header);
//                    continue;
//                }
//                if (header.toLowerCase().equals("foodboxavailability"))
//                {
//                    foodBoxAvailableStr = request.getHeader(header);
//                    continue;
//                }
//            }
//        }
        if (contractIdStr.isEmpty()) {
            logger.error("Отсутствует contractId");
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }

        if (foodBoxAvailableStr.isEmpty()) {
            logger.error("Отсутствует foodBoxAvailable");
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        Long contractId;
        try {
            contractId = Long.parseLong(contractIdStr);
        } catch (Exception e) {
            logger.error(String.format("Неверный формат contractId %s", contractIdStr), e);
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        Client client = daoReadonlyService.getClientByContractId(contractId);
        if (client == null) {
            logger.error("Клиент не найден");
            result.setCode(ResponseCodes.RC_NOT_FOUND_CLIENT.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_CLIENT.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        Boolean errorOrg = false;
        try {
            if (!client.getOrg().getUsedFoodbox()) {
                errorOrg = true;
            }
        } catch (Exception e) {
            errorOrg = true;
        }
        if (errorOrg) {
            logger.error("У организации не включен функционал фудбокса");
            result.setCode(ResponseCodes.RC_NOT_FOUND_ORG.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_ORG.toString());
            return Response.status(HttpURLConnection.HTTP_FORBIDDEN).entity(result).build();
        }
        Boolean foodBoxAvailable = true;
        try {
            foodBoxAvailable = Boolean.parseBoolean(foodBoxAvailableStr);
        } catch (Exception e) {
            logger.error("Неверный формат foodBoxAvailable");
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
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
            result.setCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
            result.setDescription(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(result).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        result.setCode(ResponseCodes.RC_OK.getCode().toString());
        result.setDescription(ResponseCodes.RC_OK.toString());
        return Response.status(HttpURLConnection.HTTP_NO_CONTENT).entity(result).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "client")
    @Transactional
    public Response getFoodboxAvailability(@Context HttpServletRequest request) {
        Result result = new Result();
        //Контроль безопасности
        if (!validateAccess()) {
            logger.error("Не удалось авторизовать пользователя");
            result.setCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_KEY.toString());
            return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).entity(result).build();
        }
        String contractIdStr = "";
        Map<String, String> params = parseParams(request);
        for (Map.Entry<String, String> currParam : params.entrySet()) {
            if (currParam.getKey().toLowerCase().equals("contractid")) {
                contractIdStr = currParam.getValue();
                continue;
            }
        }
//        Enumeration<String> headerNames = request.getHeaderNames();
//        if (headerNames != null) {
//            while (headerNames.hasMoreElements()) {
//                String header = headerNames.nextElement();
//                if (header.toLowerCase().equals("contractid"))
//                {
//                    contractIdStr = request.getHeader(header);
//                    continue;
//                }
//            }
//        }
        if (contractIdStr.isEmpty()) {
            logger.error("Отсутствует contractId");
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        Long contractId;
        try {
            contractId = Long.parseLong(contractIdStr);
        } catch (Exception e) {
            logger.error(String.format("Неверный формат contractId %s", contractIdStr), e);
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        Client client = daoReadonlyService.getClientByContractId(contractId);
        if (client == null) {
            logger.error("Клиент не найден");
            result.setCode(ResponseCodes.RC_NOT_FOUND_CLIENT.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_CLIENT.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        Boolean errorOrg = false;
        try {
            if (!client.getOrg().getUsedFoodbox()) {
                errorOrg = true;
            }
        } catch (Exception e) {
            errorOrg = true;
        }
        if (errorOrg) {
            logger.error("У организации не включен функционал фудбокса");
            result.setCode(ResponseCodes.RC_NOT_FOUND_ORG.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_ORG.toString());
            return Response.status(HttpURLConnection.HTTP_FORBIDDEN).entity(result).build();
        }
        ClientData clientData = new ClientData();
        clientData.setFoodboxAllowed(client.getFoodboxAvailability());
        clientData.setFoodboxAvailablе(client.getOrg().getUsedFoodbox());
        return Response.status(HttpURLConnection.HTTP_OK).entity(clientData).build();
    }


    private FoodboxOrderInfo convertData(FoodBoxPreorder foodBoxPreorder) {
        FoodboxOrderInfo foodboxOrderInfo = new FoodboxOrderInfo();
        foodboxOrderInfo.setExpiredAt(simpleDateFormat.format(new Date(foodBoxPreorder.getCreateDate().getTime() + 7200000)) + "Z");
        if (foodBoxPreorder.getState() != null) {
            foodboxOrderInfo.setStatus(foodBoxPreorder.getState().getDescription());
        }
        foodboxOrderInfo.setCreatedAt(simpleDateFormat.format(foodBoxPreorder.getCreateDate()) + "Z");
        foodboxOrderInfo.setFoodboxOrderId(foodBoxPreorder.getIdFoodBoxPreorder());
        Long sum = 0L;
        for (FoodBoxPreorderDish foodBoxPreorderDish : DAOReadonlyService.getInstance().getFoodBoxPreordersDishes(foodBoxPreorder)) {
            OrderDish orderDish = new OrderDish();
            orderDish.setAmount(foodBoxPreorderDish.getQty());
            orderDish.setDishId(foodBoxPreorderDish.getIdOfDish());
            orderDish.setPrice(foodBoxPreorderDish.getPrice().longValue());
            orderDish.setName(foodBoxPreorderDish.getName());
            orderDish.setBuffetCategoryId(foodBoxPreorderDish.getBuffetCategoriesId());
            orderDish.setBuffetCategoryName(foodBoxPreorderDish.getBuffetCategoriesName());
            sum += foodBoxPreorderDish.getPrice();
            foodboxOrderInfo.getDishes().add(orderDish);
        }
        foodboxOrderInfo.setTotalPrice(sum);
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
        String openTime = RuntimeContext.getInstance().getConfigProperties().getProperty(BUFFET_OPEN_TIME, "08:00");
        return openTime;
    }

    private String getBuffetCloseTime() {
        String closeTime = RuntimeContext.getInstance().getConfigProperties().getProperty(BUFFET_CLOSE_TIME, "19:00");
        return closeTime;
    }

    private boolean validateAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
            return true;
        return false;
    }
}
