package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
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

    private String code;
    private String name;

    private BBK bbk;
    private String guidBbk;

    private String guidParent;
    private BBKDetails parentBbkDetails;

    private Set<BBKDetails> bbkDetailsInternal;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("parentBbkDetails", "pbd", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("bbk", "b", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("code"), "code");
        projectionList.add(Projections.property("name"), "name");
        projectionList.add(Projections.property("b.guid"), "guidBbk");
        projectionList.add(Projections.property("pbd.guid"), "guidParent");

        criteria.setProjection(projectionList);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "Name", name);
        XMLUtils.setAttributeIfNotNull(element, "Code", code);
        if (!StringUtils.isEmpty(guidBbk))
            XMLUtils.setAttributeIfNotNull(element, "GuidBBK", guidBbk);
        else {
            System.out.println(guid+ ": guidBbk "+guidBbk);
        }
        if (!StringUtils.isEmpty(guidParent))
            XMLUtils.setAttributeIfNotNull(element, "GuidParent", guidParent);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        BBK bbk = DAOUtils.findDistributedObjectByRefGUID(BBK.class, session, guidBbk);
        if (bbk == null) {
            throw new DistributedObjectException("NOT_FOUND_VALUE BBK");
        }
        setBbk(bbk);

        BBKDetails parentBbkDetails = DAOUtils.findDistributedObjectByRefGUID(BBKDetails.class, session, guidParent);
        if (parentBbkDetails != null) {
            setParentBbkDetails(parentBbkDetails);
        }
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return null;
    }

    @Override
    protected BBKDetails parseAttributes(Node node) throws Exception {
        guidParent = XMLUtils.getStringAttributeValue(node, "GuidParent", 36);
        guidBbk = XMLUtils.getStringAttributeValue(node, "GuidBBK", 36);
        code = XMLUtils.getStringAttributeValue(node, "Code", 20);
        name = XMLUtils.getStringAttributeValue(node, "Name", 255);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setGuidBbk(((BBKDetails) distributedObject).getGuidBbk());
        setBbk(((BBKDetails) distributedObject).getBbk());
        setGuidParent(((BBKDetails) distributedObject).getGuidParent());
        setCode(((BBKDetails) distributedObject).getCode());
        setName(((BBKDetails) distributedObject).getName());
    }

    public String getGuidBbk() {
        return guidBbk;
    }

    public void setGuidBbk(String guidBbk) {
        this.guidBbk = guidBbk;
    }

    public BBK getBbk() {
        return bbk;
    }

    public void setBbk(BBK bbk) {
        this.bbk = bbk;
    }

    public String getGuidParent() {
        return guidParent;
    }

    public void setGuidParent(String guidParent) {
        this.guidParent = guidParent;
    }

    public BBKDetails getParentBbkDetails() {
        return parentBbkDetails;
    }

    public void setParentBbkDetails(BBKDetails parentBbkDetails) {
        this.parentBbkDetails = parentBbkDetails;
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

    public Set<BBKDetails> getBbkDetailsInternal() {
        return bbkDetailsInternal;
    }

    public void setBbkDetailsInternal(Set<BBKDetails> bbkDetailsInternal) {
        this.bbkDetailsInternal = bbkDetailsInternal;
    }
}
