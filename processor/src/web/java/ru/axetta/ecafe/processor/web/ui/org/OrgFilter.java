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


    /**
     * производит проверку пустоту полей ввода
     * @author Kadyrov Damir
     * @since  2012-02-27
     * @return true если хотябы одно поле будет не пустым
     */
    public boolean isEmpty() {
        return officialName==null && idOfOrg == null;
    }

    /**
     * выводид список организаций
     * @author Kadyrov Damir
     * @since  2012-02-27
     * @param session сессия
     * @return List - список организаций производя выборку либо по идентификатору либо по части имени организации
     */
    public List retrieveOrgs(Session session) {
        Criteria criteria = session.createCriteria(Org.class);
        /*criteria.add(Restrictions.or(
                Restrictions.eq("idOfOrg",idOfOrg),
                Restrictions.like("officialName",officialName, MatchMode.ANYWHERE).ignoreCase()
        ));*/
        if(idOfOrg!=null && idOfOrg.compareTo(Long.parseLong("0"))>0){
            criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
        } else if (officialName!=null) {
            criteria.add(Restrictions.like("officialName",officialName, MatchMode.ANYWHERE).ignoreCase());
        }

        criteria.addOrder(Order.asc("idOfOrg"));
        return criteria.list();
    }

    /**
     * Статус фильтра
     * @author Kadyrov Damir
     * @since  2012-02-27
     * @return String
     */
    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    /**
     * Очистка полей
     * @author Kadyrov Damir
     * @since  2012-02-27
     * @return void
     */
    public void clear() {
        this.officialName=null;
        this.idOfOrg =null;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        if (idOfOrg==null || idOfOrg==0) this.idOfOrg=null;
        else this.idOfOrg = idOfOrg;
    }

}
