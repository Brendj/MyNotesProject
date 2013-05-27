/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.contragent.*;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.01.13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ContragentCompletionReportPage extends BasicWorkspacePage implements ContragentSelectPage.CompleteHandler {

    @PersistenceContext
    private EntityManager entityManager;
    protected Date startDate;
    protected Date endDate;
    private ContragentDAOService contragentDAOService = new ContragentDAOService();
    private List<ContragentCompletionItem> contragentCompletionItems;
    private List<Contragent> contragentList;
    private Contragent defaultSupplier;
    private Integer contragentListCount = 0;
    private Calendar localCalendar;

    @Override
    public void onShow() throws Exception {
        contragentDAOService.setSession((Session) entityManager.getDelegate());
        contragentList = contragentDAOService.getPayAgentContragent();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        contragentListCount = contragentList.size();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

        localCalendar.setTime(new Date());

        this.startDate = DateUtils.truncate(localCalendar, Calendar.DAY_OF_MONTH).getTime();
        localCalendar.setTime(this.startDate);

        localCalendar.add(Calendar.DAY_OF_MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public Object generate(){
        contragentCompletionItems = new ArrayList<ContragentCompletionItem>();
        if(defaultSupplier!=null) {
            List<Org> orgItems = contragentDAOService.findDistributionOrganizationByDefaultSupplier(defaultSupplier);
            if(!orgItems.isEmpty()){
                ContragentCompletionItem total = new ContragentCompletionItem(contragentList);
                for (Org org: orgItems){
                    ContragentCompletionItem contragentCompletionItem = contragentDAOService.generateReportItems(org.getIdOfOrg(),contragentList, this.startDate, this.endDate);
                    this.contragentCompletionItems.add(contragentCompletionItem);
                    total.addContragentPayItems(contragentCompletionItem.getContragentPayItems());
                }
                this.contragentCompletionItems.add(total);
            }
        }
        return null;
    }

    public Integer getContragentListCount() {
        return contragentListCount;
    }

    public List<Contragent> getContragentList() {
        return contragentList;
    }


    public List<ContragentCompletionItem> getContragentCompletionItems() {
        return contragentCompletionItems;
    }

    @Override
    public String getPageFilename() {
        return "report/online/contragent_completion_report";
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        if (null != idOfContragent) {
            this.defaultSupplier = (Contragent) session.get(Contragent.class, idOfContragent);
        }
    }

    public Contragent getDefaultSupplier() {
        return defaultSupplier;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        localCalendar.setTime(endDate);
        localCalendar.add(Calendar.DAY_OF_MONTH,1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
