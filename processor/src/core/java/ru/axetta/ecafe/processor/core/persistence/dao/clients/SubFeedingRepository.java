/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;

import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * User: shamil
 * Date: 22.10.14
 * Time: 13:53
 */
@Repository
public class SubFeedingRepository  extends BaseJpaDao {

    private List<ClientItem> getClientsBySQL(long idOfOrg, String query){
        List<Object[]> temp = entityManager.createNativeQuery(query).setParameter("idOfOrg", idOfOrg).getResultList();
        return parse(temp);
    }


    private ClientItem getClientBySQL(long idOfOrg, String query){
        List<Object[]> temp = entityManager.createNativeQuery(query).setParameter("idOfOrg", idOfOrg).getResultList();
        return parse(temp).get(0);
    }

    private ClientItem getClientBySQL(String query){
        List<Object[]> temp = entityManager.createNativeQuery(query).getResultList();
        return parse(temp).get(0);
    }

    private List<ClientItem> parse(List<Object[]> temList) {
        List<ClientItem> resultList = new ArrayList<ClientItem>();
        for (Object[] result : temList) {
            resultList.add(new ClientItem( ((BigInteger)result[0]).longValue(),((BigInteger)result[1]).longValue(),
                    (String)result[2],(String)result[3],
                    (String)result[4],(Integer)result[5]));
        }
        return resultList;
    }

    /*
    * Находит всех клиентов у которых есть categoriesdiscounts !=50 &discountmode = 3
    * */
    public List<ClientItem> getClientInPlan(long idOfOrg){
        String sql = "SELECT c.idofClient, c.idoforg, o.shortname, (p.surname || ' ' || p.firstname || ' ' || p.secondname) as fullname,"
                + " g.groupname ," + ClientItem.IN_PLAN_TYPE +" as plantype"
                + " FROM cf_clients c "
                + " INNER JOIN cf_persons p ON p.idofperson = c.idofperson "
                + " INNER JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND g.idoforg = c.idoforg "
                + " INNER JOIN cf_orgs o ON o.idoforg = c.idoforg "
                + " WHERE c.idoforg = :idOfOrg"
                + " AND c.discountmode = 3 "
                + " AND (c.categoriesdiscounts LIKE '1' OR "
                    + " c.categoriesdiscounts LIKE '1,%' OR   "
                    + " c.categoriesdiscounts LIKE '%,1' OR   "
                    + " c.categoriesdiscounts LIKE '%,1,%' OR  "

                    + " c.categoriesdiscounts LIKE '2' OR "
                    + " c.categoriesdiscounts LIKE '2,%' OR "
                    + " c.categoriesdiscounts LIKE '%,2' OR "
                    + " c.categoriesdiscounts LIKE '%,2,%' OR "

                    + " c.categoriesdiscounts LIKE '3' OR "
                    + " c.categoriesdiscounts LIKE '3,%' OR "
                    + " c.categoriesdiscounts LIKE '%,3' OR "
                    + " c.categoriesdiscounts LIKE '%,3,%' OR "

                    + " c.categoriesdiscounts LIKE '4' OR "
                    + " c.categoriesdiscounts LIKE '4,%' OR "
                    + " c.categoriesdiscounts LIKE '%,4' OR "
                    + " c.categoriesdiscounts LIKE '%,4,%' OR "

                    + " c.categoriesdiscounts LIKE '5' OR "
                    + " c.categoriesdiscounts LIKE '5,%' OR "
                    + " c.categoriesdiscounts LIKE '%,5' OR "
                    + " c.categoriesdiscounts LIKE '%,5,%' OR "

                    + " c.categoriesdiscounts LIKE '101' OR "
                    + " c.categoriesdiscounts LIKE '101,%' OR "
                    + " c.categoriesdiscounts LIKE '%,101' OR "
                    + " c.categoriesdiscounts LIKE '%,101,%' OR "

                    + " c.categoriesdiscounts LIKE '103' OR "
                    + " c.categoriesdiscounts LIKE '103,%' OR "
                    + " c.categoriesdiscounts LIKE '%,103' OR "
                    + " c.categoriesdiscounts LIKE '%,103,%' OR "

                    + " c.categoriesdiscounts LIKE '104' OR "
                    + " c.categoriesdiscounts LIKE '104,%' OR "
                    + " c.categoriesdiscounts LIKE '%,104' OR "
                    + " c.categoriesdiscounts LIKE '%,104,%' OR "

                    + " c.categoriesdiscounts LIKE '105' OR "
                    + " c.categoriesdiscounts LIKE '105,%' OR "
                    + " c.categoriesdiscounts LIKE '%,105' OR "
                    + " c.categoriesdiscounts LIKE '%,105,%' OR "

                    + " c.categoriesdiscounts LIKE '106' OR "
                    + " c.categoriesdiscounts LIKE '106,%' OR "
                    + " c.categoriesdiscounts LIKE '%,106' OR "
                    + " c.categoriesdiscounts LIKE '%,106,%'  "

                + " ) "
                + " AND c.idofclientgroup < 1100000000 ";
        return getClientsBySQL(idOfOrg, sql);
    }

