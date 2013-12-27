package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.ConfigurationProviderService;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.util.Arrays;
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
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion) throws Exception {
        try {
            idOfConfigurationProvider = ConfigurationProviderService.extractIdOfConfigurationProviderByIdOfOrg(session, idOfOrg);
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
        Criteria criteria = session.createCriteria(getClass());
        //Criteria criteria = session.createCriteria(Good.class);
        criteria.add(Restrictions.eq("idOfConfigurationProvider", idOfConfigurationProvider));
        criteria.add(Restrictions.gt("globalVersion", currentMaxVersion));

        //Boolean isSupplier = DAOUtils.isSupplierByOrg(session, idOfOrg);
        //if(isSupplier){
        //    criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        //} else {
        //    Long supplierId = DAOUtils.findMenuExchangeSourceOrg(session, idOfOrg);
        //    criteria.add(Restrictions.in("orgOwner", Arrays.asList(idOfOrg, supplierId)));
        //}
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
