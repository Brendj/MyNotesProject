/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.DataBaseSafeConverterUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Long maxVer = (Long) query.uniqueResult();
        return maxVer == null ? 0 : maxVer;
    }

    public static OrgSetting getOrgSettingByGroupIdAndOrg(Session session, Integer groupID, Integer idOfOrg) {
        Criteria criteria = session.createCriteria(OrgSetting.class);
        criteria.add(Restrictions.eq("idOfOrg", idOfOrg.longValue()));
        criteria.add(Restrictions.eq("settingGroup", OrgSettingGroup.getGroupById(groupID)));
        return (OrgSetting) criteria.uniqueResult();
    }

    public static  Long getLastVersionOfOrgSettings(Session session){
        SQLQuery query = session.createSQLQuery("SELECT MAX(version) FROM CF_OrgSettings");
        Long maxVersion = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(query.uniqueResult());
        return maxVersion == null ? 0 : maxVersion;
    }

    public static  Long getLastVersionOfOrgSettingsItem(Session session){
        SQLQuery query = session.createSQLQuery("SELECT MAX(version) FROM CF_OrgSettings_Items");
        Long maxVersion = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(query.uniqueResult());
        return maxVersion == null ? 0 : maxVersion;
    }

    public static Long getNextVersionOfOrgSettings(Session session){
        return getLastVersionOfOrgSettings(session) + 1L;
    }

    public static Long getNextVersionOfOrgSettingItem(Session session){
        return getLastVersionOfOrgSettingsItem(session) + 1L;
    }

    public static Map<Long, Integer> getOrgSettingItemByOrgAndType(Session session, List<Long> idOfOrgs, Integer settingtype) {
        SQLQuery query = session.createSQLQuery("select os.idoforg, osi.settingvalue from cf_orgsettings_Items osi\n"
                + "left join cf_orgsettings os on os.idoforgsetting=osi.idoforgsetting "
                + "where osi.settingtype = :settingtype "
                + "and os.idoforg in (:idOfOrgs) "
                + "order by osi.version");
        if (idOfOrgs != null && !idOfOrgs.isEmpty()) {
            query = session.createSQLQuery("select os.idoforg, osi.settingvalue from cf_orgsettings_Items osi\n"
                    + "left join cf_orgsettings os on os.idoforgsetting=osi.idoforgsetting "
                    + "where osi.settingtype = :settingtype "
                    + "and os.idoforg in (:idOfOrgs) "
                    + "order by osi.version");
            query.setParameterList("idOfOrgs", idOfOrgs);
        }
        else
        {
            query = session.createSQLQuery("select os.idoforg, osi.settingvalue from cf_orgsettings_Items osi\n"
                    + "left join cf_orgsettings os on os.idoforgsetting=osi.idoforgsetting "
                    + "where osi.settingtype = :settingtype "
                    + "order by osi.version");
        }
        query.setParameter("settingtype", settingtype);
        List resultList = query.list();
        if (resultList.isEmpty())
            return null;
        Map<Long, Integer> result = new HashMap<Long, Integer>();
        for (int i=0; i<resultList.size();i++)
        {
            Object o[] = (Object[]) resultList.get(i);
            result.put(HibernateUtils.getDbLong(o[0]), HibernateUtils.getDbInt(o[1]));
        }
        return result;
    }
}
