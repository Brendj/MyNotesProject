package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.ConfigurationProviderService;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

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
public abstract class ConfigurationProviderDistributedObject extends DistributedObject {

    protected Long idOfConfigurationProvider;
    private ConfigurationProvider configurationProvider;

    @Override
    @SuppressWarnings("unchecked")
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion, int currentLimit, String currentLastGuid) throws Exception {
        try {
            idOfConfigurationProvider = ConfigurationProviderService.extractIdOfConfigurationProviderByIdOfOrg(session, idOfOrg);
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
        Criteria criteria = session.createCriteria(getClass());
        criteria.add(Restrictions.eq("idOfConfigurationProvider", idOfConfigurationProvider));
        criteria.add(Restrictions.gt("globalVersion", currentMaxVersion));
        createProjections(criteria, currentLimit, currentLastGuid);
        criteria.setCacheable(false);
        criteria.setReadOnly(true);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
        return criteria.list();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }

    private ConfigurationProvider getConfigurationProvider() {
        return configurationProvider;
    }

    private void setConfigurationProvider(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }
}
