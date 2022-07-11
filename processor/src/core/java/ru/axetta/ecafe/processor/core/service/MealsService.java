package ru.axetta.ecafe.processor.core.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
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
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.meals.MealsController;
import ru.axetta.ecafe.processor.web.partner.meals.ResponseCodesError;
import ru.axetta.ecafe.processor.web.partner.meals.ResponseResult;
import ru.axetta.ecafe.processor.web.partner.meals.models.*;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.jasper.Constants.DEFAULT_BUFFER_SIZE;

/**
 * Created by A. Voinov on 28.06.2022.
 */
@Component
@Scope(value = "singleton")
public class MealsService {
    private static final Logger logger = LoggerFactory.getLogger(MealsService.class);
    public static final String BUFFET_HEALTH = "ecafe.processor.meals.health";
    public static final String BUFFET_OPEN_TIME = "ecafe.processor.meals.buffetOpenTime";
    public static final String BUFFET_CLOSE_TIME = "ecafe.processor.meals.buffetCloseTime";
    public static final Integer TIME_ALIVE = getHealthTime();
    public static final int HTTP_UNPROCESSABLE_ENTITY = 422;
    public static final String MAX_DISH = "ecafe.processor.meals.maxDishCount";
    public static final Integer MAX_COUNT_DISH = getMaxDishCount();
    public static final String MAX_COUNT_PARAM = "ecafe.processor.meals.maxCount";
    public static final Integer MAX_COUNT = getMaxCount();//Максимальное количество блюд для возвращения в методе
    private static final ThreadLocal<SimpleDateFormat> simpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); }
    };

    private static final ThreadLocal<SimpleDateFormat> format = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm", Locale.ENGLISH); }
    };

    private Integer countunlocketed;
    private Integer countFree = 0;
    private List<WtDish> wtDishes;
    private List <FoodBoxPreorderAvailable> foodBoxPreorderAvailables;
    private Map<Long,Integer> orders;
    private List<ProhibitionMenu> prohibitionMenus;

    @Autowired
    private ResponseResult responseResult;

    @Autowired
    private DAOReadonlyService daoReadonlyService;

    @Transactional
    public MealsPOJO validateByClientInfo(Long contractId, MealsController.MealsFunctions fun)
    {
        MealsPOJO mealsPOJO = verifyClient(contractId);
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO;
        Client client = mealsPOJO.getClient();
        Boolean errorOrg = false;
        try {
            if (!client.getOrg().getUsedFoodbox()) {
                errorOrg = true;
            }
        } catch (Exception e) {
            errorOrg = true;
        }
        if (errorOrg) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_FORBIDDEN).
                    entity(responseResult.orgDisableFoodBox()).build());
            return mealsPOJO;
        }

        //Проверяем параллель клиента
        if (!new ClientParallel().verifyParallelForClient(client))
        {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_FORBIDDEN).
                    entity(responseResult.wrongParallel()).build());
            return mealsPOJO;
        }

        if (!client.getFoodboxAvailability()) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_FORBIDDEN).
                    entity(responseResult.clientNoFoodbox()).build());
            return mealsPOJO;
        }

        if (MealsController.MealsFunctions.GET_BUFET == fun)
            //На этом этапе закончиваются проверки для буфета
            return mealsPOJO;

        List<FoodBoxPreorder> foodBoxPreorders = daoReadonlyService.getActiveFoodBoxPreorderForClient(client);
        if (foodBoxPreorders != null && !foodBoxPreorders.isEmpty()) {
            mealsPOJO.setResponse(Response.status(HTTP_UNPROCESSABLE_ENTITY).
                    entity(responseResult.notEndedPrev(foodBoxPreorders.get(0))).build());
            return mealsPOJO;
        }
        Long availableMoney = 0L;
        if (client.getExpenditureLimit() != null && client.getExpenditureLimit() != 0) {
            //Получаем истраченную сумму по кассе
            Long usedSuminDay = daoReadonlyService.getSumForOrdersbyClientOnPeriod(client.getIdOfClient(), CalendarUtils.startOfDay(new Date()), CalendarUtils.endOfDay(new Date()));
            Long usedSuminDayFoodBox = daoReadonlyService.getUsedMoneyFoodBoxPreorderForClient(client, CalendarUtils.startOfDay(new Date()), CalendarUtils.endOfDay(new Date()));
            availableMoney = client.getExpenditureLimit() - usedSuminDay - usedSuminDayFoodBox;
            mealsPOJO.setAvailableMoney(availableMoney);
            if (availableMoney < 0) {
                mealsPOJO.setResponse(Response.status(HTTP_UNPROCESSABLE_ENTITY).
                        entity(responseResult.moreLimitByDay(client.getExpenditureLimit())).build());
                return mealsPOJO;
            }
        }
        return mealsPOJO;
    }

    @Transactional
    public MealsPOJO validateByClientAllowed(Long contractId)
    {
        MealsPOJO mealsPOJO = verifyClient(contractId);
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO;
        Client client = mealsPOJO.getClient();
        if (!new ClientParallel().verifyParallelForClient(client))
        {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.wrongParallel()).build());
            return mealsPOJO;
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
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.orgDisableFoodBox()).build());
            return mealsPOJO;
        }
        return mealsPOJO;
    }

    public MealsPOJO verifyClient(Long contractId)
    {
        MealsPOJO mealsPOJO = new MealsPOJO();
        Client client = null;
        client = daoReadonlyService.getClientWithOrgByContractId(contractId);
        if (client == null) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.noClient()).build());
            return mealsPOJO;
        }
        mealsPOJO.setClient(client);
        return mealsPOJO;
    }

    public MealsPOJO validateByFormalInfo(String contractIdStr, String xrequestStr)
    {
        MealsPOJO mealsPOJO = new MealsPOJO();

        //Контроль времени работы
        try {
            OrderErrorInfo orderErrorInfo = validateTime();
            if (orderErrorInfo != null) {
                mealsPOJO.setResponse(Response.status(HTTP_UNPROCESSABLE_ENTITY).
                        entity(orderErrorInfo).build());
                return mealsPOJO;
            }

        } catch (Exception e)
        {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity(responseResult.errorTimeParse(e)).build());
            return mealsPOJO;
        }
