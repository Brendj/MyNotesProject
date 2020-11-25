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
import ru.axetta.ecafe.processor.web.partner.fpsapi.dataflow.Result;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
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
@ApplicationPath("/fps/api")
public class FpsapiController extends Application {

    private Logger logger = LoggerFactory.getLogger(FpsapiController.class);
    private static final Integer RANGES_DAYS = 14;
    private static final String ERROR_DATE_FORMAT = "Date format error";
    private static final String ERROR_REQUEST_PARAMETRS = "Invalid parameters passed";

    private static final String REG_ID = "regid";
    private static final String DATE_TO = "dateto";
    private static final String DATE_FROM = "datefrom";
    private static final String DATE = "date";
    private static final String RANGE = "range";
    private static final String COUNT = "count";
    private static final String LAST_TRANSACTION_ID = "lasttransactionid";
    private static final String ALLERGEN_ID = "allergenid";
    private static final String ACTIVE = "active";

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Path(value = "/netrika/mobile/v1/sales")
    public Response getSales(@Context HttpServletRequest request) throws Exception {
        ResponseSales responseSales = new ResponseSales();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        //Вычисление результата запроса
        try {
            String regID = extractParamByName(request.getParameterMap(), REG_ID, true);
            String dateFrom =  extractParamByName(request.getParameterMap(), DATE_FROM, true);
            String dateTo =  extractParamByName(request.getParameterMap(), DATE_TO, true);
            Date dateFromD = new SimpleDateFormat("yyyy-MM-dd").parse(dateFrom);
            Date dateToD = new SimpleDateFormat("yyyy-MM-dd").parse(dateTo);

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            responseSales.setServerTimestamp(new Date());
            Client client = DAOUtils.findClientByIacregid(persistenceSession, regID);
            if (client == null) {
                throw new IllegalArgumentException("Client with regID = " + regID + " is not found");
            }

            List<Order> orders = DAOUtils
                    .findOrdersbyIdofclientandBetweenTime(persistenceSession, client, dateFromD, dateToD);
            if (orders.isEmpty()) {
                responseSales.setErrorCode(Long.toString(ResponseCodes.RC_OK.getCode()));
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
                } else {
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
            responseSales.setErrorCode(Long.toString(ResponseCodes.RC_OK.getCode()));
            responseSales.setErrorMessage(ResponseCodes.RC_OK.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseSales).build();
        } catch (ParseException e) {
            logger.error(ERROR_DATE_FORMAT, e);
            responseSales.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
            responseSales.setErrorMessage(ERROR_REQUEST_PARAMETRS);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseSales).build();
        } catch (IllegalArgumentException e) {
            logger.error("Can't find client", e);
            responseSales.setErrorCode(Long.toString(ResponseCodes.RC_INTERNAL_ERROR.getCode()));
            responseSales.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseSales).build();
        } catch (Exception e) {
            logger.error("InternalError", e);
            responseSales.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
            responseSales.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(responseSales).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Path(value = "/netrika/mobile/v1/average")
    public Response getAverage(@Context HttpServletRequest request) throws Exception {
        ResponseAverage responseAverage = new ResponseAverage();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        //Вычисление результата запроса
        try {
            String regID =  extractParamByName(request.getParameterMap(), REG_ID, true);
            String date = extractParamByName(request.getParameterMap(), DATE, true);
            String rangeStr = extractParamByName(request.getParameterMap(), RANGE, true);
            Integer range = null;
            if(!StringUtils.isEmpty(rangeStr)) {
                range = Integer.parseInt(rangeStr);
            }
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
                    logger.error(ERROR_DATE_FORMAT, e);
                    responseAverage.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
                    responseAverage.setErrorMessage(ERROR_REQUEST_PARAMETRS);
                    return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseAverage).build();

                }
            } else
            //Иначе берем текущую дату
            {
                dateTo = new Date();
            }
            //Если радиус задан
            if (range != null) {
                rangeDate = range;
            } else {
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
                    .findOrdersbyIdofclientandBetweenTime(persistenceSession, client, dateFrom, dateTo);
            if (orders.isEmpty()) {
                responseAverage.setErrorCode(Long.toString(ResponseCodes.RC_OK.getCode()));
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
                if (order.getRSum() != null) {
                    sum += order.getRSum();
                }

                if (order.getOrderDate() != null) {
                    datesEat.add(new SimpleDateFormat("yyyy-MM-dd").format(order.getOrderDate()));
                }
            }

            AverageItem averageItem = new AverageItem();

            averageItem.setDate(new SimpleDateFormat("yyyy-MM-dd").format(dateFrom));
            averageItem.setRange(Integer.toString(rangeDate));
            averageItem.setSum(converMoney(sum));
            averageItem.setAveragesum(converMoney(sum / datesEat.size()));

            averageItem.setDaycount(Integer.toString(datesEat.size()));
            averageItem.setAccounttypeid("1");
            responseAverage.getAverage().add(averageItem);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseAverage.setErrorCode(Long.toString(ResponseCodes.RC_OK.getCode()));
            responseAverage.setErrorMessage(ResponseCodes.RC_OK.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseAverage).build();
        } catch (IllegalArgumentException e) {
            logger.error("Can't find client", e);
            responseAverage.setErrorCode(Long.toString(ResponseCodes.RC_INTERNAL_ERROR.getCode()));
            responseAverage.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseAverage).build();
        } catch (Exception e) {
            logger.error("InternalError", e);
            responseAverage.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
            responseAverage.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(responseAverage).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Path(value = "/netrika/mobile/v1/transactionsbydate")
    public Response getTransactionsbyDate(@Context HttpServletRequest request) throws Exception {
        return workWithTransactions(request.getParameterMap(), 1);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Path(value = "/netrika/mobile/v1/transactions")
    public Response getTransactions(@Context HttpServletRequest request) throws Exception {
        return workWithTransactions(request.getParameterMap(), 0);

    }

    //type - определяет тип выборки: по количесву или по дате
    private Response workWithTransactions(Map<String, String[]> paramMap, Integer type) {
        ResponseTransactions responseTransactions = new ResponseTransactions();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        String dateTo = "";
        String dateFrom = "";
        //Вычисление результата запроса
        try {
            String regID = extractParamByName(paramMap, REG_ID, true);
            if(type == 1) {
                dateFrom = extractParamByName(paramMap, DATE_FROM, true);
                dateTo = extractParamByName(paramMap, DATE_TO, true);
            }

            String countStr = extractParamByName(paramMap, COUNT, true);
            Integer count = null;
            if(!StringUtils.isEmpty(countStr)) {
                count = Integer.parseInt(countStr);
            }
            String lastTransactionIdStr = extractParamByName(paramMap, LAST_TRANSACTION_ID, false);
            Long lastTransactionId = null;
            if(!StringUtils.isEmpty(lastTransactionIdStr)) {
                lastTransactionId = Long.parseLong(lastTransactionIdStr);
            }

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Date dateToT = new Date();
            Date dateFromT = new Date();
            //Получаем клиента
            Client client = DAOUtils.findClientByIacregid(persistenceSession, regID);
            if (client == null) {
                throw new IllegalArgumentException("Client with regID = " + regID + " is not found");
            }
            if (count == null && type == 0) {
                logger.error("Отсутствет количество выбираемых записей");
                responseTransactions.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
                responseTransactions.setErrorMessage(ERROR_REQUEST_PARAMETRS);
                return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseTransactions).build();
            }
            if (type == 1) {
                dateToT = new SimpleDateFormat("yyyy-MM-dd").parse(dateTo);
                dateFromT = new SimpleDateFormat("yyyy-MM-dd").parse(dateFrom);
                //Сдвиг на 1 день
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateToT);
                cal.add(Calendar.DATE, -1);
                dateToT = cal.getTime();
            }
            List<AccountTransaction> accountTransactions = new ArrayList<>();
            if (type == 0) {
                accountTransactions = DAOUtils
                        .getAccountTransactionsForClientbyLast(persistenceSession, client, lastTransactionId, count,
                                null, null);
            }
            if (type == 1) {
                accountTransactions = DAOUtils
                        .getAccountTransactionsForClientbyLast(persistenceSession, client, lastTransactionId, null,
                                dateFromT, dateToT);
            }
            if (accountTransactions == null) {
                throw new Exception();
            }
            responseTransactions.setServerTimestamp(new Date());

            //В указанный период нет транзакций
            if (accountTransactions.isEmpty()) {
                responseTransactions.setErrorCode(Long.toString(ResponseCodes.RC_OK.getCode()));
                responseTransactions.setErrorMessage(ResponseCodes.RC_OK.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(responseTransactions).build();
            }

            for (AccountTransaction accountTransaction : accountTransactions) {
                TransactionItem transactionItem = new TransactionItem();
                transactionItem.setId(Long.toString(accountTransaction.getIdOfTransaction()));
                transactionItem.setAccounttypeid(Integer.toString(SalesOrderType.HOT_FOOD.getCode()));
                transactionItem.setAccounttypename(SalesOrderType.HOT_FOOD.getDescription());
                transactionItem.setSum(converMoney(accountTransaction.getTransactionSum()));
                transactionItem.setTimestamp(timeConverter(accountTransaction.getTransactionTime()));

                if (isPositive(accountTransaction.getTransactionSum())) {
                    if (accountTransaction.getSourceType() == AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE
                            || accountTransaction.getSourceType() == AccountTransaction.CASHBOX_TRANSACTION_SOURCE_TYPE
                            || accountTransaction.getSourceType() == AccountTransaction.CANCEL_TRANSACTION_SOURCE_TYPE
                            || accountTransaction.getSourceType()
                            == AccountTransaction.ACCOUNT_TRANSFER_TRANSACTION_SOURCE_TYPE) {
                        transactionItem.setTransactiontypeid("2");
                        transactionItem.setTransactiontypename("Пополнение");
                    }
                } else {
                    if (accountTransaction.getSourceType() == AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE
                            || accountTransaction.getSourceType() == AccountTransaction.CASHBOX_TRANSACTION_SOURCE_TYPE
                            || accountTransaction.getSourceType()
                            == AccountTransaction.CLIENT_ORDER_TRANSACTION_SOURCE_TYPE
                            || accountTransaction.getSourceType()
                            == AccountTransaction.ACCOUNT_TRANSFER_TRANSACTION_SOURCE_TYPE
                            || accountTransaction.getSourceType()
                            == AccountTransaction.ACCOUNT_REFUND_TRANSACTION_SOURCE_TYPE
                            || accountTransaction.getSourceType()
                            == AccountTransaction.CUSTOMERS_CARD_REVEALING_TRANSACTION_SOURCE_TYPE) {
                        transactionItem.setTransactiontypeid("3");
                        transactionItem.setTransactiontypename("Списание");
                    }
                }

                transactionItem.setTransactiontag("");

                responseTransactions.getTransactions().add(transactionItem);
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseTransactions.setErrorCode(Long.toString(ResponseCodes.RC_OK.getCode()));
            responseTransactions.setErrorMessage(ResponseCodes.RC_OK.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseTransactions).build();
        } catch (ParseException e) {
            logger.error(ERROR_DATE_FORMAT, e);
            responseTransactions.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
            responseTransactions.setErrorMessage(ERROR_REQUEST_PARAMETRS);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseTransactions).build();
        } catch (IllegalArgumentException e) {
            logger.error("Can't find client", e);
            responseTransactions.setErrorCode(Long.toString(ResponseCodes.RC_INTERNAL_ERROR.getCode()));
            responseTransactions.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseTransactions).build();
        } catch (Exception e) {
            logger.error("InternalError", e);
            responseTransactions.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
            responseTransactions.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(responseTransactions).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private Boolean isPositive(long i) {
        if (i == 0) {
            return true;
        }
        if (i >> 63 != 0) {
            return false;
        }
        return true;
    }

    private String converMoney(Long sum) {
        String sumStr = Long.toString(sum);
        if (sum >= 100 || sum <= -100) {
            return sumStr.substring(0, sumStr.length() - 2) + "." + sumStr
                    .substring(sumStr.length() - 2, sumStr.length());
        } else {
            if (sum >= 0) {
                return "0." + sumStr;
            } else {
                return "-0." + sumStr.substring(1, sumStr.length());
            }
        }
    }

    private SalesItem setParametrs(Order order, OrderDetail orderDetail, SalesOrderType salesOrderType) {
        SalesItem salesItem = new SalesItem();
        salesItem.setId(Long.toString(order.getCompositeIdOfOrder().getIdOfOrder()));
        salesItem.setTimestamp(timeConverter(order.getOrderDate()));
        salesItem.setDate_creation(timeConverter(order.getCreateTime()));
        salesItem.setProductid(Long.toString(orderDetail.getCompositeIdOfOrderDetail().getIdOfOrderDetail()));
        salesItem.setProductname(orderDetail.getMenuDetailName());
        salesItem.setQuantity(orderDetail.getQty().toString());
        salesItem.setSum(converMoney(orderDetail.getQty() * orderDetail.getRPrice()));
        salesItem.setDiscount(Long.toString(orderDetail.getDiscount()));
        if (order.getState() == 1 && order.getTransaction() != null) {
            salesItem.setRemoved(timeConverter(order.getTransaction().getTransactionTime()));
        } else {
            salesItem.setRemoved("");
        }
        if (SalesOrderType.HOT_FOOD.getCode() == salesOrderType.getCode()) {
            salesItem.setAccount_type(Integer.toString(SalesOrderType.HOT_FOOD.getCode()));
            salesItem.setAccount_name(SalesOrderType.HOT_FOOD.getDescription());
        }
        if (SalesOrderType.BUFFET.getCode() == salesOrderType.getCode()) {
            salesItem.setAccount_type(Integer.toString(SalesOrderType.BUFFET.getCode()));
            salesItem.setAccount_name(SalesOrderType.BUFFET.getDescription());
        }
        if (order.getTransaction() != null) {
            salesItem.setTransactionid(Long.toString(order.getTransaction().getIdOfTransaction()));
        }
        return salesItem;
    }

    private String timeConverter(Date date) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        return timeStamp.replace(' ', 'T');
    }

    @Path("/netrika/mobile/v1/allergens")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response getAllergens(@Context HttpServletRequest request) {
        AllergenResult result = new AllergenResult();

        Session session = null;
        Transaction transaction = null;
        try {
            String regID = extractParamByName(request.getParameterMap(), REG_ID, true);
            if (StringUtils.isEmpty(regID)) {
                throw new IllegalArgumentException("Couldn't find all parameters");
            }

            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createReportPersistenceSession();
            transaction = session.beginTransaction();

            Client client = DAOUtils.findClientByIacregid(session, regID);

            if (null == client) {
                throw new IllegalArgumentException(String.format("Unable to find client by regId=%s", regID));
            }

            Menu menu = DAOUtils.findLastMenuByOrgBeforeDate(session, client.getOrg().getIdOfOrg(), new Date());

            if (null == menu) {
                throw new NoResultException(String.format("Unable to find menu for client with regId=%s", regID));
            }

            result.setAllergens(findAllergens(session, client, menu));

            transaction.commit();
            transaction = null;

            result.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_OK.toString());
            result.setServerTimestamp(new Date());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            result.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            result.setServerTimestamp(new Date());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        } catch (NoResultException e) {
            logger.error(e.getMessage(), e);
            result.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode().toString());
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
        Query query = session.createSQLQuery("select distinct "
                + "case when p.idofprohibitions is not null then p.idofprohibitions else md.idofmenudetail end as id, "
                + "case when p.idofprohibitions is not null then p.filtertext else md.menudetailname end as name, "
                + "md.groupname, p.idofprohibitions is not null as active " + "from cf_menu m "
                + "join cf_menudetails md on md.idofmenu = m.idofmenu "
                + "left join cf_prohibitions p on p.filtertext = md.menudetailname and p.idofclient = :idOfClient and p.deletedstate = false "
                + "where m.idoforg = :idOfOrg and m.idofmenu = :idOfMenu ");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("idOfMenu", menu.getIdOfMenu());

        List list = query.list();

        HashMap<String, Integer> typeIdHashMap = new HashMap<>();
        for (Object o : list) {
            Object[] res = (Object[]) o;
            Long idOfProhibition = ((BigInteger) res[0]).longValue();
            String filterText = (String) res[1];
            String groupName = (String) res[2];
            Boolean active = (Boolean) res[3];
            if (!typeIdHashMap.containsKey(groupName)) {
                typeIdHashMap.put(groupName, typeIdHashMap.keySet().size());
            }
            allergenList
                    .add(new Allergen(idOfProhibition, filterText, typeIdHashMap.get(groupName), groupName, active));
        }
        return allergenList;
    }

    @Path("/netrika/mobile/v1/allergens/create")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response createAllergen(@Context HttpServletRequest request) {
        Result result = new Result();

        Session session = null;
        Transaction transaction = null;
        try {
            String regID = extractParamByName(request.getParameterMap(), REG_ID, true);


            String activeStr = extractParamByName(request.getParameterMap(), ACTIVE, true);
            Integer active = null;
            if(!StringUtils.isEmpty(activeStr)) {
                active = Integer.parseInt(activeStr);
            }
            String allergenIdStr = extractParamByName(request.getParameterMap(), ALLERGEN_ID, true);
            Long allergenId = null;
            if(!StringUtils.isEmpty(allergenIdStr)) {
                allergenId = Long.parseLong(allergenIdStr);
            }

            if (StringUtils.isEmpty(regID) || (null == allergenId) || (null == active)) {
                throw new IllegalArgumentException("Couldn't find all parameters");
            }
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = DAOUtils.findClientByIacregid(session, regID);

            if (null == client) {
                throw new IllegalArgumentException(String.format("Unable to find client by regId=%s", regID));
            }

            if (0 == active) {
                ProhibitionMenu prohibitionMenu = DAOUtils
                        .findProhibitionMenuByIdAndClientId(session, allergenId, client.getIdOfClient());
                if (null == prohibitionMenu) {
                    throw new IllegalArgumentException(
                            String.format("Unable to find prohibitionMenu by id = %d and idOfClient = %d", allergenId,
                                    client.getIdOfClient()));
                }
                prohibitionMenu.setDeletedState(true);
                prohibitionMenu.setUpdateDate(new Date());
                prohibitionMenu.setVersion(DAOUtils.nextVersionByProhibitionsMenu(session));
                session.save(prohibitionMenu);
            } else {

                ProhibitionMenu prohibitionMenu = new ProhibitionMenu();
                prohibitionMenu.setVersion(DAOUtils.nextVersionByProhibitionsMenu(session));
                prohibitionMenu.setClient(client);
                prohibitionMenu.setCreateDate(new Date());
                prohibitionMenu.setFilterText(DAOUtils.findMenudetailNameByIdOfMenudetail(session, allergenId));
                prohibitionMenu.setProhibitionFilterType(ProhibitionFilterType.PROHIBITION_BY_GOODS_NAME);
                prohibitionMenu.setDeletedState(false);
                session.save(prohibitionMenu);
            }

            transaction.commit();
            transaction = null;

            result.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_OK.toString());
            result.setServerTimestamp(new Date());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        } catch (NoResultException e) {
            logger.error(e.getMessage(), e);
            result.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            result.setServerTimestamp(new Date());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            result.setServerTimestamp(new Date());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Path(value = "/netrika/mobile/v1/enterEvents")
    public Response enterEvent(@Context HttpServletRequest request) throws Exception {
        ResponseEnterEvent responseEnterEvent = new ResponseEnterEvent();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        try {
            String regID = extractParamByName(request.getParameterMap(), REG_ID, true);
            String dateFrom = extractParamByName(request.getParameterMap(), DATE_FROM, true);
            String dateTo = extractParamByName(request.getParameterMap(), DATE_TO, true);
            
            Date dateFromD = new SimpleDateFormat("yyyy-MM-dd").parse(dateFrom);
            Date dateToD = new SimpleDateFormat("yyyy-MM-dd").parse(dateTo);

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            responseEnterEvent.setServerTimestamp(new Date());
            Client client = DAOUtils.findClientByIacregid(persistenceSession, regID);
            if (client == null) {
                throw new IllegalArgumentException("Client with regID = " + regID + " is not found");
            }
            List<EnterEvent> events = DAOUtils
                    .findEventsByIdOfClientBetweenTime(persistenceSession, client, dateFromD, dateToD);
            if (events.isEmpty()) {
                responseEnterEvent.setErrorCode(Long.toString(ResponseCodes.RC_OK.getCode()));
                responseEnterEvent.setErrorMessage(ResponseCodes.RC_OK.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(responseEnterEvent).build();
            }

            for (EnterEvent event : events) {
                if (event.getPassDirection() == EnterEvent.ENTRY || event.getPassDirection() == EnterEvent.EXIT
                        || event.getPassDirection() == EnterEvent.RE_ENTRY
                        || event.getPassDirection() == EnterEvent.RE_EXIT) {
                    responseEnterEvent.getEnterEvents().add(enterEventFilling(event));
                } else {
                    continue;
                }
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseEnterEvent.setErrorCode(Long.toString(ResponseCodes.RC_OK.getCode()));
            responseEnterEvent.setErrorMessage(ResponseCodes.RC_OK.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseEnterEvent).build();
        } catch (ParseException e) {
            logger.error(ERROR_DATE_FORMAT, e);
            responseEnterEvent.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
            responseEnterEvent.setErrorMessage(ERROR_REQUEST_PARAMETRS);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseEnterEvent).build();
        } catch (IllegalArgumentException e) {
            logger.error("Can't find client", e);
            responseEnterEvent.setErrorCode(Long.toString(ResponseCodes.RC_INTERNAL_ERROR.getCode()));
            responseEnterEvent.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseEnterEvent).build();
        } catch (Exception e) {
            logger.error("InternalError", e);
            responseEnterEvent.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
            responseEnterEvent.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(responseEnterEvent).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private EnterEventItem enterEventFilling(EnterEvent enterEvent) {
        EnterEventItem eventItem = new EnterEventItem();
        eventItem.setEvtDateTime(timeConverter(enterEvent.getEvtDateTime()));
        eventItem.setDirection(Integer.toString(enterEvent.getPassDirection()));
        if (enterEvent.getPassDirection() == 1) {
            enterEvent.setEnterName("Выход из здания");
        } else {
            enterEvent.setEnterName("Вход в здание");
        }
        eventItem.setName(enterEvent.getEnterName());
        eventItem.setAddress(enterEvent.getOrg().getAddress());
        eventItem.setShortNameInfoService(enterEvent.getOrg().getShortNameInfoService());
        return eventItem;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Path(value = "/netrika/mobile/v1/accounts")
    public Response Accounts(@Context HttpServletRequest request) throws Exception {
        ResponseAccounts responseAccounts = new ResponseAccounts();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            String regID = extractParamByName(request.getParameterMap(), REG_ID, true);
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            responseAccounts.setServerTimestamp(new Date());
            Client client = DAOUtils.findClientByIacregid(persistenceSession, regID);
            if (client == null) {
                throw new IllegalArgumentException("Client with regID = " + regID + " is not found");
            } else {
                responseAccounts.getAccounts().add(accountsFilling(client));
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseAccounts.setErrorCode(Long.toString(ResponseCodes.RC_OK.getCode()));
            responseAccounts.setErrorMessage(ResponseCodes.RC_OK.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseAccounts).build();
        } catch (ParseException e) {
            logger.error(ERROR_DATE_FORMAT, e);
            responseAccounts.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
            responseAccounts.setErrorMessage(ERROR_REQUEST_PARAMETRS);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseAccounts).build();
        } catch (IllegalArgumentException e) {
            logger.error("Can't find client", e);
            responseAccounts.setErrorCode(Long.toString(ResponseCodes.RC_INTERNAL_ERROR.getCode()));
            responseAccounts.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseAccounts).build();
        } catch (Exception e) {
            logger.error("InternalError", e);
            responseAccounts.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
            responseAccounts.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(responseAccounts).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private AccountsItem accountsFilling(Client client) {
        AccountsItem accountsItem = new AccountsItem();
        accountsItem.setId(Long.toString(client.getContractId()));
        accountsItem.setSum(Double.toString(client.getBalance().doubleValue() / 100));
        accountsItem.setAccouttypename(accountsItem.getAccouttypename());
        accountsItem.setAccounttypeid(accountsItem.getAccounttypeid());
        return accountsItem;
    }

    private String extractParamByName(Map<String, String[]> parametersMap, String name, Boolean mandatory) {
        for (String key : parametersMap.keySet()) {
            String tmpString = key.toLowerCase();
            if (!tmpString.equals(name)) {
                continue;
            }
            String[] paramValue = parametersMap.get(key);
            if (paramValue.length > 1 || paramValue.length == 0 && mandatory) {
                throw new IllegalArgumentException("Unexpected " + name + " size");
            }
            return paramValue[0];
        }
        return null;
    }

}
