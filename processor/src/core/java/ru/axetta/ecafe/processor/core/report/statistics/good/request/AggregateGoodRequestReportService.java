package ru.axetta.ecafe.processor.core.report.statistics.good.request;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.report.BasicReportForOrgJob;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.05.13
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */
@Component
@Transactional
public class AggregateGoodRequestReportService {

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    public List<AggregateGoodRequestReportItem> fetchAggregateGoodRequestReportItems(List<Long> idOfSourceOrgList,List<Long> idOfEduList, Date startDate, Date endDate){
        List<AggregateGoodRequestReportItem> aggregateGoodRequestReportItems = new ArrayList<AggregateGoodRequestReportItem>();
        Session session = entityManager.unwrap(Session.class);
        String sql;
        Query query;

        Criteria orgCriteria = session.createCriteria(Org.class);
        orgCriteria.createAlias("sourceMenuOrgs", "sm").add(Restrictions.in("sm.idOfOrg", idOfSourceOrgList));
        if(!(idOfEduList==null || idOfEduList.isEmpty())) {
            orgCriteria.add(Restrictions.in("idOfOrg", idOfEduList));
        }
        orgCriteria.setProjection(Projections.projectionList()
                .add(Projections.property("idOfOrg"))
                .add(Projections.property("shortName"))
                .add(Projections.property("officialName"))
                .add(Projections.property("address"))
                .add(Projections.property("sm.idOfOrg"))
                .add(Projections.property("sm.shortName"))
                .add(Projections.property("sm.officialName"))
                .add(Projections.property("sm.address"))
        );
        List list = orgCriteria.list();

        HashMap<BasicReportForOrgJob.OrgShortItem, List<BasicReportForOrgJob.OrgShortItem>> map = new HashMap<BasicReportForOrgJob.OrgShortItem, List<BasicReportForOrgJob.OrgShortItem>>();
        /* Строим отображения ключ (Поставшик) -> значения (Образовательные учереждения) */
        for (Object entity: list){
            Object[] row = (Object[]) entity;
            BasicReportForOrgJob.OrgShortItem educationItem = new BasicReportForOrgJob.OrgShortItem(Long.parseLong(row[0].toString()), row[1].toString(), row[2].toString(), row[3].toString());
            BasicReportForOrgJob.OrgShortItem sourceItem = new BasicReportForOrgJob.OrgShortItem(Long.parseLong(row[4].toString()),row[5].toString(),row[6].toString(),row[7].toString());
            if(map.keySet().contains(sourceItem)){
                List<BasicReportForOrgJob.OrgShortItem> l = map.get(sourceItem);
                if(l==null || l.isEmpty()){
                    l = new ArrayList<BasicReportForOrgJob.OrgShortItem>();
                }
                l.add(educationItem);
                map.put(sourceItem,l);
            } else {
                List<BasicReportForOrgJob.OrgShortItem> l = new ArrayList<BasicReportForOrgJob.OrgShortItem>();
                l.add(educationItem);
                map.put(sourceItem,l);
            }
        }
        for (BasicReportForOrgJob.OrgShortItem item: map.keySet()){
            for (BasicReportForOrgJob.OrgShortItem edu: map.get(item)){
                /* Строим отображения ключ (Требования-заявка) -> значения (Позиции заявок) */
                HashMap<HashMap.SimpleEntry<String,Date>, List<HashMap.SimpleEntry<String,HashMap.SimpleEntry<Long, Long>>>> requestMap =
                        new HashMap<AbstractMap.SimpleEntry<String, Date>, List<AbstractMap.SimpleEntry<String, HashMap.SimpleEntry<Long, Long>>>>();
                List requests = new ArrayList();
                sql = "select request.number, request.doneDate, position.totalCount/1000, position.dailySampleCount/1000, product.productName"
                        + "                from GoodRequest request"
                        + "                join request.goodRequestPositionInternal position"
                        + "                right join position.product product"
                        + "                where request.orgOwner = :eduid and request.state = :state "
                        + "                and request.doneDate between :startDate and :endDate "
                        + "                order by request.doneDate desc";
                query = session.createQuery(sql);
                query.setParameter("eduid", edu.getIdOfOrg());
                query.setParameter("state", DocumentState.FOLLOW);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                List requestsGoods = query.list();

                if(!(requestsGoods==null || requestsGoods.isEmpty())){
                    requests.addAll(requestsGoods);
                }
                sql = "select request.number, request.doneDate, position.totalCount/1000, position.dailySampleCount/1000, good.nameOfGood"
                        + "                from GoodRequest request"
                        + "                join request.goodRequestPositionInternal position"
                        + "                right join position.good good"
                        + "                where request.orgOwner = :eduid and request.state = :state "
                        + "                and request.doneDate between :startDate and :endDate "
                        + "                order by request.doneDate desc";
                query = session.createQuery(sql);
                query.setParameter("eduid", edu.getIdOfOrg());
                query.setParameter("state", DocumentState.FOLLOW);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                List requestsProducts = query.list();
                if(!(requestsProducts==null || requestsProducts.isEmpty())){
                    requests.addAll(requestsProducts);
                }
                if(!(requests==null || requests.isEmpty())){
                    for (Object o: requests){
                        Object[] row = (Object[]) o;
                        HashMap.SimpleEntry<String,Date> request = new HashMap.SimpleEntry<String,Date>(row[0].toString(), (Date) row[1]);
                        Long dcount = null;
                        if(row[3]!=null){
                            dcount = Long.parseLong(row[3].toString());
                        }
                        HashMap.SimpleEntry<Long, Long> counts =
                                new HashMap.SimpleEntry<Long, Long>(Long.parseLong(row[2].toString()), dcount);
                        HashMap.SimpleEntry<String, HashMap.SimpleEntry<Long, Long>> position =
                                new HashMap.SimpleEntry<String,HashMap.SimpleEntry<Long, Long>>(row[4].toString(), counts);
                        if(requestMap.keySet().contains(request)){
                            List<HashMap.SimpleEntry<String,HashMap.SimpleEntry<Long, Long>>> p = requestMap.get(request);
                            if(p==null || p.isEmpty()){
                                p = new ArrayList<HashMap.SimpleEntry<String,HashMap.SimpleEntry<Long, Long>>>();
                            }
                            p.add(position);
                            requestMap.put(request,p);
                        } else {
                            List<HashMap.SimpleEntry<String,HashMap.SimpleEntry<Long, Long>>> p
                                    = new ArrayList<HashMap.SimpleEntry<String,HashMap.SimpleEntry<Long, Long>>>();
                            p.add(position);
                            requestMap.put(request,p);
                        }
                    }
                    for (HashMap.SimpleEntry<String,Date> requestEntity: requestMap.keySet()){
                        List<Commodity> commodityList = new ArrayList<Commodity>();
                        for (HashMap.SimpleEntry<String,HashMap.SimpleEntry<Long, Long>> positionEntity: requestMap.get(requestEntity)){
                            Commodity commodity = new Commodity(positionEntity.getKey(), positionEntity.getValue().getKey(),
                                    positionEntity.getValue().getValue());
                            commodityList.add(commodity);
                        }
                        AggregateGoodRequestReportItem aggregateGoodRequestReportItem = new AggregateGoodRequestReportItem(
                                requestEntity.getKey(),
                                item.getIdOfOrg(),
                                item.getShortName(),
                                edu.getIdOfOrg(),
                                Org.extractOrgNumberFromName(edu.getOfficialName()),
                                edu.getShortName(),
                                commodityList,
                                requestEntity.getValue()
                        );
                        aggregateGoodRequestReportItems.add(aggregateGoodRequestReportItem);
                    }

                }
            }
        }
        Collections.sort(aggregateGoodRequestReportItems, new ReportComparator());

        return aggregateGoodRequestReportItems;
    }

