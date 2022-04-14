package ru.axetta.ecafe.processor.core.persistence.dao.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.LastProcessSectionsDates;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.AbstractJpaDao;
import ru.axetta.ecafe.processor.core.sync.SectionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
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
    private static final Long dateDiffForTransactionsSearch = 1000L * 60L * 5L; //5 минут

    @PersistenceContext(unitName = "externalServicesPU")
    private EntityManager emExternalServices;

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
        Query nativeQuery = entityManager.createNativeQuery(
                "SELECT IdOfOrg, ShortName, District FROM CF_Orgs  WHERE State =1 ORDER BY OfficialName ");

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

    public List<OrgItem> findAllActiveBySupplier(List<Long> orgsList, long userId) {
        String orgsCondition = (orgsList == null || orgsList.size() == 0) ? "" : " and o.idOfOrg in :orgs ";
        List<OrgItem> orgItemList = new ArrayList<OrgItem>();
        Query nativeQuery = entityManager.createNativeQuery("select o.IdOfOrg, o.ShortName, o.District, o.Address from cf_orgs o "
                + "where o.defaultsupplier in ("
                + "select uc.idofcontragent "
                + "from cf_users u join cf_usercontragents uc on u.idofuser = uc.idofuser "
                + "where u.idofuser = :user) "
                + orgsCondition
                + "AND State = 1 and OrganizationType = 0 and RefectoryType != 3 ORDER BY OfficialName");
                nativeQuery.setParameter("user", userId);
        if (!(orgsList == null || orgsList.size() == 0)) {
            nativeQuery.setParameter("orgs", orgsList);
        }

        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            orgItemList.add(new OrgItem(((BigInteger)o[0]).longValue(),(String)o[1],(String)o[2],(String)o[3]));
        }

        return orgItemList;
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
                + " WHERE State =1 and DefaultSupplier=:idOfContragent and RefectoryType != 3 ORDER BY OfficialName ")
                .setParameter("idOfContragent",idOfContragent);

        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            orgItemList.add(new OrgItem(((BigInteger)o[0]).longValue(),(String)o[1],(String)o[2]));
        }

        return orgItemList;
    }

    public List<OrgItem> findAllNamesByContragentTSPByListOfOrgIds(long idOfContragent, List<Long> idOfOrgList){
        List<OrgItem> orgItemList = new ArrayList<OrgItem>();
        Query nativeQuery = entityManager.createNativeQuery("SELECT IdOfOrg, ShortName, District FROM CF_Orgs "
                + " WHERE State =1 and DefaultSupplier=:idOfContragent and RefectoryType != 3 and IdOfOrg in (:idOfOrgList) ORDER BY OfficialName ")
                .setParameter("idOfContragent",idOfContragent)
                .setParameter("idOfOrgList", idOfOrgList);

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

    @Transactional(readOnly = true)
    public Date getLastProcessSectionsDate(Long idOfOrg, SectionType sectionType)  {
        List<SectionType> sectionTypes = new ArrayList<SectionType>();
        sectionTypes.add(sectionType);
        return getLastProcessSectionsDate(idOfOrg, sectionTypes);
    }

    @Transactional(readOnly = true)
    public Date getLastProcessSectionsDate(Long idOfOrg, List<SectionType> sectionTypes) {
        List<Integer> types = new ArrayList<Integer>();
        for(SectionType sectionType : sectionTypes){
            types.add(sectionType.getType());
        }
        Query query = emExternalServices.createQuery(
                "select l from LastProcessSectionsDates l where l.compositeIdOfLastProcessSectionsDates.idOfOrg = :idOfOrg "
                        + "and l.compositeIdOfLastProcessSectionsDates.type in :types order by date desc ",
                LastProcessSectionsDates.class);
        query.setMaxResults(1);
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("types", types);
        LastProcessSectionsDates result = null;
        try{
            result = (LastProcessSectionsDates) query.getSingleResult();
        } catch (Exception ignore){}
        return result != null ? result.getDate() : null;
    }

    @Transactional(readOnly = true)
    public boolean transactionsExistByLastProcessSectionDates(List<Long> idOfOrgs, Date startDate, Date endDate)  {
        if (endDate.getTime() - startDate.getTime() > dateDiffForTransactionsSearch) {
            return true;
        }
        Integer timeDelay = Integer.valueOf(RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.transactions.timedelay", "0"));
        startDate.setTime(startDate.getTime()-(timeDelay*60*1000));
        Query query = entityManager.createQuery(
                "select count(*) from LastProcessSectionsDates l where l.compositeIdOfLastProcessSectionsDates.idOfOrg in :idOfOrgs "
                        + "and l.compositeIdOfLastProcessSectionsDates.type = :sectionType and l.date between :startDate and :endDate");
        query.setParameter("idOfOrgs", idOfOrgs);
        query.setParameter("sectionType", SectionType.LAST_TRANSACTION.getType());
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        Long result = null;
        try {
            result = (Long) query.getSingleResult();
        } catch (Exception ignore){}
        return result == null ? true : result.intValue() > 0;
    }
}
