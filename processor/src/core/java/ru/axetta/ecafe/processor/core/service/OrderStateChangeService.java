/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 10.04.14
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
@Service
public class OrderStateChangeService {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderStateChangeService.class);

    public void notifyOrderStateChange() throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        if (!runtimeContext.isMainNode()) {
            return;
        }
        Long duration = System.currentTimeMillis();
        Date currentDate = new Date();
        Date endDate = CalendarUtils.truncateToDayOfMonth(currentDate);
        Date startDate = CalendarUtils.addDays(endDate, -1);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        //Map<Long, List<OrderItem>> - структура словаря
        MultiValueMap orgDictionary = new MultiValueMap();
        //Map<ContragentItem, List<OrgItem>> - структура словаря
        MultiValueMap supplierDictionary = new MultiValueMap();
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria cancelOrderCriteria = persistenceSession.createCriteria(CanceledOrder.class);
            cancelOrderCriteria.add(Restrictions.between("createTime", startDate, endDate));
            List list = cancelOrderCriteria.list();
            for (Object obj: list){
                CanceledOrder canceledOrder = (CanceledOrder) obj;
                Org org = canceledOrder.getOrg();
                final OrgItem orgItem = new OrgItem(org);
                final ContragentItem contragentItem = new ContragentItem(org.getDefaultSupplier());
                supplierDictionary.put(contragentItem, orgItem);
                orgDictionary.put(org.getIdOfOrg(), new OrderItem(canceledOrder.getOrder()));
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, LOGGER);
            HibernateUtils.close(persistenceSession, LOGGER);
        }

        /* убираем дубли */
        MultiValueMap supplierDictionaryUnique = new MultiValueMap();
        for (Object obj: supplierDictionary.keySet()){
            ContragentItem contragentItem = (ContragentItem) obj;
            final Collection collection = supplierDictionary.getCollection(contragentItem);
            Set<OrgItem> orgItems = new TreeSet<OrgItem>();
            for (Object o: collection){
                orgItems.add((OrgItem) o);
            }
            if(!supplierDictionaryUnique.containsValue(contragentItem)){
                supplierDictionaryUnique.putAll(contragentItem, orgItems);
            }
        }
        supplierDictionary = supplierDictionaryUnique;

        for (Object obj: supplierDictionary.keySet()){
            ContragentItem contragentItem = (ContragentItem) obj;
            List<String> strings = Arrays.asList(StringUtils.split(contragentItem.getOrderNotifyMailList(), ";"));
            List<String> addresses = new ArrayList<String>(strings);
            if(addresses.isEmpty()){
                LOGGER.debug("addresses isEmpty " +contragentItem.idOfContragent);
            }
            final Collection collection = supplierDictionary.getCollection(contragentItem);
            LOGGER.debug("supplierDictionary.getCollection(contragentItem) size " +collection.size());
            for (Object o: collection){
                OrgItem orgItem = (OrgItem) o;
                List<OrderItem> orderItems = (List<OrderItem>) orgDictionary.getCollection(orgItem.getIdOfOrg());
                if(orderItems!=null){
                    LOGGER.debug("orgDictionary.getCollection(orgItem.getIdOfOrg()) size " +orderItems.size());
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    StringWriter stringWriter = new StringWriter();
                    String emailSubject = String.format("Уведомление об отмененных заказах \"%s\" - \"%s\"",orgItem.getShortName(), orgItem.getAddress());
                    Collections.sort(orderItems, new Comparator<OrderItem>(){
                        @Override
                        public int compare(OrderItem o1, OrderItem o2) {
                            return o1.getCreateTime().compareTo(o2.getCreateTime());
                        }
                    });
                    try {
                        writeReportDocumentTo(orderItems, stringWriter, dateTimeFormat, emailSubject, startDate, endDate);
                    } finally {
                        IOUtils.closeQuietly(stringWriter);
                    }
                    for (String address : addresses) {
                        if (StringUtils.trimToNull(address) != null) {
                            try {
                                RuntimeContext.getInstance().getPostman().postNotificationEmail(address, emailSubject, stringWriter.toString());
                            } catch (Exception e) {
                                LOGGER.error("Failed to post event", e);
                            }
                        }
                    }
                }
            }
        }
        duration = System.currentTimeMillis() - duration;
        LOGGER.debug("OrderStateChangeService generateTime: "+duration);
    }

    protected static class ContragentItem {
        private final long idOfContragent;
        private final String orderNotifyMailList;

        ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            if(StringUtils.isEmpty(contragent.getOrderNotifyMailList())){
                this.orderNotifyMailList = "";
            } else {
                this.orderNotifyMailList = contragent.getOrderNotifyMailList();
            }
        }

        public long getIdOfContragent() {
            return idOfContragent;
        }

        public String getOrderNotifyMailList() {
            return orderNotifyMailList;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ContragentItem that = (ContragentItem) o;

            if (idOfContragent != that.idOfContragent) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return (int) (idOfContragent ^ (idOfContragent >>> 32));
        }
    }

    protected static class OrgItem implements Comparable<OrgItem>{
        private final Long idOfOrg;
        private final String shortName;
        private final String number;
        private final String address;

        @Override
        public int compareTo(OrgItem o) {
            return idOfOrg.compareTo(o.getIdOfOrg());
        }

        OrgItem(Org org) {
            idOfOrg = org.getIdOfOrg();
            shortName = org.getShortName();
            number = Org.extractOrgNumberFromName(shortName);
            address = org.getAddress();
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getNumber() {
            return number;
        }

        public String getAddress() {
            return address;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            OrgItem orgItem = (OrgItem) o;

            if (!idOfOrg.equals(orgItem.idOfOrg)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return idOfOrg.hashCode();
        }
    }

    protected static class OrderItem{
        private final long idOfOrder;
        private final Long socDiscount;
        private final Long tradeDiscount;
        private final Long grantSum;
        private final Long rSum;
        private final Date createTime;
        private final Long sumByCard;
        private final Long sumByCash;
        private final String state;
        private final List<OrderDetailItem> orderDetailItems;

        OrderItem(Order order) {
            this.idOfOrder = order.getCompositeIdOfOrder().getIdOfOrder();
            this.socDiscount = order.getSocDiscount();
            this.tradeDiscount = order.getSocDiscount();
            this.grantSum = order.getGrantSum();
            this.rSum = order.getRSum();
            this.createTime = order.getCreateTime();
            this.sumByCard = order.getSumByCard();
            this.sumByCash = order.getSumByCash();
            this.state = order.getStateAsString();
            Set<OrderDetail> orderDetails = order.getOrderDetails();
            orderDetailItems = new ArrayList<OrderDetailItem>(orderDetails.size());
            for (OrderDetail detail: orderDetails){
                orderDetailItems.add(new OrderDetailItem(detail));
            }

        }

        public long getIdOfOrder() {
            return idOfOrder;
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

        public Long getrSum() {
            return rSum;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public Long getSumByCard() {
            return sumByCard;
        }

        public Long getSumByCash() {
            return sumByCash;
        }

        public String getState() {
            return state;
        }

        public List<OrderDetailItem> getOrderDetailItems() {
            return orderDetailItems;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            OrderItem orderItem = (OrderItem) o;

            if (idOfOrder != orderItem.idOfOrder) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return (int) (idOfOrder ^ (idOfOrder >>> 32));
        }
    }

    protected static class OrderDetailItem{
        private final long idOfOrderDetail;
        private final String menuDetailName;

        OrderDetailItem(OrderDetail detail) {
            this.idOfOrderDetail = detail.getCompositeIdOfOrderDetail().getIdOfOrderDetail();
            this.menuDetailName = detail.getMenuDetailName();
        }

        public long getIdOfOrderDetail() {
            return idOfOrderDetail;
        }

        public String getMenuDetailName() {
            return menuDetailName;
        }
    }

    private static void writeReportDocumentTo(List<OrderItem> items,  StringWriter writer,
            DateFormat timeFormat, String title, Date beginDate, Date endDate) throws IOException {
        writer.write("<html>");
        writer.write("<head>");
        writer.write("<title>");
        writer.write(StringEscapeUtils.escapeHtml(title));
        writer.write("</title>");
        writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        writer.write("<meta http-equiv=\"Content-Language\" content=\"ru\">");
        writer.write("</head>");
        writer.write("<body>");
        String beginDateStr = StringEscapeUtils.escapeHtml(timeFormat.format(beginDate));
        String endDateStr = StringEscapeUtils.escapeHtml(timeFormat.format(endDate));
        String datePeriod = String.format("<p>В период c %s по %s были изменены следующие заказы:</p>",beginDateStr, endDateStr);
        writer.write(datePeriod);
        writer.write("<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\">");
        writer.write("<tr>");
        // заголовок таблицы
        writer.write("<th align=\"center\"><p style=\"margin: 10px;\">");
        writer.write(StringEscapeUtils.escapeHtml("Ид. заказа"));
        writer.write("</p></th>");
        writer.write("<th align=\"center\"><p style=\"margin: 10px;\">");
        writer.write(StringEscapeUtils.escapeHtml("Время покупки"));
        writer.write("</p></th>");
        writer.write("<th align=\"center\"><p style=\"margin: 10px;\">");
        writer.write(StringEscapeUtils.escapeHtml("Сумма"));
        writer.write("</p></th>");
        writer.write("<th align=\"center\"><p style=\"margin: 10px;\">");
        writer.write(StringEscapeUtils.escapeHtml("Социальная скидка"));
        writer.write("</p></th>");
        writer.write("<th align=\"center\"><p style=\"margin: 10px;\">");
        writer.write(StringEscapeUtils.escapeHtml("Скидка поставщика"));
        writer.write("</p></th>");
        writer.write("<th align=\"center\"><p style=\"margin: 10px;\">");
        writer.write(StringEscapeUtils.escapeHtml("Дотация"));
        writer.write("</p></th>");
        writer.write("<th align=\"center\"><p style=\"margin: 10px;\">");
        writer.write(StringEscapeUtils.escapeHtml("Состав"));
        writer.write("</p></th>");
        writer.write("<th align=\"center\"><p style=\"margin: 10px;\">");
        writer.write(StringEscapeUtils.escapeHtml("Статус"));
        writer.write("</p></th>");
        writer.write("</tr>");
        // Содержимое таблицы
        long totalRSum = 0L;
        long totalSocDiscount = 0L;
        long totalTradeDiscount = 0L;
        long totalGrantSum = 0L;
        for (OrderItem orderItem: items){
            writer.write("<tr>");
            writer.write("<td align=\"left\"><p style=\"margin: 10px;\">");
            writer.write(StringEscapeUtils.escapeHtml(Long.toString(orderItem.getIdOfOrder())));
            writer.write("</p></td>");
            writer.write("<td align=\"center\"><p style=\"margin: 10px;\">");
            writer.write(StringEscapeUtils.escapeHtml(timeFormat.format(orderItem.getCreateTime())));
            writer.write("</p></td>");
            writer.write("<td align=\"right\"><p style=\"margin: 10px;\">");
            writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(orderItem.getrSum())));
            totalRSum+=orderItem.getrSum();
            writer.write("</p></td>");
            writer.write("<td align=\"right\"><p style=\"margin: 10px;\">");
            writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(orderItem.getSocDiscount())));
            totalSocDiscount+=orderItem.getSocDiscount();
            writer.write("</p></td>");
            writer.write("<td align=\"right\"><p style=\"margin: 10px;\">");
            writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(orderItem.getTradeDiscount())));
            totalTradeDiscount+=orderItem.getTradeDiscount();
            writer.write("</p></td>");
            writer.write("<td align=\"right\"><p style=\"margin: 10px;\">");
            writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(orderItem.getGrantSum())));
            totalGrantSum+=orderItem.getGrantSum();
            writer.write("</p></td>");

            writer.write("<td align=\"center\">");
            writer.write("<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\">");
            //writer.write("<tr>");
            //writer.write("<th align=\"center\"><p style=\"margin: 10px;\">");
            //writer.write(StringEscapeUtils.escapeHtml("Ид."));
            //writer.write("</p></th>");
            //writer.write("<th align=\"center\"><p style=\"margin: 10px;\">");
            //writer.write(StringEscapeUtils.escapeHtml(""));
            //writer.write("</p></th>");
            for (OrderDetailItem detailItem: orderItem.getOrderDetailItems()){
                writer.write("<tr>");
                writer.write("<td align=\"center\"><p style=\"margin: 10px;\">");
                writer.write(StringEscapeUtils.escapeHtml(detailItem.getMenuDetailName()));
                writer.write("</p></td>");
                writer.write("</tr>");
            }
            //writer.write("</tr>");
            writer.write("</table>");
            writer.write("</td>");

            writer.write("<td align=\"center\"><p style=\"margin: 10px;\">");
            writer.write(StringEscapeUtils.escapeHtml(orderItem.getState()));
            writer.write("</p></td>");
            writer.write("</tr>");
        }
        // итого
        writer.write("<tr>");
        writer.write("<td align=\"center\" colspan=\"2\" style=\"padding: 4:px\"><p style=\"margin: 10px;\">");
        writer.write("<b>"+StringEscapeUtils.escapeHtml("ИТОГО")+"</b>");
        writer.write("</p></td>");
        writer.write("<td align=\"right\"><p style=\"margin: 10px;\">");
        writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(totalRSum)));
        writer.write("</p></td>");
        writer.write("<td align=\"right\"><p style=\"margin: 10px;\">");
        writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(totalSocDiscount)));
        writer.write("</p></td>");
        writer.write("<td align=\"right\"><p style=\"margin: 10px;\">");
        writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(totalTradeDiscount)));
        writer.write("</p></td>");
        writer.write("<td align=\"right\"><p style=\"margin: 10px;\">");
        writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(totalGrantSum)));
        writer.write("</p></td>");
        writer.write("<td></td>");
        writer.write("</tr>");
        writer.write("</table>");
        String message = String.format("Отчеты, содержащие данные по этим заказам, могли измениться.");
        writer.write("<p><b>"+StringEscapeUtils.escapeHtml(message)+"</b></p>");
        writer.write("</body>");
        writer.write("</html>");
        writer.flush();
        writer.close();
    }
}
