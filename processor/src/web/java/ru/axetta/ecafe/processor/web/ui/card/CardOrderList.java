/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;

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
public class CardOrderList {

    public static class Item {

        public static class Detail {

            private final Long qty;
            private final Long discount;
            private final Long rPrice;
            private final String menuDetailName;
            private final String rootMenu;

            public Detail(OrderDetail orderDetail) {
                this.qty = orderDetail.getQty();
                this.discount = orderDetail.getDiscount();
                this.rPrice = orderDetail.getRPrice();
                this.menuDetailName = orderDetail.getMenuDetailName();
                this.rootMenu = orderDetail.getRootMenu();
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
        }

        private final Long idOfCashier;
        private final Long discount;
        private final Long grantSum;
        private final Long rSum;
        private final Date createTime;
        private final Date transactionTime;
        private final Long sumByCard;
        private final Long sumByCash;
        private final List<Detail> details;

        public Item(Order order) {
            this.idOfCashier = order.getIdOfCashier();
            this.discount = order.getSocDiscount();
            this.grantSum = order.getGrantSum();
            this.rSum = order.getRSum();
            this.createTime = order.getCreateTime();
            //this.transactionTime = order.getTransaction().getTransactionTime();
            AccountTransaction accountTransaction = order.getTransaction();
            this.transactionTime = null == accountTransaction ? null : accountTransaction.getTransactionTime();
            this.sumByCard = order.getSumByCard();
            this.sumByCash = order.getSumByCash();
            this.details = new LinkedList<Detail>();
            for (OrderDetail currOrderDetail : order.getOrderDetails()) {
                this.details.add(new Detail(currOrderDetail));
            }
        }

        public Long getIdOfCashier() {
            return idOfCashier;
        }

        public Long getDiscount() {
            return discount;
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
    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }

    public void fill(Session session, Card card, Date startTime, Date endTime) throws Exception {
        Criteria criteria = session.createCriteria(Order.class);
        criteria.add(Restrictions.eq("card", card));
        criteria.add(Restrictions.ge("createTime", startTime));
        criteria.add(Restrictions.le("createTime", endTime));

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