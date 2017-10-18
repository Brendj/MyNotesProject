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
                "SELECT o.idoforg, o.shortname, o.mainbuilding "
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
                    (String)vals[1], ((Integer)vals[2] == 1) ? Boolean.TRUE : Boolean.FALSE);
            resultList.add(item);
        }
        return resultList;
    }

    public static class OrgItem {
        private final Long idOfOrg;
        private final String shortName;
        private final Boolean mainBuilding;

        OrgItem(Org org) {
            this(org.getIdOfOrg(), org.getShortName(), org.isMainBuilding());
        }

        public OrgItem(Long idOfOrg, String shortName) {
            this.idOfOrg = idOfOrg;
            this.shortName = shortName;
            this.mainBuilding = Boolean.FALSE;
        }

        public OrgItem(Long idOfOrg, String shortName, Boolean mainBuilding) {
            this.idOfOrg = idOfOrg;
            this.shortName = shortName;
            this.mainBuilding = mainBuilding;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }
    }
}
