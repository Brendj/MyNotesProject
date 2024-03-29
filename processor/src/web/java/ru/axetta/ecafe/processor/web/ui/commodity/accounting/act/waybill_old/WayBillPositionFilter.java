/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill_old;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractFilter;

import org.hibernate.Criteria;
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
public class WayBillPositionFilter extends AbstractFilter {

    //private Long idOfOrg;
    private Long idOfWayBill;
    private Date startDate;
    private Date endDate;
    private Boolean deletedState = true;

    @Override
    public boolean isEmpty() {
        //return (idOfWayBill==null &&  idOfOrg==null && startDate==null && endDate==null);
        return (idOfWayBill==null &&  startDate==null && endDate==null);
    }

    @Override
    public void clear() {
        idOfWayBill = null;
        //idOfOrg=null;
        startDate = endDate = null;
        deletedState = true;
    }

    @Override
    protected void apply(EntityManager entityManager, Criteria crit) {

        if (idOfWayBill!=null) crit.add(Restrictions.eq("wayBill.globalId", idOfWayBill));
        //if (idOfOrg!=null) crit.add(Restrictions.eq("orgOwner", idOfOrg));
        if(deletedState){
            crit.add(Restrictions.eq("deletedState", false));
        }
        if (startDate!=null) {
            crit.add(Restrictions.ge("goodsCreationDate", CalendarUtils.truncateToDayOfMonth(startDate)));
            if (endDate==null) endDate = CalendarUtils.addOneDay(startDate);
        }
        if (endDate!=null) crit.add(Restrictions.le("goodsCreationDate", CalendarUtils.truncateToDayOfMonth(endDate)));
    }

    //public Long getIdOfOrg() {
    //    return idOfOrg;
    //}
    //
    //public void setIdOfOrg(Long idOfOrg) {
    //    this.idOfOrg = idOfOrg;
    //}

    public Long getIdOfWayBill() {
        return idOfWayBill;
    }

    public void setIdOfWayBill(Long idOfWayBill) {
        this.idOfWayBill = idOfWayBill;
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
