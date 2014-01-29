/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.dashboard;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.dashboard.data.DashboardResponse;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 06.08.12
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class DashboardServiceBean {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceBean.class);

    private static final int ID_OF_ORG_PARAM_INDEX = 0;
    private static final int ORG_NAME_PARAM_INDEX = ID_OF_ORG_PARAM_INDEX + 1;
    private static final int LAST_SUCCESSFUL_BALANCE_SYNC_TYME_PARAM_INDEX = ORG_NAME_PARAM_INDEX + 1;
    private static final int LAST_UNSUCCESSFUL_BALANCE_SYNC_TYME_PARAM_INDEX =
            LAST_SUCCESSFUL_BALANCE_SYNC_TYME_PARAM_INDEX + 1;
    private static final int FIRST_FULL_SYNC_TIME_PARAM_INDEX = LAST_UNSUCCESSFUL_BALANCE_SYNC_TYME_PARAM_INDEX + 1;
    private static final int SYNC_HISTORY_PARAM_INDEX = FIRST_FULL_SYNC_TIME_PARAM_INDEX + 1;
    private static final int NUM_OF_STUDENTS_PARAM_INDEX = SYNC_HISTORY_PARAM_INDEX + 1;
    private static final int NUM_OF_STAFF_PARAM_INDEX = NUM_OF_STUDENTS_PARAM_INDEX + 1;
    private static final int NUM_OF_STUDENT_ENTER_EVENTS_PARAM_INDEX = NUM_OF_STAFF_PARAM_INDEX + 1;
    private static final int NUM_OF_STAFF_ENTER_EVENTS_PARAM_INDEX = NUM_OF_STUDENT_ENTER_EVENTS_PARAM_INDEX + 1;
    private static final int NUM_OF_STUDENT_SOC_MENU_PARAM_INDEX = NUM_OF_STAFF_ENTER_EVENTS_PARAM_INDEX + 1;
    private static final int NUM_OF_STUDENT_MENU_PARAM_INDEX = NUM_OF_STUDENT_SOC_MENU_PARAM_INDEX + 1;
    private static final int NUM_OF_STAFF_SOC_MENU_PARAM_INDEX = NUM_OF_STUDENT_MENU_PARAM_INDEX + 1;
    private static final int NUM_OF_STAFF_MENU_PARAM_INDEX = NUM_OF_STAFF_SOC_MENU_PARAM_INDEX + 1;

    private static final int ID_OF_CONTRAGENT_PARAM_INDEX = 0;
    private static final int CONTRAGENT_NAME_PARAM_INDEX = ID_OF_CONTRAGENT_PARAM_INDEX + 1;
    private static final int LAST_OPERATION_TIME_PARAM_INDEX = CONTRAGENT_NAME_PARAM_INDEX + 1;
    private static final int NUM_OF_OPERATIONS_PARAM_INDEX = LAST_OPERATION_TIME_PARAM_INDEX + 1;

    @Autowired
    @Qualifier(value = "txManager")
    private PlatformTransactionManager txManager;

    @Autowired
    DAOService daoService;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    private DashboardResponse prepareDashboardResponse() {
        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setEduInstItemInfoList(new LinkedList<DashboardResponse.EduInstItemInfo>());
        return dashboardResponse;
    }

    public List<DashboardResponse.NamedParams> getNamedParams() {
        //Session session = null;
        try {
            Calendar now = new GregorianCalendar();
            now.setTimeInMillis(System.currentTimeMillis());
            now.set(Calendar.HOUR_OF_DAY, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);
            List<DashboardResponse.NamedParams> params = new ArrayList<DashboardResponse.NamedParams>();
            Query q = entityManager.createNativeQuery(
                    "select 'Доставлено SMS' as name, count(cf_clientsms.idofsms) as value, 'long' as type "
                            + "from cf_clientsms "
                            + "where "
                            //+ "deliverystatus=:deliveredSMSStatus and "
                            + "servicesenddate>:maxDate "
                            + "union all "
                            /*+ "select 'Количество не доставленных SMS' as name, count(cf_clientsms.idofsms) as value, 'long' as type "
                         + "from cf_clientsms "
                         + "where deliverystatus=:notDeliveredSMSStatus "
                         + "union all "*/
                            + "select 'Последнее SMS' as name, max(cf_clientsms.servicesenddate) as value, 'date' as type "
                            + "from cf_clientsms "
                            + "union all "
                            + "select '{href=NSIOrgRegistrySynchPage}Ошибок при сверке с Реестрами' as name, count(cf_registrychange_errors.idofregistrychangeerror) as value, 'long' as type "
                            + "from cf_registrychange_errors "
                            + "where comment is null or comment=''");
            //q.setParameter("deliveredSMSStatus", ClientSms.DELIVERED_TO_RECIPENT);
            q.setParameter("maxDate", now.getTimeInMillis());
            //q.setParameter("notDeliveredSMSStatus", ClientSms.NOT_DELIVERED_TO_RECIPENT);
            List queryResult = q.getResultList();
            for (Object object : queryResult) {
                Object[] result = (Object[]) object;
                String name = ((String) result[0]).trim();
                long value  = ((BigInteger) result[1]).longValue();
                String type = ((String) result[2]).trim();
                if (type.equals("long")) {
                    params.add(new DashboardResponse.NamedParams(name, value));
                } else if (type.equals("date")) {
                    params.add(new DashboardResponse.NamedParams(name, new Date(value)));
                }
            }
            return params;
        } catch (Exception e) {
            logger.error("Failed to load named params from database", e);
            return Collections.EMPTY_LIST;
        } finally {
            /*try {
                if (session != null) { HibernateUtils.close(session, logger); }
            } catch (Exception e) { }*/
        }
    }

    public DashboardResponse.OrgBasicStats getOrgBasicStats(Date dt, Long idOfOrg, int orgStatus) throws Exception {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        def.setReadOnly(true);
        TransactionStatus status = txManager.getTransaction(def);
        def.setTimeout(600 * 1000);
        DashboardResponse.OrgBasicStats basicStats = new DashboardResponse.OrgBasicStats();
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            int dom = cal.get(Calendar.DAY_OF_MONTH);

            Calendar dayStart = Calendar.getInstance();
            dayStart.set(year, month, dom, 0, 0, 0);
            Calendar dayEnd = Calendar.getInstance();
            dayEnd.set(year, month, dom + 1, 0, 0, 0);

            Date dayStartDate = dayStart.getTime();
            Date dayEndDate = dayEnd.getTime();

            String queryText = "SELECT org.idOfOrg, org.officialName, org.district, org.location, org.tag, org.lastSuccessfulBalanceSync FROM Org org WHERE 1 = 1";
            if (idOfOrg != null) {
                queryText += " AND org.idOfOrg = :idOfOrg";
            }
            if (orgStatus < 2) {
                queryText += " AND org.state = :orgStatus";
            }
            Query query = entityManager.createQuery(queryText);
            if (idOfOrg != null) {
                query.setParameter("idOfOrg", idOfOrg);
            }
            if (orgStatus < 2) {
                query.setParameter("orgStatus", orgStatus);
            }
            HashMap<Long, DashboardResponse.OrgBasicStatItem> orgStats = new HashMap<Long, DashboardResponse.OrgBasicStatItem>();
            List queryResult = query.getResultList();
            for (Object object : queryResult) {
                Object[] result = (Object[]) object;
                int n = 0;
                Long curIdOfOrg = (Long) result[n++];
                DashboardResponse.OrgBasicStatItem statItem = orgStats.get(curIdOfOrg);
                if (statItem == null) {
                    statItem = new DashboardResponse.OrgBasicStatItem();
                    orgStats.put(curIdOfOrg, statItem);
                }
                statItem.setIdOfOrg(curIdOfOrg);
                statItem.setOrgName((String) result[n++]);
                statItem.setOrgDistrict((String) result[n++]);
                statItem.setOrgLocation((String) result[n++]);
                statItem.setOrgTag((String) result[n++]);
                statItem.setOrgNameNumber(Org.extractOrgNumberFromName(statItem.getOrgName()));
                statItem.setLastSuccessfulBalanceSyncTime((Date) result[n++]);
            }
            ////
            //queryText = "SELECT cl.org.idOfOrg, count(*) FROM Client cl WHERE cl.clientGroup.compositeIdOfClientGroup.idOfClientGroup <:studentsMaxValue GROUP BY cl.org.idOfOrg";
            //query = entityManager.createQuery(queryText);
            //query.setParameter("studentsMaxValue", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            //queryResult = query.getResultList();
            //for (Object object : queryResult) {
            //    Object[] result = (Object[]) object;
            //    Long curIdOfOrg = (Long) result[0];
            //    DashboardResponse.OrgBasicStatItem statItem = orgStats.get(curIdOfOrg);
            //    if (statItem == null) {
            //        continue;
            //    }
            //    statItem.setNumberOfStudentClients((Long) result[1]);
            //}

            //// Статистика по Детям
            // ид групп которые входят в группу предопредленые
            Session session = entityManager.unwrap(Session.class);

            for (Long idOfOrgKey: orgStats.keySet()){
                Criteria groupChildrenCriteria = session.createCriteria(ClientGroup.class);
                groupChildrenCriteria.add(Restrictions.eq("compositeIdOfClientGroup.idOfOrg", idOfOrgKey));
                // исключаем предопределенные круппы
                for (ClientGroup.Predefined predefined: ClientGroup.Predefined.values()){
                    groupChildrenCriteria.add(Restrictions.ne("groupName", predefined.getNameOfGroup()).ignoreCase());
                    groupChildrenCriteria.add(Restrictions.ne("compositeIdOfClientGroup.idOfClientGroup", predefined.getValue()));
                }
                groupChildrenCriteria.add(Restrictions.ne("groupName", "Сотрудники").ignoreCase());
                groupChildrenCriteria.setProjection(Projections.property("compositeIdOfClientGroup.idOfClientGroup"));
                List<Long> clientChildrenGroups = groupChildrenCriteria.list();
                //if(!clientChildrenGroups.isEmpty()){
                //    clientPredefinedGroup.addAll(clientChildrenGroups);
                //    //childrenCount.add(Restrictions.not(Restrictions.in("idOfClientGroup", clientChildrenGroups)));
                //}
                Criteria childrenCount = session.createCriteria(Client.class);
                if(clientChildrenGroups.isEmpty()){
                    for (ClientGroup.Predefined predefined: ClientGroup.Predefined.values()){
                        groupChildrenCriteria.add(Restrictions.ne("idOfClientGroup", predefined.getValue()));
                    }
                }  else {
                    childrenCount.add(Restrictions.in("idOfClientGroup", clientChildrenGroups));
                }
                childrenCount.setProjection(Projections.projectionList()
                        .add(Projections.property("org.idOfOrg"))
                        .add(Projections.rowCount())
                        .add(Projections.groupProperty("org.idOfOrg"))
                );
                queryResult = childrenCount.list();
                for (Object object : queryResult) {
                    Object[] result = (Object[]) object;
                    Long curIdOfOrg = (Long) result[0];
                    DashboardResponse.OrgBasicStatItem statItem = orgStats.get(curIdOfOrg);
                    if (statItem == null) {
                        continue;
                    }
                    statItem.setNumberOfChildrenClients((Long) result[1]);
                }
            }

            //// Статистика по Родителям
            ClientGroup.Predefined parent = ClientGroup.Predefined.CLIENT_PARENTS;
            //Criteria parentGroupCriteria = session.createCriteria(ClientGroup.class);
            //parentGroupCriteria.add(Restrictions.eq("compositeIdOfClientGroup.idOfOrg", idOfOrg));
            //parentGroupCriteria.add(Restrictions.or(
            //        Restrictions.eq("groupName", parent.getNameOfGroup()),
            //        Restrictions.eq("compositeIdOfClientGroup.idOfClientGroup", parent.getValue())
            //));
            //parentGroupCriteria.setProjection(Projections.property("compositeIdOfClientGroup.idOfClientGroup"));
            //List<Long> clientGroups = parentGroupCriteria.list();
            Criteria parentsCount = session.createCriteria(Client.class);
            parentsCount.createCriteria("clientGroup").add(
                    Restrictions.or(
                            Restrictions.eq("groupName", parent.getNameOfGroup()),
                            Restrictions.eq("compositeIdOfClientGroup.idOfClientGroup", parent.getValue())
                    )
            );

            parentsCount.setProjection(Projections.projectionList()
                    .add(Projections.property("org.idOfOrg"))
                    .add(Projections.rowCount())
                    .add(Projections.groupProperty("org.idOfOrg"))
            );
            queryResult = parentsCount.list();
            for (Object object : queryResult) {
                Object[] result = (Object[]) object;
                Long curIdOfOrg = (Long) result[0];
                DashboardResponse.OrgBasicStatItem statItem = orgStats.get(curIdOfOrg);
                if (statItem == null) {
                    continue;
                }
                statItem.setNumberOfParentsClients((Long) result[1]);
            }



            //// Старая логика по сотрудникам
            //queryText = "SELECT cl.org.idOfOrg, count(*) FROM Client cl WHERE cl.clientGroup.compositeIdOfClientGroup.idOfClientGroup>=:nonStudentGroups AND cl.clientGroup.compositeIdOfClientGroup.idOfClientGroup<:leavingClientGroup GROUP BY cl.org.idOfOrg";
            //query = entityManager.createQuery(queryText);
            //query.setParameter("nonStudentGroups", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            //query.setParameter("leavingClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
            //queryResult = query.getResultList();
            //for (Object object : queryResult) {
            //    Object[] result = (Object[]) object;
            //    Long curIdOfOrg = (Long) result[0];
            //    DashboardResponse.OrgBasicStatItem statItem = orgStats.get(curIdOfOrg);
            //    if (statItem == null) {
            //        continue;
            //    }
            //    statItem.setNumberOfNonStudentClients((Long) result[1]);
            //}

            /// Статистика по сотрудникам
            for (Long idOfOrgKey: orgStats.keySet()){
                // соберем все группы которые должны попасть в выборку
                List<ClientGroup.Predefined> predefineds = new ArrayList<ClientGroup.Predefined>(4);
                predefineds.add(ClientGroup.Predefined.CLIENT_EMPLOYEES);
                predefineds.add(ClientGroup.Predefined.CLIENT_ADMINISTRATION);
                predefineds.add(ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES);
                //predefineds.add(ClientGroup.Predefined.CLIENT_OTHERS);

                Criteria groupEmployeesCriteria = session.createCriteria(ClientGroup.class);
                groupEmployeesCriteria.add(Restrictions.eq("compositeIdOfClientGroup.idOfOrg", idOfOrgKey));

                Disjunction or = Restrictions.disjunction();
                for (ClientGroup.Predefined predefined: predefineds){
                    or.add(Restrictions.eq("groupName", predefined.getNameOfGroup()).ignoreCase());
                    or.add(Restrictions.eq("compositeIdOfClientGroup.idOfClientGroup", predefined.getValue()));
                }
            /* В старых версия Пед. Состав назывался  Сотрудники*/
                or.add(Restrictions.eq("groupName", "Сотрудники").ignoreCase());
                groupEmployeesCriteria.add(or);
                groupEmployeesCriteria.setProjection(Projections.property("compositeIdOfClientGroup.idOfClientGroup"));

                List<Long> groupEmployees =  groupEmployeesCriteria.list();

                Criteria employeesCount = session.createCriteria(Client.class);
                if(groupEmployees.isEmpty()){
                    for (ClientGroup.Predefined predefined: predefineds){
                        employeesCount.add(Restrictions.eq("idOfClientGroup", predefined.getValue()));
                    }
                } else {
                    employeesCount.add(Restrictions.in("idOfClientGroup", groupEmployees));
                }

                employeesCount.setProjection(Projections.projectionList()
                        .add(Projections.property("org.idOfOrg"))
                        .add(Projections.rowCount())
                        .add(Projections.groupProperty("org.idOfOrg"))
                );
                queryResult = employeesCount.list();
                for (Object object : queryResult) {
                    Object[] result = (Object[]) object;
                    Long curIdOfOrg = (Long) result[0];
                    DashboardResponse.OrgBasicStatItem statItem = orgStats.get(curIdOfOrg);
                    if (statItem == null) {
                        continue;
                    }
                    statItem.setNumberOfNonStudentClients((Long) result[1]);
                }
            }

            ////
            query = entityManager.createNativeQuery(
                    "select cl.idOfOrg, count(*) from CF_Clients cl LEFT JOIN CF_Cards cr ON cr.idOfClient=cl.idOfClient WHERE cl.idOfClientGroup<=:maxStaffGroup AND cr.idOfCard IS NULL GROUP BY cl.idOfOrg");
            query.setParameter("maxStaffGroup", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            queryResult = query.getResultList();
            for (Object object : queryResult) {
                Object[] result = (Object[]) object;
                Long curIdOfOrg = Long.parseLong("" + result[0]);
                DashboardResponse.OrgBasicStatItem statItem = orgStats.get(curIdOfOrg);
                if (statItem == null) {
                    continue;
                }
                statItem.setNumberOfClientsWithoutCard(Long.parseLong("" + result[1]));
            }
            ////
            queryText = "SELECT eev.org.idOfOrg, count(*), min(eev.evtDateTime) FROM EnterEvent eev WHERE eev.evtDateTime BETWEEN :dayStart AND :dayEnd GROUP BY eev.org.idOfOrg";
            query = entityManager.createQuery(queryText);
            query.setParameter("dayStart", dayStartDate);
            query.setParameter("dayEnd", dayEndDate);
            queryResult = query.getResultList();
            for (Object object : queryResult) {
                Object[] result = (Object[]) object;
                Long curIdOfOrg = (Long) result[0];
                DashboardResponse.OrgBasicStatItem statItem = orgStats.get(curIdOfOrg);
                if (statItem == null) {
                    continue;
                }
                statItem.setNumberOfEnterEvents((Long) result[1]);
                statItem.setLastEnterEvent((Date) result[2]);
            }
            //// Дата первой транзакции платного питания, Дата первой транзакции льготного питания.
            queryText = "SELECT org.idOfOrg, min(case when o.socDiscount = 0 then a.transactionTime else null end), "
                    + " min(case when o.socDiscount > 0 then a.transactionTime else null end) \n"
                    + " FROM AccountTransaction a join a.ordersInternal o join o.org org \n"
                    + "WHERE a.transactionTime BETWEEN :dayStart AND :dayEnd GROUP BY org.idOfOrg";
            query = entityManager.createQuery(queryText);
            query.setParameter("dayStart", dayStartDate);
            query.setParameter("dayEnd", dayEndDate);
            queryResult = query.getResultList();
            for (Object object : queryResult) {
                Object[] result = (Object[]) object;
                Long curIdOfOrg = (Long) result[0];
                DashboardResponse.OrgBasicStatItem statItem = orgStats.get(curIdOfOrg);
                if (statItem == null) {
                    continue;
                }
                statItem.setFirstPayOrderDate((Date) result[1]);
                statItem.setFirstDiscountOrderDate((Date) result[2]);
            }
            ////
            queryText = "SELECT order.org.idOfOrg, count(*) FROM Order order WHERE order.socDiscount > 0 AND order.createTime BETWEEN :dayStart AND :dayEnd GROUP BY order.org.idOfOrg";
            query = entityManager.createQuery(queryText);
            query.setParameter("dayStart", dayStartDate);
            query.setParameter("dayEnd", dayEndDate);
            queryResult = query.getResultList();
            for (Object object : queryResult) {
                Object[] result = (Object[]) object;
                Long curIdOfOrg = (Long) result[0];
                DashboardResponse.OrgBasicStatItem statItem = orgStats.get(curIdOfOrg);
                if (statItem == null) {
                    continue;
                }
                statItem.setNumberOfDiscountOrders((Long) result[1]);
            }
            ////
            queryText = "SELECT order.org.idOfOrg, count(*) FROM Order order WHERE order.socDiscount = 0 AND order.createTime BETWEEN :dayStart AND :dayEnd GROUP BY order.org.idOfOrg";
            query = entityManager.createQuery(queryText);
            query.setParameter("dayStart", dayStartDate);
            query.setParameter("dayEnd", dayEndDate);
            queryResult = query.getResultList();
            for (Object object : queryResult) {
                Object[] result = (Object[]) object;
                Long curIdOfOrg = (Long) result[0];
                DashboardResponse.OrgBasicStatItem statItem = orgStats.get(curIdOfOrg);
                if (statItem == null) {
                    continue;
                }
                statItem.setNumberOfPayOrders((Long) result[1]);
            }
            ////
            queryText = "select cf_orders.idoforg, count(distinct cf_orders.idoforder) from cf_orders left join cf_orderdetails on cf_orders.idoforder=cf_orderdetails.idoforder and cf_orders.idoforg=cf_orderdetails.idoforg where lower(cf_orderdetails.menugroup)=:groupName AND cf_orders.createddate BETWEEN :dayStart AND :dayEnd group by cf_orders.idoforg";
            query = entityManager.createNativeQuery(queryText);
            query.setParameter("groupName", "вендинг");
            query.setParameter("dayStart", dayStartDate.getTime());
            query.setParameter("dayEnd", dayEndDate.getTime());
            queryResult = query.getResultList();
            for (Object object : queryResult) {
                Object[] result = (Object[]) object;
                Long curIdOfOrg = Long.parseLong(""+result[0]);
                DashboardResponse.OrgBasicStatItem statItem = orgStats.get(curIdOfOrg);
                if (statItem == null) {
                    continue;
                }
                statItem.setNumberOfVendingOrders(Long.parseLong(""+ result[1]));
            }
            for (Long orgID : orgStats.keySet()) {
                DashboardResponse.OrgBasicStatItem statItem = orgStats.get(orgID);
                if (statItem.getNumberOfVendingOrders () == null)
                {
                    statItem.setNumberOfVendingOrders(0L);
                }
            }



            //  Заполняем процентные показатели
            Map<Long, Integer> studentEnters = daoService
                    .getOrgEntersCountByGroupType(dayStartDate, dayEndDate, DAOService.GROUP_TYPE_STUDENTS);
            Map<Long, Integer> employeeEnters = daoService
                    .getOrgEntersCountByGroupType(dayStartDate, dayEndDate, DAOService.GROUP_TYPE_NON_STUDENTS);
            Map<Long, Integer> studentPays = daoService
                    .getOrgOrdersCountByGroupType(dayStartDate, dayEndDate, DAOService.GROUP_TYPE_STUDENTS, true);
            Map<Long, Integer> employeePays = daoService
                    .getOrgOrdersCountByGroupType(dayStartDate, dayEndDate, DAOService.GROUP_TYPE_NON_STUDENTS, true);
            Map<Long, Integer> studentDiscounts = daoService
                    .getProposalOrgDiscounsCountByGroupType(dayStartDate, dayEndDate, DAOService.GROUP_TYPE_STUDENTS);
            Map<Long, Integer> employeeDiscounts = daoService
                    .getProposalOrgDiscounsCountByGroupType(dayStartDate, dayEndDate,
                            DAOService.GROUP_TYPE_NON_STUDENTS);
            Map<Long, Integer> studentUniqueOrders = daoService
                    .getOrgUniqueOrdersCountByGroupType(dayStartDate, dayEndDate, DAOService.GROUP_TYPE_STUDENTS);
            Map<Long, Integer> employeeUniqueOrders = daoService
                    .getOrgUniqueOrdersCountByGroupType(dayStartDate, dayEndDate, DAOService.GROUP_TYPE_NON_STUDENTS);

            for (Long orgID : orgStats.keySet()) {
                DashboardResponse.OrgBasicStatItem statItem = orgStats.get(orgID);
                if (statItem == null) {
                    continue;
                }
                Integer studentsEntersCount = studentEnters.get(orgID);
                Integer employeesEntersCount = employeeEnters.get(orgID);
                Integer studentsPayOrdersCount = studentPays.get(orgID);
                Integer employeePayOrdersCount = employeePays.get(orgID);
                Integer studentsDiscountsCount = zeroIfNull (studentDiscounts.get(orgID));
                Integer employeeDiscountsCount = zeroIfNull (employeeDiscounts.get(orgID));
                Integer studentsUniqueCount = studentUniqueOrders.get(orgID);
                Integer employeeUniqueCount = employeeUniqueOrders.get(orgID);
                double per1 = 0D, per2 = 0D, per3 = 0D, per4 = 0D, per5 = 0D, per6 = 0D;
                if (studentsEntersCount != null && statItem.getNumberOfEnterEvents() != 0) {
                    per1 = (double) studentsEntersCount / (double) statItem.getNumberOfEnterEvents();
                }
                if (employeesEntersCount != null && statItem.getNumberOfEnterEvents() != 0) {
                    per2 = (double) employeesEntersCount / (double) statItem.getNumberOfEnterEvents();
                }
                //if(statItem.getNumberOfStudentClients()+statItem.getNumberOfNonStudentClients()!=0){
                //    if (studentsPayOrdersCount != null && statItem.getNumberOfPayOrders() != 0) {
                //        per3 = (double) studentsPayOrdersCount / (double) (statItem.getNumberOfStudentClients() +
                //                statItem.getNumberOfNonStudentClients());
                //    }
                //    if (employeePayOrdersCount != null && statItem.getNumberOfPayOrders() != 0) {
                //        per4 = (double) employeePayOrdersCount / (double) (statItem.getNumberOfNonStudentClients() +
                //                statItem.getNumberOfStudentClients());
                //    }
                //}
                if(statItem.getNumberOfChildrenClients()+statItem.getNumberOfNonStudentClients()!=0){
                    if (studentsPayOrdersCount != null && statItem.getNumberOfPayOrders() != 0) {
                        per3 = (double) studentsPayOrdersCount / (double) (statItem.getNumberOfChildrenClients() +
                                statItem.getNumberOfNonStudentClients());
                    }
                    if (employeePayOrdersCount != null && statItem.getNumberOfPayOrders() != 0) {
                        per4 = (double) employeePayOrdersCount / (double) (statItem.getNumberOfNonStudentClients() +
                                statItem.getNumberOfChildrenClients());
                    }
                }
                if (studentsUniqueCount != null && studentsDiscountsCount != 0) {
                    per5 = (double) studentsUniqueCount / (double) studentsDiscountsCount;
                }
                if (employeeUniqueCount != null && employeeDiscountsCount != 0) {
                    per6 = (double) employeeUniqueCount / (double) employeeDiscountsCount;
                }
                statItem.setNumberOfStudentsWithEnterEventsPercent(beautifyPercent (per1));
                statItem.setNumberOfEmployeesWithEnterEventsPercent(beautifyPercent (per2));
                statItem.setNumberOfStudentsWithPayedOrdersPercent(beautifyPercent (per3));
                statItem.setNumberOfEmployeesWithPayedOrdersPercent(beautifyPercent (per4));
                statItem.setNumberOfStudentsWithDiscountOrdersPercent(beautifyPercent (per5));
                statItem.setNumberOfEmployeesWithDiscountOrdersPercent(beautifyPercent (per6));
            }
            ////
            for (Map.Entry<Long, DashboardResponse.OrgBasicStatItem> e : orgStats.entrySet()) {
                basicStats.getOrgBasicStatItems().add(e.getValue());
            }


            /*
            HashMap<Long, DashboardResponse.OrgBasicStatItem> orgStats = new HashMap<Long, DashboardResponse.OrgBasicStatItem>();
            if (idOfOrg!=null) queryText+=" WHERE org.idOfOrg=:idOfOrg";
            Query query = entityManager.createQuery(queryText);
            query.setParameter("contractState", Client.ACTIVE_CONTRACT_STATE);
            List queryResult = query.getResultList();


            orgBasicStatItem.setNumberOfClients((Long)result[n++]);
            orgBasicStatItem.setNumberOfEnterEvents((Long)result[n++]);
            orgBasicStatItem.setNumberOfDiscountOrders((Long)result[n++]);
            orgBasicStatItem.setNumberOfPayOrders((Long)result[n++]);



            String queryText = "SELECT org.idOfOrg, org.officialName, org.district, org.location, org.tag, org.lastSuccessfulBalanceSync, "
                                                + "(SELECT count(*) FROM Client cl WHERE cl.org.idOfOrg = org.idOfOrg AND cl.contractState=:contractState) AS numOfClients, "
                                                + "(SELECT count(*) FROM EnterEvent eev WHERE eev.org.idOfOrg = org.idOfOrg AND eev.evtDateTime BETWEEN :dayStart AND :dayEnd) AS numOfEnterEvents, "
                                                + "(SELECT count(*) FROM Order order WHERE order.org.idOfOrg = org.idOfOrg AND order.socDiscount > 0 AND order.createTime BETWEEN :dayStart AND :dayEnd) AS numOfSocOrders, "
                                                + "(SELECT count(*) FROM Order order WHERE order.org.idOfOrg = org.idOfOrg AND order.socDiscount = 0 AND order.createTime BETWEEN :dayStart AND :dayEnd) AS numOfPayOrders "
                                                + "FROM Org org";
            if (idOfOrg!=null) queryText+=" WHERE org.idOfOrg=:idOfOrg";
            Query query = entityManager.createQuery(queryText);

            if (idOfOrg!=null) query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("contractState", Client.ACTIVE_CONTRACT_STATE);

            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            int dom = cal.get(Calendar.DAY_OF_MONTH);

            Calendar dayStart = Calendar.getInstance();
            dayStart.set(year, month, dom, 0, 0, 0);
            Calendar dayEnd = Calendar.getInstance();
            dayEnd.set(year, month, dom + 1, 0, 0, 0);

            Date dayStartDate = dayStart.getTime();
            Date dayEndDate = dayEnd.getTime();

            query.setParameter("dayStart", dayStartDate);
            query.setParameter("dayEnd", dayEndDate);

            List queryResult = query.getResultList();

            for (Object object : queryResult) {
                DashboardResponse.OrgBasicStatItem orgBasicStatItem = new DashboardResponse.OrgBasicStatItem();
                Object[] result = (Object[]) object;

                int n=0;
                orgBasicStatItem.setIdOfOrg((Long) result[n++]);
                orgBasicStatItem.setOrgName((String) result[n++]);
                orgBasicStatItem.setOrgDistrict((String) result[n++]);
                orgBasicStatItem.setOrgLocation((String) result[n++]);
                orgBasicStatItem.setOrgTag((String) result[n++]);
                orgBasicStatItem.setOrgNameNumber(Org.extractOrgNumberFromName(orgBasicStatItem.getOrgName()));
                orgBasicStatItem.setLastSuccessfulBalanceSyncTime((Date)result[n++]);
                orgBasicStatItem.setNumberOfClients((Long)result[n++]);
                orgBasicStatItem.setNumberOfEnterEvents((Long)result[n++]);
                orgBasicStatItem.setNumberOfDiscountOrders((Long)result[n++]);
                orgBasicStatItem.setNumberOfPayOrders((Long)result[n++]);
                basicStats.getOrgBasicStatItems().add(orgBasicStatItem);
            }*/
        } catch (Exception e) {
            txManager.rollback(status);
            throw e;
        }
        txManager.commit(status);
        return basicStats;
    }


    public int zeroIfNull (Integer val)
    {
        return val == null ? 0 : val;
    }


    public double beautifyPercent (double percent)
    {
        return new BigDecimal(percent).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public DashboardResponse getOrgInfo(DashboardResponse dashboardResponse, Date dt, Long idOfOrg) throws Exception {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        def.setTimeout(600 * 1000);
        try {
            String queryText =
                    "SELECT DISTINCT " + "org.idOfOrg, org.officialName, " + "org.lastSuccessfulBalanceSync, "
                            + "org.lastUnSuccessfulBalanceSync, " + "min(sh.syncStartTime) AS firstSyncTime, "
                            + "(SELECT ish FROM SyncHistory ish WHERE ish.syncStartTime = max(sh.syncStartTime)) AS lastSyncHistoryRecord, "
                            + "(SELECT count(*) FROM Client cl WHERE cl.org.idOfOrg = org.idOfOrg AND cl.contractState = :contractState AND cl.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :studentsMinValue AND :studentsMaxValue) AS numOfStudents, "
                            + "(SELECT count(*) FROM Client cl WHERE cl.org.idOfOrg = org.idOfOrg AND cl.contractState = :contractState AND cl.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :staffMinValue AND :staffMaxValue) AS numOfStaff, "
                            + "(SELECT count(*) FROM EnterEvent eev WHERE eev.org.idOfOrg = org.idOfOrg AND eev.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :studentsMinValue AND :studentsMaxValue AND eev.evtDateTime BETWEEN :dayStart AND :dayEnd) AS numOfStudentsEnterEvents, "
                            + "(SELECT count(*) FROM EnterEvent eev WHERE eev.org.idOfOrg = org.idOfOrg AND eev.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :staffMinValue AND :staffMaxValue AND eev.evtDateTime BETWEEN :dayStart AND :dayEnd) AS numOfStaffEnterEvents, "
                            + "(SELECT count(*) FROM Order order WHERE order.org.idOfOrg = org.idOfOrg AND order.socDiscount > 0 AND order.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :studentsMinValue AND :studentsMaxValue AND order.createTime BETWEEN :dayStart AND :dayEnd) AS numOfStudentSocMenu, "
                            + "(SELECT count(*) FROM Order order WHERE order.org.idOfOrg = org.idOfOrg AND order.socDiscount = 0 AND order.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :studentsMinValue AND :studentsMaxValue AND order.createTime BETWEEN :dayStart AND :dayEnd) AS numOfStudentMenu, "
                            + "(SELECT count(*) FROM Order order WHERE order.org.idOfOrg = org.idOfOrg AND order.socDiscount > 0 AND order.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :staffMinValue AND :staffMaxValue AND order.createTime BETWEEN :dayStart AND :dayEnd) AS numOfStaffSocMenu, "
                            + "(SELECT count(*) FROM Order order WHERE order.org.idOfOrg = org.idOfOrg AND order.socDiscount = 0 AND order.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :staffMinValue AND :staffMaxValue AND order.createTime BETWEEN :dayStart AND :dayEnd) AS numOfStaffMenu "
                            + "FROM Org org LEFT OUTER JOIN org.syncHistoriesInternal sh ";
            if (idOfOrg != null) {
                queryText += " WHERE org.idOfOrg=:idOfOrg";
            }
            queryText += " GROUP BY org";
            Query query = entityManager.createQuery(queryText);

            if (idOfOrg != null) {
                query.setParameter("idOfOrg", idOfOrg);
            }
            query.setParameter("contractState", Client.ACTIVE_CONTRACT_STATE);
            query.setParameter("studentsMinValue", ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue());
            query.setParameter("studentsMaxValue", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("staffMinValue", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("staffMaxValue", ClientGroup.Predefined.CLIENT_PARENTS.getValue());

            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            int dom = cal.get(Calendar.DAY_OF_MONTH);

            Calendar dayStart = Calendar.getInstance();
            dayStart.set(year, month, dom, 0, 0, 0);
            Calendar dayEnd = Calendar.getInstance();
            dayEnd.set(year, month, dom + 1, 0, 0, 0);

            Date dayStartDate = dayStart.getTime();
            Date dayEndDate = dayEnd.getTime();

            query.setParameter("dayStart", dayStartDate);
            query.setParameter("dayEnd", dayEndDate);

            List queryResult = query.getResultList();
            Date timestamp = new Date();

            List<DashboardResponse.EduInstItemInfo> eduInstItemInfoList = dashboardResponse.getEduInstItemInfoList();
            for (Object object : queryResult) {
                DashboardResponse.EduInstItemInfo eduInstItemInfo = new DashboardResponse.EduInstItemInfo();
                try {
                    eduInstItemInfo.setTimestamp(timestamp);
                    Object[] result = (Object[]) object;

                    eduInstItemInfo.setIdOfOrg((Long) result[ID_OF_ORG_PARAM_INDEX]);
                    eduInstItemInfo.setOrgName((String) result[ORG_NAME_PARAM_INDEX]);
                    eduInstItemInfo.setOrgNameNumber(Org.extractOrgNumberFromName(eduInstItemInfo.getOrgName()));
                    eduInstItemInfo.setLastSuccessfulBalanceSyncTime(
                            (Date) result[LAST_SUCCESSFUL_BALANCE_SYNC_TYME_PARAM_INDEX]);
                    eduInstItemInfo.setLastUnSuccessfulBalanceSyncTime(
                            (Date) result[LAST_UNSUCCESSFUL_BALANCE_SYNC_TYME_PARAM_INDEX]);
                    eduInstItemInfo.setFirstFullSyncTime((Date) result[FIRST_FULL_SYNC_TIME_PARAM_INDEX]);

                    SyncHistory syncHistory = (SyncHistory) result[SYNC_HISTORY_PARAM_INDEX];
                    if (syncHistory != null) {
                        eduInstItemInfo.setLastFullSyncTime(syncHistory.getSyncStartTime());
                        eduInstItemInfo.setLastSyncErrors(syncHistory.getSyncResult() != 0);
                    }

                    long numOfStudents = (Long) result[NUM_OF_STUDENTS_PARAM_INDEX];
                    long numOfStaff = (Long) result[NUM_OF_STAFF_PARAM_INDEX];

                    long numOfStudentEnterEvents = (Long) result[NUM_OF_STUDENT_ENTER_EVENTS_PARAM_INDEX];
                    long numOfStaffEnterEvents = (Long) result[NUM_OF_STAFF_ENTER_EVENTS_PARAM_INDEX];

                    long numOfStudentSocMenu = (Long) result[NUM_OF_STUDENT_SOC_MENU_PARAM_INDEX];
                    long numOfStudentMenu = (Long) result[NUM_OF_STUDENT_MENU_PARAM_INDEX];

                    long numOfStaffSocMenu = (Long) result[NUM_OF_STAFF_SOC_MENU_PARAM_INDEX];
                    long numOfStaffMenu = (Long) result[NUM_OF_STAFF_MENU_PARAM_INDEX];

                    if (numOfStudents > 0) {
                        eduInstItemInfo.setNumberOfPassagesPerNumOfStudents(numOfStudentEnterEvents / numOfStudents);
                        eduInstItemInfo.setNumberOfPaidMealsPerNumOfStudents(numOfStudentMenu / numOfStudents);
                        eduInstItemInfo
                                .setNumberOfReducedPriceMealsPerNumOfStudents(numOfStudentSocMenu / numOfStudents);
                    }

                    if (numOfStaff > 0) {
                        eduInstItemInfo.setNumberOfPassagesPerNumOfStaff(numOfStaffEnterEvents / numOfStaff);
                        eduInstItemInfo.setNumberOfPaidMealsPerNumOfStaff(numOfStaffMenu / numOfStaff);
                        eduInstItemInfo.setNumberOfReducedPriceMealsPerNumOfStaff(numOfStaffSocMenu / numOfStaff);
                    }
                } catch (Exception e) {
                    eduInstItemInfo.setError(e.getMessage());
                    logger.error("Ошибка подготовки данных", e);
                } finally {
                    eduInstItemInfoList.add(eduInstItemInfo);
                }
            }
        } catch (Exception e) {
            txManager.rollback(status);
            throw e;
        }
        txManager.commit(status);

        return dashboardResponse;
    }

    public DashboardResponse.PaymentSystemStats getPaymentSystemInfo(Date dt) {
        DashboardResponse.PaymentSystemStats paymentSystemStats = new DashboardResponse.PaymentSystemStats();
        //// получение данных по платежам
        List<Object[]> lastTransactionStats = daoService.getMonitoringPayLastTransactionStats();
        LinkedList<DashboardResponse.PaymentSystemStatItem> payStatItems = new LinkedList<DashboardResponse.PaymentSystemStatItem>();
        for (Object[] r : lastTransactionStats) {
            DashboardResponse.PaymentSystemStatItem psi = new DashboardResponse.PaymentSystemStatItem();
            psi.setIdOfContragent(Long.parseLong("" + r[0]));
            psi.setContragentName((String) r[1]);
            psi.setLastOperationTime(new Date(Long.parseLong("" + r[2])));
            payStatItems.add(psi);
        }
        List<Object[]> monitoringPayDayTransactionsStats = daoService
                .getMonitoringPayDayTransactionsStats(CalendarUtils.truncateToDayOfMonth(dt),
                        CalendarUtils.truncateToDayOfMonth(CalendarUtils.addDays(dt, 1)));
        for (Object[] r : monitoringPayDayTransactionsStats) {
            Long caId = Long.parseLong("" + r[0]);
            DashboardResponse.PaymentSystemStatItem psi = null;
            for (DashboardResponse.PaymentSystemStatItem psiCursor : payStatItems) {
                if (psiCursor.getIdOfContragent() == caId) {
                    psi = psiCursor;
                    break;
                }
            }
            if (psi == null) {
                psi = new DashboardResponse.PaymentSystemStatItem();
                psi.setIdOfContragent(caId);
                psi.setContragentName((String) r[1]);
                payStatItems.add(psi);
            }
            psi.setNumOfOperations(Long.parseLong(r[2] + ""));
        }
        paymentSystemStats.setPaymentSystemItemInfos(payStatItems);
        return paymentSystemStats;
    }

    public DashboardResponse.OrgSyncStats getOrgSyncInfo() {
        DashboardResponse.OrgSyncStats orgSyncStats = new DashboardResponse.OrgSyncStats();
        ///// получение данных по сихронизации
        List<Org> orgs = daoService.getOrderedSynchOrgsList(true);
        LinkedList<DashboardResponse.OrgSyncStatItem> items = new LinkedList<DashboardResponse.OrgSyncStatItem>();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        for (Org org : orgs) {
            //items.add(new DashboardResponse.OrgSyncStatItem(org.getShortName(), org.getLastSuccessfulBalanceSync(),
            //        org.getLastUnSuccessfulBalanceSync(),
            //        runtimeContext.getProcessor().getOrgSyncAddress(org.getIdOfOrg())));
            String tags = parseTags(org.getTag());
            ;
            items.add(new DashboardResponse.OrgSyncStatItem(org.getIdOfOrg(), org.getShortName(), tags, org.getLastSuccessfulBalanceSync(),
                    org.getLastUnSuccessfulBalanceSync(),org.getRemoteAddress(), org.getClientVersion(),
                    daoService.getSynchErrorsCount(org)));

        }
        orgSyncStats.setOrgSyncStatItems(items);
        return orgSyncStats;
    }

    public String parseTags(String orgTagsStr) {
        if (orgTagsStr == null || orgTagsStr.length() < 1) {
            return "";
        }
        String allowedTagsStr = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_MSK_MONITORING_ALLOWED_TAGS);
        if (allowedTagsStr == null || allowedTagsStr.length() < 1) {
            return "";
        }
        String allowedTags [] = allowedTagsStr.split(";");
        String orgTags [] = orgTagsStr.split(";");
        String result = "";
        for (String allowedTag : allowedTags) {
            for (String orgTag : orgTags) {
                if (allowedTag.trim().equals(orgTag.trim())) {
                    if (result.length() > 0) {
                        result = result + "<br/>";
                    }
                    result = result + allowedTag;
                }
            }
        }
        return result;
    }


    public DashboardResponse getInfoForDashboard() throws Exception {
        DashboardResponse dashboardResponse = prepareDashboardResponse();
        dashboardResponse = getOrgInfo(dashboardResponse, new Date(), null);
        dashboardResponse.setPaymentSystemStats(getPaymentSystemInfo(new Date()));
        return dashboardResponse;
    }

    public DashboardResponse getInfoForDashboardForDateAndOrg(Date dt, Long idOfOrg) throws Exception {
        DashboardResponse dashboardResponse = prepareDashboardResponse();
        dashboardResponse = getOrgInfo(dashboardResponse, dt, idOfOrg);
        dashboardResponse.setPaymentSystemStats(getPaymentSystemInfo(dt));
        return dashboardResponse;
    }

    @SuppressWarnings("unchecked")
    public List<DashboardResponse.MenuLastLoadItem> getMenuLastLoad() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        def.setReadOnly(true);
        TransactionStatus status = txManager.getTransaction(def);
        List<DashboardResponse.MenuLastLoadItem> items = new ArrayList<DashboardResponse.MenuLastLoadItem>();
        try {
            // Извлекаем посл.дату загрузки меню по орг-ям с типом "Комбинат питания".
            Query query = entityManager.createQuery("select o.idOfOrg, o.shortName, max(m.createTime) \n"
                    + "from Menu m join m.org o where o.refectoryType = :type group by o.idOfOrg, o.shortName")
                    .setParameter("type", Org.REFECTORY_TYPE_FOOD_FACTORY);
            List<Object[]> res = query.getResultList();
            for (Object[] record : res) {
                DashboardResponse.MenuLastLoadItem item = new DashboardResponse.MenuLastLoadItem();
                item.setContragent((String) record[1]);
                item.setLastLoadTime((Date) record[2]);
                items.add(item);
            }
            txManager.commit(status);
        } catch (Exception e) {
            txManager.rollback(status);
            throw new RuntimeException(e.getMessage());
        }
        return items;
    }
}
