/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.contract;

import ru.axetta.ecafe.processor.core.persistence.Contract;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEditPage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractSelectedEntityGroupPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 08.08.12
 * Time: 9:47
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ContractEditPage extends AbstractEditPage<ContractItem> implements OrgListSelectPage.CompleteHandlerList{

    @Autowired
    private SelectedContractGroupPage selectedContractGroupPage;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DAOService daoService;

    @Override
    protected String getPageFileName() {
        return "org/contract/edit";
    }

    @Override
    protected SelectedContractGroupPage getSelectedEntityGroupPage() {
        return selectedContractGroupPage;
    }

    @Override
    @Transactional
    protected void onSave(){
        try {
            Contract contract = entityManager.find(Contract.class, currentEntity.getIdOfContract());
            contract.setContractNumber(currentEntity.getContractNumber());
            contract.setCustomer(currentEntity.getCustomer());
            contract.setPerformer(currentEntity.getPerformer());
            contract.setContractState(currentEntity.isContractState());
            contract.setDateOfClosing(currentEntity.getDateOfClosing());
            contract.setDateOfConclusion(currentEntity.getDateOfConclusion());
            if(!currentEntity.getIdOfOrgList().isEmpty()){
                for (Org org: DAOUtils.findOrgs(entityManager, currentEntity.getIdOfOrgList())){
                    org.setContract(contract);
                    daoService.saveEntity(org);
                }
            }
            contract = daoService.saveEntity(contract);
            currentEntity.fill(contract);
            printMessage("Контракт успешно изменен.");
        } catch (Exception e) {
            printError("Ошибка при изменении контракта.");
            getLogger().error("Error edit contract: ", e);
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            currentEntity.setIdOfOrgList(new ArrayList<Long>(0));
            if (orgMap.isEmpty())
                currentEntity.setOrgNames("Не выбрано");
            else {
                String filter = "";
                for(Long idOfOrg : orgMap.keySet()) {
                    currentEntity.getIdOfOrgList().add(idOfOrg);
                    filter = filter.concat(orgMap.get(idOfOrg) + "; ");
                }
                filter = filter.substring(0, filter.length() - 1);
                currentEntity.setOrgNames(filter);
            }
        }
    }

    public String getStringIdOfOrgList() {
        return currentEntity.getIdOfOrgList().toString().replaceAll("[^0-9,]","");
    }


}
