/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.contract;

import ru.axetta.ecafe.processor.core.persistence.Contract;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractSelectedEntityGroupPage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.08.12
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class SelectedContractGroupPage extends AbstractSelectedEntityGroupPage<ContractItem> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected String buildTitle() {
        return String.format("%s: %s", getCurrentEntity().getContractNumber(), getCurrentEntity().getPerformer());
    }

    @Override
    public ContractItem getEntity() {
        Contract contract = entityManager.find(Contract.class, getCurrentEntityId());
        ContractItem contractItem = new ContractItem(entityManager);
        contractItem.fill(contract);
        return contractItem;
    }
}
