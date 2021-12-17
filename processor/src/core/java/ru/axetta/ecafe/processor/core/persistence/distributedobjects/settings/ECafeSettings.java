/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
    private String settingsIdDescription;
    private InformationContents informationContent = InformationContents.ONLY_CURRENT_ORG;

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        if (informationContent != null && informationContent == InformationContents.FRIENDLY_ORGS) {
            return processSettingsFriendlyOrgs(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        }
        else {
            return processSettingsSelfOrg(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        }
    }

    private List<DistributedObject> processSettingsSelfOrg(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws DistributedObjectException {
        List<DistributedObject> listSettings = toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid,
                currentLimit);
        removeOldDuplicatedSettings(listSettings);
        return toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
    }

    private List<DistributedObject> processSettingsFriendlyOrgs(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws DistributedObjectException {
        List<DistributedObject> listSettings = toFriendlyOrgsProcess(session, idOfOrg, currentMaxVersion,
                currentLastGuid, currentLimit);
        Set<Long> uniqueOrgIds = new HashSet<Long>();
        for (DistributedObject distributedObject : listSettings) {
            uniqueOrgIds.add(distributedObject.getOrgOwner());
        }
        for (Long orgId : uniqueOrgIds) {
            List<DistributedObject> settingsForOrg = selectSettingsForOrganization(listSettings, orgId);
            removeOldDuplicatedSettings(settingsForOrg);
        }
        return toFriendlyOrgsProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
    }

    private List<DistributedObject> selectSettingsForOrganization(List<DistributedObject> listSettings, Long orgId) {
        ArrayList<DistributedObject> result = new ArrayList<DistributedObject>();
        for (DistributedObject distributedObject : listSettings) {
            ECafeSettings settings = (ECafeSettings) distributedObject;
            if (settings.orgOwner.equals(orgId)) {
                result.add(settings);
            }
        }
        return result;
    }

    private void removeOldDuplicatedSettings(List<DistributedObject> listSettings) {
        Map<SettingsIds, Long> map = new TreeMap<SettingsIds, Long>();
        for (DistributedObject distributedObject : listSettings) {
            ECafeSettings settings = (ECafeSettings) distributedObject;
            if (map.keySet().contains(settings.getSettingsId())) {
                if (map.get(settings.getSettingsId()) < settings.getGlobalVersion()) {
                    map.put(settings.getSettingsId(), settings.getGlobalVersion());
                }
            } else {
                map.put(settings.getSettingsId(), settings.getGlobalVersion());
            }
        }
        for (DistributedObject listSetting : listSettings) {
            ECafeSettings settings = (ECafeSettings) listSetting;
            Long maxVer = map.get(settings.getSettingsId());
            if (settings.getGlobalVersion() < maxVer) {
                DAOService.getInstance().removeSetting(settings);
            }
        }
    }

    public boolean isPreOrderFeeding() {
        return settingsId != null && settingsId.equals(SettingsIds.PreOrderFeeding);
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

    @Override
    public void setNewInformationContent(InformationContents informationContent) {
        this.informationContent = informationContent;
    }

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

    public String getSettingsIdDescription() {
        return settingsIdDescription;
    }

    public void setSettingsIdDescription(String settingsIdDescription) {
        this.settingsIdDescription = settingsIdDescription;
    }
}
