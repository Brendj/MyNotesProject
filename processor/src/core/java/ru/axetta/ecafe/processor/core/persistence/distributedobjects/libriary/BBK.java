/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 04.08.14
 * Time: 11:14
 */
public class BBK extends LibraryDistributedObject {

    public static final int NAME_LENGTH = 127;
    public static final int NOTE_LENGTH = 255;
    private String name;
    private String note;
    private Set<BBKDetails> bbkDetails;

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "Name", name);
        XMLUtils.setAttributeIfNotNull(element, "Note", note);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        // Проверка на дублирование данных
        Criteria criteria = session.createCriteria(BBK.class);
        criteria.add(Restrictions.eq("name", getName()));
        List bbkList = criteria.list();
        BBK bbk = null;
        if(bbkList != null && !bbkList.isEmpty()){
            bbk = (BBK) bbkList.get(0);
        }
        session.clear();
        if(!(bbk==null || bbk.getDeletedState() || !name.equals(bbk.getName()))){
            DistributedObjectException distributedObjectException =  new DistributedObjectException("BBK DATA_EXIST_VALUE Name equals");
            distributedObjectException.setData(bbk.getGuid());
            throw  distributedObjectException;
        }
    }

    @Override
    public BBK parseAttributes(Node node) throws Exception {
        setName(XMLUtils.getStringAttributeValue(node, "Name", NAME_LENGTH));
        setNote(XMLUtils.getStringAttributeValue(node, "Note", NOTE_LENGTH));
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setName(((BBK) distributedObject).getName());
        setNote(((BBK) distributedObject).getNote());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        Criteria criteria = session.createCriteria(BBK.class);
        buildVersionCriteria(currentMaxVersion, currentLastGuid, currentLimit, criteria);
        createProjections(criteria);
        criteria.setCacheable(false);
        criteria.setReadOnly(true);
        criteria.setResultTransformer(Transformers.aliasToBean(getClass()));
        return criteria.list();
    }

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("name"), "name");
        projectionList.add(Projections.property("note"), "note");

        criteria.setProjection(projectionList);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<BBKDetails> getBbkDetails() {
        return bbkDetails;
    }

    public void setBbkDetails(Set<BBKDetails> bbkDetailsInternal) {
        this.bbkDetails = bbkDetailsInternal;
    }

    @Override
    public String toString() {
        return String.format("BBK{name='%s', note='%s'}", name, note);
    }
}
