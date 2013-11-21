/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SupplierRequestDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.12
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */
public class ActOfWayBillDifference extends SupplierRequestDistributedObject {

    private Date date;
    private String number;
    private Staff staff;
    private String guidOfStaff;
    private Set<WayBill> wayBillInternal;
    private Set<ActOfWayBillDifferencePosition> actOfWayBillDifferencePositionInternal;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean addReceiverRestriction(Criteria criteria, Session session, String supplierOrgId, boolean isReceiver) {
        final String s = "select distinct ad.globalId from WayBill wb left join wb.actOfWayBillDifference ad where ";
        Query query = session.createQuery(s +(isReceiver?"wb.receiver":"wb.shipper")+"=:idOfOrg and ad!=null");
        query.setParameter("idOfOrg", supplierOrgId);
        List<Long> ids = query.list();
        if(ids!=null && !ids.isEmpty()) {
            criteria.add(Restrictions.in("globalId", ids));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("staff","s", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        projectionList.add(Projections.property("date"), "date");
        projectionList.add(Projections.property("number"), "number");

        projectionList.add(Projections.property("s.guid"), "guidOfStaff");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Staff st = DAOUtils.findDistributedObjectByRefGUID(Staff.class, session, guidOfStaff);
        if(st==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setStaff(st);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(date));
        XMLUtils.setAttributeIfNotNull(element, "Number", number);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfStaff", guidOfStaff);
    }

    @Override
    protected ActOfWayBillDifference parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        Date dateOfActOfDifference = XMLUtils.getDateTimeAttributeValue(node, "Date");
        if (dateOfActOfDifference != null)
            setDate(dateOfActOfDifference);
        String stringNumber = XMLUtils.getStringAttributeValue(node, "Number", 128);
        if (stringNumber != null)
            setNumber(stringNumber);
        guidOfStaff = XMLUtils.getStringAttributeValue(node, "GuidOfStaff", 36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setDate(((ActOfWayBillDifference) distributedObject).getDate());
        setNumber(((ActOfWayBillDifference) distributedObject).getNumber());
    }

    public Set<ActOfWayBillDifferencePosition> getActOfWayBillDifferencePositionInternal() {
        return actOfWayBillDifferencePositionInternal;
    }

    public void setActOfWayBillDifferencePositionInternal(
            Set<ActOfWayBillDifferencePosition> actOfWayBillDifferencePositionInternal) {
        this.actOfWayBillDifferencePositionInternal = actOfWayBillDifferencePositionInternal;
    }

    public Set<WayBill> getWayBillInternal() {
        return wayBillInternal;
    }

    public void setWayBillInternal(Set<WayBill> wayBillInternal) {
        this.wayBillInternal = wayBillInternal;
    }

    public String getGuidOfStaff() {
        return guidOfStaff;
    }

    public void setGuidOfStaff(String guidOfStaff) {
        this.guidOfStaff = guidOfStaff;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
