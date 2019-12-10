/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.TaloonPPStatesEnum;
import ru.axetta.ecafe.processor.core.report.taloonApprovalPreorder.TaloonApprovalPreorderVerification;
import ru.axetta.ecafe.processor.core.report.taloonApprovalPreorder.TaloonApprovalPreorderVerificationItem;
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
 * Created by o.petrova on 09.12.2019.
 */
@Component
@Scope("session")
public class TaloonApprovalPreorderVerificationPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private Long idOfOrg;
    private String filter = "Не выбрано";
    private List<TaloonApprovalPreorderVerificationItem> items;
    private Date startDate;
    private Date endDate;
    private TaloonApprovalPreorderVerification builder;
    private TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail currentTaloonApprovalPreorderVerificationItemDetail;
    private TaloonApprovalPreorderVerificationItem currentTaloonApprovalPreorderVerificationItem;
    private String currentState;
    private String remarksToShow;

    private static final Logger logger = LoggerFactory.getLogger(TaloonApprovalPreorderVerificationPage.class);

    @Override
    public String getPageFilename() {
        return "report/online/taloon_approval_preorder_verification";
    }

    public TaloonApprovalPreorderVerificationPage() {
        super();
        builder = new TaloonApprovalPreorderVerification();
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
        for (TaloonApprovalPreorderVerificationItem item : items) {
            for (TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail detail : item.getDetails()) {
                if (detail.equals(currentTaloonApprovalPreorderVerificationItemDetail)) {
                    detail.setPpState(TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED);
                    break;
                }
            }
        }
    }

    public void switchPpState() {
        for (TaloonApprovalPreorderVerificationItem item : items) {
            for (TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail detail : item.getDetails()) {
                if (detail.equals(currentTaloonApprovalPreorderVerificationItemDetail)) {
                    if (currentState.equals(TaloonApprovalPreorderVerificationItem.MAKE_CANCEL) //&& !detail.needFillShippedQty()
                    ) {
                        detail.setPpState(TaloonPPStatesEnum.TALOON_PP_STATE_CANCELED);
                    }
                    if (currentState.equals(TaloonApprovalPreorderVerificationItem.MAKE_CONFIRM)) {
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
        for (TaloonApprovalPreorderVerificationItem item : items) {
            if (item.equals(currentTaloonApprovalPreorderVerificationItem)) {
                for (TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail detail : item.getDetails()) {
                    if (detail.getPpState() != null) {
                        if ((state == TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED // && detail.allowedSetFirstFlag()
                        )
                                ||
                                ((state == TaloonPPStatesEnum.TALOON_PP_STATE_CANCELED || state == TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED)) //&& detail.allowedClearFirstFlag())
                        ) {
                            detail.setPpState(state);
                        }
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

    public List<TaloonApprovalPreorderVerificationItem> getItems() {
        return items;
    }

    public void setItems(List<TaloonApprovalPreorderVerificationItem> items) {
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


    public TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail getCurrentTaloonApprovalPreorderVerificationItemDetail() {
        return currentTaloonApprovalPreorderVerificationItemDetail;
    }

    public void setCurrentTaloonApprovalPreorderVerificationItemDetail(
            TaloonApprovalPreorderVerificationItem.TaloonApprovalPreorderVerificationItemDetail currentTaloonApprovalPreorderVerificationItemDetail) {
        this.currentTaloonApprovalPreorderVerificationItemDetail = currentTaloonApprovalPreorderVerificationItemDetail;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public TaloonApprovalPreorderVerificationItem getCurrentTaloonApprovalPreorderVerificationItem() {
        return currentTaloonApprovalPreorderVerificationItem;
    }

    public void setCurrentTaloonApprovalPreorderVerificationItem(
            TaloonApprovalPreorderVerificationItem currentTaloonApprovalPreorderVerificationItem) {
        this.currentTaloonApprovalPreorderVerificationItem = currentTaloonApprovalPreorderVerificationItem;
    }

    public String getRemarksToShow() {
        return remarksToShow;
    }

    public void setRemarksToShow(String remarksToShow) {
        this.remarksToShow = remarksToShow;
    }
}
