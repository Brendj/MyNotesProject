/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.contract;

import ru.axetta.ecafe.processor.core.persistence.Contract;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractCreatePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractSelectedEntityGroupPage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractViewPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.08.12
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ContractViewPage extends AbstractViewPage<ContractItem> {

    @Autowired
    private SelectedContractGroupPage selectedContractGroupPage;

    @Override
    protected String getPageFileName() {
        return "org/contract/view";
    }

    @Override
    protected SelectedContractGroupPage getSelectedEntityGroupPage() {
        return selectedContractGroupPage;
    }
}
