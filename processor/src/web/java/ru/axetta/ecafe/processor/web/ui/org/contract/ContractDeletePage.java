/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.contract;

import ru.axetta.ecafe.processor.core.persistence.Contract;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractDeletePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractListPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 08.08.12
 * Time: 12:14
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ContractDeletePage extends AbstractDeletePage<Contract> {

    @Autowired
    private DAOService daoService;
    @Autowired
    private ContractListPage contractListPage;

    @Override
    protected DAOService getDAOService() {
        return daoService;
    }

    @Override
    protected ContractListPage getAbstractListPage() {
         return contractListPage;
    }
}
