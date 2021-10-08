/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 23.12.15
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
public class OrganizationStructure implements AbstractToElement {

    private Map<Long, OrganizationStructureItem> organizationItemMap = new HashMap<Long, OrganizationStructureItem>();
    private final long resultCode;
    private final String resultDescription;

    public OrganizationStructure() {
        resultCode = 0;
        resultDescription = "OK";
    }

    public OrganizationStructure(long resultCode, String resultDescription) {
        this.resultCode = resultCode;
        this.resultDescription = resultDescription;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("OrganizationStructure");
        element.setAttribute("Code", Long.toString(resultCode));
        element.setAttribute("Descr", resultDescription);
        for (OrganizationStructureItem item : this.organizationItemMap.values()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public void addOrganizationStructureInfo(Session session, Org org, List<Org> orgList, boolean isAllOrgs) {
        List<Long> friendlyOrgsIds = new ArrayList<Long>();
        if (isAllOrgs) {
            friendlyOrgsIds = DAOReadonlyService.getInstance().findFriendlyOrgsIds(org.getIdOfOrg());
        }
        for (Org o : orgList) {
            boolean isFriendly = true;
            if (isAllOrgs) {
                isFriendly = friendlyOrgsIds.contains(o.getIdOfOrg());
            }
            OrganizationStructureItem item = new OrganizationStructureItem(o.getIdOfOrg(), o.getType().ordinal(),
                    o.getShortNameInfoService(), o.getOfficialName(), o.getShortName(),
                    o.getOfficialPerson().getFullName(), o.getAddress(), o.getUsePaydableSubscriptionFeeding(),
                    getConfigurationId(o), getSupplierId(o), isFriendly, o.getDistrict(), o.getState(),
                    o.getVariableFeeding(), o.getNeedVerifyCardSign(), o.getPreordersEnabled(), o.getShortAddress(),
                    o.getOrgStructureVersion(), o.multiCardModeIsEnabled(), o.getPreorderlp(), o.getUseWebArm(),
                    o.getUseLongCardNo());
            organizationItemMap.put(o.getIdOfOrg(), item);
        }
        if (!organizationItemMap.containsKey(org.getIdOfOrg())) {
            //session.refresh(org);
            session.refresh(org.getOfficialPerson());
            OrganizationStructureItem item = new OrganizationStructureItem(org.getIdOfOrg(), org.getType().ordinal(),
                    org.getShortNameInfoService(), org.getOfficialName(), org.getShortName(),
                    org.getOfficialPerson().getFullName(), org.getAddress(), org.getUsePaydableSubscriptionFeeding(),
                    getConfigurationId(org), getSupplierId(org), true, org.getDistrict(), org.getState(),
                    org.getVariableFeeding(), org.getNeedVerifyCardSign(), org.getPreordersEnabled(),
                    org.getShortAddress(), org.getOrgStructureVersion(), org.multiCardModeIsEnabled(),
                    org.getPreorderlp(), org.getUseWebArm(), org.getUseLongCardNo());
            organizationItemMap.put(org.getIdOfOrg(), item);
        }
    }

    private Long getConfigurationId(Org o) {
        return o.getConfigurationProvider() != null ? o.getConfigurationProvider().getIdOfConfigurationProvider()
                : null;
    }

    private Long getSupplierId(Org o) {
        return o.getDefaultSupplier() != null ? o.getDefaultSupplier().getIdOfContragent() : null;
    }

    private static class OrganizationStructureItem {

        private final Long idOfOrg;
        private final Integer organizationType;
        private final String shortNameInfoService;
        private final String officialName;
        private final String shortName;
        private final String chief;
        private final String address;
        private final Boolean useSubscriptionFeeding;
        private final Long configurationId;
        private final Long defaultSupplier;
        private final Boolean isFriendly;
        private final String nCounty;
        private final Long version;
        private final Integer state;
        private final Boolean variableFeeding;
        private final Boolean needVerifyCardSign;
        private final Boolean useSpecialMenu;
        private final String shortAddress;
        private final Boolean multiCardModeEnabled;
        private final Boolean preorderlp;
        private final Boolean useWebArm;
        private final Boolean useLongCardId;

        private OrganizationStructureItem(Long idOfOrg, Integer organizationType, String shortNameInfoService,
                String officialName, String shortName, String chief, String address, Boolean useSubscriptionFeeding,
                Long configurationId, Long defaultSupplier, Boolean isFriendly, String nCounty, Integer state,
                Boolean variableFeeding, Boolean needVerifyCardSign, Boolean useSpecialMenu, String shortAddress,
                Long version, Boolean multiCardModeEnabled, Boolean preorderlp, Boolean useWebArm, Boolean useLongCardId) {
            this.idOfOrg = idOfOrg;
            this.organizationType = organizationType;
            this.shortNameInfoService = shortNameInfoService;
            this.officialName = officialName;
            this.shortName = shortName;
            this.chief = chief;
            this.address = address;
            this.useSubscriptionFeeding = useSubscriptionFeeding;
            this.configurationId = configurationId;
            this.defaultSupplier = defaultSupplier;
            this.isFriendly = isFriendly;
            this.nCounty = nCounty;
            this.state = state;
            this.variableFeeding = variableFeeding;
            this.needVerifyCardSign = needVerifyCardSign;
            this.useSpecialMenu = useSpecialMenu;
            this.shortAddress = shortAddress;
            this.version = version;
            this.multiCardModeEnabled = multiCardModeEnabled;
            this.preorderlp = preorderlp;
            this.useWebArm = useWebArm;
            this.useLongCardId = useLongCardId;
        }

        public Element toElement(Document document) throws Exception {
            Element element = document.createElement("OU");
            element.setAttribute("OrgId", Long.toString(idOfOrg));
            element.setAttribute("OrgType", Integer.toString(organizationType));
            element.setAttribute("Name", shortNameInfoService);
            element.setAttribute("FName", officialName);
            element.setAttribute("NForService", shortName);
            element.setAttribute("DirName", chief);
            element.setAttribute("Address", address);
            element.setAttribute("UsePaydableSF", useSubscriptionFeeding ? "1" : "0");
            element.setAttribute("Version", Long.toString(version));
            if (configurationId != null) {
                element.setAttribute("ConfId", Long.toString(configurationId));
            }
            if (defaultSupplier != null) {
                element.setAttribute("SuplId", Long.toString(defaultSupplier));
            }
            element.setAttribute("Fr", isFriendly ? "1" : "0");
            element.setAttribute("NCounty", nCounty);
            element.setAttribute("State", Integer.toString(state));
            element.setAttribute("UseVariableFeeding", variableFeeding ? "1" : "0");
            element.setAttribute("NeedVerifyCardSign", needVerifyCardSign ? "1" : "0");
            element.setAttribute("UseSpecialMenu", useSpecialMenu ? "1" : "0");
            element.setAttribute("ShortAddress", shortAddress);
            element.setAttribute("multiCardModeEnabled", multiCardModeEnabled ? "1" : "0");
            element.setAttribute("receiveRequestsFromEZD", preorderlp ? "1" : "0");
            element.setAttribute("UseWebArm", useWebArm ? "1" : "0");
            element.setAttribute("UseLongCardId", BooleanUtils.toBoolean(useLongCardId) ? "1" : "0");
            return element;
        }

        public Boolean getPreorderlp() {
            return preorderlp;
        }
    }

}
