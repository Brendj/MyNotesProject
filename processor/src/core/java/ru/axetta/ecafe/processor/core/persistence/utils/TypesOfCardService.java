package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.report.TypesOfCardOrgItem;

import org.hibernate.Query;

import java.math.BigInteger;
import java.util.ArrayList;
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

    public Long getStatByOrgId(Long idOfOrg, String cardType, Integer cardState, Date starDate, String groupRestrict) {
        Query query = getSession().createSQLQuery(
                "SELECT count(cfc.idofclient) FROM cf_cards cfc LEFT JOIN cf_clients cl ON cfc.idofclient = cl.idofclient "
                        + "LEFT JOIN cf_orgs cfo ON cl.idoforg = cfo.idoforg "
                        + "LEFT OUTER JOIN cf_clientgroups cfcl on cfo.idoforg = cfcl.idoforg and cl.IdOfClientGroup = cfcl.IdOfClientGroup "
                        + "WHERE cfo.idoforg = :idOfOrg AND cfc.cardtype IN(" + cardType
                        + ") AND cfc.state IN(:cardState) AND cfc.createddate >= :startDate " + groupRestrict);
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("startDate", starDate.getTime());
        query.setParameter("cardState", cardState);

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    // Сбор статистики по имени Округа например: САО, ЮВАО.
    public Long getStatByDistrictName(String districtName, String cardType, Integer cardState, Date startDate,
            String groupRestrict) {
        Query query = getSession().createSQLQuery(
                "SELECT count(cfc.idofclient) FROM cf_cards cfc left join cf_clients cl on cfc.idofclient = cl.idofclient "
                        + "LEFT JOIN cf_orgs cfo ON cl.idoforg = cfo.idoforg "
                        + "LEFT OUTER JOIN cf_clientgroups cfcl on cfo.idoforg = cfcl.idoforg and cl.IdOfClientGroup = cfcl.IdOfClientGroup "
                        + "where cfc.cardtype in (" + cardType
                        + ") and cfc.state in (:cardState) and cfo.district like :districtName and cfc.createddate >= :startDate "
                        + groupRestrict);
        query.setString("districtName", districtName);
        query.setInteger("cardState", cardState);
        query.setLong("startDate", startDate.getTime());

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    public List<TypesOfCardOrgItem> getAllOrgsByDistrictName(String districtName) {
        List<TypesOfCardOrgItem> resultList = new ArrayList<TypesOfCardOrgItem>();

        Query query = getSession().createSQLQuery(
                "SELECT idoforg, shortname, address FROM cf_orgs WHERE district LIKE :districtName ORDER BY substring(shortname FROM '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)')");
        query.setString("districtName", districtName);

        List result = query.list();

        for (Object res : result) {
            Object[] resItem = (Object[]) res;

            Long idOfOrg = ((BigInteger) resItem[0]).longValue();
            String shortName = (String) resItem[1];
            String address = (String) resItem[2];

            TypesOfCardOrgItem typesOfCardOrgItem = new TypesOfCardOrgItem(idOfOrg, shortName, address);

            resultList.add(typesOfCardOrgItem);
        }

        return resultList;
    }
}
