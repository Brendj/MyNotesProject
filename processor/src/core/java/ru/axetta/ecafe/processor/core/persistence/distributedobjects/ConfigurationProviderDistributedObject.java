package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.ConfigurationProviderService;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
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
public abstract class ConfigurationProviderDistributedObject extends DistributedObject {

    private Long idOfConfigurationProvider;

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        try {
            idOfConfigurationProvider = ConfigurationProviderService.extractIdOfConfigurationProviderByIdOfOrg(session, idOfOrg);
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
        if(idOfConfigurationProvider==null){
            throw new DistributedObjectException("ConfigurationProvider NOT_FOUND_VALUE");
        }
        beforeProcess(session, idOfOrg);
    }

    protected abstract void beforeProcess(Session session, Long idOfOrg) throws DistributedObjectException;

    @Override
    @SuppressWarnings("unchecked")
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        try {
            idOfConfigurationProvider = ConfigurationProviderService.extractIdOfConfigurationProviderByIdOfOrg(session, idOfOrg);
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
        Criteria criteria = session.createCriteria(getClass());
        //if (currentLimit == null || currentLimit <= 0) {
        //    if (StringUtils.isNotEmpty(currentLastGuid)) {
        //        Disjunction mainRestriction = Restrictions.disjunction();
        //        mainRestriction.add(Restrictions.gt("globalVersion", currentMaxVersion));
        //        Conjunction andRestr = Restrictions.conjunction();
        //        andRestr.add(Restrictions.gt("guid", currentLastGuid));
        //        andRestr.add(Restrictions.ge("globalVersion", currentMaxVersion));
        //        mainRestriction.add(andRestr);
        //        criteria.add(mainRestriction);
        //    } else {
        //        criteria.add(Restrictions.ge("globalVersion", currentMaxVersion));
        //    }
        //} else {
        //    criteria.add(Restrictions.gt("globalVersion", currentMaxVersion));
        //}
        buildVersionCriteria(currentMaxVersion, currentLastGuid, currentLimit, criteria);
        criteria.add(Restrictions.eq("idOfConfigurationProvider", idOfConfigurationProvider));
        createProjections(criteria);
        criteria.setCacheable(false);
        criteria.setReadOnly(true);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
        return criteria.list();
    }

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }

}
