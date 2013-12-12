package ru.axetta.ecafe.processor.core.daoservices.commodity.accounting;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOCurrentOrgVersion;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.12.13
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class DistributedObjectCurrentVersionRepository {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public Map<Long, DOCurrentOrgVersion> findAllByGoodRequest(){
        String sql = "from DOCurrentOrgVersion where objectId=:objectId";
        TypedQuery<DOCurrentOrgVersion> query = entityManager.createQuery(sql, DOCurrentOrgVersion.class);
        query.setParameter("objectId", DOCurrentOrgVersion.GOOD_REQUEST);
        List<DOCurrentOrgVersion> list = query.getResultList();
        Map<Long, DOCurrentOrgVersion> map = new HashMap<Long, DOCurrentOrgVersion>(list.size());
        for (DOCurrentOrgVersion version: list){
           map.put(version.getIdOfOrg(), version);
        }
        return map;
    }

    public Map<Long, DOCurrentOrgVersion> findAllByGoodRequestPosition(){
        String sql = "from DOCurrentOrgVersion where objectId=:objectId";
        TypedQuery<DOCurrentOrgVersion> query = entityManager.createQuery(sql, DOCurrentOrgVersion.class);
        query.setParameter("objectId", DOCurrentOrgVersion.GOOD_REQUEST_POSITION);
        List<DOCurrentOrgVersion> list = query.getResultList();
        Map<Long, DOCurrentOrgVersion> map = new HashMap<Long, DOCurrentOrgVersion>(list.size());
        for (DOCurrentOrgVersion version: list){
            map.put(version.getIdOfOrg(), version);
        }
        return map;
    }


    @Transactional
    public void persist(DOCurrentOrgVersion doCurrentOrgVersion) {
        entityManager.persist(doCurrentOrgVersion);
    }

    @Transactional
    public HashMap.SimpleEntry<String, String> extractShortNameAndEmailFromOrg(Long orgOwner) {
        TypedQuery<Org> query = entityManager.createQuery("from Org where idOfOrg=:idOfOrg", Org.class);
        query.setParameter("idOfOrg", orgOwner);
        Org org = query.getSingleResult();
        return new AbstractMap.SimpleEntry<String, String>(org.getShortName(),org.getDefaultSupplier().getRequestNotifyMailList());
    }
}
