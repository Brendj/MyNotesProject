/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.TaloonPPStatesEnum;
import ru.axetta.ecafe.processor.core.report.taloonApproval.TaloonApprovalVerification;
import ru.axetta.ecafe.processor.core.report.taloonApproval.TaloonApprovalVerificationItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 18.07.2016.
 */
@Component
@Scope("session")
public class TaloonApprovalVerificationPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private Long idOfOrg;
    private String filter = "Не выбрано";
    private List<TaloonApprovalVerificationItem> items;
    private Date startDate;
    private Date endDate;
    private TaloonApprovalVerification builder;
    private TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail currentTaloonApprovalVerificationItemDetail;
    private TaloonApprovalVerificationItem currentTaloonApprovalVerificationItem;
    private String currentState;

    private static final Logger logger = LoggerFactory.getLogger(TaloonApprovalVerificationPage.class);

    @Override
    public String getPageFilename() {
        return "report/online/taloon_approval_verification";
    }

    public TaloonApprovalVerificationPage() {
        super();
        builder = new TaloonApprovalVerification();
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.idOfOrg = idOfOrg;
        if (this.idOfOrg == null) {
            filter = "Не выбрано";
        } else {
            Org org = (Org)session.load(Org.class, this.idOfOrg);
            filter = org.getShortName();
        }
    }

    public void setData() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Calendar localCalendar = RuntimeContext.getInstance()
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

        localCalendar.setTime(new Date());
        this.startDate = DateUtils.truncate(localCalendar, Calendar.MONTH).getTime();

        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    @Override
    public void onShow() throws Exception {
        setData();
    }

    public void apply() throws Exception {
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            builder.applyChanges(session, items);
            setItems(builder.getItems(session, startDate, endDate, idOfOrg));
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    public void reload() throws Exception {
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            setItems(builder.getItems(session, startDate, endDate, idOfOrg));
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    public void resetPpState() {
        for (TaloonApprovalVerificationItem item : items) {
            for (TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail detail : item.getDetails()) {
                if (detail.equals(currentTaloonApprovalVerificationItemDetail)) {
                    detail.setPpState(TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED);
                    break;
                }
            }
        }
    }

    public void switchPpState() {
        for (TaloonApprovalVerificationItem item : items) {
            for (TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail detail : item.getDetails()) {
                if (detail.equals(currentTaloonApprovalVerificationItemDetail)) {
                    if (currentState.equals(TaloonApprovalVerificationItem.MAKE_CANCEL) && !detail.needFillShippedQty()) {
                        detail.setPpState(TaloonPPStatesEnum.TALOON_PP_STATE_CANCELED);
                    }
                    if (currentState.equals(TaloonApprovalVerificationItem.MAKE_CONFIRM)) {
                        detail.performConfirm();
                    }
                    break;
                }
            }
        }
    }

    public void confirmPpStateAllDay() {
        changePpStateAllDay(TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED);
    }

    public void deselectPpStateAllDay() {
        changePpStateAllDay(TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED);
    }

    public void changePpStateAllDay(TaloonPPStatesEnum state) {
        for (TaloonApprovalVerificationItem item : items) {
            if (item.equals(currentTaloonApprovalVerificationItem)) {
                for (TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail detail : item.getDetails()) {
                    if (detail.getPpState() != null) {
                        detail.setPpState(state);
                    }
                }
                break;
            }
        }
    }

    public String getFilter() {
        return filter;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public List<TaloonApprovalVerificationItem> getItems() {
        return items;
    }

    public void setItems(List<TaloonApprovalVerificationItem> items) {
        this.items = items;
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


    public TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail getCurrentTaloonApprovalVerificationItemDetail() {
        return currentTaloonApprovalVerificationItemDetail;
    }

    public void setCurrentTaloonApprovalVerificationItemDetail(
            TaloonApprovalVerificationItem.TaloonApprovalVerificationItemDetail currentTaloonApprovalVerificationItemDetail) {
        this.currentTaloonApprovalVerificationItemDetail = currentTaloonApprovalVerificationItemDetail;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public TaloonApprovalVerificationItem getCurrentTaloonApprovalVerificationItem() {
        return currentTaloonApprovalVerificationItem;
    }

    public void setCurrentTaloonApprovalVerificationItem(
            TaloonApprovalVerificationItem currentTaloonApprovalVerificationItem) {
        this.currentTaloonApprovalVerificationItem = currentTaloonApprovalVerificationItem;
    }
}
