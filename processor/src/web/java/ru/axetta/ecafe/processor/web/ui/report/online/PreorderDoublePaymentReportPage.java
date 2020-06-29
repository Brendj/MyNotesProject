/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.PreorderComplex;
import ru.axetta.ecafe.processor.core.report.PreorderDoublePaymentReportItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Scope(value = "session")
public class PreorderDoublePaymentReportPage extends OnlineReportPage {
    private final static Logger logger = LoggerFactory.getLogger(PreorderDoublePaymentReportPage.class);
    private List<PreorderDoublePaymentReportItem> items = new ArrayList<>();

    public PreorderDoublePaymentReportPage() {
        super();
        Date currentDate = new Date();
        startDate = CalendarUtils.getFirstDayOfMonth(currentDate);
        startDate = CalendarUtils.startOfDay(startDate);

        endDate = CalendarUtils.getLastDayOfMonth(currentDate);
        endDate = CalendarUtils.endOfDay(endDate);
    }

    public void reload() {
        items.clear();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            Query query = session.createSQLQuery("select ctg.contragentName, "
                    + "pc.preorderDate, "
                    + "pc.idOfPreorderComplex, "
                    + "pc.idofclient, "
                    + "p.surname, "
                    + "p.firstname, "
                    + "p.secondname, "
                    + "cg.groupName, "
                    + "pc.complexName, "
                    + "pc.complexPrice, "
                    + "pc.amount, "
                    + "pc.usedSum, "
                    + "pc.guid "
                    + "from cf_preorder_complex pc join cf_orgs o on pc.idoforgoncreate = o.idoforg "
                    + "join cf_contragents ctg on o.defaultsupplier = ctg.idofcontragent "
                    + "join cf_clients c on pc.idofclient = c.idofclient "
                    + "join cf_persons p on c.idofperson = p.IdOfPerson "
                    + "join cf_clientgroups cg on c.idoforg = cg.idoforg and c.idofclientgroup = cg.idofclientgroup "
                    + "where pc.deletedState = 0 and pc.preorderDate between :startDate and :endDate "
                    + "and pc.modeOfAdd <> :mode and pc.amount > 0 and pc.usedAmount > pc.amount "
                    + "UNION "
                    + "select ctg.contragentName, "
                    + "pc.preorderDate, "
                    + "pc.idOfPreorderComplex, "
                    + "pc.idofclient, "
                    + "p.surname, "
                    + "p.firstname, "
                    + "p.secondname, "
                    + "cg.groupName, "
                    + "pc.complexName, "
                    + "pc.complexPrice, "
                    + "pc.amount, "
                    + "pc.usedSum, "
                    + "pc.guid "
                    + "from cf_preorder_complex pc join cf_orgs o on pc.idoforgoncreate = o.idoforg "
                    + "join cf_preorder_menudetail pmd on pc.idofpreordercomplex = pmd.idofpreordercomplex "
                    + "join cf_contragents ctg on o.defaultsupplier = ctg.idofcontragent "
                    + "join cf_clients c on pc.idofclient = c.idofclient "
                    + "join cf_persons p on c.idofperson = p.IdOfPerson "
                    + "join cf_clientgroups cg on c.idoforg = cg.idoforg and c.idofclientgroup = cg.idofclientgroup "
                    + "where pc.deletedState = 0 and pmd.deletedState = 0 and pc.preorderDate between :startDate and :endDate "
                    + "and pc.modeOfAdd = :mode and pmd.amount > 0 and pmd.usedAmount > pmd.amount "
                    + "order by 1, 2");
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameter("mode", PreorderComplex.COMPLEX_MODE_4);
            List list = query.list();
            for (Object o : list) {
                Object[] row = (Object[])o;
                String contragentName = (String) row[0];
                Date preorderDate = new Date(((BigInteger) row[1]).longValue());
                Long idOfPreorderComplex = ((BigInteger) row[2]).longValue();
                Long idOfClient = ((BigInteger) row[3]).longValue();
                String surname = (String) row[4];
                String firstName = (String) row[5];
                String secondName = (String) row[6];
                String groupName = (String) row[7];
                String complexName = (String) row[8];
                Long compexPrice = ((BigInteger) row[9]).longValue();
                Integer qty = (Integer) row[10];
                Long usedSum = ((BigInteger) row[11]).longValue();
                String guid = (String) row[12];
                String orderInfo = getOrderInfo(session, guid);
                PreorderDoublePaymentReportItem item = new PreorderDoublePaymentReportItem(contragentName, preorderDate, orderInfo,
                        idOfPreorderComplex, idOfClient, surname + " " + firstName + " " + secondName, groupName,
                        complexName, compexPrice * qty, usedSum);
                items.add(item);
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in build preorder double payment report: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private String getOrderInfo(Session session, String guid) {
        String result = "";
        Query query = session.createQuery("select o from Order o, PreorderLinkOD p "
                + "where p.idOfOrg = o.compositeIdOfOrder.idOfOrg and p.idOfOrder = o.compositeIdOfOrder.idOfOrder "
                + "and p.preorderGuid = :guid order by o.createTime");
        query.setParameter("guid", guid);
        List<Order> list = query.list();
        for (Order order : list) {
            String add = "[" + order.getCompositeIdOfOrder().getIdOfOrg() + ", " + order.getCompositeIdOfOrder().getIdOfOrder()
                    + "] - " + CalendarUtils.dateTimeToString(order.getOrderDate());
            result += add + "<br />";
        }
        if (result.length() > 0) result = result.substring(0, result.length() - 6);
        return result;
    }


    @Override
    public String getPageFilename() {
        return "report/online/preorder_double_payment";
    }

    public List<PreorderDoublePaymentReportItem> getItems() {
        return items;
    }

    public void setItems(List<PreorderDoublePaymentReportItem> items) {
        this.items = items;
    }
}
