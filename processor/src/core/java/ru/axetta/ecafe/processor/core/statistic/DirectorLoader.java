/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.statistic;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;

import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DirectorLoader {

    public DirectorLoader() {

    }

    public List<DirectorLoader.OrgItem> loadOrganizations(Session session) {
        List<DirectorLoader.OrgItem> resultList = new ArrayList<DirectorLoader.OrgItem>();

        User currentUser = DAOReadonlyService.getInstance().getUserFromSession();

        String sqlQuery =
                "SELECT o.idoforg, o.shortname, o.shortAddress, o.mainbuilding "
                        + "FROM cf_user_director_org udo "
                        + "INNER JOIN cf_friendly_organization fo ON fo.currentorg=udo.idoforg "
                        + "INNER JOIN cf_orgs o ON fo.friendlyorg=o.idoforg "
                        + "WHERE udo.idofuser=:idOfUser "
                        + "ORDER BY o.mainbuilding";

        Query query = session.createSQLQuery(sqlQuery);

        query.setParameter("idOfUser", currentUser.getIdOfUser());

        List list = query.list();

        for (Object o : list) {
            Object vals[]=(Object[])o;

            DirectorLoader.OrgItem item = new DirectorLoader.OrgItem(((BigInteger)vals[0]).longValue(),
                    (String)vals[1], (String)vals[2], ((Integer)vals[3] == 1) ? Boolean.TRUE : Boolean.FALSE);
            resultList.add(item);
        }
        return resultList;
    }

    public static class OrgItem {
        private final Long idOfOrg;
        private final String shortName;
        private final String shortAddress;
        private final Boolean mainBuilding;

        OrgItem(Org org) {
            this(org.getIdOfOrg(), org.getShortName(), org.getShortAddress(), org.isMainBuilding());
        }

        public OrgItem(Long idOfOrg, String shortName, String shortAddress) {
            this.idOfOrg = idOfOrg;
            this.shortName = shortName;
            this.shortAddress = shortAddress;
            this.mainBuilding = Boolean.FALSE;
        }

        public OrgItem(Long idOfOrg, String shortName, String shortAddress, Boolean mainBuilding) {
            this.idOfOrg = idOfOrg;
            this.shortName = shortName;
            this.shortAddress = shortAddress;
            this.mainBuilding = mainBuilding;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getShortAddress() {
            return shortAddress;
        }
    }
}
