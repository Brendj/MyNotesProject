/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.logic.CurrentPositionsManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.report.BasicReport;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 21.11.11
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class CurrentPositionsReportPage extends BasicWorkspacePage {

    public String getPageFilename() {
        return "report/online/current_positions_report";
    }

    private List<CurPosition> currentPositionList;

    public List<CurPosition> getCurrentPositionList() {
        return currentPositionList;
    }

    public void buildReport(Session session) throws Exception {
        Criteria currentPositionCriteria = session.createCriteria(CurrentPosition.class);
        currentPositionCriteria.addOrder(Order.asc("idOfPosition"));
        currentPositionCriteria.list();
        List list = currentPositionCriteria.list();
        currentPositionList = new ArrayList<CurPosition>();
        for (Object obj : list) {
            CurrentPosition currentPosition = (CurrentPosition) obj;
            currentPositionList.add(new CurPosition(currentPosition.getIdOfPosition(),
                    currentPosition.getIdOfContragentDebtor().getContragentName(),
                    currentPosition.getIdOfContragentCreditor().getContragentName(), currentPosition.getSumma()));
        }
    }

    private class OrderItem {
        private Long sumByCard;
        private Long budgetSum;
        private Contragent supplier;

        private OrderItem(Long sumByCard, Long budgetSum, Contragent supplier) {
            this.sumByCard = sumByCard;
            this.budgetSum = budgetSum;
            this.supplier = supplier;
        }

        public Long getSumByCard() {
            return sumByCard;
        }

        public Long getBudgetSum() {
            return budgetSum;
        }

        public Contragent getSupplier() {
            return supplier;
        }
    }

    private class ClientPaymentItem {

        Long paySum;
        Contragent payAgent;
        Contragent objectContragent;

        private ClientPaymentItem(Long paySum, Contragent payAgent, Contragent objectContragent) {
            this.paySum = paySum;
            this.payAgent = payAgent;
            this.objectContragent = objectContragent;
        }

        public Long getPaySum() {
            return paySum;
        }

        public Contragent getPayAgent() {
            return payAgent;
        }

        public Contragent getObjectContragent() {
            return objectContragent;
        }
    }

    public class SettlementItem {
        private Long summa;
        private Integer payerClassId;
        private Integer receiverClassId;
        private Contragent contragentPayer;
        private Contragent contragentReceiver;

        public SettlementItem(Long summa, Integer payerClassId, Integer receiverClassId, Contragent contragentPayer,
                Contragent contragentReceiver) {
            this.summa = summa;
            this.payerClassId = payerClassId;
            this.receiverClassId = receiverClassId;
            this.contragentPayer = contragentPayer;
            this.contragentReceiver = contragentReceiver;
        }

        public Long getSumma() {
            return summa;
        }

        public Integer getPayerClassId() {
            return payerClassId;
        }

        public Integer getReceiverClassId() {
            return receiverClassId;
        }

        public Contragent getContragentPayer() {
            return contragentPayer;
        }

        public Contragent getContragentReceiver() {
            return contragentReceiver;
        }
    }

    public class CurrentPositionData {
        private List<OrderItem> orderItemList;
        private List<ClientPaymentItem> clientPaymentItemList;
        private List<SettlementItem> settlementItemList;
        private List<AddPayment> addPaymentList;
        private Long internalServicesFromOperatorToClientSum;
        private boolean withOperator;
        private Contragent operatorContragent;
        private Contragent clientContragent;
        private Contragent budgetContragent;

        public CurrentPositionData(List<OrderItem> orderItemList,
                List<ClientPaymentItem> clientPaymentItemList, List<SettlementItem> settlementItemList,
                List<AddPayment> addPaymentList, Long internalServicesFromOperatorToClientSum,
                boolean withOperator, Contragent operatorContragent,
                Contragent clientContragent, Contragent budgetContragent) {
            this.orderItemList = orderItemList;
            this.clientPaymentItemList = clientPaymentItemList;
            this.settlementItemList = settlementItemList;
            this.addPaymentList = addPaymentList;
            this.internalServicesFromOperatorToClientSum = internalServicesFromOperatorToClientSum;
            this.withOperator = withOperator;
            this.operatorContragent = operatorContragent;
            this.clientContragent = clientContragent;
            this.budgetContragent = budgetContragent;
        }

        public List<OrderItem> getOrderItemList() {
            return orderItemList;
        }

        public List<ClientPaymentItem> getClientPaymentItemList() {
            return clientPaymentItemList;
        }

        public List<SettlementItem> getSettlementItemList() {
            return settlementItemList;
        }

        public List<AddPayment> getAddPaymentList() {
            return addPaymentList;
        }

        public boolean isWithOperator() {
            return withOperator;
        }

        public Contragent getOperatorContragent() {
            return operatorContragent;
        }

        public Contragent getClientContragent() {
            return clientContragent;
        }

        public Contragent getBudgetContragent() {
            return budgetContragent;
        }
    }

    public CurrentPositionData prepareCurrentPositionsData(Session session) {
        Criteria optionCriteria = session.createCriteria(Option.class);
        optionCriteria.add(Restrictions.eq("idOfOption", 2L));
        Option option = (Option) optionCriteria.uniqueResult();
        Boolean withOperator = option.getOptionText().equals("1");

        Criteria criteria = null;

        // Оператор
        criteria = session.createCriteria(Contragent.class);
        criteria.add(Restrictions.eq("classId", Contragent.OPERATOR));
        Contragent operatorContragent = (Contragent) criteria.uniqueResult();

        // Клиент
        criteria = session.createCriteria(Contragent.class);
        criteria.add(Restrictions.eq("classId", Contragent.CLIENT));
        Contragent clientContragent = (Contragent) criteria.uniqueResult();

        // Бюджет
        criteria = session.createCriteria(Contragent.class);
        criteria.add(Restrictions.eq("classId", Contragent.BUDGET));
        Contragent budgetContragent = (Contragent) criteria.uniqueResult();

        // CF_Orders
        Query query = session.createQuery("select o.contragent.idOfContragent, o.pos.idOfPos, sum(o.sumByCard), sum(o.socDiscount), sum(o.grantSum) "
                                           + "  from Order o where state="+ ru.axetta.ecafe.processor.core.persistence.Order.STATE_COMMITED
                                           + " group by o.contragent.idOfContragent, o.pos.idOfPos ");

        List orderList = query.list();
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        for (Object object : orderList) {
            Object[] objects = (Object[])object;
            Long idOfContragent = (Long) objects[0];
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            Long idOfPos = (Long) objects[1];
            POS pos = null;
            if (idOfPos != null)
                pos = (POS) session.load(POS.class, idOfPos);
            Long sumByCard = (Long) objects[2];
            Long sosDiscount = (Long) objects[3];
            Long grantSum = (Long) objects[4];
            Long budgetSum = sosDiscount + grantSum;
            Contragent supplier = null;
            if (pos != null)
                supplier = pos.getContragent();
            else
                supplier = contragent;
            orderItemList.add(new OrderItem(sumByCard, budgetSum, supplier));
        }


        orderList = null;

        // CF_ClientPayments
        query = session.createQuery("select cp.contragent.idOfContragent,"
                                  + "       cp.transaction.client.org.defaultSupplier.idOfContragent, sum(cp.paySum) "
                                  + "  from ClientPayment cp"
                                  + " group by cp.contragent.idOfContragent, "
                                  + "          cp.transaction.client.org.defaultSupplier.idOfContragent");
        List clientPaymentList = query.list();
        List<ClientPaymentItem> clientPaymentItemList = new ArrayList<ClientPaymentItem>();
        for (Object object : clientPaymentList) {
            Object[] objects = (Object[])object;
            Long idOfContragent = (Long) objects[0];
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            Long paySum = (Long) objects[2];
            Contragent objectContragent = null;
            if (withOperator) {
                objectContragent = operatorContragent;
            } else {
                idOfContragent = (Long) objects[1];
                objectContragent = (Contragent) session.load(Contragent.class, idOfContragent);
            }
            clientPaymentItemList.add(new ClientPaymentItem(paySum, contragent, objectContragent));
        }
        clientPaymentList = null;

        // CF_Settlements
        query = session.createQuery("select s.idOfContragentPayer.idOfContragent, "
                                  + "       s.idOfContragentReceiver.idOfContragent, sum(s.summa) "
                                  + "  from Settlement s"
                                  + " group by s.idOfContragentPayer.idOfContragent, "
                                  + "          s.idOfContragentReceiver.idOfContragent");
        List settlementList = query.list();
        List<SettlementItem> settlementItemList = new ArrayList<SettlementItem>();
        for (Object object : settlementList) {
            Object[] objects = (Object[])object;
            Long idOfContragent = (Long) objects[0];
            Contragent contragentPayer = (Contragent) session.load(Contragent.class, idOfContragent);
            idOfContragent = (Long) objects[1];
            Contragent contragentReceiver = (Contragent) session.load(Contragent.class, idOfContragent);
            Long summa = (Long) objects[2];
            settlementItemList.add(new SettlementItem(summa, contragentPayer.getClassId(),
                    contragentReceiver.getClassId(), contragentPayer, contragentReceiver));
        }
        settlementList = null;

        // CF_ADDPayments
        query = session.createQuery("select ap.contragentPayer.idOfContragent, "
                                  + "ap.contragentReceiver.idOfContragent, sum(ap.summa) "
                                  + "  from AddPayment ap"
                                  + " group by ap.contragentPayer.idOfContragent, "
                                  + "          ap.contragentReceiver.idOfContragent");
        List addPaymentObjectList = query.list();
        List<AddPayment> addPaymentList = new ArrayList<AddPayment>();
        for (Object object : addPaymentObjectList) {
            Object[] objects = (Object[])object;
            Long idOfContragent = (Long) objects[0];
            Contragent contragentPayer = (Contragent) session.load(Contragent.class, idOfContragent);
            idOfContragent = (Long) objects[1];
            Contragent contragentReceiver = (Contragent) session.load(Contragent.class, idOfContragent);
            Long summa = (Long) objects[2];
            AddPayment addPayment = new AddPayment();
            addPayment.setContragentPayer(contragentPayer);
            addPayment.setContragentReceiver(contragentReceiver);
            addPayment.setSumma(summa);
            addPaymentList.add(addPayment);
        }

        addPaymentObjectList = null;

        /// CF_ClientSms
        query = session.createQuery("select sum(price) from ClientSms");
        Long smsPayments = (Long)query.list().get(0);

        CurrentPositionData currentPositionData = new CurrentPositionData(orderItemList, clientPaymentItemList,
                settlementItemList, addPaymentList, smsPayments, withOperator, operatorContragent,
                clientContragent, budgetContragent);
        return currentPositionData;
    }

    public void countCurrentPositions(
            CurrentPositionsManager currentPositionsManager, CurrentPositionData currentPositionData) throws Exception {
        // CF_Orders
        for (OrderItem orderItem : currentPositionData.getOrderItemList()) {
            currentPositionsManager.changeOrderPosition(orderItem.getSumByCard(), orderItem.getBudgetSum(), orderItem.getSupplier());
        }

        // CF_ClientPayments
        for (ClientPaymentItem clientPaymentItem : currentPositionData.getClientPaymentItemList()) {
            Contragent payAgent = clientPaymentItem.getPayAgent();
            Long paySum = clientPaymentItem.getPaySum();
            Contragent objectContragent = clientPaymentItem.getObjectContragent();
            currentPositionsManager.changeClientPaymentPosition(payAgent, paySum, objectContragent);
        }

        // CF_Settlements
        for (SettlementItem settlementItem : currentPositionData.getSettlementItemList()) {
            currentPositionsManager
                    .changeSettlementPosition(settlementItem.getSumma(), settlementItem.getPayerClassId(),
                            settlementItem.getReceiverClassId(), settlementItem.getContragentPayer(),
                            settlementItem.getContragentReceiver());
        }

        // CF_ADDPayments
        for (AddPayment addPayment : currentPositionData.getAddPaymentList()) {
            currentPositionsManager.changeAddPaymentPosition(addPayment, addPayment.getSumma());
        }

        // internal services
        currentPositionsManager.changeCurrentPosition(-currentPositionData.internalServicesFromOperatorToClientSum,
                currentPositionData.getOperatorContragent(), currentPositionData.getClientContragent());
    }

    public void fixCurrentPositions(Session session, List<CurrentPositionsManager.CurrentPositionItem> curPositionList) {
        // Удалить все текущие позиции
        Criteria currentPositionCriteria = session.createCriteria(CurrentPosition.class);
        List<CurrentPosition> currentPositionList = currentPositionCriteria.list();
        for (CurrentPosition currentPosition : currentPositionList) {
            session.delete(currentPosition);
        }

        for (CurrentPositionsManager.CurrentPositionItem currentPositionItem : curPositionList) {
            Contragent contragentDebtor =
                    (Contragent) session.load(Contragent.class, currentPositionItem.getIdOfContragentDebtor());
            Contragent contragentCreditor =
                    (Contragent) session.load(Contragent.class, currentPositionItem.getIdOfContragentCreditor());
            CurrentPosition currentPosition = new CurrentPosition();
            currentPosition.setIdOfContragentDebtor(contragentDebtor);
            currentPosition.setIdOfContragentCreditor(contragentCreditor);
            currentPosition.setSumma(currentPositionItem.getSum());
            session.persist(currentPosition);
        }
    }

    public class CurPosition {

        private long idOfPosition;
        private String contragentDebtorName;
        private String contragentCreditorName;
        private String summa;

        public CurPosition(long idOfPosition, String contragentDebtorName, String contragentCreditorName, long summa) {
            this.idOfPosition = idOfPosition;
            this.contragentDebtorName = contragentDebtorName;
            this.contragentCreditorName = contragentCreditorName;
            this.summa = BasicReport.longToMoney(summa);
        }

        public long getIdOfPosition() {
            return idOfPosition;
        }

        public String getContragentDebtorName() {
            return contragentDebtorName;
        }

        public String getContragentCreditorName() {
            return contragentCreditorName;
        }

        public String getSumma() {
            return summa;
        }
    }
}
