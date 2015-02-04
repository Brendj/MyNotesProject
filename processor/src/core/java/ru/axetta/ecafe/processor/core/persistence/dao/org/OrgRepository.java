package ru.axetta.ecafe.processor.core.persistence.dao.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.AbstractJpaDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

    public List<BigInteger> findOrgCategories(Long id) {
        return entityManager
                .createNativeQuery("SELECT idofcategoryorg FROM cf_categoryorg_orgs WHERE idoforg = :idoforg ")
                .setParameter("idoforg", id).getResultList();
    }

    public List<OrgItem> findAllNames(){
        List<OrgItem> orgItemList = new ArrayList<OrgItem>();
        Query nativeQuery = entityManager.createNativeQuery("SELECT IdOfOrg, ShortName, District FROM CF_Orgs  WHERE State =1 and OrganizationType=0 ORDER BY OfficialName ");

        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            orgItemList.add(new OrgItem(((BigInteger)o[0]).longValue(),(String)o[1],(String)o[2]));
        }

        return orgItemList;
    }

    public List<OrgItem> findAllNamesByContragentTSP(long idOfContragent){
        List<OrgItem> orgItemList = new ArrayList<OrgItem>();
        Query nativeQuery = entityManager.createNativeQuery("SELECT IdOfOrg, ShortName, District FROM CF_Orgs "
                + " WHERE State =1 and OrganizationType=0 and DefaultSupplier=:idOfContragent and RefectoryType != 3 ORDER BY OfficialName ")
                .setParameter("idOfContragent",idOfContragent);

        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            orgItemList.add(new OrgItem(((BigInteger)o[0]).longValue(),(String)o[1],(String)o[2]));
        }

        return orgItemList;
    }
}
