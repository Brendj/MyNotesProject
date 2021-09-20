/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.commodity.accounting;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
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
public class GoodRequestPositionRepository {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    @Transactional
    public  Map<GoodRequest, List<GoodRequestPosition>> findByIsNotNullLastUpdateAndGtVersionAndOrgOwner(Long version, Long orgOwner){
        String sql = "from GoodRequestPosition p where p.globalVersion>:version and p.lastUpdate is not null and p.orgOwner=:orgOwner group by p.goodRequest, p.globalId";
        TypedQuery<GoodRequestPosition> query = entityManager.createQuery(sql, GoodRequestPosition.class);
        query.setParameter("version", version);
        query.setParameter("orgOwner", orgOwner);
        List<GoodRequestPosition> goodRequestPositions = query.getResultList();
        Map<GoodRequest, List<GoodRequestPosition>> map = new HashMap<GoodRequest, List<GoodRequestPosition>>();
        for (GoodRequestPosition gr: goodRequestPositions){
            gr.getCurrentElementValue();
            GoodRequest goodRequest = gr.getGoodRequest();
            if(map.containsKey(goodRequest)){
                map.get(goodRequest).add(gr);
            } else {
                ArrayList<GoodRequestPosition> list = new ArrayList<GoodRequestPosition>();
                list.add(gr);
                map.put(goodRequest, list);
            }
        }
        return map;
    }

    public Map<Long, Long> extractOrgOwnerAndVersion() {
        String sql = "select orgowner, max(globalversion) from cf_goods_requests_positions group by orgowner order by orgowner;";
        Query query = entityManager.createNativeQuery(sql);
        List list = query.getResultList();
        Map<Long, Long> map = new HashMap<Long, Long>(list.size());
        for (Object o:list){
            Object[] vals = (Object[]) o;
            map.put(Long.valueOf(vals[0].toString()), Long.valueOf(vals[1].toString()));
        }
        return map;
    }
}
