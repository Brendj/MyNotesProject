/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding;

import ru.axetta.ecafe.processor.core.persistence.PreorderComplex;
import ru.axetta.ecafe.processor.core.persistence.PreorderMenuDetail;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class PreOrdersFeedingItem {
    private final Long idOfClient;
    private final Date dateStart;
    private final Long version;
    private final Boolean isDeleted;
    private final Long idOfOrg;
    private final Date createdDate;
    private final Date lastUpdate;
    private final Boolean isComposite;
    private List<PreOrderFeedingDetail> preOrderFeedingDetailList = new LinkedList<PreOrderFeedingDetail>();

    public PreOrdersFeedingItem(Session session, PreorderComplex preorderComplex) {
        this.idOfClient = preorderComplex.getClient().getIdOfClient();
        this.dateStart = preorderComplex.getPreorderDate();
        this.version = preorderComplex.getVersion();
        this.isDeleted = preorderComplex.getDeletedState();
        this.idOfOrg = preorderComplex.getIdOfOrgOnCreate() == null ? preorderComplex.getClient().getOrg().getIdOfOrg() : preorderComplex.getIdOfOrgOnCreate();
        this.createdDate = preorderComplex.getCreatedDate();
        this.lastUpdate = preorderComplex.getLastUpdate();
        if (preorderComplex.getModeOfAdd() == null) {
            this.isComposite = false;
        } else {
            this.isComposite = preorderComplex.getModeOfAdd().equals(PreorderComplex.COMPLEX_MODE_4) ? true : false;
        }

        PreOrderFeedingDetail feedingDetail = new PreOrderFeedingDetail(preorderComplex);
        this.preOrderFeedingDetailList.add(feedingDetail);

        for (PreorderMenuDetail menuDetail : preorderComplex.getPreorderMenuDetails()) {
            PreOrderFeedingDetail preOrderFeedingDetail = new PreOrderFeedingDetail(session, menuDetail,
                    preorderComplex.getArmComplexId(), preorderComplex.getGuid());
            this.preOrderFeedingDetailList.add(preOrderFeedingDetail);
        }
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public Long getVersion() {
        return version;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Element toElement(Document document) throws Exception{
        Element element = document.createElement("POF");
        DateFormat timeFormat = CalendarUtils.getDateTimeFormatLocal();

        if (null != idOfClient) {
            element.setAttribute("ClientId", Long.toString(idOfClient));
        }
        if (null != dateStart) {
            element.setAttribute("DateStart", timeFormat.format(dateStart));
        }
        if (null != version) {
            element.setAttribute("Version", Long.toString(version));
        }
        if (null != isDeleted) {
            element.setAttribute("IsDeleted", isDeleted ? "1" : "0");
        }
        if (null != idOfOrg) {
            element.setAttribute("OrgId", Long.toString(idOfOrg));
        }
        if (null != createdDate) {
            element.setAttribute("CreatedDate", timeFormat.format(createdDate));
        }
        if (null != lastUpdate) {
            element.setAttribute("LastUpdate", timeFormat.format(lastUpdate));
        }
        if (null != isComposite) {
            element.setAttribute("IsCompositeComplex", isComposite ? "1" : "0");
        }

        for (PreOrderFeedingDetail detail : this.preOrderFeedingDetailList) {
            element.appendChild(detail.toElement(document));
        }
        return element;
    }

    public Boolean getComposite() {
        return isComposite;
    }
}
