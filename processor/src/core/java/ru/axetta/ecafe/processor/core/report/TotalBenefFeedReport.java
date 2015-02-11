/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientDao;
import ru.axetta.ecafe.processor.core.persistence.dao.enterevents.EnterEventsRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.model.ClientCount;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.EnterEventCount;
import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;
import ru.axetta.ecafe.processor.core.persistence.dao.order.OrdersRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgItem;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;
import ru.axetta.ecafe.processor.core.report.model.totalbeneffeedreport.TotalBenefFeedData;
import ru.axetta.ecafe.processor.core.report.model.totalbeneffeedreport.TotalBenefFeedItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Сводный отчет по льготному питанию
 * User: Shamil
 * Date: 02.02.15
 */
public class TotalBenefFeedReport extends BasicReportForAllOrgJob {

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public static class Builder extends BasicReportJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();

            if (RuntimeContext.getInstance().isTestMode()) {
                startTime = new Date(1409515200000L);
                endTime = new Date(1410119999000L);
            }

            parameterMap.put("startDate", CalendarUtils.dateShortToString(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToString(endTime));

            endTime = CalendarUtils.endOfDay(endTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime));


            Date generateEndTime = new Date();
            return new TotalBenefFeedReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime) throws Exception {
            Map<Long, TotalBenefFeedItem> dataMap = new HashMap<Long, TotalBenefFeedItem>();

            retrieveAllOrgs(dataMap);

            retrieveStudentsCount(dataMap);
            retrieveBeneficiaryStudentsCount(dataMap);
            retrieveEnterEventsCount(dataMap);
            retrieveOrdersCount(dataMap);
            retrieveOrdersWithEnterEventsCount(dataMap);
            retrieveOrdersWithNoEnterEventsCount(dataMap);













            List<TotalBenefFeedData> dataList = new ArrayList<TotalBenefFeedData>();
            dataList.add(new TotalBenefFeedData());
            dataList.get(0).getItemList().addAll(dataMap.values());

            return new JRBeanCollectionDataSource(dataList);
        }

        private void retrieveOrdersCount(Map<Long, TotalBenefFeedItem> dataMap) {
            OrdersRepository ordersRepository = RuntimeContext.getAppContext().getBean(OrdersRepository.class);
            for (OrderItem orderItem : ordersRepository.findAllBeneficiaryComplexes()) {
                TotalBenefFeedItem totalBenefFeedItem = dataMap.get(orderItem.getIdOfOrg());
                if(totalBenefFeedItem== null){
                    continue;
                    //todo wtf
                }
                totalBenefFeedItem.setOrderedMeals(totalBenefFeedItem.getOrderedMeals() + ((Long)orderItem.getSum()).intValue());
                if (orderItem.getOrdertype() == 4){
                    totalBenefFeedItem.setReceiveMealBenefStudents(totalBenefFeedItem.getReceiveMealBenefStudents() + ((Long)orderItem.getSum()).intValue());
                }else if (orderItem.getOrdertype() == 6){
                    totalBenefFeedItem.setReceiveMealReserveStudents(totalBenefFeedItem.getReceiveMealReserveStudents() + ((Long)orderItem.getSum()).intValue());
                }
            }
        }

        private void retrieveOrdersWithEnterEventsCount(Map<Long, TotalBenefFeedItem> dataMap) {
            OrdersRepository ordersRepository = RuntimeContext.getAppContext().getBean(OrdersRepository.class);
            for (OrderItem orderItem : ordersRepository.findAllWithEnterEventCount()) {
                TotalBenefFeedItem totalBenefFeedItem = dataMap.get(orderItem.getIdOfOrg());
                if(totalBenefFeedItem== null){
                    continue;
                }
                if (orderItem.getQty() == 1){
                }else if (orderItem.getQty() == 0){
                    totalBenefFeedItem.setReceiveMealNotEnteredBenefStudents(totalBenefFeedItem.getReceiveMealNotEnteredBenefStudents() + ((Long)orderItem.getSum()).intValue());
                }
            }
        }


        private void retrieveOrdersWithNoEnterEventsCount(Map<Long, TotalBenefFeedItem> dataMap) {
            OrdersRepository ordersRepository = RuntimeContext.getAppContext().getBean(OrdersRepository.class);
            for (OrderItem orderItem : ordersRepository.findAllWithNoEnterEventCount()) {
                TotalBenefFeedItem totalBenefFeedItem = dataMap.get(orderItem.getIdOfOrg());
                if(totalBenefFeedItem== null){
                    continue;
                }
                totalBenefFeedItem.setNotReceiveMealEnteredBenefStudents(totalBenefFeedItem.getNotReceiveMealEnteredBenefStudents() + ((Long)orderItem.getSum()).intValue());

            }
        }

        private void retrieveStudentsCount(Map<Long, TotalBenefFeedItem> dataMap) {
            ClientDao clientDao = RuntimeContext.getAppContext().getBean(ClientDao.class);
            for (ClientCount clientCount : clientDao.findAllStudentsCount()) {
                TotalBenefFeedItem totalBenefFeedItem = dataMap.get(clientCount.getIdOfOrg());
                if(totalBenefFeedItem != null){
                    totalBenefFeedItem.setStudents(clientCount.getCount());
                }
            }
        }


        private void retrieveBeneficiaryStudentsCount(Map<Long, TotalBenefFeedItem> dataMap) {
            ClientDao clientDao = RuntimeContext.getAppContext().getBean(ClientDao.class);
            for (ClientCount clientCount : clientDao.findAllBeneficiaryStudentsCount()) {
                TotalBenefFeedItem totalBenefFeedItem = dataMap.get(clientCount.getIdOfOrg());
                if(totalBenefFeedItem != null){
                    totalBenefFeedItem.setBenefStudents(clientCount.getCount());
                }
            }
        }


        private void retrieveEnterEventsCount(Map<Long, TotalBenefFeedItem> dataMap) {
            EnterEventsRepository enterEventsRepository = RuntimeContext.getAppContext().getBean(EnterEventsRepository.class);
            for (EnterEventCount enterEventCount : enterEventsRepository.findAllBeneficiaryStudentsEnterEventsCount()) {
                TotalBenefFeedItem totalBenefFeedItem = dataMap.get(enterEventCount.getIdOfOrg());
                if(totalBenefFeedItem != null){
                    totalBenefFeedItem.setEnteredBenefStudents(enterEventCount.getCount());
                }
            }
        }


        private void retrieveAllOrgs(Map<Long, TotalBenefFeedItem> totalBenefFeedItemsMap) {
            OrgRepository orgRepository = RuntimeContext.getAppContext().getBean(OrgRepository.class);

            List<OrgItem> allNames = orgRepository.findAllActive();
            TotalBenefFeedItem totalBenefFeedItem;
            for (OrgItem allName : allNames) {
                totalBenefFeedItem = new TotalBenefFeedItem(allName.getIdOfOrg(),allName.getOfficialName(),allName.getAddress());

                totalBenefFeedItemsMap.put(allName.getIdOfOrg(), totalBenefFeedItem);
            }
        }
    }




    public TotalBenefFeedReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    private static final Logger logger = LoggerFactory.getLogger(TotalBenefFeedReport.class);

    public TotalBenefFeedReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new TotalBenefFeedReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_CURRENT_MONTH;
    }
}
