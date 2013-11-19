/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractFilter;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.12
 * Time: 17:52
 * To change this template use File | Settings | File Templates.
 */
public class WayBillFilter extends AbstractFilter {

    private Long idOfOrg;
    private String number;
    private Integer state;
    private Date startDate;
    private Date endDate;
    private Boolean deletedState = true;

    @Override
    public boolean isEmpty() {
        return (StringUtils.isEmpty(number) && state==null && idOfOrg==null && startDate==null && endDate==null);
    }

    @Override
    public void clear() {
        number = "";
        state=null;
        idOfOrg=null;
        startDate = endDate = null;
        deletedState = true;
    }

    @Override
    protected void apply(EntityManager entityManager, Criteria crit) {
        if (!StringUtils.isEmpty(number)) crit.add(Restrictions.like("number", number, MatchMode.ANYWHERE).ignoreCase());
        if (state!=null) crit.add(Restrictions.eq("state", idOfOrg));
        if (idOfOrg!=null) crit.add(Restrictions.eq("orgOwner", idOfOrg));
        if(deletedState){
            crit.add(Restrictions.eq("deletedState", false));
        }
        if (startDate!=null) {
            crit.add(Restrictions.ge("dateOfWayBill", CalendarUtils.truncateToDayOfMonth(startDate)));
            if (endDate==null) endDate = CalendarUtils.addOneDay(startDate);
        }
        if (endDate!=null) crit.add(Restrictions.le("dateOfWayBill", CalendarUtils.truncateToDayOfMonth(endDate)));
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }
}
