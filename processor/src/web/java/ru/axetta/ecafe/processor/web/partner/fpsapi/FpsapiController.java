/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.fpsapi.dataflow.Allergen;
import ru.axetta.ecafe.processor.web.partner.fpsapi.dataflow.AllergenResult;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Path(value = "")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Controller
public class FpsapiController {

    private Logger logger = LoggerFactory.getLogger(FpsapiController.class);
    private static final Integer RANGES_DAYS = 14;

    @GET
    @Path(value = "/netrika/mobile/v1/sales")
    public Response getSales(@QueryParam(value = "RegId") String regID, @QueryParam(value = "DateFrom") String dateFrom,
            @QueryParam(value = "DateTo") String dateTo) throws Exception {
        ResponseSales responseSales = new ResponseSales();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        //Вычисление результата запроса
        try {
            Date dateFromD;
            Date dateToD;
            try {
                dateFromD = new SimpleDateFormat("yyyy-MM-dd").parse(dateFrom);
                dateToD = new SimpleDateFormat("yyyy-MM-dd").parse(dateTo);
            }
            catch (ParseException e) {
                logger.error("Ошибка в формате даты", e);
                responseSales.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode());
                responseSales.setErrorMessage("Переданы некорректные параметры");
                return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseSales).build();
            }

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            responseSales.setServerTimestamp(new Date());
            Client client = DAOUtils.findClientByIacregid(persistenceSession, regID);
            if (client == null) {
                throw new IllegalArgumentException("Client with regID = " + regID + " is not found");
            }

