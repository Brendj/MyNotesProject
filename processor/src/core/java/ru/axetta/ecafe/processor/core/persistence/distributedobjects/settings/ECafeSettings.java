/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.security.spec.ECField;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 09.11.12
 * Time: 19:06
 * To change this template use File | Settings | File Templates.
 */
public class ECafeSettings extends DistributedObject {

    private String settingValue;
    private SettingsIds settingsId;

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        List<DistributedObject> listSettings = toFriendlyOrgsProcess(session, idOfOrg, currentMaxVersion,
                currentLastGuid, currentLimit);
        for (Long orgId : getOrgIdsFromSettings(listSettings)) {
            List<ECafeSettings> settingsForOrg = selectSettingsForOrganization(listSettings, orgId);
            removeOldDuplicatedSettings(settingsForOrg);
        }
        return toFriendlyOrgsProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
    }

    private Set<Long> getOrgIdsFromSettings(List<DistributedObject> listSettings) {
        Set<Long> uniqueOrgIds = new HashSet<Long>();
        for (DistributedObject distributedObject : listSettings) {
            uniqueOrgIds.add(distributedObject.getOrgOwner());
        }
        return uniqueOrgIds;
    }

    private List<ECafeSettings> selectSettingsForOrganization(List<DistributedObject> listSettings, Long orgId) {
        ArrayList<ECafeSettings> result = new ArrayList<ECafeSettings>();
        for (DistributedObject distributedObject : listSettings) {
            ECafeSettings settings = (ECafeSettings) distributedObject;
            if (settings.orgOwner == orgId) {
                result.add(settings);
            }
        }
        return result;
    }

    private void removeOldDuplicatedSettings(List<ECafeSettings> listSettings) {
        Map<SettingsIds, Long> map = new TreeMap<SettingsIds, Long>();
        for (ECafeSettings settings : listSettings) {
            if (map.keySet().contains(settings.getSettingsId())) {
                if (map.get(settings.getSettingsId()) < settings.getGlobalVersion()) {
                    map.put(settings.getSettingsId(), settings.getGlobalVersion());
                }
            } else {
                map.put(settings.getSettingsId(), settings.getGlobalVersion());
            }
        }
        Iterator<ECafeSettings> settingsIterator = listSettings.iterator();
        while (settingsIterator.hasNext()) {
            ECafeSettings settings = settingsIterator.next();
            Long maxVer = map.get(settings.getSettingsId());
            if (settings.getGlobalVersion() < maxVer) {
                DAOService.getInstance().removeSetting(settings);
            }
        }
    }

    private List<DistributedObject> toFriendlyOrgsProcess(Session session, Long idOfOrg, Long currentMaxVersion, String currentLastGuid, Integer currentLimit) throws
            DistributedObjectException {
        Org currentOrg = (Org) session.load(Org.class, idOfOrg);
        Criteria criteria = session.createCriteria(getClass());
        Set<Long> friendlyOrgIds = OrgUtils.getFriendlyOrgIds(currentOrg);
        criteria.add(Restrictions.in("orgOwner", friendlyOrgIds));
        buildVersionCriteria(currentMaxVersion, currentLastGuid, currentLimit, criteria);
        createProjections(criteria);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
        return criteria.list();
    }

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("settingValue"), "settingValue");
        projectionList.add(Projections.property("settingsId"), "settingsId");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        //if(getTagName().equals("C")){
        Criteria criteria = session.createCriteria(ECafeSettings.class);
        criteria.add(Restrictions.eq("settingsId", settingsId));
        criteria.add(Restrictions.eq("orgOwner", orgOwner));
        criteria.add(Restrictions.ne("guid", guid));
        criteria.setMaxResults(1);
        ECafeSettings settings = (ECafeSettings) criteria.uniqueResult();
        session.clear();
        if (settings != null) {
            DistributedObjectException distributedObjectException = new DistributedObjectException(
                    "ECafeSettings DATA_EXIST_VALUE");
            distributedObjectException.setData(settings.getGuid());
            throw distributedObjectException;
        }
        //}
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Value", settingValue);
        XMLUtils.setAttributeIfNotNull(element, "Id", settingsId.getId() + 1);
    }

    @Override
    protected ECafeSettings parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null) {
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        Integer intId = XMLUtils.getIntegerAttributeValue(node, "Id");
        if (intId != null) {
            final int id = intId - 1;
            SettingsIds settingsId1 = SettingsIds.fromInteger(id);
            if (settingsId1 == null) {
                throw new DistributedObjectException("ECafeSettings Unknown Settings Id");
            }
            setSettingsId(settingsId1);
        } else {
            throw new DistributedObjectException("ECafeSettings Id not null");
        }
        String stringValue = XMLUtils.getStringAttributeValue(node, "Value", 128);
        if (stringValue != null) {
            setSettingValue(stringValue);
            AbstractParserBySettingValue value = getSplitSettingValue();
            if (!value.check()) {
                final String message = "ECafeSettings invalid string value: " + getSettingsId() + " " + getSettingValue();
                throw new DistributedObjectException(message);
            }
        }
        setSendAll(SendToAssociatedOrgs.SendToSelf);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setSettingValue(((ECafeSettings) distributedObject).getSettingValue());
        setSettingsId(((ECafeSettings) distributedObject).getSettingsId());
    }

    // Настройки, заданные на клиенте, имеют приоритет над серверными.
    // Поэтому активную настройку, созданную на сервере, мы блокируем и прикрываем активной клиентской.
    // Не может быть у орг-ии двух активных настроек!
    //@Override
    //public void beforePersist(Session session, Long idOfOrg, String ignoreUuid) {
    //    final String updateString = "delete ECafeSettings where orgOwner=:idoforg and settingsId=:settingsId and guid!=:guid";
    //    final Query updateQ = session.createQuery(updateString);
    //    updateQ.setParameter("idoforg",idOfOrg);
    //    updateQ.setParameter("settingsId",settingsId);
    //    updateQ.setParameter("guid",ignoreUuid);
    //    updateQ.executeUpdate();
    //}
    //
    //@Override
    //public void beforePersist(Session session, Long idOfOrg) {
    //    final String updateString = "delete ECafeSettings where orgOwner=:idoforg and settingsId=:settingsId";
    //    final Query updateQ = session.createQuery(updateString);
    //    updateQ.setParameter("idoforg",idOfOrg);
    //    updateQ.setParameter("settingsId",settingsId);
    //    updateQ.executeUpdate();
    //}

    public SettingsIds getSettingsId() {
        return settingsId;
    }

    public void setSettingsId(SettingsIds settingsId) {
        this.settingsId = settingsId;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public AbstractParserBySettingValue getSplitSettingValue() throws Exception {
        SettingValueParser settingValueParser = new SettingValueParser(settingValue, settingsId);
        return settingValueParser.getParserBySettingValue();
    }

}
