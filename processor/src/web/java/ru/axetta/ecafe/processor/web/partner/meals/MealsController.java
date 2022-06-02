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
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.ClientParallel;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ProhibitionMenu;
import ru.axetta.ecafe.processor.core.persistence.foodbox.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtCategory;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtCategoryItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDish;
import ru.axetta.ecafe.processor.core.service.CancelledFoodBoxService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.meals.models.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.jasper.Constants.DEFAULT_BUFFER_SIZE;

@Path(value = "")
@Controller
@ApplicationPath("/ispp/meals/v1/")
public class MealsController extends Application {

    private Logger logger = LoggerFactory.getLogger(MealsController.class);
    public static final String BUFFET_OPEN_TIME = "ecafe.processor.meals.buffetOpenTime";
    public static final String BUFFET_CLOSE_TIME = "ecafe.processor.meals.buffetCloseTime";
    public static final String BUFFET_OPEN = "ecafe.processor.meals.buffetOpen";
    public static final String BUFFET_HEALTH = "ecafe.processor.meals.health";
    public static final Integer MAX_COUNT_DISH = 5;
    public static final int HTTP_UNPROCESSABLE_ENTITY = 422;
    public static final Integer TIME_ALIVE = getHealthTime();

    public static final Integer MAX_COUNT = 40;//Максимальное количество блюд для возвращения в методе
    protected static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final DateFormat format = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat((DATE_FORMAT));

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path(value = "contract.yaml")
    public Response getFile() throws IOException {
        File file = File.createTempFile("prefix-", "-suffix");
        copyResourceToFile("swagger/foodbox.yaml", file);
        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=contract.yaml" )
                .build();
    }

