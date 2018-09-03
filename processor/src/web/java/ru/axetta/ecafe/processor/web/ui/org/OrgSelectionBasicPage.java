/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.daoservices.org.OrgShortItem;
import ru.axetta.ecafe.processor.core.persistence.MenuExchangeRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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
           0               - нет фильтра
           1               - фильтр "только ОУ"
           4               - фильтр "только ДОУ"
           5               - фильтр "только СОШ"
           другое значение - фильтр "только поставщики"
        */
    protected int supplierFilter = 0;
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
    protected boolean allOrgFilterDisabled = false;
    protected boolean schoolFilterDisabled = false;
    protected boolean primarySchoolFilterDisabled = false;
    protected boolean secondarySchoolFilterDisabled = false;
    protected boolean supplierFilterDisabled = false;
    protected boolean districtFilterDisabled = false;
    protected boolean allOrgsFilterDisabled = false;
    protected List<OrgShortItem> items = Collections.emptyList();
    protected Long idOfContragent;
    protected Long idOfContract;

    @SuppressWarnings("unchecked")
    public static List<OrgShortItem> retrieveOrgs(Session session, String filter, String tagFilter, int supplierFilter,
            String idFilter, String region, List<Long> idOfSourceMenuOrgList, List<Long> idOfSupplierList,
            Long idOfContragent, Long idOfContract) throws Exception {

        Criteria orgCriteria = session.createCriteria(Org.class);
        orgCriteria.addOrder(Order.asc("idOfOrg"));
        //  Ограничение оргов, которые позволено видеть пользователю
        //try {// -- Убран try с пустым catch для потверждения догадки в рамках задачи EP-1377
            Long idOfUser = DAOReadonlyService.getInstance().getUserFromSession().getIdOfUser();
            if(idOfUser == null){
                throw new Exception("Не удалось получить пользователя");
            }
            ContextDAOServices.getInstance().buildOrgRestriction(idOfUser, orgCriteria);
        //} catch (Exception ignored) {
        //}

        if (StringUtils.isNotEmpty(filter)) {
            orgCriteria.add(Restrictions.or(Restrictions.ilike("shortName", filter, MatchMode.ANYWHERE),
                    Restrictions.ilike("officialName", filter, MatchMode.ANYWHERE)));
        }

        if (StringUtils.isNotEmpty(tagFilter)) {
            orgCriteria.add(Restrictions.ilike("tag", tagFilter, MatchMode.ANYWHERE));
        }

        if (idFilter != null && idFilter.length() > 0) {
            try {
                Long id = Long.parseLong(idFilter);
                orgCriteria.add(Restrictions.eq("id", id));
            } catch (Exception ignored) {
            }
        }

        if (region != null && region.length() > 0) {
            orgCriteria.add(Restrictions.eq("district", region));
        }

        if (supplierFilter == 1 && idOfSourceMenuOrgList != null && !idOfSourceMenuOrgList.isEmpty()
                && idOfSourceMenuOrgList.get(0) != null) {
            orgCriteria.createAlias("sourceMenuOrgs", "sm").add(Restrictions.in("sm.idOfOrg", idOfSourceMenuOrgList));
        }

        if (supplierFilter != 6) {
            if (!CollectionUtils.isEmpty(idOfSupplierList)) {
                orgCriteria.add(Restrictions.in("defaultSupplier.idOfContragent", idOfSupplierList));
            }
        }

        if (idOfContract != null) {
            orgCriteria.add(Restrictions.eq("contractId", "" + idOfContract));
        } else if (idOfContragent != null) {
            orgCriteria.add(Restrictions.eq("defaultSupplier.idOfContragent", idOfContragent));
        }

        if (supplierFilter != 0 && supplierFilter != 6) {
            Criteria destMenuExchangeCriteria = session.createCriteria(MenuExchangeRule.class);
            List menuExchangeRuleList = destMenuExchangeCriteria.list();
            HashSet<Long> idOfSourceOrgSet = new HashSet<Long>();
            for (Object object : menuExchangeRuleList) {
                MenuExchangeRule menuExchangeRule = (MenuExchangeRule) object;
                Long idOfSourceOrg = menuExchangeRule.getIdOfSourceOrg();
                if (idOfSourceOrg != null) {
                    idOfSourceOrgSet.add(idOfSourceOrg);
                }
            }
            if (idOfSourceOrgSet.size() > 0) {
                Criterion criterion = Restrictions.in("idOfOrg", idOfSourceOrgSet);
                if (supplierFilter == 1 || supplierFilter == 4 || supplierFilter == 5 || supplierFilter == 2) {
                    criterion = Restrictions.not(criterion);
                }
                if (supplierFilter != 2) {
                    orgCriteria.add(criterion);
                }
            }
            if (supplierFilter == 4) {
                orgCriteria.add(Restrictions.eq("type", OrganizationType.KINDERGARTEN));
            }
            if (supplierFilter == 5) {
                orgCriteria.add(Restrictions.eq("type", OrganizationType.SCHOOL));
            }
            if (supplierFilter == 2) {
                orgCriteria.add(Restrictions.eq("type", OrganizationType.SUPPLIER));
            }
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

    public boolean isDistrictFilterDisabled() {
        return districtFilterDisabled;
    }

    public void setDistrictFilterDisabled(boolean districtFilterDisabled) {
        this.districtFilterDisabled = districtFilterDisabled;
    }

    public boolean isAllOrgsFilterDisabled() {
        return allOrgsFilterDisabled;
    }

    public void setAllOrgsFilterDisabled(boolean allOrgsFilterDisabled) {
        this.allOrgsFilterDisabled = allOrgsFilterDisabled;
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

    public int getSupplierFilter() {
        return supplierFilter;
    }

    public void setSupplierFilter(int supplierFilter) {
        this.supplierFilter = supplierFilter;
    }

    public int getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(int filterMode) {
        this.filterMode = filterMode;
        switch (filterMode) {
            case 1:
                setOrgFilterModeParameters(true, false, true, true, true, false, false);
                setSupplierFilter(1);
                break;
            case 2:
                setOrgFilterModeParameters(true, true, false, true, true, true, false);
                setSupplierFilter(2);
                break;
            case 3:
                setOrgFilterModeParameters(true, false, false, false, false, false, false);
                setSupplierFilter(1);
                break;
            case 4:
                setOrgFilterModeParameters(true, true, true, false, true, false, false);
                setSupplierFilter(4);
                break;
            case 5:
                setOrgFilterModeParameters(true, true, true, true, false, false, false);
                setSupplierFilter(5);
                break;
            case 6:
                setOrgFilterModeParameters(false, false, false, false, false, false, false);
                setSupplierFilter(0);
                break;
            case 7:
                setOrgFilterModeParameters(true, false, true, false, false, false, false);
                setSupplierFilter(1);
                break;
            default:
                setOrgFilterModeParameters(false, false, false, false, false, false, false);
                setSupplierFilter(0);
                break;
        }
    }

    public boolean isAllOrgFilterDisabled() {
        return allOrgFilterDisabled;
    }

    public void setAllOrgFilterDisabled(boolean allOrgFilterDisabled) {
        this.allOrgFilterDisabled = allOrgFilterDisabled;
    }

    public boolean isSchoolFilterDisabled() {
        return schoolFilterDisabled;
    }

    public void setSchoolFilterDisabled(boolean schoolFilterDisabled) {
        this.schoolFilterDisabled = schoolFilterDisabled;
    }

    public boolean isPrimarySchoolFilterDisabled() {
        return primarySchoolFilterDisabled;
    }

    public void setPrimarySchoolFilterDisabled(boolean primarySchoolFilterDisabled) {
        this.primarySchoolFilterDisabled = primarySchoolFilterDisabled;
    }

    public boolean isSecondarySchoolFilterDisabled() {
        return secondarySchoolFilterDisabled;
    }

    public void setSecondarySchoolFilterDisabled(boolean secondarySchoolFilterDisabled) {
        this.secondarySchoolFilterDisabled = secondarySchoolFilterDisabled;
    }

    public boolean isSupplierFilterDisabled() {
        return supplierFilterDisabled;
    }

    public void setSupplierFilterDisabled(boolean supplierFilterDisabled) {
        this.supplierFilterDisabled = supplierFilterDisabled;
    }

    protected void setOrgFilterModeParameters(boolean allOrgFilterDisabled, boolean schoolFilterDisabled,
            boolean supplierFilterDisabled, boolean primarySchoolFilterDisabled, boolean secondarySchoolFilterDisabled,
            boolean districtFilterDisabled, boolean allOrgsFilterDisabled) {
        this.allOrgFilterDisabled = allOrgFilterDisabled;
        this.schoolFilterDisabled = schoolFilterDisabled;
        this.supplierFilterDisabled = supplierFilterDisabled;
        this.primarySchoolFilterDisabled = primarySchoolFilterDisabled;
        this.secondarySchoolFilterDisabled = secondarySchoolFilterDisabled;
        this.districtFilterDisabled = districtFilterDisabled;
        this.allOrgsFilterDisabled = allOrgsFilterDisabled;
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

    protected List<OrgShortItem> retrieveOrgs(Session session, List<Long> idOfSourceMenuOrgList,
            List<Long> idOfSupplierList) throws Exception {
        deselectAllItems();
        return retrieveOrgs(session, getFilter(), getTagFilter(), getSupplierFilter(), getIdFilter(), getRegion(),
                idOfSourceMenuOrgList, idOfSupplierList, null, null);
    }

    @SuppressWarnings("unchecked")
    protected List<OrgShortItem> retrieveOrgs(Session session, List<Long> idOfSourceMenuOrgList)
            throws Exception {
        deselectAllItems();
        return retrieveOrgs(session, getFilter(), getTagFilter(), getSupplierFilter(), getIdFilter(), getRegion(),
                idOfSourceMenuOrgList, Collections.EMPTY_LIST, null, null);
    }
}
