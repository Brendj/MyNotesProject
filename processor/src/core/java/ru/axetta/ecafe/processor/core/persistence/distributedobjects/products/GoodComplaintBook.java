package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GoodComplaintBook extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        DistributedObjectException distributedObjectException = new DistributedObjectException("Client NOT_FOUND_VALUE");
        distributedObjectException.setData(String.valueOf(idOfClient));
        Client c;
        try {
            c = (Client) DAOUtils.findClient(session, idOfClient);
        } catch (Exception e) {
            throw distributedObjectException;
        }
        if (c == null) throw distributedObjectException;
        setClient(c);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "IdOfClient", client.getIdOfClient());
    }

    @Override
    protected GoodComplaintBook parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null) setOrgOwner(longOrgOwner);
        idOfClient = getLongAttributeValue(node, "IdOfClient");
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
    }

    private Client client;
    private Long idOfClient;

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

}