    private void copyResourceToFile(String fileName, File file)
    {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(fileName);
            // append = false
            try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
                int read;
                byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }
        } catch (Exception e)
        {
            logger.error("Ошибка при формировании файла foodbox.yaml");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "orders/foodbox")
    @Transactional
    public Response addPersonFoodboxOrder(@Context HttpServletRequest request, FoodboxOrder foodboxOrders) {
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
        if (!validateTime())
        {
            try {
                logger.error("Заказ пришел на время закрытия буфета");
                OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
                orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_TIME.getCode());
                orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_TIME.toString());
                orderErrorInfo.getDetails().setBuffetOpenAt(format.format(CalendarUtils.convertdateInUTC(format.parse(getBuffetOpenTime()))));
                orderErrorInfo.getDetails().setBuffetCloseAt(format.format(CalendarUtils.convertdateInUTC(format.parse(getBuffetCloseTime()))));
                return Response.status(HTTP_UNPROCESSABLE_ENTITY).entity(orderErrorInfo).build();
            } catch (Exception e)
            {
                logger.error("Ошибка при получении парсинге времени работы Фудбокса", e);
                result.setCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
                result.setDescription(ResponseCodes.RC_INTERNAL_ERROR.toString());
                List<Result> results = new ArrayList<>();
                results.add(result);
                return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(results).build();
            }

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

        //Проверяем параллель клиента
        if (!new ClientParallel().verifyParallelForClient(client))
        {
            result.setCode(ResponseCodes.RC_NOT_FOUND_AVAILABLE_PARALLEL.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_AVAILABLE_PARALLEL.toString());
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
            orderErrorInfo.getDetails().setFoodboxOrderId(foodBoxPreorders.get(0).getIdFoodBoxPreorder());
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
                orderErrorInfo.getDetails().setBalanceLimit(client.getExpenditureLimit());
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
        //Получаем список запретов
        List<ProhibitionMenu> prohibitionMenus = daoReadonlyService.findProhibitionMenuByClientId(client);
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
            currentFoodboxOrderInfo.setExpiredAt(simpleDateFormat.format(
                    CalendarUtils.convertdateInUTC(new Date(new Date().getTime() + TIME_ALIVE))) + "Z");
            currentFoodboxOrderInfo.setCreatedAt(simpleDateFormat.format(
                    CalendarUtils.convertdateInUTC(new Date())) + "Z");
            currentFoodboxOrderInfo.setBalance(client.getBalance());
            currentFoodboxOrderInfo.setBalanceLimit(client.getExpenditureLimit());
            Long priceAll = 0L;
            boolean havegoodDish = false;
            Integer countDish = 0;
            boolean toMaxCount = false;
            for (OrderDish orderDish : foodboxOrders.getDishes()) {
                //Проверки
                try {
                    WtDish wtDish = daoReadonlyService.getWtDishById(orderDish.getDishId());
                    WtCategory wtCategory = wtDish.getWtCategory();
                    if (have_prohobition(wtDish, wtCategory, null, prohibitionMenus))
                        continue;
                    //Находим все подкатегории
                    List<WtCategoryItem> wtCategoryItemList = daoReadonlyService.getCategoryItemsByWtDish(wtDish.getIdOfDish());
                    if (!wtCategoryItemList.isEmpty()) {
                        //Значит у блюда изначально были подкатегории
                        //Убираем все подкатегории, которые не проходят по ограничению меню
                        wtCategoryItemList.removeIf(wtCategoryItem -> have_prohobition(null, null, wtCategoryItem, prohibitionMenus));
                        //Если после удаления всех запрещенных подкатегорий ничего не осталось, то не добавляем блюдо
                        if (wtCategoryItemList.isEmpty())
                            continue;
                    }
                    if (!wtDishes.contains(wtDish)) {
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
                if (toMaxCount)
                {
                    countDish += orderDish.getAmount();
                    continue;
                }
                havegoodDish = true;
                FoodBoxPreorderDish foodBoxPreorderDish = new FoodBoxPreorderDish();
                foodBoxPreorderDish.setFoodBoxPreorder(foodBoxPreorder);
                foodBoxPreorderDish.setIdOfDish(orderDish.getDishId());
                foodBoxPreorderDish.setPrice(orderDish.getPrice().intValue());
                foodBoxPreorderDish.setQty(orderDish.getAmount());
                if (orderDish.getPrice() != null) {
                    if (orderDish.getAmount() == null) {
                        priceAll += orderDish.getPrice();
                    } else {
                        priceAll += (orderDish.getPrice() * orderDish.getAmount());
                    }
                }
                foodBoxPreorderDish.setName(orderDish.getName());
                foodBoxPreorderDish.setBuffetCategoriesId(orderDish.getBuffetCategoryId());
                foodBoxPreorderDish.setBuffetCategoriesName(orderDish.getBuffetCategoryName());
                foodBoxPreorderDish.setCreateDate(new Date());
                persistenceSession.persist(foodBoxPreorderDish);
                currentFoodboxOrderInfo.getDishes().add(orderDish);
                countDish += orderDish.getAmount();
                if (countDish > MAX_COUNT_DISH) {
                    logger.error(String.format("Блюд заказано больше допустимого idOfDish: %s", orderDish.getDishId()));
                    //Все последующие блюда мы будем проверять только для того, что бы узнать количество заказанного
                    toMaxCount = true;
                }
            }
            if (toMaxCount) {
                OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
                orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_DISH_COUNT.getCode());
                orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_DISH_COUNT.toString());
                orderErrorInfo.getDetails().setAmount(countDish.longValue());
                return Response.status(HTTP_UNPROCESSABLE_ENTITY).entity(orderErrorInfo).build();
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
            if (client.getBalance() != null) {
                if (priceAll > client.getBalance()) {
                    logger.error("Сумма заказа превышает баланс клиента");
                    OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
                    orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_NOMONEY.getCode());
                    orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_NOMONEY.toString());
                    orderErrorInfo.getDetails().setBalance(client.getBalance());
                    return Response.status(HTTP_UNPROCESSABLE_ENTITY).entity(orderErrorInfo).build();
                }
            }
            if (client.getExpenditureLimit() != null && client.getExpenditureLimit() != 0) {
                if (priceAll > availableMoney) {
                    logger.error("Сумма заказа превышает дневной лимит трат");
                    OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
                    orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_LIMIT.getCode());
                    orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_LIMIT.toString());
                    orderErrorInfo.getDetails().setBalanceLimit(client.getExpenditureLimit());
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
            //Добавляем заказ для отслеживания
            CancelledFoodBoxService.currentFoodBoxPreorders.put(foodBoxPreorder.getIdFoodBoxPreorder(), foodBoxPreorder.getCreateDate());
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
    public Response getPersonFoodboxOrders(@Context HttpServletRequest request) {
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
        try {
            List<FoodBoxPreorder> foodBoxPreorders = daoReadonlyService.getFoodBoxPreorderByForClient(client, from, to);
            List<FoodboxOrderInfo> foodboxOrderInfos = new ArrayList<>();
            for (FoodBoxPreorder foodBoxPreorder : foodBoxPreorders) {
                FoodboxOrderInfo foodboxOrderInfo = convertData(foodBoxPreorder);
                foodboxOrderInfos.add(foodboxOrderInfo);
            }
            if (sortDesc)
                Collections.sort(foodboxOrderInfos);
            else
                Collections.reverse(foodboxOrderInfos);

            return Response.status(HttpURLConnection.HTTP_OK).entity(foodboxOrderInfos).build();
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
    public Response getPersonFoodboxOrder(@Context HttpServletRequest request, @PathParam("foodboxOrderId") String foodboxOrderId) {
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
            logger.error(String.format("Неверный формат foodboxOrderId %s", foodboxOrderId), e);
            result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
        }
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        try {
            FoodBoxPreorder foodBoxPreorder = daoReadonlyService.findFoodBoxPreorderById(isppIdFoodbox);
            if (foodBoxPreorder == null) {
                logger.error(String.format("Не найден заказ с id %s", isppIdFoodbox));
                result.setCode(ResponseCodes.RC_NOT_FOUND_FOODBOX.getCode().toString());
                result.setDescription(ResponseCodes.RC_NOT_FOUND_FOODBOX.toString());
                return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
            }
            FoodboxOrderInfo foodboxOrderInfo = convertData(foodBoxPreorder);
//            List<FoodboxOrderInfo> foodboxOrderInfos = new ArrayList<>();
//            foodboxOrderInfos.add(foodboxOrderInfo);
            return Response.status(HttpURLConnection.HTTP_OK).entity(foodboxOrderInfo).build();
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
    public Response getPersonBuffetMenu(@Context HttpServletRequest request) {
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
        Date onDate = new Date();
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
        //Проверяем параллель клиента
        if (!new ClientParallel().verifyParallelForClient(client))
        {
            result.setCode(ResponseCodes.RC_NOT_FOUND_AVAILABLE_PARALLEL.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_AVAILABLE_PARALLEL.toString());
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
        //Получаем количество доступных блюд для орг
        List <FoodBoxPreorderAvailable> foodBoxPreorderAvailables = daoReadonlyService.getFoodBoxPreorderAvailable(client.getOrg());
        //Получаем список активных заказов футбокса
        Map<Long,Integer> orders = daoReadonlyService.getDishesCountActiveFoodBoxPreorderForOrg(client.getOrg());
        //Получаем список запретов
        List<ProhibitionMenu> prohibitionMenus = daoReadonlyService.findProhibitionMenuByClientId(client);
        //
        //Расскидываем по классам
        PersonBuffetMenu personBuffetMenu = new PersonBuffetMenu();
        personBuffetMenu.setBuffetIsOpen(validateTime());
        try {
            personBuffetMenu.setBuffetOpenAt(format.format(CalendarUtils.convertdateInUTC(format.parse(getBuffetOpenTime()))));
            personBuffetMenu.buffetCloseTime(format.format(CalendarUtils.convertdateInUTC(format.parse(getBuffetCloseTime()))));
        } catch (Exception e)
        {
            logger.error("Ошибка при форматировании времени работы буфета", e);
        }
        Integer corCount = 0;
        for (WtDish wtDish : wtDishes) {
            if (have_prohobition(wtDish, null, null, prohibitionMenus))
                continue;
            try {
                Integer countAvailableinOrg = 0;
                //Получаем количество блюда в буфете
                for (FoodBoxPreorderAvailable foodBoxPreorderAvailable: foodBoxPreorderAvailables)
                {
                    if (foodBoxPreorderAvailable.getIdOfDish().equals(wtDish.getIdOfDish())) {
                        countAvailableinOrg = foodBoxPreorderAvailable.getAvailableQty();
                        break;
                    }
                }
                //Получаем сколько уже заказано данного блюда
                Integer correntValinOrders = orders.get(wtDish.getIdOfDish());
                if (correntValinOrders != null) {
                    //Проверяем, сколько ещё можно заказать
                    int availableCount = countAvailableinOrg - correntValinOrders;
                    if (availableCount < 1)
                        continue;
                }
            } catch (Exception e) {
                continue;
            }
            //Находим все подкатегории
            List<WtCategoryItem> wtCategoryItemList = daoReadonlyService.getCategoryItemsByWtDish(wtDish.getIdOfDish());
            boolean wtCategoryItemListEmp = false;
            if (wtCategoryItemList.isEmpty()) {
                //Ставим флаг, что погкатегорий нет изначально
                wtCategoryItemListEmp = true;
            }
            else {
                //Убираем все подкатегории, которые не проходят по ограничению меню
                wtCategoryItemList.removeIf(wtCategoryItem -> have_prohobition(null, null, wtCategoryItem, prohibitionMenus));
            }
            //Если подкатегорий не было изначально, то блюдо записываем в категорию
            if (wtCategoryItemListEmp) {
                WtCategory wtCategory = wtDish.getWtCategory();
                if (have_prohobition(null, wtCategory, null, prohibitionMenus))
                    continue;
                Dish dish = new Dish();
                ///
                dish.setId(wtDish.getIdOfDish());
                dish.setCode(wtDish.getBarcode());
                dish.setName(wtDish.getDishName());
                dish.setPrice(wtDish.getPrice().multiply(new BigDecimal(100)).longValue());
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
            //Если все подкатегории оказались заблокированы, то не добавляем это блюдо
            if (wtCategoryItemList.isEmpty())
                continue;
            for (WtCategoryItem wtCategoryItem : wtCategoryItemList) {
                WtCategory wtCategory = wtCategoryItem.getWtCategory();
                Dish dish = new Dish();
                ///
                dish.setId(wtDish.getIdOfDish());
                dish.setCode(wtDish.getBarcode());
                dish.setName(wtDish.getDishName());
                dish.setPrice(wtDish.getPrice().multiply(new BigDecimal(100)).longValue());
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
            if (corCount.equals(MAX_COUNT))
                break;
        }
        return Response.status(HttpURLConnection.HTTP_OK).entity(personBuffetMenu).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "clients/foodboxAllowed")
    @Transactional
    public Response setPersonFoodboxAllowed(@Context HttpServletRequest request) {
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
        if (!new ClientParallel().verifyParallelForClient(client))
        {
            logger.error("Клиент не входит в параллель");
            result.setCode(ResponseCodes.RC_NOT_FOUND_AVAILABLE_PARALLEL.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_AVAILABLE_PARALLEL.toString());
            return Response.status(HttpURLConnection.HTTP_FORBIDDEN).entity(result).build();
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
            client.setFoodboxavailabilityguardian(true);
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
    @Path(value = "clients")
    @Transactional
    public Response getClientData(@Context HttpServletRequest request) {
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
        clientData.getClientId().setContractId(client.getContractId());
        clientData.getClientId().setStaffId(null);
        clientData.getClientId().setPersonId(null);
        if (client.getOrg() != null)
        {
            clientData.getOrganization().setAddress(client.getOrg().getShortAddress());
            clientData.getOrganization().setName(client.getOrg().getShortNameInfoService());
            if (client.getOrg().getType() != null)
                clientData.getOrganization().setType(client.getOrg().getType().toString());
        }
        clientData.setPreorderAllowed(ClientManager.getAllowedPreorderByClient(client.getIdOfClient(), null));
        clientData.setBalance(client.getBalance());
        clientData.setFoodboxAllowed(client.getFoodboxAvailability());
        clientData.setFoodboxAvailable(client.getOrg().getUsedFoodbox());

        //Проверяем, что параллель клиента доступна для заказа фудбокса
        if (!new ClientParallel().verifyParallelForClient(client))
        {
            logger.error("Клиент не входит в параллель");
            result.setCode(ResponseCodes.RC_NOT_FOUND_AVAILABLE_PARALLEL.getCode().toString());
            result.setDescription(ResponseCodes.RC_NOT_FOUND_AVAILABLE_PARALLEL.toString());
            return Response.status(HttpURLConnection.HTTP_FORBIDDEN).entity(result).build();
        }
        else
        {
            logger.info("Клиент входит в параллель");
            return Response.status(HttpURLConnection.HTTP_OK).entity(clientData).build();
        }
    }


    private FoodboxOrderInfo convertData(FoodBoxPreorder foodBoxPreorder) {
        FoodboxOrderInfo foodboxOrderInfo = new FoodboxOrderInfo();
        foodboxOrderInfo.setExpiredAt(simpleDateFormat.format(
                CalendarUtils.convertdateInUTC(new Date(foodBoxPreorder.getCreateDate().getTime() + TIME_ALIVE))) + "Z");
        if (foodBoxPreorder.getState() != null) {
            foodboxOrderInfo.setStatus(foodBoxPreorder.getState().getDescription());
        }
        foodboxOrderInfo.setCreatedAt(simpleDateFormat.format(CalendarUtils.convertdateInUTC(foodBoxPreorder.getCreateDate())) + "Z");
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
            if (foodBoxPreorderDish.getPrice() != null) {
                if (foodBoxPreorderDish.getQty() == null)
                {
                    sum += foodBoxPreorderDish.getPrice();
                } else {
                    sum += (foodBoxPreorderDish.getPrice() * foodBoxPreorderDish.getQty());
                }
            }
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

    private Boolean getBuffetOpenFlag() {
        try {
            String openBuf = RuntimeContext.getInstance().getConfigProperties().getProperty(BUFFET_OPEN, "false");
            return Boolean.parseBoolean(openBuf);
        } catch (Exception e)
        {
            return false;
        }
    }

    private Boolean validateTime()
    {
        Long createTime = new Date().getTime() - CalendarUtils.startOfDay(new Date()).getTime();
        try {
            Date startOpen = CalendarUtils.convertdateInLocal(format.parse(getBuffetOpenTime()));
            Date closeEnd = CalendarUtils.convertdateInLocal(format.parse(getBuffetCloseTime()));
            if (createTime < (startOpen.getTime()) || createTime > (closeEnd.getTime() - TIME_ALIVE)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static Integer getHealthTime() {
        try {
            String health = RuntimeContext.getInstance().getConfigProperties().getProperty(BUFFET_HEALTH, "7200000");
            return Integer.parseInt(health);
        } catch (Exception e)
        {
            return 7200000;
        }
    }

    private boolean validateAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
            return true;
        return false;
    }

    private boolean have_prohobition (WtDish wtDish, WtCategory wtCategory, WtCategoryItem wtCategoryItem, List<ProhibitionMenu> prohibitionMenus)
    {
        boolean prohobition = false;
        for (ProhibitionMenu prohibitionMenu: prohibitionMenus)
        {
            if (wtDish != null) {
                if (prohibitionMenu.getWtDish() != null) {
                    if (prohibitionMenu.getWtDish().getIdOfDish().equals(wtDish.getIdOfDish())) {
                        prohobition = true;
                        break;
                    }
                }
                if (prohibitionMenu.getFilterText() != null && wtDish.getDishName() != null) {
                    if (prohibitionMenu.getFilterText().equals(wtDish.getDishName())) {
                        prohobition = true;
                        break;
                    }
                }
            }
            if (prohibitionMenu.getWtCategory() != null && wtCategory != null) {
                if (prohibitionMenu.getWtCategory().getIdOfCategory().equals(wtCategory.getIdOfCategory())) {
                    prohobition = true;
                    break;
                }
            }
            if (prohibitionMenu.getWtCategoryItem() != null && wtCategoryItem != null) {
                if (prohibitionMenu.getWtCategoryItem().getIdOfCategoryItem().equals(wtCategoryItem.getIdOfCategoryItem())) {
                    prohobition = true;
                    break;
                }
            }
        }
        return prohobition;
    }
}