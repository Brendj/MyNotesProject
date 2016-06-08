/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientDao;
import ru.axetta.ecafe.processor.core.persistence.dao.enterevents.EnterEventsRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.model.ClientCount;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.EnterEventCount;
import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;
import ru.axetta.ecafe.processor.core.persistence.dao.order.OrdersRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgItem;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;
import ru.axetta.ecafe.processor.core.persistence.service.org.OrgService;
import ru.axetta.ecafe.processor.core.report.model.UserOrgsAndContragents;
import ru.axetta.ecafe.processor.core.report.model.totalbeneffeedreport.SubItem;
import ru.axetta.ecafe.processor.core.report.model.totalbeneffeedreport.TotalBenefFeedData;
import ru.axetta.ecafe.processor.core.report.model.totalbeneffeedreport.TotalBenefFeedItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
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
    public static final String REPORT_NAME = "Сводный отчет по льготному питанию";
    public static final String[] TEMPLATE_FILE_NAMES = {"TotalBenefFeedReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


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

            //if (RuntimeContext.getInstance().isTestMode()) {
            //    startTime = new Date(1409877287000L);
            //    endTime = new Date(1409963687000L);
            //}
            UserOrgsAndContragents userOAC = new UserOrgsAndContragents(session, getUserId());

            parameterMap.put("startDate", CalendarUtils.dateShortToString(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToString(endTime));

            endTime = CalendarUtils.endOfDay(endTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, userOAC));


            Date generateEndTime = new Date();
            return new TotalBenefFeedReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime, UserOrgsAndContragents userOAC) throws Exception {
            Map<Long, TotalBenefFeedItem> dataMap = new HashMap<Long, TotalBenefFeedItem>();
            Map<Long,Long> mainBuildingMap = new HashMap<Long, Long>();
            retrieveAllOrgs(dataMap, mainBuildingMap, userOAC);

            retrieveStudentsCount(dataMap, mainBuildingMap);
            retrieveBeneficiaryStudentsCount(dataMap, mainBuildingMap);
            retrieveEnterEventsCount(dataMap, mainBuildingMap, startTime, endTime);
            retrieveMealCount(dataMap, mainBuildingMap, startTime, endTime);

            retrieveOrdersWithEnterEventsCount(dataMap);
            retrieveOrdersWithNoEnterEventsCount(dataMap);

            retrieveOrderedMeals(dataMap, mainBuildingMap, startTime, endTime);



            List<TotalBenefFeedData> dataList = new ArrayList<TotalBenefFeedData>();
            dataList.add(new TotalBenefFeedData());
            dataList.get(0).getItemList().addAll(dataMap.values());

            return new JRBeanCollectionDataSource(dataList);
        }

        private void retrieveOrderedMeals(Map<Long, TotalBenefFeedItem> dataMap, Map<Long, Long> mainBuildingMap, Date startDate,
                Date endDate) {
            GoodRequestsNewReportService service;
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            if (!runtimeContext.isMainNode()) {
                return;
            }
            List<GoodRequestsNewReportService.Item> items;
            try {
                persistenceSession = runtimeContext.createReportPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                service = new GoodRequestsNewReportService(persistenceSession,"И", "И", true);
                List<Long> idOfOrgList = new ArrayList<Long>(mainBuildingMap.keySet());//orgRepository.findAllActiveIds();
                items = service
                        .buildReportItems(startDate,endDate, "", 1, 1, new Date(), new Date(),
                                idOfOrgList, new ArrayList<Long>(), true, true, 1, false);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }

            for (GoodRequestsNewReportService.Item item : items) {
                if(item.getFeedingPlanTypeNum() != 0){
                    continue;
                }
                if(item.getGoodName().contains("1-4")){
                    continue;
                }

                TotalBenefFeedItem dataMapItem = null;
                for (TotalBenefFeedItem totalBenefFeedItem : dataMap.values()) {
                    if(totalBenefFeedItem.getOrgNum().equals(item.getOrgNum())){
                        dataMapItem = totalBenefFeedItem;
                        break;
                    }
                }

                if(dataMapItem == null){
                    continue;
                }
                dataMapItem.setOrderedMeals(dataMapItem.getOrderedMeals() + item.getTotalCount().intValue());

            }
        }

        private void retrieveMealCount(Map<Long, TotalBenefFeedItem> dataMap, Map<Long, Long> mainBuildingMap, Date startTime, Date endTime) {
            OrdersRepository ordersRepository = RuntimeContext.getAppContext().getBean(OrdersRepository.class);
            for (OrderItem orderItem : ordersRepository.findAllBeneficiaryComplexes(startTime, endTime)) {
                TotalBenefFeedItem totalBenefFeedItem = dataMap.get(mainBuildingMap.get(orderItem.getIdOfOrg()));
                if(totalBenefFeedItem== null){
                    continue;
                    //todo wtf
                }
                if (orderItem.getOrdertype() == 4){
                    totalBenefFeedItem.setReceiveMealBenefStudents(totalBenefFeedItem.getReceiveMealBenefStudents() + 1);
                    totalBenefFeedItem.getReceiveMealBenefStudentsList().add(new SubItem(orderItem.getIdOfClient()));
                }else if (orderItem.getOrdertype() == 6){
                    totalBenefFeedItem.setReceiveMealReserveStudents(totalBenefFeedItem.getReceiveMealReserveStudents() + 1);
                }
            }
        }

        private void retrieveOrdersWithEnterEventsCount(Map<Long, TotalBenefFeedItem> dataMap) {
            for (TotalBenefFeedItem item : dataMap.values()) {
                List<Long> tempEnterEvents = new ArrayList<Long>();
                for (SubItem subItem : item.getEnteredBenefStudentsList()) {
                    tempEnterEvents.add(subItem.getIdofclient());
                }
                for ( SubItem subItem : item.getReceiveMealBenefStudentsList()){
                    tempEnterEvents.remove(subItem.getIdofclient());
                }
                item.setNotReceiveMealEnteredBenefStudents(tempEnterEvents.size());
            }
        }


        private void retrieveOrdersWithNoEnterEventsCount(Map<Long, TotalBenefFeedItem> dataMap) {
            for (TotalBenefFeedItem item : dataMap.values()) {
                List<Long> tempReceiveMeal = new ArrayList<Long>();
                for (SubItem subItem : item.getReceiveMealBenefStudentsList()) {
                    tempReceiveMeal.add(subItem.getIdofclient());
                }
                for ( SubItem subItem : item.getEnteredBenefStudentsList()){
                    tempReceiveMeal.remove(subItem.getIdofclient());
                }
                item.setReceiveMealNotEnteredBenefStudents(tempReceiveMeal.size());
            }
        }

        private void retrieveStudentsCount(Map<Long, TotalBenefFeedItem> dataMap, Map<Long, Long> mainBuildingMap) {
            ClientDao clientDao = RuntimeContext.getAppContext().getBean(ClientDao.class);
            for (ClientCount clientCount : clientDao.findAllStudentsCount()) {
                TotalBenefFeedItem totalBenefFeedItem = dataMap.get(mainBuildingMap.get(clientCount.getIdOfOrg()));
                if(totalBenefFeedItem != null){
                    totalBenefFeedItem.setStudents(totalBenefFeedItem.getStudents() + clientCount.getCount());
                }
            }
        }


        private void retrieveBeneficiaryStudentsCount(Map<Long, TotalBenefFeedItem> dataMap,
                Map<Long, Long> mainBuildingMap) {
            ClientDao clientDao = RuntimeContext.getAppContext().getBean(ClientDao.class);
            for (ClientCount clientCount : clientDao.findAllBeneficiaryStudentsCount()) {
                TotalBenefFeedItem totalBenefFeedItem = dataMap.get(mainBuildingMap.get(clientCount.getIdOfOrg()));
                if(totalBenefFeedItem != null){
                    totalBenefFeedItem.setBenefStudents(totalBenefFeedItem.getBenefStudents() + clientCount.getCount());
                }
            }
        }


        private void retrieveEnterEventsCount(Map<Long, TotalBenefFeedItem> dataMap, Map<Long, Long> mainBuildingMap, Date startTime, Date endTime) {
            EnterEventsRepository enterEventsRepository = RuntimeContext.getAppContext().getBean(EnterEventsRepository.class);
            for (EnterEventCount enterEventCount : enterEventsRepository.findAllBeneficiaryStudentsEnterEvents(startTime, endTime)) {
                TotalBenefFeedItem totalBenefFeedItem = dataMap.get(mainBuildingMap.get(enterEventCount.getIdOfOrg()));
                if(totalBenefFeedItem != null){
                    totalBenefFeedItem.setEnteredBenefStudents(totalBenefFeedItem.getEnteredBenefStudents() + 1);
                    totalBenefFeedItem.getEnteredBenefStudentsList().add( new SubItem(enterEventCount.getIdOfOrg(),enterEventCount.getIdOfClient()) );
                }
            }
        }


        private void retrieveAllOrgs(Map<Long, TotalBenefFeedItem> totalBenefFeedItemsMap, Map<Long,Long> mainBuildingMap, UserOrgsAndContragents userOAC) {
            OrgRepository orgRepository = RuntimeContext.getAppContext().getBean(OrgRepository.class);
            OrgService orgService = RuntimeContext.getAppContext().getBean(OrgService.class);
            List<OrgItem> allNames;
            //если роль пользователя = поставщик ( = 2 ), то берем только организации привязанных контрагентов. Для остальных ролей - все организации
            if (User.DefaultRole.SUPPLIER.getIdentification().equals(userOAC.getUser().getIdOfRole())) {
                allNames = orgRepository.findAllActiveBySupplier(userOAC.getOrgs(), userOAC.getUser().getIdOfUser());
            }
            else {
                allNames = orgRepository.findAllActive();
            }

            TotalBenefFeedItem totalBenefFeedItem;
            for (OrgItem allName : allNames) {
                Org mainBulding = orgService.getMainBulding(allName.getIdOfOrg());
                mainBuildingMap.put(allName.getIdOfOrg(),mainBulding.getIdOfOrg());
                totalBenefFeedItem = new TotalBenefFeedItem(mainBulding);
                totalBenefFeedItemsMap.put(mainBulding.getIdOfOrg(), totalBenefFeedItem);
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
        return REPORT_PERIOD_PREV_DAY;
    }
}
