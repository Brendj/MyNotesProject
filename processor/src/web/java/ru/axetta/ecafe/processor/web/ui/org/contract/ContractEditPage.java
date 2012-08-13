/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.contract;

import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEditPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Component
@Scope("session")
public class ContractEditPage extends AbstractEditPage<ContractItem> implements OrgListSelectPage.CompleteHandlerList{

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

    public String getStringIdOfOrgList() {
        return currentItem.getIdOfOrgList().toString().replaceAll("[^0-9,]","");
    }


}
