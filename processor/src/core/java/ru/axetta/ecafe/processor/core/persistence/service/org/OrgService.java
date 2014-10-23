package ru.axetta.ecafe.processor.core.persistence.service.org;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * User: shamil
 * Date: 20.08.14
 * Time: 12:34
 */
@Service
public class OrgService {
    OrgRepository dao;

    @Autowired
    public void setDao(OrgRepository dao) {
        this.dao = dao;
        dao.setClazz(Org.class);
    }

    public Org findOrg(Long id){
        return dao.findOne(id);
    }

    public List<BigInteger> findOrgCategories(Long id){
        return dao.findOrgCategories(id);
    }


}