/////////
        if (contractIdStr.isEmpty()) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.emptyContractId()).build());
            return mealsPOJO;
        }

        Long contractId;
        try {
            contractId = Long.parseLong(contractIdStr);
        } catch (Exception e) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.wrongFormatContractId(contractIdStr, e)).build());
            return mealsPOJO;
        }
        mealsPOJO.setContractId(contractId);
//////////
        verifyContractId(contractIdStr, mealsPOJO);
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO;
///////////
        if (xrequestStr.isEmpty()) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.noXRID()).build());
            return mealsPOJO;
        }

        FoodBoxPreorder foodBoxPreorderDB = daoReadonlyService.getFoodBoxPreorderByExternalId(xrequestStr);
        if (foodBoxPreorderDB != null) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.doubleIdentif(xrequestStr)).build());
            return mealsPOJO;
        }
        return mealsPOJO;
    }

    public MealsPOJO validateByFormalInfoGetFoodbox(String contractIdStr, String fromStr, String toStr, String sortStr)
    {
        MealsPOJO mealsPOJO = new MealsPOJO();
        Long contract = null;
        Date from = null;
        Date to = null;
        Boolean sortDesc = true;

        verifyContractId(contractIdStr, mealsPOJO);
        if (mealsPOJO.getResponse() != null)
            return mealsPOJO;
        
        try {
            from = simpleDateFormat.get().parse(fromStr);
        } catch (Exception e) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.wrongFormatFrom(fromStr,e)).build());
            return mealsPOJO;
        }
        mealsPOJO.setFrom(from);
        try {
            to = simpleDateFormat.get().parse(toStr);
        } catch (Exception e) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.wrongFormatTo(toStr,e)).build());
            return mealsPOJO;
        }
        mealsPOJO.setTo(to);
        try {
            if (!sortStr.isEmpty()) {
                if (sortStr.equals("asc"))
                    sortDesc = false;
            }
        } catch (Exception e) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.wrongFormatSort(sortStr,e)).build());
            return mealsPOJO;
        }
        mealsPOJO.setSortDesc(sortDesc);
        return mealsPOJO;
    }

    public MealsPOJO validateByFormalInfoGetFoodbox(String foodboxOrderId)
    {
        MealsPOJO mealsPOJO = new MealsPOJO();
        Long isppIdFoodbox;
        try {
            isppIdFoodbox = Long.parseLong(foodboxOrderId);
        } catch (Exception e) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.wrongFormatFoodBoxOrder(foodboxOrderId,e)).build());
            return mealsPOJO;
        }
        mealsPOJO.setIsppIdFoodbox(isppIdFoodbox);
        return mealsPOJO;
    }

    public MealsPOJO validateByFormalInfoForBuffet4(String onDateStr, String contractIdStr)
    {
        MealsPOJO mealsPOJO = new MealsPOJO();

        try {
            if (!onDateStr.isEmpty()) {
                mealsPOJO.setOnDate(new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss")).parse(onDateStr));
            }
            else
            {
                mealsPOJO.setOnDate(new Date());
            }
        } catch (Exception e) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.wrongOnDate()).build());
            return mealsPOJO;
        }
        verifyContractId(contractIdStr, mealsPOJO);
        return mealsPOJO;
    }

    public MealsPOJO validateByFormalInfoClientAllowed(String contractIdStr, String foodBoxAvailableStr, MealsController.MealsFunctions fun)
    {
        MealsPOJO mealsPOJO = new MealsPOJO();

        if (MealsController.MealsFunctions.SET_FOODBOX_ALLOWED == fun) {
            if (foodBoxAvailableStr.isEmpty()) {
                mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                        entity(responseResult.emptyFoodBoxAvailable()).build());
                return mealsPOJO;
            }

            Boolean foodBoxAvailable;
            try {
                foodBoxAvailable = Boolean.parseBoolean(foodBoxAvailableStr);
            } catch (Exception e) {
                mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                        entity(responseResult.wrongFoodBoxAvailable()).build());
                return mealsPOJO;
            }
            mealsPOJO.setFoodBoxAvailable(foodBoxAvailable);
        }
        verifyContractId(contractIdStr, mealsPOJO);
        return mealsPOJO;
    }

    private void verifyContractId(String contractIdStr, MealsPOJO mealsPOJO)
    {
        if (contractIdStr.isEmpty()) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.emptyContractId()).build());
            return;
        }

        Long contractId;
        try {
            contractId = Long.parseLong(contractIdStr);
        } catch (Exception e) {
            mealsPOJO.setResponse(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(responseResult.wrongFormatContractId(contractIdStr, e)).build());
            return;
        }
        mealsPOJO.setContractId(contractId);
    }

    public Response mainLogicNewPreorder(FoodboxOrder foodboxOrders, Client client, String xrequestStr, Long availableMoney)
    {
        //Структура ответа
        CurrentFoodboxOrderInfo currentFoodboxOrderInfo = new CurrentFoodboxOrderInfo(client, simpleDateFormat);
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            //Структура сохранения в бд
            FoodBoxPreorder foodBoxPreorder = new FoodBoxPreorder(
                    client, daoReadonlyService.getMaxVersionOfFoodBoxPreorder() + 1, xrequestStr);
            persistenceSession.persist(foodBoxPreorder);
            currentFoodboxOrderInfo.setFoodboxOrderId(foodBoxPreorder.getIdFoodBoxPreorder());
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
                    return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                            entity(responseResult.internalError(e, 1)).build();
                }
                if (toMaxCount)
                {
                    countDish += orderDish.getAmount();
                    continue;
                }

                havegoodDish = true;
                FoodBoxPreorderDish foodBoxPreorderDish = new FoodBoxPreorderDish(foodBoxPreorder, orderDish);
                if (orderDish.getPrice() != null) {
                    if (orderDish.getAmount() == null) {
                        priceAll += orderDish.getPrice();
                    } else {
                        priceAll += (orderDish.getPrice() * orderDish.getAmount());
                    }
                }
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
                return Response.status(HTTP_UNPROCESSABLE_ENTITY).
                        entity(responseResult.moreCountDish(countDish.longValue())).build();
            }
            if (!havegoodDish) {
                return Response.status(HTTP_UNPROCESSABLE_ENTITY).
                        entity(responseResult.allDishNotAvailable(xrequestStr)).build();
            }
            foodBoxPreorder.setOrderPrice(priceAll);
            persistenceSession.merge(foodBoxPreorder);
            currentFoodboxOrderInfo.setTotalPrice(priceAll);

            if (client.getBalance() != null) {
                if (priceAll > client.getBalance()) {
                    return Response.status(HTTP_UNPROCESSABLE_ENTITY).
                            entity(responseResult.moreLimit(client.getBalance())).build();
                }
            }
            if (client.getExpenditureLimit() != null && client.getExpenditureLimit() != 0) {
                if (priceAll > availableMoney) {
                    return Response.status(HTTP_UNPROCESSABLE_ENTITY).
                            entity(responseResult.moreLimitByDay(client.getExpenditureLimit())).build();
                }
            }
            //Если количество свободных ячеек не позволяет создавать новый заказ
            if (countFree <= countunlocketed) {
                return Response.status(HTTP_UNPROCESSABLE_ENTITY).
                        entity(responseResult.errorCell(client.getOrg().getIdOfOrg())).build();
            }
            persistenceTransaction.commit();
            //Добавляем заказ для отслеживания
            CancelledFoodBoxService.currentFoodBoxPreorders.put(foodBoxPreorder.getIdFoodBoxPreorder(), foodBoxPreorder.getCreateDate());
            persistenceTransaction = null;
        } catch (Exception e) {
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(responseResult.internalError(e, 1)).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return Response.status(HttpURLConnection.HTTP_CREATED).entity(currentFoodboxOrderInfo).build();
    }

    public Response getPreordersForDates(Client client, Date from, Date to, Boolean sortDesc)
    {
        //Структура ответа
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
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(responseResult.internalError(e, 2)).build();
        }
    }

    public Response getPreorderById(Long isppIdFoodbox)
    {
        //Структура ответа
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        try {
            FoodBoxPreorder foodBoxPreorder = daoReadonlyService.findFoodBoxPreorderById(isppIdFoodbox);
            if (foodBoxPreorder == null) {
                return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseResult.notFindPreorder(isppIdFoodbox)).build();
            }
            FoodboxOrderInfo foodboxOrderInfo = convertData(foodBoxPreorder);
            return Response.status(HttpURLConnection.HTTP_OK).entity(foodboxOrderInfo).build();
        } catch (Exception e) {
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(responseResult.internalError(e, 2)).build();
        }
    }

    public Response getBuffetInfo()
    {
        //Структура ответа
        //Раскидываем по классам
        PersonBuffetMenu personBuffetMenu = new PersonBuffetMenu();
        personBuffetMenu.setBuffetIsOpen(validateTime() == null);
        try {
            personBuffetMenu.setBuffetOpenAt(format.get().
                    format(CalendarUtils.convertdateInUTC(format.get().parse(getBuffetOpenTime()))));
            personBuffetMenu.buffetCloseTime(format.get().
                    format(CalendarUtils.convertdateInUTC(format.get().parse(getBuffetCloseTime()))));
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
                Dish dish = new Dish(wtDish);
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
                Dish dish = new Dish(wtDish);
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

    public Response setFoodboxAllowed(Client client, Boolean foodBoxAvailable)
    {
        //Структура ответа
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
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity(responseResult.internalError(e, 3)).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return Response.status(HttpURLConnection.HTTP_NO_CONTENT).
                entity(responseResult.ok()).build();
    }

    public Response getFoodBoxAllowed(Client client, Boolean foodBoxAvailable)
    {
        //Структура ответа
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

        return Response.status(HttpURLConnection.HTTP_OK).entity(clientData).build();
    }

    public void getDataFromDAO(Client client, MealsController.MealsFunctions fun)
    {
        if (MealsController.MealsFunctions.CREATE_FOODBOX == fun) {
            Set<FoodBoxCells> foodBoxCells = daoReadonlyService.getFoodBoxCellsByOrg(client.getOrg());
            countunlocketed = daoReadonlyService.getFoodBoxPreordersUnallocated(client.getOrg());
            for (FoodBoxCells foodBoxCells1 : foodBoxCells) {
                //Считаем количество свободных ячеек в фудбоксах
                countFree += (foodBoxCells1.getTotalcellscount() - foodBoxCells1.getBusycells());
            }
        }
        //Собираем данные для орг
        wtDishes = daoReadonlyService.getWtDishesByOrgandDate(client.getOrg(), new Date());
        //Получаем количество доступных блюд для орг
        foodBoxPreorderAvailables = daoReadonlyService.getFoodBoxPreorderAvailable(client.getOrg());
        //Получаем список активных заказов футбокса
        orders = daoReadonlyService.getDishesCountActiveFoodBoxPreorderForOrg(client.getOrg());
        //Получаем список запретов
        prohibitionMenus = daoReadonlyService.findProhibitionMenuByClientId(client);
    }

    public boolean validateAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
            return true;
        return false;
    }

    public OrderErrorInfo validateTime() {
        try {
            if (!validateTimeInternal())
            {
                logger.error("Заказ пришел на время закрытия буфета");
                OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
                orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_TIME.getCode());
                orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_TIME.toString());
                orderErrorInfo.getDetails().setBuffetOpenAt(format.get().format(CalendarUtils.convertdateInUTC(format.get().parse(getBuffetOpenTime()))));
                orderErrorInfo.getDetails().setBuffetCloseAt(format.get().format(CalendarUtils.convertdateInUTC(format.get().parse(getBuffetCloseTime()))));
                return orderErrorInfo;
            }
            else
                return null;
        } catch (Exception e)
        {
            return null;
        }

    }

    private Boolean validateTimeInternal()
    {
        Long createTime = new Date().getTime() - CalendarUtils.startOfDay(new Date()).getTime();
        try {
            Date startOpen = CalendarUtils.convertdateInLocal(format.get().parse(getBuffetOpenTime()));
            Date closeEnd = CalendarUtils.convertdateInLocal(format.get().parse(getBuffetCloseTime()));
            if (createTime < (startOpen.getTime()) || createTime > (closeEnd.getTime() - TIME_ALIVE)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void copyResourceToFile(String fileName, File file)
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

    private static Integer getHealthTime() {
        try {
            String health = RuntimeContext.getInstance().getConfigProperties().getProperty(BUFFET_HEALTH, "7200000");
            return Integer.parseInt(health);
        } catch (Exception e)
        {
            return 7200000;
        }
    }

    private static Integer getMaxDishCount() {
        try {
            String health = RuntimeContext.getInstance().getConfigProperties().getProperty(MAX_DISH, "5");
            return Integer.parseInt(health);
        } catch (Exception e)
        {
            return 5;
        }
    }

    private static Integer getMaxCount() {
        try {
            String health = RuntimeContext.getInstance().getConfigProperties().getProperty(MAX_COUNT_PARAM, "40");
            return Integer.parseInt(health);
        } catch (Exception e)
        {
            return 40;
        }
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

    private String getBuffetOpenTime() {
        String openTime = RuntimeContext.getInstance().getConfigProperties().getProperty(BUFFET_OPEN_TIME, "08:00");
        return openTime;
    }

    private String getBuffetCloseTime() {
        String closeTime = RuntimeContext.getInstance().getConfigProperties().getProperty(BUFFET_CLOSE_TIME, "19:00");
        return closeTime;
    }

    private FoodboxOrderInfo convertData(FoodBoxPreorder foodBoxPreorder) {
        FoodboxOrderInfo foodboxOrderInfo = new FoodboxOrderInfo();
        foodboxOrderInfo.setExpiredAt(simpleDateFormat.get().format(
                CalendarUtils.convertdateInUTC(new Date(foodBoxPreorder.getCreateDate().getTime() + 7200000))) + "Z");
        if (foodBoxPreorder.getState() != null) {
            foodboxOrderInfo.setStatus(foodBoxPreorder.getState().getDescription());
        }
        foodboxOrderInfo.setCreatedAt(simpleDateFormat.get().format(CalendarUtils.convertdateInUTC(foodBoxPreorder.getCreateDate())) + "Z");
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

    public Integer getCountunlocketed() {
        return countunlocketed;
    }

    public void setCountunlocketed(Integer countunlocketed) {
        this.countunlocketed = countunlocketed;
    }

    public Integer getCountFree() {
        return countFree;
    }

    public void setCountFree(Integer countFree) {
        this.countFree = countFree;
    }

    public List<WtDish> getWtDishes() {
        return wtDishes;
    }

    public void setWtDishes(List<WtDish> wtDishes) {
        this.wtDishes = wtDishes;
    }

    public List<FoodBoxPreorderAvailable> getFoodBoxPreorderAvailables() {
        return foodBoxPreorderAvailables;
    }

    public void setFoodBoxPreorderAvailables(List<FoodBoxPreorderAvailable> foodBoxPreorderAvailables) {
        this.foodBoxPreorderAvailables = foodBoxPreorderAvailables;
    }

    public Map<Long, Integer> getOrders() {
        return orders;
    }

    public void setOrders(Map<Long, Integer> orders) {
        this.orders = orders;
    }

    public List<ProhibitionMenu> getProhibitionMenus() {
        return prohibitionMenus;
    }

    public void setProhibitionMenus(List<ProhibitionMenu> prohibitionMenus) {
        this.prohibitionMenus = prohibitionMenus;
    }
}