            List<Order> orders = DAOUtils
                    .findOrdersbyIdofclientandBetweenTime(persistenceSession, client, dateFromD,
                            dateToD);
            if (orders.isEmpty()) {
                responseSales.setErrorCode(ResponseCodes.RC_OK.getCode());
                responseSales.setErrorMessage(ResponseCodes.RC_OK.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(responseSales).build();
            }
            for (Order order : orders) {
                boolean complex = false;
                Long idComplex = 0L;
                //Не обрабатываем заказы данного типа
                if (order.getOrderType() == OrderTypeEnumType.REDUCED_PRICE_PLAN
                        || order.getOrderType() == OrderTypeEnumType.DAILY_SAMPLE
                        || order.getOrderType() == OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE
                        || order.getOrderType() == OrderTypeEnumType.CORRECTION_TYPE
                        || order.getOrderType() == OrderTypeEnumType.TEST_EMULATOR
                        || order.getOrderType() == OrderTypeEnumType.WATER_ACCOUNTING
                        || order.getOrderType() == OrderTypeEnumType.DISCOUNT_PLAN_CHANGE
                        || order.getOrderType() == OrderTypeEnumType.RECYCLING_RETIONS) {
                    continue;
                }

                //Текущий заказ комплексный ?
                for (OrderDetail orderDetail : order.getOrderDetails()) {
                    if (orderDetail.getMenuType() > 0 && orderDetail.getMenuType() < 100) {
                        //Да
                        idComplex = orderDetail.getCompositeIdOfOrderDetail().getIdOfOrderDetail();
                        complex = true;
                        break;
                    }
                }
                if (!complex) {
                    //Если не комплекс, то ДОЛЖЕН быть одного из 2-ух типов: "По-умолчанию" или "Вендинг"
                    if (order.getOrderType() == OrderTypeEnumType.DEFAULT
                            || order.getOrderType() == OrderTypeEnumType.VENDING) {
                        for (OrderDetail orderDetail : order.getOrderDetails()) {
                            if (orderDetail.getMenuType() == 0) {
                                responseSales.getSales().add(setParametrs(order, orderDetail, SalesOrderType.BUFFET));
                            }
                        }
                    }
                }
                else
                {
                    //Если комплекс, то ДОЛЖЕН быть одного из 2-ух типов: "План платного питания" или "Абонементное питание"
                    if (order.getOrderType() == OrderTypeEnumType.PAY_PLAN
                            || order.getOrderType() == OrderTypeEnumType.SUBSCRIPTION_FEEDING) {
                        for (OrderDetail orderDetail : order.getOrderDetails()) {
                            if (orderDetail.getCompositeIdOfOrderDetail().getIdOfOrderDetail().equals(idComplex)) {
                                responseSales.getSales().add(setParametrs(order, orderDetail, SalesOrderType.HOT_FOOD));
                            }
                        }
                    }
                }
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseSales.setErrorCode(ResponseCodes.RC_OK.getCode());
            responseSales.setErrorMessage(ResponseCodes.RC_OK.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseSales).build();
        } catch (IllegalArgumentException e) {
            logger.error("Can't find client", e);
            responseSales.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode());
            responseSales.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseSales).build();
        } catch (Exception e) {
            logger.error("InternalError", e);
            responseSales.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode());
            responseSales.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(responseSales).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

    }


    @GET
    @Path(value = "/netrika/mobile/v1/average")
    public Response getAverage(@QueryParam(value = "RegId") String regID, @QueryParam(value = "Date") String date,
            @QueryParam(value = "Range") Integer range) throws Exception {
        ResponseAverage responseAverage = new ResponseAverage();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        //Вычисление результата запроса
        try {
            //Дата до
            Date dateTo;
            //Дата с
            Date dateFrom;
            //Общая сумма покупок в период
            Long sum = 0L;
            //Массив дат, когда школьник питался
            HashSet<String> datesEat = new HashSet<>();

            Integer rangeDate;
            //Если дата задана
            if (date != null) {
                try {
                    dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                } catch (ParseException e) {
                    logger.error("Ошибка в формате даты", e);
                    responseAverage.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode());
                    responseAverage.setErrorMessage("Переданы некорректные параметры");
                    return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseAverage).build();

                }
            }
            else
                //Иначе берем текущую дату
                dateTo = new Date();
            //Если радиус задан
            if (range != null)
            {
                rangeDate = range;
            }
            else
            {
                //Берём константу
                rangeDate = RANGES_DAYS;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateTo);
            cal.add(Calendar.DATE, -rangeDate);
            dateFrom = cal.getTime();


            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            responseAverage.setServerTimestamp(new Date());
            Client client = DAOUtils.findClientByIacregid(persistenceSession, regID);
            if (client == null) {
                throw new IllegalArgumentException("Client with regID = " + regID + " is not found");
            }

            List<Order> orders = DAOUtils
                    .findOrdersbyIdofclientandBetweenTime(persistenceSession, client, dateFrom,
                            dateTo);
            if (orders.isEmpty()) {
                responseAverage.setErrorCode(ResponseCodes.RC_OK.getCode());
                responseAverage.setErrorMessage(ResponseCodes.RC_OK.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(responseAverage).build();
            }
            for (Order order : orders) {
                //Не обрабатываем заказы данного типа
                if (order.getOrderType() == OrderTypeEnumType.REDUCED_PRICE_PLAN
                        || order.getOrderType() == OrderTypeEnumType.DAILY_SAMPLE
                        || order.getOrderType() == OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE
                        || order.getOrderType() == OrderTypeEnumType.CORRECTION_TYPE
                        || order.getOrderType() == OrderTypeEnumType.TEST_EMULATOR
                        || order.getOrderType() == OrderTypeEnumType.WATER_ACCOUNTING
                        || order.getOrderType() == OrderTypeEnumType.DISCOUNT_PLAN_CHANGE
                        || order.getOrderType() == OrderTypeEnumType.RECYCLING_RETIONS) {
                    continue;
                }
                //Берем только валидные заказы
                if (order.getState() == 1) {
                    continue;
                }
                //Считаем сумму по всем заказам
                if (order.getRSum() != null)
                    sum+=order.getRSum();

                if (order.getOrderDate() != null)
                {
                    datesEat.add(new SimpleDateFormat("yyyy-MM-dd").format(order.getOrderDate()));
                }
            }

            AverageItem averageItem = new AverageItem();

            averageItem.setDate(new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
            averageItem.setRange(rangeDate);
            averageItem.setSum(sum);
            averageItem.setAveragesum(sum.floatValue()/(float)datesEat.size());
            averageItem.setDaycount(datesEat.size());
            averageItem.setAccounttypeid(1);
            responseAverage.getAverage().add(averageItem);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseAverage.setErrorCode(ResponseCodes.RC_OK.getCode());
            responseAverage.setErrorMessage(ResponseCodes.RC_OK.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseAverage).build();
        } catch (IllegalArgumentException e) {
            logger.error("Can't find client", e);
            responseAverage.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode());
            responseAverage.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseAverage).build();
        } catch (Exception e) {
            logger.error("InternalError", e);
            responseAverage.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode());
            responseAverage.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(responseAverage).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

    }

    private SalesItem setParametrs (Order order, OrderDetail orderDetail, SalesOrderType salesOrderType)
    {
        SalesItem salesItem = new SalesItem();
        salesItem.setId(order.getCompositeIdOfOrder().getIdOfOrder());
        salesItem.setTimestamp(timeConverter(order.getOrderDate()));
        salesItem.setDate_creation(timeConverter(order.getCreateTime()));
        salesItem.setProductid(orderDetail.getCompositeIdOfOrderDetail().getIdOfOrderDetail());
        salesItem.setProductname(orderDetail.getMenuDetailName());
        salesItem.setQuantity(orderDetail.getQty());
        salesItem.setSum(orderDetail.getQty() * orderDetail.getRPrice());
        salesItem.setDiscount(orderDetail.getDiscount());
        if (order.getState() == 1 && order.getTransaction() != null)
            salesItem.setRemoved(timeConverter(order.getTransaction().getTransactionTime()));
        else
            salesItem.setRemoved("");
        if (SalesOrderType.HOT_FOOD.getCode() == salesOrderType.getCode()) {
            salesItem.setAccount_type(SalesOrderType.HOT_FOOD.getCode());
            salesItem.setAccount_name(SalesOrderType.HOT_FOOD.getDescription());
        }
        if (SalesOrderType.BUFFET.getCode() == salesOrderType.getCode()) {
            salesItem.setAccount_type(SalesOrderType.BUFFET.getCode());
            salesItem.setAccount_name(SalesOrderType.BUFFET.getDescription());
        }
        if (order.getTransaction() != null)
            salesItem.setTransactionid(order.getTransaction().getIdOfTransaction());
        return salesItem;
    }

    private String timeConverter (Date date)
    {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        return timeStamp.replace(' ', 'T');
    }

    @Path("/netrika/mobile/v1/allergens")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllergetns(@QueryParam(value = "RegID") String regId) {
        AllergenResult result = new AllergenResult();

        Session session = null;
        Transaction transaction = null;
        try {
            if (StringUtils.isEmpty(regId)) {
                throw new IllegalArgumentException("Couldn't find all parameters");
            }

            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createReportPersistenceSession();
            transaction = session.beginTransaction();

            Client client = DAOUtils.findClientByIacregid(session, regId);

            if (null == client) {
                throw new IllegalArgumentException(String.format("Unable to find client by regId=%s", regId));
            }

            Menu menu = DAOUtils.findLastMenuByOrgBeforeDate(session, client.getOrg().getIdOfOrg(), new Date());

            if (null == menu) {
                throw new NoResultException(String.format("Unable to find menu for client with regId=%s", regId));
            }

            result.setAllergens(findAllergens(session, client, menu));

            transaction.commit();
            transaction = null;

            result.setErrorCode(ResponseCodes.RC_OK.getCode());
            result.setErrorMessage(ResponseCodes.RC_OK.toString());
            result.setServerTimestamp(new Date());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            result.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode());
            result.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            result.setServerTimestamp(new Date());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        } catch (NoResultException e) {
            logger.error(e.getMessage(), e);
            result.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode());
            result.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            result.setServerTimestamp(new Date());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private List<Allergen> findAllergens(Session session, Client client, Menu menu) {
        List<Allergen> allergenList = new ArrayList<>();
        Query query = session.createSQLQuery("select p.idofprohibitions, p.filtertext, md.groupname from cf_menu m "
                + "join cf_menudetails md on md.idofmenu = m.idofmenu "
                + "join cf_prohibitions p on p.filtertext = md.menudetailname and p.idofclient = :idOfClient "
                + "where m.idoforg = :idOfOrg and m.idofmenu = :idOfMenu and p.deletedstate = false");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("idOfMenu", menu.getIdOfMenu());

        List list = query.list();

        if (!list.isEmpty()) {
            HashMap<String, Integer> typeIdHashMap = new HashMap<>();
            for (Object o : list) {
                Object[] res = (Object[]) o;
                Long idOfProhibition = ((BigInteger) res[0]).longValue();
                String filterText = (String) res[1];
                String groupName = (String) res[2];
                if (!typeIdHashMap.containsKey(groupName)) {
                    typeIdHashMap.put(groupName, typeIdHashMap.keySet().size());
                }
                allergenList.add(new Allergen(idOfProhibition, filterText, typeIdHashMap.get(groupName), groupName, true));
            }
            return allergenList;
        }

        query = session.createSQLQuery(
                "select m.idofmenu, md.menudetailname, md.groupname from cf_menu m "
                        + "join cf_menudetails md on md.idofmenu = m.idofmenu "
                        + "where m.idoforg = :idOfOrg and m.idofmenu = :idOfMenu");
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("idOfMenu", menu.getIdOfMenu());

        list = query.list();

        HashMap<String, Integer> typeIdHashMap = new HashMap<>();
        for (Object o : list) {
            Object[] res = (Object[]) o;
            Long idOfMenu = ((BigInteger) res[0]).longValue();
            String menuDetailName = (String) res[1];
            String groupName = (String) res[2];
            if (!typeIdHashMap.containsKey(groupName)) {
                typeIdHashMap.put(groupName, typeIdHashMap.keySet().size());
            }
            allergenList.add(new Allergen(idOfMenu, menuDetailName, typeIdHashMap.get(groupName), groupName, false));
        }
        return allergenList;
    }
}
