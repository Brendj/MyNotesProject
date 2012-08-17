/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.banks;

import ru.axetta.ecafe.processor.core.persistence.Bank;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractDeletePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractListPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 09.08.12
 * Time: 10:02
 * To change this template use File | Settings | File Templates.
 */
@Component
public class BankDeletePage extends AbstractDeletePage<Bank> {

    @Autowired
    private DAOService daoService;
    @Autowired
    private BankListPage bankListPage;

    @Override
    protected DAOService getDAOService() {
        return daoService;
    }

    @Override
    protected BankListPage getAbstractListPage() {
         return bankListPage;
    }
}
