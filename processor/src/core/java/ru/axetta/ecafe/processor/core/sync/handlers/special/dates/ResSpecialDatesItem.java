/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.special.dates;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.SpecialDate;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 13.04.16
 * Time: 12:20
 */
public class ResSpecialDatesItem {
    private Long idOfOrg;
    private Date date;
    private Boolean isWeekend;
    private String comment;
    private String groupName;
    private Boolean deleted;
    private Long idOfOrgOwner;
    private Long version;
    private Integer resCode;
    private String errorMessage;

    public ResSpecialDatesItem() {
    }

    public ResSpecialDatesItem(Session session, SpecialDate specialDate) throws Exception {
        this.idOfOrg = specialDate.getIdOfOrg();
        this.date = specialDate.getDate();
        this.isWeekend = specialDate.getIsWeekend();
        this.deleted = specialDate.getDeleted() == null ? false : specialDate.getDeleted();
        this.comment = "";
        this.version = specialDate.getVersion();
        this.idOfOrgOwner = specialDate.getOrgOwner().getIdOfOrg();
        if (specialDate.getIdOfClientGroup() != null) {
            Criteria criteria = session.createCriteria(ClientGroup.class);
            criteria.add(Restrictions.eq("compositeIdOfClientGroup.idOfOrg", this.idOfOrg));
            criteria.add(Restrictions.eq("compositeIdOfClientGroup.idOfClientGroup", specialDate.getIdOfClientGroup()));
            ClientGroup cg = (ClientGroup)criteria.uniqueResult();
            if (cg != null) {
                this.groupName = cg.getGroupName();
            }
        }
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "IdOfOrg", idOfOrg);
        XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.dateShortToStringFullYear(date));
        XMLUtils.setAttributeIfNotNull(element, "Res", resCode);
        XMLUtils.setAttributeIfNotNull(element, "IsWeekend", isWeekend);
        if(comment != null && !comment.isEmpty()) {
            XMLUtils.setAttributeIfNotNull(element, "Comment", comment);
        }
        XMLUtils.setAttributeIfNotNull(element, "GroupName", groupName);
        if (deleted) {
            XMLUtils.setAttributeIfNotNull(element, "D", deleted);
        }
        if (resCode != null && resCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        return element;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getIsWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(Boolean isWeekend) {
        this.isWeekend = isWeekend;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
