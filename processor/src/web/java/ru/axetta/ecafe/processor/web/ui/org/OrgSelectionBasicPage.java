/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.daoservices.org.OrgShortItem;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

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
    protected String idFilter;
    protected Long ekisId;
    protected Long orgIdFromNsi;
    protected Long idOfSelectedContragent;
    protected List<SelectItem> regions;
    protected List<SelectItem> contragentsList = Collections.emptyList();
    protected Integer filterMode = 0;
    protected Boolean districtFilterDisabled = false;

    // По мере расширения типов ОО необходимо дополнять нижеизложенные группы
    protected static final List<OrganizationType> ONLY_OO_GROUP = Arrays.asList(
            OrganizationType.SCHOOL,
            OrganizationType.KINDERGARTEN,
            OrganizationType.PROFESSIONAL,
            OrganizationType.ADDEDEDUCATION
    );

    protected List<Integer> unsupportedUserRole = Arrays.asList(
            User.DefaultRole.ADMIN_SECURITY.ordinal(),
            User.DefaultRole.ADMIN.ordinal(),
            User.DefaultRole.CARD_OPERATOR.ordinal()
    );

    protected static final List<OrganizationType> ONLY_SUPPLIERS = Collections
            .singletonList(OrganizationType.SUPPLIER);

    protected static final List<OrganizationType> ONLY_KIND_OO = Collections
            .singletonList(OrganizationType.KINDERGARTEN);

    protected static final List<OrganizationType> ONLY_SCHOOLS = Collections
            .singletonList(OrganizationType.SCHOOL);

    protected List<OrganizationTypeItem> availableOrganizationTypes = buildAvailableOrganizationTypes();

    protected List<OrgShortItem> items = Collections.emptyList();
    protected Long idOfContract;

    @SuppressWarnings("unchecked")
    public static List<OrgShortItem> retrieveOrgs(Session session, String filter, List<OrganizationTypeItem> orgTypes,
            String idFilter, String region, List<Long> idOfSourceMenuOrgList, List<Long> idOfSupplierList,
            Long idOfContragent, Long idOfContract, boolean onlySupplier, Long ekisId, Long orgIdFromNsi,
            Boolean webARM) throws Exception {
        Criteria orgCriteria = session.createCriteria(Org.class);
        if (webARM != null) {
            orgCriteria.add(Restrictions.eq("useWebArm", webARM));
        }
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

        if(ekisId != null && ekisId.compareTo(Long.parseLong("-1")) > 0){
            orgCriteria.add(Restrictions.eq("ekisId",ekisId));
        }
        if(orgIdFromNsi != null && orgIdFromNsi.compareTo(Long.parseLong("-1")) > 0){
            orgCriteria.add(Restrictions.eq("orgIdFromNsi", orgIdFromNsi));
        }

        if (StringUtils.isNotBlank(region)) {
            orgCriteria.add(Restrictions.eq("district", region));
        }

        if(!onlySupplier) {
            if (CollectionUtils.isNotEmpty(idOfSourceMenuOrgList) && idOfSourceMenuOrgList.get(0) != null) {
                orgCriteria.createAlias("sourceMenuOrgs", "sm").add(Restrictions.in("sm.idOfOrg", idOfSourceMenuOrgList));
            }

            if (CollectionUtils.isNotEmpty(idOfSupplierList)) {
                orgCriteria.add(Restrictions.in("defaultSupplier.idOfContragent", idOfSupplierList));
            } else if (idOfContragent != null) {
                orgCriteria.add(Restrictions.eq("defaultSupplier.idOfContragent", idOfContragent));
            } else if (idOfContract != null) {
                orgCriteria.add(Restrictions.eq("contractId", "" + idOfContract));
            }
        }

        if(CollectionUtils.isNotEmpty(orgTypes)) {
            List<OrganizationType> selectedTypes = new LinkedList<>();
            for(OrganizationTypeItem item : orgTypes){
                if(!item.selected){
                    continue;
                }
                OrganizationType type = OrganizationType.fromInteger(item.getCode());
                if(type != null){
                    selectedTypes.add(type);
                }
            }
            if(!selectedTypes.isEmpty()) {
                orgCriteria.add(Restrictions.in("type", selectedTypes));
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
        if(CollectionUtils.isEmpty(regions)) {
            List<String> regionsFromDB = DAOService.getInstance().getRegions();
            regions = new LinkedList<>();
            regions.add(new SelectItem(""));
            for (String reg : regionsFromDB) {
                regions.add(new SelectItem(reg));
            }
        }
        return regions;
    }

    private List<OrganizationTypeItem> buildAvailableOrganizationTypes(){
        List<OrganizationTypeItem> availableTypes = new LinkedList<>();
        for (OrganizationType type : OrganizationType.values()) {
            OrganizationTypeItem item = new OrganizationTypeItem(type);
            availableTypes.add(item);
        }
        return availableTypes;
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

    public String getIdFilter() {
        return idFilter;
    }

    public void setIdFilter(String idFilter) {
        this.idFilter = idFilter;
    }

    public Long getEkisId() { return ekisId; }

    public void setEkisId(Long ekisId) {
        if (ekisId == null || ekisId == 0) {
            this.ekisId = null;
        } else {
            this.ekisId = ekisId;
        }
    }

    public Long getOrgIdFromNsi() { return orgIdFromNsi; }

    public void setOrgIdFromNsi(Long orgIdFromNsi) {
        if (orgIdFromNsi == null || orgIdFromNsi == 0) {
            this.orgIdFromNsi = null;
        } else {
            this.orgIdFromNsi = orgIdFromNsi;
        }
    }

    public Integer getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(Integer filterMode) {
        this.filterMode = filterMode;
    }

    public Boolean getDistrictFilterDisabled() {
        return districtFilterDisabled;
    }

    public void setDistrictFilterDisabled(Boolean districtFilterDisabled) {
        this.districtFilterDisabled = districtFilterDisabled;
    }

        /*
           0 - доступны все фильтры
           1 - доступны только фильтр по ОУ, ДОУ и СОШ
           2 - доступен только фильтр по поставщикам
           3 - доступны только фильтры по ОУ, ДОУ, СОШ и по поставщикам
           4 - доступен только фильтр по ДОУ
           5 - доступен только фильтр по СОШ
           6 - доступны все фильтры для всех поставщиков
        */

    public Boolean disableContragentFilter(){
        return getFilterMode().equals(2);
    }


    public void updateOrgTypesItems() {
        switch (getFilterMode()) {
            case 2:
                disableAvailableTypesAndSetSelected(ONLY_SUPPLIERS);
                districtFilterDisabled = true;
                break;
            case 3:
                disableAvailableTypesAndSetSelected(CollectionUtils.union(ONLY_OO_GROUP, ONLY_SUPPLIERS));
                districtFilterDisabled = false;
                break;
            case 4:
                disableAvailableTypesAndSetSelected(ONLY_KIND_OO);
                districtFilterDisabled = false;
                break;
            case 5:
                disableAvailableTypesAndSetSelected(ONLY_SCHOOLS);
                districtFilterDisabled = false;
                break;
            case 1:
            case 6:
            case 7:
            default:
                disableAvailableTypesAndSetSelected(null);
                districtFilterDisabled = false;
        }
    }

    protected void resetAvailableOrganizationTypes() {
        for (OrganizationTypeItem item : availableOrganizationTypes) {
            item.setDisabled(false);
            item.setSelected(!item.code.equals(OrganizationType.SUPPLIER.getCode()));
        }
    }

    private void disableAvailableTypesAndSetSelected(Collection<OrganizationType> targetTypes) {
        if (CollectionUtils.isEmpty(targetTypes)) {
            for (OrganizationTypeItem item : availableOrganizationTypes) {
                item.setDisabled(false);
            }
        } else if (targetTypes.size() == 1) {
            for (OrganizationTypeItem item : availableOrganizationTypes) {
                OrganizationType currentType = OrganizationType.fromInteger(item.getCode());
                item.setSelected(targetTypes.contains(currentType));
                item.setDisabled(true);
            }
        } else {
            for (OrganizationTypeItem item : availableOrganizationTypes) {
                OrganizationType currentType = OrganizationType.fromInteger(item.getCode());
                if (targetTypes.contains(currentType)) {
                    item.setDisabled(true);
                } else {
                    item.setSelected(false);
                    item.setDisabled(true);
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
        return idOfSelectedContragent;
    }

    public void setIdOfContragent(Long idOfContragent) {
        this.idOfSelectedContragent = idOfContragent;
    }

    public Long getIdOfContract() {
        return idOfContract;
    }

    public void setIdOfContract(Long idOfContract) {
        this.idOfContract = idOfContract;
    }

    public List<OrganizationTypeItem> getAvailableOrganizationTypes() {
        return availableOrganizationTypes;
    }

    public void setAvailableOrganizationTypes(List<OrganizationTypeItem> availableOrganizationTypes) {
        this.availableOrganizationTypes = availableOrganizationTypes;
    }

    protected List<OrgShortItem> retrieveOrgs(Session session, List<Long> idOfSourceMenuOrgList,
            List<Long> idOfSupplierList) throws Exception {
        return retrieveOrgs(session, idOfSourceMenuOrgList, idOfSupplierList, null);
    }

    protected List<OrgShortItem> retrieveOrgs(Session session, List<Long> idOfSourceMenuOrgList,
            List<Long> idOfSupplierList, Boolean webARM) throws Exception {
        deselectAllItems();
        Long idOfContragent = idOfSelectedContragent == null || idOfSelectedContragent.equals(-1L) ? null : idOfSelectedContragent;
        return retrieveOrgs(session, getFilter(), getAvailableOrganizationTypes(), getIdFilter(), getRegion(),
                idOfSourceMenuOrgList, idOfSupplierList, idOfContragent, null, getFilterMode().equals(2),
                getEkisId(), getOrgIdFromNsi(), webARM);
    }

    @SuppressWarnings("unchecked")
    protected List<OrgShortItem> retrieveOrgs(Session session, List<Long> idOfSourceMenuOrgList)
            throws Exception {
        deselectAllItems();
        Long idOfContragent = idOfSelectedContragent == null || idOfSelectedContragent.equals(-1L) ? null : idOfSelectedContragent;
        return retrieveOrgs(session, getFilter(), getAvailableOrganizationTypes(), getIdFilter(), getRegion(),
                idOfSourceMenuOrgList, Collections.EMPTY_LIST, idOfContragent, null, getFilterMode().equals(2),
                getEkisId(), getOrgIdFromNsi(), null);
    }

    public List<SelectItem> getContragentsList() {
        if (CollectionUtils.isEmpty(contragentsList)) {
            try {
                User user = MainPage.getSessionInstance().getCurrentUser();
                if (unsupportedUserRole.contains(user.getIdOfRole())) {
                    return Collections.emptyList();
                }

                List<Contragent> contragents = DAOService.getInstance().contragentsListByUser(user.getIdOfUser());
                contragentsList = new LinkedList<>();

                contragentsList.add(new SelectItem(-1L, ""));
                for (Contragent c : contragents) {
                    SelectItem item = new SelectItem(c.getIdOfContragent(), c.getContragentName());
                    contragentsList.add(item);
                }
            } catch (Exception e) {
                getLogger().error("Exception when build ContragentList in OrgSelectionBasicPage", e);
                contragentsList = Collections.emptyList();
            }
        }
        return contragentsList;
    }

    public void setContragentsList(List<SelectItem> contragentsList) {
        this.contragentsList = contragentsList;
    }

    public Long getIdOfSelectedContragent() {
        return idOfSelectedContragent;
    }

    public void setIdOfSelectedContragent(Long idOfSelectedContragent) {
        this.idOfSelectedContragent = idOfSelectedContragent;
    }

    public static class OrganizationTypeItem {
        private Boolean selected;
        private Boolean disabled = false;
        private String typeName;
        private Integer code;

        OrganizationTypeItem(OrganizationType type){
            this.typeName = type.getShortType();
            this.code = type.getCode();
            selected = !type.equals(OrganizationType.SUPPLIER);
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public Boolean getDisabled() {
            return disabled;
        }

        public void setDisabled(Boolean disabled) {
            this.disabled = disabled;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }
}
