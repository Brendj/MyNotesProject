package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.hibernate.Criteria;
import org.hibernate.Session;
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

    public DistributedObject mergedDistributedObject;

    @Override
    @SuppressWarnings("unchecked")
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        Criteria criteria = session.createCriteria(getClass());
        //criteria.add(Restrictions.gt("globalVersion", currentMaxVersion));
        buildVersionCriteria(currentMaxVersion, currentLastGuid, currentLimit, criteria);
        createProjections(criteria);
        criteria.setCacheable(false);
        criteria.setReadOnly(true);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
        return criteria.list();

    }

}
