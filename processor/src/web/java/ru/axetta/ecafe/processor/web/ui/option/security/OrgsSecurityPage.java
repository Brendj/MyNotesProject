/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.security;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationSecurityLevel;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 15.04.16
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
public class OrgsSecurityPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(OrgsSecurityPage.class);
    private List<OrgSecurityItem> orgsList;

    private Long filterIdOfOrg;
    private String filterOfficialName;
    private String filterGuid;
    private String filterDistrict;
    private String filterStatus;
    private SelectItem[] securityLevels = readSecurityLevels();
    private Integer securityLevel;

    private Long selectedIdOfOrg;

    private SelectItem[] readSecurityLevels() {
        SelectItem[] items = new SelectItem[3];
        items[0] = new SelectItem(null, "Все");
        items[1] = new SelectItem(OrganizationSecurityLevel.STANDARD.getCode()+1, OrganizationSecurityLevel.STANDARD.toString());
        items[2] = new SelectItem(OrganizationSecurityLevel.EXTENDED.getCode()+1, OrganizationSecurityLevel.EXTENDED.toString());
        return items;
    }

    public void fill(Session session) {
        List<OrgSecurityItem> items;

        items = retrieveOrgs(session);

        this.orgsList = items;
    }

    public void switchSecurityLevel() {
        for(OrgSecurityItem it : orgsList) {
            if (it.getIdOfOrg().equals(selectedIdOfOrg)) {
                it.switchSecurityLevel();
                break;
            }
        }
    }

    public List<OrgSecurityItem> retrieveOrgs(Session session) {
        Criteria criteria = session.createCriteria(Org.class);
        try {
            Long idOfUser = MainPage.getSessionInstance().getCurrentUser().getIdOfUser();
            ContextDAOServices.getInstance().buildOrgRestriction(idOfUser, criteria);
        } catch (Exception e) {
        }
        if (filterIdOfOrg != null && filterIdOfOrg.compareTo(Long.parseLong("0")) > 0) {
            criteria.add(Restrictions.eq("idOfOrg", filterIdOfOrg));
        } else if (StringUtils.isNotEmpty(filterOfficialName)) {
            criteria.add(Restrictions.or(Restrictions.like("shortName", filterOfficialName, MatchMode.ANYWHERE).ignoreCase(),
                    Restrictions.like("officialName", filterOfficialName, MatchMode.ANYWHERE).ignoreCase()));
        }
        if (StringUtils.isNotEmpty(filterGuid)) {
            criteria.add(Restrictions.like("guid", filterGuid, MatchMode.ANYWHERE).ignoreCase());
        }
        if (StringUtils.isNotEmpty(filterDistrict)) {
            criteria.add(Restrictions.like("district", filterDistrict, MatchMode.ANYWHERE).ignoreCase());
        }
        if (securityLevel != null) {
            criteria.add(Restrictions.eq("securityLevel", OrganizationSecurityLevel.fromInteger(securityLevel-1)));
        }
        criteria.setProjection(Projections.projectionList()
                .add(Projections.distinct(Projections.property("idOfOrg")), "idOfOrg")
                .add(Projections.property("shortName"), "shortName")
                .add(Projections.property("shortNameInfoService"), "shortNameInfoService")
                .add(Projections.property("officialName"), "officialName")
                .add(Projections.property("INN"), "inn")
                .add(Projections.property("address"),"address")
                .add(Projections.property("securityLevel"),"securityLevel")
                .add(Projections.property("guid"),"guid")
                .add(Projections.property("city"),"city")
                .add(Projections.property("district"),"district")
                .add(Projections.property("type"),"type")
                .add(Projections.property("state"),"state")
                .add(Projections.property("statusDetailing"), "statusDetailing")
        );

        criteria.setResultTransformer(Transformers.aliasToBean(OrgSecurityItem.class));

        criteria.addOrder(Order.asc("idOfOrg"));
        return (List<OrgSecurityItem>) criteria.list();
    }

    public void save() {

    }

    @Override
    public String getPageFilename() {
        return "option/security/orgs";
    }

    public Object updateOrgListPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to set filter for org list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка организаций: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }

        return null;
    }

    public Object clearOrgListPageFilter() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            clearFilters();
            fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to clear filter for client list page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы списка клиентов: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }
        return null;
    }

    public void clearFilters() {
        filterIdOfOrg = null;
        filterOfficialName = null;
        filterGuid = null;
        filterDistrict = null;
        securityLevel = null;
    }

    public Long getFilterIdOfOrg() {
        return filterIdOfOrg;
    }

    public void setFilterIdOfOrg(Long filterIdOfOrg) {
        if (filterIdOfOrg == null || filterIdOfOrg == 0) {
            this.filterIdOfOrg = null;
        } else {
            this.filterIdOfOrg = filterIdOfOrg;
        }
    }

    public String getFilterOfficialName() {
        return filterOfficialName;
    }

    public void setFilterOfficialName(String filterOfficialName) {
        if (StringUtils.isEmpty(filterOfficialName)) {
            this.filterOfficialName = null;
        } else {
            this.filterOfficialName = filterOfficialName;
        }
    }

    public String getFilterGuid() {
        return filterGuid;
    }

    public void setFilterGuid(String filterGuid) {
        if (StringUtils.isEmpty(filterGuid)) {
            this.filterGuid = null;
        } else {
            this.filterGuid = filterGuid;
        }
    }

    public String getFilterStatus() {
        if (filterIdOfOrg == null && filterOfficialName == null && filterGuid == null && filterDistrict == null && securityLevel == null) {
            return "нет";
        }
        return "установлен";
    }

    public void setFilterStatus(String filterStatus) {
        this.filterStatus = filterStatus;
    }

    public List<OrgSecurityItem> getOrgsList() {
        return orgsList;
    }

    public void setOrgsList(List<OrgSecurityItem> orgsList) {
        this.orgsList = orgsList;
    }

    public String getFilterDistrict() {
        return filterDistrict;
    }

    public void setFilterDistrict(String filterDistrict) {
        if (StringUtils.isEmpty(filterDistrict)) {
            this.filterDistrict = null;
        } else {
            this.filterDistrict = filterDistrict;
        }
    }

    public Long getSelectedIdOfOrg() {
        return selectedIdOfOrg;
    }

    public void setSelectedIdOfOrg(Long selectedIdOfOrg) {
        this.selectedIdOfOrg = selectedIdOfOrg;
    }

    public SelectItem[] getSecurityLevels() {
        return securityLevels;
    }

    public void setSecurityLevels(SelectItem[] securityLevels) {
        this.securityLevels = securityLevels;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        if (securityLevel == null || securityLevel == 0) {
            this.securityLevel = null;
        } else {
            this.securityLevel = securityLevel;
        }
    }
}
