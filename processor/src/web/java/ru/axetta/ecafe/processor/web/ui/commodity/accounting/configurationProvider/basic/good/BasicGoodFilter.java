/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractFilter;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.07.13
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class BasicGoodFilter extends AbstractFilter {
    private String nameOfGood;
    private Date createdDateBegin;
    private Date createdDateEnd;
    private UnitScale unitsScale;
    private Long netWeight;
    private List<SelectItem> unitsScaleSelectItemList;
    private Calendar localCalendar;

    public BasicGoodFilter() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession httpSession = (HttpSession) facesContext.getExternalContext().getSession(false);
        localCalendar = runtimeContext.getDefaultLocalCalendar(httpSession);
        localCalendar.setTime(new Date());
        if (unitsScaleSelectItemList == null) {
            unitsScaleSelectItemList = new ArrayList<SelectItem>();
            for (UnitScale unitScale: UnitScale.values()){
                this.unitsScaleSelectItemList.add(new SelectItem(unitScale,unitScale.toString()));
            }
        }
        clear();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() {
        nameOfGood =  "";
        createdDateBegin = new Date(CalendarUtils.getTimeFirstDayOfMonth(System.currentTimeMillis()));
        createdDateEnd = new Date(CalendarUtils.getTimeLastDayOfMonth(System.currentTimeMillis()));
        unitsScale = null;
        netWeight = null;
    }

    @Override
    protected void apply(EntityManager entityManager, Criteria crit) {
        if (StringUtils.isNotEmpty(nameOfGood)) {
            crit.add(Restrictions.like("nameOfGood", nameOfGood, MatchMode.ANYWHERE));
        }
        crit.add(Restrictions.between("createdDate", createdDateBegin, createdDateEnd));
        if(unitsScale!=null){
            crit.add(Restrictions.eq("unitsScale", unitsScale));
        }
        if(netWeight!=null && netWeight>0L){
            crit.add(Restrictions.ge("netWeight", netWeight));
        }
    }

    public String getNameOfGood() {
        return nameOfGood;
    }

    public void setNameOfGood(String nameOfGood) {
        this.nameOfGood = nameOfGood;
    }

    public Date getCreatedDateBegin() {
        return createdDateBegin;
    }

    public void setCreatedDateBegin(Date createdDateBegin) {
        this.createdDateBegin = createdDateBegin;
    }

    public Date getCreatedDateEnd() {
        return createdDateEnd;
    }

    public void setCreatedDateEnd(Date createdDateEnd) {
        localCalendar.setTime(createdDateEnd);
        localCalendar.add(Calendar.DAY_OF_MONTH,1);
        localCalendar.add(Calendar.SECOND, -1);
        this.createdDateEnd = localCalendar.getTime();
    }

    public UnitScale getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(UnitScale unitsScale) {
        this.unitsScale = unitsScale;
    }

    public Long getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Long netWeight) {
        this.netWeight = netWeight;
    }

    public List<SelectItem> getUnitsScaleSelectItemList() {
        return unitsScaleSelectItemList;
    }

    public void setUnitsScaleSelectItemList(List<SelectItem> unitsScaleSelectItemList) {
        this.unitsScaleSelectItemList = unitsScaleSelectItemList;
    }
}
