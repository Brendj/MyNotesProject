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

    final private static Logger logger = LoggerFactory.getLogger(TotalSalesReport.class);

    public TotalSalesReport(Date generateTime, long l, JasperPrint jasperPrint, Date startTime, Date endTime,Long idOfContragent) {
        super(generateTime,l,jasperPrint,startTime,endTime,idOfContragent);
    }

    public class AutoReportBuildJob extends BasicReportForAllOrgJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportForContragentJob.Builder {

        private Long idOfContragent= -1L;

        private long sumComplex = 0L;
        private long sumBuffet = 0L;
        private long sumBen = 0L;

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
            parameterMap.put("sumComplex", sumComplex);
            parameterMap.put("sumBuffet", sumBuffet);
            parameterMap.put("sumBen", sumBen);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            return new TotalSalesReport(generateTime, generateEndTime.getTime() - generateTime.getTime(), jasperPrint,
                    startTime, endTime, idOfContragent);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Calendar calendar,
                Map<String, Object> parameterMap) throws Exception {
            long l = System.currentTimeMillis();
            startTime = CalendarUtils.truncateToDayOfMonth(startTime);
            endTime = CalendarUtils.endOfDay(endTime);
            List<String> dates = CalendarUtils.datesBetween(startTime, endTime);
            Map<Long, List<TotalSalesItem>> totalListMap = new HashMap<Long, List<TotalSalesItem>>();
            //Получаем список всех школ, заполняем ими списов
            List<Long> idOfOrgsList = new LinkedList<Long>();
            retreiveAllOrgs(totalListMap, dates, idOfOrgsList);
            if(idOfOrgsList.size() == 0){
                return new JREmptyDataSource();
            }
            logger.error("e1 : " + (System.currentTimeMillis() - l));
            TotalSalesData totalSalesTMP = new TotalSalesData();
            for (List<TotalSalesItem> totalSalesItemList : totalListMap.values()) {
                totalSalesTMP.getItemList().addAll(totalSalesItemList);
            }
            retreiveAllOrders(totalListMap, idOfOrgsList, startTime, endTime);
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
                List<Long> idOfOrgsList, Date startTime, Date endTime){
            OrdersRepository ordersRepository = RuntimeContext.getAppContext().getBean(OrdersRepository.class);
            List<OrderItem> allOrders = ordersRepository.findAllOrders(idOfOrgsList, startTime, endTime);

            for (OrderItem allOrder : allOrders) {
                if(allOrder.getMenutype() == OrderDetail.TYPE_DISH_ITEM){//buffet
                    sumBuffet += handleOrders(totalListMap, allOrder, NAME_BUFFET);
                }else if(allOrder.getSocDiscount() == 0L){//Pay
                    sumComplex += handleOrders(totalListMap, allOrder, NAME_COMPLEX);

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
                List<Long> idOfOrgsList) {
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
