package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
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

public class GoodComplaintBook extends DistributedObject {

    private Client client;
    private Long idOfClient;
    private Good good;
    private String guidOfGood;
    private Set<GoodComplaintIterations> GoodComplaintIterationsInternal;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("client","cl", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("good","g", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("price"), "price");

        projectionList.add(Projections.property("cl.id"), "idOfClient");
        projectionList.add(Projections.property("g.guid"), "guidOfGood");
        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion) throws Exception {
        return toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        DistributedObjectException distributedObjectException = new DistributedObjectException("Client NOT_FOUND_VALUE");
        distributedObjectException.setData(String.valueOf(idOfClient));
        Client c;
        try {
            c = DAOUtils.findClient(session, idOfClient);
        } catch (Exception e) {
            throw distributedObjectException;
        }
        if (c == null) throw distributedObjectException;
        setClient(c);

        Good g = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfGood);
        if (g == null) throw new DistributedObjectException("Good NOT_FOUND_VALUE");
        setGood(g);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", client.getIdOfClient());
        if(StringUtils.isNotEmpty(guidOfGood)){
            XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", guidOfGood);
        }
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setClient(((GoodComplaintBook) distributedObject).getClient());
        setGood(((GoodComplaintBook) distributedObject).getGood());
    }

    @Override
    protected GoodComplaintBook parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        idOfClient = XMLUtils.getLongAttributeValue(node, "IdOfClient");
        guidOfGood = XMLUtils.getStringAttributeValue(node, "GuidOfGoods", 36);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public String getGuidOfGood() {
        return guidOfGood;
    }

    public void setGuidOfGood(String guidOfGood) {
        this.guidOfGood = guidOfGood;
    }

    public Set<GoodComplaintIterations> getGoodComplaintIterationsInternal() {
        return GoodComplaintIterationsInternal;
    }

    public void setGoodComplaintIterationsInternal(Set<GoodComplaintIterations> goodComplaintIterationsInternal) {
        GoodComplaintIterationsInternal = goodComplaintIterationsInternal;
    }

}
