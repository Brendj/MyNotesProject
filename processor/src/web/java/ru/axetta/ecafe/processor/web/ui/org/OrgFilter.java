/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

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
    private String location;


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
    public List retrieveOrgs(Session session) {
        Criteria criteria = session.createCriteria(Org.class);
        /*criteria.add(Restrictions.or(
                Restrictions.eq("idOfOrg",idOfOrg),
                Restrictions.like("officialName",officialName, MatchMode.ANYWHERE).ignoreCase()
        ));*/
        if (idOfOrg != null && idOfOrg.compareTo(Long.parseLong("0")) > 0) {
            criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
        } else if (officialName != null) {
            criteria.add(Restrictions.or(Restrictions.like("shortName", officialName, MatchMode.ANYWHERE).ignoreCase(),
                    Restrictions.like("officialName", officialName, MatchMode.ANYWHERE).ignoreCase()));
        }
        if (tag != null && tag.length() > 0) {
            criteria.add(Restrictions.like("tag", tag, MatchMode.ANYWHERE).ignoreCase());
        }
        if (city != null && city.length() > 0) {
            criteria.add(Restrictions.like("city", tag, MatchMode.ANYWHERE).ignoreCase());
        }
        if (district != null && district.length() > 0) {
            criteria.add(Restrictions.like("district", tag, MatchMode.ANYWHERE).ignoreCase());
        }
        if (location != null && location.length() > 0) {
            criteria.add(Restrictions.like("location", tag, MatchMode.ANYWHERE).ignoreCase());
        }

        criteria.addOrder(Order.asc("idOfOrg"));
        return criteria.list();
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

}
