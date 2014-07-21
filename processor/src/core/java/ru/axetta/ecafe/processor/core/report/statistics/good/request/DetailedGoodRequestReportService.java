package ru.axetta.ecafe.processor.core.report.statistics.good.request;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.report.BasicReportForOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.apache.commons.collections.map.MultiValueMap;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.05.13
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */

public class DetailedGoodRequestReportService {

    public List<DetailedGoodRequestReportItem> buildReport(Session session, List<Long> idOfSourceOrgList,
            List<Long> idOfEduList, Date startDate, Date endDate, DocumentStateFilter documentStateFilter)
            throws Exception{
        List<DetailedGoodRequestReportItem> detailedGoodRequestReportItems = new ArrayList<DetailedGoodRequestReportItem>();
        Criteria orgCriteria = session.createCriteria(Org.class);
        if (!CollectionUtils.isEmpty(idOfEduList)) {
            orgCriteria.add(Restrictions.in("idOfOrg", idOfEduList));
        }
        orgCriteria.createAlias("sourceMenuOrgs", "sm");
        if (!CollectionUtils.isEmpty(idOfSourceOrgList)) {
            orgCriteria.add(Restrictions.in("sm.idOfOrg", idOfSourceOrgList));
        }
        orgCriteria.setProjection(
                Projections.projectionList().add(Projections.property("idOfOrg")).add(Projections.property("shortName"))
                        .add(Projections.property("officialName")).add(Projections.property("address"))
                        .add(Projections.property("sm.idOfOrg")).add(Projections.property("sm.shortName"))
                        .add(Projections.property("sm.officialName")).add(Projections.property("sm.address")));
        List list = orgCriteria.list();


        String sql;
        Query query;

          /* Строим отображения ключ (Поставшик) -> значения (Образовательные учереждения) */
        MultiValueMap multiValueMap = new MultiValueMap();
        for (Object entity : list) {
            Object[] row = (Object[]) entity;
            BasicReportForOrgJob.OrgShortItem educationItem = new BasicReportForOrgJob.OrgShortItem(
                    Long.parseLong(row[0].toString()), row[1].toString(), row[2].toString(), row[3].toString());
            BasicReportForOrgJob.OrgShortItem sourceItem = new BasicReportForOrgJob.OrgShortItem(
                    Long.parseLong(row[4].toString()), row[5].toString(), row[6].toString(), row[7].toString());
            multiValueMap.put(sourceItem, educationItem);
        }
        for (Object sourceObj : multiValueMap.keySet()) {
            BasicReportForOrgJob.OrgShortItem item = (BasicReportJob.OrgShortItem) sourceObj;
            for (Object eduObj : multiValueMap.getCollection(item)) {
                 /* Строим отображения ключ (Требования-заявка) -> значения (Позиции заявок) */
                BasicReportForOrgJob.OrgShortItem edu = (BasicReportJob.OrgShortItem) eduObj;

                MultiValueMap requestMap = new MultiValueMap();

                List<Object> requests = new ArrayList<Object>();
                sql = "select request.number, request.doneDate, position.totalCount/1000, "
                        + " position.dailySampleCount/1000 ,product.productName, position.createdDate, position.lastUpdate "
                        + " from GoodRequest request"
                        + "                join request.goodRequestPositionInternal position"
                        + "                right join position.product product"
                        + "                where request.orgOwner = :eduid and request.state in :state "
                        + "                and position.deletedState<>true and request.deletedState<>true "
                        + "                and request.doneDate between :startDate and :endDate "
                        + "                order by request.doneDate desc";
                query = session.createQuery(sql);
                query.setParameter("eduid", edu.getIdOfOrg());
                query.setParameterList("state", DocumentStateFilter.states(documentStateFilter));
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                List requestsProducts = query.list();
                if (!(requestsProducts == null || requestsProducts.isEmpty())) {
                    requests.addAll(requestsProducts);
                }

                sql = "select request.number, request.doneDate, position.totalCount/1000, "
                        + " position.dailySampleCount/1000, good.nameOfGood, position.createdDate, position.lastUpdate "
                        + " from GoodRequest request"
                        + "                join request.goodRequestPositionInternal position"
                        + "                right join position.good good"
                        + "                where request.orgOwner = :eduid and request.state in :state "
                        + "                and position.deletedState<>true and request.deletedState<>true "
                        + "                and request.doneDate between :startDate and :endDate "
                        + "                order by request.doneDate desc";
                query = session.createQuery(sql);
                query.setParameter("eduid", edu.getIdOfOrg());
                query.setParameterList("state", DocumentStateFilter.states(documentStateFilter));
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
                List requestsGoods = query.list();
                if (!(requestsGoods == null || requestsGoods.isEmpty())) {
                    requests.addAll(requestsGoods);
                }
                if (!(requests == null || requests.isEmpty())) {
                    for (Object o : requests) {
                        Object[] row = (Object[]) o;
                        Date lastCreate = (Date) row[5];
                        Date lastUpdate = (Date) row[6];
                        RequestItem request = new RequestItem(row[0].toString(), (Date) row[1], lastCreate, lastUpdate);
                        Long dcount = null;
                        if (row[3] != null) {
                            dcount = Long.parseLong(row[3].toString());
                        }
                        Commodity commodity = new Commodity(row[4].toString(), Long.parseLong(row[2].toString()),
                                dcount);
                        requestMap.put(request, commodity);

                    }
                    for (Object reqObj : requestMap.keySet()) {
                        RequestItem requestItem = (RequestItem) reqObj;

                        Date lastCreate = ((RequestItem) reqObj).lastCreate;
                        Date lastUpdate = ((RequestItem) reqObj).lastUpdate;

                        Date lastCreateOrUpdate;
                        if (lastUpdate != null) {

                            if (lastUpdate.compareTo(lastCreate) >= 0) {
                                lastCreateOrUpdate = lastUpdate;
                            } else {
                                lastCreateOrUpdate = lastCreate;
                            }
                        } else {
                            if (lastCreate == null) {
                                lastCreateOrUpdate = lastUpdate;
                            } else {
                                lastCreateOrUpdate = lastCreate;
                            }
                        }

                        List<Commodity> commodityList = (List<Commodity>) requestMap.getCollection(requestItem);

                        DetailedGoodRequestReportItem reportItem = new DetailedGoodRequestReportItem(
                                requestItem.number, item.getIdOfOrg(), item.getShortName(), edu.getIdOfOrg(),
                                edu.getShortName(), commodityList, requestItem, lastCreateOrUpdate);
                        detailedGoodRequestReportItems.add(reportItem);
                    }

                }
            }
        }
        Collections.sort(detailedGoodRequestReportItems, new ReportComparator());

        return detailedGoodRequestReportItems;
    }

    private class ReportComparator implements Comparator {

        public int compare(Object object1, Object object2) {
            DetailedGoodRequestReportItem i1 = (DetailedGoodRequestReportItem) object1;
            DetailedGoodRequestReportItem i2 = (DetailedGoodRequestReportItem) object2;
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


