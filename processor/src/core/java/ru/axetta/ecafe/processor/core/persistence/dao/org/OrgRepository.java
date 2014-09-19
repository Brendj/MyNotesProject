package ru.axetta.ecafe.processor.core.persistence.dao.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.AbstractJpaDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * User: shamil
 * Date: 14.08.14
 * Time: 13:07
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Transactional
public class OrgRepository extends AbstractJpaDao<Org> {
    private final static Logger logger = LoggerFactory.getLogger(OrgRepository.class);

    public static OrgRepository getInstance() {
        return RuntimeContext.getAppContext().getBean(OrgRepository.class);
    }
}
