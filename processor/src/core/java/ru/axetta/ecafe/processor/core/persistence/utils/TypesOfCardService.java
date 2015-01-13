package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;

import org.hibernate.Query;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 06.11.14
 * Time: 16:43
 */

public class TypesOfCardService extends AbstractDAOService {

    public List<String> loadDistrictNames() {
        List<String> result;
        Query query = getSession().createSQLQuery(
                "SELECT district FROM cf_orgs WHERE district IN (SELECT district FROM cf_orgs GROUP BY district) GROUP BY district");
        result = query.list();
        return result;
    }

    public List countsByRegionAndOrgs(Date startDate, Integer cardType, Integer cardState) {
        Query query = getSession().createSQLQuery(
                "SELECT count(cfc.idofclient),cl.idoforg, cfo.shortname, cfo.address, cfo.district, cfc.cardtype, cfc.state "
                        + "FROM cf_cards cfc LEFT JOIN cf_clients cl ON cfc.idofclient = cl.idofclient "
                        + "LEFT JOIN cf_orgs cfo ON cl.idoforg = cfo.idoforg "
                        + "WHERE cfo.district LIKE '%САО%' AND cfc.cardtype IN (:cardType) "
                        + "AND cfc.state IN (:cardState) AND cfc.lastupdate >= :startDate "
                        + "GROUP BY  cfo.district, cl.idoforg, cfo.shortname, cfo.address, cfc.cardtype, cfc.state "
                        + "ORDER BY  cfo.district");
        query.setParameter("startDate", startDate);
        query.setParameter("cardType", cardType);
        query.setParameter("cardState", cardState);
        List result = query.list();
        return result;
    }

/*    public List<TypesOfCardSubreportItem> generationStatisticsOfOrgByDistrict(String district, String cardType,
            Integer cardState, Date startDate, Long idofClientGroup) {
        Query query = getSession().createQuery(
                "SELECT count(cfc.idofclient) FROM cf_cards cfc LEFT JOIN cf_clients cl ON cfc.idofclient = cl.idofclient "
                        + "LEFT JOIN cf_orgs cfo ON cl.idoforg = cfo.idoforg WHERE cfo.idoforg = 177 AND cfc.cardtype IN(0) "
                        + "AND cfc.state IN(1) AND cfc.lastupdate >=:startDate");

    }*/


    public Long getStatByOrgId(Long idOfOrg, String cardType, Integer cardState, Date starDate) {
        Query query = getSession().createSQLQuery(
                "SELECT count(cfc.idofclient) FROM cf_cards cfc LEFT JOIN cf_clients cl ON cfc.idofclient = cl.idofclient "
                        + "LEFT JOIN cf_orgs cfo ON cl.idoforg = cfo.idoforg "
                        + "WHERE cfo.idoforg = :idOfOrg AND cfc.cardtype IN(" + cardType
                        + ") AND cfc.state IN( :cardState ) AND cfc.lastupdate >= :startDate");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("startDate", starDate.getTime());
        query.setParameter("cardState", cardState);

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    // Сбор статистики по имени Округа например: САО, ЮВАО.
    public Long getStatByDistrictName(String districtName, String cardType, Integer cardState, Date startDate) {
        Query query = getSession().createSQLQuery(
                "SELECT count(cfc.idofclient) FROM cf_cards cfc left join cf_clients cl on cfc.idofclient = cl.idofclient "
                        + "LEFT JOIN cf_orgs cfo ON cl.idoforg = cfo.idoforg where cfc.cardtype in (" + cardType
                        + ") and cfc.state in (:cardState) and cfo.district like :districtName and cfc.lastupdate >= :startDate");
        query.setString("districtName", districtName);
        query.setInteger("cardState", cardState);
        query.setLong("startDate", startDate.getTime());

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }
}
