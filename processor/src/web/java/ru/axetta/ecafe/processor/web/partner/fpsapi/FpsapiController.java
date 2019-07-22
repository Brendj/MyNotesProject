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
import java.util.Date;
import java.util.List;

@Path(value = "")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Controller
public class FpsapiController {

    private Logger logger = LoggerFactory.getLogger(FpsapiController.class);

    @GET
    @Path(value = "/netrika/mobile/v1/sales")
    public Response getSales(@QueryParam(value = "regID") String regID, @QueryParam(value = "DateFrom") Long dateFrom,
            @QueryParam(value = "DateTo") Long dateTo) throws Exception {
        ResponseSales responseSales = new ResponseSales();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        //Вычисление результата запроса
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            responseSales.setserverTimestamp(new Date());
            Client client = DAOUtils.findClientByIacregid(persistenceSession, regID);
            if (client == null) {
                throw new IllegalArgumentException("Client with regID = " + regID + " is not found");
            }

            List<Order> orders = DAOUtils
                    .findOrdersbyIdofclientandBetweenTime(persistenceSession, client, new Date(dateFrom),
                            new Date(dateTo));
            if (orders.isEmpty()) {
                return resultOK(null);
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
                                responseSales.getSales().add(setParametrs(order, orderDetail));
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
                                responseSales.getSales().add(setParametrs(order, orderDetail));
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
            return resultBadArgs(responseSales);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

    }

    private SalesItem setParametrs (Order order, OrderDetail orderDetail)
    {
        SalesItem salesItem = new SalesItem();
        salesItem.setId(order.getCompositeIdOfOrder().getIdOfOrder());
        salesItem.setDate_creation(order.getCreateTime());
        salesItem.setProductid(orderDetail.getCompositeIdOfOrderDetail().getIdOfOrderDetail());
        salesItem.setProductname(orderDetail.getMenuDetailName());
        salesItem.setQuantity(orderDetail.getQty());
        salesItem.setSum(orderDetail.getQty() * orderDetail.getRPrice());
        salesItem.setDiscount(orderDetail.getDiscount());
        /////////
        if (order.getState() == 1) {
            salesItem.setRemoved(order.getTransaction().getTransactionTime());
        }
        /////////
        salesItem.setAccount_type(orderDetail.getMenuType());
        if (SalesOrderType.HOT_FOOD.getCode() == orderDetail.getMenuType()) {
            salesItem.setAccount_name(SalesOrderType.HOT_FOOD.getDescription());
        }
        if (SalesOrderType.BUFFET.getCode() == orderDetail.getMenuType()) {
            salesItem.setAccount_name(SalesOrderType.BUFFET.getDescription());
        }
        salesItem.setTransactionid(order.getTransaction().getIdOfTransaction());
        return salesItem;
    }

    private Response resultOK(ResponseSales result) {
        result.getResult().resultCode = ResponseCodes.RC_OK.getCode();
        result.getResult().description = ResponseCodes.RC_OK.toString();
        return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
    }

    private Response resultBadArgs(ResponseSales result) {
        result.getResult().resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
        result.getResult().description = ResponseCodes.RC_INTERNAL_ERROR.toString();
        return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
    }

}
