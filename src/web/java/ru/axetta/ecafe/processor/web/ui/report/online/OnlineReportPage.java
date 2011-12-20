/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.*;

public abstract class OnlineReportPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {
    protected Date startDate;
    protected Date endDate;
    protected List<Long> idOfOrgList = new ArrayList<Long>();
    protected Calendar localCalendar;

    public OnlineReportPage() throws RuntimeContext.NotInitializedException {
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();

            FacesContext facesContext = FacesContext.getCurrentInstance();
            localCalendar = runtimeContext
                    .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

            localCalendar.setTime(new Date());
            this.startDate = DateUtils.truncate(localCalendar, Calendar.MONTH).getTime();

            localCalendar.setTime(this.startDate);
            localCalendar.add(Calendar.MONTH, 1);
            this.endDate = localCalendar.getTime();
        } finally {
            RuntimeContext.release(runtimeContext);
        }

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

    public List<Long> getIdOfOrgList() {
        return idOfOrgList;
    }

    private String filter = "Не выбрано";

    public String getFilter() {
        return filter;
    }

    public void completeOrgListSelection(Map<Long, String> orgMap) throws HibernateException {
        if (orgMap != null) {
            idOfOrgList = new ArrayList<Long>();
            if (orgMap.isEmpty())
                filter = "Не выбрано";
            else {
                filter = "";
                for(Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    filter = filter.concat(orgMap.get(idOfOrg) + "; ");
                }
                filter = filter.substring(0, filter.length() - 1);
            }
        }
    }

}
