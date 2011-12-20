/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
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

        private final OrgItem org;
        private final Long idOfOrder;
        private final Long idOfCard;
        private final Long cardNo;
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

    public void fill(Session session, Client client, Date startTime, Date endTime) throws Exception {
        Criteria criteria = session.createCriteria(Order.class);
        criteria.add(Restrictions.eq("client", client));
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