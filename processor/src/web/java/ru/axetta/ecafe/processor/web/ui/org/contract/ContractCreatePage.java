/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.contract;

import ru.axetta.ecafe.processor.core.persistence.Contract;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractCreatePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractListPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.cxf.annotations.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.08.12
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ContractCreatePage extends AbstractCreatePage<ContractItem>{

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DAOService daoService;

    @Override
    protected String getPageFileName() {
        return "org/contract/create";
    }

    @Override
    public void onShow() throws Exception {
        currentEntity = new ContractItem(entityManager);
    }

    @Override
    @Transactional
    protected void onSave(){
        try {
            Contract contract = new Contract();
            contract.setContractNumber(currentEntity.getContractNumber());
            contract.setCustomer(currentEntity.getCustomer());
            contract.setPerformer(currentEntity.getPerformer());
            contract.setContractState(currentEntity.isContractState());
            contract.setDateOfClosing(currentEntity.getDateOfClosing());
            contract.setDateOfConclusion(currentEntity.getDateOfConclusion());
            daoService.persistEntity(contract);
            printMessage("Контракт успешно создан.");
            currentEntity.fill(contract);
        } catch (Exception e) {
            printError("Ошибка при созданиии нового контракта.");
            getLogger().error("Error create new contract: ", e);
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
