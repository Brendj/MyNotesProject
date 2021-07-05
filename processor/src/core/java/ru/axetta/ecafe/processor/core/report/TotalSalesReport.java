/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;
import ru.axetta.ecafe.processor.core.persistence.dao.order.OrdersRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgItem;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;
import ru.axetta.ecafe.processor.core.report.model.totalsales.TotalSalesData;
import ru.axetta.ecafe.processor.core.report.model.totalsales.TotalSalesItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Онлайн отчеты / Сводный отчет по продажам
 * User: Shamil
 * Date: 25.01.15
 */
public class TotalSalesReport  extends BasicReportForContragentJob {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Сводный отчет по продажам (итоговые показатели)";
    public static final String[] TEMPLATE_FILE_NAMES = {"TotalSalesReport.jasper", "TotalSalesReportWithAgeGroups.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3};


    private static final String NAME_COMPLEX = "Платные комплексы";
    private static final String NAME_BUFFET = "Буфетная продукция";
    private static final String NAME_BEN = "Льготные комплексы";
    private static final String TOTAL_BUFFET_PLUS_NAME_COMPLEX = "Итого буфет собственное + Платные комплексы"; // Буфет собственное + Платные комплексы
    private static final String TOTAL_NAME_BUFFET_PLUS_NAME_COMPLEX = "Итого буфет все + Платные комплексы"; // Буфетная продукция + Платные комплексы
    private static final String[] AGE_GROUP_NAMES = {"1 - 4 классы", "5 - 11 классы", "Прочие группы обучающихся", "Прочие группы"};

    final private static Logger logger = LoggerFactory.getLogger(TotalSalesReport.class);

    public TotalSalesReport(Date generateTime, long l, JasperPrint jasperPrint, Date startTime, Date endTime,Long idOfContragent) {
        super(generateTime,l,jasperPrint,startTime,endTime,idOfContragent);
    }

    public class AutoReportBuildJob extends BasicReportForAllOrgJob.AutoReportBuildJob {

    }

    public static class Builder extends BasicReportForContragentJob.Builder {

        private List<String> titlesComplexes;

        private Long idOfContragent = -1L;
        private List<Long> idOfOrgList;

        private Long sumComplex = 0L;
        private Long sumBuffet = 0L;
        private Long sumBen = 0L;


        // Новые колонки
        private Long sumProductOwn = 0L; // Собственное
        private Long sumProductCentralize = 0L; // Централизованное
        private Long sumProductCentralizeCook = 0L; // Централизованное с доготовкой
        private Long sumProductPurchase = 0L;     // Закупленное
        private Long sumProductVending = 0L;  // Вендинг
        private Long sumProductCommercial = 0L; // Коммерческое питание


        private Long sumBuffetPlusSumComplex = 0L;
        private Long sumBuffetOwnPlusSumComplex = 0L;

        private List<TotalSalesReportItem> totalSalesReportItemList;

        public List<TotalSalesReportItem> getTotalSalesReportItemList() {
            return totalSalesReportItemList;
        }

        public void setTotalSalesReportItemList(List<TotalSalesReportItem> totalSalesReportItemList) {
            this.totalSalesReportItemList = totalSalesReportItemList;
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);

            if (contragent != null) {
                parameterMap.put("contragentName", contragent.getContragentName());
                idOfContragent = contragent.getIdOfContragent();
            } else {
                throw new Exception("Поставщик не указан.");
            }
            JRDataSource dataSource = createDataSource(session, startTime, endTime, (Calendar) calendar.clone(),
                    parameterMap);

            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(endTime));

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            return new TotalSalesReport(generateTime, generateEndTime.getTime() - generateTime.getTime(), jasperPrint,
                    startTime, endTime, idOfContragent);
        }