   /* public List<AggregateGoodRequestReportItem> fetchAggregateGoodRequestReportItems(List<Long> idOfSourceOrgList, Date startDate, Date endDate){
        List<AggregateGoodRequestReportItem> aggregateGoodRequestReportItems = new ArrayList<AggregateGoodRequestReportItem>();
        Random random = new Random(System.currentTimeMillis());
        Session session = ((Session) entityManager.getDelegate());
        String sql = "select org.idOfOrg, org.shortName, org.officialName, org.address, sourceMenu.idOfOrg,  "
                + "sourceMenu.shortName, sourceMenu.officialName, sourceMenu.address "
                + "from Org org "
                + "join org.sourceMenuOrgs sourceMenu "
                + "where sourceMenu.idOfOrg in (:idOfSourceOrgList) "
                + "order by sourceMenu.idOfOrg , org.idOfOrg";
        Query query = session.createQuery(sql);
        query.setParameterList("idOfSourceOrgList",idOfSourceOrgList);

        List list = query.list();
        HashMap<BasicReportForOrgJob.OrgShortItem, List<BasicReportForOrgJob.OrgShortItem>> map = new HashMap<BasicReportForOrgJob.OrgShortItem, List<BasicReportForOrgJob.OrgShortItem>>();
        *//* Строим отображения ключ (Поставшик) -> значения (Образовательные учереждения) *//*
        for (Object entity: list){
            Object[] row = (Object[]) entity;
            BasicReportForOrgJob.OrgShortItem educationItem = new BasicReportForOrgJob.OrgShortItem(Long.parseLong(row[0].toString()),row[1].toString(),row[2].toString(),row[3].toString());
            BasicReportForOrgJob.OrgShortItem sourceItem = new BasicReportForOrgJob.OrgShortItem(Long.parseLong(row[4].toString()),row[5].toString(),row[6].toString(),row[7].toString());
            if(map.keySet().contains(sourceItem)){
                List<BasicReportForOrgJob.OrgShortItem> l = map.get(sourceItem);
                if(l==null || l.isEmpty()){
                    l = new ArrayList<BasicReportForOrgJob.OrgShortItem>();
                }
                l.add(educationItem);
                map.put(sourceItem,l);
            } else {
                List<BasicReportForOrgJob.OrgShortItem> l = new ArrayList<BasicReportForOrgJob.OrgShortItem>();
                l.add(educationItem);
                map.put(sourceItem,l);
            }
        }
        for (BasicReportForOrgJob.OrgShortItem item: map.keySet()){
            for (BasicReportForOrgJob.OrgShortItem edu: map.get(item)){
                *//* Строим отображения ключ (Требования-заявка) -> значения (Позиции заявок) *//*
                HashMap<HashMap.SimpleEntry<String,Date>, List<HashMap.SimpleEntry<String,Long>>> requestMap = new HashMap<AbstractMap.SimpleEntry<String, Date>, List<AbstractMap.SimpleEntry<String, Long>>>();
                List requests = new ArrayList();
                sql = "select request.number, request.doneDate, position.totalCount/1000, product.productName"
                        + "                from GoodRequest request"
                        + "                join request.goodRequestPositionInternal position"
                        + "                right join position.product product"
                        + "                where request.orgOwner = :eduid and request.state = :state "
                        + "                and request.doneDate between :startDate and :endDate "
                        + "                order by request.doneDate desc";
                query = session.createQuery(sql);
                query.setParameter("eduid", edu.getIdOfOrg());
                query.setParameter("state", DocumentState.FOLLOW);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                List requestsGoods = query.list();
                if(!(requestsGoods==null || requestsGoods.isEmpty())){
                    requests.addAll(requestsGoods);
                }
                sql = "select request.number, request.doneDate, position.totalCount/1000, good.nameOfGood"
                        + "                from GoodRequest request"
                        + "                join request.goodRequestPositionInternal position"
                        + "                right join position.good good"
                        + "                where request.orgOwner = :eduid and request.state = :state "
                        + "                and request.doneDate between :startDate and :endDate "
                        + "                order by request.doneDate desc";
                query = session.createQuery(sql);
                query.setParameter("eduid", edu.getIdOfOrg());
                query.setParameter("state", DocumentState.FOLLOW);
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                List requestsProducts = query.list();
                if(!(requestsProducts==null || requestsProducts.isEmpty())){
                    requests.addAll(requestsProducts);
                }
                if(!(requests==null || requests.isEmpty())){
                    for (Object o: requests){
                        Object[] row = (Object[]) o;
                        HashMap.SimpleEntry<String,Date> request = new HashMap.SimpleEntry<String,Date>(row[0].toString(), (Date) row[1]);
                        HashMap.SimpleEntry<String,Long> position = new HashMap.SimpleEntry<String,Long>(row[3].toString(), Long.parseLong(row[2].toString()));
                        if(requestMap.keySet().contains(request)){
                            List<HashMap.SimpleEntry<String,Long>> p = requestMap.get(request);
                            if(p==null || p.isEmpty()){
                                p = new ArrayList<HashMap.SimpleEntry<String,Long>>();
                            }
                            p.add(position);
                            requestMap.put(request,p);
                        } else {
                            List<HashMap.SimpleEntry<String,Long>> p = new ArrayList<HashMap.SimpleEntry<String,Long>>();
                            p.add(position);
                            requestMap.put(request,p);
                        }
                    }
                    for (HashMap.SimpleEntry<String,Date> requestEntity: requestMap.keySet()){
                        List<Commodity> commodityList = new ArrayList<Commodity>();
                        for (HashMap.SimpleEntry<String,Long> positionEntity: requestMap.get(requestEntity)){
                            Commodity commodity = new Commodity(positionEntity.getKey(), positionEntity.getValue(),
                                    null);
                            commodityList.add(commodity);
                        }
                        AggregateGoodRequestReportItem aggregateGoodRequestReportItem = new AggregateGoodRequestReportItem(
                                requestEntity.getKey(),
                                item.getIdOfOrg(),
                                item.getShortName(),
                                edu.getIdOfOrg(),
                                Org.extractOrgNumberFromName(edu.getOfficialName()),
                                edu.getShortName(),
                                commodityList,
                                requestEntity.getValue()
                        );
                        aggregateGoodRequestReportItems.add(aggregateGoodRequestReportItem);
                    }

                }
            }
        }

        Collections.sort(aggregateGoodRequestReportItems, new ReportComparator());

        return aggregateGoodRequestReportItems;
    }

*/
    public class ReportComparator implements Comparator
    {
        public int compare ( Object object1 , Object object2 )
        {
            AggregateGoodRequestReportItem i1 = (AggregateGoodRequestReportItem) object1;
            AggregateGoodRequestReportItem i2 = (AggregateGoodRequestReportItem) object2;
            if (i1.getDoneDate().before(i2.getDoneDate())) {
                return -1;
            } else if (i1.getDoneDate().after(i2.getDoneDate())) {
                return 1;
            } else {
                return 0;
}
        }
    }
}


