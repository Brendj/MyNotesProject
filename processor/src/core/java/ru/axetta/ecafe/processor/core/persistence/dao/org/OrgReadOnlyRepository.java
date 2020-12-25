/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

/**
 * User: shamil
 * Date: 22.10.14
 * Time: 13:53
 */
@Repository
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class OrgReadOnlyRepository extends BaseJpaDao {

    public static OrgReadOnlyRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(OrgReadOnlyRepository.class);
    }

    public Org find(Long id){
        return entityManager.find( Org.class, id );
    }

    public  List<Long> findFriendlyOrgIds(Long orgId) {
        return entityManager
                .createQuery("select fo.idOfOrg from Org org join org.friendlyOrg fo where org.idOfOrg=:idOfOrg")
                .setParameter("idOfOrg", orgId).getResultList();
    }

    public Boolean isOneActiveCard(long idOfOrg){
        Query r = entityManager.createNativeQuery("select o.OneActiveCard "
                + " from cf_orgs o  "
                + " where o.idoforg=:idoforg").setParameter("idoforg", idOfOrg);
        if ((r.getResultList().size() > 0) && (r.getResultList().get(0) != null)) {
            return ((Integer)r.getResultList().get(0)) == 1;
        }
        return false;
    }

    public FeedingSettingOrgItem getFeedingSettingLimit(long idOfOrg) {
        Query query = entityManager.createNativeQuery("select limitamount, discountamount, usediscount from cf_feeding_settings fs "
                + "join cf_feeding_settings_orgs fso on fs.idofsetting = fso.idofsetting "
                + "where fso.idoforg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        List list = query.getResultList();
        if (list.size() == 0) return null;
        Object[] row = (Object[]) list.get(0);
        return new FeedingSettingOrgItem(HibernateUtils.getDbLong(row[0]), HibernateUtils.getDbLong(row[1]), HibernateUtils.getDbBoolean(row[2]));
    }
}
