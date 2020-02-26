/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.daoservices.org.OrgShortItem;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: fazero
 * Date: 6/2/15
 */
public class OrgSelectionBasicPage extends BasicWorkspacePage {

    protected String region;
    protected String filter;
    protected String tagFilter;
    protected String idFilter;

    /*
           0 - доступны все фильтры
           1 - доступны только фильтр по ОУ, ДОУ и СОШ
           2 - доступен только фильтр по поставщикам
           3 - доступны только фильтры по ОУ, ДОУ, СОШ и по поставщикам
           4 - доступен только фильтр по ДОУ
           5 - доступен только фильтр по СОШ
           6 - доступны все фильтры для всех поставщиков
        */
    protected int filterMode = 0;


    // По мере расширения типов ОО необходимо дополнять нижеизложенные группы
    protected static final List<OrganizationType> ONLY_OO_GROUP = Arrays.asList(
            OrganizationType.SCHOOL,
            OrganizationType.KINDERGARTEN,
            OrganizationType.PROFESSIONAL,
            OrganizationType.ADDEDEDUCATION
    );

    protected static final List<OrganizationType> ONLY_SUPPLIERS = Collections
            .singletonList(OrganizationType.SUPPLIER);

    protected static final List<OrganizationType> ONLY_KIND_OO = Collections
            .singletonList(OrganizationType.KINDERGARTEN);

    protected static final List<OrganizationType> ONLY_SCHOOLS = Collections
            .singletonList(OrganizationType.SCHOOL);

    protected List<SelectItem> availableOrganizationTypes;
    protected List<Integer> selectedOrganizationTypes;

    protected List<OrgShortItem> items = Collections.emptyList();
    protected Long idOfContragent;
    protected Long idOfContract;

    @SuppressWarnings("unchecked")
    public static List<OrgShortItem> retrieveOrgs(Session session, String filter, String tagFilter, List<Integer> orgTypes,
            String idFilter, String region, List<Long> idOfSourceMenuOrgList, List<Long> idOfSupplierList,
            Long idOfContragent, Long idOfContract) throws Exception {
        Criteria orgCriteria = session.createCriteria(Org.class);

        Long idOfUser = DAOReadonlyService.getInstance().getUserFromSession().getIdOfUser();
        if (idOfUser == null) {
            throw new Exception("Не удалось получить ID пользователя");
        }
        ContextDAOServices.getInstance().buildOrgRestriction(idOfUser, orgCriteria);

        if (StringUtils.isNotEmpty(filter)) {
            LogicalExpression shortNameOrOfficalNameIlike = Restrictions
                    .or(Restrictions.ilike("shortName", filter, MatchMode.ANYWHERE),
                            Restrictions.ilike("officialName", filter, MatchMode.ANYWHERE));
            Criterion shortNameInfoServiceIlike = Restrictions.ilike("shortNameInfoService", filter, MatchMode.ANYWHERE);

            orgCriteria.add(Restrictions.or(shortNameInfoServiceIlike, shortNameOrOfficalNameIlike));
        }

        if (StringUtils.isNotEmpty(tagFilter)) {
            orgCriteria.add(Restrictions.ilike("tag", tagFilter, MatchMode.ANYWHERE));
        }

        if (StringUtils.isNotBlank(idFilter)) {
            String[] stringIds = idFilter.split("\\s*,\\s*");
            List<Long> ids = new LinkedList<>();
            for(String stringId : stringIds) {
                try {
                    ids.add(Long.valueOf(stringId));
                } catch (Exception ignored) {
                }
            }
            if(CollectionUtils.isNotEmpty(ids)) {
                orgCriteria.add(Restrictions.in("id", ids));
            }
        }

        if (StringUtils.isNotBlank(region)) {
            orgCriteria.add(Restrictions.eq("district", region));
        }

        if (CollectionUtils.isNotEmpty(idOfSourceMenuOrgList) && idOfSourceMenuOrgList.get(0) != null) {
            orgCriteria.createAlias("sourceMenuOrgs", "sm").add(Restrictions.in("sm.idOfOrg", idOfSourceMenuOrgList));
        }

        if (CollectionUtils.isNotEmpty(idOfSupplierList)){
                orgCriteria.add(Restrictions.in("defaultSupplier.idOfContragent", idOfSupplierList));
        }

        if (idOfContract != null) {
            orgCriteria.add(Restrictions.eq("contractId", "" + idOfContract));
        } else if (idOfContragent != null) {
            orgCriteria.add(Restrictions.eq("defaultSupplier.idOfContragent", idOfContragent));
        }

        if(CollectionUtils.isNotEmpty(orgTypes)) {
            orgCriteria.add(Restrictions.in("type", orgTypes));
        }

        orgCriteria.setProjection(
                Projections.projectionList().add(Projections.distinct(Projections.property("idOfOrg")), "idOfOrg")
                        .add(Projections.property("shortName"), "shortName")
                        .add(Projections.property("officialName"), "officialName")
                        .add(Projections.property("address"), "address"));
        orgCriteria.setCacheMode(CacheMode.NORMAL);
        orgCriteria.setCacheable(true);
        orgCriteria.setResultTransformer(Transformers.aliasToBean(OrgShortItem.class));
        orgCriteria.addOrder(Order.asc("idOfOrg"));

        return (List<OrgShortItem>) orgCriteria.list();
    }

