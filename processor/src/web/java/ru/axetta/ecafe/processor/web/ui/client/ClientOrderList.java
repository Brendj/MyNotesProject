/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientOrderList {

    public static class Item {

        public Date getOrderDate() {
            return orderDate;
        }

        public static class OrgItem {

            private final Long idOfOrg;
            private final String shortName;

            public OrgItem(Org org) {
                this.idOfOrg = org.getIdOfOrg();
                this.shortName = org.getShortName();
            }

            public Long getIdOfOrg() {
                return idOfOrg;
            }

            public String getShortName() {
                return shortName;
            }
        }

        public static class Detail {

            private final Long qty;
            private final Long discount;
            private final Long rPrice;
            private final String menuDetailName;
            private final String rootMenu;
            private final String itemCode;
            private final String menuOutput;

            public Detail(OrderDetail orderDetail) {
                this.qty = orderDetail.getQty();
                this.discount = orderDetail.getDiscount();
                this.rPrice = orderDetail.getRPrice();
                this.menuDetailName = orderDetail.getMenuDetailName();
                this.rootMenu = orderDetail.getRootMenu();
                this.itemCode = orderDetail.getItemCode();
                this.menuOutput = orderDetail.getMenuOutput();
            }

            public String getItemCode() {
                return itemCode;
            }

            public Long getQty() {
                return qty;
            }

            public Long getDiscount() {
                return discount;
            }

            public Long getRPrice() {
                return rPrice;
            }

            public String getMenuDetailName() {
                return menuDetailName;
            }

            public String getRootMenu() {
                return rootMenu;
            }

            public String getMenuOutput() {
                return menuOutput;
            }
        }

        private final OrgItem org;
        private final Long idOfOrder;
        private final Long idOfCard;
        private final Long cardNo;
        private final Long idOfCashier;
        private final Long socDiscount;
        private final Long tradeDiscount;
        private final Long grantSum;
        private final Long rSum;
        private final Date createTime;
        private final Long idOfTransaction;
        private final Date transactionTime;
        private final Date orderDate;
        private final Long sumByCard;
        private final Long sumByCash;
        private final List<Detail> details;
        private final String state;

        public Item(Order order) {
            this.org = new OrgItem(order.getOrg());
            this.idOfOrder = order.getCompositeIdOfOrder().getIdOfOrder();
            Card card = order.getCard();
            if (null == card) {
                this.idOfCard = null;
                this.cardNo = null;
            } else {
                this.idOfCard = card.getIdOfCard();
                this.cardNo = card.getCardNo();
            }
            this.idOfCashier = order.getIdOfCashier();
            this.socDiscount = order.getSocDiscount();
            this.tradeDiscount = order.getTrdDiscount();
            this.orderDate = order.getOrderDate();
            this.grantSum = order.getGrantSum();
            this.rSum = order.getRSum();
            this.createTime = order.getCreateTime();
            //this.transactionTime = order.getTransaction().getTransactionTime();
            AccountTransaction accountTransaction = order.getTransaction();
            this.idOfTransaction = null == accountTransaction ? null : accountTransaction.getIdOfTransaction();
            this.transactionTime = null == accountTransaction ? null : accountTransaction.getTransactionTime();
            this.sumByCard = order.getSumByCard();
            this.sumByCash = order.getSumByCash();
            this.state = order.getStateAsString();
            this.details = new LinkedList<Detail>();
            for (OrderDetail currOrderDetail : order.getOrderDetails()) {
                this.details.add(new Detail(currOrderDetail));
            }
        }

        public OrgItem getOrg() {
            return org;
        }

        public Long getIdOfOrder() {
            return idOfOrder;
        }

        public Long getIdOfCard() {
            return idOfCard;
        }

        public Long getCardNo() {
            return cardNo;
        }

        public Long getIdOfCashier() {
            return idOfCashier;
        }

        public Long getSocDiscount() {
            return socDiscount;
        }

        public Long getTradeDiscount() {
            return tradeDiscount;
        }

        public Long getGrantSum() {
            return grantSum;
        }

        public Long getRSum() {
            return rSum;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public Long getIdOfTransaction() {
            return idOfTransaction;
        }

        public Date getTransactionTime() {
            return transactionTime;
        }

        public Long getSumByCard() {
            return sumByCard;
        }

        public Long getSumByCash() {
            return sumByCash;
        }

        public List<Detail> getDetails() {
            return details;
        }

        public String getState() {
            return state;
        }

        public String listDetailsAtString() {
            String detailsAtString = "";
            for (Detail detail: details) {
                detailsAtString = detailsAtString + detail.itemCode + " |  " + detail.menuDetailName + " | " + detail.rPrice.toString() +
                " | " + detail.menuOutput + " | " + detail.qty.toString() + " | " + detail.rootMenu + "; ";
            }
            return detailsAtString;
        }

    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }

    public void fill(Session session, Client client, Date startTime, Date endTime) throws Exception {
        Criteria criteria = session.createCriteria(Order.class);
        criteria.add(Restrictions.eq("client", client));
        criteria.add(Restrictions.ge("createTime", startTime));
        criteria.add(Restrictions.le("createTime", CalendarUtils.addDays(endTime, 1)));

        List<Item> items = new LinkedList<Item>();
        List orders = criteria.list();
        for (Object object : orders) {
            Order order = (Order) object;
            Item item = new Item(order);
            items.add(item);
        }
        this.items = items;
    }

}