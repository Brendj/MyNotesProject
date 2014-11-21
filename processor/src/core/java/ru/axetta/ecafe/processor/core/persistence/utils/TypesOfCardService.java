package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;

import org.hibernate.Query;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 06.11.14
 * Time: 16:43
 */

public class TypesOfCardService extends AbstractDAOService {

    public List<String> districtNames() {
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
                + "where cfo.district like '%САО%' and cfc.cardtype in (:cardType) "
                + "and cfc.state in (:cardState) and cfc.lastupdate >= :startDate "
                + "GROUP BY  cfo.district, cl.idoforg, cfo.shortname, cfo.address, cfc.cardtype, cfc.state "
                + "ORDER BY  cfo.district");
        query.setParameter("startDate", startDate);
        query.setParameter("cardType", cardType);
        query.setParameter("cardState", cardState);
        List result = query.list();
        return result;
    }



}
