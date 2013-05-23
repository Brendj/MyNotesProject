/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.goodRequest;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.RequestState;
import ru.axetta.ecafe.processor.core.utils.Base64;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.04.13
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class GoodRequestService {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<GoodRequest> findByFilter(Long idOfOrg, List<RequestState> stateList, Date startDate,Date endDate,  Integer deletedState){
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
        if ((stateList != null) && !stateList.isEmpty()) {
            criteria.add(Restrictions.in("state",stateList));
        }
        return criteria.list();
    }

}
