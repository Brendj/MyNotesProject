/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class OrgSettingDAOUtils {
    private static final Logger logger = LoggerFactory.getLogger(OrgSettingDAOUtils.class);

    public static List<OrgSetting> getOrgSettingsForAllFriendlyOrgByIdOfOrgAndMaxVersion(Long idOfOrg, Long maxVersion, Session session){
        Set<Long> friendlyOrgsIds = DAOUtils.getIdOfFriendlyOrg(session, idOfOrg);

        Criteria criteria = session.createCriteria(OrgSetting.class);
        criteria.add(Restrictions.gt("version", maxVersion));
        criteria.add(Restrictions.in("idOfOrg", friendlyOrgsIds));
        return criteria.list();
    }

    public static Long getMaxVersionOfOrgSettingForFriendlyOrgGroup(Long idOfOrg, Session session) {
        Set<Long> friendlyOrgsIds = DAOUtils.getIdOfFriendlyOrg(session, idOfOrg);

        Query query = session.createQuery("SELECT MAX(os.version) FROM OrgSetting AS os WHERE os.idOfOrg in (:orgIds)");
        query.setParameterList("orgIds", friendlyOrgsIds);
        return (Long) query.uniqueResult();
    }
}
