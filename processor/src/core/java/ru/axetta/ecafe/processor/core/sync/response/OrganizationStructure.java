/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
public class OrganizationStructure {
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
             friendlyOrgsIds = DAOUtils.findFriendlyOrgIds(session, org.getIdOfOrg());
        }
        for(Org o : orgList) {
            boolean isFriendly = true;
            if (isAllOrgs) {
                isFriendly = friendlyOrgsIds.contains(o.getIdOfOrg());
            }
            // подгрузка объекта из lazy инициализации
            session.update(o);
            OrganizationStructureItem item = new OrganizationStructureItem(o.getIdOfOrg(), o.getType().ordinal(),
                    o.getShortNameInfoService(), o.getOfficialName(), o.getShortName(),
                    o.getOfficialPerson().getFullName(), o.getAddress(), o.getUsePaydableSubscriptionFeeding(),
                    o.getConfigurationProvider() != null ? o.getConfigurationProvider()
                            .getIdOfConfigurationProvider() : null, isFriendly, o.getDistrict(), o.getOrgStructureVersion());
            organizationItemMap.put(o.getIdOfOrg(), item);
        }
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
        private final Boolean isFriendly;
        private final String nCounty;
        private final Long version;

        private OrganizationStructureItem(Long idOfOrg, Integer organizationType, String shortNameInfoService, String officialName,
                String shortName, String chief, String address,Boolean useSubscriptionFeeding,Long configurationId, Boolean isFriendly, String nCounty, Long version) {
            this.idOfOrg = idOfOrg;
            this.organizationType = organizationType;
            this.shortNameInfoService = shortNameInfoService;
            this.officialName = officialName;
            this.shortName = shortName;
            this.chief = chief;
            this.address = address;
            this.useSubscriptionFeeding = useSubscriptionFeeding;
            this.configurationId = configurationId;
            this.isFriendly = isFriendly;
            this.nCounty = nCounty;
            this.version = version;
        }

        public Element toElement(Document document) throws Exception{
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
            element.setAttribute("Fr", isFriendly ? "1" : "0");
            element.setAttribute("NCounty", nCounty);
            return element;
        }

    }

}
