/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.contract;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEditPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    public String getPageFilename() {
        return "contragent/contract/edit";
    }

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            currentItem.setIdOfOrgList(new ArrayList<Long>(0));
            if (orgMap.isEmpty())
                currentItem.setOrgNames("Не выбрано");
            else {
                String filter = "";
                for(Long idOfOrg : orgMap.keySet()) {
                    currentItem.getIdOfOrgList().add(idOfOrg);
                    filter = filter.concat(orgMap.get(idOfOrg) + "; ");
                }
                filter = filter.substring(0, filter.length() - 1);
                currentItem.setOrgNames(filter);
            }
        }
    }

    @Override
    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes)
            throws Exception {
        contragentFilter.completeContragentSelection(session, idOfContragent);
        Contragent contragent = DAOService.getInstance().getContragentById(idOfContragent);
        currentItem.setContragent(contragent);
        currentItem.setPerformer(contragent.getContragentName());
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


}
