/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import org.hibernate.Transaction;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.TaloonPPStatesEnum;
import ru.axetta.ecafe.processor.core.report.taloonPreorder.TaloonPreorderVerification;
import ru.axetta.ecafe.processor.core.report.taloonPreorder.TaloonPreorderVerificationComplex;
import ru.axetta.ecafe.processor.core.report.taloonPreorder.TaloonPreorderVerificationDetail;
import ru.axetta.ecafe.processor.core.report.taloonPreorder.TaloonPreorderVerificationItem;
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
public class TaloonPreorderVerificationPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private Long idOfOrg;
    private String filter = "Не выбрано";
    private List<TaloonPreorderVerificationItem> items;
    private Date startDate;
    private Date endDate;
    private TaloonPreorderVerification builder;
    private String remarksToShow;
    private boolean changedData;

    private static final Logger logger = LoggerFactory.getLogger(TaloonPreorderVerificationPage.class);

    @Override
    public String getPageFilename() {
        return "report/online/taloon_preorder_verification";
    }

    public TaloonPreorderVerificationPage() {
        super();
        builder = new TaloonPreorderVerification();
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.idOfOrg = idOfOrg;
        if (this.idOfOrg == null) {
            filter = "Не выбрано";
        } else {
            Org org = (Org) session.load(Org.class, this.idOfOrg);
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
        Transaction persistenceTransaction = null;
        changedData = false;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            builder.applyChanges(session, items);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            setItems(builder.getItems(session, startDate, endDate, idOfOrg));
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    public void reload() throws Exception {
        Session session = null;
        changedData = false;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            setItems(builder.getItems(session, startDate, endDate, idOfOrg));
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    public void confirmPpStateAllDay() {
        changePpStateAllDay(TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED);
    }

    public void deselectPpStateAllDay() {
        changePpStateAllDay(TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED);
    }

    public void changePpStateAllDay(TaloonPPStatesEnum ppState) {
        for (TaloonPreorderVerificationItem item : items) {
            item.changePpState(ppState);
        }
    }

    public boolean isAllowedSetPeriodFirstFlag() {
        for (TaloonPreorderVerificationItem item : items) {
            if (item.isAllowedSetFirstFlag()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAllowedClearPeriodFirstFlag() {
        for (TaloonPreorderVerificationItem item : items) {
            if (item.isAllowedClearFirstFlag()) {
                return true;
            }
        }
        return false;
    }

    public boolean isChangedData() {
        if (items == null) {
            return changedData = false;
        }
        for (TaloonPreorderVerificationItem item : items) {
            for (TaloonPreorderVerificationComplex complex : item.getComplexes()) {
                for (TaloonPreorderVerificationDetail detail : complex.getDetails()) {
                    if (detail.isChangedData()) {
                        return changedData = true;
                    }
                }
            }
        }
        return changedData = false;
    }

    public boolean isNeedFillQty() {
        if (items == null) {
            return false;
        }
        for (TaloonPreorderVerificationItem item : items) {
            for (TaloonPreorderVerificationComplex complex : item.getComplexes()) {
                for (TaloonPreorderVerificationDetail detail : complex.getDetails()) {
                    if (detail.isNeedFillShippedQty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isPpStateNotSelected() {
        return !isPpStateConfirmed();
    }

    public boolean isPpStateConfirmed() {
        for (TaloonPreorderVerificationItem item : items) {
            if (item.getPpState() != TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED) {
                return false;
            }
        }
        return true;
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

    public List<TaloonPreorderVerificationItem> getItems() {
        return items;
    }

    public void setItems(List<TaloonPreorderVerificationItem> items) {
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

    public String getRemarksToShow() {
        return remarksToShow;
    }

    public void setRemarksToShow(String remarksToShow) {
        this.remarksToShow = remarksToShow;
    }
}
