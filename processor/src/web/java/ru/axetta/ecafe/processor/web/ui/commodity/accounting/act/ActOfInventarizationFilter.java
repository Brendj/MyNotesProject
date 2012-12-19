/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act;

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
public class ActOfInventarizationFilter extends AbstractFilter {

    private Long idOfOrg;
    private String number;
    private String commission;
    private Date startDate;
    private Date endDate;
    private Boolean deletedState = true;

    @Override
    public boolean isEmpty() {
        return (StringUtils.isEmpty(number) && StringUtils.isEmpty(commission) && idOfOrg==null && startDate==null && endDate==null);
    }

    @Override
    public void clear() {
        number =  commission = "";
        idOfOrg=null;
        startDate = endDate = null;
        deletedState = true;
    }

    @Override
    protected void apply(EntityManager entityManager, Criteria crit) {
        if (!StringUtils.isEmpty(number)) crit.add(Restrictions.like("number", number, MatchMode.ANYWHERE).ignoreCase());
        if (!StringUtils.isEmpty(commission)) crit.add(Restrictions.like("commission", number, MatchMode.ANYWHERE).ignoreCase());
        if (idOfOrg!=null) crit.add(Restrictions.eq("orgOwner", idOfOrg));
        if(deletedState){
            crit.add(Restrictions.eq("deletedState", false));
        }
        if (startDate!=null) {
            crit.add(Restrictions.ge("dateOfAct", CalendarUtils.truncateToDayOfMonth(startDate)));
            if (endDate==null) endDate = CalendarUtils.addOneDay(startDate);
        }
        if (endDate!=null) crit.add(Restrictions.le("dateOfAct", CalendarUtils.truncateToDayOfMonth(endDate)));
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

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
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
