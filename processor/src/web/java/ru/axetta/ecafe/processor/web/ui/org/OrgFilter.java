/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 27.02.12
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
public class OrgFilter {

    /* полное имя организации*/
    private String officialName;
    /* идентификатор организации */
    private Long idOfOrg;
    private String tag;
    private String city;
    private String district;
    private String guid;
    private Long ekisId;
    private Long orgIdFromNsi;


    /**
     * производит проверку пустоту полей ввода
     *
     * @return true если хотябы одно поле будет не пустым
     * @author Kadyrov Damir
     * @since 2012-02-27
     */
    public boolean isEmpty() {
        return officialName == null && idOfOrg == null && tag == null;
    }

    /**
     * выводид список организаций
     *
     * @param session сессия
     * @return List - список организаций производя выборку либо по идентификатору либо по части имени организации
     * @author Kadyrov Damir
     * @since 2012-02-27
     */
    @SuppressWarnings("unchecked")
    public List<OrgItem> retrieveOrgs(Session session) {
        Criteria criteria = session.createCriteria(Org.class);
        /*criteria.add(Restrictions.or(
                Restrictions.eq("idOfOrg",idOfOrg),
                Restrictions.like("officialName",officialName, MatchMode.ANYWHERE).ignoreCase()
        ));*/
        try {
            Long idOfUser = MainPage.getSessionInstance().getCurrentUser().getIdOfUser();
            ContextDAOServices.getInstance().buildOrgRestriction(idOfUser, criteria);
        } catch (Exception e) {
        }
        if (idOfOrg != null && idOfOrg.compareTo(Long.parseLong("-1")) > 0) {
            criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
        } else if (StringUtils.isNotEmpty(officialName)) {
            criteria.add(Restrictions.or(Restrictions.like("shortName", officialName, MatchMode.ANYWHERE).ignoreCase(),
                    Restrictions.like("officialName", officialName, MatchMode.ANYWHERE).ignoreCase()));
        }
        if (StringUtils.isNotEmpty(tag)) {
            criteria.add(Restrictions.like("tag", tag, MatchMode.ANYWHERE).ignoreCase());
        }
        if (StringUtils.isNotEmpty(city)) {
            criteria.add(Restrictions.like("city", city, MatchMode.ANYWHERE).ignoreCase());
        }
        if (StringUtils.isNotEmpty(district)) {
            criteria.add(Restrictions.like("district", district, MatchMode.ANYWHERE).ignoreCase());
        }
        if (StringUtils.isNotEmpty(guid)) {
            criteria.add(Restrictions.like("guid", guid, MatchMode.ANYWHERE).ignoreCase());
        }
        if(ekisId != null && ekisId.compareTo(Long.parseLong("-1")) > 0){
            criteria.add(Restrictions.eq("ekisId",ekisId));
        }
        if(orgIdFromNsi != null && orgIdFromNsi.compareTo(Long.parseLong("-1")) > 0){
            criteria.add(Restrictions.eq("orgIdFromNsi", orgIdFromNsi));
        }
        criteria.setProjection(Projections.projectionList()
                .add(Projections.distinct(Projections.property("idOfOrg")),"idOfOrg")
                .add(Projections.property("shortName"),"shortName")
                .add(Projections.property("contractId"),"contractId")
                .add(Projections.property("state"),"state")
                .add(Projections.property("phone"),"phone")
                .add(Projections.property("tag"),"tag")
                .add(Projections.property("city"),"city")
                .add(Projections.property("district"),"district")
                .add(Projections.property("shortAddress"),"shortAddress")
                .add(Projections.property("location"),"location")
        );

        criteria.setResultTransformer(Transformers.aliasToBean(OrgItem.class));

        criteria.addOrder(Order.asc("idOfOrg"));
        return (List<OrgItem>) criteria.list();
    }

    /**
     * Статус фильтра
     *
     * @return String
     * @author Kadyrov Damir
     * @since 2012-02-27
     */
    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    /**
     * Очистка полей
     *
     * @return void
     * @author Kadyrov Damir
     * @since 2012-02-27
     */
    public void clear() {
        this.officialName = null;
        this.idOfOrg = null;
        this.tag = null;
        this.guid = null;
        this.ekisId = null;
        this.orgIdFromNsi = null;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        if (idOfOrg == null || idOfOrg == 0) {
            this.idOfOrg = null;
        } else {
            this.idOfOrg = idOfOrg;
        }
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
}
