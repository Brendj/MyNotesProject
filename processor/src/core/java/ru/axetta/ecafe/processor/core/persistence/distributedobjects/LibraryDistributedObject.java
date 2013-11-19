package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.09.13
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
public abstract class LibraryDistributedObject extends DistributedObject {

    @Override
    @SuppressWarnings("unchecked")
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion, int currentLimit, String currentLastGuid) throws Exception {
        Criteria criteria = session.createCriteria(getClass());
        criteria.add(Restrictions.ge("globalVersion", currentMaxVersion));
        createProjections(criteria, currentLimit, currentLastGuid);
        criteria.setCacheable(false);
        criteria.setReadOnly(true);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
        return criteria.list();

    }
}
