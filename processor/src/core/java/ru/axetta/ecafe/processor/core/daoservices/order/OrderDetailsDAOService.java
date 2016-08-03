/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.ClientReportItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem1;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampElectronicCollationReportItem;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.RegistryTalon;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.RegistryTalonType;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.04.13
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class OrderDetailsDAOService extends AbstractDAOService {

    @SuppressWarnings("unchecked")
    public Long buildRegisterStampBodyValue(Long idOfOrg, Date start, String fullname, boolean includeActDiscrepancies) {
        // todo привести к единому виду способ отнесения заказа к льготной или платной группе
        String sql ="select sum(orderdetail.qty) "
                + " from cf_orders cforder "
                + "     left join cf_orderdetails orderdetail on orderdetail.idoforg = cforder.idoforg and orderdetail.idoforder = cforder.idoforder"
                + "     left join cf_goods good on good.idofgood = orderdetail.idofgood"
                + " where cforder.state=0 "
                + "     and orderdetail.state=0 "
                + "     and cforder.createddate>=:startDate "
                + "     and cforder.createddate<=:endDate "
                + "     and orderdetail.socdiscount>0 "
                + "     and cforder.idoforg=:idoforg "
                + "     and good.fullname like '"+fullname+"' "
                + "     and orderdetail.menutype>=:mintype "
                + "     and orderdetail.menutype<=:maxtype "
                + "     and (cforder.ordertype in (4,6,10) or (cforder.ordertype=8"
                + (includeActDiscrepancies ?" ":" and orderdetail.qty>=0 ") + " )) ";
        Query query = getSession().createSQLQuery(sql);
        query.setParameter("idoforg",idOfOrg);
        query.setParameter("mintype",OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype",OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate",start.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.DATE, 1);
        long endTime = calendar.getTimeInMillis()-1;
        query.setParameter("endDate", endTime);
        List list = query.list();
        if(list==null || list.isEmpty() || list.get(0)==null){
            return  0L;
        } else {
            return new Long(list.get(0).toString());
        }
    }

    @SuppressWarnings("unchecked")
    public Long buildRegisterStampBodyValuePaid(Long idOfOrg, Date start, String fullname,
            boolean includeActDiscrepancies) {
        String sql = "select sum(orderdetail.qty) from cf_orders cforder" +
                " left join cf_orderdetails orderdetail on orderdetail.idoforg = cforder.idoforg " +
                " and orderdetail.idoforder = cforder.idoforder" +
                " left join cf_goods good on good.idofgood = orderdetail.idofgood" +
                " where cforder.state=0 and orderdetail.state=0 and cforder.createddate>=:startDate and cforder.createddate<=:endDate and" +
                " cforder.idoforg=:idoforg and good.fullname like '" + fullname + "' and " +
                " orderdetail.menutype>=:mintype and orderdetail.menutype<=:maxtype and " +
                " (cforder.ordertype=3 or (cforder.ordertype=8 " + (includeActDiscrepancies ? " "
                : " and orderdetail.qty>=0 ") + " )) ";
        Query query = getSession().createSQLQuery(sql);
        query.setParameter("idoforg", idOfOrg);
        query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate", start.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.DATE, 1);
        long endTime = calendar.getTimeInMillis() - 1;
        query.setParameter("endDate", endTime);
        List list = query.list();
        if (list == null || list.isEmpty() || list.get(0) == null) {
            return 0L;
        } else {
            return new Long(list.get(0).toString());
        }
    }

    @SuppressWarnings("unchecked")
    public Long buildRegisterStampBodyValueSubscriptionFeeding(Long idOfOrg, Date start, String fullname,
            boolean includeActDiscrepancies) {
        String sql = "select sum(orderdetail.qty) from cf_orders cforder" +
                " left join cf_orderdetails orderdetail on orderdetail.idoforg = cforder.idoforg " +
                " and orderdetail.idoforder = cforder.idoforder" +
                " left join cf_goods good on good.idofgood = orderdetail.idofgood" +
                " where cforder.state=0 and orderdetail.state=0 and cforder.createddate>=:startDate and cforder.createddate<=:endDate and" +
                " cforder.idoforg=:idoforg and good.fullname like '" + fullname + "' and " +
                " orderdetail.menutype>=:mintype and orderdetail.menutype<=:maxtype and " +
                " (cforder.ordertype=7 or (cforder.ordertype=8 " + (includeActDiscrepancies ? " "
                : " and orderdetail.qty>=0 ") + " )) ";
        Query query = getSession().createSQLQuery(sql);
        query.setParameter("idoforg", idOfOrg);
        query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate", start.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.DATE, 1);
        long endTime = calendar.getTimeInMillis() - 1;
        query.setParameter("endDate", endTime);
        List list = query.list();
        if (list == null || list.isEmpty() || list.get(0) == null) {
            return 0L;
        } else {
            return new Long(list.get(0).toString());
        }
    }

    /* Подсчет суточной пробы для льготного питания*/
    @SuppressWarnings("unchecked")
    public Long buildRegisterStampDailySampleValue(Long idOfOrg, Date start, Date end, String fullname) {
        String sql ="select sum(orderdetail.qty) from cf_orders cforder" +
                " left join cf_orderdetails orderdetail on orderdetail.idoforg = cforder.idoforg " +
                "   and orderdetail.idoforder = cforder.idoforder" +
                " left join cf_goods good on good.idofgood = orderdetail.idofgood" +
                " where cforder.state=0 and orderdetail.state=0 and cforder.createddate between :startDate and :endDate and orderdetail.socdiscount>0 and" +
                " orderdetail.menutype>=:mintype and orderdetail.menutype<=:maxtype and " +
                " cforder.idoforg=:idoforg and good.fullname like '"+fullname+"' and" +
                " cforder.ordertype in (5) ";
        Query query = getSession().createSQLQuery(sql);
        query.setParameter("idoforg",idOfOrg);
        query.setParameter("startDate",start.getTime());
        query.setParameter("endDate",end.getTime());
        query.setParameter("mintype",OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype",OrderDetail.TYPE_COMPLEX_MAX);
        Object res = query.uniqueResult();
        if (res==null) {
            return 0L;
        } else {
            return ((BigInteger) res).longValue();
        }
    }

    /* получаем список всех товаров для льготного питания */
    @SuppressWarnings("unchecked")
    public List<GoodItem> findAllGoods(Long idOfOrg, Date startTime, Date endTime, Set orderTypes){
        String sql = "select distinct good.globalId as globalId, "
                + "     good.parts as parts, "
                + "     good.fullName as fullName, "
                + "     case ord.orderType when 10 then 1 else 0 end as orderType "
                + " from OrderDetail details "
                + "     left join details.good good "
                + "     left join details.order ord "
                + "     left join ord.org o "
                + " where ord.state=0 "
                + "     and details.state=0 "
                + "     and ord.orderType in :orderType "
                + "     and details.good is not null "
                + "     and o.idOfOrg=:idOfOrg "
                + "     and ord.createTime between :startDate and :endDate "
                + "     and details.menuType >= :mintype "
                + "     and details.menuType <=:maxtype "
                + " order by fullName";
        Query query = getSession().createQuery(sql);
        query.setParameterList("orderType", orderTypes);
        query.setParameter("idOfOrg",idOfOrg);
        query.setParameter("mintype",OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype",OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate",startTime);
        query.setParameter("endDate", endTime);
        query.setResultTransformer(Transformers.aliasToBean(GoodItem.class));
        return  (List<GoodItem>) query.list();
    }

    /* получаем список всех товаров для льготного питания */
    @SuppressWarnings("unchecked")
    public List<GoodItem> findAllGoodsElectronicCollation(Long idOfOrg, Date startTime, Date endTime) {
        List<GoodItem> result = new ArrayList<GoodItem>();

        String sql = "SELECT compositeIdOfTaloonApproval.taloonName AS taloon_2 FROM TaloonApproval WHERE  org.idOfOrg = :idOfOrg AND deletedState = false AND (compositeIdOfTaloonApproval.taloonDate BETWEEN :startDate AND :endDate) ORDER BY taloon_2";
        Query query = getSession().createQuery(sql);
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("startDate", startTime);
        query.setParameter("endDate", endTime);

        List resList = query.list();

        for (Object obj: resList) {
            String str = (String) obj;

            GoodItem goodItem = new GoodItem();
            goodItem.setFullName(str);
            goodItem.setParts(str.split("/"));

            result.add(goodItem);
        }

        return result;
    }

    // для вывода сообщ

    public boolean findNotConfirmedTaloons(Session session, Date startDate, Date endDate, Long idOfOrg)  {

        boolean b = false;

        String sql = "SELECT compositeIdOfTaloonApproval.taloonName AS taloon_2 FROM TaloonApproval WHERE  org.idOfOrg = :idOfOrg AND deletedState = false AND (compositeIdOfTaloonApproval.taloonDate BETWEEN :startDate AND :endDate) AND (isppState in (0) OR ppState in (0,2)) ORDER BY taloon_2";
        Query query = getSession().createQuery(sql);
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        List resTaloonError = query.list();

        if (!resTaloonError.isEmpty()) {
            b = true;
        }

        return b;
    }


    public List<RegisterStampElectronicCollationReportItem> findAllRegisterStampElectronicCollationItems(Long idOfOrg, Date startTime, Date endTime) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");

        List<RegisterStampElectronicCollationReportItem> result = new ArrayList<RegisterStampElectronicCollationReportItem>();

        String sql = "SELECT compositeIdOfTaloonApproval.taloonName AS taloon_2, soldedQty, compositeIdOfTaloonApproval.taloonDate, taloonNumber FROM TaloonApproval WHERE  org.idOfOrg = :idOfOrg AND deletedState = false AND (compositeIdOfTaloonApproval.taloonDate BETWEEN :startDate AND :endDate) ORDER BY taloon_2";
        Query query = getSession().createQuery(sql);
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("startDate", startTime);
        query.setParameter("endDate", endTime);

        List resList = query.list();

        String [] parts;

        String pathPart1 = "";
        String pathPart2 = "";
        String pathPart3 = "";
        String pathPart4 = "";

        Long qty = 0L;

        String date;

        String number;

        Date dateTime;

        RegisterStampElectronicCollationReportItem registerStampElectronicCollationReportItem;

        for (Object obj: resList) {
            Object [] objects = (Object[]) obj;

            parts = objects[0].toString().split("/");

            if(parts.length>0) pathPart1 = parts[0];
            if(parts.length>1) pathPart2 = parts[1];
            if(parts.length>2) pathPart3 = parts[2];
            if(parts.length>3) pathPart4 = parts[3];

            qty = ((Integer) objects[1]).longValue();

            date = timeFormat.format(objects[2]);

            if (objects.length > 3) {
                number = objects[3] == null ? "" : String.valueOf(objects[3]);
            } else {
                number = "";
            }

            dateTime = (Date) objects[2];

            registerStampElectronicCollationReportItem = new RegisterStampElectronicCollationReportItem(qty, date, number, dateTime, pathPart1, pathPart2, pathPart3, pathPart4);

            result.add(registerStampElectronicCollationReportItem);
        }

        return result;
    }

    public Set<OrderTypeEnumType> getReducedPaymentOrderTypesWithDailySample() {
        Set<OrderTypeEnumType> orderTypeEnumTypeSet = new HashSet<OrderTypeEnumType>();
        orderTypeEnumTypeSet.add(OrderTypeEnumType.REDUCED_PRICE_PLAN);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.DAILY_SAMPLE);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.CORRECTION_TYPE);
        //orderTypeEnumTypeSet.add(OrderTypeEnumType.WATER_ACCOUNTING);
        return orderTypeEnumTypeSet;
    }

    public Set<OrderTypeEnumType> getWaterAccountingOrderTypesWithDailySample() {
        Set<OrderTypeEnumType> orderTypeEnumTypeSet = new HashSet<OrderTypeEnumType>();
        orderTypeEnumTypeSet.add(OrderTypeEnumType.WATER_ACCOUNTING);
        return orderTypeEnumTypeSet;
    }

    /* получаем список всех товаров для платного питания */
    @SuppressWarnings("unchecked")
    public List<GoodItem1> findAllGoodsPaid(Long idOfOrg, Date startTime, Date endTime){
        Set<OrderTypeEnumType> orderTypeEnumTypeSet = new HashSet<OrderTypeEnumType>();
        orderTypeEnumTypeSet.add(OrderTypeEnumType.PAY_PLAN);
        String sql = "select distinct good.globalId as globalId, good.pathPart3 as pathPart3, "
                + " good.pathPart4 as pathPart4,good.pathPart2 as pathPart2, good.pathPart1 as pathPart1, good.fullName as fullName "
                + " , details.RPrice as price"
                + " from OrderDetail details "
                + " left join details.good good left join details.order ord left join ord.org o "
                + " where ord.state=0 and details.state=0 and ord.orderType in :orderType and details.good is not null and o.idOfOrg=:idOfOrg and "
                + " ord.createTime between :startDate and :endDate and "
                + " details.menuType >= :mintype and details.menuType <=:maxtype order by fullName";
        Query query = getSession().createQuery(sql);
        query.setParameterList("orderType",orderTypeEnumTypeSet);
        query.setParameter("idOfOrg",idOfOrg);
        query.setParameter("mintype",OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype",OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate",startTime);
        query.setParameter("endDate", endTime);
        query.setResultTransformer(Transformers.aliasToBean(GoodItem1.class));
        return  (List<GoodItem1>) query.list();
    }

    /* получаем список всех товаров для абонементного питания питания */
    @SuppressWarnings("unchecked")
    public List<GoodItem1> findAllGoodsSubscriptionFeeding(Long idOfOrg, Date startTime, Date endTime){
        Set<OrderTypeEnumType> orderTypeEnumTypeSet = new HashSet<OrderTypeEnumType>();
        orderTypeEnumTypeSet.add(OrderTypeEnumType.SUBSCRIPTION_FEEDING);
        String sql = "select distinct good.globalId as globalId, good.pathPart3 as pathPart3, "
                + " good.pathPart4 as pathPart4,good.pathPart2 as pathPart2, good.pathPart1 as pathPart1, good.fullName as fullName "
                + " , details.RPrice as price"
                + " from OrderDetail details "
                + " left join details.good good left join details.order ord left join ord.org o "
                + " where ord.state=0 and details.state=0 and ord.orderType in :orderType and details.good is not null and o.idOfOrg=:idOfOrg and "
                + " ord.createTime between :startDate and :endDate and "
                + " details.menuType >= :mintype and details.menuType <=:maxtype order by fullName";
        Query query = getSession().createQuery(sql);
        query.setParameterList("orderType",orderTypeEnumTypeSet);
        query.setParameter("idOfOrg",idOfOrg);
        query.setParameter("mintype",OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype",OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate",startTime);
        query.setParameter("endDate", endTime);
        query.setResultTransformer(Transformers.aliasToBean(GoodItem1.class));
        return  (List<GoodItem1>) query.list();
    }

    /* получение карты по талонам */
    @SuppressWarnings("uncheked")
    public Map<Date, Long> findAllRegistryTalons(Long idOfOrg, Date startTime, Date endTime) {
        Criteria criteria = getSession().createCriteria(RegistryTalon.class);
        criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        criteria.add(Restrictions.between("talonDate", startTime, endTime));
        criteria.add(Restrictions.eq("talonType", RegistryTalonType.Benefit_Plan));
        criteria.add(Restrictions.eq("deletedState", false));
        List list = criteria.list();

        Map<Date, Long> map = new HashMap<Date, Long>();
        for (Object lst : list) {
            RegistryTalon curr = (RegistryTalon) lst;
            Calendar calendar = Calendar.getInstance(RuntimeContext.getInstance().getLocalTimeZone(null));
            calendar.setTime(curr.getTalonDate());
            CalendarUtils.truncateToDayOfMonth(calendar);
            map.put(calendar.getTime(), curr.getNumber());
        }
        return map;
    }

    @SuppressWarnings("uncheked")
    public Map<Date, Long> findAllRegistryTalonsPaid(Long idOfOrg, Date startTime, Date endTime) {
        Criteria criteria = getSession().createCriteria(RegistryTalon.class);
        criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        criteria.add(Restrictions.between("talonDate", startTime, endTime));
        criteria.add(Restrictions.eq("talonType", RegistryTalonType.Pay_Plan));
        criteria.add(Restrictions.eq("deletedState", false));
        List list = criteria.list();

        Map<Date, Long> map = new HashMap<Date, Long>();
        for (Object lst : list) {
            RegistryTalon curr = (RegistryTalon) lst;
            Calendar calendar = Calendar.getInstance(RuntimeContext.getInstance().getLocalTimeZone(null));
            calendar.setTime(curr.getTalonDate());
            CalendarUtils.truncateToDayOfMonth(calendar);
            map.put(calendar.getTime(), curr.getNumber());
        }
        return map;
    }

    @SuppressWarnings("uncheked")
    public Map<Date, Long> findAllRegistryTalonsSubscriptionFeeding(Long idOfOrg, Date startTime, Date endTime) {
        Criteria criteria = getSession().createCriteria(RegistryTalon.class);
        criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        criteria.add(Restrictions.between("talonDate", startTime, endTime));
        criteria.add(Restrictions.eq("talonType", RegistryTalonType.Subscriber_Feeding_Plan));
        criteria.add(Restrictions.eq("deletedState", false));
        List list = criteria.list();

        Map<Date, Long> map = new HashMap<Date, Long>();
        for (Object lst : list) {
            RegistryTalon curr = (RegistryTalon) lst;
            Calendar calendar = Calendar.getInstance(RuntimeContext.getInstance().getLocalTimeZone(null));
            calendar.setTime(curr.getTalonDate());
            CalendarUtils.truncateToDayOfMonth(calendar);
            map.put(calendar.getTime(), curr.getNumber());
        }
        return map;
    }

    public List<ClientReportItem> fetchClientReportItem(Date startDate, Date endDate, Long idOfOrg){
        List<ClientReportItem> clientReportItems = new LinkedList<ClientReportItem>();
        Criteria criteriaOrder = getSession().createCriteria(Order.class);
        if(idOfOrg!=null) {
            criteriaOrder.add(Restrictions.eq("compositeIdOfOrder.idOfOrg", idOfOrg));
        }
        criteriaOrder.add(Restrictions.eq("state", 0));
        criteriaOrder.add(Restrictions.between("createTime", startDate, endDate));
        criteriaOrder.add(Restrictions.gt("sumByCard", 0L));
        criteriaOrder.addOrder(org.hibernate.criterion.Order.asc("compositeIdOfOrder.idOfOrder"));
        List result =criteriaOrder.list();
        for (Object o: result){
            Order order = (Order) o;
            Client client = order.getClient();
            if(order.getSumByCash()>0){
                float sumByCash = order.getSumByCash() / 100.0f;
                Set<OrderDetail> details = order.getOrderDetails();
                for (OrderDetail detail: details){
                    if(detail.getState()!=0) continue;
                    Long idOfOrderDetail = detail.getCompositeIdOfOrderDetail().getIdOfOrderDetail();
                    Long contractId = client.getContractId();
                    String fullName = client.getPerson().getFullName();
                    Integer menuOrigin = detail.getMenuOrigin();
                    String menuName = detail.getMenuDetailName();
                    Float price = detail.getRPrice() / 100f;
                    Float discount = detail.getDiscount() / 100f;
                    Long quantity = detail.getQty();
                    Float totalDetailSum = (price - discount) * quantity;
                    if(totalDetailSum - sumByCash <= 0){
                        sumByCash=sumByCash-totalDetailSum;
                    } else {
                        totalDetailSum = totalDetailSum - sumByCash;
                        sumByCash=0L;
                        clientReportItems.add(new ClientReportItem(idOfOrderDetail, contractId, fullName, menuName, OrderDetail.getMenuOriginAsString(menuOrigin), totalDetailSum));
                    }
                }
            } else {
                Set<OrderDetail> details = order.getOrderDetails();
                for (OrderDetail detail: details){
                    if(detail.getState()==1) continue;
                    Long idOfOrderDetail = detail.getCompositeIdOfOrderDetail().getIdOfOrderDetail();
                    Long contractId = client.getContractId();
                    String fullName = client.getPerson().getFullName();
                    Integer menuOrigin = detail.getMenuOrigin();
                    String menuName = detail.getMenuDetailName();
                    Float price = detail.getRPrice() / 100f;
                    Float discount = detail.getDiscount() / 100f;
                    Long quantity = detail.getQty();
                    Float totalDetailSum = (price - discount) * quantity;
                    clientReportItems.add(new ClientReportItem(idOfOrderDetail, contractId, fullName, menuName, OrderDetail.getMenuOriginAsString(menuOrigin), totalDetailSum));
                }
            }
        }
        return clientReportItems;
    }

}