    public List<SelectItem> getRegions() {
        List<String> regions = DAOService.getInstance().getRegions();
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(""));
        for (String reg : regions) {
            items.add(new SelectItem(reg));
        }
        return items;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getTagFilter() {
        return tagFilter;
    }

    public void setTagFilter(String tagFilter) {
        this.tagFilter = tagFilter;
    }

    public String getIdFilter() {
        return idFilter;
    }

    public void setIdFilter(String idFilter) {
        this.idFilter = idFilter;
    }

    public int getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(Integer filterMode) {
        this.filterMode = filterMode;
       buildOrgTypesItems(filterMode);
    }

    private void buildOrgTypesItems(Integer filterMode) {
        availableOrganizationTypes.clear();
        selectedOrganizationTypes.clear();
        switch (filterMode) {
            case 1:
            case 7:
                for (OrganizationType type : ONLY_OO_GROUP) {
                    SelectItem item = new SelectItem(type.getCode(), type.getShortType());
                    availableOrganizationTypes.add(item);
                    selectedOrganizationTypes.add(type.getCode());
                }
                break;
            case 2:
                for (OrganizationType type : ONLY_SUPPLIERS) {
                    SelectItem item = new SelectItem(type.getCode(), type.getShortType());
                    availableOrganizationTypes.add(item);
                    selectedOrganizationTypes.add(type.getCode());
                }
                break;
            case 3:
                for (OrganizationType type : ONLY_OO_GROUP) {
                    SelectItem item = new SelectItem(type.getCode(), type.getShortType());
                    availableOrganizationTypes.add(item);
                    selectedOrganizationTypes.add(type.getCode());
                }
                for (OrganizationType type : ONLY_SUPPLIERS) {
                    SelectItem item = new SelectItem(type.getCode(), type.getShortType());
                    availableOrganizationTypes.add(item);
                }
                break;
            case 4:
                for (OrganizationType type : ONLY_KIND_OO) {
                    SelectItem item = new SelectItem(type.getCode(), type.getShortType());
                    availableOrganizationTypes.add(item);
                    selectedOrganizationTypes.add(type.getCode());
                }
                break;
            case 5:
                for (OrganizationType type : ONLY_SCHOOLS) {
                    SelectItem item = new SelectItem(type.getCode(), type.getShortType());
                    availableOrganizationTypes.add(item);
                    selectedOrganizationTypes.add(type.getCode());
                }
                break;
            case 6:
            default:
                for (OrganizationType type : OrganizationType.values()) {
                    SelectItem item = new SelectItem(type.getCode(), type.getShortType());
                    availableOrganizationTypes.add(item);
                    if (!type.equals(OrganizationType.SUPPLIER)) {
                        selectedOrganizationTypes.add(type.getCode());
                    }
                }
        }
    }

    public List<OrgShortItem> getItems() {
        return items;
    }

    public void deselectAllItems() {
        for (OrgShortItem item : getItems()) {
            item.setSelected(false);
        }
    }

    public void selectAllItems() {
        for (OrgShortItem item : getItems()) {
            item.setSelected(true);
        }
    }

    public Long getIdOfContragent() {
        return idOfContragent;
    }

    public void setIdOfContragent(Long idOfContragent) {
        this.idOfContragent = idOfContragent;
    }

    public Long getIdOfContract() {
        return idOfContract;
    }

    public void setIdOfContract(Long idOfContract) {
        this.idOfContract = idOfContract;
    }

    public List<SelectItem> getAvailableOrganizationTypes() {
        return availableOrganizationTypes;
    }

    public void setAvailableOrganizationTypes(List<SelectItem> availableOrganizationTypes) {
        this.availableOrganizationTypes = availableOrganizationTypes;
    }

    public List<Integer> getSelectedOrganizationTypes() {
        return selectedOrganizationTypes;
    }

    public void setSelectedOrganizationTypes(List<Integer> selectedOrganizationTypes) {
        this.selectedOrganizationTypes = selectedOrganizationTypes;
    }

    protected List<OrgShortItem> retrieveOrgs(Session session, List<Long> idOfSourceMenuOrgList,
            List<Long> idOfSupplierList) throws Exception {
        deselectAllItems();
        return retrieveOrgs(session, getFilter(), getTagFilter(), getSelectedOrganizationTypes(), getIdFilter(), getRegion(),
                idOfSourceMenuOrgList, idOfSupplierList, null, null);
    }

    @SuppressWarnings("unchecked")
    protected List<OrgShortItem> retrieveOrgs(Session session, List<Long> idOfSourceMenuOrgList)
            throws Exception {
        deselectAllItems();
        return retrieveOrgs(session, getFilter(), getTagFilter(), getSelectedOrganizationTypes(), getIdFilter(), getRegion(),
                idOfSourceMenuOrgList, Collections.EMPTY_LIST, null, null);
    }
}
