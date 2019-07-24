/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Path(value = "")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Controller
public class FpsapiController {

    private Logger logger = LoggerFactory.getLogger(FpsapiController.class);

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
                return resultBadArgs(responseSales, 2);
            }

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            responseSales.setserverTimestamp(timeConverter(new Date()));
            Client client = DAOUtils.findClientByIacregid(persistenceSession, regID);
            if (client == null) {
                throw new IllegalArgumentException("Client with regID = " + regID + " is not found");
            }

            List<Order> orders = DAOUtils
                    .findOrdersbyIdofclientandBetweenTime(persistenceSession, client, dateFromD,
                            dateToD);
            if (orders.isEmpty()) {
                return resultOK(responseSales);
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
            return resultOK(responseSales);
        } catch (IllegalArgumentException e) {
            logger.error("Can't find client", e);
            return resultBadArgs(responseSales, 1);
        } catch (Exception e) {
            logger.error("InternalError", e);
            return resultError(responseSales);
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
        if (order.getState() == 1) {
            salesItem.setRemoved(timeConverter(order.getTransaction().getTransactionTime()));
        }
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

    private Response resultOK(ResponseSales result) {
        result.getResult().resultCode = ResponseCodes.RC_OK.getCode();
        result.getResult().description = ResponseCodes.RC_OK.toString();
        return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
    }

    private Response resultBadArgs(ResponseSales result, Integer type) {
        if (type == 1) {
            result.getResult().resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
            result.getResult().description = ResponseCodes.RC_INTERNAL_ERROR.toString();
        }
        if (type == 2) {
            result.getResult().resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
            result.getResult().description = "Переданы некорректные параметры";
        }
        return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
    }

    private Response resultError(ResponseSales result) {
        result.getResult().resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
        result.getResult().description = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
        return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(result).build();
    }

}
