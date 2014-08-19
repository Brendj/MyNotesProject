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
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 04.08.14
 * Time: 11:14
 */
public class BBK extends LibraryDistributedObject {
    private String name;
    private String note;
    private Set<BBKDetails> bbkDetailsInternal;

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {}

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("name"), "name");
        projectionList.add(Projections.property("note"), "note");

        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return null;
    }

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    public BBK parseAttributes(Node node) throws Exception {
        name = XMLUtils.getStringAttributeValue(node, "Name", 127);
        note = XMLUtils.getStringAttributeValue(node, "Note", 36);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setName(((BBK) distributedObject).getName());
        setNote(((BBK) distributedObject).getNote());
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

    public Set<BBKDetails> getBbkDetailsInternal() {
        return bbkDetailsInternal;
    }

    public void setBbkDetailsInternal(Set<BBKDetails> bbkDetailsInternal) {
        this.bbkDetailsInternal = bbkDetailsInternal;
    }

    public List<BBKDetails> getBbkDetail() {
        return new ArrayList<BBKDetails>(getBbkDetailsInternal());
    }
}
