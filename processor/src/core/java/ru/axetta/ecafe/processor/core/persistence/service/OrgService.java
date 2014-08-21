package ru.axetta.ecafe.processor.core.persistence.service;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.AbstractJpaDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * User: shamil
 * Date: 20.08.14
 * Time: 12:34
 */
@Service
public class OrgService {
    AbstractJpaDao<Org> dao;

    @Autowired
    @Qualifier(value = "orgRepository")
    public void setDao(AbstractJpaDao<Org> dao) {
        this.dao = dao;
        dao.setClazz(Org.class);
    }
}
