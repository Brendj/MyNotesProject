package ru.axetta.ecafe.processor.core.persistence.dao.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;
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

    public List<OrgItem> findAllActive(){
        List<OrgItem> orgItemList = new ArrayList<OrgItem>();
        Query nativeQuery = entityManager.createNativeQuery("SELECT IdOfOrg, ShortName, District, Address  FROM CF_Orgs "
                + " WHERE State =1 and OrganizationType=0 and RefectoryType != 3 ORDER BY OfficialName ");

        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            orgItemList.add(new OrgItem(((BigInteger)o[0]).longValue(),(String)o[1],(String)o[2],(String)o[3]));
        }

        return orgItemList;
    }

    public List<OrgItem> findAllActiveBySupplier(List<Long> ids){
        List<OrgItem> allOrgs = findAllActive();
        List<OrgItem> supplierOrgs = new ArrayList<OrgItem>();
        for (OrgItem item : allOrgs) {
            if (ids.contains(item.getIdOfOrg())) {
                supplierOrgs.add(item);
            }
        }
        return supplierOrgs;
    }

    public List<Long> findAllActiveIds(){
        List<Long> orgItemList = new ArrayList<Long>();
        Query nativeQuery = entityManager.createNativeQuery("SELECT IdOfOrg FROM CF_Orgs "
                + " WHERE State =1 and OrganizationType=0 and RefectoryType != 3 ORDER BY OfficialName ");

        List<BigInteger> temp = nativeQuery.getResultList();
        for(BigInteger o : temp){
            orgItemList.add(o.longValue());
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

    public Org findOrgWithFriendlyOrgs(long idOfOrg) {
        return entityManager.createQuery("select o from Org o left join fetch o.friendlyOrg where o.idOfOrg=:idOfOrg", Org.class)
                .setParameter("idOfOrg", idOfOrg)
                .getResultList().get(0);

    }

    /*
    * Если paybycashier = true то передаем № платежного контрагента
    * */
    @Transactional(readOnly = true)
     public Long isPaymentByCashierEnabled(long idOfOrg){
        Query r = entityManager.createNativeQuery("select c.DefaultContragent "
                + " from cf_orgs o inner join cf_contragents c on o.defaultsupplier=c.idofcontragent and c.PayByCashier=1 "
                + " where o.idoforg=:idoforg and o.paybycashier=1").setParameter("idoforg", idOfOrg);
        if ((r.getResultList().size() > 0) && (r.getResultList().get(0) != null)) {
            return ((BigInteger) r.getResultList().get(0)).longValue();
        }
        return null;
    }
}
