package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 19.07.13
 * Time: 12:29
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class OrgClientSettingsPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList, OrgSelectPage.CompleteHandler {

    private static Logger logger = LoggerFactory.getLogger(OrgClientSettingsPage.class);
    private List<Long> idOfOrgList = new ArrayList<Long>(0);

    @Autowired
    private DAOService daoService;
    @Autowired
    private ClientAllocationRulesPage rulesPage;
    private String filter = "Не выбрано";

    @Override
    public void onShow() throws Exception {
        filter = "Не выбрано";
        idOfOrgList.clear();
        rulesPage.onShow();
    }

    public Object applyFullSyncOperation(){
        try {
            daoService.applyFullSyncOperationByOrgList(idOfOrgList);
            printMessage("Запрос отправлен");
        } catch (Exception e){
            printError("Ошибка при сохранении данных: "+e.getMessage());
            logAndPrintMessage("Error by create full sync param",e);
        }
        return null;
    }

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            idOfOrgList = new ArrayList<Long>(orgMap.size());
            if (orgMap.isEmpty())
                filter = "Не выбрано";
            else {
                filter = "";
                for(Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    filter = filter.concat(orgMap.get(idOfOrg) + "; ");
                }
                filter = filter.substring(0, filter.length() - 2);
            }
        }
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        rulesPage.completeOrgSelection(session, idOfOrg);
    }

    @Override
    public String getPageFilename() {
        return "org/settings";
    }

    public List<Long> getIdOfOrgList() {
        return idOfOrgList;
    }

    public void setIdOfOrgList(List<Long> idOfOrgList) {
        this.idOfOrgList = idOfOrgList;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
