/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 24.12.15
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
public abstract class ContractDistributedObject extends DistributedObject {
    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        setContract(session, idOfOrg);
        if (this.getDeletedState()) {
            setContractOrgHistory(session, idOfOrg);
        }
    }

    protected abstract void setContract(Session session, Long idOfOrg);
    protected abstract void setContractOrgHistory(Session session, Long idofOrg);
}
