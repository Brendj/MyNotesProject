/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: shamil
 * Date: 18.05.15
 * Time: 13:53
 */
@Repository
@Transactional()
public class OrgWritableRepository extends WritableJpaDao {

    public static OrgWritableRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(OrgWritableRepository.class);
    }

    public Org find(long id){
        return entityManager.find(Org.class, id);
    }

    /*public Org findByAdditionalId(long additionalIdBuildingId){
        List<Org> additionalIdBuildingList = entityManager.createQuery("from Org where additionalIdBuilding =:additionalIdBuildingId", Org.class)
                .setParameter("additionalIdBuildingId", additionalIdBuildingId).getResultList();
        if (additionalIdBuildingList.size() > 0) {
            return additionalIdBuildingList.get(0);
        } else {
            return null;
        }
    }*/


    public Org findByBtiUnom(long btiUnom) {
        List<Org> btiUnomList = entityManager.createQuery("from Org where btiUnom =:btiUnom", Org.class)
                .setParameter("btiUnom", btiUnom).getResultList();
        if (btiUnomList.size() > 0) {
            return btiUnomList.get(0);
        } else {
            return null;
        }
    }

    public void saveEntity(Card card) {
        entityManager.merge(card);
    }


}