        private  List<TotalSalesReportItem> summaries(HashMap<Long, PriceAndSum> priceAndSumBenefitHashMap, HashMap<Long, PriceAndSum> priceAndSumPaidHashMap) {
            totalSalesReportItemList = new ArrayList<TotalSalesReportItem>();

            if (!titlesComplexes.isEmpty()) {

                if(titlesComplexes.contains("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[4]))) {
                    TotalSalesReportItem totalSalesReportItemSumProductVending =
                            new TotalSalesReportItem("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[4]),
                            Double.parseDouble(sumProductVending.toString()) / 100);
                    totalSalesReportItemList.add(totalSalesReportItemSumProductVending);
                }

                if(titlesComplexes.contains("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[3]))) {
                    TotalSalesReportItem totalSalesReportItemSumProductPurchase = new TotalSalesReportItem(
                            "Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[3]),
                            Double.parseDouble(sumProductPurchase.toString())/100);
                    totalSalesReportItemList.add(totalSalesReportItemSumProductPurchase);
                }

                if(titlesComplexes.contains("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[5]))) {
                    TotalSalesReportItem totalSalesReportItemSumProductCommercial = new TotalSalesReportItem(
                            "Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[5]),
                            Double.parseDouble(sumProductCommercial.toString())/100);
                    totalSalesReportItemList.add(totalSalesReportItemSumProductCommercial);
                }

                if(titlesComplexes.contains("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[0]))) {
                    TotalSalesReportItem totalSalesReportItemSumProductOwn = new TotalSalesReportItem(
                            "Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[0]),
                            Double.parseDouble(sumProductOwn.toString())/100);
                    totalSalesReportItemList.add(totalSalesReportItemSumProductOwn);
                }

                if(titlesComplexes.contains("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[1]))) {
                    TotalSalesReportItem totalSalesReportItemSumProductCentralize = new TotalSalesReportItem(
                            "Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[1]),
                            Double.parseDouble(sumProductCentralize.toString())/100);
                    totalSalesReportItemList.add(totalSalesReportItemSumProductCentralize);
                }

                if(titlesComplexes.contains("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[2]))) {
                    TotalSalesReportItem totalSalesReportItemSumProductCentralizeCook = new TotalSalesReportItem(
                            "Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[2]),
                            Double.parseDouble(sumProductCentralizeCook.toString())/100);
                    totalSalesReportItemList.add(totalSalesReportItemSumProductCentralizeCook);
                }

                for(PriceAndSum value : priceAndSumBenefitHashMap.values()) {
                    totalSalesReportItemList.add(new TotalSalesReportItem(value.getTitle(), Double.parseDouble(value.getSum().toString())/100));
                }

                for(PriceAndSum value : priceAndSumPaidHashMap.values()) {
                    totalSalesReportItemList.add(new TotalSalesReportItem(value.getTitle(), Double.parseDouble(value.getSum().toString())/100));
                }
            }

            TotalSalesReportItem totalSalesReportItemSumBuffet = new TotalSalesReportItem(NAME_BUFFET,
                    Double.parseDouble(sumBuffet.toString())/100);
            totalSalesReportItemList.add(totalSalesReportItemSumBuffet);

            TotalSalesReportItem totalSalesReportItemSumBuffetOwnPlusSumComplex = new TotalSalesReportItem(
                    TOTAL_BUFFET_PLUS_NAME_COMPLEX, Double.parseDouble(sumBuffetOwnPlusSumComplex.toString())/100);
            totalSalesReportItemList.add(totalSalesReportItemSumBuffetOwnPlusSumComplex);

            TotalSalesReportItem totalSalesReportItemSumBuffetPlusSumComplex = new TotalSalesReportItem(
                    TOTAL_NAME_BUFFET_PLUS_NAME_COMPLEX, Double.parseDouble(sumBuffetPlusSumComplex.toString())/100);
            totalSalesReportItemList.add(totalSalesReportItemSumBuffetPlusSumComplex);

            TotalSalesReportItem totalSalesReportItemSumBen = new TotalSalesReportItem(NAME_BEN,
                    Double.parseDouble(sumBen.toString())/100);
            totalSalesReportItemList.add(totalSalesReportItemSumBen);

            TotalSalesReportItem totalSalesReportItemSubComplex = new TotalSalesReportItem(NAME_COMPLEX,
                    Double.parseDouble(sumComplex.toString())/100);
            totalSalesReportItemList.add(totalSalesReportItemSubComplex);

            return totalSalesReportItemList;
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Calendar calendar,
                Map<String, Object> parameterMap) throws Exception {

            titlesComplexes = new ArrayList<String>();

            String idOfOrgListString = StringUtils.trimToEmpty(getReportProperties().getProperty("idOfOrgList"));

            List<String> idOfOrgListStrings = new ArrayList<String>();

            if (!idOfOrgListString.equals("")) {
                idOfOrgListStrings = Arrays.asList(StringUtils.split(idOfOrgListString, ','));
            }

            Boolean showAgeGroups = Boolean.valueOf(getReportProperties().getProperty("showAgeGroups"));

            String titles = StringUtils.trimToEmpty(getReportProperties().getProperty("preferentialTitleComplexes"));

            String benefitTitleAndSums = StringUtils.trimToEmpty(getReportProperties().getProperty("benefitTitleAndSumList"));

            String paidTitleAndSumList = StringUtils.trimToEmpty(getReportProperties().getProperty("paidTitleAndSumList"));

            //Мапа с названием колонки Льготный, ценой, и суммой
            HashMap<Long, PriceAndSum> priceAndSumBenefitHashMap = new HashMap<Long, PriceAndSum>();

            for (String titleAndSum : Arrays.asList(StringUtils.split(benefitTitleAndSums, ';'))) {
                String[] str = StringUtils.split(titleAndSum, ',');
                priceAndSumBenefitHashMap.put(Long.parseLong(str[1]), new PriceAndSum(str[0].trim(), 0L));
                titlesComplexes.add(str[0].trim());
            }

            //Мапа с названием колонки Платный, церщй и суммой
            HashMap<Long, PriceAndSum> priceAndSumPaidHashMap = new HashMap<Long, PriceAndSum>();

            for (String titleAndSum : Arrays.asList(StringUtils.split(paidTitleAndSumList, ';'))) {
                String[] strings = StringUtils.split(titleAndSum, ',');
                priceAndSumPaidHashMap.put(Long.parseLong(strings[1]), new PriceAndSum(strings[0].trim(), 0L));
                titlesComplexes.add(strings[0].trim());
            }

            for (String title : Arrays.asList(StringUtils.split(titles, ','))) {
                titlesComplexes.add(title);
            }

            idOfOrgList = new ArrayList<Long>();

            if (!idOfOrgListStrings.isEmpty()) {
                for (String idOfOrg : idOfOrgListStrings) {
                    idOfOrgList.add(Long.valueOf(idOfOrg));
                }
            }

            long l = System.currentTimeMillis();
            startTime = CalendarUtils.truncateToDayOfMonth(startTime);
            endTime = CalendarUtils.endOfDay(endTime);
            List<String> datesStringList = CalendarUtils.datesBetween(startTime, endTime);
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
            List<Date> dates = new ArrayList<Date>();
            for (String date : datesStringList) {
                dates.add(dateFormat.parse(date));
            }
            Map<Long, List<TotalSalesItem>> totalListMap = new HashMap<Long, List<TotalSalesItem>>();
            //Получаем список всех школ, заполняем ими списов
            List<Long> idOfOrgsList = new LinkedList<Long>();
            retreiveAllOrgs(totalListMap, dates, idOfOrgsList, titlesComplexes, showAgeGroups);
            if (idOfOrgsList.size() == 0) {
                return new JREmptyDataSource();
            }

            TotalSalesData totalSalesTMP = new TotalSalesData();
            for (List<TotalSalesItem> totalSalesItemList : totalListMap.values()) {
                totalSalesTMP.getItemList().addAll(totalSalesItemList);
            }
            retreiveAllOrders(totalListMap, idOfOrgsList, titlesComplexes, startTime, endTime, priceAndSumBenefitHashMap, showAgeGroups);
            retreiveAllOrdersPaid(totalListMap, idOfOrgsList, startTime, endTime, priceAndSumPaidHashMap, showAgeGroups);


            //Вывод, разбивка по районам.
            Map<String, TotalSalesData> totalSalesResult = new HashMap<String, TotalSalesData>();

            List<TotalSalesReportItem> tot = summaries(priceAndSumBenefitHashMap, priceAndSumPaidHashMap);

            for (TotalSalesItem o : totalSalesTMP.getItemList()) {
                if (!totalSalesResult.containsKey(o.getDisctrict())) {
                    totalSalesResult.put(o.getDisctrict(), new TotalSalesData(o.getDisctrict(), tot));
                }
                List<TotalSalesItem> itemList = totalSalesResult.get(o.getDisctrict()).getItemList();
                if(itemList == null) {
                    itemList = new ArrayList<TotalSalesItem>();
                }

                itemList.add(o);
            }
            return new JRBeanCollectionDataSource(totalSalesResult.values());
        }

        private void retreiveAllOrders(Map<Long, List<TotalSalesItem>> totalListMap, List<Long> idOfOrgsList,
                List<String> titleComplexes, Date startTime, Date endTime,
                HashMap<Long, PriceAndSum> priceAndSumHashMap, boolean showAgeGroups) {
            OrdersRepository ordersRepository = RuntimeContext.getAppContext().getBean(OrdersRepository.class);
            List<OrderItem> allOrders = ordersRepository.findAllOrders(idOfOrgsList, startTime, endTime);

            boolean wasSumProductOwn = false;
            if(titleComplexes.isEmpty()) {
                wasSumProductOwn = true;
                titleComplexes.add("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[0]));
            }
            for (OrderItem allOrder : allOrders) {

                for (String title : titleComplexes) {
                    if (title.equals("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[0]))) {
                        if (allOrder.getMenuOrigin() == OrderDetail.PRODUCT_OWN
                                && allOrder.getMenutype() == OrderDetail.TYPE_DISH_ITEM) {
                            sumProductOwn += handleOrders(totalListMap, allOrder, title, showAgeGroups);
                            sumBuffetOwnPlusSumComplex += handleOrders(totalListMap, allOrder,
                                    TOTAL_BUFFET_PLUS_NAME_COMPLEX, showAgeGroups); //Буфет собственное + Платные комплексы
                        }
                    } else if (title.equals("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[1]))) {
                        if (allOrder.getMenuOrigin() == OrderDetail.PRODUCT_CENTRALIZE
                                && allOrder.getMenutype() == OrderDetail.TYPE_DISH_ITEM) {
                            sumProductCentralize += handleOrders(totalListMap, allOrder, title, showAgeGroups);
                        }
                    } else if (title.equals("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[2]))) {
                        if (allOrder.getMenuOrigin() == OrderDetail.PRODUCT_CENTRALIZE_COOK
                                && allOrder.getMenutype() == OrderDetail.TYPE_DISH_ITEM) {
                            sumProductCentralizeCook += handleOrders(totalListMap, allOrder, title, showAgeGroups);
                        }
                    } else if (title.equals("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[3]))) {
                        if (allOrder.getMenuOrigin() == OrderDetail.PRODUCT_PURCHASE
                                && allOrder.getMenutype() == OrderDetail.TYPE_DISH_ITEM) {
                            sumProductPurchase += handleOrders(totalListMap, allOrder, title, showAgeGroups);
                        }
                    } else if (title.equals("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[4]))) {
                        if (allOrder.getMenuOrigin() == OrderDetail.PRODUCT_VENDING
                                && allOrder.getMenutype() == OrderDetail.TYPE_DISH_ITEM) {
                            sumProductVending += handleOrders(totalListMap, allOrder, title, showAgeGroups);
                        }
                    } else if (title.equals("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[5]))) {
                        if (allOrder.getMenuOrigin() == OrderDetail.PRODUCT_COMMERCIAL
                                && allOrder.getMenutype() == OrderDetail.TYPE_DISH_ITEM) {
                            sumProductCommercial += handleOrders(totalListMap, allOrder, title, showAgeGroups);
                        }
                    }
                }

                if (!priceAndSumHashMap.isEmpty()) {
                    if (allOrder.getMenutype() != OrderDetail.TYPE_DISH_ITEM && allOrder.getSumPay() == 0L) {
                        if (priceAndSumHashMap.get(allOrder.getSum()) != null) {
                            Long sum = priceAndSumHashMap.get(allOrder.getSum()).getSum();
                            sum += handleOrders(totalListMap, allOrder,
                                    priceAndSumHashMap.get(allOrder.getSum()).getTitle(), showAgeGroups);
                            priceAndSumHashMap.get(allOrder.getSum()).setSum(sum);
                        }
                    }
                }

                if(allOrder.getMenutype() == OrderDetail.TYPE_DISH_ITEM){//buffet
                    sumBuffet += handleOrders(totalListMap, allOrder, NAME_BUFFET, showAgeGroups);
                    sumBuffetPlusSumComplex += handleOrders(totalListMap, allOrder, TOTAL_NAME_BUFFET_PLUS_NAME_COMPLEX, showAgeGroups); //Буфетная продукция + Платные комплексы
                }else if(allOrder.getSumPay() > 0L){//Pay
                    sumComplex += handleOrders(totalListMap, allOrder, NAME_COMPLEX, showAgeGroups);
                    sumBuffetPlusSumComplex += handleOrders(totalListMap, allOrder, TOTAL_NAME_BUFFET_PLUS_NAME_COMPLEX, showAgeGroups); //Буфетная продукция + Платные комплексы

                    sumBuffetOwnPlusSumComplex += handleOrders(totalListMap, allOrder, TOTAL_BUFFET_PLUS_NAME_COMPLEX, showAgeGroups); //Буфет собственное + Платные комплексы
                }else{ // free
                    sumBen += handleOrders(totalListMap, allOrder, NAME_BEN, showAgeGroups);

                }
            }

            if(wasSumProductOwn) {
                titleComplexes.remove(titleComplexes.indexOf("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[0])));
            }

        }

        private void retreiveAllOrdersPaid(Map<Long, List<TotalSalesItem>> totalListMap, List<Long> idOfOrgsList, Date startTime, Date endTime,
                HashMap<Long, PriceAndSum> priceAndSumPaidHashMap, boolean showAgeGroups) {
            OrdersRepository ordersRepository = RuntimeContext.getAppContext().getBean(OrdersRepository.class);
            List<OrderItem> allOrdersPaid = ordersRepository.findAllOrdersPaid(idOfOrgsList, startTime, endTime);

            for (OrderItem allOrder : allOrdersPaid) {
                if (!priceAndSumPaidHashMap.isEmpty()) {
                    if (priceAndSumPaidHashMap.get(allOrder.getSumPay()) != null && allOrder.getSumDiscount() == 0L) {
                        Long sum = priceAndSumPaidHashMap.get(allOrder.getSumPay()).getSum();
                        sum += handleOrders(totalListMap, allOrder,
                                priceAndSumPaidHashMap.get(allOrder.getSumPay()).getTitle(), showAgeGroups);
                        priceAndSumPaidHashMap.get(allOrder.getSumPay()).setSum(sum);
                    }
                    if (priceAndSumPaidHashMap.get(allOrder.getSumPay()) != null && allOrder.getSumDiscount() > 0) {
                        Long sum = priceAndSumPaidHashMap.get(allOrder.getSumPay()).getSum();
                        sum += handleOrders(totalListMap, allOrder,
                                priceAndSumPaidHashMap.get(allOrder.getSumPay()).getTitle(), showAgeGroups);
                        priceAndSumPaidHashMap.get(allOrder.getSumPay()).setSum(sum);
                    }
                    if (priceAndSumPaidHashMap.get(allOrder.getSumDiscount()) != null && allOrder.getSumDiscount() > 0) {
                        Long sum = priceAndSumPaidHashMap.get(allOrder.getSumDiscount()).getSum();
                        sum += handleOrders(totalListMap, allOrder,
                                priceAndSumPaidHashMap.get(allOrder.getSumDiscount()).getTitle(), showAgeGroups);
                        priceAndSumPaidHashMap.get(allOrder.getSumDiscount()).setSum(sum);
                    }
                }
            }
        }


        private long handleOrders(Map<Long, List<TotalSalesItem>> totalListMap, OrderItem buffetOrder, String type, boolean showAgeGroups) {
            List<TotalSalesItem> totalSalesItemList;
            long sum = 0L;

            totalSalesItemList = totalListMap.get(buffetOrder.getIdOfOrg());
            if (totalSalesItemList == null) {
                return 0L;
            }
            String ageGroup = showAgeGroups ? getAgeGroup(buffetOrder) : "";
            Date date = CalendarUtils.truncateToDayOfMonth(new Date(buffetOrder.getOrderDate()));
            for (TotalSalesItem totalSalesItem : totalSalesItemList) {
                if ((totalSalesItem.getDate().equals(date)) && (totalSalesItem.getType().equals(type))
                        && totalSalesItem.getAgeGroup().equals(ageGroup)) {
                    totalSalesItem.setSum(totalSalesItem.getSum() + buffetOrder.getSum());
                    totalSalesItem.setSumPay(totalSalesItem.getSumPay() + buffetOrder.getSumPay());
                    long realDiscountAdd = buffetOrder.getSumDiscount() > 0 ? buffetOrder.getSumDiscount() : buffetOrder.getKazanDiscount();
                    totalSalesItem.setSumDiscount(totalSalesItem.getSumDiscount() + realDiscountAdd);
                    if(buffetOrder.getIdOfClient() != null && !totalSalesItem.getIdOfClientList().contains(buffetOrder.getIdOfClient())) {
                        totalSalesItem.getIdOfClientList().add(buffetOrder.getIdOfClient());
                        totalSalesItem.setUniqueClientCount(totalSalesItem.getIdOfClientList().size());
                    }
                    sum += buffetOrder.getSum();
                }
            }
            return sum;
        }

        private String getAgeGroup(OrderItem buffetOrder) {
            if(buffetOrder.getIdOfClientGroup() == null || buffetOrder.getIdOfClientGroup() >= ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES) {
                return AGE_GROUP_NAMES[3];
            } else if(StringUtils.isNotEmpty(buffetOrder.getGroupName()) && is511Group(buffetOrder.getGroupName())) {
                return AGE_GROUP_NAMES[1];
            } else if(StringUtils.isNotEmpty(buffetOrder.getGroupName()) && is14Group(buffetOrder.getGroupName())) {
                return AGE_GROUP_NAMES[0];
            } else {
                return AGE_GROUP_NAMES[2];
            }
        }

        private boolean is14Group(String groupName) {
            boolean b = true;
            if(groupName.length() > 1) {
                b = !Character.isDigit(groupName.charAt(1));
            }
            return (groupName.startsWith("1") ||
                    groupName.startsWith("2") ||
                    groupName.startsWith("3") ||
                    groupName.startsWith("4")) && b;
        }

        private boolean is511Group(String groupName) {
            boolean b1 = true;
            if(groupName.length() > 1) {
                b1 = !Character.isDigit(groupName.charAt(1));
            }
            boolean b2 = true;
            if(groupName.length() > 2) {
                b2 = !Character.isDigit(groupName.charAt(2));
            }
            return ((groupName.startsWith("5") ||
                    groupName.startsWith("6") ||
                    groupName.startsWith("7") ||
                    groupName.startsWith("8") ||
                    groupName.startsWith("9")) && b1) ||
                    ((groupName.startsWith("10") ||
                    groupName.startsWith("11")) && b2);
        }

        private void retreiveAllOrgs(Map<Long, List<TotalSalesItem>> totalSalesItemMap, List<Date> dates,
                List<Long> idOfOrgsList, List<String> titleComplexes, boolean showAgeGroups) {
            OrgRepository orgRepository = RuntimeContext.getAppContext().getBean(OrgRepository.class);

            List<OrgItem> allNames;

            if (!idOfOrgList.isEmpty()) {
                allNames = orgRepository.findAllNamesByContragentTSPByListOfOrgIds(idOfContragent, idOfOrgList);
            } else {
                allNames = orgRepository.findAllNamesByContragentTSP(idOfContragent);
            }

            String[] ageGroups = showAgeGroups ? AGE_GROUP_NAMES : new String[]{""};

            List<TotalSalesItem> totalSalesItemList;
            for (OrgItem orgItem : allNames) {
                totalSalesItemList = new ArrayList<TotalSalesItem>();
                for(String ageGroup : ageGroups) {
                    for (Date date : dates) {
                        totalSalesItemList
                                .add(new TotalSalesItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L,
                                        NAME_BUFFET, ageGroup, false, 0L, 0L));
                        totalSalesItemList
                                .add(new TotalSalesItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L,
                                        NAME_BEN, ageGroup, false, 0L, 0L));
                        totalSalesItemList
                                .add(new TotalSalesItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L,
                                        NAME_COMPLEX, ageGroup, false, 0L, 0L));
                        totalSalesItemList
                                .add(new TotalSalesItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L,
                                        TOTAL_BUFFET_PLUS_NAME_COMPLEX, ageGroup, true, 0L, 0L));
                        totalSalesItemList
                                .add(new TotalSalesItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L,
                                        TOTAL_NAME_BUFFET_PLUS_NAME_COMPLEX, ageGroup, true, 0L, 0L));
                        for (String title : titleComplexes) {
                            totalSalesItemList
                                    .add(new TotalSalesItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L,
                                            title, ageGroup, true, 0L, 0L));
                        }
                    }
                }
                totalSalesItemMap.put(orgItem.getIdOfOrg(), totalSalesItemList);
                idOfOrgsList.add(orgItem.getIdOfOrg());
            }
        }

        public void setIdOfContragent(Long idOfContragent) {
            this.idOfContragent = idOfContragent;
        }
    }


    public TotalSalesReport() {
    }


    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }


    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_LAST_WEEK;
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new TotalSalesReport();
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }
}
