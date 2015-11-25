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

import java.util.*;

/**
 * Онлайн отчеты / Сводный отчет по продажам
 * User: Shamil
 * Date: 25.01.15
 */
public class TotalSalesReport  extends BasicReportForContragentJob {

    private static final String NAME_COMPLEX = "Платные комплексы";
    private static final String NAME_BUFFET = "Буфетная продукция";
    private static final String NAME_BEN = "Льготные комплексы";
    private static final String TOTAL_BUFFET_PLUS_NAME_COMPLEX = "Итого буфет собственное + Платные комплексы"; // Буфет собственное + Платные комплексы
    private static final String TOTAL_NAME_BUFFET_PLUS_NAME_COMPLEX = "Итого буфет все + Платные комплексы"; // Буфетная продукция + Платные комплексы

    final private static Logger logger = LoggerFactory.getLogger(TotalSalesReport.class);

    public TotalSalesReport(Date generateTime, long l, JasperPrint jasperPrint, Date startTime, Date endTime,Long idOfContragent) {
        super(generateTime,l,jasperPrint,startTime,endTime,idOfContragent);
    }

    public class AutoReportBuildJob extends BasicReportForAllOrgJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportForContragentJob.Builder {

        private Long idOfContragent= -1L;

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
            }else {
                throw new Exception("Контрагент не указан.");
            }
            JRDataSource dataSource = createDataSource(session, startTime, endTime, (Calendar) calendar.clone(),
                    parameterMap);

            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(endTime));
            parameterMap.put("sumComplex", Double.parseDouble(sumComplex.toString()));
            parameterMap.put("sumBuffet", Double.parseDouble(sumBuffet.toString()));
            parameterMap.put("sumBen", Double.parseDouble(sumBen.toString()));
            parameterMap.put("sumBuffetPlusSumComplex", Double.parseDouble(sumBuffetPlusSumComplex.toString()));
            parameterMap.put("sumBuffetOwnPlusSumComplex", Double.parseDouble(sumBuffetOwnPlusSumComplex.toString()));

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            return new TotalSalesReport(generateTime, generateEndTime.getTime() - generateTime.getTime(), jasperPrint,
                    startTime, endTime, idOfContragent);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Calendar calendar,
                Map<String, Object> parameterMap) throws Exception {

            List<String> titlesComplexes = new ArrayList<String>();

            String titles = StringUtils.trimToEmpty(getReportProperties().getProperty("titleComplexes"));

            for (String title : Arrays.asList(StringUtils.split(titles, ','))) {
                titlesComplexes.add(title);
            }

            long l = System.currentTimeMillis();
            startTime = CalendarUtils.truncateToDayOfMonth(startTime);
            endTime = CalendarUtils.endOfDay(endTime);
            List<String> dates = CalendarUtils.datesBetween(startTime, endTime);
            Map<Long, List<TotalSalesItem>> totalListMap = new HashMap<Long, List<TotalSalesItem>>();
            //Получаем список всех школ, заполняем ими списов
            List<Long> idOfOrgsList = new LinkedList<Long>();
            retreiveAllOrgs(totalListMap, dates, idOfOrgsList, titlesComplexes);
            if(idOfOrgsList.size() == 0){
                return new JREmptyDataSource();
            }
            logger.error("e1 : " + (System.currentTimeMillis() - l));
            TotalSalesData totalSalesTMP = new TotalSalesData();
            for (List<TotalSalesItem> totalSalesItemList : totalListMap.values()) {
                totalSalesTMP.getItemList().addAll(totalSalesItemList);
            }
            retreiveAllOrders(totalListMap, idOfOrgsList, titlesComplexes, startTime, endTime);
            logger.error("e2 : " + (System.currentTimeMillis() - l));

            //Вывод, разбивка по районам.
            Map<String,TotalSalesData> totalSalesResult = new HashMap<String, TotalSalesData>();

            for (TotalSalesItem o : totalSalesTMP.getItemList()) {
                if(!totalSalesResult.containsKey(o.getDisctrict())){
                    totalSalesResult.put(o.getDisctrict(),new TotalSalesData(o.getDisctrict()));
                }
                List<TotalSalesItem> itemList = totalSalesResult.get(o.getDisctrict()).getItemList();
                if(itemList == null) {
                    itemList = new ArrayList<TotalSalesItem>();
                }

                itemList.add(o);
            }
            return new JRBeanCollectionDataSource(totalSalesResult.values());
        }

        private void retreiveAllOrders(Map<Long, List<TotalSalesItem>> totalListMap,
                List<Long> idOfOrgsList, List<String> titleComplexes, Date startTime, Date endTime){
            OrdersRepository ordersRepository = RuntimeContext.getAppContext().getBean(OrdersRepository.class);
            List<OrderItem> allOrders = ordersRepository.findAllOrders(idOfOrgsList, startTime, endTime);

            for (OrderItem allOrder : allOrders) {

                if (!titleComplexes.isEmpty()) {
                    for (String title : titleComplexes) {
                        if (title.equals("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[0]))) {
                            if (allOrder.getMenuOrigin() == OrderDetail.PRODUCT_OWN) {
                                sumProductOwn += handleOrders(totalListMap, allOrder, title);
                                sumBuffetOwnPlusSumComplex += sumProductOwn + handleOrders(totalListMap, allOrder, TOTAL_BUFFET_PLUS_NAME_COMPLEX); //Буфет собственное + Платные комплексы
                            }
                        } else if (title.equals("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[1]))) {
                            if (allOrder.getMenuOrigin() == OrderDetail.PRODUCT_CENTRALIZE) {
                                sumProductCentralize += handleOrders(totalListMap, allOrder, title);
                            }
                        } else if (title.equals("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[2]))) {
                            if (allOrder.getMenuOrigin() == OrderDetail.PRODUCT_CENTRALIZE_COOK) {
                                sumProductCentralizeCook += handleOrders(totalListMap, allOrder, title);
                            }
                        } else if (title.equals("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[3]))) {
                            if (allOrder.getMenuOrigin() == OrderDetail.PRODUCT_PURCHASE) {
                                sumProductPurchase += handleOrders(totalListMap, allOrder, title);
                            }
                        } else if (title.equals("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[4]))) {
                            if (allOrder.getMenuOrigin() == OrderDetail.PRODUCT_VENDING) {
                                sumProductVending += handleOrders(totalListMap, allOrder, title);
                            }
                        } else if (title.equals("Буфет ".concat(OrderDetail.PRODUCTION_NAMES_TYPES[5]))) {
                            if (allOrder.getMenuOrigin() == OrderDetail.PRODUCT_COMMERCIAL) {
                                sumProductCommercial += handleOrders(totalListMap, allOrder, title);
                            }
                        }
                    }
                }

                if(allOrder.getMenutype() == OrderDetail.TYPE_DISH_ITEM){//buffet
                    sumBuffet += handleOrders(totalListMap, allOrder, NAME_BUFFET);
                    sumBuffetPlusSumComplex += sumBuffet + handleOrders(totalListMap, allOrder, TOTAL_NAME_BUFFET_PLUS_NAME_COMPLEX); //Буфетная продукция + Платные комплексы
                }else if(allOrder.getSocDiscount() == 0L){//Pay
                    sumComplex += handleOrders(totalListMap, allOrder, NAME_COMPLEX);
                    sumBuffetPlusSumComplex += sumComplex + handleOrders(totalListMap, allOrder, TOTAL_NAME_BUFFET_PLUS_NAME_COMPLEX); //Буфетная продукция + Платные комплексы

                    sumBuffetOwnPlusSumComplex += sumComplex + handleOrders(totalListMap, allOrder, TOTAL_BUFFET_PLUS_NAME_COMPLEX); //Буфет собственное + Платные комплексы
                }else{ // free
                    sumBen += handleOrders(totalListMap, allOrder, NAME_BEN);

                }
            }

        }


        private long handleOrders(Map<Long, List<TotalSalesItem>> totalListMap, OrderItem buffetOrder, String type) {
            List<TotalSalesItem> totalSalesItemList;
            long sum = 0L;

            totalSalesItemList = totalListMap.get(buffetOrder.getIdOfOrg());
            if (totalSalesItemList == null) {
                return 0L;
            }
            String date = CalendarUtils.dateShortToString(buffetOrder.getOrderDate());
            for (TotalSalesItem totalSalesItem : totalSalesItemList) {
                if ((totalSalesItem.getDate().equals(date)) && (totalSalesItem.getType().equals(type))) {
                    totalSalesItem.setSum(totalSalesItem.getSum() + buffetOrder.getSum());
                    sum += buffetOrder.getSum();
                }
            }
            return sum;
        }

        private void retreiveAllOrgs(Map<Long, List<TotalSalesItem>> totalSalesItemMap, List<String> dates,
                List<Long> idOfOrgsList, List<String> titleComplexes) {
            OrgRepository orgRepository = RuntimeContext.getAppContext().getBean(OrgRepository.class);
            if(idOfContragent != -1){

            }
            List<OrgItem> allNames = orgRepository.findAllNamesByContragentTSP(idOfContragent);
            List<TotalSalesItem> totalSalesItemList;
            for (OrgItem orgItem : allNames) {
                totalSalesItemList = new ArrayList<TotalSalesItem>();
                for (String date : dates) {
                    totalSalesItemList.add(new TotalSalesItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L, NAME_BUFFET));
                    totalSalesItemList.add(new TotalSalesItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L, NAME_BEN));
                    totalSalesItemList.add(new TotalSalesItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L, NAME_COMPLEX));
                    totalSalesItemList.add(new TotalSalesItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L, TOTAL_BUFFET_PLUS_NAME_COMPLEX));
                    totalSalesItemList.add(new TotalSalesItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L, TOTAL_NAME_BUFFET_PLUS_NAME_COMPLEX));
                    for (String title: titleComplexes) {
                        totalSalesItemList.add(new TotalSalesItem(orgItem.getOfficialName(), orgItem.getDistrict(), date, 0L, title));
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
