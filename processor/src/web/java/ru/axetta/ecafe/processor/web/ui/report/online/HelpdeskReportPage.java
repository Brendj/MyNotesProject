/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.HelpRequest;
import ru.axetta.ecafe.processor.core.persistence.HelpRequestStatusEnumType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.HelpdeskItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 25.01.2018.
 */
@Component
@Scope(value = "session")
public class HelpdeskReportPage extends BasicWorkspacePage {
    private final List<HelpdeskItem> items = new ArrayList<HelpdeskItem>();;
    private static final Logger logger = LoggerFactory.getLogger(HelpdeskReportPage.class);
    private Date startDate;
    private Date endDate;
    private Long selectedIdOfHelpRequest;

    @Override
    public String getPageFilename() {
        return "report/online/helpdesk";
    }

    @PostConstruct
    public void setDates() {
        startDate = CalendarUtils.startOfDay(new Date());
        endDate = CalendarUtils.endOfDay(new Date());
    }

    public void reload() throws Exception {
        startDate = CalendarUtils.startOfDay(startDate);
        endDate = CalendarUtils.endOfDay(endDate);
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            List<HelpRequest> list = getHelpRequestItems(session);
            items.clear();
            for (HelpRequest helpRequest : list) {
                items.add(new HelpdeskItem(helpRequest));
            }
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    private List getHelpRequestItems(Session session) {
        Criteria criteria = session.createCriteria(HelpRequest.class);
        criteria.add(Restrictions.between("requestDate", startDate, endDate));
        criteria.addOrder(org.hibernate.criterion.Order.desc("requestDate"));
        return criteria.list();
    }

    public void closeHelpRequest() {
        HelpdeskItem item = getCurrentHelpdeskItem();
        if (item != null) {
            item.setStatus(HelpRequestStatusEnumType.WORKED_OUT);
            item.setChanged(true);
            item.setEditMode(false);
        }
    }

    public void editHelpRequest() {
        HelpdeskItem item = getCurrentHelpdeskItem();
        if (item != null) {
            item.setEditMode(true);
        }
    }

    public void cancelEditHelpRequest() {
        HelpdeskItem item = getCurrentHelpdeskItem();
        if (item != null) {
            item.setEditMode(false);
            item.setComment(item.getPrevComment());
        }
    }

    private HelpdeskItem getCurrentHelpdeskItem() {
        if (selectedIdOfHelpRequest == null) return null;
        for (HelpdeskItem item : items) {
            if (item.getIdOfHelpRequest().equals(selectedIdOfHelpRequest)) {
                return item;
            }
        }
        return null;
    }

    public void apply() throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Long version = null;
            for (HelpdeskItem item : items) {
                if (item.isChanged()) {
                    if (version == null) version = DAOUtils.nextVersionByHelpRequests(session);
                    HelpRequest helpRequest = (HelpRequest)session.load(HelpRequest.class, item.getIdOfHelpRequest());
                    helpRequest.setStatus(item.getStatus());
                    helpRequest.setComment(item.getComment());
                    helpRequest.setRequestUpdateDate(new Date());
                    helpRequest.setVersion(version);
                    session.save(helpRequest);
                }
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in saving HelpRequest: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        reload();
        printMessage("Изменения сохранены");
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

    public List<HelpdeskItem> getItems() {
        return items;
    }

    public Long getSelectedIdOfHelpRequest() {
        return selectedIdOfHelpRequest;
    }

    public void setSelectedIdOfHelpRequest(Long selectedIdOfHelpRequest) {
        this.selectedIdOfHelpRequest = selectedIdOfHelpRequest;
    }
}
