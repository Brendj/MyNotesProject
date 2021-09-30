/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent.contract;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.org.Contract;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEditPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Scope("session")
public class ContractEditPage extends AbstractEditPage<ContractItem> implements ContragentSelectPage.CompleteHandler, OrgListSelectPage.CompleteHandlerList{
    protected final CCAccountFilter contragentFilter = new CCAccountFilter();

    @Component(value = "contractCreatePage")
    @Scope("session")
    public static class ContractCreatePage extends ContractEditPage {
        @Override
        public boolean isCreateMode() {
            return true;
        }
        @Override
        public boolean isEditMode() {
            return false;
        }
        @Override
        public void onShow() throws Exception {
            currentItem = new ContractItem();
        }
    }
    @Component(value = "contractViewPage")
    @Scope("session")
    public static class ContractViewPage extends ContractEditPage {
        @Override
        public boolean isReadonly() {
            return true;
        }
        @Override
        public boolean isEditMode() {
            return false;
        }
    }
    
    public ContractEditPage getAp() {
        return (ContractEditPage)MainPage.getSessionInstance().getCurrentWorkspacePage();
    }

    @Override
    protected boolean onCheckRequiredFields() {
        if(currentItem.getContragent()==null){
            printError("Введите данные контрагента.");
            return false;
        }
        if(StringUtils.isEmpty(currentItem.getPerformer())){
           printError("Введите данные исполнителя.");
           return false;
        }
        if(StringUtils.isEmpty(currentItem.getCustomer())){
            printError("Введите данные заказчика.");
            return false;
        }
        if(StringUtils.isEmpty(currentItem.getContractNumber())){
            printError("Введите номер контракта.");
            return false;
        }
        if(currentItem.getDateOfConclusion()==null){
            printError("Укажите дату заключения контракта.");
            return false;
        }
        if(currentItem.getDateOfClosing()==null){
            printError("Укажите срок действия контракта.");
            return false;
        }
        return true;
    }

    @Override
    public String getPageFilename() {
        return "contragent/contract/edit";
    }

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            //currentItem.setIdOfOrgList(new ArrayList<Long>(0));
            if (orgMap.isEmpty())
                currentItem.setOrgNames("Не выбрано");
            else {
                List newOrgs = new ArrayList<Long>();
                String filter = "";
                for(Long idOfOrg : orgMap.keySet()) {
                    //currentItem.getIdOfOrgList().add(idOfOrg);
                    filter = filter.concat(orgMap.get(idOfOrg) + "; ");
                    newOrgs.add(idOfOrg);
                }
                currentItem.setIdOfOrgList(newOrgs);
                filter = filter.substring(0, filter.length() - 1);
                currentItem.setOrgNames(filter);
            }
        }
    }

    @Override
    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes)
            throws Exception {
        contragentFilter.completeContragentSelection(session, idOfContragent);
        if(idOfContragent!=null){
            Contragent contragent = DAOReadonlyService.getInstance().getContragentById(idOfContragent);
            if(contragent!=null){
                currentItem.setContragent(contragent);
                currentItem.setPerformer(contragent.getContragentName());
            }
        }
    }

    public CCAccountFilter getContragentFilter() {
        if ((contragentFilter.getContragent() == null ||
             contragentFilter.getContragent().getContragentName() == null ||
             contragentFilter.getContragent().getContragentName().length() < 1) &&
            currentItem.getContragent() != null) {
            try {
                contragentFilter.completeContragentSelection(currentItem.getContragent());
            } catch (Exception e) {

            }
        }
        if (currentItem.getContragent() == null) {
            contragentFilter.clear();
        }
        return contragentFilter;
    }

    public String getStringIdOfOrgList() {
        return currentItem.getIdOfOrgList().toString().replaceAll("[^0-9,]","");
    }

    @Override
    public void onShow() throws Exception {
        super.onShow();
        Long id = this.getSelectedEntityGroupPage().getCurrentEntityItemId();
        if(this.currentItem==null && id!=null){
            currentItem = new ContractItem();
            Contract contract = entityManager.find(Contract.class, id);
            if(contract!=null){
                currentItem.fill(entityManager, contract);
                this.getSelectedEntityGroupPage().setCurrentEntityItem(currentItem);
            }
        }
    }
}
