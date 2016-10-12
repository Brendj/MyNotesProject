/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.ComplexInfo;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.cxf.common.i18n.Exception;
import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

/**
 * User: akmukov
 * Date: 01.03.2016
 */
public class OrganizationComplexesStructure implements AbstractToElement{
    private static final String SECTION_NAME="OrganizationComplexesStructure";
    private static final String CODE_ATTRIBUTE="Code";
    private static final String DESCRIPTION_ATTRIBUTE="Descr";

    private final List<ProviderComplexesItem> providerComplexesList = new ArrayList<ProviderComplexesItem>();
    private final long resultCode;
    private final String resultDescription;


    public OrganizationComplexesStructure() {
        this.resultDescription = "OK";
        resultCode = 0;
    }

    public OrganizationComplexesStructure(long resultCode, String resultDescription) {
        this.resultCode = resultCode;
        this.resultDescription = resultDescription;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement(SECTION_NAME);
        element.setAttribute(CODE_ATTRIBUTE,Long.toString(resultCode));
        element.setAttribute(DESCRIPTION_ATTRIBUTE,resultDescription);
        for (ProviderComplexesItem providerComplexesItem : providerComplexesList) {
            element.appendChild(providerComplexesItem.toElement(document));
        }
        return element;
    }

    public void fillComplexesStructure(Session session, Long idOfOrg) {
        providerComplexesList.clear();
        Org org = (Org) session.load(Org.class, idOfOrg);
        Set<Org> friendlyOrgs = org.getFriendlyOrg();
        friendlyOrgs.add(org);
        Set<ConfigurationProvider> uniqueProviders = selectProviders(session, friendlyOrgs);
        if (uniqueProviders.size() == 0)
            return;
        for (ConfigurationProvider configurationProvider : uniqueProviders) {
            ProviderComplexesItem providerComplexesItem = createProviderComplexesItem(session, friendlyOrgs,
                    configurationProvider);
            providerComplexesList.add(providerComplexesItem);
        }
    }

    private ProviderComplexesItem createProviderComplexesItem(Session session, Set<Org> friendlyOrgs,
            ConfigurationProvider configurationProvider) {
        Map<Date, Set<ComplexInfo>> complexesMap = new HashMap<Date, Set<ComplexInfo>>();
        Date searchDate = new Date();
        Set<Org> orgsWithConfiguration = selectOrgsWithConfiguration(configurationProvider, friendlyOrgs);
        Set<ComplexInfo> complexes = findUniqueComplexes(session, orgsWithConfiguration,searchDate);
        complexesMap.put(searchDate,complexes);
        return new ProviderComplexesItem(configurationProvider,complexesMap,orgsWithConfiguration);
    }

    private Set<ConfigurationProvider> selectProviders(Session session, Set<Org> friendlyOrgs) {
        Set<Long> result = new HashSet<Long>();
        for (Org org : friendlyOrgs) {
            ConfigurationProvider configurationProvider = org.getConfigurationProvider();
            if (configurationProvider != null) {
                result.add(configurationProvider.getIdOfConfigurationProvider());
            }
        }
        if (result.size() == 0) {
            return new HashSet<ConfigurationProvider>();
        }
        Query query = session.createQuery(
                "select distinct cfp from ConfigurationProvider cfp where cfp.idOfConfigurationProvider in :ids");
        query.setParameterList("ids", result);
        List list = query.list();
        return new HashSet<ConfigurationProvider>(list);
    }


    private Set<Org> selectOrgsWithConfiguration(ConfigurationProvider configurationProvider, Set<Org> orgs) {
        if (configurationProvider == null)
            return Collections.emptySet();
        HashSet<Org> result = new HashSet<Org>();
        for (Org org : orgs) {
            ConfigurationProvider provider = org.getConfigurationProvider();
            if (provider != null && provider.getIdOfConfigurationProvider().equals(configurationProvider
                    .getIdOfConfigurationProvider())) {
                result.add(org);
            }
        }
        return result;
    }


    private Set<ComplexInfo> findUniqueComplexes(Session session, Set<Org> orgs, Date searchDate) {
        List complexes = findComplexesForOrgs(session, orgs,searchDate);
        Set<ComplexInfo> resultComplexes = filterOnlyUniqueComplexes(complexes);
        return resultComplexes;
    }

    private List findComplexesForOrgs(Session session, Set<Org> orgs, Date searchDate) {
        List<Long> orgsId = new ArrayList<Long>();
        for (Org org : orgs) {
            orgsId.add(org.getIdOfOrg());
        }
        Date beginDate = CalendarUtils.truncateToDayOfMonth(searchDate);
        Date endDate = CalendarUtils.endOfDay(beginDate);
        Query query = session.createQuery(
                "select c from ComplexInfo c left join fetch c.good where c.org.idOfOrg in :orgsId and c.menuDate between :beginDate and :endDate");
        query.setParameterList("orgsId", orgsId);
        query.setParameter("beginDate", beginDate);
        query.setParameter("endDate", endDate);
        return query.list();
    }