    public List<ClientItem> getClientInReserve(long idOfOrg){
        String sql = "SELECT c.idofClient, c.idoforg, o.shortname, (p.surname || ' ' || p.firstname || ' ' || p.secondname) as fullname,"
                + " g.groupname ," + ClientItem.IN_RESERVE_TYPE +" as plantype"
                + " FROM cf_clients c "
                + " INNER JOIN cf_persons p ON p.idofperson = c.idofperson "
                + " INNER JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND g.idoforg = c.idoforg "
                + " INNER JOIN cf_orgs o ON o.idoforg = c.idoforg "
                + " WHERE c.idoforg = :idOfOrg"
                + " AND c.discountmode = 3 AND c.categoriesdiscounts like '%50%' "
                + " AND c.idofclientgroup < 1100000000 ";
        return getClientsBySQL(idOfOrg, sql);
    }

    public List<ClientItem> getClientAllClientsInOrg(long idOfOrg){
        String sql = "SELECT c.idofClient, c.idoforg, o.shortname, (p.surname || ' ' || p.firstname || ' ' || p.secondname) as fullname,"
                + " g.groupname ," + ClientItem.IN_PLAN_TYPE +" as plantype"
                + " FROM cf_clients c "
                + " INNER JOIN cf_persons p ON p.idofperson = c.idofperson "
                + " INNER JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND g.idoforg = c.idoforg "
                + " INNER JOIN cf_orgs o ON o.idoforg = c.idoforg "
                + " WHERE c.idoforg = :idOfOrg "
                + " AND c.idofclientgroup < 1100000000 ";
        return getClientsBySQL(idOfOrg,sql);
    }
    public List<ClientItem> getClientAllClientsInOrgReserve(long idOfOrg){
        String sql = "SELECT c.idofClient, c.idoforg, o.shortname, (p.surname || ' ' || p.firstname || ' ' || p.secondname) as fullname,"
                + " g.groupname ," + ClientItem.IN_RESERVE_TYPE +" as plantype"
                + " FROM cf_clients c "
                + " INNER JOIN cf_persons p ON p.idofperson = c.idofperson "
                + " INNER JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND g.idoforg = c.idoforg "
                + " INNER JOIN cf_orgs o ON o.idoforg = c.idoforg "
                + " WHERE c.idoforg = :idOfOrg AND c.categoriesdiscounts like '%50%' "
                + " AND c.idofclientgroup < 1100000000 " ;
        return getClientsBySQL(idOfOrg, sql);
    }

