package ru.axetta.ecafe.processor.core.persistence.service.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
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

    public static OrgService getInstance(){
        return RuntimeContext.getAppContext().getBean(OrgService.class);
    }

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

    public Org findOrgWithFriendlyOrgs(Long id){
        return dao.findOrgWithFriendlyOrgs(id);
    }

    public Org getMainBulding(long idOfOrg){
        Org org = findOrgWithFriendlyOrgs(idOfOrg);
        for (Org org1 : org.getFriendlyOrg()) {
            if(org1.isMainBuilding()){
                return org1;
            }
        }
        return org;
    }

    public Org getMainBulding(Org org){
        return getMainBulding(org.getIdOfOrg());
    }

}
