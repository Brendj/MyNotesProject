/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.sync.response.AccountTransactionExtended;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.jboss.as.web.security.SecurityContextAssociationValve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 12.05.16
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
@Transactional
public class DAOReadonlyService {
    private final static Logger logger = LoggerFactory.getLogger(DAOReadonlyService.class);

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    public static DAOReadonlyService getInstance() {
        return RuntimeContext.getAppContext().getBean(DAOReadonlyService.class);
    }

    public List<AccountTransactionExtended> getAccountTransactionsForOrgSinceTimeV2(Org org,
            Date fromDateTime, Date toDateTime) {
        String str_query = "select t.idOfTransaction, t.source, t.transactionDate, " +
                "t.sourceType, t.transactionSum,  " +
                "coalesce(t.transactionSubBalance1Sum, 0) as transactionSubBalance1Sum, coalesce(query.complexsum, 0) as complexsum, " +
                "coalesce(query.discountsum, 0) as discountsum, coalesce(query.orderType, 0) as ordertype, t.idOfClient " +
                "from cf_transactions t left join " +
                "(select coalesce(sum(dd.qty * dd.rprice), 0) as complexsum, coalesce(sum(dd.socDiscount), 0) as discountsum, oo.orderType, oo.idOfTransaction " +
                "from cf_orders oo join cf_orderdetails dd on oo.idOfOrder = dd.idOfOrder and oo.idOfOrg = dd.idOfOrg " +
                "where oo.createdDate > :orders_begDate AND oo.createddate <= :orders_endDate AND oo.idOfOrg in (:orgs) " +
                "AND dd.idOfOrg in (:orgs) AND dd.menuType between :menuMin and :menuMax " +
                "group by oo.orderType, oo.idOfTransaction) as query " +
                "on t.idOfTransaction = query.idOfTransaction " +
                "where t.idOfOrg in (:orgs) AND t.transactionDate > :trans_begDate AND t.transactionDate <= :trans_endDate " +
                "order by t.idOfClient";
        Session session = entityManager.unwrap(Session.class);
        org = (Org) session.merge(org);
        SQLQuery q = session.createSQLQuery(str_query);
        // заказы будем искать за последние 24 часа от времени запроса
        q.setParameter("orders_begDate", CalendarUtils.addDays(toDateTime, -1).getTime());
        q.setParameter("orders_endDate",toDateTime.getTime());
        // транзакции будем искать строго от запрашиваемого времени
        q.setParameter("trans_begDate", fromDateTime.getTime());
        q.setParameter("trans_endDate", toDateTime.getTime());
        q.setParameterList("orgs", org.getFriendlyOrg());
        q.setParameter("menuMin", OrderDetail.TYPE_COMPLEX_MIN);
        q.setParameter("menuMax", OrderDetail.TYPE_COMPLEX_MAX);
        q.setResultTransformer(Transformers.aliasToBean(AccountTransactionExtended.class));
        q.addScalar("idoftransaction").addScalar("source").addScalar("transactiondate").addScalar("sourcetype").addScalar("transactionsum").addScalar("transactionsubbalance1sum")
                .addScalar("complexsum", StandardBasicTypes.BIG_DECIMAL).addScalar("discountsum", StandardBasicTypes.BIG_DECIMAL).addScalar("ordertype")
                .addScalar("idofclient");
        return q.list();
    }

    public Org findOrg(Long idOfOrg) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        Org org = (Org) session.get(Org.class, idOfOrg);
        if (null == org) {
            final String message = String.format("Unknown org with IdOfOrg == %s", idOfOrg);
            logger.error(message);
            throw new NullPointerException(message);
        }
        return org;
    }

    public List<Long> getClientsIdsWhereHasEnterEvents(long idOfOrg, Date beginDate, Date endDate) {
        Session session = entityManager.unwrap(Session.class);
        SQLQuery query = session.createSQLQuery(" SELECT ee.idofclient " +
                " FROM cf_enterevents ee " +
                " WHERE ee.idoforg = :idOfOrg  AND ee.evtdatetime BETWEEN :beginDate AND :endDate " +
                " AND ee.idofclient IS NOT NULL AND ee.PassDirection IN (:passDirections)");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("beginDate", beginDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        query.setParameterList("passDirections",
                Arrays.asList(EnterEvent.ENTRY, EnterEvent.EXIT, EnterEvent.PASSAGE_RUFUSAL, EnterEvent.RE_ENTRY,
                        EnterEvent.RE_EXIT, EnterEvent.DETECTED_INSIDE, EnterEvent.CHECKED_BY_TEACHER_EXT, EnterEvent.CHECKED_BY_TEACHER_INT));
        query.addScalar("idofclient", StandardBasicTypes.LONG);
        List result = query.list();
        if (result == null)
            return new ArrayList<Long>();
        return result;
    }

    public User getUserFromSession() {
        try {
            HttpServletRequest request = SecurityContextAssociationValve.getActiveRequest().getRequest();
            HttpSession httpSession = request.getSession(true);
            Long idOfUser = (Long)httpSession.getAttribute(User.USER_ID_ATTRIBUTE_NAME);
            User user = findUserById(idOfUser);
            return user;
        } catch (Exception e) {
            //logger.error(String.format("Can't retrieve user from current Session. Message: %s", e.getMessage()));
            return null;
        }
    }

    public User findUserById(long idOfUser) throws Exception {
        User user = entityManager.find(User.class, idOfUser);
        return user;
    }

    public Order findOrder(Long idOfOrg, Long idOfOrder) {
        Order order = entityManager.find(Order.class, new CompositeIdOfOrder(idOfOrg, idOfOrder));
        return order;
    }
}
