/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
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
 * User: Shamil
 * Date: 25.01.15
 */
public class TotalSalesReport extends BasicReportForAllOrgJob {

    private static final String NAME_COMPLEX = "Платные комплексы";
    private static final String NAME_BUFFET = "Буфетная продукция";
    private static final String NAME_BEN = "Льготные комплексы";

    final private static Logger logger = LoggerFactory.getLogger(TotalSalesReport.class);

    public class AutoReportBuildJob extends BasicReportForAllOrgJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(endTime));

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new TotalSalesReport(generateTime, generateEndTime.getTime() - generateTime.getTime(), jasperPrint,
                    startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Calendar calendar,
                Map<String, Object> parameterMap) throws Exception {
            startTime = CalendarUtils.truncateToDayOfMonth(startTime);
            endTime = CalendarUtils.endOfDay(endTime);
            List<String> dates = CalendarUtils.datesBetween(startTime, endTime);


            Map<String, List<TotalSalesItem>> totalListMap = new HashMap<String, List<TotalSalesItem>>();

            //Получаем список всех школ, заполняем ими списов
            retreiveAllOrgs(totalListMap, dates);

            TotalSalesData totalSalesData = new TotalSalesData("one");

            for (List<TotalSalesItem> totalSalesItemList : totalListMap.values()) {
                totalSalesData.getItemList().addAll(totalSalesItemList);
            }

            retreiveAndHandleBuffetOrders(totalListMap, startTime, endTime);
            retreiveAndHandleFreeComplexes(totalListMap, startTime, endTime);
            retreiveAndHandlePayComplexes(totalListMap, startTime, endTime);

            List<TotalSalesData> totalSalesDataList = new ArrayList<TotalSalesData>();
            totalSalesDataList.add(totalSalesData);
            return new JRBeanCollectionDataSource(totalSalesDataList);
        }

        private void retreiveAndHandleBuffetOrders(Map<String, List<TotalSalesItem>> totalListMap, Date startTime,
                Date endTime) {
            OrdersRepository ordersRepository = RuntimeContext.getAppContext().getBean(OrdersRepository.class);
            List<OrderItem> allBuffetOrders = ordersRepository.findAllBuffetOrders(startTime, endTime);
            handleOrders(totalListMap, allBuffetOrders, NAME_BUFFET);
        }

        private void retreiveAndHandleFreeComplexes(Map<String, List<TotalSalesItem>> totalListMap, Date startTime,
                Date endTime) {
            OrdersRepository ordersRepository = RuntimeContext.getAppContext().getBean(OrdersRepository.class);
            List<OrderItem> allBuffetOrders = ordersRepository.findAllFreeComplex(startTime, endTime);
            handleOrders(totalListMap, allBuffetOrders, NAME_BEN);

        }

        private void retreiveAndHandlePayComplexes(Map<String, List<TotalSalesItem>> totalListMap, Date startTime,
                Date endTime) {
            OrdersRepository ordersRepository = RuntimeContext.getAppContext().getBean(OrdersRepository.class);
            List<OrderItem> allBuffetOrders = ordersRepository.findAllPayComplex(startTime, endTime);
            handleOrders(totalListMap, allBuffetOrders, NAME_COMPLEX);

        }

        private void handleOrders(Map<String, List<TotalSalesItem>> totalListMap, List<OrderItem> allBuffetOrders,
                String type) {
            List<TotalSalesItem> totalSalesItemList;
            for (OrderItem allBuffetOrder : allBuffetOrders) {
                totalSalesItemList = totalListMap.get(allBuffetOrder.getOrgName());
                if (totalSalesItemList == null) {
                    continue;
                }
                String date = CalendarUtils.dateShortToString(allBuffetOrder.getOrderDate());
                for (TotalSalesItem totalSalesItem : totalSalesItemList) {
                    if ((totalSalesItem.getDate().equals(date)) && (totalSalesItem.getType().equals(type))) {
                        totalSalesItem.setSumm(totalSalesItem.getSumm() + allBuffetOrder.getSum());
                    }
                }
            }
        }


        private void retreiveAllOrgs(Map<String, List<TotalSalesItem>> totalSalesItemMap, List<String> dates) {
            OrgRepository orgRepository = RuntimeContext.getAppContext().getBean(OrgRepository.class);
            List<OrgItem> allNames = orgRepository.findAllNames();
            List<TotalSalesItem> totalSalesItemList;
            for (OrgItem allName : allNames) {
                totalSalesItemList = new ArrayList<TotalSalesItem>();
                for (String date : dates) {
                    totalSalesItemList.add(new TotalSalesItem(allName.getOfficialName(), date, 0L, NAME_BUFFET));
                    totalSalesItemList.add(new TotalSalesItem(allName.getOfficialName(), date, 0L, NAME_BEN));
                    totalSalesItemList.add(new TotalSalesItem(allName.getOfficialName(), date, 0L, NAME_COMPLEX));
                }
                totalSalesItemMap.put(allName.getOfficialName(), totalSalesItemList);
            }
        }
    }


    public TotalSalesReport() {
    }


    //Платные комлпексы:
    /*
    select org.officialName, od.menuDetailName, od.rPrice, od.discount, sum(od.qty) as quantity, min(o.createdDate), max(o.createdDate)
    from CF_Orders o, CF_OrderDetails od, CF_Orgs org
    where o.idOfOrder = od.idOfOrder
    and o.idOfOrg = od.idOfOrg
    and org.idOfOrg = od.idOfOrg
    and o.createdDate >= 1409515200000
    and o.createdDate <= 1410811199000
    and (od.menuType >= 50 and od.menuType <= 99)
    and (o.idOfOrg in (0,1,2,3))
    and (od.socDiscount c 0)
    and o.state=0
    and od.state=0

    group by org.officialName, od.menuDetailName, od.rPrice, od.discount  order by org.officialName, od.menuDetailName*/


    //бесплатные комплексы

    /*

    select org.officialName, od.menuDetailName, sum(od.rPrice), sum(od.discount), sum(od.qty) as quantity,  min(o.createdDate), max(o.createdDate)
from CF_Orders o, CF_OrderDetails od, CF_Orgs org
where o.idOfOrder = od.idOfOrder and o.state=0 and od.state=0
and o.idOfOrg = od.idOfOrg   and org.idOfOrg = od.idOfOrg
and o.createdDate >= 1410120000000
 and o.createdDate <= 1410811199000
 and (od.menuType >= 50 and od.menuType <= 99)
 and (o.idOfOrg = 0 or o.idOfOrg = 3)    and (od.socDiscount > 0)
 group by org.officialName, od.menuDetailName, od.rPrice, od.discount  order by org.officialName, od.menuDetailName

     */


    //Буфет:

    /*

    select org.officialName, od.MenuDetailName, od.MenuOutput, od.MenuOrigin, od.rPrice,  od.discount, sum(od.qty) as quantity, min(o.createdDate), max(o.createdDate)
from CF_Orders o join CF_OrderDetails od on (o.idOfOrder = od.idOfOrder and o.idOfOrg = od.idOfOrg)                  join CF_Orgs org on (org.idOfOrg = od.idOfOrg)
where o.createdDate >= 1410724800000 and o.createdDate <= 1411415999000 and od.menuType = 0
and o.state=0 and od.state=0 and (org.idOfOrg in (0) or org.idOfOrg in   (select me.IdOfDestOrg from CF_MenuExchangeRules me where me.IdOfSourceOrg in (0)))
group by org.officialName, od.menuDetailName, od.MenuOrigin, od.rPrice, od.MenuOutput, od.discount order by org.officialName, od.MenuOrigin, od.menuDetailName


    * */

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new TotalSalesReport();
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public TotalSalesReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime, Date endTime) {
        super(generateTime, generateDuration, print, startTime,
                endTime);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_TODAY;
    }

}
