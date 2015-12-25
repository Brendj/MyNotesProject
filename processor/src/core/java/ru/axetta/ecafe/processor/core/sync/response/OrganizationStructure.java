/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.Persistence;
import java.util.HashMap;
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

    public void addOrganizationStructureInfo(Session session, Long idOfOrg){
        Org org = (Org)session.load(Org.class, idOfOrg);
        OrganizationStructureItem item = new OrganizationStructureItem(org.getIdOfOrg(), org.getType().ordinal(), org.getShortNameInfoService(), org.getOfficialName(),
                org.getShortName(), org.getOfficialPerson().getFullName(), org.getAddress());
        organizationItemMap.put(org.getIdOfOrg(), item);
    }

    private static class OrganizationStructureItem {
        private final Long idOfOrg;
        private final Integer organizationType;
        private final String shortNameInfoService;
        private final String officialName;
        private final String shortName;
        private final String chief;
        private final String address;
        //private final Long version;

        //private List<ProhibitionMenuDetail> prohibitionMenuDetails = new LinkedList<ProhibitionMenuDetail>();

        private OrganizationStructureItem(Long idOfOrg, Integer organizationType, String shortNameInfoService, String officialName,
                String shortName, String chief, String address) {
            this.idOfOrg = idOfOrg;
            this.organizationType = organizationType;
            this.shortNameInfoService = shortNameInfoService;
            this.officialName = officialName;
            this.shortName = shortName;
            this.chief = chief;
            this.address = address;
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
            element.setAttribute("Version", "-1"); //todo remove stub
            return element;
        }

    }

}