    private Set<ComplexInfo> filterOnlyUniqueComplexes(List<ComplexInfo> complexes) {
        Set<ComplexInfo> result = new HashSet<ComplexInfo>();
        for (ComplexInfo complexInfo : complexes) {
            if (!containComplexInResult(result,complexInfo)){
                 result.add(complexInfo);
            }
        }
        return result;
    }

    private boolean containComplexInResult(Set<ComplexInfo> result, ComplexInfo foundComplex) {
        for (ComplexInfo complex : result) {
            if (complex.equals(foundComplex)) {
                return true;
            }
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            // сравнение по ключевым полям
            if (equalsBuilder.append(complex.getIdOfComplex(), foundComplex.getIdOfComplex())
                    .append(complex.getComplexName(), foundComplex.getComplexName())
                    .append(complex.getCurrentPrice(), foundComplex.getCurrentPrice()).isEquals()) {
                return true;
            }
        }
        return false;
    }


    private static class ProviderComplexesItem {
        private static final String PROVIDER_ELEMENT_NAME ="CFP";
        private static final String PROVIDER_ATTR_NAME="name";
        private static final String PROVIDER_ATTR_ID="id";
        private static final String PROVIDER_ATTR_ORGS="orgs";
        private static final String DATE_ELEMENT="Date";
        private static final String DATE_ATTR_VALUE="value";

        private final ConfigurationProvider configurationProvider;
        private final Map<Date,Set<ComplexInfo>> complexesMap;
        private final Set<Org> workedOrgs;

        public ProviderComplexesItem(ConfigurationProvider configurationProvider,
                Map<Date, Set<ComplexInfo>> complexesMap, Set<Org> workedOrgs) {

            this.configurationProvider = configurationProvider;
            this.complexesMap = complexesMap;
            this.workedOrgs = workedOrgs;
        }

        public Element toElement(Document document) {
            Element element = document.createElement(PROVIDER_ELEMENT_NAME);
            element.setAttribute(PROVIDER_ATTR_NAME,configurationProvider.getName());
            element.setAttribute(PROVIDER_ATTR_ID,Long.toString(configurationProvider.getIdOfConfigurationProvider()));
            element.setAttribute(PROVIDER_ATTR_ORGS, StringUtils.join(getOrgIds(),','));
            for (Date date : complexesMap.keySet()) {
                Element elementDate = document.createElement(DATE_ELEMENT);
                elementDate.setAttribute(DATE_ATTR_VALUE,CalendarUtils.getDateFormatLocal().format(date));
                for (ComplexInfo complexInfo : complexesMap.get(date)) {
                    elementDate.appendChild(new ComplexItem(complexInfo).toElement(document));
                }
                element.appendChild(elementDate);
            }
            return element;
        }

        private Set<Long> getOrgIds() {
            Set<Long> result = new HashSet<Long>();
            for (Org org : workedOrgs) {
                result.add(org.getIdOfOrg());
            }
            return result;
        }

    }


    private static class ComplexItem {
        private static final String COMPLEX_ELEMENT_NAME = "CI";
        private static final String ATTR_NAME = "name";
        private static final String ATTR_ID = "id";
        private static final String ATTR_PRICE = "p";
        private static final String ATTR_APPLY_DISCOUNT = "d";
        private static final String ATTR_APPLY_GRANT = "g";
        private static final String ATTR_APPLY_SUBSCRIPTION_FEEDING = "sf";
        private static final String ATTR_CENTRALIZE_VISIBLE = "cv";
        private static final String ATTR_GOOD_GUID = "gGuid";
        private static final String ATTR_GOOD_NAME = "gsname";
        private static final String ATTR_GOOD_FULL_NAME = "gsfullname";

        private final ComplexInfo complexInfo;

        public ComplexItem(ComplexInfo complexInfo) {

            this.complexInfo = complexInfo;
        }

        public Element toElement(Document document) {
            Element element = document.createElement(COMPLEX_ELEMENT_NAME);
            element.setAttribute(ATTR_NAME,complexInfo.getComplexName());
            element.setAttribute(ATTR_ID,Integer.toString(complexInfo.getIdOfComplex()));
            element.setAttribute(ATTR_PRICE,Long.toString(complexInfo.getCurrentPrice()));
            element.setAttribute(ATTR_APPLY_DISCOUNT,Integer.toString(complexInfo.getModeFree()));
            element.setAttribute(ATTR_APPLY_GRANT,Integer.toString(complexInfo.getModeGrant()));
            element.setAttribute(ATTR_APPLY_SUBSCRIPTION_FEEDING,Integer.toString(complexInfo.getUsedSubscriptionFeeding()));
            Integer mv = complexInfo.getModeVisible();
            if (mv == null) {
                mv = 0;
            }
            element.setAttribute(ATTR_CENTRALIZE_VISIBLE,Integer.toString(mv));

            Good good = complexInfo.getGood();
            if (good !=null) {
                element.setAttribute(ATTR_GOOD_GUID, good.getGuid());
                element.setAttribute(ATTR_GOOD_NAME, good.getNameOfGood());
                element.setAttribute(ATTR_GOOD_FULL_NAME, good.getFullName());
            }
            return element;
        }
    }

}
