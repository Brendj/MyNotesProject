/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.contract;

import ru.axetta.ecafe.processor.core.persistence.Contract;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractListPage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class ContractListPage extends AbstractListPage<Contract, ContractItem> {
    BasicWorkspacePage groupPage = new BasicWorkspacePage();
    ContractItem.Filter filter = new ContractItem.Filter();

    @Override
    protected String getPageFileName() {
        return "contragent/contract/list";
    }

    @Override
    protected Class<Contract> getEntityClass() {
        return Contract.class;
    }

    @Override
    protected ContractItem createItem() {
        return new ContractItem();
    }

    @Override
    protected String getSortField() {
        return "contractNumber";
    }

    public BasicWorkspacePage getGroupPage() {
        return groupPage;
    }

    @Override
    public ContractItem.Filter getFilter() {
        return filter;
    }
}
