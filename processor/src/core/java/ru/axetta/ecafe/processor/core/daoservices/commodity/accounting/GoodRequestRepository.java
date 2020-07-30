/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.commodity.accounting;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.04.13
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
@Service
public class GoodRequestRepository {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public static GoodRequestRepository getInstance() {
        return RuntimeContext.getAppContext().getBean(GoodRequestRepository.class);
    }

    public void extractEmail(){

    }

    public boolean isGoodRequestForROSection(GoodRequest goodRequest) throws Exception {
        String sql = "select grp from GoodRequestPosition grp where grp.goodRequest = :goodRequest and grp.good is null";
        TypedQuery<GoodRequestPosition> query = entityManager.createQuery(sql, GoodRequestPosition.class);
        query.setParameter("goodRequest", goodRequest);
        List<GoodRequestPosition> res = query.getResultList();
        if (res != null && res.size() > 0) {
            return false;
        }
        return true;
    }

    public Map<Long, Long> extractOrgOwnerAndVersion(){
        String sql = "select orgowner, max(globalversion) from cf_goods_requests group by orgowner order by orgowner;";
        Query query = entityManager.createNativeQuery(sql);
        List list = query.getResultList();
        Map<Long, Long> map = new HashMap<Long, Long>(list.size());
        for (Object o:list){
            Object[] vals = (Object[]) o;
            map.put(Long.valueOf(vals[0].toString()), Long.valueOf(vals[1].toString()));
        }
        return map;
    }

