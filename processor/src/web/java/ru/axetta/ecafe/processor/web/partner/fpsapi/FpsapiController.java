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
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

            responseSales.setserverTimestamp(timeConverter(new Date()));
            Client client = DAOUtils.findClientByIacregid(persistenceSession, regID);
            if (client == null) {
                throw new IllegalArgumentException("Client with regID = " + regID + " is not found");
            }

            List<Order> orders = DAOUtils
                    .findOrdersbyIdofclientandBetweenTime(persistenceSession, client, new Date(dateFrom),
                            new Date(dateTo));
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
            return resultBadArgs(responseSales);
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
        /////////
        if (order.getState() == 1) {
            salesItem.setRemoved(timeConverter(order.getTransaction().getTransactionTime()));
        }
        /////////
        if (SalesOrderType.HOT_FOOD.getCode() == salesOrderType.getCode()) {
            salesItem.setAccount_type(SalesOrderType.HOT_FOOD.getCode());
            salesItem.setAccount_name(SalesOrderType.HOT_FOOD.getDescription());
        }
        if (SalesOrderType.BUFFET.getCode() == salesOrderType.getCode()) {
            salesItem.setAccount_type(SalesOrderType.BUFFET.getCode());
            salesItem.setAccount_name(SalesOrderType.BUFFET.getDescription());
        }
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

    private Response resultBadArgs(ResponseSales result) {
        result.getResult().resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
        result.getResult().description = ResponseCodes.RC_INTERNAL_ERROR.toString();
        return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(result).build();
    }

    @Path("/netrika/mobile/v1/allergens")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response getAllergens(@QueryParam(value = "RegId") String regId) {
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
                + "md.groupname, p.idofprohibitions is not null as active "
                + "from cf_menu m "
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
            allergenList.add(new Allergen(idOfProhibition, filterText, typeIdHashMap.get(groupName), groupName, active));
        }
        return allergenList;
    }

    @Path("/netrika/mobile/v1/allergens/create")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response createAllergen(@QueryParam(value = "RegId") String regId,
            @QueryParam(value = "AllergenId") Long allergenId, @QueryParam(value = "Active") Integer active) {
        Result result = new Result();

        Session session = null;
        Transaction transaction = null;
        try {
            if (StringUtils.isEmpty(regId) || (null == allergenId) || (null == active)) {
                throw new IllegalArgumentException("Couldn't find all parameters");
            }
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = DAOUtils.findClientByIacregid(session, regId);

            if (null == client) {
                throw new IllegalArgumentException(String.format("Unable to find client by regId=%s", regId));
            }

            if (0 == active) {
                ProhibitionMenu prohibitionMenu = DAOUtils.findProhibitionMenuByIdAndClientId(session, allergenId, client.getIdOfClient());
                if (null == prohibitionMenu) {
                    throw new IllegalArgumentException(String.format("Unable to find prohibitionMenu by id = %d and idOfClient = %d", allergenId, client.getIdOfClient()));
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

}