    public List<ClientItem> getClientWithSocialBenefits(long idOfOrg){
        String sql = "SELECT c.idofClient, c.idoforg, o.shortname, (p.surname || ' ' || p.firstname || ' ' || p.secondname) as fullname,"
                + " c.idofclientgroup ," + ClientItem.IN_PLAN_TYPE +" as plantype"
                + " FROM cf_clients c "
                + " INNER JOIN cf_persons p ON p.idofperson = c.idofperson "
                + " INNER JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND g.idoforg = c.idoforg "
                + " INNER JOIN cf_orgs o ON o.idoforg = c.idoforg "
                + " WHERE c.idoforg = :idOfOrg "
                + " AND c.discountmode = 3 "
                + " AND (c.categoriesdiscounts LIKE '1' OR "
                + " c.categoriesdiscounts LIKE '1,%' OR   "
                + " c.categoriesdiscounts LIKE '%,1' OR   "
                + " c.categoriesdiscounts LIKE '%,1,%' OR  "

                + " c.categoriesdiscounts LIKE '2' OR "
                + " c.categoriesdiscounts LIKE '2,%' OR "
                + " c.categoriesdiscounts LIKE '%,2' OR "
                + " c.categoriesdiscounts LIKE '%,2,%' OR "

                + " c.categoriesdiscounts LIKE '3' OR "
                + " c.categoriesdiscounts LIKE '3,%' OR "
                + " c.categoriesdiscounts LIKE '%,3' OR "
                + " c.categoriesdiscounts LIKE '%,3,%' OR "

                + " c.categoriesdiscounts LIKE '4' OR "
                + " c.categoriesdiscounts LIKE '4,%' OR "
                + " c.categoriesdiscounts LIKE '%,4' OR "
                + " c.categoriesdiscounts LIKE '%,4,%' OR "

                + " c.categoriesdiscounts LIKE '5' OR "
                + " c.categoriesdiscounts LIKE '5,%' OR "
                + " c.categoriesdiscounts LIKE '%,5' OR "
                + " c.categoriesdiscounts LIKE '%,5,%' OR "

                + " c.categoriesdiscounts LIKE '101' OR "
                + " c.categoriesdiscounts LIKE '101,%' OR "
                + " c.categoriesdiscounts LIKE '%,101' OR "
                + " c.categoriesdiscounts LIKE '%,101,%' OR "

                + " c.categoriesdiscounts LIKE '103' OR "
                + " c.categoriesdiscounts LIKE '103,%' OR "
                + " c.categoriesdiscounts LIKE '%,103' OR "
                + " c.categoriesdiscounts LIKE '%,103,%' OR "

                + " c.categoriesdiscounts LIKE '104' OR "
                + " c.categoriesdiscounts LIKE '104,%' OR "
                + " c.categoriesdiscounts LIKE '%,104' OR "
                + " c.categoriesdiscounts LIKE '%,104,%' OR "

                + " c.categoriesdiscounts LIKE '105' OR "
                + " c.categoriesdiscounts LIKE '105,%' OR "
                + " c.categoriesdiscounts LIKE '%,105' OR "
                + " c.categoriesdiscounts LIKE '%,105,%' OR "

                + " c.categoriesdiscounts LIKE '106' OR "
                + " c.categoriesdiscounts LIKE '106,%' OR "
                + " c.categoriesdiscounts LIKE '%,106' OR "
                + " c.categoriesdiscounts LIKE '%,106,%'  "

                + " ) "
                + " AND c.idofclientgroup < 1100000000 ";
        return getClientsBySQL(idOfOrg, sql);
    }

    public ClientItem getClient(String orgsIdsString, long idOfClient) {
        String sql = "SELECT c.idofClient, c.idoforg, o.shortname, (p.surname || ' ' || p.firstname || ' ' || p.secondname) as fullname,"
                + " g.groupname ," + ClientItem.IN_PLAN_TYPE +" as plantype "
                + " FROM cf_clients c "
                + " INNER JOIN cf_persons p ON p.idofperson = c.idofperson "
                + " INNER JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND g.idoforg = c.idoforg "
                + " INNER JOIN cf_orgs o ON o.idoforg = c.idoforg "
                + " WHERE c.idoforg  IN ( " + orgsIdsString + " ) and c.idofclient = "  +idOfClient;
        return getClientBySQL( sql);
    }

    public List<ClientItem> getPrimarySchoolClients(Long idOfOrg){
        String sql = "SELECT c.idofClient, c.idoforg, o.shortname, (p.surname || ' ' || p.firstname || ' ' || p.secondname) as fullname,"
                + " g.groupname ," + ClientItem.IN_PLAN_TYPE +" as plantype"
                + " FROM cf_clients c "
                + " INNER JOIN cf_persons p ON p.idofperson = c.idofperson "
                + " INNER JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND g.idoforg = c.idoforg "
                + " INNER JOIN cf_orgs o ON o.idoforg = c.idoforg "
                + " WHERE c.idoforg = :idOfOrg  "
                + " AND g.groupname SIMILAR TO '[1-4](\\D)%'\n ";
        return getClientsBySQL(idOfOrg, sql);
    }
}
