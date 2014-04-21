/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.10.2009
 * Time: 14:09:39
 * Отчет по реализации за месяц (для школ)
 */
public class OrgOrderCategoryReport extends BasicReportForOrgJob {

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    private static final Logger logger = LoggerFactory.getLogger(OrgOrderCategoryReport.class);

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new OrgOrderCategoryReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    public static class Builder extends BasicReportJob.Builder {

        private static class OrderCategory {

            private final String orderCategoryJavaRegexpMask;
            private final String reportTitle;

            private OrderCategory(String orderCategoryJavaRegexpMask, String reportTitle) {
                this.orderCategoryJavaRegexpMask = orderCategoryJavaRegexpMask;
                this.reportTitle = reportTitle;
            }

            public String getOrderCategoryJavaRegexpMask() {
                return orderCategoryJavaRegexpMask;
            }

            public String getReportTitle() {
                return reportTitle;
            }
        }

        private static class OrderCategoryItem {

            private final OrderCategory orderCategory;
            private final Pattern pattern;
            private long sum;

            private OrderCategoryItem(OrderCategory orderCategory) {
                this.orderCategory = orderCategory;
                this.pattern = Pattern.compile(orderCategory.getOrderCategoryJavaRegexpMask());
                this.sum = 0L;
            }

            public OrderCategory getOrderCategory() {
                return orderCategory;
            }

            public long getSum() {
                return sum;
            }

            public void setSum(long sum) {
                this.sum = sum;
            }

            public void addSum(long sum) {
                this.sum += sum;
            }

            public void subSum(long sum) {
                this.sum -= sum;
            }

            public boolean matches(String orderCategory) {
                return this.pattern.matcher(orderCategory).matches();
            }
        }

        private static final OrderCategory COMPLEX_ORDER_CATEGORY = new OrderCategory("\\[.*\\]",
                "Горячее питание");

        private static final List<OrderCategory> MAUSSP_BUFFET_ORDER_CATEGORIES = Arrays
                .asList(new OrderCategory("Буфет", "Буфет"),
                        new OrderCategory("Буфет \\(выпечка\\)", "Буфет (выпечка)"),
                        new OrderCategory("Буфет \\(кухня\\)", "Буфет (кухня)"));

        public static class Row {

            private final String orgName;
            private final String firstName;
            private final String surname;
            private final String secondName;
            private final String nameAbbreviation;
            private final Long contractId;
            private final String orderCategory;
            private final Long sum;

            public Row(Client client, String orderCategory, long sum) {
                this.orgName = client.getOrg().getShortName();
                Person person = client.getPerson();
                this.firstName = person.getFirstName();
                this.surname = person.getSurname();
                this.secondName = person.getSecondName();
                this.nameAbbreviation = AbbreviationUtils
                        .buildFullAbbreviation(person.getFirstName(), person.getSurname(), person.getSecondName());
                this.contractId = client.getContractId();
                this.orderCategory = orderCategory;
                this.sum = sum;
            }

            public String getOrgName() {
                return orgName;
            }

            public String getFirstName() {
                return firstName;
            }

            public String getSurname() {
                return surname;
            }

            public String getSecondName() {
                return secondName;
            }

            public String getNameAbbreviation() {
                return nameAbbreviation;
            }

            public Long getContractId() {
                return contractId;
            }

            public String getOrderCategory() {
                return orderCategory;
            }

            public Long getSum() {
                return sum;
            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public OrgOrderCategoryReport build(Session session, Date startTime, Date endTime,
                Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("idOfOrg", org.getIdOfOrg());
            parameterMap.put("orgName", org.getOfficialName());
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone()));
            Date generateEndTime = new Date();
            return new OrgOrderCategoryReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private List<OrderCategory> findBuffetOrderCategories() throws Exception {
            return MAUSSP_BUFFET_ORDER_CATEGORIES;
        }

        private static List<OrderCategoryItem> createOrderCategoryItems(List<OrderCategory> orderCategories) {
            List<OrderCategoryItem> orderCategoryItems = new LinkedList<OrderCategoryItem>();
            for (OrderCategory orderCategory : orderCategories) {
                orderCategoryItems.add(new OrderCategoryItem(orderCategory));
            }
            return orderCategoryItems;
        }

        private static void setZeroSum(List<OrderCategoryItem> orderCategoryItems) {
            for (OrderCategoryItem orderCategoryItem : orderCategoryItems) {
                orderCategoryItem.setSum(0L);
            }
        }

        private static void addSum(List<OrderCategoryItem> orderCategoryItems, String orderCategory, long sum)
                throws Exception {
            boolean foundOrderCategoryItem = false;
            for (OrderCategoryItem orderCategoryItem : orderCategoryItems) {
                if (orderCategoryItem.matches(orderCategory)) {
                    orderCategoryItem.addSum(sum);
                    foundOrderCategoryItem = true;
                    break;
                }
            }
            if (!foundOrderCategoryItem) {
                throw new IllegalArgumentException(String.format("Unknown order detail root menu: %s", orderCategory));
            }
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar) throws Exception {
            List<Row> rows = new LinkedList<Row>();

            List<OrderCategory> buffetOrderCategories = findBuffetOrderCategories();
            Query clientListQuery = session.createQuery(
                    "from Client client where client.org = ? order by client.contractId");
            clientListQuery.setParameter(0, org);

            OrderCategoryItem complexOrderCategoryItem = new OrderCategoryItem(COMPLEX_ORDER_CATEGORY);
            List<OrderCategoryItem> cardBuffetOrderCategoryItems = createOrderCategoryItems(buffetOrderCategories);
            List<OrderCategoryItem> cashBuffetOrderCategoryItems = createOrderCategoryItems(buffetOrderCategories);

            Query ordersQuery = session.createQuery(
                    "from Order clientOrder where clientOrder.state=0 and clientOrder.client = ? and (clientOrder.createTime between ? and ?)");
            ordersQuery.setParameter(1, startTime);
            ordersQuery.setParameter(2, endTime);

            List clientList = clientListQuery.list();
            for (Object object : clientList) {
                Client client = (Client)object;
                ordersQuery.setParameter(0, client);

                complexOrderCategoryItem.setSum(0L);
                setZeroSum(cardBuffetOrderCategoryItems);
                setZeroSum(cashBuffetOrderCategoryItems);
                long grantSum = 0L;
                long discountSum = 0L;

                List orderList = ordersQuery.list();
                for (Object orderObject : orderList) {
                    Order order = (Order) orderObject;

                    grantSum += order.getGrantSum();
                    discountSum += order.getSocDiscount() + order.getTrdDiscount();

                    boolean hasComplexDetails = false;
                    Set<OrderDetail> orderDetails = order.getOrderDetails();
                    for (OrderDetail orderDetail : orderDetails) {
                        if(orderDetail.getState()==1) continue;
                        long totalDetailSum =
                                (orderDetail.getRPrice() - orderDetail.getDiscount()) * orderDetail.getQty();
                        String orderCategory = orderDetail.getRootMenu();
                        if (complexOrderCategoryItem.matches(orderCategory)) {
                            hasComplexDetails = true;
                            complexOrderCategoryItem.addSum(totalDetailSum);
                        } else {
                            addSum(cardBuffetOrderCategoryItems, orderCategory, totalDetailSum);
                        }
                    }
                    if (hasComplexDetails) {
                        complexOrderCategoryItem.subSum(order.getGrantSum());
                    }
                }
                rows.add(new Row(client, complexOrderCategoryItem.getOrderCategory().getReportTitle(),
                        complexOrderCategoryItem.getSum()));
                rows.add(new Row(client, "Дотации", grantSum));
                rows.add(new Row(client, "Льготы", discountSum));
                for (OrderCategoryItem orderCategoryItem : cardBuffetOrderCategoryItems) {
                    rows.add(new Row(client, orderCategoryItem.getOrderCategory().getReportTitle(),
                            orderCategoryItem.getSum()));
                }
            }
            return new JRBeanCollectionDataSource(rows);
        }

    }

    public OrgOrderCategoryReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void BasicReportForOrgJob(Date startTime, Date endTime, Long idOfOrg, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar) {
        super.BasicReportForOrgJob(startTime, endTime, idOfOrg, templateFilename, sessionFactory,
                calendar);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public OrgOrderCategoryReport() {
    }
}