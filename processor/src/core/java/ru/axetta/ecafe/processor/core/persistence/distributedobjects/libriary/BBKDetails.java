/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 07.08.14
 * Time: 14:26
 */

public class BBKDetails extends LibraryDistributedObject {

    public static final int CODE_LENGTH = 20;
    public static final int NAME_LENGTH = 255;
    public static final int GUID_LENGTH = 36;
    private String code;
    private String name;

    private String guidBBK;
    private BBK bbk;

    private String guidParentBBKDetails;
    private BBKDetails parentBBKDetails;

    private Set<BBKDetails> bbkDetailsInternal;


    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "Code", code);
        XMLUtils.setAttributeIfNotNull(element, "Name", name);
        XMLUtils.setAttributeIfNotNull(element, "GuidBBK", guidBBK);
        XMLUtils.setAttributeIfNotNull(element, "GuidParent", guidParentBBKDetails);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {

        // Проверка на наличие в базе объекта BBKDetails с указанным guidParentBBKDetails
        // и установка значения parentBBKDetails
        BBKDetails parentBBKDetails = DAOUtils.findDistributedObjectByRefGUID(BBKDetails.class, session, guidParentBBKDetails);
        if (parentBBKDetails != null) {
            setParentBBKDetails(parentBBKDetails);
        } else { setParentBBKDetails(null); }

        // Создание запроса для выборки из таблицы BBKDetails объектов с идентичными полями code и name
        Criteria criteria = session.createCriteria(BBKDetails.class);
        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("code", code));
        conjunction.add(Restrictions.eq("name", name));
        criteria.add(conjunction);

        BBKDetails bbkDetails = null;

        List bbkDetailsList = criteria.list();

        if (bbkDetailsList != null && !bbkDetailsList.isEmpty()) {
            bbkDetails = (BBKDetails) bbkDetailsList.get(0);
        }

        if(!(bbkDetails==null || bbkDetails.getDeletedState() || guid.equals(bbkDetails.getGuid()))){
            DistributedObjectException distributedObjectException =  new DistributedObjectException("BBKDetail DATA_EXIST_VALUE Code and BBK equals");
            distributedObjectException.setData(bbkDetails.getGuid());
            throw  distributedObjectException;
        }

        // Проверка на наличие в базе объекта BBK с указанным guidBBK и инициация исключения если его нет
        BBK bbkLocal = DAOUtils.findDistributedObjectByRefGUID(BBK.class, session, guidBBK);
        if (null == bbkLocal) {
            throw new DistributedObjectException("NOT_FOUND_VALUE BBK");
        } else {
            setBbk(bbkLocal);
        }
    }

    @Override
    protected BBKDetails parseAttributes(Node node) throws Exception {
        code = XMLUtils.getStringAttributeValue(node, "Code", CODE_LENGTH);
        name = XMLUtils.getStringAttributeValue(node, "Name", NAME_LENGTH);
        guidBBK = XMLUtils.getStringAttributeValue(node, "GuidBBK", GUID_LENGTH);
        guidParentBBKDetails = XMLUtils.getStringAttributeValue(node, "GuidParent", GUID_LENGTH);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setCode(((BBKDetails) distributedObject).getCode());
        setName(((BBKDetails) distributedObject).getName());
        setBbk(((BBKDetails) distributedObject).getBbk());
        setGuidBBK(((BBKDetails) distributedObject).getGuidBBK());
        setParentBBKDetails(((BBKDetails) distributedObject).getParentBBKDetails());
        setGuidParentBBKDetails(((BBKDetails) distributedObject).getGuidParentBBKDetails());
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        Criteria criteria = session.createCriteria(getClass());
        buildVersionCriteria(currentMaxVersion, currentLastGuid, currentLimit, criteria);
        createProjections(criteria);
        criteria.setCacheable(false);
        criteria.setReadOnly(true);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
        return criteria.list();
    }

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("bbk", "b", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("parentBBKDetails", "pbd", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("code"), "code");
        projectionList.add(Projections.property("name"), "name");

        projectionList.add(Projections.property("b.guid"), "guidBBK");
        projectionList.add(Projections.property("pbd.guid"), "guidParentBBKDetails");

        criteria.setProjection(projectionList);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BBK getBbk() {
        return bbk;
    }

    public void setBbk(BBK bbk) {
        this.bbk = bbk;
    }

    public BBKDetails getParentBBKDetails() {
        return parentBBKDetails;
    }

    public void setParentBBKDetails(BBKDetails parentBBKDetails) {
        this.parentBBKDetails = parentBBKDetails;
    }

    public Set<BBKDetails> getBbkDetailsInternal() {
        return bbkDetailsInternal;
    }

    public void setBbkDetailsInternal(Set<BBKDetails> bbkDetailsInternal) {
        this.bbkDetailsInternal = bbkDetailsInternal;
    }

    @Override
    public String toString() {
        return String.format("BBKDetails{code='%s', name='%s', guidbbk='%s', guidparentbbkdetail='%s'}", code, name, guidBBK, guidParentBBKDetails);
    }

    public String getGuidBBK() {
        return guidBBK;
    }

    public void setGuidBBK(String guidBBK) {
        this.guidBBK = guidBBK;
    }

    public String getGuidParentBBKDetails() {
        return guidParentBBKDetails;
    }

    public void setGuidParentBBKDetails(String guidParentBBKDetails) {
        this.guidParentBBKDetails = guidParentBBKDetails;
    }
}
