/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConsumerRequestDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.StateChange;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.12
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public class GoodRequest extends ConsumerRequestDistributedObject {

    private Date dateOfGoodsRequest;
    private String number;
    private Date doneDate;
    private String comment;
    private String guidOfStaff;
    private Staff staff;
    private DocumentState state;
    private Integer requestType;
    private Set<StateChange> stateChangeInternal;
    private Set<GoodRequestPosition> goodRequestPositionInternal;
    private InformationContents informationContent = InformationContents.ONLY_CURRENT_ORG;

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        if (informationContent == null || informationContent.isDefault()) {
            return super.process(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        }
        if (DAOUtils.isSupplierByOrg(session, idOfOrg)) {
            return super.process(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        } else {
            return toFriendlyOrgsProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        }
    }

    @Override
    public void createProjections(Criteria criteria) {
        Date validDate = CalendarUtils.startOfDay(new Date());
        criteria.createAlias("staff","s", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("dateOfGoodsRequest"), "dateOfGoodsRequest");
        projectionList.add(Projections.property("number"), "number");
        projectionList.add(Projections.property("state"), "state");
        projectionList.add(Projections.property("doneDate"), "doneDate");
        projectionList.add(Projections.property("comment"), "comment");
        projectionList.add(Projections.property("requestType"), "requestType");
        projectionList.add(Projections.property("s.guid"), "guidOfStaff");
        criteria.setProjection(projectionList);
        //criteria.add(Property.forName("doneDate").ge(validDate));
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Staff st  = DAOUtils.findDistributedObjectByRefGUID(Staff.class, session, guidOfStaff);
        if(st==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setStaff(st);
        if (!isGoodDate(session, orgOwner, doneDate, requestType))
        {
            DistributedObjectException distributedObjectException = new DistributedObjectException("CANT_CHANGE_GRP_ON_DATE");
            throw distributedObjectException;
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(dateOfGoodsRequest));
        XMLUtils.setAttributeIfNotNull(element, "Number", number);
        XMLUtils.setAttributeIfNotNull(element, "State", state.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "DoneDate", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(doneDate));
        XMLUtils.setAttributeIfNotNull(element, "Comment", comment);
        XMLUtils.setAttributeIfNotNull(element, "Type", requestType);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfStaff", guidOfStaff);
    }

    @Override
    protected GoodRequest parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null) {
            if (DAOReadonlyService.getInstance().isMenuExchange(getIdOfSyncOrg())) {
                /* случай когда требование создает поставщик */
                throw new DistributedObjectException("NOT_HAVE_RIGHTS_TO_CREATE_OR_MODIFY_A_GOOD_REQUEST");
            }
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        Date dateDateOfGoodsRequest = XMLUtils.getDateTimeAttributeValue(node, "Date");
        String stringNumber = XMLUtils.getStringAttributeValue(node, "Number", 128);
        if (stringNumber != null)
            setNumber(stringNumber);
        Integer integerState = XMLUtils.getIntegerAttributeValue(node, "State");
        if (integerState != null)
            setState(DocumentState.values()[integerState]);
        if (dateDateOfGoodsRequest != null)
            setDateOfGoodsRequest(dateDateOfGoodsRequest);
        Date dateDoneDate = XMLUtils.getDateTimeAttributeValue(node, "DoneDate");
        if (dateDoneDate != null)
            setDoneDate(dateDoneDate);
        String stringComment = XMLUtils.getStringAttributeValue(node, "Comment", 128);
        if (stringComment != null)
            setComment(stringComment);
        Integer typeComment = XMLUtils.getIntegerAttributeValue(node, "Type");
        if (typeComment != null){
            setRequestType(typeComment);
        } else {
            setRequestType(0);
        }
        guidOfStaff = XMLUtils.getStringAttributeValue(node, "GuidOfStaff", 36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setStaff(((GoodRequest) distributedObject).getStaff());
        setGuidOfStaff(((GoodRequest)distributedObject).getGuidOfStaff());
        setDateOfGoodsRequest(((GoodRequest) distributedObject).getDateOfGoodsRequest());
        setNumber(((GoodRequest) distributedObject).getNumber());
        setState(((GoodRequest) distributedObject).getState());
        setDoneDate(((GoodRequest) distributedObject).getDoneDate());
        setComment(((GoodRequest) distributedObject).getComment());
        setRequestType(((GoodRequest) distributedObject).getRequestType());
    }

    @Override
    public void setNewInformationContent(InformationContents informationContent) {
        this.informationContent = informationContent;
    }

    public DocumentState getState() {
        return state;
    }

    public void setState(DocumentState state) {
        this.state = state;
    }

    public void setIntState(Integer value) {
        this.state = DocumentState.values()[value];
    }

    public Integer getIntState() {
        return this.state.ordinal();
    }

    public List<GoodRequestPosition> getGoodRequestPosition(){
        return new ArrayList<GoodRequestPosition>(getGoodRequestPositionInternal());
    }

    public Set<GoodRequestPosition> getGoodRequestPositionInternal() {
        return goodRequestPositionInternal;
    }

    public void setGoodRequestPositionInternal(Set<GoodRequestPosition> goodRequestPositionInternal) {
        this.goodRequestPositionInternal = goodRequestPositionInternal;
    }

    public Set<StateChange> getStateChangeInternal() {
        return stateChangeInternal;
    }

    public void setStateChangeInternal(Set<StateChange> stateChangeInternal) {
        this.stateChangeInternal = stateChangeInternal;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDateOfGoodsRequest() {
        return dateOfGoodsRequest;
    }

    public void setDateOfGoodsRequest(Date dateOfGoodsRequest) {
        this.dateOfGoodsRequest = dateOfGoodsRequest;
    }

    public Integer getRequestType() {
        return requestType;
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }
}