    @Transactional
    public List<GoodRequest> findByIsNotNullLastUpdateAndGtVersionAndOrgOwner(Long version, Long orgOwner){
        String sql = "from GoodRequest where globalVersion>:version and lastUpdate is not null and orgOwner=:orgOwner";
        TypedQuery<GoodRequest> query = entityManager.createQuery(sql, GoodRequest.class);
        query.setParameter("version", version);
        query.setParameter("orgOwner", orgOwner);
        List<GoodRequest> goodRequests = query.getResultList();
        for (GoodRequest gr:goodRequests){
            gr.getGoodRequestPosition();
        }
        return goodRequests;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<GoodRequest> findByFilter(Long idOfOrg, List<DocumentState> stateList, Date startDate,Date endDate,  Integer deletedState){
        Long idofgoodsgroup = null;
        return findByFilter(idOfOrg, stateList, startDate, endDate, deletedState, idofgoodsgroup);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<GoodRequest> findByFilter(Long idOfOrg, List<DocumentState> stateList, Date startDate,Date endDate,  Integer deletedState, Boolean showZero){
        return findByFilter(idOfOrg, stateList, startDate, endDate, deletedState, null, showZero);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<GoodRequest> findByFilter(Long idOfOrg, List<DocumentState> stateList, Date startDate,Date endDate,  Integer deletedState, Long idofgoodsgroup){
        return findByFilter(idOfOrg, stateList, startDate, endDate, deletedState, idofgoodsgroup, Boolean.FALSE);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<GoodRequest> findByFilter(Long idOfOrg, List<DocumentState> stateList, Date startDate,Date endDate,  Integer deletedState, Long idofgoodsgroup, Boolean showZero){
        Session session =  (Session) entityManager.getDelegate();
        Criteria criteria = session.createCriteria(GoodRequest.class);
        criteria.add(Restrictions.between("doneDate", startDate, endDate));
        if (deletedState != 2) {
            boolean deletedFlag = false;
            if (deletedState == 1) {
                deletedFlag = true;
            }
            criteria.add(Restrictions.eq("deletedState",deletedFlag));
        }
        if (idOfOrg != null) {
            criteria.add(Restrictions.eq("orgOwner",idOfOrg));
        }
        boolean restrictByZero = showZero != null && !showZero;
        boolean restrictByGroup = idofgoodsgroup != null && idofgoodsgroup != Long.MIN_VALUE;
        if (restrictByZero || restrictByGroup) {
            Criteria goodPos = criteria.createCriteria("goodRequestPositionInternal");
            if (restrictByZero) {
                goodPos.add(Restrictions.ne("totalCount", 0L));
            }
            if (restrictByGroup) {
                goodPos.createCriteria("good").createCriteria("goodGroup").add(Restrictions.eq("globalId", idofgoodsgroup));
            }
        }
        if ((stateList != null) && !stateList.isEmpty()) {
            criteria.add(Restrictions.in("state",stateList));
        }
        criteria.addOrder(Order.desc("doneDate"));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<GoodRequest> findByFilterOnlyGoodRequest(Long idOfOrg, List<DocumentState> stateList, Date startDate,Date endDate,  Integer deletedState) {
        Session session =  entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(GoodRequest.class);
        criteria.add(Restrictions.between("doneDate", startDate, endDate));
        if (deletedState != 2) {
            boolean deletedFlag = false;
            if (deletedState == 1) {
                deletedFlag = true;
            }
            criteria.add(Restrictions.eq("deletedState",deletedFlag));
        }
        if (idOfOrg != null) {
            criteria.add(Restrictions.eq("orgOwner",idOfOrg));
        }

        if ((stateList != null) && !stateList.isEmpty()) {
            criteria.add(Restrictions.in("state",stateList));
        }
        criteria.addOrder(Order.desc("doneDate"));
        return criteria.list();
    }

    @Transactional(readOnly = true)
    public List<GoodRequest> findGoodRequestAll(List<Long> idOfOrg){
        TypedQuery<GoodRequest> query = entityManager.createQuery("from GoodRequest where deletedState=false and orgOwner in :orgOwner", GoodRequest.class);
        query.setParameter("orgOwner", idOfOrg);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public List<GoodRequest> findGoodRequestPositionAll(List<Long> idOfOrg){
        TypedQuery<GoodRequest> query = entityManager.createQuery("from GoodRequestPosition where deletedState=false and orgOwner in :orgOwner", GoodRequest.class);
        query.setParameter("orgOwner", idOfOrg);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public GoodRequest findGoodRequestById(Long id){
        TypedQuery<GoodRequest> query = entityManager.createQuery("from GoodRequest where id=:id", GoodRequest.class);
        query.setParameter("id",id);
        return query.getSingleResult();
    }

    @Transactional(readOnly = true)
    public GoodRequestPosition findGoodRequestPositionById(Long id){
        TypedQuery<GoodRequestPosition> query = entityManager.createQuery("from GoodRequestPosition where id=:id", GoodRequestPosition.class);
        query.setParameter("id",id);
        return query.getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<GoodRequestPosition> getGoodRequestPositionByGoodRequest(GoodRequest goodRequest){
        TypedQuery<GoodRequestPosition> query = entityManager.createQuery("from GoodRequestPosition gr where gr.goodRequest=:goodRequest", GoodRequestPosition.class);
        query.setParameter("goodRequest",goodRequest);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public GoodRequest findByGUID(String guid){
        TypedQuery<GoodRequest> query = entityManager.createQuery("from GoodRequest where guid=:guid", GoodRequest.class);
        query.setParameter("guid",guid);
        return query.getSingleResult();
    }

    @Transactional
    public GoodRequest save(GoodRequest goodRequest){
        TypedQuery<DOVersion> query = entityManager
                .createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",
                        DOVersion.class);
        query.setParameter("distributedObjectClassName", "GoodRequest".toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        Long version = null;
        if (doVersionList.size() == 0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            version = 0L;
        } else {
            doVersion = entityManager.find(DOVersion.class, doVersionList.get(0).getIdOfDOObject());
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
        }
        doVersion.setDistributedObjectClassName("GoodRequest");
        if(goodRequest.getGlobalId()==null){
            goodRequest.setGlobalVersion(version);
            entityManager.persist(doVersion);
            entityManager.persist(goodRequest);
        } else {
            goodRequest.setGlobalVersion(version);
            entityManager.persist(doVersion);
            goodRequest=entityManager.merge(goodRequest);
        }
        return goodRequest;
    }

    @Transactional
    public GoodRequestPosition save(GoodRequestPosition goodRequestPosition){
        TypedQuery<DOVersion> query = entityManager
                .createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",
                        DOVersion.class);
        query.setParameter("distributedObjectClassName", "GoodRequestPosition".toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        Long version = null;
        if (doVersionList.size() == 0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            version = 0L;
        } else {
            doVersion = entityManager.find(DOVersion.class, doVersionList.get(0).getIdOfDOObject());
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
        }
        doVersion.setDistributedObjectClassName("GoodRequestPosition");
        if(goodRequestPosition.getGlobalId()==null){
            goodRequestPosition.setGlobalVersion(version);
            entityManager.persist(doVersion);
            entityManager.persist(goodRequestPosition);
        } else {
            goodRequestPosition.setGlobalVersion(version);
            entityManager.persist(doVersion);
            goodRequestPosition=entityManager.merge(goodRequestPosition);
        }
        return goodRequestPosition;
    }

    @Transactional
    public void delete(GoodRequest goodRequest){
        goodRequest = entityManager.merge(goodRequest);
        TypedQuery<DOVersion> query = entityManager
                .createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",
                        DOVersion.class);
        query.setParameter("distributedObjectClassName", "GoodRequest".toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        Long version = null;
        if (doVersionList.size() == 0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            version = 0L;
        } else {
            doVersion = entityManager.find(DOVersion.class, doVersionList.get(0).getIdOfDOObject());
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
        }
        doVersion.setDistributedObjectClassName("GoodRequest");
        goodRequest.setDeletedState(true);
        goodRequest.setGlobalVersion(version);
        entityManager.persist(doVersion);
        goodRequest = entityManager.merge(goodRequest);
    }

    @Transactional
    public void delete(GoodRequestPosition goodRequestPosition){
        goodRequestPosition = entityManager.merge(goodRequestPosition);
        TypedQuery<DOVersion> query = entityManager
                .createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",
                        DOVersion.class);
        query.setParameter("distributedObjectClassName", "GoodRequestPosition".toUpperCase());
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion = null;
        Long version = null;
        if (doVersionList.size() == 0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            version = 0L;
        } else {
            doVersion = entityManager.find(DOVersion.class, doVersionList.get(0).getIdOfDOObject());
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
        }
        doVersion.setDistributedObjectClassName("GoodRequestPosition");
        goodRequestPosition.setDeletedState(true);
        goodRequestPosition.setGlobalVersion(version);
        entityManager.persist(doVersion);
        goodRequestPosition = entityManager.merge(goodRequestPosition);
    }


    @Transactional
    public GoodRequest createGoodRequestWithPosition(long idoforg, long idofgood, long time, long totalCount, String comment) {
        //  Формируем номер по маске {idOfOrg}-{yyMMdd}-ЗВК-{countToDay}. countToDay всегда первый
        Date now = new Date(System.currentTimeMillis());
        String number = "";
        number = "" + idoforg;
        number = number + "-" + new SimpleDateFormat("yyMMdd").format(now);
        number = number + "-ЗВК-1";

        Good good = DAOService.getInstance().getGood(idofgood);

        //  Создание GoodRequest
        GoodRequest goodRequest = new GoodRequest();
        goodRequest.setOrgOwner(idoforg);
        goodRequest.setDateOfGoodsRequest(new Date(time));
        goodRequest.setDoneDate(new Date(time));
        goodRequest.setNumber(number);
        goodRequest.setState(DocumentState.FOLLOW);
        goodRequest.setDeletedState(false);
        goodRequest.setCreatedDate(now);
        goodRequest.setComment(comment);
        goodRequest = save(goodRequest);

        //  Создание GoodRequestPosition
        GoodRequestPosition pos = new GoodRequestPosition();
        pos.setGoodRequest(goodRequest);
        pos.setGood(good);
        pos.setDeletedState(false);
        pos.setOrgOwner(idoforg);
        pos.setUnitsScale(good.getUnitsScale());
        pos.setNetWeight(good.getNetWeight());
        pos.setCreatedDate(now);
        pos.setTotalCount(totalCount);
        save(pos);
        return goodRequest;
    }
}
